package eqsat.meminfer.engine.event;

public abstract class PatternEvent<P> extends AbstractChainEvent<P,P> {
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