package util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import util.pair.ArrayPairedList;
import util.pair.PairedList;

public class PriorityHeap<E,P> {
	protected final Map<E,Integer> mEntries = new HashMap();
	protected final PairedList<P,Entry<E,Integer>> mHeap
			= new ArrayPairedList();
	protected final Comparator<? super P> mComparator;
	
	public PriorityHeap(Comparator<? super P> comparator) {
		mComparator = comparator;
	}
	
	public boolean isEmpty() {return mHeap.isEmpty();}
	public E peek() {return mHeap.getSecond(0).getKey();}
	public P peekPriority() {return mHeap.getFirst(0);}
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

	public void improvePriority(E element, P priority) {
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
			if (mComparator.compare(mHeap.getFirst(index), priority) >= 0)
				return;
			mHeap.setFirst(index, priority);
		}
		int parent;
		while (index > 0 && mComparator.compare(
				mHeap.getFirst(parent = getParentIndex(index)), priority) < 0) {
			swap(parent, index);
			index = parent;
			priority = mHeap.getFirst(index);
		}
	}
	
	protected void heapify(int index) {
		int left = getLeftChildIndex(index);
		int right = getRightChildIndex(index);
		int largest = left < mHeap.size() && mComparator.compare(
				mHeap.getFirst(left), mHeap.getFirst(index)) > 0 ? left : index;
		if (right < mHeap.size() && mComparator.compare(
				mHeap.getFirst(right), mHeap.getFirst(largest)) > 0)
			largest = right;
		if (largest != index) {
			swap(index, largest);
			heapify(largest);
		}
	}
	
	protected void swap(int left, int right) {
		P leftPriority = mHeap.getFirst(left);
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
