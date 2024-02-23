package eqsat.meminfer.engine.proof;

import eqsat.meminfer.engine.peg.PEGTerm;

public class OpIsExtendedDomain<T extends PEGTerm<?,?,T,?>>
		implements Property {
	private final T mTerm;
	
	public OpIsExtendedDomain(T term) {
		if (!term.getOp().isExtendedDomain())
			throw new IllegalArgumentException();
		mTerm = term;
	}
	
	public T getTerm() {return mTerm;}
	
	public boolean equals(Object that) {
		return that instanceof OpIsExtendedDomain
				&& equals((OpIsExtendedDomain)that);
	}
	public boolean equals(OpIsExtendedDomain<T> that) {
		return mTerm.equals(that.mTerm);
	}
	public int hashCode() {return mTerm.hashCode() + 27;}
	
	public String toString() {
		return "OpIsExtendedDomain(" + mTerm.hashCode() + ")";
	}
}
