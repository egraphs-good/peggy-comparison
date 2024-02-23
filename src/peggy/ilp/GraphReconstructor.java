package peggy.ilp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import peggy.AbstractLoggable;
import util.Tag;
import util.graph.CRecursiveExpressionGraph;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;

/**
 * This class reconstructs a PEG from the output description of the GLPK
 * formulation.
 */
public class GraphReconstructor<O,P,R> extends AbstractLoggable {
	private final Collection<PEGEdge<CPEGTerm<O,P>,R>> edges;
	private CRecursiveExpressionGraph<FlowValue<P,O>> graph;
	private Map<R,Vertex<FlowValue<P,O>>> returnMap;
	private final Tag<CPEGTerm<O,P>> termTag;
	
	/**
	 * termTag can be null.
	 */
	public GraphReconstructor(
			Tag<CPEGTerm<O,P>> _termTag, 
			Collection<? extends PEGEdge<CPEGTerm<O,P>,R>> _edges) { 
		this.termTag = _termTag;
		this.edges = new ArrayList<PEGEdge<CPEGTerm<O,P>,R>>(_edges);
	}
	
	/** Lazily evaluate the return map.
	 */
	public Map<R,Vertex<FlowValue<P,O>>> getReturnMap() {
		if (this.returnMap == null) {
			getReversionGraph();
		}
		return this.returnMap;
	}
	
	/** Lazily evaluate the reversion graph.
	 */
	public CRecursiveExpressionGraph<FlowValue<P,O>> getReversionGraph() {
		if (this.graph == null) {
			this.graph = reconstructGraph();
		}
		return this.graph;
	}
	private CRecursiveExpressionGraph<FlowValue<P,O>> reconstructGraph() {
		CRecursiveExpressionGraph<FlowValue<P,O>> newgraph = 
			new CRecursiveExpressionGraph<FlowValue<P,O>>();
		Map<CPEGTerm<O,P>,Vertex<FlowValue<P,O>>> term2placeholder = 
			new HashMap<CPEGTerm<O,P>,Vertex<FlowValue<P,O>>>();
		Map<R,Vertex<FlowValue<P,O>>> returnMap = 
			new HashMap<R,Vertex<FlowValue<P,O>>>();
		Map<CPEGTerm<O,P>,Vertex[]> node2children = 
			new HashMap<CPEGTerm<O,P>,Vertex[]>();  
		
		// create placeholders for all nodes
		for (PEGEdge<CPEGTerm<O,P>,R> edge : edges) {
			CPEGTerm<O,P> sink = edge.getSinkNode();
			if (!term2placeholder.containsKey(sink)) {
				term2placeholder.put(sink, newgraph.createPlaceHolder());
				node2children.put(sink, new Vertex[sink.getArity()]);
			}

			if (!edge.isSourceReturn()) {
				CPEGTerm<O,P> source = edge.getSourceNode();
				if (!term2placeholder.containsKey(source)) {
					term2placeholder.put(source, newgraph.createPlaceHolder());
					node2children.put(source, new Vertex[source.getArity()]);
				}
			} else {
				returnMap.put(edge.getSourceReturn(), term2placeholder.get(sink));
			}
		}
		
		// build children map
		for (PEGEdge<CPEGTerm<O,P>,R> edge : edges) {
			if (edge.isSourceReturn()) continue;
			CPEGTerm<O,P> source = edge.getSourceNode();
			CPEGTerm<O,P> sink = edge.getSinkNode();
			node2children.get(source)[edge.getSourceIndex()] = term2placeholder.get(sink);
		}
		
		// now build replacements
		for (CPEGTerm<O,P> term : term2placeholder.keySet()) {
			Vertex<FlowValue<P,O>> placeholder = term2placeholder.get(term);
			Vertex<FlowValue<P,O>> replacement = newgraph.getVertex(term.getOp(),
					node2children.get(term));
			placeholder.replaceWith(replacement);
			replacement.setTag(termTag, term);
		}
		
		this.returnMap = returnMap;
		
		return newgraph;
	}
}
