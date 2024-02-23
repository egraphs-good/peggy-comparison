package util;

import java.util.Collection;
import java.util.Set;

public interface Grouping<E> {
	public boolean group(E left, E right);
	public boolean group(Collection<? extends E> group);
	public boolean isGrouped(Object left, Object right);
	public E getRepresentative(E element);
	public E getOptionalRepresentative(Object element);
	public boolean isRepresentative(Object element);
	public Set<? extends E> getGroup(E element);
	public Collection<? extends E> getRepresentatives();
	public Collection<? extends Set<? extends E>> getGroups();
	public boolean groupAll(Grouping<? extends E> grouping);
	public Set<? extends E> ungroup(E element);
}
