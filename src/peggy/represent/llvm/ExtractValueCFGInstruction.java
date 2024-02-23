package peggy.represent.llvm;

/**
 * This is a CFGInstruction that simply returns the value of the given variable.
 */
public class ExtractValueCFGInstruction extends CFGInstruction {
	protected final LLVMVariable source;

	public ExtractValueCFGInstruction(LLVMVariable _source) {
		this.source = _source;
	}

	public LLVMVariable getSource() {return this.source;}

	public boolean isExtractValue() {return true;}
	public ExtractValueCFGInstruction getExtractValueSelf() {return this;}
	
	public String toString() {
		return "extractValue(" + this.source + ")";
	}
}
