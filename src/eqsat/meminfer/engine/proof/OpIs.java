package eqsat.meminfer.engine.proof;

import eqsat.meminfer.engine.op.OpTerm;

public class OpIs<O, T extends OpTerm<? extends O,T,?>> implements Property {
	private final T mTerm;
	
	public OpIs(T term, O op) {
		if (!term.getOp().equals(op))
			throw new IllegalArgumentException();
		mTerm = term;
	}
	
	public T getTerm() {return mTerm;}
	public O getOp() {return mTerm.getOp();}
	
	public boolean equals(Object that) {
		return that instanceof OpIs && equals((OpIs)that);
	}
	public boolean equals(OpIs<O,T> that) {return mTerm.equals(that.mTerm);}
	public int hashCode() {return mTerm.hashCode() + 13;}
	
	public String toString() {
		return "OpIs(" + mTerm.hashCode() + "," + mTerm.getOp() + ")";
	}
}
