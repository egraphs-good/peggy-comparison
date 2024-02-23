package util.pair;

public final class Couple<E> {
	private final E mLeft, mRight;
	
	public Couple(E left, E right) {
		mLeft = left;
		mRight = right;
	}
	
	public E getLeft() {return mLeft;}
	public E getRight() {return mRight;}
	
	public boolean contains(E that) {
		return that == null ? mLeft == null || mRight == null
				: mLeft != null && mLeft.equals(that)
				|| mRight != null && mRight.equals(that);
	}
	
	public boolean equals(Object that) {
		return that instanceof Couple && equals((Couple)that);
	}
	public boolean equals(Couple that) {
		return ((mLeft == null
				? that.mLeft == null : mLeft.equals(that.mLeft))
				&& (mRight == null
				? that.mRight == null : mRight.equals(that.mRight)))
				|| ((mLeft == null
				? that.mRight == null : mLeft.equals(that.mRight))
				&& (mRight == null
				? that.mLeft == null : mRight.equals(that.mLeft)));
	}
	
	public int hashCode() {
		return (mLeft == null ? 0 : mLeft.hashCode())
				+ (mRight == null ? 0 : mRight.hashCode());
	}
	
	public String toString() {
		return "{" + (mLeft == null ? "<null>" : mLeft.toString()) + ","
				+ (mRight == null ? "<null>" : mRight.toString()) + "}";
	}
}
