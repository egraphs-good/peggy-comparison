package eqsat.meminfer.engine.proof;

import eqsat.meminfer.engine.peg.PEGTerm;

public class OpIsLoopLifted<T extends PEGTerm<?,?,T,?>> implements Property {
	private final T mTerm, mLoop;
	
	public OpIsLoopLifted(T term, T loop) {
		if (!term.getOp().isLoopLiftedAll(loop.getOp().getLoopDepth()))
			throw new IllegalArgumentException();
		mTerm = term;
		mLoop = loop;
	}
	
	public T getTerm() {return mTerm;}
	public T getLoop() {return mLoop;}
	
	public boolean equals(Object that) {
		return that instanceof OpIsLoopLifted && equals((OpIsLoopLifted)that);
	}
	public boolean equals(OpIsLoopLifted<T> that) {
		return mTerm.equals(that.mTerm) && mLoop.equals(that.mLoop);
	}
	public int hashCode() {return mTerm.hashCode() + mLoop.hashCode() + 29;}
	
	public String toString() {
		return "OpIsLoopLifted(" + mTerm.hashCode()
				+ "," + mLoop.hashCode() + ")";
	}
}
