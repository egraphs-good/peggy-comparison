package peggy.represent.java;

import soot.RefType;

/**
 * This is the JavaVariable for the this reference in a non-static Java method.
 */
public class ThisJavaVariable extends JavaVariable {
	private final RefType thisType;

	public ThisJavaVariable(RefType _type) {
		this.thisType = _type;
	}
	
	public boolean isThis() {return true;}
	public ThisJavaVariable getThisSelf() {return this;}
	
	public RefType getThisType() {return this.thisType;}
	
	public boolean equals(Object o) {
		if (!(o instanceof JavaVariable))
			return false;
		JavaVariable p = (JavaVariable)o;
		if (!p.isThis()) return false;
		return this.getThisType().equals(p.getThisSelf().getThisType());
	}
	
	public int hashCode() {
		return this.getThisType().hashCode()*31;
	}
	public String toString() {
		return "This[" + this.thisType + "]";
	}
}
