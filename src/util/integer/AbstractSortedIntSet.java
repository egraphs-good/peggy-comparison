package util.integer;

import java.util.NoSuchElementException;
import java.util.SortedSet;

public abstract class AbstractSortedIntSet extends AbstractIntSet
		implements SortedIntSet {
	protected SortedIntSet getEmptySubSet() {
		return IntCollections.sortedSet(comparator());
	}
	
	public SortedIntSet headSet(int toElement) {
		if (toElement == comparator().getMaximum())
			return this;
		return new HeadSet(toElement);
	}

	public SortedIntSet subSet(int fromElement, int toElement) {
		if (fromElement == comparator().getMinimum())
			return headSet(toElement);
		if (toElement == comparator().getMaximum())
			return tailSet(fromElement);
		if (comparator().compare(fromElement, toElement) > 0)
			return getEmptySubSet();
		return new StrictSubSet(fromElement, toElement);
	}

	public SortedIntSet tailSet(int fromElement) {
		if (fromElement == comparator().getMinimum())
			return this;
		return new TailSet(fromElement);
	}

	public Integer first() {return firstInt();}

	public SortedSet<Integer> headSet(Integer toElement) {
		if (toElement == null)
			throw new IllegalArgumentException();
		int to = toElement.intValue();
		if (to == comparator().getMinimum())
			return getEmptySubSet();
		return headSet(comparator().getNextSmallest(toElement.intValue()));
	}

	public Integer last() {return lastInt();}

	public SortedSet<Integer> subSet(Integer fromElement, Integer toElement) {
		if (fromElement == null || toElement == null)
			throw new IllegalArgumentException();
		int to = toElement.intValue();
		if (to == comparator().getMinimum())
			return getEmptySubSet();
		return subSet(fromElement.intValue(), 
				comparator().getNextSmallest(toElement.intValue()));
	}

	public SortedSet<Integer> tailSet(Integer fromElement) {
		if (fromElement == null)
			throw new IllegalArgumentException();
		return tailSet(fromElement.intValue());
	}
	
	protected abstract class SubSet extends AbstractSortedIntSet {
		public IntComparator comparator() {
			return AbstractSortedIntSet.this.comparator();
		}
	}
	
	protected class StrictSubSet extends AbstractSortedIntSet {
		protected final int mFirst, mLast;
		
		public StrictSubSet(int first, int last) {mFirst = first; mLast = last;}
		
		public IntComparator comparator() {
			return AbstractSortedIntSet.this.comparator();
		}
		
		public IntIterator iterator() {
			return new AbstractIntIterator() {
				private IntIterator mIterator
						= AbstractSortedIntSet.this.iterator(mFirst);
				private int mCurrent = comparator().getNextLargest(mLast);
				private int mNext = !mIterator.hasNext()
						? mCurrent : mIterator.next();

				public int nextInt() {
					mCurrent = mNext;
					if (!hasNext())
						throw new IllegalStateException();
					mNext = mIterator.hasNext() ? mIterator.next()
							: comparator().getNextLargest(mLast);
					return mCurrent;
				}

				public boolean hasNext() {
					return comparator().compare(mNext, mLast) <= 0;
				}

				public void remove() {
					if (comparator().compare(mCurrent, mLast) > 0)
						throw new IllegalStateException();
					AbstractSortedIntSet.this.remove(mCurrent);
					mCurrent = comparator().getNextLargest(mLast);
				}
			};
		}

		public int size() {
			return AbstractSortedIntSet.this.countBetween(mFirst, mLast);
		}

		public int firstInt() {
			int first = AbstractSortedIntSet.this.firstAfter(mFirst);
			if (comparator().compare(first, mLast) > 0)
				throw new NoSuchElementException();
			return first;
		}

		public int lastInt() {
			int last = AbstractSortedIntSet.this.lastBefore(mLast);
			if (comparator().compare(last, mFirst) < 0)
				throw new NoSuchElementException();
			return last;
		}

		public boolean remove(int element) {
			if (comparator().compare(mFirst, element) <= 0
					&& comparator().compare(element, mLast) <= 0)
				return AbstractSortedIntSet.this.remove(element);
			else
				return false;
		}

		public int countAfter(int element) {
			return AbstractSortedIntSet.this.countBetween(
					comparator().max(element, mFirst), mLast);
		}

		public int countBefore(int element) {
			return AbstractSortedIntSet.this.countBetween(
					mFirst, comparator().min(element, mLast));
		}

		public int countBetween(int lower, int upper) {
			return AbstractSortedIntSet.this.countBetween(
					comparator().max(lower, mFirst),
					comparator().min(upper, mLast));
		}

		public int firstAfter(int element) {
			int first = AbstractSortedIntSet.this.firstAfter(
					comparator().max(element, mFirst));
			if (comparator().compare(first, mLast) > 0)
				throw new NoSuchElementException();
			return first;
		}

		public int lastBefore(int element) {
			int last = AbstractSortedIntSet.this.lastBefore(
					comparator().min(element, mLast));
			if (comparator().compare(last, mFirst) < 0)
				throw new NoSuchElementException();
			return last;
		}

		public IntIterator iterator(final int start) {
			if (comparator().compare(mFirst, start) <= 0)
				return iterator();
			return new AbstractIntIterator() {
				private IntIterator mIterator
						= AbstractSortedIntSet.this.iterator(start);
				private int mCurrent = comparator().getNextLargest(mLast);
				private int mNext = !mIterator.hasNext()
						? mCurrent : mIterator.next();

				public int nextInt() {
					mCurrent = mNext;
					if (!hasNext())
						throw new IllegalStateException();
					mNext = mIterator.hasNext() ? mIterator.next()
							: comparator().getNextLargest(mLast);
					return mCurrent;
				}

				public boolean hasNext() {
					return comparator().compare(mNext, mLast) <= 0;
				}

				public void remove() {
					if (comparator().compare(mCurrent, mLast) > 0)
						throw new IllegalStateException();
					AbstractSortedIntSet.this.remove(mCurrent);
					mCurrent = comparator().getNextLargest(mLast);
				}
			};
		}
		
		public SortedIntSet headSet(int toElement) {
			if (comparator().compare(toElement, mLast) >= 0)
				return this;
			return AbstractSortedIntSet.this.subSet(mFirst,
					comparator().min(toElement, mLast));
		}

		public SortedIntSet subSet(int fromElement, int toElement) {
			if (comparator().compare(fromElement, mFirst) <= 0
					&& comparator().compare(toElement, mLast) >= 0)
				return this;
			return AbstractSortedIntSet.this.subSet(
					comparator().max(fromElement, mFirst),
					comparator().min(toElement, mLast));
		}

		public SortedIntSet tailSet(int fromElement) {
			if (comparator().compare(fromElement, mFirst) <= 0)
				return this;
			return AbstractSortedIntSet.this.subSet(
					comparator().max(fromElement, mFirst), mLast);
		}
	}
	
	protected class HeadSet extends AbstractSortedIntSet {
		protected final int mLast;
		
		public HeadSet(int last) {mLast = last;}
		
		public IntComparator comparator() {
			return AbstractSortedIntSet.this.comparator();
		}
		
		public IntIterator iterator() {
			return new AbstractIntIterator() {
				private IntIterator mIterator
						= AbstractSortedIntSet.this.iterator();
				private int mCurrent = comparator().getNextLargest(mLast);
				private int mNext = !mIterator.hasNext()
						? mCurrent : mIterator.next();
			
				public int nextInt() {
					mCurrent = mNext;
					if (!hasNext())
						throw new IllegalStateException();
					mNext = mIterator.hasNext() ? mIterator.next()
							: comparator().getNextLargest(mLast);
					return mCurrent;
				}
			
				public boolean hasNext() {
					return comparator().compare(mNext, mLast) <= 0;
				}
			
				public void remove() {
					if (comparator().compare(mCurrent, mLast) > 0)
						throw new IllegalStateException();
					AbstractSortedIntSet.this.remove(mCurrent);
					mCurrent = comparator().getNextLargest(mLast);
				}
			};
		}

		public int size() {return AbstractSortedIntSet.this.countBefore(mLast);}

		public int countAfter(int element) {
			return AbstractSortedIntSet.this.countBetween(element, mLast);
		}

		public int countBefore(int element) {
			return AbstractSortedIntSet.this.countBefore(
					comparator().min(element, mLast));
		}

		public int countBetween(int lower, int upper) {
			return AbstractSortedIntSet.this.countBetween(
					lower, comparator().min(upper, mLast));
		}

		public int firstAfter(int element) {
			int first = AbstractSortedIntSet.this.firstAfter(element);
			if (comparator().compare(first, mLast) > 0)
				throw new NoSuchElementException();
			return first;
		}

		public int firstInt() {
			int first = AbstractSortedIntSet.this.firstInt();
			if (comparator().compare(first, mLast) > 0)
				throw new NoSuchElementException();
			return first;
		}

		public IntIterator iterator(final int start) {
			return new AbstractIntIterator() {
				private IntIterator mIterator
						= AbstractSortedIntSet.this.iterator(start);
				private int mCurrent = comparator().getNextLargest(mLast);
				private int mNext = !mIterator.hasNext()
						? mCurrent : mIterator.next();
			
				public int nextInt() {
					mCurrent = mNext;
					if (!hasNext())
						throw new IllegalStateException();
					mNext = mIterator.hasNext() ? mIterator.next()
							: comparator().getNextLargest(mLast);
					return mCurrent;
				}
			
				public boolean hasNext() {
					return comparator().compare(mNext, mLast) <= 0;
				}
			
				public void remove() {
					if (comparator().compare(mCurrent, mLast) > 0)
						throw new IllegalStateException();
					AbstractSortedIntSet.this.remove(mCurrent);
					mCurrent = comparator().getNextLargest(mLast);
				}
			};
		}

		public int lastBefore(int element) {
			return AbstractSortedIntSet.this.lastBefore(
					comparator().min(element, mLast));
		}

		public int lastInt() {
			return AbstractSortedIntSet.this.lastBefore(mLast);
		}

		public boolean remove(int element) {
			if (comparator().compare(element, mLast) < 0)
				return AbstractSortedIntSet.this.remove(element);
			else
				return false;
		}
		
		public SortedIntSet headSet(int toElement) {
			if (comparator().compare(toElement, mLast) >= 0)
				return this;
			return AbstractSortedIntSet.this.headSet(
					comparator().min(toElement, mLast));
		}

		public SortedIntSet subSet(int fromElement, int toElement) {
			if (fromElement == comparator().getMinimum()
					&& comparator().compare(toElement, mLast) >= 0)
				return this;
			return AbstractSortedIntSet.this.subSet(fromElement,
					comparator().min(toElement, mLast));
		}

		public SortedIntSet tailSet(int fromElement) {
			if (fromElement == comparator().getMinimum())
				return this;
			return AbstractSortedIntSet.this.subSet(fromElement, mLast);
		}
	}
	
	protected class TailSet extends AbstractSortedIntSet {
		protected final int mFirst;
		
		public TailSet(int first) {mFirst = first;}
		
		public IntComparator comparator() {
			return AbstractSortedIntSet.this.comparator();
		}
		
		public IntIterator iterator() {
			return AbstractSortedIntSet.this.iterator(mFirst);
		}

		public int size() {return AbstractSortedIntSet.this.countAfter(mFirst);}

		public int countAfter(int element) {
			return AbstractSortedIntSet.this.countAfter(
					comparator().max(element, mFirst));
		}

		public int countBefore(int element) {
			return AbstractSortedIntSet.this.countBetween(mFirst, element);
		}

		public int countBetween(int lower, int upper) {
			return AbstractSortedIntSet.this.countBetween(
					comparator().max(mFirst, lower), upper);
		}

		public int firstAfter(int element) {
			return AbstractSortedIntSet.this.firstAfter(
					comparator().max(mFirst, element));
		}

		public int firstInt() {
			return AbstractSortedIntSet.this.firstAfter(mFirst);
		}

		public IntIterator iterator(int start) {
			return AbstractSortedIntSet.this.iterator(
					comparator().max(start, mFirst));
		}

		public int lastBefore(int element) {
			int last = AbstractSortedIntSet.this.lastBefore(element);
			if (comparator().compare(last, mFirst) < 0)
				throw new NoSuchElementException();
			return last;
		}

		public int lastInt() {
			return AbstractSortedIntSet.this.lastBefore(mFirst);
		}

		public boolean remove(int element) {
			if (comparator().compare(element, mFirst) >= 0)
				return AbstractSortedIntSet.this.remove(element);
			else
				return false;
		}
		
		public SortedIntSet headSet(int toElement) {
			if (toElement == comparator().getMaximum())
				return this;
			return AbstractSortedIntSet.this.subSet(mFirst, toElement);
		}

		public SortedIntSet subSet(int fromElement, int toElement) {
			if (comparator().compare(fromElement, mFirst) <= 0
					&& toElement == comparator().getMaximum())
				return this;
			return AbstractSortedIntSet.this.subSet(
					comparator().max(fromElement, mFirst), toElement);
		}

		public SortedIntSet tailSet(int fromElement) {
			if (comparator().compare(fromElement, mFirst) <= 0)
				return this;
			return AbstractSortedIntSet.this.tailSet(
					Math.max(fromElement, mFirst));
		}
	}
}
