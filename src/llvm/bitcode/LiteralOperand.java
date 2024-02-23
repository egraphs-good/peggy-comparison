package llvm.bitcode;

/**
 * This operand type defines an operand that is completely defined by its defineabbrev.
 * A literal operand value will not appear in the data stream for a record 
 * value, but will have as its value the same value that is defined in this 
 * operand type.
 */
public class LiteralOperand extends BasicOperand {
	protected final long value;

	public LiteralOperand(long _value) {
		this.value = _value;
	}

	public long getValue() {return this.value;}

	public final boolean isLiteral() {return true;}
	public final LiteralOperand getLiteralSelf() {return this;}
	
	public boolean isNumeric() {return true;}

	public String toString() {
		return "Literal(" + this.value + ")";
	}
	public boolean equals(Object o) {
		if (o == null || !(o instanceof LiteralOperand))
			return false;
		LiteralOperand l = (LiteralOperand)o;
		return this.value == l.value;
	}
	public int hashCode() {
		return 13*(int)this.value;
	}
}
