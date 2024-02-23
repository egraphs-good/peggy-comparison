package llvm.values;

import java.util.Iterator;
import java.util.Map;

import llvm.types.Type;

/**
 * Represents an undefined value.
 * An undefined value has only a type, its value is undefined.
 */
public class UndefValue extends Value {
	protected final Type type;
	
	public UndefValue(Type _type) {
		if (_type == null)
			throw new NullPointerException("type is null");
		if (_type.equals(Type.VOID_TYPE) || _type.equals(Type.LABEL_TYPE))
			throw new IllegalArgumentException("type must not be label or void");
		this.type = _type;
	}
	
	public void ensureConstant() {}
	public Type getType() {return this.type;}
	public Iterator<? extends Value> getSubvalues() {
		return new ValueIterator();
	}
	public boolean isUndef() {return true;}
	public UndefValue getUndefSelf() {return this;}
	
	public String toString() {
		return "undef[" + this.type + "]";
	}
	public boolean equalsValue(Value o) {
		if (!o.isUndef())
			return false;
		UndefValue u = o.getUndefSelf();
		return this.type.equalsType(u.type);
	}
	public int hashCode() {
		return this.type.hashCode()*17;
	}
	
	protected UndefValue rewriteChildren(Map<Value,Value> old2new) {
		return this;
	}
}
