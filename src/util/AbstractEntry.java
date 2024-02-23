package util;

import java.util.Map.Entry;

public abstract class AbstractEntry<K, V> implements Entry<K,V> {
    public boolean equals(Object o) {
        return o instanceof Entry && equals((Entry)o);
    }
    
    public boolean equals(Entry e) {
        Object k1 = getKey();
        Object k2 = e.getKey();
        if (k1 == k2 || (k1 != null && k1.equals(k2))) {
            Object v1 = getValue();
            Object v2 = e.getValue();
            if (v1 == v2 || (v1 != null && v1.equals(v2)))
                return true;
        }
        return false;
    }

    public final int hashCode() {
        return (getKey() == null   ? 0 : getKey().hashCode()) ^
               (getValue() == null ? 0 : getValue().hashCode());
    }
    
	public String toString() {return getKey() + "=" + getValue();}
}
