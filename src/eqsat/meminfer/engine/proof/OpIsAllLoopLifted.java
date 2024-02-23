package eqsat.meminfer.engine.proof;

import eqsat.meminfer.engine.peg.PEGTerm;

public class OpIsAllLoopLifted<T extends PEGTerm<?,?,T,?>>
		implements Property {
	private final T mTerm;
	
	public OpIsAllLoopLifted(T term) {
		if (!term.getOp().isLoopLiftedAll())
			throw new IllegalArgumentException();
		mTerm = term;
	}
	
	public T getTerm() {return mTerm;}
	
	public boolean equals(Object that) {
		return that instanceof OpIsAllLoopLifted
				&& equals((OpIsAllLoopLifted)that);
	}
	public boolean equals(OpIsAllLoopLifted<T> that) {
		return mTerm.equals(that.mTerm);
	}
	public int hashCode() {return mTerm.hashCode() + 31;}
	
	public String toString() {
		return "OpIsAllLoopLifted(" + mTerm.hashCode() + ")";
	}
}
