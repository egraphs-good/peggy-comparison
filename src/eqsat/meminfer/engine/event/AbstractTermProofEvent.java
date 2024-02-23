package eqsat.meminfer.engine.event;

import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.basic.Term;
import eqsat.meminfer.engine.proof.Proof;

public abstract class AbstractTermProofEvent<T extends Term<T,?>, P>
		extends AbstractProofEvent<T,P> {
	public final void generateProof(Structure<T> term, Proof proof) {
		generateProof((T)term, proof);
	}
	protected abstract void generateProof(T term, Proof proof);
}
