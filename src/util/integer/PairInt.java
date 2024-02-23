package util.integer;

public class PairInt<F> {
	protected final F mFirst;
	protected final int mSecond;
	
	public PairInt(F first, int second) {
		mFirst = first;
		mSecond = second;
	}
	
	public F getFirst() {return mFirst;}
	public int getSecond() {return mSecond;}
	
	public boolean equals(Object that) {
		return that instanceof PairInt && equals((PairInt)that);
	}
	public boolean equals(PairInt that) {
		return (mFirst == null
				? that.mFirst == null : mFirst.equals(that.mFirst))
				&& mSecond == that.mSecond;
	}
	
	public int hashCode() {
		return (mFirst == null ? 0 : mFirst.hashCode()) + 37 * mSecond;
	}
	
	public String toString() {
		return "[" + (mFirst == null ? "<null>" : mFirst.toString()) + ","
				+ mSecond + "]";
	}
}
