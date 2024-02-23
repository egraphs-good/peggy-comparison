package eqsat.meminfer.engine.basic;

public final class RepresentativeConstructor<V> {
	private final ValueManager<V> mManager;
	
	public RepresentativeConstructor(ValueManager<V> manager) {
		mManager = manager;
	}

	public ValueManager<V> getValueManager() {return mManager;}
}
