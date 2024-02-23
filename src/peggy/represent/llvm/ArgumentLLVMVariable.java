package peggy.represent.llvm;

import llvm.types.Type;

/**
 * This is the LLVMVariable for function parameters.
 */
public class ArgumentLLVMVariable extends LLVMVariable {
	protected final FunctionLLVMLabel function;
	protected final int argumentIndex;
	protected final Type argumentType;
	
	public ArgumentLLVMVariable(
			FunctionLLVMLabel _label,
			int _index,
			Type _type) {
		this.function = _label;
		this.argumentIndex = _index;
		this.argumentType = _type;
	}
	
	public Type getType() {return this.argumentType;}
	public FunctionLLVMLabel getFunction() {return this.function;}
	public int getIndex() {return this.argumentIndex;}
	
	public boolean isArgument() {return true;}
	public ArgumentLLVMVariable getArgumentSelf() {return this;}
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof LLVMVariable))
			return false;
		LLVMVariable var = (LLVMVariable)o;
		if (!var.isArgument())
			return false;
		ArgumentLLVMVariable ar = var.getArgumentSelf();
		return ar.getFunction().equals(this.getFunction()) &&
			ar.getIndex() == this.getIndex() &&
			ar.getType().equalsType(this.getType());
	}
	public int hashCode() {
		return this.getFunction().hashCode()*41 +
			this.getIndex()*31 + 
			this.getType().hashCode()*37;
	}
	public String toString() {
		return "ArgumentVariable[" + getFunction() + "(" + getIndex() + "): " + getType() + "]";
	}
}
