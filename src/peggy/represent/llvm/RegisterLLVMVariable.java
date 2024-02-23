package peggy.represent.llvm;

import llvm.values.VirtualRegister;

/**
 * This is an LLVMVariable for LLVM virtual registers.
 */
public class RegisterLLVMVariable extends LLVMVariable {
	protected final VirtualRegister register;
	
	public RegisterLLVMVariable(VirtualRegister _vr) {
		this.register = _vr;
	}
	
	public VirtualRegister getVirtualRegister() {return this.register;}
	
	public boolean isRegister() {return true;}
	public RegisterLLVMVariable getRegisterSelf() {return this;}
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof LLVMVariable))
			return false;
		LLVMVariable var = (LLVMVariable)o;
		if (!var.isRegister())
			return false;
		return var.getRegisterSelf().getVirtualRegister().equals(this.getVirtualRegister());
	}
	public int hashCode() {
		return this.getVirtualRegister().hashCode()*101;
	}
	public String toString() {
		return "RegisterVariable[" + this.getVirtualRegister() + "]";
	}
}
