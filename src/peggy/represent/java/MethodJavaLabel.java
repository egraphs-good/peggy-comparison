package peggy.represent.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import soot.Type;

/**
 * This is a JavaLabel that names a particular Java method.
 * This includes the method name, class name, return type, and parameter types.
 */
public class MethodJavaLabel extends JavaLabel {
	private final String className;
	private final String methodName;
	private final Type returnType;
	private final List<? extends Type> parameterTypes;

	public MethodJavaLabel(
			String _classname, 
			String _methodName, 
			Type _return, 
			List<? extends Type> _parameters) {
		this.className = _classname;
		this.methodName = _methodName;
		this.returnType = _return;
		this.parameterTypes = Collections.unmodifiableList(new ArrayList<Type>(_parameters));
	}
	
	public int getNumOutputs() {return 1;}
	public boolean isMethod() {return true;}
	public MethodJavaLabel getMethodSelf() {return this;}
	
	public String getClassName() {return this.className;}
	public String getMethodName() {return this.methodName;}
	public Type getReturnType() {return this.returnType;}
	public List<? extends Type> getParameterTypes() {return this.parameterTypes;}
	
	public boolean equalsLabel(JavaLabel o){
		if (!o.isMethod()) return false;

		MethodJavaLabel m = o.getMethodSelf();
		return m.getClassName().equals(this.getClassName()) &&
			m.getMethodName().equals(this.getMethodName()) &&
			m.getReturnType().equals(this.getReturnType()) &&
			this.getParameterTypes().equals(m.getParameterTypes());
	}

	public int hashCode(){
		return this.getClassName().hashCode()*11 +
			this.getMethodName().hashCode()*13 +
			this.getReturnType().hashCode()*17 +
			this.getParameterTypes().hashCode()*19;
	}

	public String toString(){
		return "Method[" + this.getMethodName() + "]";
	}
	
	public boolean isRevertible() {return true;}
}
