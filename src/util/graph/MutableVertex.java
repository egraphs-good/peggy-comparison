package util.graph;

public interface MutableVertex<G, V> extends Vertex<G,V> {
	public void addChild(V vertex);
	public void addParent(V vertex);
	public void removeChild(V vertex);
	public void removeParent(V vertex);
	public void removeAllChildren();
	public void removeAllParents();
}
