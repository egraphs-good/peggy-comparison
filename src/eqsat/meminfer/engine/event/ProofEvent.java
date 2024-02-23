package eqsat.meminfer.engine.event;

import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.proof.Proof;

public interface ProofEvent<T, P> extends Event<P> {
	void generateProof(Structure<T> result, Proof proof);
}
