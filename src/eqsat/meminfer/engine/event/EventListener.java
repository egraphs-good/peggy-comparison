package eqsat.meminfer.engine.event;

public interface EventListener<P> {
	/** Return true to keep listening. */
	public boolean notify(P parameter);
	public boolean canUse(P parameter);
}
