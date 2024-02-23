package peggy.revert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import peggy.Loggable;
import peggy.Logger;
import peggy.represent.PEGInfo;
import util.Tag;
import util.graph.CRecursiveExpressionGraph;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;

/**
 * Rebuilds a PEG based on a mapping from EPEG values to EPEG terms.
 * Also requires a mapping from returns to their values.
 * Optionally can take a Tag<term> that will be used to annotate each 
 * PEG node with the EPEG term that it came from. 
 */
public class GraphReconstructor<O,P,R> implements Loggable {
	private final Map<? extends CPEGValue<O,P>, ? extends CPEGTerm<O,P>> nodeMap;
	private CRecursiveExpressionGraph<FlowValue<P,O>> graph;
	private final Map<R,CPEGValue<O,P>> return2value;
	private Map<R,Vertex<FlowValue<P,O>>> returnMap;
	private final Map<CPEGTerm<O,P>,List<CPEGTerm<O,P>>> rep2children;
	private final Tag<CPEGTerm<O,P>> termTag;
	private Logger logger;
	
	/**
	 * termTag can be null.
	 */
	public GraphReconstructor(
			Tag<CPEGTerm<O,P>> _termTag, 
			Map<? extends CPEGValue<O,P>, ? extends CPEGTerm<O,P>> _nodeMap, 
			Map<? extends R,? extends CPEGValue<O,P>> return2value) {
		this.termTag = _termTag;
		this.nodeMap = new HashMap<CPEGValue<O,P>, CPEGTerm<O,P>>(_nodeMap);
		
		this.rep2children = new HashMap<CPEGTerm<O,P>,List<CPEGTerm<O,P>>>();
		for (CPEGTerm<O,P> usednode : this.nodeMap.values()) {
			List<CPEGTerm<O,P>> children = new ArrayList<CPEGTerm<O,P>>(usednode.getArity());
			this.rep2children.put(usednode, children);
			for (int i = 0; i < usednode.getArity(); i++) {
				CPEGTerm<O,P> child = this.nodeMap.get(usednode.getChild(i).getValue());
				if (child == null)
					throw new RuntimeException("Null child of node: " + usednode + " child " + i);
				children.add(child);
			}
		}
		
		this.return2value = new HashMap<R,CPEGValue<O,P>>(return2value);
	}
	
	public Logger getLogger() {return this.logger;}
	public void setLogger(Logger _logger) {this.logger = _logger;}
	
	/** Lazily evaluate the return map.
	 */
	public Map<R,Vertex<FlowValue<P,O>>> getReturnMap() {
		if (this.returnMap == null) {
			getReversionGraph();
		}
		return this.returnMap;
	}
	
	/**
	 * Combines the results of getReversionGraph and getReturnMap
	 * into a PEGInfo.
	 */
	public PEGInfo<O,P,R> getReversionInfo() {
		CRecursiveExpressionGraph<FlowValue<P,O>> graph = getReversionGraph();
		final Map<R,Vertex<FlowValue<P,O>>> map = getReturnMap();
		return new PEGInfo<O,P,R>(graph, map) {
			public Collection<? extends R> getReturns() {
				return map.keySet();
			}
		};
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

		Map<CPEGTerm<O,P>,Vertex<FlowValue<P,O>>> cached = 
			new HashMap<CPEGTerm<O,P>,Vertex<FlowValue<P,O>>>();

		boolean gotone = false;
		for (R arr : this.return2value.keySet()) {
			gotone = true;
			buildExpression(this.nodeMap.get(this.return2value.get(arr)), newgraph, cached).makeSignificant();
		}
		if (!gotone) {
			if (this.getLogger()!=null) {
				this.getLogger().log("Cannot reconstruct PEG from node set");
			}
			throw new RuntimeException("No expressions were built");
		}
		
		// build return map
		Map<R,Vertex<FlowValue<P,O>>> map = 
			new HashMap<R,Vertex<FlowValue<P,O>>>();
		for (R arrr : this.return2value.keySet()) {
			CPEGValue<O,P> value = this.return2value.get(arrr);
			CPEGTerm<O,P> usedrep = this.nodeMap.get(value);
			if (usedrep==null) {
				if (this.getLogger()!=null) {
					this.getLogger().log("Node set has no entry for return value");
				}
				throw new RuntimeException("No used representation for return "+arrr);
			}
			
			Vertex<FlowValue<P,O>> vertex = cached.get(usedrep);
			if (vertex==null) {
				if (this.getLogger()!=null) {
					this.getLogger().log("Node set has no entry for return value");
				}
				throw new RuntimeException("Null vertex for Rvalue "+arrr);
			}
			
			map.put(arrr, vertex);
		}
		this.returnMap = map;
		
		return newgraph;
	}
	
	private Vertex<FlowValue<P,O>> buildExpression(
			CPEGTerm<O,P> term,
			CRecursiveExpressionGraph<FlowValue<P,O>> newgraph, 
			Map<CPEGTerm<O,P>,Vertex<FlowValue<P,O>>> cached) {
		if (cached.containsKey(term)) {
			if (cached.get(term) == null) {
				Vertex<FlowValue<P,O>> placeholder = 
					newgraph.createPlaceHolder();
				cached.put(term, placeholder);
				if (this.termTag != null)
					placeholder.setTag(this.termTag, term);
				return placeholder;
			} else {
				return cached.get(term);
			}
		}

		List<Vertex<FlowValue<P,O>>> children = 
			new ArrayList<Vertex<FlowValue<P,O>>>(term.getArity());
		List<CPEGTerm<O,P>> repchildren = this.rep2children.get(term);
		cached.put(term, null);
		for (int i = 0; i < term.getArity(); i++) {
			children.add(buildExpression(repchildren.get(i), newgraph, cached));
		}
		
		Vertex<FlowValue<P,O>> vertex = newgraph.getVertex(term.getOp(), children);
		if (cached.get(term) != null) {
			cached.get(term).replaceWith(vertex);
		} else {
			cached.put(term, vertex);
		}

		if (this.termTag != null)
			vertex.setTag(this.termTag, term);
		
		return vertex;
	}
}
