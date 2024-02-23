package llvm.bitcode;

/**
 * This defines the type of array operands, which are homogeneous 
 * arrays of BasicOperands.
 */
public final class ArrayOperand extends Operand {
	protected final BasicOperand elementType;

	public ArrayOperand(BasicOperand _elementType) {
		this.elementType = _elementType;
	}

	public BasicOperand getElementType() {return this.elementType;}
	public final boolean isArray() {return true;}
	public final ArrayOperand getArraySelf() {return this;}

	public String toString() {
		return "Array(" + this.elementType.toString() + ")";
	}
	public boolean equals(Object o) {
		if (o == null || !(o instanceof ArrayOperand))
			return false;
		ArrayOperand array = (ArrayOperand)o;
		return this.elementType.equals(array.elementType);
	}
	public int hashCode() {
		return 5*this.elementType.hashCode();
	}
}
