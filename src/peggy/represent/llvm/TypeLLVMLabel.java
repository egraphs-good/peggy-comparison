package peggy.represent.llvm;

import llvm.types.Type;

/**
 * This is an LLVMLabel that names a particular LLVM type.
 */
public class TypeLLVMLabel extends LLVMLabel {
	protected final Type type;
	
	public TypeLLVMLabel(Type _type) {
		this.type = _type;
	}
	
	public Type getType() {return this.type;}
	
	public boolean isType() {return true;}
	public TypeLLVMLabel getTypeSelf() {return this;}
	
	public boolean equalsLabel(LLVMLabel label) {
		if (!label.isType()) return false;
		TypeLLVMLabel t = label.getTypeSelf();
		return t.getType().equalsType(this.getType());
	}
	public int hashCode() {
		return this.getType().hashCode()*59;
	}
	public String toString() {
		String typestr = this.getType().toString();
		if (typestr.length() > 10)
			typestr = typestr.substring(0,10) + " ...";
		return "Type[" + typestr + "]";
	}
	public boolean isRevertible() {return true;}
}
