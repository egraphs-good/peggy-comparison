package peggy.represent.llvm;

import llvm.types.Type;

/**
 * This is the LLVMParameter for function parameters.
 */
public class ArgumentLLVMParameter extends LLVMParameter {
	protected final ArgumentLLVMVariable variable;
	
	public ArgumentLLVMParameter(ArgumentLLVMVariable _variable) {
		this.variable = _variable;
	}
	
	public Type getType() {return variable.getType();}
	public int getIndex() {return this.variable.getIndex();}
	public ArgumentLLVMVariable getVariableVersion() {return this.variable;}
	public boolean isArgument() {return true;}
	public ArgumentLLVMParameter getArgumentSelf() {return this;}
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof LLVMParameter))
			return false;
		LLVMParameter p = (LLVMParameter)o;
		if (!p.isArgument()) return false;
		return p.getArgumentSelf().getVariableVersion().equals(this.getVariableVersion());
	}
	public int hashCode() {
		return this.getVariableVersion().hashCode() * 79;
	}
	public String toString() {
		return "ArgumentParameter[" + this.getIndex() + "]"; 
	}
}
