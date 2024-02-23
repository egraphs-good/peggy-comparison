package util;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public final class Collections {
	public static <D,R> Iterator<R> mapIterator(
			final Iterator<? extends D> iterator,
			final Function<? super D, ? extends R> map) {
		return new MappedIterator<D,R>() {
			public Iterator<? extends D> getWrapped() {return iterator;}
			public Function<? super D, ? extends R> getMap() {return map;}
		};
	}
	
	public static <D,R> ListIterator<R> mapListIterator(
			final ListIterator<? extends D> iterator,
			final Function<? super D, ? extends R> map) {
		return new MappedListIterator<D,R>() {
			public ListIterator<? extends D> getWrapped() {return iterator;}
			public Function<? super D, ? extends R> getMap() {return map;}
		};
	}
	
	public static <D,R> Iterable<R> mapIterable(
			final Iterable<? extends D> iterable,
			final Function<? super D,? extends R> map) {
		return new MappedIterable<D,R>() {
			protected Iterable<? extends D> getWrapped() {return iterable;}
			protected Function<? super D, ? extends R> getMap() {return map;}
		};
	}
	
	public static <D,R> Collection<R> mapCollection(
			final Collection<? extends D> collection,
			final Function<? super D,? extends R> map) {
		return new MappedCollection<D,R>() {
			protected Collection<? extends D> getWrapped() {return collection;}
			protected Function<? super D, ? extends R> getMap() {return map;}
		};
	}
	
	public static <D,R> List<R> mapList(final List<? extends D> list,
			final Function<? super D,? extends R> map) {
		return new MappedList<D,R>() {
			protected List<? extends D> getWrapped() {return list;}
			protected Function<? super D, ? extends R> getMap() {return map;}
		};
	}
	
	public static <D,R> Set<R> mapSet(
			final Set<? extends D> set,
			final Function<? super D,? extends R> injectiveMap) {
		return new MappedSet<D,R>() {
			protected Set<? extends D> getWrapped() {return set;}
			protected Function<? super D, ? extends R> getMap() {
				return injectiveMap;
			}
		};
	}
	
	private static abstract class MappedIterator<D,R> implements Iterator<R> {
		protected abstract Iterator<? extends D> getWrapped();
		protected abstract Function<? super D, ? extends R> getMap();
		
		public boolean hasNext() {return getWrapped().hasNext();}
		public R next() {return getMap().get(getWrapped().next());}
		public void remove() {getWrapped().remove();}
	}
	
	private static abstract class MappedListIterator<D,R>
			extends MappedIterator<D,R> implements ListIterator<R> {
		protected abstract ListIterator<? extends D> getWrapped();

		public void add(R arg0) {throw new UnsupportedOperationException();}
		public boolean hasPrevious() {return getWrapped().hasPrevious();}
		public int nextIndex() {return getWrapped().nextIndex();}
		public R previous() {return getMap().get(getWrapped().previous());}
		public int previousIndex() {return getWrapped().previousIndex();}
		public void set(R arg0) {throw new UnsupportedOperationException();}
	}
	
	private static abstract class MappedIterable<D,R> implements Iterable<R> {
		protected abstract Iterable<? extends D> getWrapped();
		protected abstract Function<? super D, ? extends R> getMap();
		
		public Iterator<R> iterator() {
			return mapIterator(getWrapped().iterator(), getMap());
		}
		
		public String toString() {return Collections.toString(this);}
	}
	
	private static abstract class MappedCollection<D,R>
			extends MappedIterable<D,R> implements Collection<R> {
		protected abstract Collection<? extends D> getWrapped();
		
		public boolean add(R o) {throw new UnsupportedOperationException();}
		
		public boolean addAll(Collection<? extends R> c) {
			throw new UnsupportedOperationException();
		}
		
		public void clear() {getWrapped().clear();}
		
		public boolean contains(Object o) {
			for (R mapped : this)
				if (mapped == null ? o == null : mapped.equals(o))
					return true;
			return false;
		}
		
		public boolean containsAll(Collection<?> c) {
			for (Object o : c)
				if (!contains(o))
					return false;
			return true;
		}
		
		public boolean isEmpty() {return getWrapped().isEmpty();}
		
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}
		
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}
		
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}
		
		public int size() {return getWrapped().size();}

		public Object[] toArray() {
			Object[] objects = new Object[size()];
			int i = 0;
			for (R mapped : this)
				objects[i++] = mapped;
			return objects;
		}
		
		public <T> T[] toArray(T[] a) {
			if (a == null)
				return (T[])toArray();
			if (a.length < size())
				a = (T[])Array.newInstance(a.getClass().getComponentType(),
						size());
			int i = 0;
			for (R mapped : this)
				a[i++] = (T)mapped;
			if (a.length != i)
				a[i] = null;
			return a;
		}
	}
	
	private static abstract class MappedList<D,R>
			extends MappedCollection<D,R> implements List<R> {
		protected abstract List<? extends D> getWrapped();

		public void add(int index, R element) {
			throw new UnsupportedOperationException();
		}

		public boolean addAll(int index, Collection<? extends R> c) {
			throw new UnsupportedOperationException();
		}

		public R get(int index) {return getMap().get(getWrapped().get(index));}

		public int indexOf(Object o) {
			int i = 0;
			for (R mapped : this) {
				if (mapped == null ? o == null : mapped.equals(o))
					return i;
				i++;
			}
			return -1;
		}

		public int lastIndexOf(Object o) {
			int i = size();
			for (R mapped : this) {
				i--;
				if (mapped == null ? o == null : mapped.equals(o))
					return i;
			}
			return -1;
		}

		public ListIterator<R> listIterator() {
			return mapListIterator(getWrapped().listIterator(), getMap());
		}

		public ListIterator<R> listIterator(int index) {
			return mapListIterator(getWrapped().listIterator(index), getMap());
		}

		public R remove(int index) {
			return getMap().get(getWrapped().remove(index));
		}

		public R set(int index, R element) {
			throw new UnsupportedOperationException();
		}

		public List<R> subList(int fromIndex, int toIndex) {
			return mapList(getWrapped().subList(fromIndex, toIndex), getMap());
		}
	}
	
	private static abstract class MappedSet<D,R>
			extends MappedCollection<D,R> implements Set<R> {
	}
	
	public static <E> Iterator<E> filterIterator(final Iterator<E> iterator,
			final Pattern<? super E> pattern) {
		return new FilterIterator<E>() {
			protected Iterator<E> getWrapped() {return iterator;}
			protected Pattern<? super E> getPattern() {return pattern;}
		};
	}
	
	public static <E> Iterable<E> filterIterable(final Iterable<E> iterable,
			final Pattern<? super E> pattern) {
		return new FilterIterable<E>() {
			protected Iterable<E> getWrapped() {return iterable;}
			protected Pattern<? super E> getPattern() {return pattern;}
		};
	}
	
	private static abstract class FilterIterator<E> implements Iterator<E> {
		protected boolean mHasNext;
		protected E mNext = null;
		
		public FilterIterator() {mHasNext = findNext();}
		
		protected abstract Iterator<E> getWrapped();
		protected abstract Pattern<? super E> getPattern();
		
		protected boolean findNext() {
			while (getWrapped().hasNext())
				if (getPattern().matches(mNext = getWrapped().next()))
					return true;
			return false;
		}

		public boolean hasNext() {return mHasNext;}

		public E next() {
			if (!mHasNext)
				throw new IllegalStateException();
			E next = mNext;
			mHasNext = findNext();
			return next;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	private static abstract class FilterIterable<E> implements Iterable<E> {
		protected abstract Iterable<E> getWrapped();
		protected abstract Pattern<? super E> getPattern();
		
		public Iterator<E> iterator() {
			return filterIterator(getWrapped().iterator(), getPattern());
		}
		
		public String toString() {return Collections.toString(this);}
	}
	
	public static int size(Iterable<?> iterable) {
		int count = 0;
		for (Iterator<?> iterator = iterable.iterator(); iterator.hasNext();
				iterator.next())
			count++;
		return count;
	}
	
	public static String toString(Iterable<?> iterable) {
		Iterator<?> elements = iterable.iterator();
		if (!elements.hasNext())
			return "[]";
		StringBuilder string = new StringBuilder("[");
		string.append(elements.next());
		while (elements.hasNext()) {
			string.append(", ");
			string.append(elements.next());
		}
		string.append(']');
		return string.toString();
	}
	
	public static <E> List<E> nullList(final int size) {
		if (size < 0)
			throw new IllegalArgumentException();
		return size == 0 ? java.util.Collections.<E>emptyList()
				: new AbstractList<E>() {
			public boolean contains(Object o) {return o == null;}

			public E get(int index) {
				if (index < 0 || size <= index)
					throw new IndexOutOfBoundsException();
				return null;
			}

			public int indexOf(Object o) {return o == null ? 0 : -1;}

			public boolean isEmpty() {return false;}

			public Iterator<E> iterator() {return listIterator();}

			public int lastIndexOf(Object o) {return o == null ? size - 1 : -1;}

			public ListIterator<E> listIterator() {return listIterator(0);}

			public ListIterator<E> listIterator(final int index) {
				if (index < 0 || size < index)
					throw new IndexOutOfBoundsException();
				return new ListIterator<E>() {
					private int mNext = index;

					public int nextIndex() {return mNext;}
					public boolean hasNext() {return mNext != size;}
					public E next() {
						if (mNext == size)
							throw new IllegalStateException();
						mNext++;
						return null;
					}

					public int previousIndex() {return mNext - 1;}
					public boolean hasPrevious() {return mNext != 0;}
					public E previous() {
						if (mNext == 0)
							throw new IllegalStateException();
						mNext--;
						return null;
					}

					public void add(E e) {
						throw new UnsupportedOperationException();
					}
					public void remove() {
						throw new UnsupportedOperationException();
					}
					public void set(E e) {
						throw new UnsupportedOperationException();
					}
				};
			}

			public int size() {return size;}

			public Object[] toArray() {return new Object[size];}
		};
	}
	
	public static <E> Iterator<E> concatonateIterators(
			Iterator<? extends E>... iterators) {
		return concatonateIterators(Arrays.asList(iterators).iterator());
	}
	
	public static <E> Iterator<E> concatonateIterators(
			final Iterator<? extends Iterator<? extends E>> iterators) {
		return new Iterator<E>() {
			private Iterator<? extends E> mRemove = null;
			private Iterator<? extends E> mNext = null;
			
			private Iterator<? extends E> nextIterator() {
				while (iterators.hasNext()) {
					Iterator<? extends E> next = iterators.next();
					if (next.hasNext())
						return next;
				}
				return null;
			}

			public boolean hasNext() {
				if (mNext != null && mNext.hasNext())
					return true;
				mNext = nextIterator();
				return mNext != null;
			}

			public E next() {
				if (!hasNext())
					throw new IllegalStateException();
				mRemove = mNext;
				return mNext.next();
			}

			public void remove() {
				if (mRemove == null)
					throw new IllegalStateException();
				mRemove.remove();
			}
		};
	}
}
