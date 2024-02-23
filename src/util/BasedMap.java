package util;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import util.integer.AbstractIntFunction;
import util.integer.ArrayIntMap;
import util.integer.IntCollections;

public class BasedMap<K, V> extends AbstractMap<K,V> {
	protected final Basis<K> mBasis;
	protected final ArrayIntMap<V> mMap = new ArrayIntMap<V>();
	
	public BasedMap(Basis<K> basis) {
		if (basis == null)
			throw new NullPointerException();
		mBasis = basis;
	}
	
	public Set<Map.Entry<K, V>> entrySet() {
		return IntCollections.mapSet(mMap.keySet(),
				new AbstractIntFunction<Map.Entry<K, V>>() {
			public java.util.Map.Entry<K,V> get(final int key) {
				return new Map.Entry<K,V>() {
					public K getKey() {return mBasis.getElement(key);}

					public V getValue() {return mMap.get(key);}

					public V setValue(V value) {return mMap.put(key, value);}
				};
			}
		});
	}

	public void clear() {mMap.clear();}

	public boolean containsKey(Object element) {
		K key = mBasis.getElement(element);
		return key != null && mMap.containsKey(mBasis.getIndex(key));
	}

	public V get(Object element) {
		K key = mBasis.getElement(element);
		return key == null ? null : mMap.get(mBasis.getIndex(key));
	}

	public boolean isEmpty() {return mMap.isEmpty();}

	public Set<K> keySet() {
		return IntCollections.mapSet(mMap.keySet(),
				new AbstractIntFunction<K>() {
			public K get(int parameter) {return mBasis.getElement(parameter);}
		});
	}

	public V put(K key, V value) {
		return mMap.put(mBasis.getIndex(key), value);
	}

	public V remove(Object element) {
		K key = mBasis.getElement(element);
		return key == null ? null : mMap.remove(mBasis.getIndex(key));
	}

	public int size() {return mMap.size();}

	public Collection<V> values() {return mMap.values();}
}
