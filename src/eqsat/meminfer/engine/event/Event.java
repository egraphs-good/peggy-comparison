package eqsat.meminfer.engine.event;

import java.util.Collection;

public interface Event<P> {
	public void trigger(P parameter);
	public void addListener(EventListener<? super P> listener);
	public void addListeners(
			Collection<? extends EventListener<? super P>> listeners);
	//public Collection<? extends EventListener<? super P>> getListeners();
}
