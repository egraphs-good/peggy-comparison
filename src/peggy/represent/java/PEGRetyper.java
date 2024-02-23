package peggy.represent.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import soot.ArrayType;
import soot.RefType;
import soot.Type;

/**
 * This class is used to rewrite the types in certain JavaLabels.
 */
public class PEGRetyper {
	public static JavaLabel retypeLabel(
			JavaLabel label,
			Map<String,String> before2after) {
		if (label.isField()) {
			return retypeFieldLabel(label.getFieldSelf(), before2after);
		} else if (label.isMethod()) {
			return retypeMethodLabel(label.getMethodSelf(), before2after);
		} else if (label.isGetException()) {
			return retypeGetExceptionLabel(label.getGetExceptionSelf(), before2after);
		} else if (label.isIsException()) {
			return retypeIsExceptionLabel(label.getIsExceptionSelf(), before2after);
		} else if (label.isType()) {
			return retypeTypeLabel(label.getTypeSelf(), before2after);
		} else {
			return label;
		}
	}
	
	public static FieldJavaLabel retypeFieldLabel(
			FieldJavaLabel label,
			Map<String,String> before2after) {
		String className;
		if (before2after.containsKey(label.getClassName())) {
			className = before2after.get(label.getClassName());
		} else {
			className = label.getClassName();
		}
		return new FieldJavaLabel(
				className,
				label.getFieldName(),
				retypeType(label.getType(), before2after));
	}
	
	public static GetExceptionJavaLabel retypeGetExceptionLabel(
			GetExceptionJavaLabel label,
			Map<String,String> before2after) {
		return new GetExceptionJavaLabel(
				(RefType)retypeType(label.getExceptionType(), before2after));
	}
	
	public static IsExceptionJavaLabel retypeIsExceptionLabel(
			IsExceptionJavaLabel label,
			Map<String,String> before2after) {
		return new IsExceptionJavaLabel(
				(RefType)retypeType(label.getExceptionType(), before2after));
	}
	
	public static TypeJavaLabel retypeTypeLabel(
			TypeJavaLabel label,
			Map<String,String> before2after) {
		return new TypeJavaLabel(
				retypeType(label.getType(), before2after));
	}
	
	public static MethodJavaLabel retypeMethodLabel(
			MethodJavaLabel label,
			Map<String,String> before2after) {
		String className;
		if (before2after.containsKey(label.getClassName())) {
			className = before2after.get(label.getClassName());
		} else {
			className = label.getClassName();
		}
		List<Type> newParamTypes = new ArrayList<Type>(label.getParameterTypes().size());
		for (Type oldtype : label.getParameterTypes()) {
			newParamTypes.add(retypeType(oldtype, before2after));
		}
		return new MethodJavaLabel(
				className,
				label.getMethodName(),
				retypeType(label.getReturnType(), before2after),
				newParamTypes);
	}
	
	public static JavaParameter retypeParameter(
			JavaParameter param,
			Map<String,String> before2after) {
		if (param.isSigma()) {
			return param;
		} else if (param.isThis()) {
			ThisJavaParameter thisParam = param.getThisSelf();
			return new ThisJavaParameter(
					new ThisJavaVariable(
							(RefType)retypeType(thisParam.getVariableVersion().getThisType(), 
							before2after)));
		} else if (param.isArgument()) {
			ArgumentJavaParameter arg = param.getArgumentSelf();
			return new ArgumentJavaParameter(
					new ArgumentJavaVariable(
							retypeMethodLabel(arg.getVariableVersion().getMethod(), before2after),
							arg.getIndex()));
		} else {
			throw new IllegalArgumentException("Unsupported param: " + param);
		}
	}
	
	public static Type retypeType(Type input, Map<String,String> before2afterMap) {
		if (input instanceof ArrayType) {
			ArrayType array = (ArrayType)input;
			return ArrayType.v(retypeType(array.getElementType(), before2afterMap), 1);
		} else if (input instanceof RefType) {
			RefType reftype = (RefType)input;
			String className = reftype.getClassName(); 
			if (before2afterMap.containsKey(className)) {
				return RefType.v(before2afterMap.get(className));
			} else {
				return input;
			}
		} else {
			return input;
		}
	}
}
