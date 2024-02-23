package eqsat.meminfer.engine.basic;

public final class HeadStructure<T> implements Structure<T> {
	private final Structure<T> mHead;
	private final int mTailCount;
	
	public HeadStructure(Structure<T> head, int tailCount) {
		if (head == null)
			throw new NullPointerException();
		if (tailCount < 1)
			throw new IllegalArgumentException();
		mHead = head;
		mTailCount = tailCount;
	}

	public T getTerm(int index) {
		if (index < mHead.getTermCount())
			return mHead.getTerm(index);
		else if (index < getTermCount())
			return null;
		else
			throw new IndexOutOfBoundsException();
	}

	public int getTermCount() {return mHead.getTermCount() + mTailCount;}
	
	public boolean isComplete() {return false;}
	
	public boolean isRemoved() {return mHead.isRemoved();}
	
	public String toString() {return "["+ mHead +",?" + mTailCount + "]";}
}
