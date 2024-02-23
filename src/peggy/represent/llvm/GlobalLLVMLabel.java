package peggy.represent.llvm;

import llvm.types.Type;

/**
 * This is an LLVMLabel that represents a particular LLVM global value.
 * It names the type and the name of the global.
 * The type should be the value type, not the pointer type.
 */
public class GlobalLLVMLabel extends LLVMLabel {
	protected final String name;
	protected final Type type;
	
	public GlobalLLVMLabel(Type _type, String _name) {
		this.type = _type;
		this.name = _name;
	}
	
	public String getName() {return this.name;}
	public Type getType() {return this.type;}
	
	public boolean isGlobal() {return true;}
	public GlobalLLVMLabel getGlobalSelf() {return this;}
	public boolean equalsLabel(LLVMLabel label) {
		if (!label.isGlobal())
			return false;
		GlobalLLVMLabel g = label.getGlobalSelf();
		return g.getName().equals(this.getName()) && g.getType().equalsType(this.getType());
	}
	public int hashCode() {
		return this.getName().hashCode()*101 + this.getType().hashCode()*7;
	}
	public String toString() {
		return "Global " + this.getName();
	}
	public boolean isRevertible() {return true;}
}

