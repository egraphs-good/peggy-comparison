package llvm.instructions;

import java.util.Iterator;
import java.util.Map;

import llvm.types.PointerType;
import llvm.types.Type;
import llvm.types.TypeIterator;
import llvm.values.Value;
import llvm.values.ValueIterator;

/**
 * This represents an ALLOCA instruction. It takes an element type, a 
 * number-of-elements value, and a literal alignment for the pointer.
 */
public class AllocaInstruction extends Instruction {
	protected final Type elementType;
	protected final Value numElementsValue;
	protected final int alignment;
	
	public AllocaInstruction(Type _type, Value _numElementsValue, int _alignment) {
		if (!_type.hasTypeSize())
			throw new IllegalArgumentException("Pointee type must be sized");
		if (!_numElementsValue.getType().isInteger())
			throw new IllegalArgumentException("numElementsValue must be integer type");
		this.elementType = _type;
		this.numElementsValue = _numElementsValue;
		this.alignment = _alignment;
	}
	
	public Type getElementType() {return this.elementType;}
	public Value getNumElementsValue() {return this.numElementsValue;}
	public int getAlignment() {return this.alignment;}
	
	public PointerType getType() {return new PointerType(this.elementType);}
	public Iterator<? extends Value> getValues() {
		return new ValueIterator(this.numElementsValue);
	}
	public Iterator<? extends Type> getTypes() {
		return new TypeIterator(this.getType(), this.numElementsValue.getType());
	}
	public boolean isAlloca() {return true;}
	public AllocaInstruction getAllocaSelf() {return this;}
	
	public String toString() {
		return "alloca " + this.elementType + " " + this.numElementsValue + 
		(this.alignment != 0 ? " alignment=" + this.alignment : "");
	}
	public boolean equalsInstruction(Instruction i) {
		if (!i.isAlloca())
			return false;
		AllocaInstruction a = i.getAllocaSelf();
		return this.elementType.equals(a.elementType) && 
			this.numElementsValue.equalsValue(a.numElementsValue) && 
			this.alignment == a.alignment;
	}
	public int hashCode() {
		return this.elementType.hashCode()*11 + this.numElementsValue.hashCode()*101 + this.alignment*5;
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newnum = this.numElementsValue.rewrite(old2new);
		if (newnum == this.numElementsValue)
			return this;
		else
			return new AllocaInstruction(this.elementType, newnum, this.alignment);
	}
}
