package peggy.represent.java;

/**
 * This class implements the R parameter of a CFG.
 * A JavaReturn is one of:
 * 1) SIGMA return
 * 2) value return
 * 3) object counter return
 */
public abstract class JavaReturn {
	public boolean isSigma() {return false;}
	public boolean isValue() {return false;}
	
	public abstract JavaVariable getVariableVersion();
	public abstract String toString();
	
	public static final JavaReturn SIGMA =
		new JavaReturn() {
			public boolean isSigma() {return true;}
			public JavaVariable getVariableVersion() {return JavaVariable.SIGMA;}
			public String toString() {return "SigmaReturn";}
		};

	public static final JavaReturn VALUE =
		new JavaReturn() {
			public boolean isValue() {return true;}
			public JavaVariable getVariableVersion() {return JavaVariable.RETURN;}
			public String toString() {return "ValueReturn";}
		};
}
