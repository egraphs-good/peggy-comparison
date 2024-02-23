package eqsat.meminfer.engine.basic;

public final class TermChild<T extends Term<T,V>, V extends Value<T,V>>
		implements TermOrTermChild<T,V> {
	private final T mTerm;
	private final int mChild;
	
	public TermChild(T term, int child) {
		mTerm = term;
		mChild = child;
	}
	
	public boolean equals(Object that) {
		return that instanceof TermChild && equals((TermChild)that);
	}
	public boolean equals(TermOrTermChild<T,V> that) {
		return that.isTermChild() && equals((TermChild<T,V>)that);
	}
	public boolean equals(TermChild<T,V> that) {
		return mChild == that.mChild && mTerm.equals(that.mTerm);
	}
	public int hashCode() {return mTerm.hashCode() - (mChild << 3);}
	
	public boolean isTerm() {return false;}
	public T getTerm() {throw new UnsupportedOperationException();}
	
	public boolean isTermChild() {return true;}
	public T getParentTerm() {return mTerm;}
	public int getChildIndex() {return mChild;}
	
	public T asTerm() {return mTerm.getChildAsTerm(mChild);}
	
	public Representative<V> getRepresentative() {
		return mTerm.getChild(mChild);
	}
	public V getValue() {return getRepresentative().getValue();}
	
	public String toString() {return "Child " + mChild + " of " + mTerm;}
}
