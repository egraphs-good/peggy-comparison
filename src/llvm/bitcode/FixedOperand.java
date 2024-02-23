package llvm.bitcode;

/**
 * This operand type defines an operand with a fixed bit width.
 */
public final class FixedOperand extends BasicOperand {
	protected final long width;

	public FixedOperand(long _width) {
		this.width = _width;
	}

	public long getWidth() {return this.width;}

	public final boolean isFixed() {return true;}
	public final FixedOperand getFixedSelf() {return this;}
	
	public boolean isNumeric() {return true;}

	public String toString() {
		return "Fixed(" + this.width + ")";
	}
	public boolean equals(Object o) {
		if (o == null || !(o instanceof FixedOperand))
			return false;
		FixedOperand f = (FixedOperand)o;
		return this.width == f.width;
	}
	public int hashCode() {
		return 11*(int)this.width;
	}
}
