package llvm.instructions;

import java.util.Iterator;
import java.util.Map;

import llvm.types.PointerType;
import llvm.types.Type;
import llvm.types.TypeIterator;
import llvm.values.Value;
import llvm.values.ValueIterator;

/**
 * This represents the MALLOC instruction.
 * It takes an element type, a number-of-elements integer value,
 * and a literal alignment for the result pointer.
 */
public class MallocInstruction extends Instruction {
	protected final Type elementType;
	protected final Value numElementsValue;
	protected final int alignment;
	
	public MallocInstruction(Type _type, Value _numElementsValue, int _alignment) {
		if (!_type.hasTypeSize())
			throw new IllegalArgumentException("Malloced type must be sized");
		if (!_numElementsValue.getType().equals(Type.getIntegerType(32)))
			throw new IllegalArgumentException("Numelements value must have type i32");
		
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
		return new TypeIterator(this.elementType, this.numElementsValue.getType());
	}
	public boolean isMalloc() {return true;}
	public MallocInstruction getMallocSelf() {return this;}
	
	public String toString() {
		return "malloc " + this.elementType + " " + this.numElementsValue + 
		(this.alignment != 0 ? " alignment=" + this.alignment : "");
	}
	public boolean equalsInstruction(Instruction i) {
		if (!i.isMalloc())
			return false;
		MallocInstruction m = i.getMallocSelf();
		return this.elementType.equals(m.elementType) && this.numElementsValue.equalsValue(m.numElementsValue) && this.alignment == m.alignment;
	}
	public int hashCode() {
		return this.elementType.hashCode()*3 + this.numElementsValue.hashCode()*47 + this.alignment*79;
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newnum = this.numElementsValue.rewrite(old2new);
		if (newnum == this.numElementsValue)
			return this;
		else
			return new MallocInstruction(this.elementType, newnum, this.alignment);
	}
}
