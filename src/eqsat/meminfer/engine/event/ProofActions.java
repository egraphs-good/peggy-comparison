package eqsat.meminfer.engine.event;

import eqsat.meminfer.engine.basic.Value;
import eqsat.meminfer.engine.op.OpTerm;
import eqsat.meminfer.engine.op.axiom.AxiomInstance;

public final class ProofActions {
	private static final ProofAction mEmpty = new ProofAction() {
		public void execute(Object parameter) {}
		public void generateProof(AxiomInstance instance) {}
	};
	
	public static <O, T extends OpTerm<O,T,V>, V extends Value<T,V>>
			ProofAction<O,T,V> empty() {
		return mEmpty;
	}
	
	public static <O, T extends OpTerm<O,T,V>, V extends Value<T,V>>
			ProofAction<O,T,V> sequence(
			final ProofAction<O,T,V> first, final ProofAction<O,T,V> second) {
		return new ProofAction<O,T,V>() {
			public void execute(AxiomInstance<O,T,V> parameter) {
				first.execute(parameter);
				second.execute(parameter);
			}
			public void generateProof(AxiomInstance<O,T,V> instance) {
				first.generateProof(instance);
				second.generateProof(instance);
			}
		};
	}
	
	public static <O, T extends OpTerm<O,T,V>, V extends Value<T,V>>
			ProofAction<O,T,V> sequence(
			final ProofAction<O,T,V>... actions) {
		return new ProofAction<O,T,V>() {
			public void execute(AxiomInstance<O,T,V> parameter) {
				for (ProofAction<O,T,V> action : actions)
					action.execute(parameter);
			}
			public void generateProof(AxiomInstance<O,T,V> instance) {
				for (ProofAction<O,T,V> action : actions)
					action.generateProof(instance);
			}
		};
	}
}
