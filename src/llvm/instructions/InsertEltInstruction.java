package llvm.instructions;

import java.util.Iterator;
import java.util.Map;

import llvm.types.Type;
import llvm.types.TypeIterator;
import llvm.values.Value;
import llvm.values.ValueIterator;

/**
 * This represents the INSERTELEMENT instruction.
 * It takes a vector value, an element value, and an index value.
 */
public class InsertEltInstruction extends Instruction {
	protected final Value vector;
	protected final Value element;
	protected final Value index;
	
	public InsertEltInstruction(Value _vector, Value _element, Value _index) {
		if (!(_vector.getType().isComposite() && _vector.getType().getCompositeSelf().isVector()))
			throw new IllegalArgumentException("Vector does not have vector type");
		if (!_vector.getType().getCompositeSelf().getVectorSelf().getElementType().equals(_element.getType()))
			throw new IllegalArgumentException("Element type does not match vector element type");
		if (!_index.getType().equals(Type.getIntegerType(32)))
			throw new IllegalArgumentException("Index must be an i32");
		this.vector = _vector;
		this.element = _element;
		this.index = _index;
	}
	
	public Value getVector() {return this.vector;}
	public Value getElement() {return this.element;}
	public Value getIndex() {return this.index;}

	public Type getType() {return this.vector.getType();}
	public Iterator<? extends Value> getValues() {
		return new ValueIterator(this.vector, this.element, this.index);
	}
	public Iterator<? extends Type> getTypes() {
		return new TypeIterator(this.vector.getType(), this.element.getType(), this.index.getType());
	}
	public boolean isInsertElt() {return true;}
	public InsertEltInstruction getInsertEltSelf() {return this;}
	
	public String toString() {
		return "insertelement ( " + this.vector + ", " + this.element + ", " + this.index + " )";
	}
	public boolean equalsInstruction(Instruction o){
		if (!o.isInsertElt())
			return false;
		InsertEltInstruction c = o.getInsertEltSelf();
		return this.vector.equalsValue(c.vector) && this.element.equalsValue(c.element) && this.index.equalsValue(c.index);
	}
	public int hashCode() {
		return 
			this.vector.hashCode()*541 +
			this.element.hashCode()*523 +
			this.index.hashCode()*521;
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newvector = this.vector.rewrite(old2new);
		Value newelement = this.element.rewrite(old2new);
		Value newindex = this.index.rewrite(old2new);
		if (newvector == this.vector && newelement == this.element && newindex == this.index)
			return this;
		else
			return new InsertEltInstruction(newvector, newelement, newindex);
	}
}
