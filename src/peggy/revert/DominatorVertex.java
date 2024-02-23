package peggy.revert;

import java.util.Collection;


import util.graph.Vertex;

public interface DominatorVertex<G extends DominatorGraph<G,V>, V extends DominatorVertex<G,V>> extends Vertex<G,V> {
	/**
	 * Dominator graphs have an explicit start node.
	 */
	boolean isStart();
	
	/**
	 * Returns the set of vertices dominated by this vertex.
	 */
	Collection<? extends V> getDominated();
	
	/**
	 * Returns the set of vertices that dominate this one.
	 */
	Collection<? extends V> getDominators();

	/**
	 * Returns the number of vertices dominated by this vertex.
	 */
	int getDominatedCount();
	
	/**
	 * Returns the number of vertices that dominate this vertex.
	 */
	int getDominatorCount();
}
