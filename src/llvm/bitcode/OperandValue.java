package llvm.bitcode;

/**
 * This is the parent class of all operand value types.
 * An operand value contains a description of the type of the operand
 * (an Operand instance) as well as the value itself, usually encoded as
 * long integers.
 */
public abstract class OperandValue {
	public boolean isBasic() {return false;}
	public BasicOperandValue getBasicSelf() {throw new UnsupportedOperationException();}

	public boolean isArray() {return false;}
	public ArrayOperandValue getArraySelf() {throw new UnsupportedOperationException();}

	public boolean isBlob() {return false;}
	public BlobOperandValue getBlobSelf() {throw new UnsupportedOperationException();}
	
	public boolean isNumeric() {return this.getOperand().isNumeric();}
	public long getNumericValue() {throw new UnsupportedOperationException();}
	
	public abstract Operand getOperand();
	public abstract String toString();
}
