package peggy.analysis.java.inlining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import peggy.analysis.java.ClassAnalysis;
import peggy.represent.java.SootUtils;
import soot.Body;
import soot.IntType;
import soot.PatchingChain;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.ValueBox;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.ParameterRef;
import soot.jimple.ThisRef;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JCastExpr;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JGotoStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JInstanceOfExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JNopStmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JReturnVoidStmt;
import soot.jimple.internal.JimpleLocal;

/**
 * This class actually ties together all the necessary analyses
 * and actually performs inlining. It is abstract because the type of
 * JimpleTypeAnalysis that it uses is not specified. Subclasses must
 * implement the getTypeAnalysis(SootMethod) method to specify this.
 * 
 * @author steppm
 */
public abstract class JimpleInliner {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.out.println("JimpleInliner: " + message);
	}
	
	protected final InlinerSafetyAnalysis safetyAnalysis;

	public JimpleInliner() {
		this.safetyAnalysis = new InlinerSafetyAnalysis();
	}
	
	/**
	 * Returns a type analysis for the given method.
	 */
	protected abstract JimpleTypeAnalysis getTypeAnalysis(SootMethod inliner);
	
	/**
	 * This method is a wrapper around getPotentialInlineTargets.
	 * It will return only return a DispatchMap if there is exactly one entry in it.
	 */
	public DispatchMap getSingleInlineTarget(SootMethod inliner, Unit where) {
		DispatchMap potentialTargets = this.getTypeAnalysis(inliner).getPotentialTargets(where);

		SootMethod potential;
		
		if (potentialTargets == null) {
			debug("null dispatch map");
			return null;
		} else if (potentialTargets.isDependent()) {
			TargetDependentDispatchMap tddm = potentialTargets.getDependentSelf();
			Set<SootMethod> methods = new HashSet<SootMethod>(tddm.getMethodValues());
			if (methods.size() != 1) {
				debug("multiple potential targets");
				return null;
			} else {
				potential = methods.iterator().next();
				SootClass clazz = potential.getDeclaringClass();
				if (!ClassAnalysis.isAccessible(inliner.getDeclaringClass(), clazz))
					return null;
				Map<SootClass,SootMethod> newmap = new HashMap<SootClass,SootMethod>();
				newmap.put(clazz, potential);
				potentialTargets = new TargetDependentDispatchMap(potential.getSignature(), newmap);
			}
		} else {
			TargetIndependentDispatchMap tidm = potentialTargets.getIndependentSelf();
			potential = tidm.getDispatchedMethod();
		}
		
		if (this.safetyAnalysis.isSafeToInline(inliner, potential)) {
			return potentialTargets;
		} else {
			debug("single potential target not safe for inlining");
			return null;
		}
	}


	/**
	 * Assume that dispatch names only the classes/methods that are safe to inline.
	 */
	protected void justDoItMulti(SootMethod inliner, Unit where, TargetDependentDispatchMap dispatch, boolean extras) {
		InvokeExpr invoke;
		Value thisValue;
		JimpleLocal returnLocal;
		if (where instanceof JInvokeStmt) {
			invoke = ((JInvokeStmt)where).getInvokeExpr();
			returnLocal = null;
		} else {
			invoke = ((JAssignStmt)where).getInvokeExpr();
			returnLocal = (JimpleLocal) ((JAssignStmt)where).getLeftOp();
		}
		if (invoke instanceof InstanceInvokeExpr)
			thisValue = ((InstanceInvokeExpr)invoke).getBase();
		else
			throw new RuntimeException("Must have a this value!");

		List<Value> actualParameters = invoke.getArgs();
		Body inlinerBody = inliner.retrieveActiveBody();
		PatchingChain inlinerUnits = inlinerBody.getUnits();

		NameGenerator ng = new NameGenerator(inlinerBody);
		JimpleLocal TEST = new JimpleLocal(ng.nextName("TEST"), IntType.v());
		inlinerBody.getLocals().add(TEST);

		List<Unit> instanceofStmts = new ArrayList<Unit>(dispatch.size());
		List<JIfStmt> ifStmts = new ArrayList<JIfStmt>(dispatch.size());
		
		Unit nop = new JNopStmt();
		inlinerUnits.insertAfter(nop, where);
		
		Unit last = where;
		
		for (SootClass clazz : dispatch.getClassKeys()) {
			JAssignStmt instanceofStmt = new JAssignStmt(TEST, new JInstanceOfExpr(thisValue, clazz.getType()));
			JIfStmt ifStmt = new JIfStmt(new JEqExpr(TEST, IntConstant.v(0)), nop); // REMEMBER TO PATCH THIS LATER!
			JimpleLocal cast = new JimpleLocal(ng.nextName("CAST"), clazz.getType());
			inlinerBody.getLocals().add(cast);
			JAssignStmt castStmt = new JAssignStmt(cast, new JCastExpr(thisValue, clazz.getType()));
			JGotoStmt gotoStmt = new JGotoStmt(nop); // this target is correct

			instanceofStmts.add(instanceofStmt);
			ifStmts.add(ifStmt);

			inlinerUnits.insertAfter(instanceofStmt, last);
			last = instanceofStmt;
			inlinerUnits.insertAfter(ifStmt, last);
			last = ifStmt;
			inlinerUnits.insertAfter(castStmt, last);
			last = castStmt;
			inlinerUnits.insertAfter(gotoStmt, last);
			last = gotoStmt;
			
			this.pasteInBody(inliner, castStmt, dispatch.getDispatchedMethod(clazz), cast, actualParameters, returnLocal, ng);
		}
		
		// redirect branches from where to instanceofStmts.get(0)
		for (Unit unit : (Collection<Unit>)inlinerUnits) {
			SootUtils.redirectBranch(unit, where, instanceofStmts.get(0));
		}
		
		// move where to the correct place
		inlinerUnits.remove(where);
		inlinerUnits.insertAfter(where, inlinerUnits.getPredOf(nop));
		
		
		// rediect the ifStmt branches
		last = where;
		for (int i = ifStmts.size()-1; i >= 0; i--) {
			ifStmts.get(i).setTarget(last);
			last = instanceofStmts.get(i);
		}
	}
	
	
	static class NameGenerator {
		private Set<String> localNames;
		private int next;
		
		public NameGenerator(Body body) {
			this.localNames = new HashSet<String>();
			for (JimpleLocal local : (Collection<JimpleLocal>)body.getLocals()) {
				this.localNames.add(local.getName());
			}
			this.next = 0;
		}
		
		public String nextName(String prefix) {
			String name;
			do {
				name = prefix + (this.next++);
			} while (this.localNames.contains(name));
			this.localNames.add(name);
			return name;
		}
	}
	
	
	
	/**
	 * This method is called after you have completely validated that
	 * it is safe to inline 'actualMethod' into 'inliner' at codepoint 'where'.
	 * This method will do NO additional safety checks, it'll just inline.
	 */  
	protected void justDoIt(SootMethod inliner, Unit where, SootMethod actualMethod, SootClass dynamicType) {
		InvokeExpr invoke;
		Value thisValue;
		JimpleLocal returnLocal;
		if (where instanceof JInvokeStmt) {
			invoke = ((JInvokeStmt)where).getInvokeExpr();
			returnLocal = null;
		} else {
			invoke = ((JAssignStmt)where).getInvokeExpr();
			returnLocal = (JimpleLocal) ((JAssignStmt)where).getLeftOp();
		}
		if (invoke instanceof InstanceInvokeExpr)
			thisValue = ((InstanceInvokeExpr)invoke).getBase();
		else
			thisValue = null;

		List<Value> actualParameters = invoke.getArgs();

		Body inlinerBody = inliner.retrieveActiveBody();
		PatchingChain inlinerUnits = inlinerBody.getUnits();
		NameGenerator ng = new NameGenerator(inlinerBody);
		Unit start = where;
		
		if (dynamicType != null) {
			if (thisValue == null)
				throw new RuntimeException("Null this with dynamic type " + dynamicType);
			JimpleLocal casted = new JimpleLocal(ng.nextName("__CAST"), dynamicType.getType());
			JAssignStmt assign = new JAssignStmt(casted, new JCastExpr(thisValue, dynamicType.getType()));
			inlinerBody.getLocals().add(casted);
			inlinerUnits.insertAfter(assign, where);
			thisValue = casted;
			start = assign;
		}
		
		this.pasteInBody(inliner, start, actualMethod, thisValue, actualParameters, returnLocal, ng);
		
		for (Unit unit : (Collection<Unit>)inlinerUnits) {
			SootUtils.redirectBranch(unit, where, start);
		}
		inlinerUnits.remove(where);
	}
	
	
	/**
	 * Just performs the splicing of inlinee's body into inliner.
	 * Does not care what kind of instruction 'where' is, but it will
	 * insert the body right after that. This will NOT remove 'where' from the
	 * inliner's unit chain.
	 * 
	 * @param inliner the method into which the code is copied
	 * @param where the Unit right before where the code should be inserted
	 * @param inlinee the method from which the code is copied 
	 * @param thisValue the value to be used for the 'this' pointer in the inlinee
	 * @param actualParameters the values to be used for the parameters in the inlinee
	 * @param returnLocal the local variable to store the return value into for the inlinee
	 */
	private void pasteInBody(SootMethod inliner, Unit where, SootMethod inlinee, 
							 Value thisValue, List<Value> actualParameters, JimpleLocal returnLocal,
							 NameGenerator nameGenerator) {
		Map<Unit,Unit> old2newUnits = new HashMap<Unit,Unit>();
		Map<JimpleLocal,JimpleLocal> old2newLocals = new HashMap<JimpleLocal,JimpleLocal>();

		Body inlinerBody = inliner.retrieveActiveBody();
		final Body inlineeBody = inlinee.retrieveActiveBody();
		
		List<Unit> branches = new ArrayList<Unit>();

		PatchingChain inlinerUnits = inlinerBody.getUnits();
		Unit afterWhere = (Unit)inlinerUnits.getSuccOf(where);
		Unit last = where;
		
		List<Unit> unitCopies = new ArrayList<Unit>(inlineeBody.getUnits());
		
		for (Unit old : unitCopies) {
			Unit copy = (Unit)old.clone();

			// build the local map
			for (ValueBox box : (List<ValueBox>)old.getUseBoxes()) {
				if (box.getValue() instanceof JimpleLocal) {
					JimpleLocal oldLocal = (JimpleLocal)box.getValue();
					if (!old2newLocals.containsKey(oldLocal)) {
						String newname = nameGenerator.nextName("__LOCAL");
						JimpleLocal newlocal = new JimpleLocal(newname, oldLocal.getType());
						old2newLocals.put(oldLocal, newlocal);
						inlinerBody.getLocals().add(newlocal);
					}
				}
			}
			for (ValueBox box : (List<ValueBox>)old.getDefBoxes()) {
				if (box.getValue() instanceof JimpleLocal) {
					JimpleLocal oldLocal = (JimpleLocal)box.getValue();
					if (!old2newLocals.containsKey(oldLocal)) {
						String newname = nameGenerator.nextName("__LOCAL");
						JimpleLocal newlocal = new JimpleLocal(newname, oldLocal.getType());
						old2newLocals.put(oldLocal, newlocal);
						inlinerBody.getLocals().add(newlocal);
					}
				}
			}


			// replace params and this
			if (copy instanceof JIdentityStmt) {
				JIdentityStmt id = (JIdentityStmt)copy;
				if (id.getRightOp() instanceof ThisRef) {
					if (thisValue == null)
						throw new RuntimeException("ThisRef with null target!");
					copy = new JAssignStmt(id.getLeftOp(), thisValue);
				} else if (id.getRightOp() instanceof ParameterRef) {
					ParameterRef ref = (ParameterRef)id.getRightOp();
					copy = new JAssignStmt(id.getLeftOp(), actualParameters.get(ref.getIndex()));
				} else {
					throw new RuntimeException("WTF is this?? " + id.getRightOp());
				}
			}


			// replace returns with {assigns and} gotos
			if (copy instanceof JReturnStmt) {
				JReturnStmt returnStmt = (JReturnStmt)copy;
				if (returnLocal != null)
					copy = new JAssignStmt(returnLocal, returnStmt.getOp());
				else
					copy = new JNopStmt();
				JGotoStmt gotoStmt = new JGotoStmt(afterWhere);

				branches.add(gotoStmt);

				old2newUnits.put(old, copy);
				inlinerUnits.insertAfter(copy, last);
				inlinerUnits.insertAfter(gotoStmt, copy);
				last = gotoStmt;
			} else if (copy instanceof JReturnVoidStmt) {
				copy = new JGotoStmt(afterWhere);
				old2newUnits.put(old, copy);
				inlinerUnits.insertAfter(copy, last);
				last = copy;
			} else {
				old2newUnits.put(old, copy);
				inlinerUnits.insertAfter(copy, last);
				last = copy;
			}


			// reset the locals
			for (ValueBox box : (List<ValueBox>)copy.getDefBoxes()) {
				if (old2newLocals.containsKey(box.getValue())) {
					box.setValue(old2newLocals.get(box.getValue()));
				}
			}
			for (ValueBox box : (List<ValueBox>)copy.getUseBoxes()) {
				if (old2newLocals.containsKey(box.getValue())) {
					box.setValue(old2newLocals.get(box.getValue()));
				}
			}

			// remember to patch branches
			if (copy.branches())
				branches.add(copy);
		}


		// patch the branches
		for (Unit branch : branches) {
			for (UnitBox targetBox : (List<UnitBox>)branch.getUnitBoxes()) {
				if (targetBox.getUnit() == afterWhere)
					continue;
				Unit newunit = old2newUnits.get(targetBox.getUnit());
				if (newunit == null)
					throw new RuntimeException("null entry : " + targetBox.getUnit());
				targetBox.setUnit(newunit);
			}
		}
	}
}
