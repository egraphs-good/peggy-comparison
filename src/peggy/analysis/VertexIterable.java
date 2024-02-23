package peggy.analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * This class takes an abstract representation of a vertex with a fixed
 * number of children and provides an iterator to iterate over the unique
 * reachable vertices.
 */
public abstract class VertexIterable<V> implements Iterable<V> {
	private final Set<V> allnodes;
	public VertexIterable(Collection<? extends V> roots) {
		this.allnodes = new HashSet<V>();
		LinkedList<V> worklist = new LinkedList<V>(roots);
		while (worklist.size() > 0) {
			V next = worklist.removeFirst();
			if (this.allnodes.contains(next))
				continue;
			this.allnodes.add(next);
			worklist.addAll(getChildren(next));
		}
	}
	protected abstract Collection<? extends V> getChildren(V v);
	public Iterator<V> iterator() {
		return this.allnodes.iterator();
	}
}
