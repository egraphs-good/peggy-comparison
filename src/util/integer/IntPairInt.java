package util.integer;

public final class IntPairInt {
	protected final int mFirst;
	protected final int mSecond;
	
	public IntPairInt(int first, int second) {
		mFirst = first;
		mSecond = second;
	}
	
	public int getFirst() {return mFirst;}
	public int getSecond() {return mSecond;}
	
	public boolean equals(Object that) {
		return that instanceof IntPairInt && equals((IntPairInt)that);
	}
	public boolean equals(IntPairInt that) {
		return mFirst == that.mFirst && mSecond == that.mSecond;
	}
	
	public int hashCode() {
		return mFirst + 37 * mSecond;
	}
	
	public String toString() {
		return "[" + mFirst + "," + mSecond + "]";
	}
}
