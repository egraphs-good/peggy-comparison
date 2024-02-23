package peggy.represent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.graph.CRecursiveExpressionGraph;
import util.graph.CRecursiveExpressionGraph.Vertex;
import util.pair.Pair;
import eqsat.FlowValue;

/**
 * This class takes 2 PEGInfos and returns a MergedPEGInfo.
 * While it is building the MergedPEGInfo, it performs theta merging
 * on all the nodes in both PEGs.
 * @author mstepp
 */
public abstract class PEGMerger<L,P,R> {
	private final PEGInfo<L,P,R> peg1, peg2;
	public PEGMerger(PEGInfo<L,P,R> _peg1, PEGInfo<L,P,R> _peg2) {
		this.peg1 = _peg1;
		this.peg2 = _peg2;
	}
	
	protected abstract boolean equalConstants(
			FlowValue<P,L> left, FlowValue<P,L> right);
	
	private Vertex<FlowValue<P,L>> find(
			Map<Vertex<FlowValue<P,L>>,Vertex<FlowValue<P,L>>> unionfind,
			Vertex<FlowValue<P,L>> node) {
		if (unionfind.containsKey(node)) {
			// path compression!
			Vertex<FlowValue<P,L>> root = find(unionfind, unionfind.get(node));
			unionfind.put(node, root);
			return root;
		} else
			return node;
	}
	private void union(
			Map<Vertex<FlowValue<P,L>>,Vertex<FlowValue<P,L>>> unionfind,
			Vertex<FlowValue<P,L>> node1,
			Vertex<FlowValue<P,L>> node2) {
		Vertex<FlowValue<P,L>> root1 = find(unionfind, node1);
		Vertex<FlowValue<P,L>> root2 = find(unionfind, node2);
		if (root1.equals(root2))
			return;
		unionfind.put(root1, root2);
	}
	
	public MergedPEGInfo<L,P,R> mergePEGs() {
		Map<Vertex<FlowValue<P,L>>,Vertex<FlowValue<P,L>>> unionfind = 
			new HashMap<Vertex<FlowValue<P,L>>,Vertex<FlowValue<P,L>>>();
		for (Vertex<FlowValue<P,L>> left : this.peg1.getGraph().getVertices()) {
			for (Vertex<FlowValue<P,L>> right : this.peg2.getGraph().getVertices()) {
				if (nodesEqual(left, right, unionfind)) {
					union(unionfind, left, right);
				}
			}
		}
		
		// found all equiv nodes, build merged peg
		CRecursiveExpressionGraph<FlowValue<P,L>> newgraph = 
			new CRecursiveExpressionGraph<FlowValue<P,L>>();
		Map<Vertex<FlowValue<P,L>>,Vertex<FlowValue<P,L>>> cache = 
			new HashMap<Vertex<FlowValue<P,L>>,Vertex<FlowValue<P,L>>>();
		final Map<R,Vertex<FlowValue<P,L>>> rmap1 =  
			new HashMap<R,Vertex<FlowValue<P,L>>>();
		final Map<R,Vertex<FlowValue<P,L>>> rmap2 =  
			new HashMap<R,Vertex<FlowValue<P,L>>>();
		for (R arr : this.peg1.getReturns()) {
			Vertex<FlowValue<P,L>> r1 = 
				build(this.peg1.getReturnVertex(arr), newgraph, unionfind, cache);
			r1.makeSignificant();
			rmap1.put(arr, r1);
			
			Vertex<FlowValue<P,L>> r2 = 
				build(this.peg2.getReturnVertex(arr), newgraph, unionfind, cache);
			r2.makeSignificant();
			rmap2.put(arr, r2);
		}
		
		return new MergedPEGInfo<L,P,R>(newgraph, rmap1, rmap2) {
			public Collection<? extends R> getReturns() {
				return rmap1.keySet();
			}
		};
	}
	
	private Vertex<FlowValue<P,L>> build(
			Vertex<FlowValue<P,L>> node,
			CRecursiveExpressionGraph<FlowValue<P,L>> newgraph,
			Map<Vertex<FlowValue<P,L>>,Vertex<FlowValue<P,L>>> unionfind,
			Map<Vertex<FlowValue<P,L>>,Vertex<FlowValue<P,L>>> cache) {
		node = find(unionfind, node);
		if (cache.containsKey(node)) {
			Vertex<FlowValue<P,L>> result = cache.get(node);
			if (result == null) {
				result = newgraph.createPlaceHolder();
				cache.put(node, result);
			}
			return result;
		}
		
		cache.put(node, null);
		List<Vertex<FlowValue<P,L>>> children = 
			new ArrayList<Vertex<FlowValue<P,L>>>();
		for (int i = 0; i < node.getChildCount(); i++) {
			children.add(build(node.getChild(i), newgraph, unionfind, cache));
		}

		Vertex<FlowValue<P,L>> result = newgraph.getVertex(
				node.getLabel(), children);
		if (cache.get(node) != null)
			cache.get(node).replaceWith(result);
		cache.put(node, result);
		return result;
	}
	
	
	
	
	private boolean nodesEqual(
			Vertex<FlowValue<P,L>> left,
			Vertex<FlowValue<P,L>> right,
			Map<Vertex<FlowValue<P,L>>,Vertex<FlowValue<P,L>>> unionfind) {
		Vertex<FlowValue<P,L>> leftRoot = find(unionfind, left);
		Vertex<FlowValue<P,L>> rightRoot = find(unionfind, right);
		if (leftRoot.equals(rightRoot))
			return true;
		
		Set<Pair<Vertex<FlowValue<P,L>>,Vertex<FlowValue<P,L>>>> seen = 
			new HashSet<Pair<Vertex<FlowValue<P,L>>,Vertex<FlowValue<P,L>>>>();
		LinkedList<Pair<Vertex<FlowValue<P,L>>,Vertex<FlowValue<P,L>>>> worklist = 
			new LinkedList<Pair<Vertex<FlowValue<P,L>>,Vertex<FlowValue<P,L>>>>();
		worklist.addLast(new Pair<Vertex<FlowValue<P,L>>,Vertex<FlowValue<P,L>>>(left, right));
		while (worklist.size() > 0) {
			Pair<Vertex<FlowValue<P,L>>,Vertex<FlowValue<P,L>>> next = worklist.removeFirst();
			if (seen.contains(next))
				continue;
			seen.add(next);
			
			final Vertex<FlowValue<P,L>> l = next.getFirst();
			final Vertex<FlowValue<P,L>> r = next.getSecond();
			if (find(unionfind, l).equals(find(unionfind, r)))
				continue;
			if (l.getChildCount() != r.getChildCount())
				return false;
			if (l.getChildCount() == 0 &&
				equalConstants(l.getLabel(), r.getLabel()))
				continue;
			if (!l.getLabel().equals(r.getLabel()))
				return false;
			
			for (int i = 0; i < l.getChildCount(); i++) {
				worklist.addLast(
						new Pair<Vertex<FlowValue<P,L>>,Vertex<FlowValue<P,L>>>(
								l.getChild(i), r.getChild(i)));
			}
		}

		return true;
	}
}
