package peggy.represent.llvm;

/**
 * This is a CFGInstruction that copies the value of the given variable.
 */
public class CopyCFGInstruction extends CFGInstruction {
	protected final LLVMVariable copy;
	
	public CopyCFGInstruction(LLVMVariable _copy) {
		this.copy = _copy;
	}
	
	public LLVMVariable getValue() {return this.copy;}
	
	public boolean isCopy() {return true;}
	public CopyCFGInstruction getCopySelf() {return this;}
	public String toString() {
		return "Copy[" + this.getValue() + "]";
	}
}
