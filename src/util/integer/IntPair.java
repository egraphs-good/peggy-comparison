package util.integer;

public final class IntPair<S> {
	protected final int mFirst;
	protected final S mSecond;
	
	public IntPair(int first, S second) {
		mFirst = first;
		mSecond = second;
	}
	
	public int getFirst() {return mFirst;}
	public S getSecond() {return mSecond;}
	
	public boolean equals(Object that) {
		return that instanceof IntPair && equals((IntPair)that);
	}
	public boolean equals(IntPair that) {
		return mFirst == that.mFirst && (mSecond == null
				? that.mSecond == null : mSecond.equals(that.mSecond));
	}
	
	public int hashCode() {
		return mFirst + 37 * (mSecond == null ? 0 : mSecond.hashCode());
	}
	
	public String toString() {
		return "[" + mFirst + ","
				+ (mSecond == null ? "<null>" : mSecond.toString()) + "]";
	}
}
