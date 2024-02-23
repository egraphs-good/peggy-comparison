package peggy.represent.java;

import java.util.List;

import soot.SootFieldRef;
import soot.SootMethodRef;
import soot.Type;

/**
 * A ReferenceResolver is any class that can give you a SootMethodRef or
 * SootFieldRef based on descriptions of the method/field in question.
 */
public interface ReferenceResolver {
	public SootMethodRef resolveMethod(String className, String methodName, Type returnType, List<? extends Type> paramTypes);
	public SootFieldRef resolveField(String className, String fieldName, Type fieldType);
}
