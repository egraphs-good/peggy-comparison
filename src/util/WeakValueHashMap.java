package util;

import java.lang.ref.Reference;
import java.util.HashMap;

public class WeakValueHashMap<K,V> extends WeakValueMap<K,V> {
	public WeakValueHashMap() {super(new HashMap<K,Reference<V>>());}
}
