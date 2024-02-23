package util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HashGrouping<E> extends AbstractGrouping<E> {
	protected Map<E,E> mRepresentatives = new HashMap<E,E>();
	protected Map<E,Set<E>> mGroups = new HashMap<E,Set<E>>();
	
	public HashGrouping() {}
	public HashGrouping(Iterable<? extends E> force) {
		for (E element : force) {
			Set<E> group = new HashSet<E>();
			group.add(element);
			mRepresentatives.put(element, element);
			mGroups.put(element, group);
		}
	}

	public boolean group(E left, E right) {
		if (left == null ? right == null : left.equals(right))
			return false;
		E leftRep = mRepresentatives.get(left);
		E rightRep = mRepresentatives.get(right);
		if (leftRep == null) {
			if (rightRep == null) {
				if (left == null) {
					mRepresentatives.put(left, right);
					mRepresentatives.put(right, right);
					Set<E> group = new HashSet<E>();
					group.add(left);
					group.add(right);
					mGroups.put(right, group);
				} else {
					E rep = swap(left, right) ? right : left;
					mRepresentatives.put(left, rep);
					mRepresentatives.put(right, rep);
					Set<E> group = new HashSet<E>();
					group.add(left);
					group.add(right);
					mGroups.put(rep, group);
				}
			} else if (left == null || !swap(rightRep, left)){
				mRepresentatives.put(left, rightRep);
				mGroups.get(rightRep).add(left);
			} else {
				Set<E> group = mGroups.remove(rightRep);
				group.add(left);
				mGroups.put(left, group);
				for (E friend : group)
					mRepresentatives.put(friend, left);
			}
		} else if (rightRep == null) {
			if (right == null || !swap(leftRep, right)) {
				mRepresentatives.put(right, leftRep);
				mGroups.get(leftRep).add(right);
			} else {
				Set<E> group = mGroups.remove(leftRep);
				group.add(right);
				mGroups.put(right, group);
				for (E friend : group)
					mRepresentatives.put(friend, right);
			}
		}
		else if (leftRep.equals(rightRep))
			return false;
		else if (!swap(leftRep, rightRep))
			merge(leftRep, rightRep);
		else
			merge(rightRep, leftRep);
		return true;
	}
	
	protected boolean swap(E left, E right) {return false;}
	
	protected void merge(E leftRep, E rightRep) {
		Set<E> rightGroup = mGroups.remove(rightRep);
		mGroups.get(leftRep).addAll(rightGroup);
		for (E rightFriend : rightGroup)
			mRepresentatives.put(rightFriend, leftRep);
	}

	public boolean isGrouped(Object left, Object right) {
		if (left == null ? right == null : left.equals(right))
			return true;
		E leftRep = mRepresentatives.get(left);
		if (leftRep == null)
			return false;
		E rightRep = mRepresentatives.get(right);
		return rightRep != null && leftRep.equals(rightRep);
	}

	public E getRepresentative(E element) {
		E rep = mRepresentatives.get(element);
		return rep == null ? element : rep;
	}

	public E getOptionalRepresentative(Object element) {
		return mRepresentatives.get(element);
	}
	
	public boolean isRepresentative(Object element) {
		E rep = mRepresentatives.get(element);
		return rep == null || element != null && element.equals(rep);
	}
	
	public Set<? extends E> getGroup(E element) {
		E rep = mRepresentatives.get(element);
		return rep == null ? Collections.singleton(element)
				: mGroups.get(rep);
	}
	
	public Collection<? extends E> getRepresentatives() {
		return mGroups.keySet();
	}
	
	public Collection<? extends Set<? extends E>> getGroups() {
		return mGroups.values();
	}
	
	public Set<? extends E> ungroup(E element) {
		E rep = mRepresentatives.get(element);
		if (rep == null)
			return Collections.singleton(element);
		Set<E> group = mGroups.remove(rep);
		for (E friend : group)
			mRepresentatives.remove(friend);
		return group;
	}
}
