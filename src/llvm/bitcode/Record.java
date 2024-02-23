package llvm.bitcode;

/**
 * This is the parent type of all records within an LLVM bitcode file.
 * This includes data records, as well as the {ENTER,END}_BLOCK records,
 * and the DEFINEABBREV records.
 */
public abstract class Record {
	public abstract int getAbbreviationID();

	public boolean isEnterSubblock() {return false;}
	public EnterSubblock getEnterSubblockSelf() {throw new UnsupportedOperationException();}

	public boolean isEndBlock() {return false;}
	public EndBlock getEndBlockSelf() {throw new UnsupportedOperationException();}

	public boolean isDefineAbbrev() {return false;}
	public DefineAbbrev getDefineAbbrevSelf() {throw new UnsupportedOperationException();}

	public boolean isUnabbrevRecord() {return false;}
	public UnabbrevRecord getUnabbrevRecordSelf() {throw new UnsupportedOperationException();}

	// Means that this record was defined by a DEFINE_ABBREV
	public boolean isAbbreviated() {return false;}
	public AbbreviatedRecord getAbbreviatedSelf() {throw new UnsupportedOperationException();}

	public abstract String toString();
}
