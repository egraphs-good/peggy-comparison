package util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HashPartition<E> extends AbstractGrouping<E> {
	protected Map<E,E> mRepresentatives = new HashMap<E,E>();
	protected Map<E,Set<E>> mGroups = new HashMap<E,Set<E>>();
	
	public HashPartition(Collection<? extends E> elements) {
		for (E element : elements) {
			mRepresentatives.put(element, element);
			Set<E> elementSet = new HashSet<E>();
			elementSet.add(element);
			mGroups.put(element, elementSet);
		}
	}

	public boolean group(E left, E right) {
		if (left.equals(right))
			return false;
		E leftRep = mRepresentatives.get(left);
		E rightRep = mRepresentatives.get(right);
		if (leftRep.equals(rightRep))
			return false;
		else {
			Set<E> leftGroup = mGroups.remove(leftRep);
			mGroups.get(rightRep).addAll(leftGroup);
			for (E leftFriend : leftGroup)
				mRepresentatives.put(leftFriend, rightRep);
			return true;
		}
	}

	public boolean isGrouped(Object left, Object right) {
		if (left.equals(right))
			return true;
		E leftRep = mRepresentatives.get(left);
		E rightRep = mRepresentatives.get(right);
		return leftRep.equals(rightRep);
	}

	public E getRepresentative(E element) {
		return mRepresentatives.get(element);
	}

	public E getOptionalRepresentative(Object element) {
		return mRepresentatives.get(element);
	}
	
	public boolean isRepresentative(Object element) {
		E rep = mRepresentatives.get(element);
		return rep != null || element != null && element.equals(rep);
	}
	
	public Set<? extends E> getGroup(E element) {
		E rep = mRepresentatives.get(element);
		return mGroups.get(rep);
	}
	
	public Collection<? extends E> getRepresentatives() {
		return mGroups.keySet();
	}
	
	public Collection<? extends Set<? extends E>> getGroups() {
		return mGroups.values();
	}
	
	public Set<? extends E> ungroup(E element) {
		E rep = mRepresentatives.get(element);
		Set<E> group = mGroups.remove(rep);
		for (E friend : group) {
			mRepresentatives.put(friend, friend);
			Set<E> elementSet = new HashSet<E>();
			elementSet.add(friend);
			mGroups.put(friend, elementSet);
		}
		return group;
	}
}