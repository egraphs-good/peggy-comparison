package util;

public abstract class DisjointUnion<L, R> {
	public static <L,R> DisjointUnion<L,R> injectLeft(L left) {
		return new Left<L,R>(left);
	}
	public static <L,R> DisjointUnion<L,R> injectRight(R right) {
		return new Right<L,R>(right);
	}
	
	public abstract boolean isLeft();
	public abstract boolean isRight();
	public abstract L getLeft();
	public abstract R getRight();
	
	public abstract boolean equals(DisjointUnion<L,R> that);
	public abstract String toString();
	
	private static final class Left<L,R> extends DisjointUnion<L,R> {
		private final L mLeft;
		public Left(L left) {mLeft = left;}
		public boolean isLeft() {return true;}
		public boolean isRight() {return false;}
		public L getLeft() {return mLeft;}
		public R getRight() {throw new UnsupportedOperationException();}
		
		public boolean equals(Object that) {
			return that instanceof Left && equals((Left<L,R>)that);
		}
		public boolean equals(DisjointUnion<L,R> that) {
			return that.isLeft() && (mLeft == null ? that.getLeft() == null
					: mLeft.equals(that.getLeft()));
		}
		public int hashCode() {return mLeft == null ? 0 : mLeft.hashCode();}
		public String toString() {
			return /*"Left=" + */(mLeft == null ? "<null>" : mLeft.toString());
		}
	}
	
	private static final class Right<L,R> extends DisjointUnion<L,R> {
		private final R mRight;
		public Right(R right) {mRight = right;}
		public boolean isLeft() {return false;}
		public boolean isRight() {return true;}
		public L getLeft() {throw new UnsupportedOperationException();}
		public R getRight() {return mRight;}
		
		public boolean equals(Object that) {
			return that instanceof Right && equals((Right<L,R>)that);
		}
		public boolean equals(DisjointUnion<L,R> that) {
			return that.isRight() && (mRight == null ? that.getRight() == null
					: mRight.equals(that.getRight()));
		}
		public int hashCode() {return mRight == null ? 0 : mRight.hashCode();}
		public String toString() {
			return /*"Right=" + */(mRight == null ? "<null>" : mRight.toString());
		}
	}
}
