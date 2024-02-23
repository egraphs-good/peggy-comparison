package eqsat.meminfer.engine.event;

import eqsat.meminfer.engine.basic.Value;
import eqsat.meminfer.engine.op.OpTerm;
import eqsat.meminfer.engine.op.axiom.AxiomInstance;
import util.Action;

public interface ProofAction<O,T extends OpTerm<O,T,V>,V extends Value<T,V>>
		extends Action<AxiomInstance<O,T,V>> {
	void generateProof(AxiomInstance<O,T,V> instance);
}
