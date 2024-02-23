package util.graph;

import java.util.Collection;
import java.util.HashSet;

public abstract class GenericVertex
		<G extends Graph<?,? extends V>, V extends GenericVertex<?,V>>
		extends AbstractMutableVertex<G,V> {
	protected final G mGraph;
	protected final Collection<V> mChildren, mParents;
	
	public GenericVertex(G graph) {
		this(graph, new HashSet<V>(), new HashSet<V>());
	}
	protected GenericVertex(G graph, Collection<V> children,
			Collection<V> parents) {
		mGraph = graph;
		mChildren = children;
		mParents = parents;
	}

	public G getGraph() {return mGraph;}
	
	public Collection<? extends V> getChildren() {return mChildren;}
	
	public Collection<? extends V> getParents() {return mParents;}

	public void addChild(V vertex) {
		if (mChildren.add(vertex))
			vertex.mParents.add((V)this);
	}

	public void addParent(V vertex) {
		if (mParents.add(vertex))
			vertex.mChildren.add((V)this);
	}

	public void removeChild(V vertex) {
		if (mChildren.remove(vertex))
			vertex.mParents.remove(this);
	}

	public void removeParent(V vertex) {
		if (mParents.remove(vertex))
			vertex.mChildren.remove(this);
	}
}
