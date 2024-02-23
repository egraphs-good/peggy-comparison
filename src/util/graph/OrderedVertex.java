package util.graph;

import java.util.List;

public interface OrderedVertex<G, V> extends Vertex<G,V> {
	List<? extends V> getChildren();
	V getChild(int child);
	boolean hasChildren(V... children);
	boolean hasChildren(List<? extends V> children);
}
