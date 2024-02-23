package llvm.bitcode;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an unabbreviated data record, which is a record
 * that has all VBR6's as its values.
 */
public final class UnabbrevRecord extends Record implements DataRecord {
	public static final int UNABBREV_RECORD_ID = 3;
	private static final VBROperand vbr6 = new VBROperand(6);

	protected final int code;
	protected final List<OperandValue> ops;

	public UnabbrevRecord(int _code, long... _ops) {
		this.code = _code;
		this.ops = new ArrayList<OperandValue>(_ops.length);
		for (long l : _ops) {
			this.ops.add(new BasicOperandValue(vbr6, l));
		}
	}
	
	public UnabbrevRecord(int _code, List<Long> _ops) {
		this.code = _code;
		this.ops = new ArrayList<OperandValue>(_ops.size());

		for (long l : _ops) {
			this.ops.add(new BasicOperandValue(vbr6, l));
		}
	}
	
	public final boolean isDataRecord() {return true;}
	public final DataRecord getDataRecordSelf() {return this;}
	public final boolean isBlock() {return false;}
	public final Block getBlockSelf() {throw new UnsupportedOperationException();}

	public int getCode() {return this.code;}
	public int getNumOps() {return this.ops.size();}
	public OperandValue getOp(int i) {return this.ops.get(i);}
	public int getNumUnflattenedOps() {return this.getNumOps();}
	public OperandValue getUnflattenedOp(int i) {return this.getOp(i);}

	public int getAbbreviationID() {return UNABBREV_RECORD_ID;}
	public boolean isUnabbrevRecord() {return true;}
	public UnabbrevRecord getUnabbrevRecordSelf() {return this;}

	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append("[UNABBREV_RECORD, code(vbr6)=");
		buffer.append(this.code);
		buffer.append(", numops(vbr6)=");
		buffer.append(this.ops.size());

		for (int i = 0; i < this.ops.size(); i++) {
			buffer.append(", op");
			buffer.append(i);
			buffer.append("=");
			buffer.append(this.ops.get(i));
		}
		buffer.append(']');

		return buffer.toString();
	}
	public String toUnflattenedString() {return this.toString();}
}
