package eqsat.meminfer.engine.proof;

import eqsat.meminfer.engine.basic.Term;
import eqsat.meminfer.engine.basic.Value;
import util.pair.Couple;

public final class AreEquivalent<T extends Term<T,V>, V extends Value<T,V>>
		implements Property {
	private final Couple<T> mTerms;
	
	public AreEquivalent(T left, T right) {
		if (!left.getValue().equals(right.getValue()))
			throw new IllegalArgumentException();
		mTerms = new Couple<T>(left, right);
	}
	
	public T getLeft() {return mTerms.getLeft();}
	public T getRight() {return mTerms.getRight();}
	public Couple<T> getTerms() {return mTerms;}
	
	public boolean equals(Object that) {
		return that instanceof AreEquivalent && equals((AreEquivalent)that);
	}
	public boolean equals(AreEquivalent that) {
		return mTerms.equals(that.mTerms);
	}
	public int hashCode() {return mTerms.hashCode() + 17;}
	
	public String toString() {
		return "AreEquivalent(" + mTerms.getLeft().hashCode() + ","
				+ mTerms.getRight().hashCode() + ")";
	}
}
