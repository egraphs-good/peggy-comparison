package peggy.represent.llvm;

/**
 * This is used as the P parameter for LLVM CFGs.
 * A LLVMParameter is one of:
 * 1) function parameter
 * 2) sigma input
 */
public abstract class LLVMParameter {
	public abstract LLVMVariable getVariableVersion();
	
	public boolean isArgument() {return false;}
	public ArgumentLLVMParameter getArgumentSelf() {throw new UnsupportedOperationException();}
	
	public boolean isSigma() {return false;}
	
	public abstract boolean equals(Object o);
	public abstract int hashCode();
	public abstract String toString();
	
	public static final LLVMParameter SIGMA = 
		new LLVMParameter() {
			public boolean isSigma() {return true;}
			public LLVMVariable getVariableVersion() {return LLVMVariable.SIGMA;}
			public boolean equals(Object o) {
				if (o == null || !(o instanceof LLVMParameter))
					return false;
				return ((LLVMParameter)o).isSigma();
			}
			public int hashCode() {return 101*79*31;}
			public String toString() {return "SigmaParameter";}
		};
}
