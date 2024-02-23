package llvm.values;

import llvm.bitcode.UnsignedLong;
import llvm.types.ArrayType;

/**
 * This represents a constant array value, all of whose elements
 * must be constants.
 */
public abstract class ConstantArrayValue extends Value {
	protected final ArrayType type;
	
	protected ConstantArrayValue(ArrayType _type) {
		this.type = _type;
	}

	public final UnsignedLong getNumElements() {return this.type.getNumElements();}
	public abstract Value getElement(int i);
	
	public final ArrayType getType() {return this.type;}
	public final boolean isConstantArray() {return true;}
	public final ConstantArrayValue getConstantArraySelf() {return this;}

	public final boolean equalsValue(Value o) {
		if (!o.isConstantArray())
			return false;
		ConstantArrayValue c = o.getConstantArraySelf();
		if (!this.getType().equalsType(c.getType()))
			return false;
		for (int i = 0; i < this.getNumElements().signedValue(); i++) {
			if (!this.getElement(i).equalsValue(c.getElement(i)))
				return false;
		}
		return true;
	}
}
