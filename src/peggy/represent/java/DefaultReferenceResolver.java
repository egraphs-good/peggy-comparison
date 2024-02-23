package peggy.represent.java;

import java.util.List;

import peggy.analysis.java.ClassAnalysis;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;

/**
 * This is the default reference resolver, which uses the ClassAnalysis to
 * load methods and fields.
 */
public class DefaultReferenceResolver implements ReferenceResolver {
	public SootMethodRef resolveMethod(MethodJavaLabel label) {
		return resolveMethod(
				label.getClassName(), 
				label.getMethodName(),
				label.getReturnType(),
				label.getParameterTypes());
	}
	public SootFieldRef resolveField(FieldJavaLabel label) {
		return resolveField(
				label.getClassName(),
				label.getFieldName(),
				label.getType());
	}
	
	public SootMethodRef resolveMethod(
			String className, String methodName, 
			Type returnType, List<? extends Type> paramTypes) {
		SootClass C = Scene.v().getSootClass(className);
		SootMethod resolved = ClassAnalysis.lookupMethod(
				C,
				SootMethod.getSubSignature(methodName, paramTypes, returnType));
		if (resolved == null)
			throw new IllegalArgumentException("Cannot resolve method: " + className + "." + methodName);
		return resolved.makeRef();
	}
	
	public SootFieldRef resolveField(
			String className, String fieldName, Type fieldType) {
		SootClass C = Scene.v().getSootClass(className);
		String signature = SootField.getSignature(C, fieldName, fieldType);
		SootField field = ClassAnalysis.lookupField(
				C,
				Scene.v().signatureToSubsignature(signature));
		if (field == null)
			throw new IllegalArgumentException("Cannot resolved field: " + className + "." + fieldName);
		return field.makeRef();
	}
}
