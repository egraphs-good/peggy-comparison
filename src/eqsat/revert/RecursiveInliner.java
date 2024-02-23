package eqsat.revert;

import util.Labeled;
import util.Taggable;
import util.graph.OrderedVertex;
import util.graph.RecursiveExpressionGraph;
import util.graph.RecursiveExpressionVertex;

public abstract class RecursiveInliner<L,
		V extends OrderedVertex<?,? extends V> & Labeled<L> & Taggable,
		G extends RecursiveExpressionGraph<G,E,L>,
		E extends RecursiveExpressionVertex<G,E,L>>
		extends Inliner<L,V,G,E> {
	public RecursiveInliner(G graph) {super(graph);}
	
	protected E process(V vertex) {
		if (vertex.hasTag(mConverted)) {
			E converted = vertex.getTag(mConverted);
			if (converted != null)
				return converted;
			converted = mGraph.createPlaceHolder();
			vertex.setTag(mConverted, converted);
			return converted;
		} else {
			vertex.setTag(mConverted, null);
			E converted = shallowCopy(vertex);
			E holder = vertex.setTag(mConverted, converted);
			if (holder != null)
				holder.replaceWith(converted);
			return converted;
		}
	}
}
