package peggy.represent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eqsat.FlowValue;

import util.Taggable;
import util.graph.CRecursiveExpressionGraph;

/**
 * A PEG is an immutable data structure that represents a single function of
 * a program. It is a directed graph with vertices that have ordered outgoing 
 * edges. There are a few vertices designated as 'roots', and they represent
 * the output values of the function. For each root there is an R value that 
 * corresponds to it. The T value is the label type of the vertices. 
 */
public abstract class PEG<T,R,P extends PEG<T,R,P,V>,V extends PEG.Vertex<T,R,P,V>> {
	public static interface Vertex<L,R,P extends PEG<L,R,P,V>,V extends Vertex<L,R,P,V>> extends Taggable {
		public int getChildCount();
		public V getChild(int i);
		public L getLabel();
		public P getPEG();
		public boolean isRoot();
	}
	
	public abstract Iterable<? extends V> getVertices();
	public abstract Set<? extends R> getReturns();
	public abstract V getReturnVertex(R arr);
	public abstract Set<? extends V> getReturnVertices();
	
	
	/**
	 * Turns a PEG into a PEGInfo.
	 */
	public static <L,P,R,PP extends PEG<FlowValue<P,L>,R,PP,VV>, VV extends Vertex<FlowValue<P,L>,R,PP,VV>> 
	PEGInfo<L,P,R> toPEGInfo(PP peg) {
		final CRecursiveExpressionGraph<FlowValue<P,L>> graph = 
			new CRecursiveExpressionGraph<FlowValue<P,L>>();
		final Map<VV,CRecursiveExpressionGraph.Vertex<FlowValue<P,L>>> cache = 
			new HashMap<VV,CRecursiveExpressionGraph.Vertex<FlowValue<P,L>>>();
		
		final Map<R,CRecursiveExpressionGraph.Vertex<FlowValue<P,L>>> roots = 
			new HashMap<R, CRecursiveExpressionGraph.Vertex<FlowValue<P,L>>>();
		for (R arr : peg.getReturns()) {
			roots.put(arr, PEG.<L,P,R,PP,VV>getVertex(graph, peg.getReturnVertex(arr), cache));
		}

		return new PEGInfo<L,P,R>(graph, roots) {
			public Collection<? extends R> getReturns() {
				return roots.keySet();
			}
		};
	}
	private static <L,P,R,PP extends PEG<FlowValue<P,L>,R,PP,VV>, VV extends Vertex<FlowValue<P,L>,R,PP,VV>> 
	CRecursiveExpressionGraph.Vertex<FlowValue<P,L>> getVertex(
			CRecursiveExpressionGraph<FlowValue<P,L>> peg,
			VV vertex,
			Map<VV,CRecursiveExpressionGraph.Vertex<FlowValue<P,L>>> cache) {
		if (cache.containsKey(vertex)) {
			CRecursiveExpressionGraph.Vertex<FlowValue<P,L>> result = 
				cache.get(vertex);
			if (result == null) {
				result = peg.createPlaceHolder();
				cache.put(vertex, result);
			}
			return result;
		}
		
		cache.put(vertex, null);
		List<CRecursiveExpressionGraph.Vertex<FlowValue<P,L>>> children = 
			new ArrayList<CRecursiveExpressionGraph.Vertex<FlowValue<P,L>>>();
		for (int i = 0; i < vertex.getChildCount(); i++) {
			children.add(PEG.<L,P,R,PP,VV>getVertex(peg, vertex.getChild(i), cache));
		}
		CRecursiveExpressionGraph.Vertex<FlowValue<P,L>> result = 
			peg.getVertex(vertex.getLabel(), children);
		if (cache.get(vertex) != null)
			cache.get(vertex).replaceWith(result);
		cache.put(vertex, result);
		return result;
	}
}
