package eqsat.meminfer.engine.event;

import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.proof.Proof;

public abstract class AbstractProofChainEvent<T, I, O>
		extends AbstractProofEvent<T,O> implements ProofChainEvent<T,I,O> {
	private final ProofEvent<T,? extends I> mInput;
	
	protected AbstractProofChainEvent(ProofEvent<T,? extends I> input) {
		mInput = input;
		mInput.addListener(this);
	}
	
	public final void generateProof(Structure<T> result, Proof proof) {
		mInput.generateProof(result, proof);
		addConstraints(result, proof);
	}
	
	protected abstract void addConstraints(Structure<T> result, Proof proof);
}
