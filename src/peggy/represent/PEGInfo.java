package peggy.represent;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import util.graph.CRecursiveExpressionGraph;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;

/**
 * A PEGInfo instance is the main data structure that represents a PEG.
 * It contains a CREG, and tells which returns are in use and which vertices
 * correspond to the roots for those returns.
 */
public abstract class PEGInfo<L,P,R> {
	protected final Map<R,Vertex<FlowValue<P,L>>> return2vertex;
	protected final CRecursiveExpressionGraph<FlowValue<P,L>> graph;
	public PEGInfo(
			CRecursiveExpressionGraph<FlowValue<P,L>> _graph,
			Map<? extends R,? extends Vertex<FlowValue<P,L>>> _return2vertex) {
		this.graph = _graph;
		this.return2vertex = new HashMap<R,Vertex<FlowValue<P,L>>>(_return2vertex);
	}
	public abstract Collection<? extends R> getReturns();
	public CRecursiveExpressionGraph<FlowValue<P,L>> getGraph() {return this.graph;}
	public Vertex<FlowValue<P,L>> getReturnVertex(R arr) {
		if (this.return2vertex.containsKey(arr))
			return this.return2vertex.get(arr);
		else
			throw new IllegalArgumentException(arr.toString());
	}
	public Collection<? extends Vertex<FlowValue<P,L>>> getReturnVertices() {
		Set<Vertex<FlowValue<P,L>>> result = 
			new HashSet<Vertex<FlowValue<P,L>>>();
		for (R arr : getReturns())
			result.add(getReturnVertex(arr));
		return result;
	}
}
