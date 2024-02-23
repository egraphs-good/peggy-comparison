package peggy.represent.llvm;

/**
 * This class is used as the V parameter of a CFG for LLVM PEGs.
 * A LLVMVariable is one of:
 * 1) value return variable
 * 2) sigma return variable
 * 3) dummy variable
 * 4) virtual register variable
 * 5) function parameter variable
 */
public abstract class LLVMVariable {
	public boolean isReturn() {return false;}
	public boolean isSigma() {return false;}
	public boolean isDummy() {return false;}
	
	public boolean isRegister() {return false;}
	public RegisterLLVMVariable getRegisterSelf() {throw new UnsupportedOperationException();}

	public boolean isArgument() {return false;}
	public ArgumentLLVMVariable getArgumentSelf() {throw new UnsupportedOperationException();}
	
	public abstract int hashCode();
	public abstract boolean equals(Object o);
	public abstract String toString();
	
	public static final LLVMVariable RETURN = 
		new LLVMVariable() {
			public boolean isReturn() {return true;}
			public boolean equals(Object o) {
				if (o == null || !(o instanceof LLVMVariable))
					return false;
				return ((LLVMVariable)o).isReturn();
			}
			public int hashCode() {return 543879203;}
			public String toString() {return "ReturnVariable";}
		};

	public static final LLVMVariable SIGMA = 
		new LLVMVariable() {
			public boolean isSigma() {return true;}
			public boolean equals(Object o) {
				if (o == null || !(o instanceof LLVMVariable))
					return false;
				return ((LLVMVariable)o).isSigma();
			}
			public int hashCode() {return 439081754;}
			public String toString() {return "SigmaVariable";}
		};
		
	private static int NEXT = 1;
	public static LLVMVariable getDummyVariable() {
		final int id = (NEXT++);
		return new LLVMVariable() {
			private int myid = id;
			public boolean isDummy() {return true;}
			public boolean equals(Object o) {return o == this;}
			public int hashCode() {return this.myid*101*17;}
			public String toString() {return "DummyVariable[" + this.myid + "]";}
		};
	}
}
