package peggy.represent.llvm;

import llvm.types.FunctionType;

/**
 * This is an LLVMLabel that names a particular LLVM function.
 * This includes the function name and the function type.
 */
public class FunctionLLVMLabel extends LLVMLabel {
	protected final FunctionType function;
	protected final String functionName;
	
	public FunctionLLVMLabel(FunctionType _function, String _functionName) {
		this.function = _function;
		this.functionName = _functionName;
	}
	
	public FunctionType getType() {return this.function;}
	public String getFunctionName() {return this.functionName;}
	
	public boolean isFunction() {return true;}
	public FunctionLLVMLabel getFunctionSelf() {return this;}
	public boolean equalsLabel(LLVMLabel label) {
		if (!label.isFunction()) return false;
		FunctionLLVMLabel f = label.getFunctionSelf();
		return this.getType().equals(f.getType()) && this.getFunctionName().equals(f.getFunctionName());
	}
	public int hashCode() {
		return this.getType().hashCode()*83 + this.getFunctionName().hashCode()*107;
	}
	public String toString() {
		return "Function " + this.getFunctionName();
	}
	public boolean isRevertible() {return true;}
}
