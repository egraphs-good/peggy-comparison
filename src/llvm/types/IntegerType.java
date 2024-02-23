package llvm.types;

/**
 * Represents all integer types in LLVM.
 * All integers are signed in LLVM.
 * An integer type has a bit width, which can be anything from 1 -- 2^24-1
 */
public class IntegerType extends Type {
	protected final int width;
	
	public IntegerType(int _width) {
		if (_width <= 0 || _width >= (1<<24))
			throw new IllegalArgumentException("Size must be between 1 and 2^23-1");
		this.width = _width;
	}
	public int getWidth() {return this.width;}
	
	public boolean isInteger() {return true;}
	public IntegerType getIntegerSelf() {return this;}
	public boolean isPrimitive() {return true;}
	public boolean isDerived() {return true;}
	public boolean isFirstClass() {return true;}
	protected void ensureSized() {}
	public boolean hasTypeSize() {return true;}
	public long getTypeSize() {return this.width;} // correct?
	
	public IntegerType intern() {
		return Type.getIntegerType(this.width);
	}
	
	protected String toString(int depth) {
		return "i" + this.width;
	}
	public boolean equalsType(Type o) {
		if (!o.isInteger())
			return false;
		IntegerType t = o.getIntegerSelf();
		return this.width == t.width;
	}
	protected int hashCode(int depth) {
		return 541 * this.width;
	}
}
