package llvm.types;

/**
 * Represents floating point types in LLVM.
 * There are 5 floating point types in LLVM:
 *    float
 *    double
 *    x86_fp80
 *    fp128
 *    ppc_fp128
 */
public class FloatingPointType extends Type {
	public static enum Kind {
		FLOAT("float", 32),
		DOUBLE("double", 64),
		X86_FP80("x86_fp80", 80),
		FP128("fp128", 128),
		PPC_FP128("ppc_fp128", 128);
		
		private String label;
		private int typeSize;
		private Kind(String _label, int _typeSize) {
			this.label = _label;
			this.typeSize = _typeSize;
		}
		public String getLabel() {return this.label;}
		public int getTypeSize() {return this.typeSize;}
	}
	
	protected final Kind kind;
	
	public FloatingPointType(Kind _kind) {
		if (_kind == null)
			throw new NullPointerException("kind is null");
		this.kind = _kind;
	}
	public Kind getKind() {return this.kind;}
	
	public boolean isFloatingPoint() {return true;}
	public FloatingPointType getFloatingPointSelf() {return this;}
	public boolean isPrimitive() {return true;}
	public boolean isFirstClass() {return true;}
	protected void ensureSized() {}
	public boolean hasTypeSize() {return true;}
	public long getTypeSize() {return this.kind.getTypeSize();}
	
	public FloatingPointType intern() {
		return Type.getFloatingPointType(this.kind);
	}
	
	public static FloatingPointType fromString(String str) {
		for (Kind k : Kind.values()) {
			if (k.name().equals(str.toLowerCase()))
				return new FloatingPointType(k);
		}
		return null;
	}
	
	protected String toString(int depth) {
		return this.kind.getLabel();
	}
	public boolean equalsType(Type o) {
		if (!o.isFloatingPoint())
			return false;
		FloatingPointType f = o.getFloatingPointSelf();
		return this.kind.equals(f.kind);
	}
	protected int hashCode(int depth) {
		return this.kind.hashCode() * 523;
	}
}
