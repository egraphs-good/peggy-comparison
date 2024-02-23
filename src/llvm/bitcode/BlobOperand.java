package llvm.bitcode;

/**
 * This is an operand type that represents a Blob, or an uninterpreted
 * array of bytes.
 */
public class BlobOperand extends Operand {
	public static final BlobOperand INSTANCE = new BlobOperand();
	public static final FixedOperand FIX8 = new FixedOperand(8);
	
	private BlobOperand() {}

	public FixedOperand getElementType() {return FIX8;}
	public final boolean isBlob() {return true;}
	public final BlobOperand getBlobSelf() {return this;}
	
	public String toString() {
		return "Blob";
	}
	public boolean equals(Object o) {
		return (o != null) && (o instanceof BlobOperand);
	}
	public int hashCode() {
		return 91;
	}
}
