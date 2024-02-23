package util.integer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import util.pair.ArrayPairedList;
import util.pair.PairedList;

/** Smaller numbers are considered higher priority */
public class IntPriorityHeap<E> {
	protected final Map<E,Integer> mEntries = new HashMap();
	protected final PairedList<Integer,Entry<E,Integer>> mHeap
			= new ArrayPairedList();
	
	public boolean isEmpty() {return mHeap.isEmpty();}
	public E peek() {return mHeap.getSecond(0).getKey();}
	public int peekPriority() {return mHeap.getFirst(0);}
	public E pop() {
		E top = mHeap.getSecond(0).getKey();
		mEntries.remove(top);
		Entry<E,Integer> entry = mHeap.getSecond(mHeap.size() - 1);
		mHeap.set(0, mHeap.getFirst(mHeap.size() - 1), entry);
		mHeap.removeLast();
		entry.setValue(0);
		heapify(0);
		return top;
	}

	/** Smaller numbers are considered higher priority */
	public void improvePriority(E element, int priority) {
		Integer boxxedIndex = mEntries.get(element);
		int index;
		if (boxxedIndex == null) {
			index = mHeap.size();
			mEntries.put(element, index);
			for (Entry<E,Integer> entry : mEntries.entrySet())
				if (element == null ? entry.getKey() == null
						: (entry.getKey() != null
						&& element.equals(entry.getKey()))) {
					mHeap.add(priority, entry);
					break;
				}
		} else {
			index = boxxedIndex;
			if (mHeap.getFirst(index) <= priority)
				return;
			mHeap.setFirst(index, priority);
		}
		int parent;
		while (index > 0
				&& mHeap.getFirst(parent = getParentIndex(index)) > priority) {
			swap(parent, index);
			index = parent;
			priority = mHeap.getFirst(index);
		}
	}
	
	protected void heapify(int index) {
		int left = getLeftChildIndex(index);
		int right = getRightChildIndex(index);
		int smallest = left < mHeap.size()
				&& mHeap.getFirst(left) < mHeap.getFirst(index)
				? left : index;
		if (right < mHeap.size()
				&& mHeap.getFirst(right) < mHeap.getFirst(smallest))
			smallest = right;
		if (smallest != index) {
			swap(index, smallest);
			heapify(smallest);
		}
	}
	
	protected void swap(int left, int right) {
		Integer leftPriority = mHeap.getFirst(left);
		Entry<E,Integer> leftEntry = mHeap.getSecond(left);
		Entry<E,Integer> rightEntry = mHeap.getSecond(right);
		mHeap.set(left, mHeap.getFirst(right), rightEntry);
		mHeap.set(right, leftPriority, leftEntry);
		leftEntry.setValue(right);
		rightEntry.setValue(left);
	}
	
	protected final int getParentIndex(int index) {return index >>> 1;}
	protected final int getLeftChildIndex(int index) {return index << 1;}
	protected final int getRightChildIndex(int index) {return (index << 1) | 1;}
}
