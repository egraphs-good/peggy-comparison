package util;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BasedMultiMap<K, V> extends BackedMultiMap<K,V> {
	protected final Basis<K> mBasis;
	
	public BasedMultiMap(Basis<K> basis) {
		this(basis, new BasedMap<K,Box>(basis),
				new BasedMap<K,Reference<Box>>(basis));
	}
	public BasedMultiMap(Basis<K> basis, Map<K,Box> map,
			Map<K,Reference<Box>> idle) {
		super(map, idle);
		if (basis == null)
			throw new NullPointerException();
		mBasis = basis;
	}

	protected <R> Map<K,R> makeKeyMap() {return new BasedMap<K,R>(mBasis);}
	protected Set<V> makeValueSet() {return new HashSet<V>();}
	protected Set<V> makeValueSet(Collection<? extends V> values) {
		return new HashSet<V>(values);
	}
}
