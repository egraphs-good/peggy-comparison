package util.graph;

public interface MTreeVertex<G, V> extends TreeVertex<G,V> {
	public void setParent(V parent);
}
