package llvm.bitcode;

/**
 * This interface describes a generic DataRecord, which contains operand values.
 * A datarecord can either be abbreviated or unabbreviated, but those details
 * are opaque against this interface.
 */
public interface DataRecord extends BlockContents {
	public int getCode();
	
	public int getNumOps();
	public OperandValue getOp(int i);
	public String toString();
	
	public int getNumUnflattenedOps();
	public OperandValue getUnflattenedOp(int i);
	public String toUnflattenedString();
}
