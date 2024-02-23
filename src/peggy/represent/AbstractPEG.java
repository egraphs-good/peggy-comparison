package peggy.represent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * This is a PEG implementation with default implementations of certain methods.
 */
public abstract class AbstractPEG<T,R,PP extends PEG<T,R,PP,VV>,VV extends PEG.Vertex<T,R,PP,VV>> extends PEG<T,R,PP,VV> {
	public Set<? extends VV> getReturnVertices() {
		Set<VV> result = new HashSet<VV>();
		for (R arr : getReturns()) {
			result.add(getReturnVertex(arr));
		}
		return result;
	}

	public Iterable<? extends VV> getVertices() {
		return new Iterable<VV>() {
			public Iterator<VV> iterator() {
				return new Iterator<VV>() {
					LinkedList<VV> worklist = new LinkedList<VV>(getReturnVertices());
					Set<VV> seen = new HashSet<VV>();
					VV next = null;
					public boolean hasNext() {
						if (next != null) return true;
						while (worklist.size() > 0) {
							VV item = worklist.removeFirst();
							if (seen.contains(item))
								continue;
							seen.add(item);
							for (int i = 0; i < item.getChildCount(); i++) {
								worklist.addLast(item.getChild(i));
							}
							next = item;
							return true;
						}
						return false;
					}
					public VV next() {
						if (!hasNext())
							throw new NoSuchElementException();
						VV result = next;
						next = null;
						return result;
					}
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
}
