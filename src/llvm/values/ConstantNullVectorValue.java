package llvm.values;

import java.util.*;

import llvm.bitcode.UnsignedLong;
import llvm.types.VectorType;

/**
 * This represents a constant vector all of whose elements are the null 
 * value for the given type (i.e. some form of 0).
 */
public class ConstantNullVectorValue extends ConstantVectorValue {
	public ConstantNullVectorValue(VectorType _type) {
		super(_type);
	}
	
	public Value getElement(int i) {
		UnsignedLong ui = new UnsignedLong(i);
		if (i < 0 || (ui.gt(this.getNumElements()) || ui.equals(this.getNumElements())))
			throw new IndexOutOfBoundsException(""+i);
		return Value.getNullValue(this.type.getElementType());
	}
	
	public void ensureConstant() {}
	public Iterator<? extends Value> getSubvalues() {
		return new ValueIterator(getElement(0));
	}
	
	public String toString() {
		return "<" + Value.getNullValue(this.type.getElementType()) + " x " + this.type.getNumElements() + "> null";
	}
	public int hashCode() {
		return this.type.hashCode()*97 + 
			Value.getNullValue(this.type.getElementType()).hashCode()*107*this.type.getNumElements().hashCode();
	}
	
	protected ConstantVectorValue rewriteChildren(Map<Value,Value> old2new) {
		Value original = this.getElement(0);
		Value rewrite = original.rewrite(old2new);
		if (original != rewrite) {
			// make a new freaking vector
			List<Value> explicit = new ArrayList<Value>((int)this.getNumElements().signedValue());
			for (int i = 0; i < this.getNumElements().signedValue(); i++)
				explicit.add(rewrite);
			return new ConstantExplicitVectorValue(this.getType(), explicit);
		} else {
			return this;
		}
	}
}
