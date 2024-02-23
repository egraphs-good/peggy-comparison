package util;

import java.util.Set;

import util.MultiMap.Entry;

public abstract class AbstractMultiEntry<K, V> implements Entry<K,V> {
	public Set<V> addValue(V value) {
		return addValues(java.util.Collections.singleton(value));
	}
	
	public Set<V> setValue(V values) {
		return setValues(java.util.Collections.singleton(values));
	}
	
    public boolean equals(Object o) {
        return o instanceof Entry && equals((Entry)o);
    }
    
    public boolean equals(Entry e) {
        Object k1 = getKey();
        Object k2 = e.getKey();
        if (k1 == k2 || (k1 != null && k1.equals(k2))) {
            Set<V> v1 = getValues();
            Set v2 = e.getValues();
            if (v1 == v2 || (v1 != null && v1.equals(v2)))
                return true;
        }
        return false;
    }

    public final int hashCode() {
        return (getKey() == null   ? 0 : getKey().hashCode()) ^
               (getValues() == null ? 0 : getValues().hashCode());
    }
    
	public String toString() {return getKey() + "=" + getValues();}
}
