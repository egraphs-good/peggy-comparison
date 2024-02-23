package peggy.pb;

/**
 * This is the interface for any class that wants to implement a directed graph.
 * It also must provide a means to get the "reverse digraph", which is one
 * that is equal to this one but with the edges reversed.
 * This functionality is needed for SCC computations.
 */
public interface Digraph<N>{
	public int getNodeCount();
	public Iterable<? extends N> getNodes();
	public Iterable<? extends N> getSuccessors(N node);
	public Digraph<N> getReverseDigraph();
}
