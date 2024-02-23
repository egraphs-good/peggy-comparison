package util;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class IdentityHashGrouping<E> extends AbstractGrouping<E> {
	protected Map<E,E> mRepresentatives = new IdentityHashMap<E,E>();
	protected Map<E,Set<E>> mGroups = new IdentityHashMap<E,Set<E>>();

	public boolean group(E left, E right) {
		if (left == right)
			return false;
		E leftRep = mRepresentatives.get(left);
		E rightRep = mRepresentatives.get(right);
		if (leftRep == null) {
			if (rightRep == null) {
				if (left == null) {
					mRepresentatives.put(left, right);
					mRepresentatives.put(right, right);
					Set<E> group = new IdentityHashSet<E>();
					group.add(left);
					group.add(right);
					mGroups.put(right, group);
				} else {
					mRepresentatives.put(left, left);
					mRepresentatives.put(right, left);
					Set<E> group = new IdentityHashSet<E>();
					group.add(left);
					group.add(right);
					mGroups.put(left, group);
				}
			} else {
				mRepresentatives.put(left, rightRep);
				mGroups.get(rightRep).add(left);
			}
		} else if (rightRep == null) {
			mRepresentatives.put(right, leftRep);
			mGroups.get(leftRep).add(right);
		}
		else if (leftRep == rightRep)
			return false;
		else {
			Set<E> rightGroup = mGroups.remove(rightRep);
			mGroups.get(leftRep).addAll(rightGroup);
			for (E rightFriend : rightGroup)
				mRepresentatives.put(rightFriend, leftRep);
		}
		return true;
	}

	public boolean isGrouped(Object left, Object right) {
		if (left == right)
			return true;
		E leftRep = mRepresentatives.get(left);
		return leftRep != null && leftRep == mRepresentatives.get(right);
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
		return rep == null || element == rep;
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
