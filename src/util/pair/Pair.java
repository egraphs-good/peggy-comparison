package util.pair;

public final class Pair<F, S> {
	private final F mFirst;
	private final S mSecond;
	
	public Pair(F first, S second) {
		mFirst = first;
		mSecond = second;
	}
	
	public F getFirst() {return mFirst;}
	public S getSecond() {return mSecond;}
	
	public boolean equals(Object that) {
		return that instanceof Pair && equals((Pair)that);
	}
	public boolean equals(Pair that) {
		return (mFirst == null
				? that.mFirst == null : mFirst.equals(that.mFirst))
				&& (mSecond == null
				? that.mSecond == null : mSecond.equals(that.mSecond));
	}
	
	public int hashCode() {
		return (mFirst == null ? 0 : mFirst.hashCode())
				+ 37 * (mSecond == null ? 0 : mSecond.hashCode());
	}
	
	public String toString() {
		return "[" + (mFirst == null ? "<null>" : mFirst.toString()) + ","
				+ (mSecond == null ? "<null>" : mSecond.toString()) + "]";
	}
}
