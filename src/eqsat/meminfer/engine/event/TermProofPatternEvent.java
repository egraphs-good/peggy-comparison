package eqsat.meminfer.engine.event;

import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.basic.Term;
import eqsat.meminfer.engine.proof.Proof;

public abstract class TermProofPatternEvent<T extends Term<T,?>, P>
		extends ProofPatternEvent<T,P> {
	protected TermProofPatternEvent(ProofEvent<T,? extends P> input) {
		super(input);
	}
	
	public final void addConstraints(Structure<T> term, Proof proof) {
		addConstraints((T)term, proof);
	}
	protected abstract void addConstraints(T term, Proof proof);
}
