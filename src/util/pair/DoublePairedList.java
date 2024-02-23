package util.pair;

import java.util.List;

public class DoublePairedList<F, S> extends AbstractPairedList<F,S> {
	protected List<F> mFirst;
	protected List<S> mSecond;

	public DoublePairedList(List<F> first, List<S> second) {
		if (first.size() != second.size())
			throw new IllegalArgumentException();
		mFirst = first;
		mSecond = second;
	}

	public int size() {return mFirst.size();}

	public boolean isEmpty() {return mFirst.isEmpty();}

	public void clear() {mFirst.clear(); mSecond.clear();}

	public F getFirst(int index) {return mFirst.get(index);}

	public S getSecond(int index) {return mSecond.get(index);}

	public F setFirst(int index, F first) {return mFirst.set(index, first);}

	public S setSecond(int index, S second) {return mSecond.set(index, second);}

	public void add(int index, F first, S second) {
		mFirst.add(index, first);
		mSecond.add(index, second);
	}

	public void removeAt(int index) {
		mFirst.remove(index);
		mSecond.remove(index);
	}

	public PairedList<F,S> subList(int fromIndex, int toIndex) {
		return new DoublePairedList<F,S>(mFirst.subList(fromIndex, toIndex),
				mSecond.subList(fromIndex, toIndex));
	}
}