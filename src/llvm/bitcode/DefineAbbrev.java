package llvm.bitcode;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains information about how a particular abbreviated record type
 * will be encoded. A DefineAbbrev will be contained in every AbbrevRecord, to
 * specify how its operands are to be interpreted.
 */
public final class DefineAbbrev extends Record {
	public static final int DEFINE_ABBREV_ID = 2;
	//////////////////////////////////////////
	public static final int ENCODING_FIXED	= 1;
	public static final int ENCODING_VBR   	= 2;
	public static final int ENCODING_ARRAY 	= 3;
	public static final int ENCODING_CHAR6 	= 4;
	public static final int ENCODING_BLOB   = 5;

	protected final List<? extends Operand> abbrevops;

	public DefineAbbrev(List<? extends Operand> _abbrevops) {
		if (_abbrevops.size() < 1)
			throw new IllegalArgumentException("must have at least 1 operand");
		this.abbrevops = new ArrayList<Operand>(_abbrevops);
		for (int i = 0; i < this.abbrevops.size()-1; i++) {
			if (this.abbrevops.get(i).isArray())
				throw new IllegalArgumentException("Only last operand can be an array");
		}
	}

	public final int getNumAbbrevOps() {return this.abbrevops.size();}
	public final Operand getAbbrevOp(int i) {return this.abbrevops.get(i);}

	public int getAbbreviationID() {return DEFINE_ABBREV_ID;}
	public boolean isDefineAbbrev() {return true;}
	public DefineAbbrev getDefineAbbrevSelf() {return this;}

	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append("[DEFINE_ABBREV, numabbrevops(vbr5)=");
		buffer.append(this.abbrevops.size());

		for (int i = 0; i < this.abbrevops.size(); i++) {
			buffer.append(", abbrevop");
			buffer.append(i);
			buffer.append("=");
			buffer.append(this.abbrevops.get(i));
		}
		buffer.append(']');

		return buffer.toString();
	}
}
