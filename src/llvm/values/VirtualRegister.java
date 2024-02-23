package llvm.values;

import java.util.Iterator;
import java.util.Map;

import llvm.types.Type;

/**
 * This represents a virtual register value. 
 * A virtual register only has a type.
 * The way to assign a value (instruction) to a virtual register is
 * through a RegisterAssignment within a FunctionBody.
 */
public class VirtualRegister extends Value {
	private static int indexCounter = 0;
	public static VirtualRegister getVirtualRegister(Type _type) {
		if (!_type.isFirstClass())
			throw new IllegalArgumentException("Register type must be first-class: " + _type);
		return new VirtualRegister(indexCounter++, _type);
	}
	
	protected final int index;
	protected final Type type;
	
	private VirtualRegister(int _index, Type _type) {
		this.index = _index;
		this.type = _type;
	}
	
	public boolean isFunctionLocal() {return true;}
	
	public void ensureConstant() {throw new IllegalStateException();}
	public Type getType() {return this.type;}
	public Iterator<? extends Value> getSubvalues() {
		return new ValueIterator();
	}
	public boolean isRegister() {return true;}
	public VirtualRegister getRegisterSelf() {return this;}
	
	public String toString() {
		return "%reg" + this.index + "[" + this.type + "]";
	}
	public boolean equalsValue(Value v) {
		if (!v.isRegister())
			return false;
		return this.index == v.getRegisterSelf().index &&
			this.type.equalsType(v.getRegisterSelf().getType());
	}
	public int hashCode() {
		return this.index*7 + this.type.hashCode()*11;
	}
	
	protected VirtualRegister rewriteChildren(Map<Value,Value> old2new) {
		return this;
	}
}
