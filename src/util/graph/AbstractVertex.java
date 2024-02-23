package util.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractVertex
		<G extends Graph<?,? extends V>, V extends Vertex<?,? super V>>
		implements Vertex<G,V> {
	public boolean hasChildren() {return !getChildren().isEmpty();}

	public boolean hasParents() {return !getParents().isEmpty();}

	public boolean isLeaf() {return getChildren().isEmpty();}

	public boolean isRoot() {return getParents().isEmpty();}
	
	public boolean hasChild(V vertex) {return getChildren().contains(vertex);}
	
	public boolean hasParent(V vertex) {return getParents().contains(vertex);}

	public Collection<? extends V> getChildren() {
		Set<V> children = new HashSet<V>();
		for (V vertex : getGraph().getVertices())
			if (vertex.hasParent(getSelf()))
				children.add(vertex);
		return children;
	}
	
	public Collection<? extends V> getParents() {
		Set<V> children = new HashSet<V>();
		for (V vertex : getGraph().getVertices())
			if (vertex.hasChild(getSelf()))
				children.add(vertex);
		return children;
	}
	
	public int getChildCount() {return getChildren().size();}
	
	public int getParentCount() {return getParents().size();}
}
