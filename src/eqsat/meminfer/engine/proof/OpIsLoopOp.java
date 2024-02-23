package eqsat.meminfer.engine.proof;

import eqsat.meminfer.engine.peg.PEGTerm;
import eqsat.meminfer.network.peg.PEGNetwork.PEGLoopOp;

public final class OpIsLoopOp<T extends PEGTerm<?,?,T,?>> implements Property {
	private final T mTerm;
	private final PEGLoopOp mOp;
	
	public OpIsLoopOp(T term, PEGLoopOp op) {
		if (!op.isLoopOp(term.getOp()))
			throw new IllegalArgumentException();
		mTerm = term;
		mOp = op;
	}
	
	public T getTerm() {return mTerm;}
	public PEGLoopOp getOp() {return mOp;}
	
	public boolean equals(Object that) {
		return that instanceof OpIsLoopOp && equals((OpIsLoopOp)that);
	}
	public boolean equals(OpIsLoopOp<T> that) {
		return mTerm.equals(that.mTerm) && mOp.equals(that.mOp);
	}
	public int hashCode() {return mTerm.hashCode() + 15 * mOp.hashCode();}
	
	public String toString() {
		return "OpIsLoopOp(" + mTerm.hashCode() + "," + mOp + ")";
	}
}
