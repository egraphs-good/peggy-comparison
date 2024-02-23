package util.integer;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ArrayIntMap<E> extends AbstractIncreasingIntMap<E> {
	protected E[] mMap;
	protected final IncreasingIntSet mKeys = new BitIntSet();
	
	public ArrayIntMap() {this(10);}
	public ArrayIntMap(int initialSpace) {mMap = makeEmptyMap(initialSpace);}
	public ArrayIntMap(IntMap<? extends E> initial) {this(); putAll(initial);}
	
	public boolean containsKey(int key) {return mKeys.contains(key);}

	public E get(int key) {
		if (key < 0 || key >= mMap.length)
			return null;
		return mMap[key];
	}

	public Set<? extends IntMap.Entry<E>> intEntrySet() {
		return new EntrySet();
	}
	
	protected void ensureCapacity(int index) {
		if (index < mMap.length)
			return;
		E[] map = makeEmptyMap(Math.max(index+1, mMap.length << 1));
		System.arraycopy(mMap, 0, map, 0, mMap.length);
		mMap = map;
	}
	
	protected E[] makeEmptyMap(int size) {
		return (E[])new Object[size];
	}

	public E put(int key, E value) {
		if (key < 0)
			throw new IllegalArgumentException();
		ensureCapacity(key);
		E old = mMap[key];
		mMap[key] = value;
		mKeys.add(key);
		return old;
	}

	public E remove(int key) {
		if (key < 0 || key >= mMap.length)
			return null;
		E old = mMap[key];
		mMap[key] = null;
		mKeys.remove(key);
		return old;
	}

	public int size() {return mKeys.size();}

	public boolean equals(Object that) {
		return that != null &&
				(that instanceof ArrayIntMap ? equals((ArrayIntMap)that)
				: super.equals(that));
	}

	public boolean equals(IntMap that) {
		return that != null &&
				(that instanceof ArrayIntMap ? equals((ArrayIntMap)that)
				: super.equals(that));
	}
	
	public boolean equals(ArrayIntMap that) {
		if (that == null || !mKeys.equals(that.mKeys))
			return false;
		for (IntIterator keys = mKeys.iterator(); keys.hasNext(); ) {
			int key = keys.next();
			E value = mMap[key];
			if (value == null ? that.mMap[key] != null
					: !value.equals(that.mMap[key]))
				return false;
		}
		return true;
	}

	public IncreasingIntSet keySet() {return mKeys;}

	public Collection<E> values() {
		return IntCollections.mapCollection(mKeys,
				new AbstractIntFunction<E>() {
			public E get(int key) {return mMap[key];}
		});
	}

	public void clear() {
		mKeys.clear();
		mMap = makeEmptyMap(10);
	}

	public boolean containsValue(Object that) {
		for (IntIterator keys = mKeys.iterator(); keys.hasNext(); ) {
			E value = mMap[keys.next()];
			if (value == null ? that == null : value.equals(that))
				return true;
		}
		return false;
	}

	public boolean isEmpty() {return mKeys.isEmpty();}
	
	protected class Entry extends AbstractIntMap.Entry<E> {
		protected final int mKey;
		protected E mValue;
		
		public Entry(int key, E value) {mKey = key; mValue = value;}

		public int getIntKey() {return mKey;}
		public E getValue() {return mValue;}

		public E setValue(E value) {
			E old = mValue;
			mValue = value;
			mMap[mKey] = mValue;
			return old;
		}
		
		public boolean equals(Object that) {
			return that instanceof Map.Entry &&
					that instanceof IntMap.Entry ? equals((IntMap.Entry)that)
					: equals((Map.Entry)that);
		}
		
		public boolean equals(IntMap.Entry that) {
			return that != null && mKey == that.getIntKey()
					&& (mValue == null ? that.getValue() == null
					: mValue.equals(that.getValue()));
		}
		
		public boolean equals(Map.Entry that) {
			return that != null && that.getKey() != null
					&& that.getKey() instanceof Integer
					&& mKey == ((Integer)that.getKey()).intValue()
					&& (mValue == null ? that.getValue() == null
					: mValue.equals(that.getValue()));
		}
		
		public int hashCode() {
			return mValue == null ? mKey : mValue.hashCode() ^ mKey;
		}
	}
	
	protected class EntrySet extends AbstractSet<Entry> {
		public Iterator<Entry> iterator() {
			return IntCollections.mapIterator(mKeys.iterator(),
					new AbstractIntFunction<Entry>() {
				public Entry get(int key) {
					return new Entry(key, mMap[key]);
				}
			});
		}

		public int size() {return ArrayIntMap.this.size();}

		public void clear() {ArrayIntMap.this.clear();}

		public boolean contains(Object o) {
			return o instanceof IntMap.Entry && contains((IntMap.Entry)o);
		}
		
		public boolean contains(Map.Entry entry) {
			return entry != null && containsKey(entry.getKey())
					&& (entry.getValue() == null ? get(entry.getKey()) == null
					: entry.getValue().equals(get(entry.getKey())));
		}
		
		public boolean contains(IntMap.Entry entry) {
			return entry != null && containsKey(entry.getIntKey())
					&& (entry.getValue() == null
					? mMap[entry.getIntKey()] == null
					: entry.getValue().equals(mMap[entry.getIntKey()]));
		}

		public boolean isEmpty() {return ArrayIntMap.this.isEmpty();}
	}
}
