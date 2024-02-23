package llvm.values;

import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;

import llvm.types.FloatingPointType;
import llvm.types.FloatingPointType.Kind;

/**
 * Represents a floating point value.
 * This is implemented as a bit vector, which can be converted into 
 * a float or double (the rest are not implemented yet) if you need 
 * to do arithmetic on it.
 */
public class FloatingPointValue extends Value {
	protected final FloatingPointType type;
	protected final BitSet bits;
	
	protected FloatingPointValue(FloatingPointType _type, BitSet _bits) {
		this.type = _type;
		this.bits = _bits;
	}

	public void ensureConstant() {}
	public FloatingPointType getType() {return this.type;}
	public Iterator<? extends Value> getSubvalues() {
		return new ValueIterator();
	}
	public boolean isFloatingPoint() {return true;}
	public FloatingPointValue getFloatingPointSelf() {return this;}
	
	public float getFloatBits() {
		if (!this.type.getKind().equals(FloatingPointType.Kind.FLOAT))
			throw new UnsupportedOperationException("value is not a float");
		int intbits = 0;
		for (int i = 0; i < 32; i++) {
			if (this.bits.get(i))
				intbits |= (1<<i);
		}
		return Float.intBitsToFloat(intbits);
	}
	public double getDoubleBits() {
		if (!this.type.getKind().equals(FloatingPointType.Kind.DOUBLE))
			throw new UnsupportedOperationException("value is not a double");
		long longbits = 0L;
		for (int i = 0; i < 64; i++) {
			if (this.bits.get(i))
				longbits |= (1L<<i);
		}
		return Double.longBitsToDouble(longbits);
	}
	public boolean getBit(int i) {
		if (i < 0 || i >= this.type.getKind().getTypeSize())
			throw new IndexOutOfBoundsException(""+i);
		return this.bits.get(i);
	}
	
	public static FloatingPointValue get(FloatingPointType type, long[] words) {
		int width = type.getKind().getTypeSize();
		if (width > words.length*64)
			throw new IllegalArgumentException("Not enough bits given for float value");
		BitSet bits = new BitSet(width);
		for (int i = 0; i < width; i++) {
			long word = words[i>>6];
			int bit = i&63;
			if ((word & (1L<<bit)) != 0)
				bits.set(i);
		}
		return new FloatingPointValue(type, bits);
	}
	
	/**
	 * Returns a new integer value based on a string of 1's and 0's.
	 * The string should be in big-endian format, so that index 0 has the MSb.
	 */
	public static FloatingPointValue get(FloatingPointType type, String str) {
		int width = type.getKind().getTypeSize();
		BitSet bits = new BitSet(width);
		int min = Math.min(width, str.length());
		for (int i = min-1; i >= 0; i--) {
			char c = str.charAt(i);
			if (c == '1')
				bits.set(str.length()-1-i);
			else if (c != '0')
				throw new NumberFormatException("Expecting only 1 or 0: " + c);
		}
		return new FloatingPointValue(type, bits);
	}
	
	public static FloatingPointValue get(FloatingPointType type, BitSet input) {
		int width = type.getKind().getTypeSize();
		BitSet bits = new BitSet(width);
		int min = Math.min(width, input.length());
		for (int i = min-1; i >= 0; i--) {
			if (input.get(i))
				bits.set(i);
		}
		return new FloatingPointValue(type, bits);
	}
	
	public static FloatingPointValue fromFloat(float f) {
		int bits = Float.floatToRawIntBits(f);
		BitSet bitset = new BitSet(32);
		for (int i = 0; i < 32; i++) 
			bitset.set(i, ((bits>>i)&1)==1);
		return new FloatingPointValue(
				new FloatingPointType(Kind.FLOAT), 
				bitset);
	}
	
	public static FloatingPointValue fromDouble(double d) {
		long bits = Double.doubleToRawLongBits(d);
		BitSet bitset = new BitSet(64);
		for (int i = 0; i < 64; i++) 
			bitset.set(i, ((bits>>i)&1L)==1L);
		return new FloatingPointValue(
				new FloatingPointType(Kind.DOUBLE), 
				bitset);
	}
	
	public String toString() {
		switch (this.type.getKind()) {
		case FLOAT:
			return this.type + "(" + this.getFloatBits() + ")";
		case DOUBLE:
			return this.type + "(" + this.getDoubleBits() + ")";
		default:
			return this.type + "(too big)";
		}
	}
	public boolean equalsValue(Value o) {
		if (!o.isFloatingPoint())
			return false;
		FloatingPointValue f = o.getFloatingPointSelf();
		return this.type.equalsType(f.type) && this.bits.equals(f.bits);
	}
	public int hashCode() {
		return this.type.hashCode()*11 + this.bits.hashCode()*13;
	}
	
	protected FloatingPointValue rewriteChildren(Map<Value,Value> old2new) {
		return this;
	}
}
