package eqsat.meminfer.engine.event;

public abstract class ConvertEvent<S, T> extends AbstractChainEvent<S,T> {
	protected boolean mayConvert(S source) {return canConvert(source);}
	protected boolean canConvert(S source) {return true;}
	protected abstract T convert(S source);
	
	public boolean notify(S parameter) {
		if (canConvert(parameter))
			trigger(convert(parameter));
		return true;
	}
	
	public boolean canUse(S parameter) {
		if (!canConvert(parameter))
			return mayConvert(parameter);
		else
			return listenersCanUse(convert(parameter));
	}
}
