package peggy.analysis.java.inlining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import peggy.analysis.Lattice;
import peggy.analysis.java.ClassAnalysis;
import peggy.analysis.java.ClassMap;
import peggy.analysis.java.ClassMapLattice;
import peggy.analysis.java.ClassSet;
import soot.Body;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.FieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.NullConstant;
import soot.jimple.StringConstant;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JCastExpr;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JGotoStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JInstanceOfExpr;
import soot.jimple.internal.JInterfaceInvokeExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JLookupSwitchStmt;
import soot.jimple.internal.JNeExpr;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JReturnVoidStmt;
import soot.jimple.internal.JTableSwitchStmt;
import soot.jimple.internal.JThrowStmt;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.BriefUnitGraph;

/**
 * This is the default implementation of the JimpleTypeAnalysis.
 */
public class SimpleJimpleTypeAnalysis extends JimpleTypeAnalysis {
	protected final SootMethod method;
	protected final Lattice<ClassMap<JimpleWrapper>> lattice;
	protected final Map<Unit,List<Edge>> outputEdgeMap;
	protected final Map<Unit,List<Edge>> inputEdgeMap;
	protected final Map<Unit,ClassMap<JimpleWrapper>> inputMap;
	
	static class Edge {
		protected final Unit source, sink;
		protected ClassMap<JimpleWrapper> flowvalue;
		
		protected Edge(Unit _source, ClassMap<JimpleWrapper> _flowvalue, Unit _sink) {
			this.source = _source;
			this.flowvalue = _flowvalue;
			this.sink = _sink;
		}
		public Unit getSource() {throw new UnsupportedOperationException();}
		public Unit getSink() {throw new UnsupportedOperationException();}
		public ClassMap<JimpleWrapper> getFlowValue() {return this.flowvalue;}
	}
	
	public SimpleJimpleTypeAnalysis(SootMethod _method, Lattice<ClassMap<JimpleWrapper>> _lattice) {
		this.method = _method;
		this.lattice = _lattice;
		this.inputEdgeMap = new HashMap<Unit,List<Edge>>();
		this.outputEdgeMap = new HashMap<Unit,List<Edge>>();
		this.inputMap = new HashMap<Unit,ClassMap<JimpleWrapper>>();
		
		this.buildEdges();
		this.analyze();
	}
	
	public ClassMap<JimpleWrapper> getInputMap(Unit unit) {
		return this.inputMap.get(unit);
	}
	public ClassMap<JimpleWrapper> getOutputMap(Unit unit) {
		List<Edge> outputEdges = this.outputEdgeMap.get(unit);
		if (outputEdges == null)
			throw new IllegalArgumentException("Unit not in list");
		return outputEdges.get(0).flowvalue;
	}
	
	private void buildEdges() {
		BriefUnitGraph cfg = new BriefUnitGraph(this.method.retrieveActiveBody());
		ClassMap<JimpleWrapper> bottom = this.lattice.bottom();

		for (Iterator it = cfg.iterator(); it.hasNext(); ) {
			Unit unit = (Unit)it.next();
			List<Edge> myInputEdges = this.inputEdgeMap.get(unit);
			if (myInputEdges == null) {
				myInputEdges = new ArrayList<Edge>(10);
				this.inputEdgeMap.put(unit, myInputEdges);
			}
			List<Edge> myOutputEdges = this.outputEdgeMap.get(unit);
			if (myOutputEdges == null) {
				myOutputEdges = new ArrayList<Edge>(10);
				this.outputEdgeMap.put(unit, myOutputEdges);
			}
			
			List<Unit> succs = cfg.getSuccsOf(unit);
			for (Unit succ : succs) {
				Edge edge = new Edge(unit, bottom, succ);

				myOutputEdges.add(edge);
				
				List<Edge> inputEdges = this.inputEdgeMap.get(succ);
				if (inputEdges == null) {
					inputEdges = new ArrayList<Edge>(10);
					this.inputEdgeMap.put(succ, inputEdges);
				}
				inputEdges.add(edge);
			}
		}
	}
	
	private ClassSet analyzeAssignLocal(ClassMap<JimpleWrapper> input, JimpleWrapper lhs, Value rhs) {
		if (rhs instanceof FieldRef) {
			FieldRef ref = (FieldRef)rhs;
			SootField field = ref.getField();
			if (field.getType() instanceof RefType) {
				return this.getFieldClassSet(ref.getField());
			} else {
				return ClassSet.EMPTY_CLASSES;
			}
		} else if (rhs instanceof StringConstant) {
			SootClass string = Scene.v().getSootClass("java.lang.String");
			return ClassSet.cone(string);
		} else if (rhs instanceof JArrayRef) {
			JArrayRef array = (JArrayRef)rhs;
			Type elementType = array.getType();
			if (elementType instanceof RefType) {
				return ClassSet.cone(((RefType)elementType).getSootClass());
			} else {
				return ClassSet.EMPTY_CLASSES;
			}
		} else if (rhs instanceof JCastExpr) {
			JCastExpr cast = (JCastExpr)rhs;
			Type castType = cast.getCastType();
			if (castType instanceof RefType) {
				if (cast.getOp() instanceof NullConstant)
					return ClassSet.EMPTY_CLASSES;
				JimpleLocal op = (JimpleLocal)cast.getOp();
				RefType refCastType = (RefType)castType;
				ClassSet opSet = input.getClassSet(new JimpleWrapper(op));
				ClassSet coneSet = ClassSet.cone(refCastType.getSootClass());
				return opSet.intersection(coneSet);
			} else {
				return ClassSet.EMPTY_CLASSES; 
			}
		} else if (rhs instanceof JimpleLocal) {
			JimpleWrapper him = new JimpleWrapper((JimpleLocal)rhs);
			return input.getClassSet(him);
		} else if (rhs instanceof JNewExpr) {
			JNewExpr expr = (JNewExpr)rhs;
			RefType newType = expr.getBaseType();
			return ClassSet.singleton(newType.getSootClass());
		} else if (rhs instanceof InvokeExpr) {
			InvokeExpr invoke = (InvokeExpr)rhs;
			Type returnType = invoke.getMethodRef().returnType();
			if (returnType instanceof RefType)
				return this.getMethodReturnClassSet(invoke.getMethod());
			else
				return ClassSet.EMPTY_CLASSES;
		} else {
			return ClassSet.EMPTY_CLASSES;
		}
	}

	
	private void analyze() {
		PatchingChain units = this.method.retrieveActiveBody().getUnits();
		ClassMap<JimpleWrapper> bottom = this.lattice.bottom();

		LinkedList<Unit> queue = new LinkedList<Unit>();
		queue.add((Unit)units.getFirst());
		while (!queue.isEmpty()) {
			Unit next = queue.removeFirst();
			ClassMap<JimpleWrapper> input;
			
			{// get the input map
				List<Edge> inputEdges = this.inputEdgeMap.get(next);
				if (inputEdges.size() == 0) {
					input = bottom;
				} else {
					input = inputEdges.get(0).flowvalue;
					for (int i = 1; i < inputEdges.size(); i++) {
						input = this.lattice.lub(input, inputEdges.get(i).flowvalue);
					}
				}
			}
			
			{// check to see if input has changed, if not then skip
				if (this.inputMap.containsKey(next)) {
					ClassMap<JimpleWrapper> oldinput = this.inputMap.get(next);
					if (oldinput.isSubMap(input) && input.isSubMap(oldinput))
						continue;
				}
			}
			
			
			if (next instanceof JAssignStmt) {
				JAssignStmt assign = (JAssignStmt)next;
				Value lhs = assign.getLeftOp();
				Value rhs = assign.getRightOp();
				ClassMap<JimpleWrapper> output;
				
				if (lhs instanceof JimpleLocal) {
					JimpleWrapper wrapper = new JimpleWrapper((JimpleLocal)lhs);
					output = input.updateClassSet(wrapper, this.analyzeAssignLocal(input, wrapper, rhs));
				} else {
					output = input;
				}
				
				for (Edge edge : this.outputEdgeMap.get(next)) {
					edge.flowvalue = output;
				}
			} else if (next instanceof JIdentityStmt) {
				// ParameterRef or ThisRef
				JIdentityStmt identity = (JIdentityStmt)next;
				JimpleWrapper wrapper = new JimpleWrapper((JimpleLocal)identity.getLeftOp());
				Type idType = identity.getRightOp().getType();
				ClassMap<JimpleWrapper> output;
				
				if (idType instanceof RefType) {
					output = input.updateClassSet(wrapper, ClassSet.cone(((RefType)idType).getSootClass()));
				} else {
					output = input.updateClassSet(wrapper, ClassSet.EMPTY_CLASSES);
				}
				
				for (Edge edge : this.outputEdgeMap.get(next)) {
					edge.flowvalue = output;
				}
			} else if (next instanceof JIfStmt) {
				JIfStmt ifstmt = (JIfStmt)next;
				// check for op = JNeExpr(0, JInstanceOfExpr(LOCAL, REFTYPE))
				//        or op = JEqExpr(0, JInstanceOfExpr(LOCAL, REFTYPE))

				ClassMap<JimpleWrapper> iftrue, iffalse;
				
				Value condition = ifstmt.getCondition();
				if (condition instanceof JEqExpr) {
					JEqExpr eq = (JEqExpr)condition;
					Value other;
					
					if (eq.getOp1().equals(IntConstant.v(0)))
						other = eq.getOp2();
					else if (eq.getOp2().equals(IntConstant.v(0)))
						other = eq.getOp1();
					else
						other = null;
					
					if (other != null &&
						other instanceof JInstanceOfExpr &&
						((JInstanceOfExpr)other).getOp() instanceof JimpleLocal &&
						((JInstanceOfExpr)other).getCheckType() instanceof RefType) {
						
						JimpleLocal local = (JimpleLocal)((JInstanceOfExpr)eq.getOp2()).getOp();
						RefType type = (RefType)((JInstanceOfExpr)eq.getOp2()).getCheckType();
						
						JimpleWrapper wrapper = new JimpleWrapper(local);
						ClassSet oldset = input.getClassSet(wrapper);
						ClassSet cone = ClassSet.cone(type.getSootClass());
						iftrue = input.updateClassSet(wrapper, oldset.difference(cone));
						iffalse = input.updateClassSet(wrapper, oldset.intersection(cone));
					} else {
						iftrue = iffalse = input;
					}
				} else if (condition instanceof JNeExpr) {
					JNeExpr ne = (JNeExpr)condition;
					Value other;
					
					if (ne.getOp1().equals(IntConstant.v(0)))
						other = ne.getOp2();
					else if (ne.getOp2().equals(IntConstant.v(0)))
						other = ne.getOp1();
					else
						other = null;
					
					if (other != null &&
						other instanceof JInstanceOfExpr &&
						((JInstanceOfExpr)other).getOp() instanceof JimpleLocal &&
						((JInstanceOfExpr)other).getCheckType() instanceof RefType) {
						
						JimpleLocal local = (JimpleLocal)((JInstanceOfExpr)ne.getOp2()).getOp();
						RefType type = (RefType)((JInstanceOfExpr)ne.getOp2()).getCheckType();
						
						JimpleWrapper wrapper = new JimpleWrapper(local);
						ClassSet oldset = input.getClassSet(wrapper);
						ClassSet cone = ClassSet.cone(type.getSootClass());
						iftrue = input.updateClassSet(wrapper, oldset.intersection(cone));
						iffalse = input.updateClassSet(wrapper, oldset.difference(cone));
					} else {
						iftrue = iffalse = input;
					}
				} else {
					iftrue = iffalse = input;
				}
				
				
				List<Edge> outEdges = this.outputEdgeMap.get(next);
				if (outEdges.size() != 2)
					throw new RuntimeException("If has same target as fallthrough, unsafe for dataflow");
				if (outEdges.get(0).sink == ifstmt.getTarget()) {
					outEdges.get(0).flowvalue = iftrue;
					outEdges.get(1).flowvalue = iffalse;
				} else {
					outEdges.get(0).flowvalue = iffalse;
					outEdges.get(1).flowvalue = iftrue;
				}
			} else if (next instanceof JReturnStmt ||
					   next instanceof JReturnVoidStmt) {
				// do nothing!
			} else if (next instanceof JTableSwitchStmt ||
					   next instanceof JLookupSwitchStmt ||
					   next instanceof JGotoStmt) {
				for (Edge edge : this.outputEdgeMap.get(next)) {
					edge.flowvalue = input;
				}
			} else if (next instanceof JThrowStmt) {
				throw new RuntimeException("We don't handle throws!!!");
			} else {
				// does nothing, has fallthrough
				for (Edge edge : this.outputEdgeMap.get(next)) {
					edge.flowvalue = input;
				}
			}

			this.inputMap.put(next, input);
			
			for (Edge output : this.outputEdgeMap.get(next))
				queue.add(output.sink);
		}
		
		for (Iterator<Unit> it = units.iterator(); it.hasNext(); ) {
			Unit unit = it.next();
			if (!this.inputMap.containsKey(unit))
				this.inputMap.put(unit, bottom);
		}
	}
	
	public SootMethod getMethod() {
		return this.method;
	}
	
	public ClassSet getLocalClassSet(JimpleLocal local, Unit where) {
		ClassMap<JimpleWrapper> map = this.getInputMap(where);
		return map.getClassSet(new JimpleWrapper(local));
	}
	
	public ClassSet getFieldClassSet(SootField field) {
		Type fieldType = field.getType();
		if (!(fieldType instanceof RefType))
			throw new RuntimeException("Not an object type");
		RefType ref = (RefType)fieldType;
		ClassSet result = ClassSet.cone(ref.getSootClass());
		return result;
	}
	
	public ClassSet getMethodReturnClassSet(SootMethod method) {
		Type returnType = method.getReturnType();
		if (!(returnType instanceof RefType))
			throw new RuntimeException("Not an object type");
		RefType ref = (RefType)returnType;
		ClassSet result = ClassSet.cone(ref.getSootClass());
		return result;
	}
	
	
	
	/**
	 * Returns the set of concrete SootMethods that might actually be
	 * run by executing the invoke instruction 'where' in this analysis' method.
	 * This will not perform a safety test on the methods. If the declaring class
	 * of the method is java.lang.Object, and the method is not one of Object's final
	 * methods, then null is returned (i.e. we will never examine all of Cone(java.lang.Object))
	 */
	public DispatchMap getPotentialTargets(Unit where) {
		InvokeExpr invoke;

		if (where instanceof JInvokeStmt) {
			JInvokeStmt invokeStmt = (JInvokeStmt)where;
			invoke = invokeStmt.getInvokeExpr();
		} else if (where instanceof JAssignStmt) {
			JAssignStmt assign = (JAssignStmt)where;
			if (!(assign.getLeftOp() instanceof JimpleLocal &&
					assign.getRightOp() instanceof InvokeExpr))
				throw new IllegalArgumentException("Unit is not an invocation");
			invoke = (InvokeExpr)assign.getRightOp();
		} else {
			throw new IllegalArgumentException("Unit is not an invocation");
		}

		SootMethod callee = invoke.getMethod();
		String signature = callee.getSignature();
		JimpleLocal target;

		if (callee.isStatic()) {
			// invokestatic (doesn't have a target)
			target = null;
			callee = ClassAnalysis.resolveMethod(this.method.getDeclaringClass(), signature);
			return new TargetIndependentDispatchMap(signature, callee);
		} else {
			target = (JimpleLocal)((InstanceInvokeExpr)invoke).getBase();
			ClassSet targetSet = this.getLocalClassSet(target, where);
			Map<SootClass,SootMethod> dispatchMap = new HashMap<SootClass,SootMethod>();
			
			if (targetSet.isAll())
				return null;
			
			if (invoke instanceof JVirtualInvokeExpr) {
				// invokevirtual
				
				for (SootClass potentialClass : targetSet) {
					if (potentialClass.isAbstract() || potentialClass.isInterface())
						// this can't be the real dynamic type
						continue;
					
					SootMethod actualMethod = 
						ClassAnalysis.selectInvokeVirtualMethod(this.method.getDeclaringClass(), potentialClass, signature);
					if (actualMethod == null)
						throw new RuntimeException("selectInvokeVirtualMethod(" + 
                                             this.method.getDeclaringClass().getName() +
                                             ", " +
                                             potentialClass.getName() + 
                                             ", " + 
                                             signature +
                                             ") returned null!");
					
					dispatchMap.put(potentialClass, actualMethod);
					SootClass actualClass = actualMethod.getDeclaringClass();
					
					if (ClassAnalysis.isAccessible(this.method.getDeclaringClass(), actualClass)) {
						// check for duplicate values
						for (SootClass key : dispatchMap.keySet()) {
							if (dispatchMap.get(key).equals(actualMethod)) {
								dispatchMap.remove(key);
								dispatchMap.remove(potentialClass);
								dispatchMap.put(actualClass, actualMethod);
								break;
							}
						}
					}
				}
				
				return new TargetDependentDispatchMap(signature, dispatchMap);
			} else if (invoke instanceof JInterfaceInvokeExpr) {
				// invokeinterface
				for (SootClass potentialClass : targetSet) {
					if (potentialClass.isAbstract() || potentialClass.isInterface())
						// this can't be the real dynamic type
						continue; 

					SootMethod actualMethod = 
						ClassAnalysis.selectInvokeInterfaceMethod(this.method.getDeclaringClass(), potentialClass, signature);
					if (actualMethod == null)
						throw new RuntimeException("selectInvokeInterfaceMethod(" +
                                             this.method.getDeclaringClass().getName() + 
                                             ", " +
                                             potentialClass.getName() +
                                             ", " + 
                                             signature +
                                             ") returned null!");
                                             
					dispatchMap.put(potentialClass, actualMethod);
					SootClass actualClass = actualMethod.getDeclaringClass();
					
					if (!actualClass.isInterface() && 
							ClassAnalysis.isAccessible(this.method.getDeclaringClass(), actualClass)) {
						// check for duplicate values
						for (SootClass key : dispatchMap.keySet()) {
							if (dispatchMap.get(key).equals(actualMethod)) {
								dispatchMap.remove(key);
								dispatchMap.remove(potentialClass);
								dispatchMap.put(actualClass, actualMethod);
								break;
							}
						}
					}
				}

				return new TargetDependentDispatchMap(signature, dispatchMap);
			} else {
				// invokespecial (doesn't care about target's type)
				SootMethod actualMethod = ClassAnalysis.resolveInvokeSpecialMethod(this.method.getDeclaringClass(), signature); 
				if (actualMethod == null)
					throw new RuntimeException("resolveInvokeSpecialMethod(" +
                                          this.method.getDeclaringClass().getName() +
                                          ", " + 
                                          signature + 
                                          ") returned null!");
				
				return new TargetIndependentDispatchMap(signature, actualMethod);
			}
		}
	}
	
	
	
	
	//////////////////////////////////////////////////////////////////
	
	public static void main(String args[]) throws Throwable {
		String signature = args[0];
		SootClass clazz = Scene.v().loadClassAndSupport(Scene.v().signatureToClass(signature));
		Scene.v().loadNecessaryClasses();
		
		SootMethod method = clazz.getMethod(Scene.v().signatureToSubsignature(signature));

		SimpleJimpleTypeAnalysis sjta = new SimpleJimpleTypeAnalysis(method, new ClassMapLattice<JimpleWrapper>());
		
		Body body = method.retrieveActiveBody();
		for (Iterator it = body.getUnits().iterator(); it.hasNext(); ) {
			Unit unit = (Unit)it.next();
			System.out.println(sjta.getInputMap(unit));
			System.out.print("   ");
			System.out.println(unit);
		}
	}
}
