package llvm.bitcode;

import java.util.ArrayList;
import java.util.List;

/**
 * This contains the value for a BlobOperand, which is simply an array of
 * uninterpreted bytes (stored as longs).
 */
public class BlobOperandValue extends OperandValue {
	protected final BlobOperand operand;
	protected final List<BasicOperandValue> values;
	
	public BlobOperandValue(BlobOperand _operand, List<? extends BasicOperandValue> _values) {
		this.operand = _operand;
		this.values = new ArrayList<BasicOperandValue>(_values);
		for (BasicOperandValue bov : this.values) {
			if (!bov.getOperand().equals(this.operand.getElementType()))
				throw new RuntimeException("Element type mismatch");
		}
	}

	public int getNumValues() {return this.values.size();}
	public BasicOperandValue getValue(int i) {return this.values.get(i);}

	public boolean isBlob() {return true;}
	public BlobOperandValue getBlobSelf() {return this;}

	public BlobOperand getOperand() {return this.operand;}
	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append("blob[");
		boolean gotone = false;
		for (BasicOperandValue bov : this.values) {
			if (gotone) buffer.append(", ");
			buffer.append(bov);
			gotone = true;
		}
		buffer.append(']');
		return buffer.toString();
	}
}
