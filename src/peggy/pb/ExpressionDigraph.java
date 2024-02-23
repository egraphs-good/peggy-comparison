package peggy.pb;

import peggy.represent.StickyPredicate;

/**
 * This is a Digraph that has ordered vertices, and where each vertex lives
 * inside a valuegroup.
 */
public interface ExpressionDigraph<V,N> extends Digraph<N>{
	public int getArity(N node);
	public Iterable<? extends V> getValues();
	public Iterable<? extends V> getRootValues();
	public boolean isRoot(V value);
	public boolean isRecursive(N node);
	public Iterable<? extends N> getValueElements(V v);
	public boolean contains(V v, N n);
	public V getElementValue(N n);

	/** This will get the successors that have the 
	 *  given label on the edge.
	 */
	public V getChildValue(N node, int label);
	
	/** This returns a predicate that takes a child index 'i' and a node,
	 *  and returns true iff the given node is sticky to its i-th child.
	 */
	public StickyPredicate<N> getStickyPredicate();
	
	/** This method will return true iff the
	  * given parent node is in fact a parent of 
	  * the given child node.
	  */
	public boolean isSuccessor(N parent, int index, N child);
}