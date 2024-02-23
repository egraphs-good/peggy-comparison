package peggy.analysis.java;

import java.util.Iterator;
import java.util.List;

import peggy.analysis.java.inlining.HierarchyWrapper;
import soot.ArrayType;
import soot.ClassMember;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;

/**
 * This class encodes several of the JVM algorithms for determining
 * values related to method/field resolution, accessibility constraints, and so on.
 * It relies on the Soot Hierarchy class, which is an encoding of the class hierarchy tree.
 * 
 * @author steppm
 */
public abstract class ClassAnalysis {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("ClassAnalysis: " + message);
	}


	/**
	 * Returns true iff the given class is a Java library class.
	 * Any class in one of a few specific packages is considered to
	 * be a java library class.
	 */
	public static boolean isJavaLibraryClass(SootClass clazz) {
		String name = clazz.getName();
		return (name.startsWith("java.") ||
				name.startsWith("sun.") ||
				name.startsWith("javax.") ||
				name.startsWith("com.sun.") ||
				name.startsWith("com.ibm.") ||
				name.startsWith("org.xml.") ||
				name.startsWith("org.w3c.") ||
				name.startsWith("org.apache."));
	}


	/**
	 * Returns true iff class/interface 'to' is accessible from class/interface 'from'.
	 */
	public static boolean isAccessible(SootClass from, SootClass to) {
		if (to.isPublic())
			return true;
		String fromPackage = from.getJavaPackageName();
		String toPackage = to.getJavaPackageName();
		return fromPackage.equals(toPackage);
	}


	/**
	 * Returns true iff member 'member' is accessible from class/interface 'from'.
	 * This will be false if the declaring class of 'member' is not accessible from 'from'.
	 * (Warning: method resolution will not be performed on 'member', it will use the 
	 *  given declaring class)
	 */
	public static boolean isAccessible(SootClass from, ClassMember member) {
		if (!isAccessible(from, member.getDeclaringClass()))
			return false;
		if (member.isPublic())
			return true;
		SootClass C = member.getDeclaringClass();
		if (member.isProtected() && HierarchyWrapper.isClassSubclassOfIncluding(from, C)) // (subclass, superclass)
			return true;
		if (!(member.isPublic() || member.isPrivate()) && from.getJavaPackageName().equals(C.getJavaPackageName()))
			return true;
		return member.isPrivate() && C.equals(from);
	}


	/**
	 * Returns the result of field resolution on F from within class D.
	 * If resolution fails, null is returned.
	 * If resolution succeeds but the result is not accessible from D,
	 * null is returned. 
	 */
	public static SootField resolveField(SootClass D, String fieldSignature) {
		SootClass C = Scene.v().getSootClass(Scene.v().signatureToClass(fieldSignature));
		String subsignature = Scene.v().signatureToSubsignature(fieldSignature);
		SootField resolved = lookupField(C, subsignature);
		if (resolved == null)
			return null;
		if (!isAccessible(D, resolved)) {
			debug("field is not accessible");
			return null;
		}
		return resolved;
	}
	public static SootField lookupField(SootClass C, String subsig) {
		if (C.declaresField(subsig))
			return C.getField(subsig);
		SootField result = null;
		for (Iterator<SootClass> it = C.getInterfaces().iterator(); result == null && it.hasNext(); ) {
			result = lookupField(it.next(), subsig);
		}
		if (result == null && C.hasSuperclass())
			result = lookupField(C.getSuperclass(), subsig);
		return result;
	}


	/**
	 * Returns the result of method resolution on M from within class D.
	 * If resolution fails, null is returned.
	 * If resolution succeeds and the method is abstract but the declaring class is not abstract, 
	 * then null is returned
	 * If the resolution succeeds but the result is not accessible from D, then null
	 * is returned.
	 * 
	 * This should only be used for methods whose declaring class is NOT AN INTERFACE. 
	 */
	public static SootMethod resolveMethod(SootClass D, String methodSignature) {
		SootClass C = Scene.v().getSootClass(Scene.v().signatureToClass(methodSignature));
		if (C.isInterface())
			return null;
		SootMethod resolved = lookupMethod(C, Scene.v().signatureToSubsignature(methodSignature));
		if (resolved == null)
			return null;
		if (resolved.isAbstract() && !C.isAbstract())
			return null;
		if (!isAccessible(D, resolved))
			return null;
		return resolved;
	}
	public static SootMethod lookupMethod(SootClass C, String subsig) {
		if (C.declaresMethod(subsig))
			return C.getMethod(subsig);
		SootMethod result = null;
		if (C.hasSuperclass())
			result = lookupMethod(C.getSuperclass(), subsig);
		if (result == null) {
			for (Iterator it = C.getInterfaces().iterator(); result == null && it.hasNext(); ) {
				result = lookupMethod((SootClass)it.next(), subsig);
			}
		}
		return result;
	}



	/** 
	 * Returns the result of method resolution on interface method M from within class D.
	 * If resolution fails, null is returned.
	 * If resolution succeeds but the result is not accessible from D,
	 * null is returned.
	 * 
	 * This should only be used for methods whose declaring class is an INTERFACE.
	 */
	public static SootMethod resolveInterfaceMethod(SootClass D, String methodSignature) {
		SootClass C = Scene.v().getSootClass(Scene.v().signatureToClass(methodSignature));
		if (!C.isInterface())
			return null;
		String subsig = Scene.v().signatureToSubsignature(methodSignature);
		SootMethod resolved = lookupInterfaceMethod(C, subsig);
		if (resolved == null) {
			SootClass object = Scene.v().getSootClass("java.lang.Object");
			if (object.declaresMethod(subsig)) {
				resolved = object.getMethod(subsig);
			}
		}
		if (!isAccessible(D, resolved))
			return null;
		return resolved;
	}
	private static SootMethod lookupInterfaceMethod(SootClass C, String subsig) {
		if (C.declaresMethod(subsig))
			return C.getMethod(subsig);
		SootMethod result = null;
		for (Iterator it = C.getInterfaces().iterator(); result == null && it.hasNext(); ) {
			result = lookupInterfaceMethod((SootClass)it.next(), subsig);
		}
		return result;
	}


	/**
	 * Returns the actual target method of an invokespecial of the given method, called from
	 * within the given current class.
	 * This performs a method resolution on the given method. If the resolution fails, null is returned.
	 * If the resolution succeeds, then either the resolved method is returned or under
	 * certain circumstances a secondary lookup operation is performed, and the result of that
	 * will be returned.
	 */
	public static SootMethod resolveInvokeSpecialMethod(SootClass currentClass, String methodSignature) {
		SootMethod resolved = resolveMethod(currentClass, methodSignature);
		if (resolved == null) {
			debug("resolveMethod(" + currentClass.getName() + ", " + methodSignature + ") returned null");
			return null;
		}
		SootClass resolvedClass = resolved.getDeclaringClass();

		if (resolved.getName().equals("<init>"))
			return resolved;

		if (HierarchyWrapper.isClassSubclassOf(currentClass, resolvedClass)) {
			// invoke superclass method
			SootClass C = currentClass.getSuperclass();
			String subsig = resolved.getSubSignature();
			SootMethod newresolved = null;
			while (newresolved == null) {
				if (C.declaresMethod(subsig)) {
					newresolved = C.getMethod(subsig);
				} else if (C.hasSuperclass()) {
					C = C.getSuperclass();
				} else {
					break;
				}
			}

			if (newresolved == null)
				debug("Recursive lookup returned null: (" + currentClass.getName() + ", " + methodSignature + ")");
			return newresolved;
		} else if (currentClass.getName().equals(resolvedClass.getName())) {
			return resolved;
		} else {
			debug("Violated subclass restriction: (" + currentClass.getName() + ", " + methodSignature + ")");
			return null;
		}
	}


	/**
	 * Returns the dynamic method invocation concrete target for a given
	 * invokevirtual instruction. The instruction is assumed to be in a method
	 * inside 'currentClass', and the invocation target is assumed to have a 
	 * dynamic type of 'dynamicTargetType', and 'methodSignature' should be the
	 * signature of the exact method named in the invokevirtual instruction.
	 * If any part of this lookup fails, null is returned.
	 */
	public static SootMethod selectInvokeVirtualMethod(SootClass currentClass, 
													   SootClass dynamicTargetType, 
													   String methodSignature) {
		if (dynamicTargetType.isInterface()) {
			debug("dynamic target type is interface");
			return null;
		}
		SootMethod resolved = resolveMethod(currentClass, methodSignature);
		if (resolved == null) {
			debug("resolved method is null");
			return null;
		}
		if (resolved.isStatic()) {
			debug("method is static");
			return null;
		}

		if (resolved.isProtected() &&
			HierarchyWrapper.isClassSubclassOfIncluding(currentClass, resolved.getDeclaringClass())) {
			if (!HierarchyWrapper.isClassSubclassOfIncluding(dynamicTargetType, currentClass)) {
				debug("resolved is protected and currentClass <= resolved.class and !(dynamicType <= currentClass)");
				return null;
			}
		}

		SootMethod actualMethod = lookupVirtualMethod(dynamicTargetType, resolved);
		if (actualMethod == null) {
			debug("actualMethod is null");
			return null;
		} else if (actualMethod.isAbstract()) {
			debug("actualMethod is abstract");
			return null;
		}

		return actualMethod;
	}
	private static SootMethod lookupVirtualMethod(SootClass C, SootMethod resolved) {
		if (C.declaresMethod(resolved.getSubSignature()) && 
			isAccessible(C, resolved)) {
			return C.getMethod(resolved.getSubSignature());
		} else if (C.hasSuperclass()) {
			return lookupVirtualMethod(C.getSuperclass(), resolved);
		} else {
			return null;
		}
	}




	/**
	 * Returns the dynamic method invocation concrete target for a given
	 * invokeinterface instruction. The instruction is assumed to be in a method
	 * inside 'currentClass', and the invocation target is assumed to have a 
	 * dynamic type of 'dynamicTargetType', and 'methodSignature' should be the
	 * signature of the exact method named in the invokeinterface instruction.
	 * If any part of this lookup fails, null is returned.
	 */
	public static SootMethod selectInvokeInterfaceMethod(SootClass currentClass, 
														 SootClass dynamicTargetType,
														 String methodSignature) {
		SootMethod resolved = resolveInterfaceMethod(currentClass, methodSignature);
		if (resolved == null)
			return null;
		if (resolved.getName().equals("<init>") || resolved.getName().equals("<clinit>"))
			return null; // necessary?

		SootMethod actualMethod = lookupVirtualMethod(dynamicTargetType, resolved);
		if (actualMethod == null || actualMethod.isAbstract() || !actualMethod.isPublic())
			return null;

		SootClass declaringInterface = Scene.v().loadClassAndSupport(Scene.v().signatureToClass(methodSignature));
		if (!implementsInterface(actualMethod.getDeclaringClass(), declaringInterface))
			return null;

		return actualMethod;
	}




	/**
	 * This method will return true iff 'clazz' extends/implements
	 * 'inter' either direcly or indirectly.
	 * (This is not implemented in soot.Hierarchy)
	 */
	public static boolean implementsInterface(SootClass clazz, SootClass inter) {
		if (clazz.getInterfaces().contains(inter)) {
			return true;
		} else if (!clazz.isInterface() && 
				   clazz.hasSuperclass() && 
				   implementsInterface(clazz.getSuperclass(), inter)) {
			return true;
		} else {
			for (SootClass superint : (List<SootClass>)clazz.getInterfaces()) {
				if (implementsInterface(superint, inter))
					return true;
			}
			return false;
		}
	}




	/**
	 * Returns true iff the given type is accessible from the
	 * given class. This is a convenience method that just wraps 
	 * around isAccessible(SootClass,SootClass). This will work correctly
	 * for ArrayTypes (when the base type is a RefType).
	 */
	public static boolean isAccessibleType(SootClass from, Type type) {
		if (type instanceof RefType) {
			return isAccessible(from, ((RefType)type).getSootClass());
		} else if (type instanceof ArrayType) {
			ArrayType array = (ArrayType)type;
			if (array.baseType instanceof RefType) {
				return isAccessible(from, ((RefType)array.baseType).getSootClass()); 
			}
		}
		return true;
	}
}
