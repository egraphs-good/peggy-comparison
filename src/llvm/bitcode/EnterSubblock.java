package llvm.bitcode;

/**
 * This class holds information about the ENTER_SUBBLOCK record that is
 * encountered when reading/writing an LLVM bitcode file. This record
 * appears at the start of every block and encodes information about the
 * block type, block length, and the length of abbreviated ids.
 */
public final class EnterSubblock extends Record {
	public static final int ENTER_SUBBLOCK_ID = 1;

	protected final int blockid;
	protected final int newabbrevlen;
	protected final int blocklen; // this will include the END_BLOCK record

	public EnterSubblock(int _blockid, int _newabbrevlen, int _blocklen) {
		this.blockid = _blockid;
		this.newabbrevlen = _newabbrevlen;
		this.blocklen = _blocklen;
	}
	public int getBlockID() {return this.blockid;}
	public int getNewAbbrevLen() {return this.newabbrevlen;}
	public int getBlockLen() {return this.blocklen;}

	public int getAbbreviationID() {return ENTER_SUBBLOCK_ID;}
	public boolean isEnterSubblock() {return true;}
	public EnterSubblock getEnterSubblockSelf() {return this;}

	public String toString() {
		return "[ENTER_SUBBLOCK, blockid(vbr8)=" + this.blockid + ", newabbrevlen(vbr4)=" + this.newabbrevlen + ", <align32bits>, blocklen(32)=" + this.blocklen + "]"; 
	}
}
