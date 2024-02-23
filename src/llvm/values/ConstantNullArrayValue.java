package llvm.values;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import llvm.bitcode.UnsignedLong;
import llvm.types.ArrayType;

/** 
 * This represents a constant array value where all of its elements are the 
 * constant null value for that type.
 */
public class ConstantNullArrayValue extends ConstantArrayValue {
	public ConstantNullArrayValue(ArrayType _type) {
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
		if (this.type.getNumElements().signedValue() == 0)
			return new ValueIterator();
		else
			return new ValueIterator(getElement(0));
	}
	
	public String toString() {
		return "[" + Value.getNullValue(this.type.getElementType()) + " x " + this.type.getNumElements() + "]";
	}
	public int hashCode() {
		return this.type.hashCode()*257 + 
			Value.getNullValue(this.type.getElementType()).hashCode()*337*this.type.getNumElements().hashCode();
	}
	
	protected ConstantArrayValue rewriteChildren(Map<Value,Value> old2new) {
		Value original = this.getElement(0);
		Value rewrite = original.rewrite(old2new);
		if (original != rewrite) {
			// make a new freaking array
			List<Value> explicit = new ArrayList<Value>((int)this.getNumElements().signedValue());
			for (int i = 0; i < this.getNumElements().signedValue(); i++)
				explicit.add(rewrite);
			return new ConstantExplicitArrayValue(this.getType(), explicit);
		} else {
			return this;
		}
	}
}
