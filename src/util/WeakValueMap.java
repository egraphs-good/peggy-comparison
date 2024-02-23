package util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import util.mapped.MappedCollection;
import util.mapped.MappedSet;

public class WeakValueMap<K, V> extends AbstractMap<K,V> {
	protected final ReferenceQueue<V> mQueue = new ReferenceQueue<V>();
	private final Map<K,Reference<V>> mMap;
	protected final Set<Entry<K,V>> mEntries = new EntrySet();
	protected final Collection<V> mValues = new ValueSet();
	
	public WeakValueMap(Map<K,Reference<V>> map) {
		if (!map.isEmpty())
			throw new IllegalArgumentException();
		mMap = map;
	}
	
	protected Map<K,Reference<V>> getMap() {
		Reference<? extends V> ref;
		while ((ref = mQueue.poll()) != null)
			mMap.values().remove(ref);
		return mMap;
	}

	public void clear() {mMap.clear();}

	public boolean containsKey(Object key) {return getMap().containsKey(key);}

	public Set<Entry<K,V>> entrySet() {return mEntries;}

	public V get(Object key) {
		return getValue(getMap().get(key));
	}

	public boolean isEmpty() {return getMap().isEmpty();}

	public Set<K> keySet() {return getMap().keySet();}

	public V put(K key, V value) {
		return getValue(getMap().put(key, makeReference(key, value)));
	}

	public V remove(Object key) {
		return getValue(getMap().remove(key));
	}

	public int size() {return getMap().size();}

	public Collection<V> values() {return mValues;}
	
	protected Reference<V> makeReference(final K key, V value) {
		return value == null ? null : new WeakReference<V>(value, mQueue);
	}
	
	protected V getValue(Reference<V> ref) {
		return ref == null ? null : ref.get();
	}
	
	protected class EntrySet
			extends MappedSet<Entry<K,Reference<V>>,Entry<K,V>>
			implements Set<Entry<K,V>> {
		protected Collection<Entry<K,Reference<V>>> getWrapped() {
			return getMap().entrySet();
		}

		protected Entry<K,V> map(final Entry<K,Reference<V>> entry) {
			return entry == null ? null : new AbstractEntry<K,V>() {
				public K getKey() {return entry.getKey();}

				public V getValue() {
					return WeakValueMap.this.getValue(entry.getValue());
				}

				public V setValue(V value) {
					return WeakValueMap.this.getValue(entry.setValue(
							makeReference(entry.getKey(), value)));
				}
			};
		}
		
		public boolean add(Entry<K,V> entry) {
			boolean changed = getMap().containsKey(entry.getKey());
			put(entry.getKey(), entry.getValue());
			return changed;
		}

		public void clear() {WeakValueMap.this.clear();}

		public boolean contains(Object entry) {
			return entry instanceof Entry && contains((Entry)entry);
		}

		public boolean contains(Entry entry) {
			if (entry == null || !containsKey(entry.getKey()))
				return false;
			V value = get(entry.getKey());
			return value == null ? entry.getValue() == null
					: value.equals(entry.getValue());
		}

		public boolean isEmpty() {return WeakValueMap.this.isEmpty();}

		public boolean remove(Object entry) {
			return !(entry instanceof Entry) && remove((Entry)entry);
		}

		public boolean remove(Entry entry) {
			if (entry == null || !containsKey(entry.getKey()))
				return false;
			V value = get(entry.getKey());
			if (value == null ? entry.getValue() != null
					: !value.equals(entry.getValue()))
				return false;
			WeakValueMap.this.remove(entry.getKey());
			return true;
		}

		public int size() {return WeakValueMap.this.size();}
	}
	
	protected class ValueSet extends MappedCollection<Reference<V>,V> {
		protected Collection<Reference<V>> getWrapped() {
			return getMap().values();
		}
		protected V map(Reference<V> ref) {return getValue(ref);}
	}
}
