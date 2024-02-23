package llvm.values;

import llvm.bitcode.UnsignedLong;
import llvm.types.VectorType;

/**
 * This represents a constant vector value.
 * All of the elements of a constant vector are themselves constants.
 */
public abstract class ConstantVectorValue extends Value {
	protected final VectorType type;
	
	protected ConstantVectorValue(VectorType _type) {
		this.type = _type;
	}

	public final UnsignedLong getNumElements() {return this.type.getNumElements();}
	public abstract Value getElement(int i);

	public final VectorType getType() {return this.type;}
	public final boolean isConstantVector() {return true;}
	public final ConstantVectorValue getConstantVectorSelf() {return this;}

	public final boolean equalsValue(Value o) {
		if (!o.isConstantVector())
			return false;
		ConstantVectorValue c = o.getConstantVectorSelf();
		if (!this.getType().equalsType(c.getType()))
			return false;
		for (int i = 0; i < this.getNumElements().signedValue(); i++) {
			if (!this.getElement(i).equalsValue(c.getElement(i)))
				return false;
		}
		return true;
	}
}
