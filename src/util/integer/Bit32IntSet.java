package util.integer;

import java.util.NoSuchElementException;

public class Bit32IntSet extends AbstractSortedIntSet {
	protected static final int MAX_INDEX = 31;
	protected int mBits;
	
	protected static final int bit(int index) {return 1 << index;}
	protected static final int bitRange(int lower, int upper) {
		return (~0 >>> (MAX_INDEX - upper)) & ~((1 << lower) - 1);
	}
	
	public Bit32IntSet() {this(0);}
	public Bit32IntSet(int bits) {mBits = bits;}
	
	public IntIterator iterator() {
		return new AbstractIntIterator() {
			private int mCurrent = -1;
			private int mNext = nextSetBit(0);
			
			public int nextInt() {
				if (!hasNext())
					throw new IllegalStateException();
				mCurrent = mNext;
				mNext = nextSetBit(mNext + 1);
				return mCurrent;
			}

			public boolean hasNext() {
				return mNext != -1;
			}

			public void remove() {
				if (mCurrent == -1)
					throw new IllegalStateException();
				mBits &= ~bit(mCurrent);
				mCurrent = -1;
			}
		};
	}

	public int size() {return bitCount(mBits);}
	
	public void clear() {mBits = 0;}
	
	public boolean contains(int element) {
		return element >= 0 && ((mBits & (1 << element)) != 0);
	}
	
	public boolean add(int element) {
		if (element < 0 || element > MAX_INDEX)
			throw new IllegalArgumentException();
		if (contains(element))
			return false;
		mBits |= 1 << element;
		return true;
	}

	public boolean remove(int element) {
		if (!contains(element))
			return false;
		mBits &= ~bit(element);
		return true;
	}
	
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (this == that)
			return true;
		if (that instanceof Bit32IntSet)
			return equals((Bit32IntSet)that);
		return super.equals(that);
	}
	
	public boolean equals(Bit32IntSet that) {
		return that != null && mBits == that.mBits;
	}
	
	public int hashCode() {return mBits;}
	
	public void addRange(int lower, int upper) {
		if (lower > upper || lower < 0 || upper > MAX_INDEX)
			throw new IllegalArgumentException();
		mBits |= bitRange(lower, upper);
	}
	
	public void set(Bit32IntSet that) {
		mBits = that.mBits;
	}
	
	public int firstInt() {return nextSetBit(0);}
	
	public int lastInt() {return previousSetBit(MAX_INDEX);}

	public IntComparator comparator() {
		return IntCollections.increasingComparator();
	}
	
	public int countAfter(int element) {
		if (element > MAX_INDEX)
			return 0;
		return bitCount(mBits & bitRange(Math.max(element, 0), MAX_INDEX));
	}
	
	public int countBefore(int element) {
		if (element < 0)
			return 0;
		return bitCount(mBits & bitRange(0, element));
	}
	
	public int countBetween(int lower, int upper) {
		if (upper < 0)
			return 0;
		return bitCount(mBits & bitRange(Math.max(lower, 0), upper));
	}
	
	public int firstAfter(int element) {
		element = nextSetBit(element);
		if (element == -1)
			throw new NoSuchElementException();
		return element;
	}
	
	public IntIterator iterator(final int start) {
		return new AbstractIntIterator() {
			private int mCurrent = -1;
			private int mNext = nextSetBit(Math.max(start, 0));
			
			public int nextInt() {
				if (!hasNext())
					throw new IllegalStateException();
				mCurrent = mNext;
				mNext = nextSetBit(mNext + 1);
				return mCurrent;
			}

			public boolean hasNext() {
				return mNext != -1;
			}

			public void remove() {
				if (mCurrent == -1)
					throw new IllegalStateException();
				mBits &= ~bit(mCurrent);
				mCurrent = -1;
			}
		};
	}
	
	public int lastBefore(int element) {return previousSetBit(element);}
	
	public boolean containsAll(Bit32IntSet that) {
		return (mBits & ~that.mBits) == 0;
	}
	
	public boolean retainAll(Bit32IntSet that) {
		if (that.containsAll(this))
			return false;
		mBits &= that.mBits;
		return true;
	}
	
	public boolean removeAll(Bit32IntSet that) {
		if (!intersects(that))
			return false;
		mBits &= ~that.mBits;
		return true;
	}
	
	public boolean intersects(Bit32IntSet that) {
		return (mBits & that.mBits) != 0;
	}
	
    protected int nextSetBit(int fromIndex) {
    	// right shifting by 32+ doesn't work as you'd
    	// expect; make an explicit check for it (mstepp)
    	if (fromIndex >= 32)
    		return -1;
		if (fromIndex < 0)
		    fromIndex = 0;
        int unit = mBits >>> fromIndex;
        if (unit == 0)
        	return -1;
        return trailingZeroCnt(unit) + fromIndex;
    }

    protected int previousSetBit(int fromIndex) {
		if (fromIndex < 0)
		    return -1;
		if (fromIndex > MAX_INDEX)
			fromIndex = MAX_INDEX;
        int unit = mBits << (MAX_INDEX - fromIndex);
        if (unit == 0)
        	return -1;
        return fromIndex - leadingZeroCnt(unit);
    }
    
    protected static int leadingZeroCnt(int val) {
        // Loop unrolled for performance
        int byteVal = (val >>> 24) & 0xff;
        if (byteVal != 0)
            return leadingZeroTable[byteVal];

        byteVal = (val >>> 16) & 0xff;
        if (byteVal != 0)
            return leadingZeroTable[byteVal] + 8;

        byteVal = (val >>> 8) & 0xff;
        if (byteVal != 0)
            return leadingZeroTable[byteVal] + 16;

        byteVal = val & 0xff;
        return leadingZeroTable[byteVal] + 24;
    }

    /*
     * trailingZeroTable[i] is the number of trailing zero bits in the binary
     * representation of i.
     */
    private final static byte leadingZeroTable[] = {
		    -25, 7, 6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4,
			3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
			2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
			2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    protected static int trailingZeroCnt(int val) {
        // Loop unrolled for performance
        int byteVal = val & 0xff;
        if (byteVal != 0)
            return trailingZeroTable[byteVal];

        byteVal = (val >>> 8) & 0xff;
        if (byteVal != 0)
            return trailingZeroTable[byteVal] + 8;

        byteVal = (val >>> 16) & 0xff;
        if (byteVal != 0)
            return trailingZeroTable[byteVal] + 16;

        byteVal = (val >>> 24) & 0xff;
        return trailingZeroTable[byteVal] + 24;
    }

    /*
     * trailingZeroTable[i] is the number of trailing zero bits in the binary
     * representation of i.
     */
    private final static byte trailingZeroTable[] = {
		    -25, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
			4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
			5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
			4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
			6, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
			4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
			5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
			4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
			7, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
			4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
			5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
			4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
			6, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
			4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
			5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
			4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0};

    /**
     * Returns the number of bits set in val.
     * For a derivation of this algorithm, see
     * "Algorithms and data structures with applications to 
     *  graphics and geometry", by Jurg Nievergelt and Klaus Hinrichs,
     *  Prentice Hall, 1993.
     */
    protected static int bitCount(int val) {
        val -= (val & 0xaaaaaaaa) >>> 1;
        val =  (val & 0x33333333)
        		+ ((val >>> 2) & 0x33333333);
        val =  (val + (val >>> 4)) & 0x0f0f0f0f;
        val += val >>> 8;     
        val += val >>> 16;    
        return val & 0xff;
    }
}
