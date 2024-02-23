package util;

public final class Triple<F, S, T> {
	private final F mFirst;
	private final S mSecond;
	private final T mThird;
	
	public Triple(F first, S second, T third) {
		mFirst = first;
		mSecond = second;
		mThird = third;
	}
	
	public F getFirst() {return mFirst;}
	public S getSecond() {return mSecond;}
	public T getThird() {return mThird;}
	
	public boolean equals(Object that) {
		return that instanceof Triple && equals((Triple)that);
	}
	public boolean equals(Triple that) {
		return (mFirst == null
				? that.mFirst == null : mFirst.equals(that.mFirst))
				&& (mSecond == null
				? that.mSecond == null : mSecond.equals(that.mSecond))
				&& (mThird == null
				? that.mThird == null : mThird.equals(that.mThird));
	}
	
	public int hashCode() {
		return (mFirst == null ? 0 : mFirst.hashCode())
				+ 37 * (mSecond == null ? 0 : mSecond.hashCode())
				+ 23 * (mThird == null ? 0 : mThird.hashCode());
	}
	
	public String toString() {
		return "[" + (mFirst == null ? "<null>" : mFirst.toString()) + ","
				+ (mSecond == null ? "<null>" : mSecond.toString())
				+ (mThird == null ? "<null>" : mThird.toString()) + "]";
	}
}
