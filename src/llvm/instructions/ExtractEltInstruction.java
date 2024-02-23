package llvm.instructions;

import java.util.Iterator;
import java.util.Map;

import llvm.types.Type;
import llvm.types.TypeIterator;
import llvm.values.Value;
import llvm.values.ValueIterator;

/**
 * This represents the EXTRACTELT instruction, which takes a vector value
 * and an index value and returns the element of the vector at that index.
 */
public class ExtractEltInstruction extends Instruction {
	protected final Value vector;
	protected final Value index;
	
	public ExtractEltInstruction(Value _vector, Value _index) {
		if (!(_vector.getType().isComposite() && _vector.getType().getCompositeSelf().isVector()))
			throw new IllegalArgumentException("Vector argument does not have vector type");
		if (!_index.getType().equals(Type.getIntegerType(32)))
			throw new IllegalArgumentException("Index must be an i32");
		this.vector = _vector;
		this.index = _index;
	}
	
	public Value getVector() {return this.vector;}
	public Value getIndex() {return this.index;}

	public Type getType() {return this.vector.getType().getCompositeSelf().getVectorSelf().getElementType();}
	public Iterator<? extends Value> getValues() {
		return new ValueIterator(this.vector, this.index);
	}
	public Iterator<? extends Type> getTypes() {
		return new TypeIterator(this.vector.getType(), this.index.getType());
	}
	public boolean isExtractElt() {return true;}
	public ExtractEltInstruction getExtractEltSelf() {return this;}
	
	public String toString() {
		return "extractelement ( " + this.vector + ", " + this.index + " )";
	}
	public boolean equalsInstruction(Instruction o) {
		if (!o.isExtractElt())
			return false;
		ExtractEltInstruction c = o.getExtractEltSelf();
		return this.vector.equalsValue(c.vector) && this.index.equalsValue(c.index);
	}
	public int hashCode() {
		return this.vector.hashCode()*443 + this.index.hashCode()*449;
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newvector = this.vector.rewrite(old2new);
		Value newindex = this.index.rewrite(old2new);
		if (newvector == this.vector && newindex == this.index)
			return this;
		else 
			return new ExtractEltInstruction(newvector, newindex);
	}
}
