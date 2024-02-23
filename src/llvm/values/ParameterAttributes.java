package llvm.values;

/**
 * Represents a bitmask of attributes that can be applied to functions, 
 * function parameters, and return values.
 */
public class ParameterAttributes {
	public static final int None = 0;
	public static final int ZExt = (1<<0);
	public static final int SExt = (1<<1);
	public static final int NoReturn = (1<<2);
	public static final int InReg = (1<<3);
	public static final int StructRet = (1<<4);
	public static final int NoUnwind = (1<<5);
	public static final int NoAlias = (1<<6);
	public static final int ByVal = (1<<7);
	public static final int Nest = (1<<8);
	public static final int ReadNone = (1<<9);
	public static final int ReadOnly = (1<<10);
	public static final int Alignment = (0xFFFF<<16);
	///////// new in 2.8 /////////////////////
	public static final int Alignment2_8 = (31<<16);
	public static final int NoInline = (1<<11);
	public static final int AlwaysInline = (1<<12);
	public static final int OptimizeForSize = (1<<13);
	public static final int StackProtect = (1<<14);
	public static final int StackProtectReq = (1<<15);
	public static final int NoCapture = (1<<21);
	public static final int NoRedZone = (1<<22);
	public static final int NoImplicitFloat = (1<<23);
	public static final int Naked = (1<<24);
	public static final int InlineHint = (1<<25);
	public static final int StackAlignment = (7<<26);

	private final int bits;
	private final boolean is2_8;
	public ParameterAttributes(int _bits) {
		this(false, _bits);
	}
	public ParameterAttributes(boolean _is2_8, int _bits) {
		this.is2_8 = _is2_8;
		this.bits = _bits;
	}
	
	public boolean is2_8() {return this.is2_8;}
	public int getBits() {return this.bits;}
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof ParameterAttributes))
			return false;
		ParameterAttributes p = (ParameterAttributes)o;
		return (this.is2_8 == p.is2_8) && 
			this.bits == p.bits;
	}
	public int hashCode() {
		return this.bits*101;
	}
	public String toString() {
		return this.bits+"";
	}
}
