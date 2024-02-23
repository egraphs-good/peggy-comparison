package llvm.values;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;

import llvm.types.IntegerType;
import llvm.types.Type;

/**
 * Represents an integer value.
 * This is implemented as a bitvector, which can be converted into an int,
 * a long, or a BigInteger if you need to do arithmetic with it.
 */
public class IntegerValue extends Value {
	public static final IntegerValue TRUE = IntegerValue.get(1, new long[]{1L});
	public static final IntegerValue FALSE = IntegerValue.get(1, new long[]{0L});
	
	protected final int width;
	protected final BitSet bits; // this will be a 2's complement value
	
	public IntegerValue(int _width, BitSet _bits) {
		this.width = _width;
		this.bits = (BitSet)_bits.clone();
	}
	
	public void ensureConstant() {}
	public IntegerType getType() {return Type.getIntegerType(this.width);}
	public Iterator<? extends Value> getSubvalues() {
		return new ValueIterator();
	}
	public boolean isInteger() {return true;}
	public IntegerValue getIntegerSelf() {return this;}

	/**
	 * Puts the bits of this IntegerValue into consecutive
	 * longs in an array. The longs are in least-signficant order, 
	 * as are the bits within each long. Note that the final long
	 * will NOT be sign-extended to imply a negative number.
	 * The result of this function makes a valid input to IntegerValue.get(int,long[])
	 */
	public long[] getAsLongArray() {
		int numlongs = (this.width+63)>>6;
		long[] result = new long[numlongs];
		for (int i = 0; i < this.width; i++) {
			if (this.bits.get(i)) {
				int whichLong = (i>>6);
				int whichBit = (i&63);
				result[whichLong] |= (1L<<whichBit);
			}
		}
		return result;
	}
	
	
	public BigInteger getAsBigInteger() {
		long[] longbits = getAsLongArray();

		if (this.bits.get(this.width-1)) {
			// sign-extend the last long
			int startBit = (this.width-1)&63;
			int lastLong = longbits.length-1;
			for (int i = startBit+1; i < 64; i++) {
				longbits[lastLong] |= (1L<<i);
			}
		}

		// convert the longs into bytes
		byte[] bytes = new byte[longbits.length*8];
		int startByte = 0;
		for (int i = 0; i < longbits.length; i++) {
			bytes[startByte++] = (byte)(longbits[i]&0xFF);
			bytes[startByte++] = (byte)((longbits[i]>>8)&0xFF);
			bytes[startByte++] = (byte)((longbits[i]>>16)&0xFF);
			bytes[startByte++] = (byte)((longbits[i]>>24)&0xFF);
			bytes[startByte++] = (byte)((longbits[i]>>32)&0xFF);
			bytes[startByte++] = (byte)((longbits[i]>>40)&0xFF);
			bytes[startByte++] = (byte)((longbits[i]>>48)&0xFF);
			bytes[startByte++] = (byte)((longbits[i]>>56)&0xFF);
		}

		// now reverse so it's big-endian
		for (int i = 0; i < bytes.length/2; i++) {
			int other = bytes.length-1-i;
			byte temp = bytes[i];
			bytes[i] = bytes[other];
			bytes[other] = temp;
		}
		
		return new BigInteger(bytes);
	}
	
	public static IntegerValue get(int width, BigInteger bits) {
		if (width <= 0)
			throw new IllegalArgumentException("width must be positive: " + width);
		if (bits == null)
			throw new NullPointerException("bits");
		
		byte[] bytes = bits.toByteArray();
		
		// reverse the array to be in little-endian
		for (int i = 0; i < bytes.length/2; i++) {
			int other = bytes.length-1-i;
			byte temp = bytes[i];
			bytes[i] = bytes[other];
			bytes[other] = temp;
		}
		
		// convert to longs
		long[] longbits = new long[(bytes.length+7)/8];
		for (int i = 0; i < bytes.length; i++) {
			int longIndex = i>>3;
			int longBitOffset = (i&7)*8;
			longbits[longIndex] |= ((bytes[i]&0xFFL)<<longBitOffset);
		}
		if ((bytes[bytes.length-1]&0x80) != 0) {
			// sign-extend last long
			int startBit = (bytes.length<<3)&63;
			int lastLong = longbits.length-1;
			for (int i = startBit; i < 64; i++) {
				longbits[lastLong] |= (1L << i);
			}
		}
		
		// free up memory
		bytes = null;

		if (width > (longbits.length<<6) &&
			bits.signum() == -1) {
			// extend the array, and sign-extend
			long[] newlongs = new long[(width+63)>>6];
			for (int i = 0; i < longbits.length; i++) {
				newlongs[i] = longbits[i];
			}

			// add -1L to remaining longs
			for (int i = longbits.length; i < newlongs.length; i++) {
				newlongs[i] = -1L;
			}

			longbits = newlongs;
		}
		
		return IntegerValue.get(width, longbits);
	}
	
	public int getWidth() {return this.width;}
	public long getLongBits() {
		if (this.width > 64)
			throw new UnsupportedOperationException("integer width exceeds 64 bits: " + this.width);
		long result = 0L;
		for (int i = 0; i < this.width; i++) {
			if (this.bits.get(i))
				result |= (1L<<i);
		}
		
		if ((result & (1L<<(this.width-1))) != 0L) {
			// sign-extend
			for (int i = this.width; i < 64; i++)
				result |= (1L<<i);
		}
		return result;
	}
	public int getIntBits() {
		if (this.width > 32)
			throw new UnsupportedOperationException("integer width exceeds 32 bits: " + this.width);
		int result = 0;
		for (int i = 0; i < this.width; i++) {
			if (this.bits.get(i))
				result |= (1<<i);
		}
		
		if ((result & (1<<(this.width-1))) != 0) {
			// sign-extend
			for (int i = this.width; i < 32; i++)
				result |= (1<<i);
		}
		
		return result;
	}
	public boolean getBit(int i) {
		if (i < 0 || i >= this.width)
			throw new IndexOutOfBoundsException(""+i);
		return this.bits.get(i);
	}

	/**
	 * Expect the input words to be in raw format
	 */
	public static IntegerValue get(int width, long[] words) {
		if (width <= 0 || width >= (1<<23))
			throw new IllegalArgumentException("width must be in (0,2^23)");
		BitSet bits = new BitSet(width);
		int min = Math.min(width, words.length<<6);
		for (int i = 0; i < min; i++) {
			long word = words[i>>6];
			int bit = i&63;
			if ((word & (1L<<bit)) != 0)
				bits.set(i);
		}
		return new IntegerValue(width, bits);
	}
	
	/**
	 * Returns a new integer value based on a string of 1's and 0's.
	 * The string should be in big-endian format, so that index 0 has the MSb.
	 */
	public static IntegerValue get(int width, String str) {
		if (width <= 0 || width >= (1<<23))
			throw new IllegalArgumentException("width must be in (0,2^23)");
		BitSet bits = new BitSet(width);
		int min = Math.min(width, str.length());
		for (int i = min-1; i >= 0; i--) {
			char c = str.charAt(i);
			if (c == '1')
				bits.set(str.length()-1-i);
			else if (c != '0')
				throw new NumberFormatException("Expecting only 1 or 0: " + c);
		}
		return new IntegerValue(width, bits);
	}
	
	public String toString() {
		return this.getType() + "(" + this.getAsBigInteger() + ")";
	}
	public boolean equalsValue(Value o) {
		if (!o.isInteger())
			return false;
		IntegerValue v = o.getIntegerSelf();
		return this.width == v.width && this.bits.equals(v.bits);
	}
	public int hashCode() {
		return this.width*3 + this.bits.hashCode()*5;
	}
	
	protected IntegerValue rewriteChildren(Map<Value,Value> old2new) {
		return this;
	}
	
	public boolean isZero() {
		for (int i = 0; i < this.width; i++) {
			if (this.bits.get(i))
				return false;
		}
		return true;
	}
	/**
	 * Technically for width=1, there is no 1 it's actually -1, 
	 * but this will still return true for that case.
	 */
	public boolean isOne() {
		if (!this.bits.get(0))
			return false;
		for (int i = 1; i < this.width; i++) {
			if (this.bits.get(i))
				return false;
		}
		return true;
	}

	/**
	 * Returns true if all the bits are set (within the width).
	 * Note that for i1, this method will return true iff isOne()
	 * returns true.
	 */
	public boolean isNegativeOne() {
		for (int i = 0; i < this.width; i++) {
			if (!this.bits.get(i))
				return false;
		}
		return true;
	}

	/**
	 * Returns true iff the top bit is set.
	 */
	public boolean isNegative() {
		return this.bits.get(this.width-1);
	}
	

	public IntegerValue and(IntegerValue other) {
		if (other.getWidth() != this.getWidth())
			throw new IllegalArgumentException("Wrong width: " + other.getWidth());
		BitSet newbits = new BitSet(this.getWidth());
		for (int i = 0; i < this.getWidth(); i++) {
			newbits.set(i, this.getBit(i) & other.getBit(i));
		}
		return new IntegerValue(this.getWidth(), newbits);
	}
	
	public IntegerValue or(IntegerValue other) {
		if (other.getWidth() != this.getWidth())
			throw new IllegalArgumentException("Wrong width: " + other.getWidth());
		BitSet newbits = new BitSet(this.getWidth());
		for (int i = 0; i < this.getWidth(); i++) {
			newbits.set(i, this.getBit(i) | other.getBit(i));
		}
		return new IntegerValue(this.getWidth(), newbits);
	}
	
	public IntegerValue xor(IntegerValue other) {
		if (other.getWidth() != this.getWidth())
			throw new IllegalArgumentException("Wrong width: " + other.getWidth());
		BitSet newbits = new BitSet(this.getWidth());
		for (int i = 0; i < this.getWidth(); i++) {
			newbits.set(i, this.getBit(i) ^ other.getBit(i));
		}
		return new IntegerValue(this.getWidth(), newbits);
	}
	
	public IntegerValue negate() {
		BitSet newbits = new BitSet(this.getWidth());
		for (int i = 0; i < this.getWidth(); i++) {
			newbits.set(i, true ^ this.getBit(i));
		}
		return new IntegerValue(this.getWidth(), newbits);
	}
	
	
	public static IntegerValue getZero(int width) {
		return new IntegerValue(width, new BitSet());
	}
	
	public static IntegerValue getNegativeOne(int width) {
		BitSet bits = new BitSet(width);
		bits.set(0, width);
		return new IntegerValue(width, bits);
	}
	
	public static IntegerValue getOne(int width) {
		BitSet bits = new BitSet();
		bits.set(0);
		return new IntegerValue(width, bits);
	}

	/**
	 * Returns true if this IntegerValue
	 * equals the given int. Whichever value has the smaller width
	 * will be implicitly sign-extended before the comparison is made.
	 */
	public boolean equalsInt(int value) {
		if (this.width <= 32) {
			for (int i = 0; i < this.width; i++) {
				boolean different = this.bits.get(i) ^ (((value>>i)&1)==1);
				if (different)
					return false;
			}
			if (this.width < 32) {
				boolean last = this.bits.get(this.width-1);
				for (int i = this.width; i < 32; i++) {
					boolean different = last ^ ((value>>i)&1)==1;
					if (different)
						return false;
				}
			}
			return true;
		}
		else {
			for (int i = 0; i < 32; i++) {
				boolean different = this.bits.get(i) ^ (((value>>i)&1)==1);
				if (different)
					return false;
			}
			boolean last = (((value>>31)&1)==1);
			for (int i = 32; i < this.width; i++) {
				boolean different = this.bits.get(i) ^ last;
				if (different)
					return false;
			}
			return true;
		}
	}
}
