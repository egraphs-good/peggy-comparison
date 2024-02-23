package util.graph;

public interface MutableGraph<G, V> extends Graph<G,V> {
	public void addVertex(V vertex);
	public void removeVertex(V vertex);
	public void removeAllVertices();
}
