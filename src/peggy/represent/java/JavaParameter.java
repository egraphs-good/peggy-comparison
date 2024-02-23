package peggy.represent.java;

/**
 * This class implements the P parameter for CFGs.
 * A JavaParameter is one of:
 * 1) method parameter
 * 2) this reference
 * 3) sigma input
 * 4) object counter input
 */
public abstract class JavaParameter {
	public abstract JavaVariable getVariableVersion();
	
	public boolean isArgument() {return false;}
	public ArgumentJavaParameter getArgumentSelf() {throw new UnsupportedOperationException();}
	
	public boolean isThis() {return false;}
	public ThisJavaParameter getThisSelf() {throw new UnsupportedOperationException();}
	
	public boolean isSigma() {return false;}
	
	public abstract boolean equals(Object o);
	public abstract int hashCode();
	public abstract String toString();
	
	public static final JavaParameter SIGMA = 
		new JavaParameter() {
			public boolean isSigma() {return true;}
			public JavaVariable getVariableVersion() {return JavaVariable.SIGMA;}
			public boolean equals(Object o) {
				if (o == null || !(o instanceof JavaParameter))
					return false;
				return ((JavaParameter)o).isSigma();
			}
			public int hashCode() {return 101*79*31;}
			public String toString() {return "SigmaParameter";}
		};
}
