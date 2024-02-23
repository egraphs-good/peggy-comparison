package eqsat.meminfer.engine.event;

public final class EventListenerClosure<P> implements EventListener<Void> {
	private final EventListener<? super P> mListener;
	private final P mParameter;
	
	public EventListenerClosure(EventListener<? super P> listener,
			P parameter) {
		mListener = listener;
		mParameter = parameter;
	}

	public boolean canUse(Void parameter) {
		return mListener.canUse(mParameter);
	}

	public boolean notify(Void parameter) {
		return mListener.notify(mParameter);
	}
}
