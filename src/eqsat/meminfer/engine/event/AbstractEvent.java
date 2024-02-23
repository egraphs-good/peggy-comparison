package eqsat.meminfer.engine.event;

import java.util.Collection;
import java.util.Iterator;

import util.ArrayCollection;

public class AbstractEvent<P> implements Event<P> {
	protected final Collection<EventListener<? super P>> mListeners
			= new ArrayCollection<EventListener<? super P>>();
	
	public void trigger(P parameter) {
		for (Iterator<EventListener<? super P>> listeners
				= mListeners.iterator(); listeners.hasNext(); )
			if (!listeners.next().notify(parameter))
				listeners.remove();
	}
	
	public void addListener(EventListener<? super P> listener) {
		mListeners.add(listener);
	}
	
	public void addListeners(Collection<? extends EventListener<? super P>>
			listeners) {
		mListeners.addAll(listeners);
	}
	
	protected Collection<? extends EventListener<? super P>> getListeners() {
		return mListeners;
	}
	
	protected boolean listenersCanUse(P parameter) {
		for (EventListener<? super P> listener : getListeners())
			if (listener.canUse(parameter))
				return true;
		return false;
	}
}