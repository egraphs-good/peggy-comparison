package llvm.bitcode;

/**
 * This class contains the blocks and records for a metadata attachment block
 * from an LLVM 2.8 bitcode file.
 */
public class MetadataAttachmentBlock2_8 extends Block {
	public static final int METADATA_ATTACHMENT_BLOCK_ID = 16;
	//////////////////////////////////////////////////////////
	public static final int METADATA_ATTACHMENT  = 7;
	public static final int METADATA_ATTACHMENT2 = 11;
	
	public MetadataAttachmentBlock2_8(EnterSubblock _enter) {
		super(_enter);
	}
	
	protected void verifyContents(BlockContents bc) {
		if (bc.isBlock())
			throw new IllegalArgumentException("METADATA_ATTACHMENT_BLOCK can have no subblocks");

		DataRecord record = bc.getDataRecordSelf();

		switch (record.getCode()) {
		case METADATA_ATTACHMENT:
		case METADATA_ATTACHMENT2:
			assertNumericRecord(record, 1, "METADATA_ATTACHMENT");
			if (record.getNumOps()%2 != 1)
				throw new IllegalArgumentException("METADATA_ATTACHMENT needs an odd number of numeric arguments");
			break;
		default:
			throw new IllegalArgumentException("Unknown data record: " + record);
		}
	}

	protected boolean verify(EnterSubblock _enter) {
		return _enter.getBlockID() == METADATA_ATTACHMENT_BLOCK_ID;
	}

	public boolean isMetadataAttachment2_8() {return true;}
	public MetadataAttachmentBlock2_8 getMetadataAttachment2_8Self() {return this;}
}
