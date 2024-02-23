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
 * This is a representation of a merged PEG. Both PEGs live inside the
 * same CREG, and the returns vertices of each PEG are noted.
 */
public abstract class MergedPEGInfo<L,P,R> {
	private final Map<R,Vertex<FlowValue<P,L>>> return2vertex1;
	private final Map<R,Vertex<FlowValue<P,L>>> return2vertex2;
	private final CRecursiveExpressionGraph<FlowValue<P,L>> graph;
	
	public MergedPEGInfo(
			CRecursiveExpressionGraph<FlowValue<P,L>> _graph,
			Map<? extends R,? extends Vertex<FlowValue<P,L>>> _return2vertex1,
			Map<? extends R,? extends Vertex<FlowValue<P,L>>> _return2vertex2) {
		this.graph = _graph;
		this.return2vertex1 = 
			new HashMap<R,Vertex<FlowValue<P,L>>>(_return2vertex1);
		this.return2vertex2 = 
			new HashMap<R,Vertex<FlowValue<P,L>>>(_return2vertex2);
	}
	public abstract Collection<? extends R> getReturns();
	public CRecursiveExpressionGraph<FlowValue<P,L>> getGraph() {return this.graph;}
	
	public Vertex<FlowValue<P,L>> getReturnVertex1(R arr) {
		if (this.return2vertex1.containsKey(arr))
			return this.return2vertex1.get(arr);
		else
			throw new IllegalArgumentException();
	}
	public Vertex<FlowValue<P,L>> getReturnVertex2(R arr) {
		if (this.return2vertex2.containsKey(arr)) 
			return this.return2vertex2.get(arr);
		else
			throw new IllegalArgumentException();
	}
	
	public Collection<? extends Vertex<FlowValue<P,L>>> getReturnVertices1() {
		Set<Vertex<FlowValue<P,L>>> result = 
			new HashSet<Vertex<FlowValue<P,L>>>();
		for (R arr : getReturns())
			result.add(getReturnVertex1(arr));
		return result;
	}
	public Collection<? extends Vertex<FlowValue<P,L>>> getReturnVertices2() {
		Set<Vertex<FlowValue<P,L>>> result = 
			new HashSet<Vertex<FlowValue<P,L>>>();
		for (R arr : getReturns())
			result.add(getReturnVertex2(arr));
		return result;
	}
	public Collection<? extends Vertex<FlowValue<P,L>>> getAllReturnVertices() {
		Set<Vertex<FlowValue<P,L>>> result = 
			new HashSet<Vertex<FlowValue<P,L>>>(getReturnVertices1());
		result.addAll(getReturnVertices2());
		return result;
	}
}
