package llvm.bitcode;

import java.math.BigInteger;

/**
 * This class represents an unsigned long value, which is not supported natively
 * in Java. The representation is a signed long, which has the same bit length.
 */
public class UnsignedLong {
	private final long value;
	
	public UnsignedLong(long _value) {
		this.value = _value;
	}
	
	public UnsignedLong add(UnsignedLong other) {
		return new UnsignedLong(this.value + other.value);
	}
	public UnsignedLong subtract(UnsignedLong other) {
		return new UnsignedLong(this.value - other.value);
	}
	public UnsignedLong multiply(UnsignedLong other) {
		return new UnsignedLong(this.value * other.value);
	}
	public boolean gt(UnsignedLong other) {
		if (this.value < 0L) {
			if (other.value >= 0L)
				return true;
			else
				return this.value > other.value;
		} else if (this.value > 0L) {
			if (other.value < 0L)
				return false;
			else if (other.value > 0L)
				return this.value > other.value;
			else
				return true;
		} else {
			return false;
		}
	}
	
	public long signedValue() {return this.value;}
	public BigInteger bigintValue() {
		return new BigInteger(1, new byte[]{
				(byte)((value>>56)&0xFF),
				(byte)((value>>48)&0xFF),
				(byte)((value>>40)&0xFF),
				(byte)((value>>32)&0xFF),
				(byte)((value>>24)&0xFF),
				(byte)((value>>16)&0xFF),
				(byte)((value>>8)&0xFF),
				(byte)(value&0xFF)});
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof UnsignedLong))
			return false;
		UnsignedLong l = (UnsignedLong)o;
		return l.value == this.value;
	}
	public int hashCode() {
		return new Long(this.value).hashCode();
	}
	public String toString() {
		return this.bigintValue().toString();
	}
	
	public static void main(String args[]) {
		System.out.println(new UnsignedLong(-1L));
	}
}
