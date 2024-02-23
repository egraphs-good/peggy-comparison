package llvm.bitcode;

import java.util.*;

/**
 * This is a data record that is specially designed to encode information
 * in an abbreviated (i.e. compressed) form. For instance, if it is known that
 * a given field will never exceed 5 bits, then a full long word is not needed
 * and so a smaller operand type could be used instead. Only Abbreviated Records
 * can use Operand types other than VBR6.
 */
public class AbbreviatedRecord extends Record implements DataRecord {
	protected final List<OperandValue> ops;
	protected final DefineAbbrev definition;
	protected final int abbrevid;

	public AbbreviatedRecord(int _abbrevid, DefineAbbrev _definition, List<OperandValue> _ops) {
		this.abbrevid = _abbrevid;
		this.definition = _definition;
		this.ops = new ArrayList<OperandValue>(_ops);
		if (this.definition.getNumAbbrevOps() != this.ops.size())
			throw new RuntimeException("Operand definition mismatch");
		if (this.ops.size() < 1 || !(this.ops.get(0) instanceof BasicOperandValue))
			throw new RuntimeException("No code value");
		for (int i = 0; i < this.ops.size(); i++) {
			if (!this.definition.getAbbrevOp(i).equals(this.ops.get(i).getOperand()))
				throw new RuntimeException("Operand type mismatch");
		}
	}

	public final boolean isDataRecord() {return true;}
	public final DataRecord getDataRecordSelf() {return this;}
	public final boolean isBlock() {return false;}
	public final Block getBlockSelf() {throw new UnsupportedOperationException();}

	public DefineAbbrev getDefinition() {return this.definition;}
	public int getNumOps() {
		int numOps = 0;
		for (int i = 1; i < this.ops.size(); i++) {
			if (this.ops.get(i).isBasic())
				numOps++;
			else if (this.ops.get(i).isBlob())
				numOps += this.ops.get(i).getBlobSelf().getNumValues();
			else 
				numOps += this.ops.get(i).getArraySelf().getNumValues();
		}
		return numOps;
	}
	public OperandValue getOp(int i) {
		int passed = 0;
		for (int j = 1; j < this.ops.size(); j++) {
			final OperandValue v = this.ops.get(j);
			if (v.isBasic()) {
				if (passed == i)
					return v;
				else
					passed++;
			} else if (v.isBlob()) {
				if (i < passed + v.getBlobSelf().getNumValues())
					return v.getBlobSelf().getValue(i - passed);
				else
					passed += v.getBlobSelf().getNumValues();
			} else {
				// array
				if (i < passed + v.getArraySelf().getNumValues())
					return v.getArraySelf().getValue(i - passed);
				else
					passed += v.getArraySelf().getNumValues();
			}
		}
		throw new IllegalArgumentException();
	}
	public int getNumUnflattenedOps() {return this.ops.size()-1;}
	public OperandValue getUnflattenedOp(int i) {return this.ops.get(i+1);}
	public int getCode() {return (int)this.ops.get(0).getNumericValue();}

	public int getAbbreviationID() {return this.abbrevid;}
	public boolean isAbbreviated() {return true;}
	public AbbreviatedRecord getAbbreviatedSelf() {return this;}

	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append("[abbrevid=");
		buffer.append(this.abbrevid);
		buffer.append(", code=");
		buffer.append(this.ops.get(0));
		for (int i = 0; i < this.getNumOps(); i++) {
			buffer.append(", op");
			buffer.append(i);
			buffer.append('=');
			buffer.append(this.getOp(i));
		}
		buffer.append(']');

		return buffer.toString();
	}
	
	public String toUnflattenedString() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append("[code=");
		buffer.append(this.ops.get(0));
		for (int i = 0; i < this.getNumUnflattenedOps(); i++) {
			buffer.append(", op");
			buffer.append(i);
			buffer.append('=');
			buffer.append(this.getUnflattenedOp(i));
		}
		buffer.append(']');

		return buffer.toString();
	}
}
