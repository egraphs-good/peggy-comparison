package util.graph;

import java.util.Collection;

/** The interface for interacting with a finite mathematical (directed) graph.
 *
 * @param <G> The type of the graph
 * @param <V> The type of the vertices of the graph
 */
public interface Graph<G, V> {
	/** A trick for getting around Java's lack of self-types.
	 * 
	 * @return this but as type G rather than type Graph<G,V>
	 */
	public G getSelf();
	
	/** A Graph is assumed to have a finite number of vertices and edges.
	 * 
	 * @return The vertices of this graph
	 */
	public Collection<? extends V> getVertices();
	/** A root is a vertex with no parents.
	 * 
	 * @return The roots of this graph
	 */
	public Collection<? extends V> getRoots();
	/** A leaf is a vertex with no children.
	 * 
	 * @return The leaves of this graph
	 */
	public Collection<? extends V> getLeaves();
	/** A possibly more efficient manner for determining vertices of a graph.
	 * 
	 * @return True if vertex is a member of this graph
	 */
	public boolean hasVertex(V vertex);
	/** A graph with unique edges guarantees that there is at most one edge
	 * from any vertex to any vertex.
	 * 
	 * @return True if this graph has unique edges
	 */
	//public boolean hasUniqueEdges();
	/** An undirected graph guarantees that the edges from one vertex to
	 * another vertex can be paired with edges in the opposite direction
	 * such that both edges in each pair share every other attribute.
	 * 
	 * @return True if this graph is undirected.
	 */
	//public boolean isUndirected();
	/** A graph is connected if there is an undirected path
	 * from any vertex to any vertex.
	 * 
	 * @return True if this graph is connected.
	 */
	//public boolean isConnected();
	/** A graph is strongly connected if there is a directed path
	 * from any vertex to any vertex.
	 * 
	 * @return True if this graph is strongly connected.
	 */
	//public boolean isStronglyConnected();
}
