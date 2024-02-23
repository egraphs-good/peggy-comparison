package llvm.values;

import java.util.Iterator;
import java.util.Map;

import llvm.types.PointerType;

/**
 * This is the only constant pointer value, and it equals NULL.
 */
public class ConstantNullPointerValue extends Value {
	protected final PointerType type;
	
	public ConstantNullPointerValue(PointerType _type) {
		this.type = _type;
	}

	public void ensureConstant() {}
	public PointerType getType() {return this.type;}
	public Iterator<? extends Value> getSubvalues() {
		return new ValueIterator();
	}
	public boolean isConstantNullPointer() {return true;}
	public ConstantNullPointerValue getConstantNullPointerSelf() {return this;}
	
	public String toString() {
		return "NULLPTR";
	}
	public boolean equalsValue(Value o) {
		if (!o.isConstantNullPointer())
			return false;
		ConstantNullPointerValue n = o.getConstantNullPointerSelf();
		return this.type.equalsType(n.type);
	}
	public int hashCode() {
		return this.type.hashCode()*139;
	}
	
	protected ConstantNullPointerValue rewriteChildren(Map<Value,Value> old2new) {
		return this;
	}
}
