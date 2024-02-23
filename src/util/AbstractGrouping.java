package util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractGrouping<E> implements Grouping<E> {
	public boolean group(Collection<? extends E> group) {
		if (group.isEmpty() || group.size() == 1)
			return false;
		E rep = group.iterator().next();
		boolean changed = false;
		for (E friend : group)
			changed |= group(rep, friend);
		return changed;
	}
	
	public boolean groupAll(Grouping<? extends E> grouping) {
		boolean changed = false;
		for (Set<? extends E> group : grouping.getGroups())
			group(group);
		return changed;
	}
	
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append('[');
		for (Iterator<? extends E> reps = getRepresentatives().iterator();
				reps.hasNext(); ) {
			E rep = reps.next();
			string.append('[');
			string.append(rep);
			string.append("->");
			string.append(getGroup(rep));
			string.append(']');
			if (reps.hasNext())
				string.append(',');
		}
		return string.toString();
	}
}
