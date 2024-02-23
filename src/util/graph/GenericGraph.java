package util.graph;

import java.util.Collection;
import java.util.HashSet;

public abstract class GenericGraph
		<G extends Graph<?,? super V>, V extends MutableVertex<?,? extends V>>
		extends AbstractGraph<G,V> implements MutableGraph<G,V> {
	protected final Collection<V> mVertices;
	
	public GenericGraph() {this(new HashSet<V>());}
	protected GenericGraph(Collection<V> vertices) {mVertices = vertices;}

	public Collection<? extends V> getVertices() {return mVertices;}

	public void addVertex(V vertex) {mVertices.add(vertex);}

	public void removeVertex(V vertex) {
		vertex.removeAllChildren();
		vertex.removeAllParents();
		mVertices.remove(vertex);
	}
	
	public void removeVertices(Collection<? extends V> vertices) {
		for (V vertex : vertices)
			removeVertex(vertex);
	}
	
	public void removeAllVertices() {mVertices.clear();}
}
