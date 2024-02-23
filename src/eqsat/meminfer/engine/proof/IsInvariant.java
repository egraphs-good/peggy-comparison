package eqsat.meminfer.engine.proof;

import eqsat.meminfer.engine.peg.PEGTerm;
import eqsat.meminfer.engine.peg.PEGValue;

public final class IsInvariant
		<T extends PEGTerm<?,?,T,V>, V extends PEGValue<T,V>>
		implements Property {
	private final T mTerm, mLoop;
	
	public IsInvariant(T term, T loop) {
		if (!loop.getOp().isLoopFunction()
				&& !term.getValue().isInvariant(loop.getOp().getLoopDepth()))
			throw new IllegalArgumentException();
		mTerm = term;
		mLoop = loop;
	}
	
	public T getTerm() {return mTerm;}
	public T getLoop() {return mLoop;}
	
	public boolean equals(Object that) {
		return that instanceof IsInvariant && equals((IsInvariant)that);
	}
	public boolean equals(IsInvariant that) {
		return mTerm.equals(that.mTerm) && mLoop.equals(that.mLoop);
	}
	public int hashCode() {return mTerm.hashCode() + mLoop.hashCode() + 9;}
	
	public String toString() {
		return "IsInvariant(" + mTerm.hashCode() + ","
				+ mLoop.hashCode() + ")";
	}
}
