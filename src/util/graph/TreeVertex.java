package util.graph;

public interface TreeVertex<G, V> extends Vertex<G,V> {
	public V getParent();
}
