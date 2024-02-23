package peggy.represent.llvm;

/**
 * This is a CFGInstruction that extracts an exception value from a given variable.
 */
public class ExtractExceptionCFGInstruction extends CFGInstruction {
	protected final LLVMVariable source;
	
	public ExtractExceptionCFGInstruction(LLVMVariable _source) {
		this.source = _source;
	}
	
	public LLVMVariable getSource() {return this.source;}
	
	public boolean isExtractException() {return true;}
	public ExtractExceptionCFGInstruction getExtractExceptionSelf() {return this;}
	
	public String toString() {
		return "extractException(" + this.source + ")";
	}
}
