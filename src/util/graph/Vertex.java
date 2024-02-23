package util.graph;

import java.util.Collection;

/** The interface for interacting with vertices of a finite mathematical
 * (directed) graph.
 *
 * @param <G> The type of the graph
 * @param <V> The type of the vertices of the graph
 */
public interface Vertex<G, V> {
	/** Every vertex is assumed to belong to a graph.
	 * Note that a vertex may be removed from a graph
	 * but still believe it belongs to that graph.
	 * @return The graph this vertex belongs to.
	 */
	public G getGraph();
	/** A trick for getting around Java's lack of self-types.
	 * 
	 * @return this but as type V rather than type Vertex<G,V>
	 */
	public V getSelf();
	
	/** A child of this vertex is any vertex with an edge from this vertex
	 * to that vertex.
	 * @return The children of this vertex.
	 */
	public Collection<? extends V> getChildren();
	/** A parent of this vertex is any vertex with an edge from it
	 * to this vertex.
	 * @return The children of this vertex.
	 */
	public Collection<? extends V> getParents();
	/** Not every vertex has children.
	 * 
	 * @return True if this vertex has children.
	 */
	public boolean hasChildren();
	/** A possibly more efficient manner for determining child-parent
	 * relationships.
	 * 
	 * @return True if vertex is a parent of this vertex
	 */
	public boolean hasParent(V vertex);
	/** A possibly more efficient manner for determining child-parent
	 * relationships.
	 * 
	 * @return True if vertex is a child of this vertex
	 */
	public boolean hasChild(V vertex);
	/** A leaf is a vertex with no children.
	 * 
	 * @return True if this vertex is a leaf.
	 */
	public boolean isLeaf();
	/** Not every vertex has parents.
	 * 
	 * @return True if this vertex has parents.
	 */
	public boolean hasParents();
	/** A root is a vertex with no parents.
	 * 
	 * @return True if this vertex is a root.
	 */
	public boolean isRoot();
	/** A possibly more efficient manner for acquiring the number
	 * of children of this vertex.
	 * 
	 * @return The number of children of this vertex
	 */
	public int getChildCount();
	/** A possibly more efficient manner for acquiring the number
	 * of parents of this vertex.
	 * 
	 * @return The number of parents of this vertex
	 */
	public int getParentCount();
}
