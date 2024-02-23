package peggy.ilp;

/**
 * This interface identifies a PEG edge, which names a source node,
 * sink node, and the source node's child index.
 */
public interface PEGEdge<N,R> {
	boolean isSourceReturn();
	R getSourceReturn();
	N getSourceNode();
	
	int getSourceIndex();
	N getSinkNode();
}
