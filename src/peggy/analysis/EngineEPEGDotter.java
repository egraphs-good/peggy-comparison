package peggy.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This class is an EPEGDotter that is specialized to EPEGs represented in 
 * terms of CPEGValues and CPEGTerms.
 */
public class EngineEPEGDotter<L,P> extends EPEGDotter<CPEGTerm<L,P>,CPEGValue<L,P>> {
	private final CPeggyAxiomEngine<L,P> engine;
	private final Collection<? extends CPEGValue<L,P>> returns;
	public EngineEPEGDotter(
			CPeggyAxiomEngine<L,P> _engine, 
			Collection<? extends CPEGValue<L,P>> _returns) {
		this.returns = _returns;
		this.engine = _engine;
	}
	protected int getArity(CPEGTerm<L, P> term) {
		return term.getArity();
	}
	protected CPEGValue<L, P> getChildValue(CPEGTerm<L, P> term, int child) {
		return term.getChild(child).getValue();
	}
	protected String getLabel(CPEGTerm<L, P> term) {
		return term.getOp().toString();
	}
	protected Collection<? extends CPEGTerm<L, P>> getTerms(CPEGValue<L, P> value) {
		return value.getTerms();
	}
	protected List<CPEGValue<L, P>> getValues() {
		return new ArrayList<CPEGValue<L,P>>(
				engine.getEGraph().getValueManager().getValues());
	}
	protected boolean isRoot(CPEGValue<L, P> value) {
		return returns.contains(value);
	}
}
