package llvm.bitcode;

/**
 * This class contains the blocks and records for the type symbol table
 * in an LLVM (2.3 or 2.8) bitcode file.
 */
public class TypeSymtabBlock extends Block {
	public static final int TYPE_SYMTAB_BLOCK_ID = 13;
	public static final int TST_CODE_ENTRY = 1;
	
	public TypeSymtabBlock(EnterSubblock _enter) {
		super(_enter);
	}

	protected void verifyContents(BlockContents bc) {
		if (bc.isBlock())
			throw new IllegalArgumentException("TYPESYMTAB_BLOCK can have no subblocks");
		
		DataRecord record = bc.getDataRecordSelf();
		
		if (record.getCode() != TST_CODE_ENTRY)
			throw new IllegalArgumentException("Unknown data record: " + record);
		if (record.getNumOps() < 2)
			throw new IllegalArgumentException("TST_CODE_ENTRY record needs at least 2 arguments");
		OperandValue v0 = record.getOp(0);
		if (!v0.isNumeric())
			throw new IllegalArgumentException("TST_CODE_ENTRY record needs first numeric argument");
	}
	
	protected boolean verify(EnterSubblock _enter) {
		return _enter.getBlockID() == TYPE_SYMTAB_BLOCK_ID;
	}

	public boolean isTypeSymtab() {return true;}
	public TypeSymtabBlock getTypeSymtabSelf() {return this;}
}
