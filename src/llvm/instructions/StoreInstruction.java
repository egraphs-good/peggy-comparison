package llvm.instructions;

import java.util.Iterator;
import java.util.Map;

import llvm.types.Type;
import llvm.types.TypeIterator;
import llvm.values.Value;
import llvm.values.ValueIterator;

/** 
 * This represents the STORE/VOLATILESTORE instruction.
 * It takes an address value, a value to be stored, and an alignment.
 */
public class StoreInstruction extends Instruction {
	protected final Value address;
	protected final Value value;
	protected final int alignment;
	protected final boolean isVolatile;
	
	public StoreInstruction(Value _address, Value _value, int _alignment, boolean _volatile) {
		if (!(_address.getType().isComposite() && 
			  _address.getType().getCompositeSelf().isPointer()))
			throw new IllegalArgumentException("Address must have pointer type");
		if (!_address.getType().getCompositeSelf().getPointerSelf().getPointeeType().equalsType(_value.getType()))
			throw new IllegalArgumentException("Value type not compatible with address type: " + _address.getType().getCompositeSelf().getPointerSelf().getPointeeType() + ", " + _value.getType());
		if (!_value.getType().isFirstClass())
			throw new IllegalArgumentException("Value type must be first-class");
		
		this.address = _address;
		this.value = _value;
		this.alignment = _alignment;
		this.isVolatile = _volatile;
	}
	
	public Value getAddress() {return this.address;}
	public Value getValue() {return this.value;}
	public int getAlignment() {return this.alignment;}
	public boolean isVolatile() {return this.isVolatile;}
	
	public Type getType() {return Type.VOID_TYPE;}
	public Iterator<? extends Value> getValues() {
		return new ValueIterator(this.address, this.value);
	}
	public Iterator<? extends Type> getTypes() {
		return new TypeIterator(this.address.getType(), this.value.getType());
	}
	public boolean isStore() {return true;}
	public StoreInstruction getStoreSelf() {return this;}
	
	public String toString() {
		return "store [" + this.value + "], [" + this.address + "]" + (this.alignment!=0 ? ", alignment=" + this.alignment : "") + (this.isVolatile ? ", volatile" : "");
	}
	public boolean equalsInstruction(Instruction i) {
		if (!i.isStore())
			return false;
		StoreInstruction s = i.getStoreSelf();
		return this.address.equalsValue(s.address) && this.value.equalsValue(s.value) && this.alignment==s.alignment && this.isVolatile==s.isVolatile;
	}
	public int hashCode() {
		return this.address.hashCode()*19 + this.value.hashCode()*7 + this.alignment*11 + (this.isVolatile ? 9865896 : 45476);
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newvalue = this.value.rewrite(old2new);
		Value newaddress = this.address.rewrite(old2new);
		if (newvalue == this.value && newaddress == this.address)
			return this;
		else
			return new StoreInstruction(newaddress, newvalue, this.alignment, this.isVolatile);
	}
}
