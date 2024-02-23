package llvm.instructions;

/**
 * This represents the predefined calling conventions defined in LLVM.
 */
public enum CallingConvention {
	C(0),
	Fast(8),
	Cold(9),
	FirstTargetCC(64),
	X86_StdCall(64),
	X86_FastCall(65),
	X86_SSECall(66);
	
	private int value;
	private CallingConvention(int _value) {
		this.value = _value;
	}
	public int getValue() {return this.value;}
}
