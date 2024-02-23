package peggy.analysis;

import java.util.Collection;

import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;

/**
 * This is a vertex iterable that runs over CREG.Vertex instances.
 */
public class CREGVertexIterable<L,P> extends VertexIterable<Vertex<FlowValue<P,L>>> {
	public CREGVertexIterable(Collection<? extends Vertex<FlowValue<P, L>>> roots) {
		super(roots);
	}
	protected Collection<? extends Vertex<FlowValue<P, L>>> getChildren(
			Vertex<FlowValue<P, L>> v) {
		return v.getChildren();
	}
}
