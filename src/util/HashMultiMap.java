package util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HashMultiMap<K,V> extends BackedMultiMap<K,V> {
	protected <R> Map<K,R> makeKeyMap() {return new HashMap<K,R>();}
	protected Set<V> makeValueSet() {return new HashSet<V>();}
	protected Set<V> makeValueSet(Collection<? extends V> values) {
		return new HashSet<V>(values);
	}
}
