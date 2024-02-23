package util;

public class StandardBasedMap<K extends Based<K>, V> extends BasedMap<K,V> {
	public StandardBasedMap(Basis<K> basis) {super(basis);}

	public boolean containsKey(Object element) {
		K key = mBasis.getElement(element);
		return key != null && mMap.containsKey(key.getBasisIndex());
	}

	public V get(Object element) {
		K key = mBasis.getElement(element);
		return key == null ? null : mMap.get(key.getBasisIndex());
	}

	public V put(K key, V value) {
		return mMap.put(key.getBasisIndex(), value);
	}

	public V remove(Object element) {
		K key = mBasis.getElement(element);
		return key == null ? null : mMap.remove(key.getBasisIndex());
	}
}
