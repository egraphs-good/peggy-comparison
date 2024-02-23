package llvm.bitcode;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a basic unit of storage for bitcode. A block contains instances of
 * BlockContents, and blocks may be nested so Block implements BlockContents.
 * The contents are either other Blocks or DataRecords.
 */
public abstract class Block implements BlockContents {
	public final boolean isBlock() {return true;}
	public final Block getBlockSelf() {return this;}
	public final boolean isDataRecord() {return false;}
	public final DataRecord getDataRecordSelf() {throw new UnsupportedOperationException();}
	
	private final EnterSubblock enterSubblockRecord;
	private final List<BlockContents> contents;

	protected Block(EnterSubblock _enter) {
		if (!this.verify(_enter))
			throw new IllegalArgumentException("Invalid EnterSubblock block");
		this.enterSubblockRecord = _enter;
		this.contents = new ArrayList<BlockContents>();
	}

	/**
	 * This method should do minimal checking of 'contents' to
	 * make sure that it is valid, otherwise throw an IllegalArgumentException.
	 */
	public final void addContents(BlockContents contents) {
		verifyContents(contents);
		this.contents.add(contents);
	}
	protected abstract void verifyContents(BlockContents contents);
	public final int getNumBlockContents() {return this.contents.size();}
	public final BlockContents getBlockContents(int i) {return this.contents.get(i);}
	
	protected abstract boolean verify(EnterSubblock _enter);

	public final EnterSubblock getEnterSubblock() {return this.enterSubblockRecord;}
	public final int getBlockID() {return this.enterSubblockRecord.getBlockID();}

	public boolean isModule() {return false;}
	public ModuleBlock getModuleSelf() {throw new UnsupportedOperationException();}
	public boolean isModule2_8() {return false;}
	public ModuleBlock2_8 getModule2_8Self() {throw new UnsupportedOperationException();}

	public boolean isParamAttr() {return false;}
	public ParamAttrBlock getParamAttrSelf() {throw new UnsupportedOperationException();}

	public boolean isType() {return false;}
	public TypeBlock getTypeSelf() {throw new UnsupportedOperationException();}
	public boolean isType2_8() {return false;}
	public TypeBlock2_8 getType2_8Self() {throw new UnsupportedOperationException();}

	public boolean isConstants() {return false;}
	public ConstantsBlock getConstantsSelf() {throw new UnsupportedOperationException();}
	public boolean isConstants2_8() {return false;}
	public ConstantsBlock2_8 getConstants2_8Self() {throw new UnsupportedOperationException();}

	public boolean isFunction() {return false;}
	public FunctionBlock getFunctionSelf() {throw new UnsupportedOperationException();}
	public boolean isFunction2_8() {return false;}
	public FunctionBlock2_8 getFunction2_8Self() {throw new UnsupportedOperationException();}

	public boolean isTypeSymtab() {return false;}
	public TypeSymtabBlock getTypeSymtabSelf() {throw new UnsupportedOperationException();}

	public boolean isValueSymtab() {return false;}
	public ValueSymtabBlock getValueSymtabSelf() {throw new UnsupportedOperationException();}
	
	public boolean isMetadataAttachment2_8() {return false;}
	public MetadataAttachmentBlock2_8 getMetadataAttachment2_8Self() {throw new UnsupportedOperationException();}
	
	public boolean isMetadata2_8() {return false;}
	public MetadataBlock2_8 getMetadata2_8Self() {throw new UnsupportedOperationException();}

	protected void assertNumericRecord(DataRecord record, int minargs, String name) {
		if (record.getNumOps() < minargs)
			throw new IllegalArgumentException(name + " record needs at least " + minargs + " arguments: " + record);
		for (int i = 0; i < record.getNumOps(); i++) {
			if (!record.getOp(i).isNumeric())
				throw new IllegalArgumentException(name + " record needs numeric arguments: " + record);
		}
	}
}
