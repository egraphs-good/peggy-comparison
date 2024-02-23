package util.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractGraph<G, V extends Vertex<?,?>>
		implements Graph<G,V> {
	public Collection<? extends V> getLeaves() {
		Set<V> leaves = new HashSet<V>();
		for (V vertex : getVertices())
			if (vertex.isLeaf())
				leaves.add(vertex);
		return leaves;
	}

	public Collection<? extends V> getRoots() {
		Set<V> leaves = new HashSet<V>();
		for (V vertex : getVertices())
			if (vertex.isRoot())
				leaves.add(vertex);
		return leaves;
	}
	
	public boolean hasVertex(V vertex) {return getVertices().contains(vertex);}
	
	public String toString() {
		StringBuilder string = new StringBuilder("digraph {\nordering=out;\n");
		for (V vertex : getVertices()) {
			string.append(vertex.hashCode());
			string.append(" [label=\"");
			string.append(vertex.toString().replace("\n", "\\n"));
			string.append("\"];\n");
		}
		for (V vertex : getVertices()) {
			for (Object child : vertex.getChildren()) {
				string.append(vertex.hashCode());
				string.append(" -> ");
				string.append(child.hashCode());
				string.append(";\n");
			}
		}
		string.append("}\n");
		return string.toString();
	}
}
