package eqsat.meminfer.engine.proof;

import eqsat.meminfer.engine.peg.PEGTerm;
import eqsat.meminfer.engine.peg.PEGValue;

public class ChildIsInvariant
		<T extends PEGTerm<?,?,T,V>, V extends PEGValue<T,V>>
		implements Property {
	private final T mTerm, mLoop;
	private final int mChild;
	
	public ChildIsInvariant(T term, int child, T loop) {
		if (!loop.getOp().isLoopFunction()
				&& !term.getChild(child).getValue().isInvariant(
						loop.getOp().getLoopDepth()))
			throw new IllegalArgumentException();
		mTerm = term;
		mChild = child;
		mLoop = loop;
	}
	
	public T getTerm() {return mTerm;}
	public int getChild() {return mChild;}
	public T getLoop() {return mLoop;}
	
	public boolean equals(Object that) {
		return that instanceof ChildIsInvariant
				&& equals((ChildIsInvariant)that);
	}
	public boolean equals(ChildIsInvariant that) {
		return mTerm.equals(that.mTerm) && mChild == that.mChild
				&& mLoop.equals(that.mLoop);
	}
	public int hashCode() {
		return mTerm.hashCode() + mChild + mLoop.hashCode();
	}
	
	public String toString() {
		return "ChildIsInvariant(" + mTerm.hashCode() + "," + mChild + ","
				+ mLoop.hashCode() + ")";
	}
}
