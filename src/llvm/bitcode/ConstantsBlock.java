package llvm.bitcode;

/**
 * This class contains blocks and records defining LLVM 2.3 constants.
 * This includes global constants and function-local constants.
 */
public class ConstantsBlock extends Block {
	public static final int CONSTANTS_BLOCK_ID = 11;
	///////////////////////////////////////////////
	public static final int CST_CODE_SETTYPE 		= 1;
	public static final int CST_CODE_NULL 			= 2;
	public static final int CST_CODE_UNDEF 			= 3;
	public static final int CST_CODE_INTEGER 		= 4;
	public static final int CST_CODE_WIDE_INTEGER	= 5;	
	public static final int CST_CODE_FLOAT 			= 6;
	public static final int CST_CODE_AGGREGATE 		= 7;
	public static final int CST_CODE_STRING 		= 8;
	public static final int CST_CODE_CSTRING 		= 9;
	public static final int CST_CODE_CE_BINOP 		= 10;
	public static final int CST_CODE_CE_CAST 		= 11;
	public static final int CST_CODE_CE_GEP 		= 12;
	public static final int CST_CODE_CE_SELECT 		= 13;
	public static final int CST_CODE_CE_EXTRACTELT 	= 14;
	public static final int CST_CODE_CE_INSERTELT 	= 15;
	public static final int CST_CODE_CE_SHUFFLEVEC 	= 16;
	public static final int CST_CODE_CE_CMP 		= 17;
	public static final int CST_CODE_INLINEASM 		= 18;

	public ConstantsBlock(EnterSubblock _enter) {
		super(_enter);
	}

	protected void verifyContents(BlockContents bc) {
		if (bc.isBlock())
			throw new IllegalArgumentException("CONSTANTS_BLOCK can have no subblocks");
		
		DataRecord record = bc.getDataRecordSelf();

		switch (record.getCode()) {
		case ConstantsBlock.CST_CODE_SETTYPE: {
			assertNumericRecord(record, 1, "CST_CODE_SETTYPE");
			break;
		}
		case ConstantsBlock.CST_CODE_NULL:
		case ConstantsBlock.CST_CODE_UNDEF:
			break;
			
		case ConstantsBlock.CST_CODE_INTEGER:
			assertNumericRecord(record, 1, "CST_CODE_INTEGER");
			break;
		case ConstantsBlock.CST_CODE_WIDE_INTEGER:	
			assertNumericRecord(record, 1, "CST_CODE_WIDE_INTEGER");
			break;
		case ConstantsBlock.CST_CODE_FLOAT:
			assertNumericRecord(record, 1, "CST_CODE_FLOAT");
			break;
		case ConstantsBlock.CST_CODE_AGGREGATE:
			assertNumericRecord(record, 1, "CST_CODE_AGGREGATE");
			break;
		case ConstantsBlock.CST_CODE_STRING:
		case ConstantsBlock.CST_CODE_CSTRING:
			break;
		case ConstantsBlock.CST_CODE_CE_BINOP:
			assertNumericRecord(record, 3, "CST_CODE_CE_BINOP");
			break;
		case ConstantsBlock.CST_CODE_CE_CAST:
			assertNumericRecord(record, 3, "CST_CODE_CE_CAST");
			break;
		case ConstantsBlock.CST_CODE_CE_GEP:
			assertNumericRecord(record, 2, "CST_CODE_CE_GEP");
			if ((record.getNumOps()&1) != 0)
				throw new IllegalArgumentException("CST_CODE_CE_GEP record needs even number of arguments");
			break;
		case ConstantsBlock.CST_CODE_CE_SELECT:
			assertNumericRecord(record, 3, "CST_CODE_CE_SELECT");
			break;
		case ConstantsBlock.CST_CODE_CE_EXTRACTELT:
			assertNumericRecord(record, 3, "CST_CODE_CE_EXTRACTELT");
			break;
		case ConstantsBlock.CST_CODE_CE_INSERTELT:
			assertNumericRecord(record, 3, "CST_CODE_CE_INSERTELT");
			break;
		case ConstantsBlock.CST_CODE_CE_SHUFFLEVEC:
			assertNumericRecord(record, 3, "CST_CODE_CE_SHUFFLEVEC");
			break;
		case ConstantsBlock.CST_CODE_CE_CMP:
			assertNumericRecord(record, 4, "CST_CODE_CE_CMP");
			break;
		case ConstantsBlock.CST_CODE_INLINEASM: { 
			// [hasSideEffects,asmSize,asmSize x char,constSize,constSize x char]
			if (record.getNumOps() < 3)
				throw new IllegalArgumentException("CST_CODE_INLINEASM record needs at least 3 arguments");
			if (!(record.getOp(0).isNumeric() && record.getOp(1).isNumeric()))
				throw new IllegalArgumentException("CST_CODE_INLINEASM record needs first 2 numeric arguments");
			long asmSize = record.getOp(1).getNumericValue();
			if (asmSize < 0 || asmSize+3 > record.getNumOps())
				throw new IllegalArgumentException("CST_CODE_INLINEASM record has invalid asmSize argument");
			OperandValue constSizeValue = record.getOp((int)(asmSize)+2);
			if (!constSizeValue.isNumeric())
				throw new IllegalArgumentException("CST_CODE_INLINEASM record has invalid constSize argument");
			long constSize = constSizeValue.getNumericValue();
			if (constSize < 0 || 3+asmSize+constSize > record.getNumOps())
				throw new IllegalArgumentException("CST_CODE_INLINEASM record has invalid constSize argument");
			break;
		}
		default:
			throw new IllegalArgumentException("Unknown data record: " + record);
		}
	}
	
	protected boolean verify(EnterSubblock _enter) {
		return _enter.getBlockID() == CONSTANTS_BLOCK_ID;
	}

	public boolean isConstants() {return true;}
	public ConstantsBlock getConstantsSelf() {return this;}
}
