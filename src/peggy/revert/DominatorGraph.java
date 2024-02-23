package peggy.revert;

import util.graph.Graph;

/**
 * This is the graph type that is used in dominator computations.
 */
public interface DominatorGraph<G extends DominatorGraph<G,V>, V extends DominatorVertex<G,V>> extends Graph<G,V> {
	/**
	 * Returns the start vertex of this graph.
	 */
	V getStart();
}
