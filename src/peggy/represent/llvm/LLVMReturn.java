package peggy.represent.llvm;

/**
 * This class is used as the R parameter for LLVM CFGs.
 * A LLVMReturn is one of:
 * 1) sigma
 * 2) return value
 */
public abstract class LLVMReturn {
	public boolean isSigma() {return false;}
	public boolean isValue() {return false;}
	
	public abstract LLVMVariable getVariableVersion();
	public abstract String toString();
	
	public static final LLVMReturn SIGMA =
		new LLVMReturn() {
			public boolean isSigma() {return true;}
			public LLVMVariable getVariableVersion() {return LLVMVariable.SIGMA;}
			public String toString() {return "SigmaReturn";}
		};
		
	public static final LLVMReturn VALUE =
		new LLVMReturn() {
			public boolean isValue() {return true;}
			public LLVMVariable getVariableVersion() {return LLVMVariable.RETURN;}
			public String toString() {return "ValueReturn";}
		};
}
