package llvm.bitcode;

/**
 * This class contains the value of an operand that has a BasicOperand type.
 * This will always be encoded as one long integer.
 */
public class BasicOperandValue extends OperandValue {
	protected final BasicOperand operand;
	protected final long value;

	public BasicOperandValue(BasicOperand _operand, long _value) {
		this.operand = _operand;
		this.value = _value;
	}

	public BasicOperand getOperand() {return this.operand;}
	public long getNumericValue() {
		if (this.operand.isChar6())
			return 0xFFFFL & Char6Operand.indexToChar6((int)this.value);
		else
			return this.value;
	}

	public boolean isBasic() {return true;}
	public BasicOperandValue getBasicSelf() {return this;}

	public String toString() {
		if (this.operand.isChar6()) {
			return this.operand.toString() + "=\'" + Char6Operand.indexToChar6((int)this.value) + "\'";
		} else {
			return this.operand.toString() + "=" + this.value;
		}
	}
}
