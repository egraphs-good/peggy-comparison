package peggy.represent.java;

/**
 * This is the JavaParameter for the this reference in a non-static Java method. 
 */
public class ThisJavaParameter extends JavaParameter {
	private final ThisJavaVariable variable;
	
	public ThisJavaParameter(ThisJavaVariable _var) {
		this.variable = _var;
	}
	
	public boolean isThis() {return true;}
	public ThisJavaParameter getThisSelf() {return this;}
	
	public ThisJavaVariable getVariableVersion() {return this.variable;}
	
	public boolean equals(Object o) {
		if (!(o instanceof JavaParameter))
			return false;
		JavaParameter p = (JavaParameter)o;
		if (!p.isThis()) return false;
		return this.getVariableVersion().equals(p.getVariableVersion());
	}
	
	public int hashCode() {
		return this.getVariableVersion().hashCode();
	}
	public String toString() {
		return "This[" + this.variable.getThisType() + "]";
	}
}
