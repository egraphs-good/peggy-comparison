package llvm.bitcode;

import java.util.*;

/**
 * This contains the value of an ArrayOperand, which is
 * an array of BasicOperand values.
 */
public class ArrayOperandValue extends OperandValue {
	protected final ArrayOperand operand;
	protected final List<BasicOperandValue> values;

	public ArrayOperandValue(ArrayOperand _operand, List<? extends BasicOperandValue> _values) {
		this.operand = _operand;
		this.values = new ArrayList<BasicOperandValue>(_values);
		for (BasicOperandValue bov : this.values) {
			if (!bov.getOperand().equals(this.operand.getElementType()))
				throw new RuntimeException("Element type mismatch");
		}
	}

	public int getNumValues() {return this.values.size();}
	public BasicOperandValue getValue(int i) {return this.values.get(i);}

	public boolean isArray() {return true;}
	public ArrayOperandValue getArraySelf() {return this;}

	public ArrayOperand getOperand() {return this.operand;}
	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append('[');
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
