package llvm.bitcode;

/**
 * This is a generic interface for all objects that can live inside of
 * any bitcode Block. The only two types of objects are blocks, which contain
 * more BlockContents, and DataRecords, which contain operand values.
 */
public interface BlockContents {
	public boolean isDataRecord();
	public DataRecord getDataRecordSelf();
	
	public boolean isBlock();
	public Block getBlockSelf();
}
