package util;

import java.util.Set;

public class ArraySet<E> extends ArrayCollection<E> implements Set<E> {
	public boolean add(E o) {
		return !contains(o) && super.add(o);
	}
}
