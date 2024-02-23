package eqsat.meminfer.engine.event;

public abstract class ProofPatternEvent<T, P>
		extends AbstractProofChainEvent<T,P,P> {
	protected ProofPatternEvent(ProofEvent<T,? extends P> input) {super(input);}
	
	protected abstract boolean matches(P pattern);
	protected boolean canMatch(P pattern) {return true;}
	
	public void match(P object) {
		if (matches(object))
			trigger(object);
	}
	
	public boolean notify(P parameter) {
		match(parameter);
		return true;
	}
	
	public boolean canUse(P parameter) {
		return canMatch(parameter) && listenersCanUse(parameter);
	}
}