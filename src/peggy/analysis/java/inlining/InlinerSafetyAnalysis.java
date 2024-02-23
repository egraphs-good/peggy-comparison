package peggy.analysis.java.inlining;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import peggy.analysis.java.ClassAnalysis;
import peggy.represent.java.SootUtils;
import soot.Body;
import soot.RefType;
import soot.SootClass;
import soot.SootField;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.SootResolver;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.FieldRef;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JCastExpr;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JInstanceOfExpr;
import soot.jimple.internal.JNewArrayExpr;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JNewMultiArrayExpr;

/**
 * This class examines the security and visibility modifiers of java to 
 * determine when inlining is safe.
 */
public class InlinerSafetyAnalysis {
	private static final Set<String> EXCLUDE_SET = 
		new HashSet<String>();
	static {
		EXCLUDE_SET.add("java.lang.String");
	}
	
	
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("InlinerSafetyAnalysis: " + message);
	}

	
	public InlinerSafetyAnalysis() {}
	
	
	/**
	 * Returns true iff 'inlinee' is safe to inline into 'inliner'.
	 * This will not actually perform the inlining.
	 * Method resolution will NOT be performed on 'inlinee', so this
	 * method assumes that you want to inline the actual body of 'inlinee'.
	 * This method checks the following conditions on 'inlinee':
	 * 
	 * 1) it is concrete (has a body)
	 * 2) it contains no exceptions (PEGGY restriction)
	 * 3) for each access to a field F:
	 *		- null != resolveField(inliner,F) == resolveField(inlinee,F) 
	 * 4) for each invocation of a method M:
	 * 		- null != resolve{Interface}Method(inliner,M)
	 * 		- if (M is used in an invokespecial) {
	 *			null != resolveInvokeSpecialMethod(inliner,M) == resolveInvokeSpecialMethod(inlinee,M) 
	 * 		  }
	 * 		- if (M is used in an invokevirtual or an invokespecial) {
	 * 			let M' = resolve(inliner,M)
	 * 			if (M' is protected && 
	 * 				M'.class <= inliner.class) {
	 * 			  check that inliner.class <= the invoke target's class 
	 * 			} 
	 * 		  }
	 */
	public boolean isSafeToInline(SootMethod inliner, SootMethod inlinee) {
		if (!inlinee.isConcrete()) {
			debug("failed concrete test");
			return false;
		}
		if (inlinee.isSynchronized()) {
			debug("failed synchronized test");
			return false;
		}
		if (EXCLUDE_SET.contains(inlinee.getDeclaringClass().getName()))
			return false;

		SootResolver.v().resolveClass(inlinee.getDeclaringClass().getName(), SootClass.SIGNATURES);
		SootResolver.v().resolveClass(inlinee.getDeclaringClass().getName(), SootClass.BODIES);

		if (SootUtils.hasExceptions(inlinee)) {
			debug("failed hasExceptions test");
			return false;
		}
		
		if (inlinee.getName().equals("<init>") || inlinee.getName().equals("<clinit>")) {
			debug("failed != <init>, <clinit> test");
			return false;
		}
		
		SootClass inlinerClass = inliner.getDeclaringClass();
		SootClass inlineeClass = inlinee.getDeclaringClass();
		
		SootResolver.v().resolveClass(inlinee.getDeclaringClass().getName(), SootClass.SIGNATURES);
		SootResolver.v().resolveClass(inlinee.getDeclaringClass().getName(), SootClass.BODIES);
		Body body = inlinee.retrieveActiveBody();
		for (Iterator it = body.getUnits().iterator(); it.hasNext(); ) {
			Unit unit = (Unit)it.next();
			List<ValueBox> values = new ArrayList<ValueBox>(unit.getUseBoxes());
			values.addAll(unit.getDefBoxes());
			
			for (ValueBox box : values) {
				Value value = box.getValue();
				
				if (value instanceof JCastExpr) {
					JCastExpr cast = (JCastExpr)value;
					if (!ClassAnalysis.isAccessibleType(inlinerClass, cast.getCastType())) {
						debug("inaccessible cast type: " + value);
						return false;
					}
				} else if (value instanceof JNewArrayExpr) {
					JNewArrayExpr jnae = (JNewArrayExpr)value;
					if (!ClassAnalysis.isAccessibleType(inlinerClass, jnae.getType())) {
						debug("inaccessible newarray: " + value);
						return false;
					}
				} else if (value instanceof JNewExpr) {
					JNewExpr jne = (JNewExpr)value;
					if (!ClassAnalysis.isAccessibleType(inlinerClass, jne.getBaseType())) {
						debug("inaccessible new: " + value);
						return false;
					}
				} else if (value instanceof JNewMultiArrayExpr) {
					JNewMultiArrayExpr jnmae = (JNewMultiArrayExpr)value;
					if (!ClassAnalysis.isAccessibleType(inlinerClass, jnmae.getBaseType())) {
						debug("inaccessible newmultiarray: " + value);
						return false;
					}
				} else if (value instanceof JArrayRef) {
					JArrayRef jar = (JArrayRef)value;
					if (!ClassAnalysis.isAccessibleType(inlinerClass, jar.getType())) {
						debug("inaccessible arrayref: " + value);
						return false;
					}
				} else if (value instanceof JInstanceOfExpr) {
					JInstanceOfExpr jioe = (JInstanceOfExpr)value;
					if (!ClassAnalysis.isAccessibleType(inlinerClass, jioe.getCheckType())) {
						debug("inaccessible instanceof: " + value);
						return false;
					}
				} else if (value instanceof JInstanceFieldRef ||
						   value instanceof StaticFieldRef) {
					FieldRef fieldref = (FieldRef)value;
					SootFieldRef ref = fieldref.getFieldRef();
					SootField field = ref.resolve();
					
					if (!ClassAnalysis.isAccessibleType(inlinerClass, field.getType())) {
						debug("inaccessible field type: " + field.getSignature());
						return false;
					}
					
					SootField resolved1 = ClassAnalysis.resolveField(inlinerClass, field.getSignature());
					if (resolved1 == null) {
						debug("resolveField returned null: " + field.getSignature());
						return false;
					}
					SootField resolved2 = ClassAnalysis.resolveField(inlineeClass, field.getSignature());
					if (resolved1 != resolved2) {
						debug("resolveFields differ for field: " + field.getSignature());
						return false;
					}
				} else if (value instanceof InvokeExpr) {
					SootMethodRef ref = ((InvokeExpr)value).getMethodRef();
					SootMethod method = ref.resolve();
					
					if (!ClassAnalysis.isAccessibleType(inlinerClass, method.getReturnType())) {
						debug("inaccessible return type of invoke: " + method.getSignature());
						return false;
					}
					for (Type paramType : (List<Type>)method.getParameterTypes()) {
						if (!ClassAnalysis.isAccessibleType(inlinerClass, paramType)) {
							debug("inaccessible param type: " + paramType);
							return false;
						}
					}
					
					if (value instanceof InterfaceInvokeExpr) {
						// check accessibility
						SootMethod resolved = ClassAnalysis.resolveInterfaceMethod(inlinerClass, method.getSignature());
						if (resolved == null) {
							debug("resolved interface method is null: " + method.getSignature());
							return false;
						}
					} else if (value instanceof VirtualInvokeExpr) {
						// check extra condition on invoke target
						SootMethod simpleResolve = ClassAnalysis.resolveMethod(inlinerClass, method.getSignature());
						if (simpleResolve == null) {
							debug("resolved method is null: " + method.getSignature());
							return false;
						}
						if (simpleResolve.isProtected()) {
							if (HierarchyWrapper.isClassSubclassOfIncluding(inlinerClass, simpleResolve.getDeclaringClass())) {
								Value target = ((VirtualInvokeExpr)value).getBase();
								RefType type = (RefType)target.getType();
								if (!HierarchyWrapper.isClassSubclassOfIncluding(type.getSootClass(), inlinerClass)) {
									debug("inlinerClass not a superclass of target's type: " + type);
									return false;
								}
							}
						}
					} else if (value instanceof SpecialInvokeExpr) {
						// check extra condition on invoke target
						SootMethod simpleResolve = ClassAnalysis.resolveMethod(inlinerClass, method.getSignature());
						if (simpleResolve == null) {
							debug("method resolve gave null: " + method.getSignature());
							return false;
						}
						if (simpleResolve.isProtected()) {
							if (HierarchyWrapper.isClassSubclassOfIncluding(inlinerClass, simpleResolve.getDeclaringClass())) {
								Value target = ((SpecialInvokeExpr)value).getBase();
								RefType type = (RefType)target.getType();
								if (!HierarchyWrapper.isClassSubclassOfIncluding(type.getSootClass(), inlinerClass)) {
									debug("inlinerClass not superclass of target's type: " + type);
									return false;
								}
							}
						}
						
						SootMethod resolved1 = ClassAnalysis.resolveInvokeSpecialMethod(inliner.getDeclaringClass(), method.getSignature());
						if (resolved1 == null) {
							debug("resolved1 is null");
							return false;
						}
						SootMethod resolved2 = ClassAnalysis.resolveInvokeSpecialMethod(inlinee.getDeclaringClass(), method.getSignature());
						if (resolved1 != resolved2) {
							debug("resolved1 != resolved2");
							return false;
						}
					} else if (value instanceof StaticInvokeExpr) {
						// check accessibility
						SootMethod resolved = ClassAnalysis.resolveMethod(inlinerClass, method.getSignature());
						if (resolved == null) {
							debug("resolveMethod returned null");
							return false;
						}
					}
				}
			}
		}
		
		return true;
	}
}
