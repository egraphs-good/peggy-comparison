package util.integer;

import java.util.Collection;

public class ArrayIntPartition {
	protected ArrayIntMap<Integer> mRepresentatives
			= new ArrayIntMap<Integer>();
	protected IntMap<SortedIntSet> mGroups = new ArrayIntMap<SortedIntSet>();
	
	public ArrayIntPartition(int min, int max) {
		if (min < 0 || max < min)
			throw new IllegalArgumentException();
		for (int element = min; element <= max; element++) {
			mRepresentatives.put(element, (Integer)element);
			SortedIntSet elementSet = IntCollections.createBoundedBitSet(max);
			elementSet.add(element);
			mGroups.put(element, elementSet);
		}
	}

	public boolean group(int left, int right) {
		if (left == right)
			return false;
		int leftRep = mRepresentatives.get(left);
		int rightRep = mRepresentatives.get(right);
		if (leftRep == rightRep)
			return false;
		IntSet rightGroup = mGroups.remove(rightRep);
		mGroups.get(leftRep).addAll(rightGroup);
		Integer boxed = leftRep;
		for (IntIterator friends = rightGroup.iterator(); friends.hasNext(); )
			mRepresentatives.put(friends.nextInt(), boxed);
		return true;
	}

	public boolean isGrouped(int left, int right) {
		if (left == right)
			return true;
		int leftRep = mRepresentatives.get(left);
		int rightRep = mRepresentatives.get(right);
		return leftRep == rightRep;
	}

	public int getRepresentative(int element) {
		return mRepresentatives.get(element);
	}

	public Integer getOptionalRepresentative(int element) {
		return mRepresentatives.get(element);
	}
	
	public boolean isRepresentative(int element) {
		Integer rep = mRepresentatives.get(element);
		return rep != null && element == rep;
	}
	
	public IntSet getGroup(int element) {
		int rep = mRepresentatives.get(element);
		return mGroups.get(rep);
	}
	
	public IntSet getRepresentatives() {return mGroups.keySet();}
	
	public Collection<? extends IntSet> getGroups() {
		return mGroups.values();
	}
	
	public SortedIntSet ungroup(int element) {
		int rep = mRepresentatives.get(element);
		SortedIntSet group = mGroups.remove(rep);
		for (IntIterator friends = group.iterator(); friends.hasNext(); ) {
			int friend = friends.nextInt();
			mRepresentatives.put(friend, (Integer)friend);
			SortedIntSet elementSet = IntCollections.createBoundedBitSet(
					mRepresentatives.keySet().lastInt());
			elementSet.add(friend);
			mGroups.put(friend, elementSet);
		}
		return group;
	}
	
	public String toString() {return mGroups.toString();}
}
