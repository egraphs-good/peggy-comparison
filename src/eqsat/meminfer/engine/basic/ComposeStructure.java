package eqsat.meminfer.engine.basic;

public final class ComposeStructure<T> implements Structure<T> {
	private final Structure<T> mFirst, mSecond;
	
	public ComposeStructure(Structure<T> first, Structure<T> second) {
		if (first == null || second == null)
			throw new NullPointerException();
		mFirst = first;
		mSecond = second;
	}
	
	public int getTermCount() {
		return mFirst.getTermCount() + mSecond.getTermCount();
	}
	
	public T getTerm(int index) {
		if (index < mFirst.getTermCount())
			return mFirst.getTerm(index);
		else
			return mSecond.getTerm(index - mFirst.getTermCount());
	}
	
	public boolean isComplete() {
		return mFirst.isComplete() && mSecond.isComplete();
	}
	
	public boolean isRemoved() {
		return mFirst.isRemoved() || mSecond.isRemoved();
	}
	
	public Structure<T> getFirst() {return mFirst;}
	public Structure<T> getSecond() {return mSecond;}
	
	public String toString() {return "[" + mFirst + "," + mSecond + "]";}

}
