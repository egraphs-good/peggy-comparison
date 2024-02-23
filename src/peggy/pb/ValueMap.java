package peggy.pb;

/**
 * A ValueMap abstractly provides information about values and terms.
 */
public interface ValueMap<V,N> {
	public boolean containsNode(N node);

	/** Throws RuntimeException if this graph 
	 *  does not contain the given node
	 */
	public V getValue(N node);
	public int getArity(N node);
	public V getChildValue(N node, int index);
	
	public Iterable<? extends N> getParentNodes(V value);
	public Iterable<? extends N> getNodes(V value);
}
