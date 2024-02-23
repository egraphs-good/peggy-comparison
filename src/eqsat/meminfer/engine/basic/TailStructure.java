package eqsat.meminfer.engine.basic;

public final class TailStructure<T> implements Structure<T> {
	private final int mHeadCount;
	private final Structure<T> mTail;
	
	public TailStructure(int headCount, Structure<T> tail) {
		if (headCount < 1)
			throw new IllegalArgumentException();
		if (tail == null)
			throw new NullPointerException();
		mHeadCount = headCount;
		mTail = tail;
	}

	public T getTerm(int index) {
		if (index < mHeadCount)
			return null;
		else
			return mTail.getTerm(index - mHeadCount);
	}

	public int getTermCount() {return mHeadCount + mTail.getTermCount();}
	
	public boolean isComplete() {return false;}
	
	public boolean isRemoved() {return mTail.isRemoved();}
	
	public String toString() {return "[?"+ mHeadCount +"," + mTail + "]";}
}
