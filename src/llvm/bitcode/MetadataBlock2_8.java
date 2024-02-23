package llvm.bitcode;

/**
 * This class contains the blocks and records that define a metadata block in an
 * LLVM 2.8 bitcode file.
 */
public class MetadataBlock2_8 extends Block {
	public static final int METADATA_BLOCK_ID = 15;
	///////////////////////////////////////////////
	public static final int METADATA_STRING      = 1; 
	public static final int METADATA_NODE        = 2;
	public static final int METADATA_FN_NODE     = 3;
	public static final int METADATA_NAME        = 4;
	public static final int METADATA_NAMED_NODE  = 5;
	public static final int METADATA_KIND        = 6; 
	public static final int METADATA_NODE2       = 8;
	public static final int METADATA_FN_NODE2    = 9;
	public static final int METADATA_NAMED_NODE2 = 10;
	
	public MetadataBlock2_8(EnterSubblock _enter) {
		super(_enter);
	}
	
	protected void verifyContents(BlockContents bc) {
		if (bc.isBlock())
			throw new IllegalArgumentException("METADATA_BLOCK can have no subblocks");

		DataRecord record = bc.getDataRecordSelf();

		switch (record.getCode()) {
		case METADATA_STRING: // chars
			break;
			
		case METADATA_NODE:
		case METADATA_NODE2:
		case METADATA_FN_NODE:
		case METADATA_FN_NODE2:
			if (record.getNumOps()%2 == 1)
				throw new IllegalArgumentException("METADATA_* needs an odd number of numeric arguments");
			break;
			
		case METADATA_NAME:
			break;
			
		case METADATA_NAMED_NODE:
		case METADATA_NAMED_NODE2:
			break;
		
		case METADATA_KIND:
			assertNumericRecord(record, 1, "METADATA_KIND");
			break;

		default:
			throw new IllegalArgumentException("Unknown data record: " + record);
		}
	}

	protected boolean verify(EnterSubblock _enter) {
		return _enter.getBlockID() == METADATA_BLOCK_ID;
	}
	
	public boolean isMetadata2_8() {return true;}
	public MetadataBlock2_8 getMetadata2_8Self() {return this;}
}
