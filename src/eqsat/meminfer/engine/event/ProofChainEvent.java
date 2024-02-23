package eqsat.meminfer.engine.event;

public interface ProofChainEvent<T, I, O>
		extends ChainEvent<I,O>, ProofEvent<T,O> {
}
