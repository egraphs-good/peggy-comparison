package llvm.instructions;

import java.util.Iterator;
import java.util.Map;

import llvm.types.Type;
import llvm.types.TypeIterator;
import llvm.values.Value;
import llvm.values.ValueIterator;

/**
 * This represents the set of casting instructions, which take a value
 * and implicitly have a destination type, and coerce the given value to 
 * that type.
 */
public class CastInstruction extends Instruction {
	protected final Cast cast;
	protected final Type destinationType;
	protected final Value castee;
	
	public CastInstruction(Cast _cast, Type _dest, Value _value) {
		if (!_cast.isValid(_value.getType(), _dest))
			throw new IllegalArgumentException("Invalid operands for cast: " + _cast.name().toLowerCase() + ", " + _value.getType() + ", " + _dest);
		this.cast = _cast;
		this.destinationType = _dest;
		this.castee = _value;
	}
	
	public Cast getCast() {return this.cast;}
	public Type getDestinationType() {return this.destinationType;}
	public Value getCastee() {return this.castee;}

	public Type getType() {return this.destinationType;}
	public Iterator<? extends Value> getValues() {
		return new ValueIterator(this.castee);
	}
	public Iterator<? extends Type> getTypes() {
		return new TypeIterator(this.destinationType, this.castee.getType());
	}
	public boolean isCast() {return true;}
	public CastInstruction getCastSelf() {return this;}
	
	public String toString() {
		return this.cast.name().toLowerCase() + " " + this.castee + " to " + this.destinationType;
	}
	public boolean equalsInstruction(Instruction inst) {
		if (!inst.isCast())
			return false;
		CastInstruction c = inst.getCastSelf();
		return this.cast.equals(c.cast) && this.destinationType.equals(c.destinationType) && this.castee.equals(c.castee);
	}
	public int hashCode() {
		return this.cast.hashCode()*2 + this.destinationType.hashCode()*97 + this.castee.hashCode()*13;
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newcastee = this.castee.rewrite(old2new);
		if (newcastee == this.castee)
			return this;
		else
			return new CastInstruction(this.cast, this.destinationType, newcastee);
	}
}
