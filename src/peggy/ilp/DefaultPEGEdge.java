package peggy.ilp;

/**
 * This class is a default implementation of the PEGEdge interface.
 */
public class DefaultPEGEdge<N,R> implements PEGEdge<N,R> {
	private final N source, sink;
	private final int sourceIndex;
	private final R root;
	private final boolean isRoot;
	public DefaultPEGEdge(N _source, int _sourceIndex, N _sink) {
		this.isRoot = false;
		this.root = null;
		this.source = _source;
		this.sourceIndex = _sourceIndex;
		this.sink = _sink;
	}
	public DefaultPEGEdge(R _arr, N _sink) {
		this.isRoot = true;
		this.root = _arr;
		this.source = null;
		this.sourceIndex = 0;
		this.sink = _sink;
	}

	public boolean isSourceReturn() {return this.isRoot;}
	public R getSourceReturn() {
		if (!this.isRoot) throw new UnsupportedOperationException();
		else return this.root;
	}
	public N getSourceNode() {
		if (this.isRoot) throw new UnsupportedOperationException();
		else return this.source;
	}
	public int getSourceIndex() {return this.sourceIndex;}
	public N getSinkNode() {return this.sink;}
}
