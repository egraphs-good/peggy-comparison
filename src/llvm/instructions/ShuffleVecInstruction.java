package llvm.instructions;

import java.util.Iterator;
import java.util.Map;

import llvm.types.Type;
import llvm.types.TypeIterator;
import llvm.types.VectorType;
import llvm.values.Value;
import llvm.values.ValueIterator;

/**
 * This represents the SHUFFLEVEC instruction in LLVM 2.3.
 * It takes two vector values (of the same type) and a shuffle mask value.
 */
public class ShuffleVecInstruction extends Instruction {
	protected final Value vector1, vector2, shuffle;
	
	public ShuffleVecInstruction(Value _vector1, Value _vector2, Value _shuffle) {
		if (!(_vector1.getType().isComposite() && _vector1.getType().getCompositeSelf().isVector()))
			throw new IllegalArgumentException("Vector 1 does not have vector type");
		if (!_vector1.getType().equals(_vector2.getType()))
			throw new IllegalArgumentException("Vector 1 and vector 2 have different types");
		Type shuffleType = new VectorType(Type.getIntegerType(32), _vector1.getType().getCompositeSelf().getVectorSelf().getNumElements());
		if (!_shuffle.getType().equals(shuffleType))
			throw new IllegalArgumentException("Shuffle vector has incorrect type");
		this.vector1 = _vector1;
		this.vector2 = _vector2;
		this.shuffle = _shuffle;
	}
	
	public Value getVector1() {return this.vector1;}
	public Value getVector2() {return this.vector2;}
	public Value getShuffleVector() {return this.shuffle;}
	
	public Type getType() {return this.vector2.getType();}
	public Iterator<? extends Value> getValues() {
		return new ValueIterator(this.vector1, this.vector2, this.shuffle);
	}
	public Iterator<? extends Type> getTypes() {
		return new TypeIterator(this.vector1.getType(), this.vector2.getType(), this.shuffle.getType());
	}
	public boolean isShuffleVec() {return true;}
	public ShuffleVecInstruction getShuffleVecSelf() {return this;}

	public String toString() {
		return "shufflevector ( " + this.vector1 + ", " + this.vector2 + ", " + this.shuffle + " )";
	}
	public boolean equalsInstruction(Instruction o) {
		if (!o.isShuffleVec())
			return false;
		ShuffleVecInstruction c = o.getShuffleVecSelf();
		return this.vector1.equalsValue(c.vector1) && this.vector2.equalsValue(c.vector2) && this.shuffle.equalsValue(c.shuffle);
	}
	public int hashCode() {
		return 
			this.vector1.hashCode()*499 +
			this.vector2.hashCode()*443 +
			this.shuffle.hashCode()*389;
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newv1 = this.vector1.rewrite(old2new);
		Value newv2 = this.vector2.rewrite(old2new);
		Value newshuffle = this.shuffle.rewrite(old2new);
		if (newv1 == this.vector1 && newv2 == this.vector2 && newshuffle == this.shuffle)
			return this;
		else
			return new ShuffleVecInstruction(newv1, newv2, newshuffle);
	}
}
