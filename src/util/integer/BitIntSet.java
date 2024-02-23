package util.integer;

import java.util.NoSuchElementException;

import util.BitSet;

public class BitIntSet extends AbstractIncreasingIntSet {
	protected final BitSet mBits;
	
	public BitIntSet() {mBits = new BitSet();}
	public BitIntSet(int initialSize) {mBits = new BitSet(initialSize);}
	public BitIntSet(BitSet bits) {
		if (bits == null)
			mBits = new BitSet();
		else
			mBits = new BitSet(bits);
	}
	public BitIntSet(BitIntSet that) {
		if (that == null)
			mBits = new BitSet();
		else
			mBits = new BitSet(that.mBits);
	}
	
	public IntIterator iterator() {
		return new AbstractIntIterator() {
			private int mCurrent = -1;
			private int mNext = mBits.nextSetBit(0);
			
			public int nextInt() {
				if (!hasNext())
					throw new IllegalStateException();
				mCurrent = mNext;
				mNext = mBits.nextSetBit(mNext + 1);
				return mCurrent;
			}

			public boolean hasNext() {
				return mNext != -1;
			}

			public void remove() {
				if (mCurrent == -1)
					throw new IllegalStateException();
				mBits.clear(mCurrent);
				mCurrent = -1;
			}
		};
	}

	public int size() {return mBits.cardinality();}
	
	public void clear() {mBits.clear();}
	
	public boolean contains(int element) {
		return element >= 0 && mBits.get(element);
	}
	
	public boolean add(int element) {
		if (element < 0)
			throw new IllegalArgumentException();
		return !mBits.set(element);
	}

	public boolean remove(int element) {
		if (element < 0)
			return false;
		return mBits.clear(element);
	}
	
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (this == that)
			return true;
		if (that instanceof BitIntSet)
			return equals((BitIntSet)that);
		return super.equals(that);
	}
	
	public boolean equals(BitIntSet that) {
		return that != null && mBits.equals(that.mBits);
	}
	
	public boolean equals(java.util.BitSet that) {return mBits.equals(that);}
	
	public int hashCode() {return mBits.hashCode();}
	
	public void addRange(int lower, int upper) {
		mBits.set(lower, upper);
	}
	
	public void set(BitIntSet that) {
		mBits.imitate(that.mBits);
	}
	
	public int firstInt() {return mBits.nextSetBit(0);}
	
	public int lastInt() {return mBits.length() - 1;}
	
	public int countAfter(int element) {return mBits.cardinalityAfter(element);}
	
	public int countBefore(int element) {
		return mBits.cardinalityBefore(element);
	}
	
	public int countBetween(int lower, int upper) {
		return mBits.cardinalityBetween(lower, upper);
	}
	
	public int firstAfter(int element) {
		element = mBits.nextSetBit(element);
		if (element == -1)
			throw new NoSuchElementException();
		return element;
	}
	
	public IntIterator iterator(final int start) {
		return new AbstractIntIterator() {
			private int mCurrent = -1;
			private int mNext = mBits.nextSetBit(Math.max(start, 0));
			
			public int nextInt() {
				if (!hasNext())
					throw new IllegalStateException();
				mCurrent = mNext;
				mNext = mBits.nextSetBit(mNext + 1);
				return mCurrent;
			}

			public boolean hasNext() {
				return mNext != -1;
			}

			public void remove() {
				if (mCurrent == -1)
					throw new IllegalStateException();
				mBits.clear(mCurrent);
				mCurrent = -1;
			}
		};
	}
	
	public int lastBefore(int element) {return mBits.previousSetBit(element);}
	
	public boolean containsAll(BitIntSet that) {
		return that.mBits.implies(mBits);
	}
	
	public boolean retainAll(BitIntSet that) {return mBits.and(that.mBits);}
	
	public boolean removeAll(BitIntSet that) {return mBits.andNot(that.mBits);}
	
	public boolean intersects(BitIntSet that) {
		return mBits.intersects(that.mBits);
	}
}
