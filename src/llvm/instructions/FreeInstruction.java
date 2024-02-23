package llvm.instructions;

import java.util.Iterator;
import java.util.Map;

import llvm.types.Type;
import llvm.types.TypeIterator;
import llvm.values.Value;
import llvm.values.ValueIterator;

/**
 * This class represents the FREE instruction, which takes a pointer value 
 * that has been MALLOC'ed and frees it.
 */
public class FreeInstruction extends Instruction {
	protected final Value value;
	
	public FreeInstruction(Value _value) {
		if (!(_value.getType().isComposite() && 
			  _value.getType().getCompositeSelf().isPointer()))
			throw new IllegalArgumentException("Value must have pointer type");
		if (!_value.getType().getCompositeSelf().getPointerSelf().getPointeeType().hasTypeSize())
			throw new IllegalArgumentException("Pointee must be sized");
		this.value = _value;
	}
	
	public Value getFreedValue() {return this.value;}
	
	public Type getType() {return Type.VOID_TYPE;}
	public Iterator<? extends Value> getValues() {
		return new ValueIterator(this.value);
	}
	public Iterator<? extends Type> getTypes() {
		return new TypeIterator(this.value.getType());
	}
	public boolean isFree() {return true;}
	public FreeInstruction getFreeSelf() {return this;}
	
	public String toString() {
		return "free " + this.value; 
	}
	public boolean equalsInstruction(Instruction i) {
		if (!i.isFree())
			return false;
		FreeInstruction f = i.getFreeSelf();
		return this.value.equalsValue(f.value);
	}
	public int hashCode() {
		return this.value.hashCode()*123;
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newvalue = this.value.rewrite(old2new);
		if (newvalue == this.value)
			return this;
		else
			return new FreeInstruction(newvalue);
	}
}
