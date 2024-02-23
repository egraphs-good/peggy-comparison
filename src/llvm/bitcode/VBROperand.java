package llvm.bitcode;

/**
 * This operand type describes an operand value that is encoded in units of N
 * bits at a time.
 */
public final class VBROperand extends BasicOperand {
	protected final long width;

	public VBROperand(long _width) {
		this.width = _width;
	}

	public long getWidth() {return this.width;}

	public final boolean isVBR() {return true;}
	public final VBROperand getVBRSelf() {return this;}
	
	public boolean isNumeric() {return true;}

	public String toString() {
		return "VBR(" + this.width + ")";
	}
	public boolean equals(Object o) {
		if (o == null || !(o instanceof VBROperand))
			return false;
		VBROperand v = (VBROperand)o;
		return this.width == v.width;
	}
	public int hashCode() {
		return 17*(int)this.width;
	}
}
