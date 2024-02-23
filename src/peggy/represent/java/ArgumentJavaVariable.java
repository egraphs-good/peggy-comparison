package peggy.represent.java;

import soot.Type;

/**
 * This is a JavaVariable for method parameters.
 */
public class ArgumentJavaVariable extends JavaVariable {
	private final MethodJavaLabel method;
	private final int argumentIndex;
	
	public ArgumentJavaVariable(
			MethodJavaLabel _method,
			int _index) {
		this.method = _method;
		this.argumentIndex = _index;
	}
	
	public boolean isArgument() {return true;}
	public ArgumentJavaVariable getArgumentSelf() {return this;}
	
	public MethodJavaLabel getMethod() {return this.method;}
	public int getArgumentIndex() {return this.argumentIndex;}
	public Type getArgumentType() {
		return this.method.getParameterTypes().get(this.argumentIndex);
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof JavaVariable))
			return false;
		JavaVariable var = (JavaVariable)o;
		if (!var.isArgument())
			return false;
		ArgumentJavaVariable ar = var.getArgumentSelf();
		return ar.getMethod().equalsLabel(this.getMethod()) &&
			ar.getArgumentIndex() == this.getArgumentIndex() &&
			ar.getArgumentType().equals(this.getArgumentType());
	}
	public int hashCode() {
		return this.getMethod().hashCode()*41 +
			this.getArgumentIndex()*31 + 
			this.getArgumentType().hashCode()*37;
	}
	public String toString() {
		return "ArgumentVariable[" + getMethod() + "(" + getArgumentIndex() + "): " + getArgumentType() + "]";
	}
	
}
