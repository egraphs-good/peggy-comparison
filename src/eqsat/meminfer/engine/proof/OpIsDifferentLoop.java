package eqsat.meminfer.engine.proof;

import eqsat.meminfer.engine.peg.PEGTerm;
import util.pair.Couple;

public class OpIsDifferentLoop<T extends PEGTerm<?,?,T,?>> implements Property {
	private final Couple<T> mTerms;
	
	public OpIsDifferentLoop(T left, T right) {
		if (left.getOp().getLoopDepth() == right.getOp().getLoopDepth())
			throw new IllegalArgumentException();
		mTerms = new Couple<T>(left, right);
	}
	
	public T getLeft() {return mTerms.getLeft();}
	public T getRight() {return mTerms.getRight();}
	public Couple<T> getTerms() {return mTerms;}
	
	public boolean equals(Object that) {
		return that instanceof OpIsDifferentLoop
				&& equals((OpIsDifferentLoop)that);
	}
	public boolean equals(OpIsDifferentLoop that) {
		return mTerms.equals(that.mTerms);
	}
	public int hashCode() {return mTerms.hashCode() + 25;}
	
	public String toString() {
		return "OpIsDifferentLoop(" + mTerms.getLeft().hashCode() + ","
				+ mTerms.getRight().hashCode() + ")";
	}
}
