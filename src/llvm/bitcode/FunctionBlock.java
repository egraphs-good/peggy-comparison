package llvm.bitcode;

/**
 * This class contains the blocks and records for a function block in an 
 * LLVM 2.3 bitcode module.
 */
public class FunctionBlock extends Block {
	/*
	 * This block defines a lovely new storage unit called the value/type pair.
	 * It is defined to be either 1 or 2 elements of a record. Either it will 
	 * be a value number, or it will be a value number followed by a type number.
	 * How do I know which one it is? Easy! Read the value number; if it is a 
	 * forward reference, then there will be a following type value. Otherwise,
	 * there won't be because we can just get the type from the value, which already 
	 * exists in the value table. 
	 */
	public static final int FUNCTION_BLOCK_ID = 12;
	///////////////////////////////////////////////
	public static final int FUNC_CODE_DECLAREBLOCKS 	= 1;
	public static final int FUNC_CODE_INST_BINOP 		= 2;
	public static final int FUNC_CODE_INST_CAST 		= 3;
	public static final int FUNC_CODE_INST_GEP 			= 4;
	public static final int FUNC_CODE_INST_SELECT 		= 5;
	public static final int FUNC_CODE_INST_EXTRACTELT 	= 6;
	public static final int FUNC_CODE_INST_INSERTELT 	= 7;
	public static final int FUNC_CODE_INST_SHUFFLEVEC 	= 8;
	public static final int FUNC_CODE_INST_CMP 			= 9;
	public static final int FUNC_CODE_INST_RET 			= 10;
	public static final int FUNC_CODE_INST_BR 			= 11;
	public static final int FUNC_CODE_INST_SWITCH 		= 12;
	public static final int FUNC_CODE_INST_INVOKE 		= 13;
	public static final int FUNC_CODE_INST_UNWIND 		= 14;
	public static final int FUNC_CODE_INST_UNREACHABLE 	= 15;
	public static final int FUNC_CODE_INST_PHI 			= 16;
	public static final int FUNC_CODE_INST_MALLOC 		= 17;
	public static final int FUNC_CODE_INST_FREE 		= 18;
	public static final int FUNC_CODE_INST_ALLOCA 		= 19;
	public static final int FUNC_CODE_INST_LOAD 		= 20;
	public static final int FUNC_CODE_INST_STORE 		= 21;
	public static final int FUNC_CODE_INST_CALL 		= 22;
	public static final int FUNC_CODE_INST_VAARG 		= 23;
	public static final int FUNC_CODE_INST_STORE2 		= 24;
	public static final int FUNC_CODE_INST_GETRESULT 	= 25;

	//////////////////////////////////////////////////////
	
	public FunctionBlock(EnterSubblock _enter) {
		super(_enter);
	}
	
	protected void verifyContents(BlockContents  bc) {
		if (bc.isBlock()) {
			Block block = bc.getBlockSelf();
			if (!(block.isValueSymtab() ||
				  block.isConstants()))
				throw new IllegalArgumentException("Invalid subblock for FUNCTION_BLOCK");
		} else {
			DataRecord record = bc.getDataRecordSelf();
			
			switch (record.getCode()) { 
			case FUNC_CODE_DECLAREBLOCKS:// [numblocks]
				assertNumericRecord(record, 1, "FUNC_CODE_DECLAREBLOCKS");
				break;
			case FUNC_CODE_INST_BINOP: // [val/ty,opval,opcode]
				assertNumericRecord(record, 3, "FUNC_CODE_INST_BINOP");
				break;
			case FUNC_CODE_INST_CAST: // [val/ty,destty,castopcode]
				assertNumericRecord(record, 3, "FUNC_CODE_INST_CAST");
				break;
			case FUNC_CODE_INST_GEP: // [val/ty, n x val/ty]
				assertNumericRecord(record, 1, "FUNC_CODE_INST_GEP");
				break;
			case FUNC_CODE_INST_SELECT: // [val/ty,opval,opval]
				assertNumericRecord(record, 3, "FUNC_CODE_INST_SELECT");
				break;
			case FUNC_CODE_INST_EXTRACTELT: // [val/ty,opval]
				assertNumericRecord(record, 2, "FUNC_CODE_INST_EXTRACTELT");
				break;
			case FUNC_CODE_INST_INSERTELT: // [val/ty,opval,opval]
				assertNumericRecord(record, 3, "FUNC_CODE_INST_INSERTELT");
				break;
			case FUNC_CODE_INST_SHUFFLEVEC: // [val/ty,opval,opval]
				assertNumericRecord(record, 3, "FUNC_CODE_INST_SHUFFLEVEC");
				break;
			case FUNC_CODE_INST_CMP: // [val/ty,opval,pred]
				assertNumericRecord(record, 3, "FUNC_CODE_INST_CMP");
				break;
			case FUNC_CODE_INST_RET: // [n x val/ty] (n>=0)
				assertNumericRecord(record, 0, "FUNC_CODE_INST_RET");
				break;
			case FUNC_CODE_INST_BR: // [bb#,bb#,opval] or [bb#]
				if (record.getNumOps()!=1 && record.getNumOps()!=3)
					throw new IllegalArgumentException("FUNC_CODE_INST_BR needs 1 or 3 numeric arguments");
				assertNumericRecord(record, 1, "FUNC_CODE_INST_BR");
				break;
			case FUNC_CODE_INST_SWITCH: // [ty,val,n,n x (val,bb#)]
				assertNumericRecord(record, 3, "FUNC_CODE_INST_SWITCH");
				if ((record.getNumOps()&1) == 0)
					throw new IllegalArgumentException("FUNC_CODE_INST_SWITCH needs odd number of arguments");
				break;
			case FUNC_CODE_INST_INVOKE: // [attrs,cc,normBB,unwindBB,val/ty,n x val]
				assertNumericRecord(record, 5, "FUNC_CODE_INST_INVOKE");
				break;
			case FUNC_CODE_INST_UNWIND: // []
				break;
			case FUNC_CODE_INST_UNREACHABLE: // []
				break;
			case FUNC_CODE_INST_PHI: // [ty, (val,bb#)]
				assertNumericRecord(record, 3, "FUNC_CODE_INST_PHI");
				if ((record.getNumOps()&1) == 0)
					throw new IllegalArgumentException("FUNC_CODE_INST_PHI needs odd number of arguments");
				break;
			case FUNC_CODE_INST_MALLOC: // [ty,op,align]
				assertNumericRecord(record, 3, "FUNC_CODE_INST_MALLOC");
				break;
			case FUNC_CODE_INST_FREE: // [val/ty]
				assertNumericRecord(record, 1, "FUNC_CODE_INST_FREE");
				break;
			case FUNC_CODE_INST_ALLOCA: // [ty,op,align]
				assertNumericRecord(record, 3, "FUNC_CODE_INST_ALLOCA");
				break;
			case FUNC_CODE_INST_LOAD: // [val/ty,align,vol]
				assertNumericRecord(record, 3, "FUNC_CODE_INST_LOAD");
				break;
			case FUNC_CODE_INST_STORE: // [val/ty,val,align,vol]
				assertNumericRecord(record, 4, "FUNC_CODE_INST_STORE");
				break;
			case FUNC_CODE_INST_CALL: // [paramattrs, cc, val/ty, n x (bb,val)]
				assertNumericRecord(record, 3, "FUNC_CODE_INST_CALL");
				break;
			case FUNC_CODE_INST_VAARG: // [ty,list,ty]
				assertNumericRecord(record, 3, "FUNC_CODE_INST_VAARG");
				break;
			case FUNC_CODE_INST_STORE2: // [val/ty,val,align,vol]
				assertNumericRecord(record, 4, "FUNC_CODE_INST_STORE2");
				break;
			case FUNC_CODE_INST_GETRESULT: // [val/ty,n]
				assertNumericRecord(record, 2, "FUNC_CODE_INST_GETRESULT");
				break;
			default:
				throw new IllegalArgumentException("Unknown data record: " + record);
			}
		}
	}

	protected boolean verify(EnterSubblock _enter) {
		return _enter.getBlockID() == FUNCTION_BLOCK_ID;
	}

	public boolean isFunction() {return true;}
	public FunctionBlock getFunctionSelf() {return this;}
}
