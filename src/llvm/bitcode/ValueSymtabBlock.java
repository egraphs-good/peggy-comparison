package llvm.bitcode;

/**
 * This class contains the blocks and records for the value symbol table
 * block from an LLVM (2.3 or 2.8) bitcode file.
 */
public class ValueSymtabBlock extends Block {
	public static final int VALUE_SYMTAB_BLOCK_ID = 14;
	public static final int VST_CODE_ENTRY = 1;
	public static final int VST_CODE_BBENTRY = 2;
	
	public ValueSymtabBlock(EnterSubblock _enter) {
		super(_enter);
	}
	
	protected void verifyContents(BlockContents bc) {
		if (bc.isBlock())
			throw new IllegalArgumentException("VALUESYMTAB_BLOCK can have no subblocks");
		
		DataRecord record = bc.getDataRecordSelf();
		switch (record.getCode()) {
		case VST_CODE_ENTRY:
		case VST_CODE_BBENTRY: {
			if (record.getNumOps() < 2)
				throw new IllegalArgumentException("VST_CODE_*ENTRY needs at least 2 arguments");
			if (!record.getOp(0).isNumeric())
				throw new IllegalArgumentException("VST_CODE_*ENTRY needs numeric first argument");
			break;
		}
		default:
			throw new IllegalArgumentException("Unknown data record: " + record);
		}
	}

	protected boolean verify(EnterSubblock _enter) {
		return _enter.getBlockID() == VALUE_SYMTAB_BLOCK_ID;
	}

	public boolean isValueSymtab() {return true;}
	public ValueSymtabBlock getValueSymtabSelf() {return this;}
}
