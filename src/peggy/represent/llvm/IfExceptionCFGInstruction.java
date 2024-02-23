package peggy.represent.llvm;

/**
 * This is a CFGInstruction that tests if a given variable contains an 
 * exception value in a LLVMCFG.
 */
public class IfExceptionCFGInstruction extends CFGInstruction {
	protected final LLVMVariable source;
	
	public IfExceptionCFGInstruction(LLVMVariable _source) {
		this.source = _source;
	}
	
	public LLVMVariable getSource() {return this.source;}
	
	public boolean isIfException() {return true;}
	public IfExceptionCFGInstruction getIfExceptionSelf() {return this;}
	
	public String toString() {
		return "ifException(" + this.source + ")";
	}
}
