package llvm.bitcode;

/** 
 * The ParamAttrBlock stores information about all of the function
 * parameters for all the functions defined in the module. There will be 
 * only 1 ParamAttrBlock defined in the Module. The parameters are named by
 * global index (?) and the attributes are bit vectors where the bits are defined as
 * in the ParameterAttributes enum.
 * 
 * @author steppm
 */
public class ParamAttrBlock extends Block {
	public static final int PARAMATTR_BLOCK_ID = 9;
	public static final int PARAMATTR_CODE_ENTRY = 1;
	/////////////////////////////////////////
	public ParamAttrBlock(EnterSubblock _enter) {
		super(_enter);
	}

	protected boolean verify(EnterSubblock _enter) {
		return _enter.getBlockID() == PARAMATTR_BLOCK_ID;
	}

	protected void verifyContents(BlockContents bc) {
		if (bc.isBlock())
			throw new IllegalArgumentException("PARAMATTR_BLOCK can have no subblocks");
		
		// [n x (index,attrbits)]
		// index=0 means the function itself
		DataRecord record = bc.getDataRecordSelf();
		if ((record.getNumOps()&1) != 0)
			throw new IllegalArgumentException("PARAMATTR_CODE_ENTRY record needs even number of arguments");
		for (int i = 0; i < record.getNumOps(); i++) {
			if (!record.getOp(i).isNumeric())
				throw new IllegalArgumentException("PARAMATTR_CODE_ENTRY needs numeric arguments");
		}
	}

	public boolean isParamAttr() {return true;}
	public ParamAttrBlock getParamAttrSelf() {return this;}
}
