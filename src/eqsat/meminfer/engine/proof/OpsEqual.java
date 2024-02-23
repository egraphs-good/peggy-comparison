package eqsat.meminfer.engine.proof;

import eqsat.meminfer.engine.op.OpTerm;
import util.pair.Couple;

public final class OpsEqual<O, T extends OpTerm<? extends O,T,?>>
		implements Property {
	private final Couple<T> mTerms;
	
	public OpsEqual(T left, T right) {
		if (!left.getOp().equals(right.getOp()))
			throw new IllegalArgumentException();
		mTerms = new Couple<T>(left, right);
	}
	
	public T getLeft() {return mTerms.getLeft();}
	public T getRight() {return mTerms.getRight();}
	public Couple<T> getTerms() {return mTerms;}
	
	public boolean equals(Object that) {
		return that instanceof OpsEqual && equals((OpsEqual)that);
	}
	public boolean equals(OpsEqual that) {return mTerms.equals(that.mTerms);}
	public int hashCode() {return mTerms.hashCode() + 5;}
	
	public String toString() {
		return "OpsEqual(" + mTerms.getLeft().hashCode() + ","
				+ mTerms.getRight().hashCode() + ")";
	}
}
