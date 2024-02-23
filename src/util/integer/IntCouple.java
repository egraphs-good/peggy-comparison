package util.integer;

public class IntCouple {
	private final int mLeft, mRight;
	
	public IntCouple(int left, int right) {
		mLeft = left;
		mRight = right;
	}
	
	public int getLeft() {return mLeft;}
	public int getRight() {return mRight;}
	
	public boolean contains(int that) {return mLeft == that || mRight == that;}
	
	public boolean equals(Object that) {
		return that instanceof IntCouple && equals((IntCouple)that);
	}
	public boolean equals(IntCouple that) {
		return (mLeft == that.mLeft && mRight == that.mRight)
				|| (mLeft == that.mRight && mRight == that.mLeft);
	}
	public int hashCode() {return mLeft + mRight;}
	
	public String toString() {return "{" + mLeft + "," + mRight + "}";}
}
