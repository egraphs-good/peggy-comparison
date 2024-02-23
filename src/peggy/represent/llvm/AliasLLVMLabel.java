package peggy.represent.llvm;

import llvm.types.Type;

/**
 * This is an LLVMLabel for LLVM alias values.
 * The type should be the value type, not the pointer type.
 */
public class AliasLLVMLabel extends LLVMLabel {
	protected final String name;
	protected final Type type;
	
	public AliasLLVMLabel(Type _type, String _name) {
		this.type = _type;
		this.name = _name;
	}
	
	public String getName() {return this.name;}
	public Type getType() {return this.type;}
	
	public boolean isAlias() {return true;}
	public AliasLLVMLabel getAliasSelf() {return this;}
	public boolean equalsLabel(LLVMLabel label) {
		if (!label.isAlias())
			return false;
		AliasLLVMLabel g = label.getAliasSelf();
		return g.getName().equals(this.getName()) && g.getType().equalsType(this.getType());
	}
	public int hashCode() {
		return this.getName().hashCode()*113 + this.getType().hashCode()*11;
	}
	public String toString() {
		return "Alias " + this.getName();
	}
	public boolean isRevertible() {return true;}
}

