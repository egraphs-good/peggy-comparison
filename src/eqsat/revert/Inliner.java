package eqsat.revert;

import java.util.ArrayList;
import java.util.List;

import util.Labeled;
import util.NamedTag;
import util.Tag;
import util.Taggable;
import util.graph.ExpressionGraph;
import util.graph.ExpressionVertex;
import util.graph.OrderedVertex;

public abstract class Inliner<L,
		V extends OrderedVertex<?,? extends V> & Labeled<L> & Taggable,
		G extends ExpressionGraph<G,E,L>, E extends ExpressionVertex<G,E,L>> {
	protected final Tag<E> mConverted = new NamedTag<E>("Inlined");
	protected final G mGraph;
	
	public Inliner(G graph) {mGraph = graph;}
	
	protected abstract boolean inline(V vertex);
	protected abstract E inlineAs(V vertex);
	
	public final E get(V vertex) {
		if (!vertex.hasTag(mConverted) && inline(vertex))
			return inlineAs(vertex);
		else
			return process(vertex);
	}
	
	protected E process(V vertex) {
		if (vertex.hasTag(mConverted))
			return vertex.getTag(mConverted);
		E result = shallowCopy(vertex);
		vertex.setTag(mConverted, result);
		return result;
	}
	
	protected E shallowCopy(V vertex) {
		if (vertex.isLeaf())
			return mGraph.getVertex(vertex.getLabel());
		else if (vertex.getChildCount() == 1) {
			E child = get(vertex.getChild(0));
			return mGraph.getVertex(vertex.getLabel(), child);
		} else {
			List<E> children = new ArrayList<E>(vertex.getChildCount());
			for (int i = 0; i != vertex.getChildCount(); i++)
				children.add(get(vertex.getChild(i)));
			return mGraph.getVertex(vertex.getLabel(), children);
		}
	}
};
