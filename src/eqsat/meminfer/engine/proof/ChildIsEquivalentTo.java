package eqsat.meminfer.engine.proof;

import eqsat.meminfer.engine.basic.Term;
import eqsat.meminfer.engine.basic.Value;

public class ChildIsEquivalentTo<T extends Term<T,V>, V extends Value<T,V>>
		implements Property {
	private T mTerm;
	private int mChild;
	private T mEquivalent;
	
	public ChildIsEquivalentTo(T term, int child, T equivalent) {
		if (!term.getChild(child).getValue().equals(equivalent.getValue()))
			throw new IllegalArgumentException();
		mTerm = term;
		mChild = child;
		mEquivalent = equivalent;
	}
	
	public T getParentTerm() {return mTerm;}
	public int getChild() {return mChild;}
	public T getTerm() {return mEquivalent;}
	
	public boolean equals(Object that) {
		return that instanceof ChildIsEquivalentTo
				&& equals((ChildIsEquivalentTo)that);
	}
	public boolean equals(ChildIsEquivalentTo that) {
		return mTerm.equals(that.mTerm) && mChild == that.mChild
				&& mEquivalent.equals(that.mEquivalent);
	}
	public int hashCode() {return mEquivalent.hashCode() + 11;}
	
	public String toString() {
		return "ChildIsEquivalentTo(" + mTerm.hashCode() + "," + mChild + ","
				+ mEquivalent.hashCode() + ")";
	}
}
