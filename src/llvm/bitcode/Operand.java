package llvm.bitcode;

/**
 * This class defines the types and encodings for the values in OperandValues.
 * These are mostly used inside of DEFINEABBREV records and abbreviated data records.
 */
public abstract class Operand {
	public boolean isBasic() {return false;}
	public BasicOperand getBasicSelf() {throw new UnsupportedOperationException();}

	public boolean isArray() {return false;}
	public ArrayOperand getArraySelf() {throw new UnsupportedOperationException();}
	
	public boolean isBlob() {return false;}
	public BlobOperand getBlobSelf() {throw new UnsupportedOperationException();}

	public boolean isNumeric() {return false;}
	
	public abstract String toString();
	public abstract boolean equals(Object o);
	public abstract int hashCode();
}
