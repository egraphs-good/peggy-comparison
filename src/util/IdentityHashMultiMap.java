package util;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class IdentityHashMultiMap<K,V> extends BackedMultiMap<K,V> {
	protected <R> Map<K,R> makeKeyMap() {return new IdentityHashMap<K,R>();}
    protected Set<V> makeValueSet() {return new IdentityHashSet<V>();}
    protected Set<V> makeValueSet(Collection<? extends V> values) {
    	return new IdentityHashSet<V>(values);
    }
}
