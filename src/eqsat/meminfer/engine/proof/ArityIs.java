package eqsat.meminfer.engine.proof;

import eqsat.meminfer.engine.basic.Term;

public final class ArityIs<T extends Term<T,?>> implements Property {
	private final T mTerm;
	
	public ArityIs(T term, int arity) {
		if (term.getArity() != arity)
			throw new IllegalArgumentException();
		mTerm = term;
	}
	
	public T getTerm() {return mTerm;}
	public int getArity() {return mTerm.getArity();}
	
	public boolean equals(Object that) {
		return that instanceof ArityIs && equals((ArityIs)that);
	}
	public boolean equals(ArityIs<T> that) {return mTerm.equals(that.mTerm);}
	public int hashCode() {return mTerm.hashCode() + 9;}
	
	public String toString() {
		return "ArityIs(" + mTerm.hashCode() + "," + mTerm.getArity() + ")";
	}
}