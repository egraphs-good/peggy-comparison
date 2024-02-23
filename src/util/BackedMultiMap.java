package util;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import util.mapped.MappedSet;

public abstract class BackedMultiMap<K, V> extends AbstractMultiMap<K,V> {
	protected final Map<K,Box> mMap;
    protected final WeakValueMap<K,Box> mIdleBoxes;
    protected final EntrySet mEntries = new EntrySet();

	protected void saveBox(K key, Box box) {mIdleBoxes.put(key, box);}
    
    protected BackedMultiMap() {
    	mMap = this.<Box>makeKeyMap();
    	mIdleBoxes = new WeakValueMap<K,Box>(this.<Reference<Box>>makeKeyMap());
    }
    protected BackedMultiMap(Map<K,Box> map, Map<K,Reference<Box>> idle) {
    	mMap = map;
    	mIdleBoxes = new WeakValueMap<K,Box>(idle);
    }
    
    protected abstract <R> Map<K,R> makeKeyMap();
    protected <R> Map<K,R> makeKeyMap(Map<? extends K,? extends R> map) {
    	Map<K,R> made = makeKeyMap();
    	made.putAll(map);
    	return made;
    }
    protected abstract Set<V> makeValueSet();
    protected Set<V> makeValueSet(Collection<? extends V> values) {
    	Set<V> made = makeValueSet();
    	made.addAll(values);
    	return made;
    }

	public Set<? extends MultiMap.Entry<K,V>> entrySet() {
		return mEntries;
	}
	
	public Set<? extends Map.Entry<K, V>> entries() {return mEntries.entries();}
	
	public boolean containsKey(Object key) {return mMap.containsKey(key);}

	public Set<V> get(K key) {
		Box box = mMap.get(key);
		return box == null ? getEmptyBox(key) : box;
	}

	public Set<K> keySet() {return mMap.keySet();}

	public Set<V> putValues(K key, Collection<? extends V> valueSet) {
		Box values = mMap.get(key);
		if (values != null)
			values.replaceValues(valueSet);
		else {
			values = getBox(key, valueSet);
			mMap.put(key, values);
		}
		return values;
	}

	public Set<V> removeKey(K key) {
		Box box = mMap.remove(key);
		return box == null ? null : box.clearSet();
	}

	public Collection<? extends Set<V>> valueSets() {return mMap.values();}

    /** Returns the idle box for the key if it exists, otherwise null */
    protected Box getBox(K key) {return mIdleBoxes.get(key);}
    
    /** Returns the idle box for the key with its values set or,
     * if no such box exists,
     * constructs a new box with appropriate key and values
     */
    protected Box getBox(K key, Collection<? extends V> values) {
		Box box = getBox(key);
		if (box == null)
			box = new Box(key, values);
		else
			box.replaceValues(values);
		return box;
	}

    /** Returns the idle box for the key with no values or,
     * if no such box exists,
     * constructs a new box with appropriate key and no values
     */
    protected Box getEmptyBox(K key) {
		Box box = getBox(key);
		if (box == null)
			box = new Box(key);
		return box;
	}

    protected class Box implements Set<V>, Cloneable, Serializable {
		private static final long serialVersionUID = -2636670372336725744L;
		
		private final K mKey;
		private Set<V> mSet;

		public Box(K key) {
			mKey = key;
			mSet = null;
			saveBox(mKey, this);
		}
		public Box(K key, V value) {
			mKey = key;
			mSet = makeValueSet();
			mSet.add(value);
		}
		public Box(K key, Collection<? extends V> values) {
			mKey = key;
			mSet = makeValueSet(values);
		}

		public boolean add(V value) {
			if (mSet == null) {
				mMap.put(mKey, this);
				mSet = makeValueSet();
			}
			return mSet.add(value);
		}

		public boolean addAll(Collection<? extends V> values) {
			if (!values.isEmpty()) {
				if (mSet == null) {
					mMap.put(mKey, this);
					mSet = makeValueSet();
				}
				return mSet.addAll(values);
			}
			return false;
		}

		protected void replaceValue(V value) {
			if (mSet == null)
				mSet = makeValueSet();
			else
				mSet.clear();
			mSet.add(value);
		}

		protected void replaceValues(Collection<? extends V> values) {
			if (mSet == null)
				mSet = makeValueSet(values);
			else {
				mSet.clear();
				mSet.addAll(values);
			}
		}

		public void clear() {
			if (mSet != null)
				mMap.remove(mKey);
			mSet.clear();
			mSet = null;
		}

		public boolean contains(Object value) {
			return mSet != null && mSet.contains(value);
		}

		public boolean containsAll(Collection<?> values) {
			return mSet == null ? values.isEmpty() : mSet.containsAll(values);
		}

		public boolean equals(Object that) {
			return mSet == null ? that instanceof Set && ((Set)that).isEmpty()
					: mSet.equals(that);
		}

		public int hashCode() {return mSet == null ? 0 : mSet.hashCode();}

		public boolean isEmpty() {return mSet == null;}

		public Iterator<V> iterator() {
			return mSet == null ? Collections.<V>emptySet().iterator()
					: new Iterator<V>() {
				Iterator<V> mIterator = mSet.iterator();

				public V next() {return mIterator.next();}
				public boolean hasNext() {return mIterator.hasNext();}

				public void remove() {
					if (size() == 1) {
						mSet = null;
						mMap.remove(mKey);
						saveBox(mKey, Box.this);
					}
					mIterator.remove();
				}
			};
		}

		public boolean remove(Object value) {
			if (mSet != null) {
				boolean removed = mSet.remove(value);
				if (removed && mSet.isEmpty()) {
					mSet = null;
					mMap.remove(mKey);
					saveBox(mKey, Box.this);
				}
				return removed;
			}
			return false;
		}

		public boolean removeAll(Collection<?> values) {
			if (mSet != null) {
				boolean removed = mSet.removeAll(values);
				if (removed && mSet.isEmpty()) {
					mSet = null;
					mMap.remove(mKey);
					saveBox(mKey, Box.this);
				}
				return removed;
			}
			return false;
		}

		public boolean retainAll(Collection<?> c) {
			if (mSet != null) {
				boolean removed = mSet.retainAll(c);
				if (removed && mSet.isEmpty()) {
					mSet = null;
					mMap.remove(mKey);
					saveBox(mKey, Box.this);
				}
				return removed;
			}
			return false;
		}

		public int size() {return mSet == null ? 0 : mSet.size();}

		public Object[] toArray() {
			return mSet == null ? new Object[0] : mSet.toArray();
		}

		public <T> T[] toArray(T[] array) {
			return mSet == null ? Collections.<T>emptySet().toArray(array)
					: mSet.toArray(array);
		}

		public HashSet<V> clone() {return new HashSet<V>(this);}

		public String toString() {return mSet == null ? "[]" : mSet.toString();}

		public Set<V> clearSet() {
			saveBox(mKey, this);
			Set<V> values = mSet;
			mSet = null;
			return values;
		}
	}
	
	protected class EntrySet extends MappedSet<Map.Entry<K,Box>,Entry<K,V>>
			implements Set<Entry<K,V>> {
		private final Set<Map.Entry<K,V>> mEntries
				= new AbstractSet<Map.Entry<K,V>>() {

			public boolean add(Map.Entry<K,V> e) {
				if (contains(e))
					return false;
				addValue(e.getKey(), e.getValue());
				return true;
			}

			public void clear() {BackedMultiMap.this.clear();}

			public boolean contains(Object entry) {
				return entry instanceof Map.Entry
						&& contains((Map.Entry<K,V>)entry);
			}

			public boolean contains(Map.Entry<K,V> entry) {
				return entry != null
						&& containsEntry(entry.getKey(), entry.getValue());
			}

			public boolean isEmpty() {return BackedMultiMap.this.isEmpty();}

			public Iterator<Map.Entry<K,V>> iterator() {
				return util.Collections.concatonateIterators(
						util.Collections.mapIterator(entrySet().iterator(),
						new Function<Entry<K,V>,Iterator<Map.Entry<K,V>>>() {
					public Iterator<Map.Entry<K,V>> get(final Entry<K,V> entry){
						return util.Collections.mapIterator(
								entry.getValues().iterator(),
								new Function<V,Map.Entry<K,V>>() {
							public Map.Entry<K,V> get(final V original) {
								return new Map.Entry<K,V>() {
									private V mValue = original;
									public K getKey() {return entry.getKey();}
									public V getValue() {return mValue;}
									public V setValue(V value) {
										V old = mValue;
										entry.getValues().remove(old);
										mValue = value;
										entry.addValue(mValue);
										return old;
									}
								};
							}
						});
					}
				}));
			}

			public boolean remove(Object entry) {
				return !(entry instanceof Map.Entry)
						&& remove((Map.Entry<K,V>)entry);
			}

			public boolean remove(Map.Entry<K,V> entry) {
				if (contains(entry)) {
					removeEntry(entry.getKey(), entry.getValue());
					return true;
				} else
					return false;
			}

			public int size() {
				int size = 0;
				for (Entry<K,V> entry : entrySet())
					size += entry.getValues().size();
				return size;
			}
		};
		
		protected Collection<Map.Entry<K,Box>> getWrapped() {
			return mMap.entrySet();
		}

		protected Entry<K,V> map(final Map.Entry<K,Box> entry) {
			return entry == null ? null : new AbstractMultiEntry<K,V>() {
				public Set<V> addValues(Collection<? extends V> values) {
					entry.getValue().addAll(values);
					return entry.getValue();
				}

				public K getKey() {return entry.getKey();}

				public Set<V> getValues() {return entry.getValue();}

				public Set<V> setValues(Collection<? extends V> values) {
					entry.getValue().replaceValues(values);
					return entry.getValue();
				}
			};
		}
		
		public boolean add(Entry<K,V> entry) {
			boolean changed = mMap.containsKey(entry.getKey());
			putValues(entry.getKey(), entry.getValues());
			return changed;
		}

		public void clear() {BackedMultiMap.this.clear();}

		public boolean contains(Object entry) {
			return entry instanceof Entry && contains((Entry<K,V>)entry);
		}

		public boolean contains(Entry<K,V> entry) {
			if (entry == null || !containsKey(entry.getKey()))
				return false;
			Set<V> values = get(entry.getKey());
			return values == null ? entry.getValues() == null
					: values.equals(entry.getValues());
		}

		public boolean isEmpty() {return BackedMultiMap.this.isEmpty();}

		public boolean remove(Object entry) {
			return !(entry instanceof Entry) && remove((Entry<K,V>)entry);
		}

		public boolean remove(Entry<K,V> entry) {
			if (entry == null || !containsKey(entry.getKey()))
				return false;
			Set<V> value = get(entry.getKey());
			if (value == null ? entry.getValues() != null
					: !value.equals(entry.getValues()))
				return false;
			BackedMultiMap.this.removeKey(entry.getKey());
			return true;
		}

		public int size() {return BackedMultiMap.this.numKeys();}
		
		protected Set<Map.Entry<K,V>> entries() {return mEntries;}
	}
}
