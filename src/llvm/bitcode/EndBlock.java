package llvm.bitcode;

/**
 * This class represents the END_BLOCK record that appears in an LLVM bitcode file.
 * This record appears at the end of every block.
 */
public final class EndBlock extends Record {
	public static final int END_BLOCK_ID = 0;
	public static final EndBlock INSTANCE = new EndBlock();

	private EndBlock() {}
	public int getAbbreviationID() {return END_BLOCK_ID;}
	public boolean isEndBlock() {return true;}
	public EndBlock getEndBlockSelf() {return this;}

	public String toString() {
		return "[END_BLOCK, <align32bits>]";
	}
}
