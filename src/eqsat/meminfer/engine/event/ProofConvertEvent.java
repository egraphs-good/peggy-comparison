package eqsat.meminfer.engine.event;

public abstract class ProofConvertEvent<T, D, R>
		extends AbstractProofChainEvent<T, D, R> {
	protected ProofConvertEvent(ProofEvent<T,? extends D> input) {super(input);}
	
	protected boolean mayConvert(D domain) {return canConvert(domain);}
	protected boolean canConvert(D domain) {return true;}
	protected abstract R convert(D domain);
	
	public boolean notify(D parameter) {
		if (canConvert(parameter))
			trigger(convert(parameter));
		return true;
	}
	
	public boolean canUse(D parameter) {
		if (!canConvert(parameter))
			return mayConvert(parameter);
		else
			return listenersCanUse(convert(parameter));
	}
}
