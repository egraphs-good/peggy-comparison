package util.pair;

public final class PairSet<E> {
	protected final E mFirst;
	protected final E mSecond;
	
	public PairSet(E first, E second) {
		mFirst = first;
		mSecond = second;
	}
	
	public E getFirst() {return mFirst;}
	public E getSecond() {return mSecond;}
	
	public boolean equals(Object that) {
		return that instanceof PairSet && equals((PairSet<E>)that);
	}
	public boolean equals(PairSet<E> that) {
		if (mFirst == null ? that.mFirst == null : mFirst.equals(that.mFirst))
			return mSecond == null ? that.mSecond == null
					: mSecond.equals(that.mSecond);
		return (mFirst == null
				? that.mSecond == null : mFirst.equals(that.mSecond))
				&& (mSecond == null
				? that.mFirst == null : mSecond.equals(that.mFirst));
	}
	
	public int hashCode() {
		return (mFirst == null ? 0 : mFirst.hashCode())
				^ (mSecond == null ? 0 : mSecond.hashCode());
	}
	
	public String toString() {
		return "[" + (mFirst == null ? "<null>" : mFirst.toString()) + ","
				+ (mSecond == null ? "<null>" : mSecond.toString()) + "]";
	}
}
