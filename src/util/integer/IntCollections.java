package util.integer;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;

import util.integer.mapped.MappedIntCollection;
import util.integer.mapped.MappedIntIterable;
import util.integer.mapped.MappedIntIterator;
import util.integer.mapped.MappedIntList;
import util.integer.mapped.MappedIntListIterator;
import util.integer.mapped.MappedIntSet;

public final class IntCollections {
	private static final IntIterator mEmptyIterator = new IntIterator() {
		public int nextInt() {throw new IllegalStateException();}
		public boolean hasNext() {return false;}
		public Integer next() {throw new IllegalStateException();}
		public void remove() {throw new IllegalStateException();}
	};
	
	private static final IncreasingIntSet mEmptySet
			= new AbstractIncreasingIntSet() {
		public boolean add(int element) {
			throw new UnsupportedOperationException();
		}
		public boolean equals(IntSet that) {return that.isEmpty();}
		public boolean contains(int element) {return false;}
		public boolean equals(IntCollection that) {return that.isEmpty();}
		public boolean intersects(IntCollection that) {return false;}
		public boolean removeInt(int o) {
			throw new UnsupportedOperationException();
		}
		public int[] toIntArray() {return new int[0];}
		public IntIterator iterator() {return IntCollections.iterator();}
		public boolean add(Integer element) {
			throw new UnsupportedOperationException();
		}
		public void clear() {
			throw new UnsupportedOperationException();
		}
		public boolean contains(Object o) {return false;}
		public boolean isEmpty() {return true;}
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}
		public int size() {return 0;}
		public Integer[] toArray() {return new Integer[0];}
		public int countAfter(int element) {return 0;}
		public int countBefore(int element) {return 0;}
		public int countBetween(int lower, int upper) {return 0;}
		public int firstAfter(int element) {
			throw new NoSuchElementException();
		}
		public int firstInt() {
			throw new NoSuchElementException();
		}
		public IntIterator iterator(int start) {
			return IntCollections.iterator();
		}
		public int lastBefore(int element) {
			throw new NoSuchElementException();
		}
		public int lastInt() {
			throw new NoSuchElementException();
		}
	};
	
	private static final IntComparator mStandardComparator
			= new AbstractIntComparator() {
		public int compare(int o1, int o2) {
			return o1 - o2;
		}
		
		public int getMaximum() {return Integer.MAX_VALUE;}
		public int getMinimum() {return Integer.MIN_VALUE;}

		public int getNextLargest(int o) {
			if (o == getMaximum())
				throw new IllegalArgumentException();
			return o + 1;
		}

		public int getNextSmallest(int o) {
			if (o == getMinimum())
				throw new IllegalArgumentException();
			return o - 1;
		}

		public int max(int left, int right) {return Math.max(left, right);}
		public int min(int left, int right) {return Math.min(left, right);}
	};
	
	public static IntIterator iterator() {return mEmptyIterator;}
	
	public static IntComparator increasingComparator() {
		return mStandardComparator;
	}
	
	public static IntIterator iterator(final int mValue) {
		return new AbstractIntIterator() {
			private boolean mMoved = false;
			public boolean hasNext() {return !mMoved;}
			public int nextInt() {
				if (mMoved)
					throw new IllegalStateException();
				mMoved = true;
				return mValue;
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public static IncreasingIntSet set(final int mValue) {
		return new AbstractIncreasingIntSet() {
			public boolean add(int element) {
				throw new UnsupportedOperationException();
			}
			public boolean equals(IntSet that) {
				return that.size() == 1 && that.iterator().nextInt() == mValue;
			}
			public boolean contains(int element) {return mValue == element;}
			public boolean intersects(IntCollection that) {
				return that.contains(mValue);
			}
			public boolean removeInt(int element) {
				throw new UnsupportedOperationException();
			}
			public int[] toIntArray() {return new int[]{mValue};}
			public IntIterator iterator() {
				return IntCollections.iterator(mValue);
			}
			public void clear() {
				throw new UnsupportedOperationException();
			}
			public boolean isEmpty() {return false;}
			public int size() {return 1;}
			public Integer[] toArray() {
				return new Integer[]{new Integer(mValue)};
			}
			public int countAfter(int element) {
				return element <= mValue ? 1 : 0;
			}
			public int countBefore(int element) {
				return element >= mValue ? 1 : 0;
			}
			public int countBetween(int lower, int upper) {
				return lower <= mValue && mValue <= upper ? 1 : 0;
			}
			public int firstAfter(int element) {
				if (element > mValue)
					throw new NoSuchElementException();
				return mValue;
			}
			public int firstInt() {
				return mValue;
			}
			public IntIterator iterator(int start) {
				return start <= mValue ? IntCollections.iterator(mValue)
						: IntCollections.iterator();
			}
			public int lastBefore(int element) {
				if (element < mValue)
					throw new NoSuchElementException();
				return mValue;
			}
			public int lastInt() {return mValue;}
		};
	}
	
	public static <V> IncreasingIntMap<V> map(final int mKey, final V mValue) {
		return new AbstractIncreasingIntMap<V>() {
			public boolean isEmpty() {return false;}
			public int size() {return 1;}
			public V get(int key) {return mKey == key ? mValue : null;}
			public boolean containsKey(int key) {return mKey == key;}
			public boolean containsValue(Object value) {
				return mValue == null ? value == null : mValue.equals(value);
			}
			public boolean equals(IntMap that) {
				if (that.size() != 1)
					return false;
				IntMap<?> map = that;
				IntMap.Entry entry = map.intEntrySet().iterator().next();
				return entry.getIntKey() == mKey &&
						(mValue == null ? entry.getValue() == null
						: mValue.equals(entry.getValue()));
			}
			public Set<? extends IntMap.Entry<V>> intEntrySet() {
				return Collections.singleton(new AbstractIntMap.Entry<V>() {
					public int getIntKey() {return mKey;}
					public V getValue() {return mValue;}
					public V setValue(V value) {
						throw new UnsupportedOperationException();
					}
				});
			}
			public IncreasingIntSet keySet() {return set(mKey);}
			public Collection<V> values() {
				return Collections.singleton(mValue);
			}
			public V put(int key, V value) {
				throw new UnsupportedOperationException();
			}
			public V remove(int key) {
				throw new UnsupportedOperationException();
			}
			public void clear() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public static <V> IncreasingIntMap<V> map(final IncreasingIntSet mKeys,
			final V mValue) {
		return new AbstractIncreasingIntMap<V>() {
			public boolean isEmpty() {return mKeys.isEmpty();}
			public int size() {return mKeys.size();}
			public V get(int key) {return mKeys.contains(key) ? mValue : null;}
			public boolean containsKey(int key) {return mKeys.contains(key);}
			public boolean containsValue(Object value) {
				return mValue == null ? value == null : mValue.equals(value);
			}
			public boolean equals(IntMap that) {
				if (that.size() != mKeys.size())
					return false;
				IntMap<?> map = that;
				for (IntMap.Entry<?> entry : map.intEntrySet())
					if (!mKeys.contains(entry.getIntKey())
							|| (mValue == null ? entry.getValue() != null
							: !mValue.equals(entry.getValue())))
						return false;
				return true;
			}
			public Set<? extends IntMap.Entry<V>> intEntrySet() {
				return new MappedIntSet<IntMap.Entry<V>>() {
					public IntSet getWrapped() {return mKeys;}
					public IntMap.Entry<V> map(final int key) {
						return new AbstractIntMap.Entry<V>() {
							public int getIntKey() {return key;}
							public V getValue() {return mValue;}
							public V setValue(V value) {
								throw new UnsupportedOperationException();
							}
						};
					}
				};
			}
			public IncreasingIntSet keySet() {return mKeys;}
			public Collection<V> values() {
				return Collections.nCopies(mKeys.size(), mValue);
			}
			public V put(int key, V value) {
				throw new UnsupportedOperationException();
			}
			public V remove(int key) {
				throw new UnsupportedOperationException();
			}
			public void clear() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public static IncreasingIntSet set() {return mEmptySet;}
	
	public static SortedIntSet sortedSet(final IntComparator comparator) {
		return new SortedIntSet() {
			public IntComparator comparator() {return comparator;}
			public int countAfter(int element) {return 0;}
			public int countBefore(int element) {return 0;}
			public int countBetween(int lower, int upper) {return 0;}
			public int firstAfter(int element) {
				throw new NoSuchElementException();
			}
			public int firstInt() {
				throw new NoSuchElementException();
			}
			public SortedIntSet headSet(int toElement) {return this;}
			public IntIterator iterator() {
				return IntCollections.iterator();
			}
			public IntIterator iterator(int start) {
				return IntCollections.iterator();
			}
			public int lastBefore(int element) {
				throw new NoSuchElementException();
			}
			public int lastInt() {
				throw new NoSuchElementException();
			}
			public SortedIntSet subSet(int fromElement, int toElement) {
				return this;
			}
			public SortedIntSet tailSet(int fromElement) {return this;}
			public Integer first() {
				throw new NoSuchElementException();
			}
			public SortedSet<Integer> headSet(Integer toElement) {return this;}
			public Integer last() {
				throw new NoSuchElementException();
			}
			public SortedSet<Integer> subSet(Integer fromElement,
					Integer toElement) {
				return this;
			}
			public SortedSet<Integer> tailSet(Integer fromElement) {
				return this;
			}
			public boolean add(Integer o) {
				throw new UnsupportedOperationException();
			}
			public boolean addAll(Collection<? extends Integer> c) {
				throw new UnsupportedOperationException();
			}
			public void clear() {
				throw new UnsupportedOperationException();
			}
			public boolean contains(Object o) {return false;}
			public boolean containsAll(Collection<?> c) {return c.isEmpty();}
			public boolean isEmpty() {return true;}
			public boolean remove(Object o) {return false;}
			public boolean removeAll(Collection<?> c) {return false;}
			public boolean retainAll(Collection<?> c) {return false;}
			public int size() {return 0;}
			public Object[] toArray() {return new Object[0];}
			public <T> T[] toArray(T[] a) {
				a[0] = null;
				return a;
			}
			public boolean add(int o) {
				throw new UnsupportedOperationException();
			}
			public boolean addAll(IntCollection c) {
				throw new UnsupportedOperationException();
			}
			public boolean equals(IntSet o) {return o != null && o.isEmpty();}
			public boolean removeAll(IntCollection c) {return false;}
			public boolean retainAll(IntCollection c) {return false;}
			public boolean contains(int o) {return false;}
			public boolean containsAll(IntCollection c) {return c.isEmpty();}
			public boolean intersects(IntCollection that) {return false;}
			public boolean equals(IntCollection collection) {
				return collection != null && collection instanceof IntSet
						&& collection.isEmpty();
			}
			public boolean removeInt(int o) {
				throw new UnsupportedOperationException();
			}
			public int[] toArray(int[] a) {return a;}
			public int[] toArray(int[] a, int end) {
				if (a.length > 0)
					a[0] = end;
				return a;
			}
			public int[] toIntArray() {return new int[0];}
			public String toString() {return "[]";}
		};
	}
	
	public static <E> IncreasingIntMap<E> map() {
		return new AbstractIncreasingIntMap<E>() {
			public boolean containsKey(int key) {return false;}
			public boolean equals(IntMap that) {return that.isEmpty();}
			public E get(int key) {return null;}
			public Set<? extends IntMap.Entry<E>> intEntrySet() {
				return Collections.<IntMap.Entry<E>>emptySet();
			}
			public IncreasingIntSet keySet() {return set();}
			public E put(int key, E value) {
				throw new UnsupportedOperationException();
			}
			public E remove(int key) {
				throw new UnsupportedOperationException();
			}
			public Collection<E> values() {return Collections.<E>emptySet();}
			public void clear() {
				throw new UnsupportedOperationException();
			}
			public boolean containsKey(Object key) {return false;}

			public boolean containsValue(Object value) {return false;}
			public Set<Map.Entry<Integer,E>> entrySet() {
				return Collections.<Map.Entry<Integer,E>>emptySet();
			}
			public E get(Object key) {return null;}
			public boolean isEmpty() {return true;}
			public E put(Integer key, E value) {
				throw new UnsupportedOperationException();
			}
			public E remove(Object key) {
				throw new UnsupportedOperationException();
			}
			public int size() {return 0;}
		};
	}
	
	public static <R> Iterator<R> mapIterator(final IntIterator iterator,
			final IntFunction<? extends R> map) {
		return new MappedIntIterator<R>() {
			protected IntIterator getWrapped() {return iterator;}
			protected R map(int domain) {return map.get(domain);}
		};
	}
	
	public static <R> ListIterator<R> mapListIterator(
			final IntListIterator iterator,
			final IntFunction<? extends R> map) {
		return new MappedIntListIterator<R>() {
			protected IntListIterator getWrapped() {return iterator;}
			protected R map(int domain) {return map.get(domain);}
		};
	}
	
	public static <R> Iterable<R> mapIterable(final IntIterable iterable,
			final IntFunction<? extends R> map) {
		return new MappedIntIterable<R>() {
			protected IntIterable getWrapped() {return iterable;}
			protected R map(int domain) {return map.get(domain);}
		};
	}
	
	public static <R> Collection<R> mapCollection(
			final IntCollection collection,
			final IntFunction<? extends R> map) {
		return new MappedIntCollection<R>() {
			protected IntCollection getWrapped() {return collection;}
			protected R map(int domain) {return map.get(domain);}
		};
	}
	
	public static <R> List<R> mapList(final IntList list,
			final IntFunction<? extends R> map) {
		return new MappedIntList<R>() {
			protected IntList getWrapped() {return list;}
			protected R map(int domain) {return map.get(domain);}
		};
	}
	
	public static <R> Set<R> mapSet(final IntSet set,
			final IntFunction<? extends R> injectiveMap) {
		return new MappedIntSet<R>() {
			protected IntSet getWrapped() {return set;}
			protected R map(int domain) {return injectiveMap.get(domain);}
		};
	}
	
	public static SortedIntSet createBoundedBitSet(int max) {
		if (max < 0)
			return sortedSet(increasingComparator());
		else if (max < 32)
			return new Bit32IntSet();
		else if (max < 64)
			return new Bit64IntSet();
		else
			return new BitIntSet();
	}
}
