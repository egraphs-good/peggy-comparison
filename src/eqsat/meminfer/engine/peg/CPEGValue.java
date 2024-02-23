package eqsat.meminfer.engine.peg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eqsat.meminfer.engine.basic.Ambassador;

public final class CPEGValue<O, P>
		extends PEGValue<CPEGTerm<O,P>,CPEGValue<O,P>> {
	protected CPEGValue<O,P> mValue;
	protected List<CPEGTerm<O,P>> mTerms = new ArrayList<CPEGTerm<O,P>>();
	protected int mInvariance = 0;
	
	protected CPEGValue<O,P> getValue() {
		if (mValue != null)
			return mValue = mValue.getValue();
		else
			return this;
	}

	public boolean equals(Object that) {
		return that instanceof CPEGValue && equals((CPEGValue)that);
	}
	public boolean equals(CPEGValue that) {
		return getValue() == that.getValue();
	}
	public int hashCode() {
		return System.identityHashCode(getValue());
	}

	public void addAmbassador(
			Ambassador<CPEGTerm<O,P>,CPEGValue<O,P>> ambassador) {
		if (!equals(ambassador.getValue()))
			throw new IllegalArgumentException();
	}

	public void addTerm(CPEGTerm<O,P> term) {
		if (!equals(term.getValue()))
			throw new IllegalArgumentException();
		getValue().mTerms.add(term);
	}
	
	public void removeTerm(CPEGTerm<O,P> term) {
		if (!equals(term.getValue()))
			throw new IllegalArgumentException();
		getValue().mTerms.remove(term);
	}

	public Collection<? extends CPEGTerm<O,P>> getTerms() {
		return getValue().mTerms;
	}
	
	public int getInvariance() {return getValue().mInvariance;}
	protected void setInvariance(int invariance) {
		getValue().mInvariance = invariance;
	}
	
	public String toString() {return "Value " + hashCode();}
}
