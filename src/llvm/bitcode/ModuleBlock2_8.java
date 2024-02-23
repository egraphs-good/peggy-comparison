package llvm.bitcode;

import llvm.bitcode.DataRecord;
import llvm.bitcode.EnterSubblock;

/**
 * This class contains the blocks and records for a module block
 * from an LLVM2.8 bitcode file.
 */
public class ModuleBlock2_8 extends Block {
	/*
	 * Ordering of Values in the global ValueList:
	 * 
	 * 1) top-level values, defined as immediate children of the Module
	 *    these include:
	 *    - ConstantsBlocks values
	 *    - MODULE_CODE_FUNCTION decls (function headers)
	 *    - MODULE_CODE_GLOBALVAR decls
	 *    - MODULE_CODE_ALIAS decls
	 *    
	 * 2) values contained in Function bodies, in order: (these are temporary, and are removed after finished processing the function body)
	 * 	  - all function arguments
	 * 	  - all instructions
	 * 
	 * MODULE_CODE_PURGEVALS can resize the global value list, which is annoying
	 * 
	 * @author steppm
	 */
	public static final int MODULE_BLOCK_ID = 8;
	public static final int MODULE_CODE_VERSION 		= 1;
	public static final int MODULE_CODE_TRIPLE 			= 2;
	public static final int MODULE_CODE_DATALAYOUT 		= 3;
	public static final int MODULE_CODE_ASM 			= 4;
	public static final int MODULE_CODE_SECTIONNAME 	= 5;
	public static final int MODULE_CODE_DEPLIB 			= 6;
	public static final int MODULE_CODE_GLOBALVAR 		= 7;
	public static final int MODULE_CODE_FUNCTION 		= 8;
	public static final int MODULE_CODE_ALIAS 			= 9;
	public static final int MODULE_CODE_PURGEVALS 		= 10;
	public static final int MODULE_CODE_COLLECTORNAME 	= 11;
	
	///////////////////////////////////////////
	
	public ModuleBlock2_8(EnterSubblock _enter) {
		super(_enter);
	}

	protected boolean verify(EnterSubblock _enter) {
		return _enter.getBlockID() == MODULE_BLOCK_ID;
	}

	protected void verifyContents(BlockContents bc) {
		if (bc.isBlock()) {
			Block block = bc.getBlockSelf();
			if (!(block.isParamAttr() ||
				  block.isType2_8() ||
				  block.isTypeSymtab() ||
				  block.isValueSymtab() ||
				  block.isConstants2_8() ||
				  block.isFunction2_8() ||
				  block.isMetadata2_8())) {
				throw new IllegalArgumentException("Unknown block type for Module: " + block.getBlockID());
			}
		} else {
			DataRecord record = bc.getDataRecordSelf();
			switch (record.getCode()) {
			case MODULE_CODE_VERSION: {
				// [version#]
				assertNumericRecord(record, 1, "MODULE_CODE_VERSION");
				break;
			}
			case MODULE_CODE_TRIPLE: 
			case MODULE_CODE_DATALAYOUT:
			case MODULE_CODE_ASM:
			case MODULE_CODE_SECTIONNAME:
			case MODULE_CODE_DEPLIB:
			case MODULE_CODE_COLLECTORNAME: {
				// [strchr x N]
				break;
			}
			case MODULE_CODE_GLOBALVAR: {
				assertNumericRecord(record, 6, "MODULE_CODE_GLOBALVAR");
				break;
			}
			case MODULE_CODE_FUNCTION: {
				assertNumericRecord(record, 8, "MODULE_CODE_FUNCTION");
				break;
			}
			case MODULE_CODE_ALIAS: {
				assertNumericRecord(record, 3, "MODULE_CODE_ALIAS");
				break;
			}
			case MODULE_CODE_PURGEVALS: {
				assertNumericRecord(record, 1, "MODULE_CODE_PURGEVALS");
				break;
			}
			default: {
				// unknown
				throw new IllegalArgumentException("Unknown data record: " + record);
			}
			}
		}
	}
	
	public boolean isModule2_8() {return true;}
	public ModuleBlock2_8 getModule2_8Self() {return this;}
}
