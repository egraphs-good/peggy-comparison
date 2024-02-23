package llvm.bitcode;

/**
 * This class contains the blocks and records for the type block from
 * an LLVM 2.3 bitcode file.
 */
public class TypeBlock extends Block {
	public static final int TYPE_BLOCK_ID = 10;
	////////////////////////////////////////////
	public static final int TYPE_CODE_NUMENTRY 		= 1;
	public static final int FIRST_TYPE_CODE			= 2;
	public static final int TYPE_CODE_VOID			= 2;	// VOID
	public static final int TYPE_CODE_FLOAT			= 3;	// FLOAT
	public static final int TYPE_CODE_DOUBLE		= 4;	// DOUBLE
	public static final int TYPE_CODE_LABEL			= 5;	// LABEL
	public static final int TYPE_CODE_OPAQUE		= 6;	// OPAQUE
	public static final int TYPE_CODE_INTEGER		= 7;	// INTEGER: [width]
	public static final int TYPE_CODE_POINTER		= 8;	// POINTER: [pointee type]
	public static final int TYPE_CODE_FUNCTION		= 9;	// FUNCTION: [vararg, retty, paramty x N]
	public static final int TYPE_CODE_STRUCT		= 10;	// STRUCT: [ispacked, eltty x N]
	public static final int TYPE_CODE_ARRAY			= 11;	// ARRAY: [numelts, eltty]
	public static final int TYPE_CODE_VECTOR		= 12;	// VECTOR: [numelts, eltty]
	public static final int TYPE_CODE_X86_FP80		= 13;	// X86 LONG DOUBLE
	public static final int TYPE_CODE_FP128 		= 14;	// LONG DOUBLE (112 bit mantissa)
	public static final int TYPE_CODE_PPC_FP128		= 15;	// PPC LONG DOUBLE (2 doubles)
	public static final int LAST_TYPE_CODE			= 15;
	
	public TypeBlock(EnterSubblock _enter) {
		super(_enter);
	}

	protected boolean verify(EnterSubblock _enter) {
		return _enter.getBlockID() == TYPE_BLOCK_ID;
	}
	
	protected void verifyContents(BlockContents bc) {
		if (bc.isBlock())
			throw new IllegalArgumentException("No subblocks allowed for TypeBlock");
		
		DataRecord record = bc.getDataRecordSelf();
		
		switch (record.getCode()) {
		case TYPE_CODE_NUMENTRY:
			assertNumericRecord(record, 1, "TYPE_CODE_NUMENTRY");
			break;
		case TYPE_CODE_VOID:
		case TYPE_CODE_FLOAT:
		case TYPE_CODE_DOUBLE:
		case TYPE_CODE_LABEL:
		case TYPE_CODE_OPAQUE:
		case TYPE_CODE_X86_FP80:
		case TYPE_CODE_FP128:
		case TYPE_CODE_PPC_FP128:
			break;
		case TYPE_CODE_INTEGER:
			assertNumericRecord(record, 1, "TYPE_CODE_INTEGER");
			break;
		case TYPE_CODE_POINTER:
			assertNumericRecord(record, 1, "TYPE_CODE_POINTER");
			break;
		case TYPE_CODE_FUNCTION:
			assertNumericRecord(record, 3, "TYPE_CODE_FUNCTION");
			break;
		case TYPE_CODE_STRUCT:
			assertNumericRecord(record, 1, "TYPE_CODE_STRUCT");
			break;
		case TYPE_CODE_ARRAY:
			assertNumericRecord(record, 2, "TYPE_CODE_ARRAY");
			break;
		case TYPE_CODE_VECTOR:
			assertNumericRecord(record, 2, "TYPE_CODE_VECTOR");
			break;
		default: // invalid!
			throw new IllegalArgumentException("Invalid record code for typecode");
		}
	}
	
	public boolean isType() {return true;}
	public TypeBlock getTypeSelf() {return this;}
}
