package peggy.represent.java;

import soot.Type;

/**
 * This is a JavaParameter for method parameters.
 */
public class ArgumentJavaParameter extends JavaParameter {
	protected final ArgumentJavaVariable variable;
	
	public ArgumentJavaParameter(ArgumentJavaVariable _variable) {
		this.variable = _variable;
	}
	
	public Type getType() {return variable.getArgumentType();}
	public int getIndex() {return this.variable.getArgumentIndex();}
	public ArgumentJavaVariable getVariableVersion() {return this.variable;}
	public boolean isArgument() {return true;}
	public ArgumentJavaParameter getArgumentSelf() {return this;}
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof JavaParameter))
			return false;
		JavaParameter p = (JavaParameter)o;
		if (!p.isArgument()) return false;
		return p.getArgumentSelf().getVariableVersion().equals(this.getVariableVersion());
	}
	public int hashCode() {
		return this.getVariableVersion().hashCode() * 79;
	}
	public String toString() {
		return "Parameter[" + variable.getMethod() + " (" + 
			this.variable.getArgumentIndex() + ") " + 
			this.variable.getArgumentType() + "]"; 
	}
}
