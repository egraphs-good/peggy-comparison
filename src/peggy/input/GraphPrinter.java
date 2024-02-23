package peggy.input;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import peggy.analysis.CREGVertexIterable;
import peggy.represent.MergedPEGInfo;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;

/**
 * Contains various methods for printing out graphs.
 */
public class GraphPrinter {
	public static <L,P> void printPEG(
			PrintStream out,
			Set<? extends Vertex<FlowValue<P,L>>> roots) {
		out.println("graph: {");
		out.println("   title:\"PEG\"");
		CREGVertexIterable<L, P> iterable = new CREGVertexIterable<L, P>(roots);
		for (Vertex<FlowValue<P,L>> vertex : iterable) {
			int myhash = vertex.hashCode();
			out.println("   node: {title:\"" + myhash + "\"");
			out.println("          label:\"" + vertex.getLabel().toString() + "\"}");
			for (int i = 0; i < vertex.getChildCount(); i++) {
				int hishash = vertex.getChild(i).hashCode();
				out.println("   edge: {source:\"" + myhash + "\"");
				out.println("          target:\"" + hishash + "\"");
				out.println("          arrowstyle:line}");
			}
		}
		out.println("}");
	}
	
	public static <L,P,R> void printMergedPEG(
			PrintStream out,
			MergedPEGInfo<L,P,R> merged) {
		Set<Vertex<FlowValue<P,L>>> roots = new HashSet<Vertex<FlowValue<P,L>>>();
		for (R arr : merged.getReturns()) {
			roots.add(merged.getReturnVertex1(arr));
			roots.add(merged.getReturnVertex2(arr));
		}
		GraphPrinter.<L,P>printPEG(out, roots);
	}
	
	
	public static <L,P> void printRootPairDot(
			PrintStream out,
			Vertex<FlowValue<P,L>> root1,
			Vertex<FlowValue<P,L>> root2) {
		out.println("digraph {");
		out.println("   ordering=out;");
		Map<Vertex<FlowValue<P,L>>,Integer> cache = 
			new HashMap<Vertex<FlowValue<P,L>>, Integer>();
		int[] refid = {0};
		
		if (root1.getLabel().equals(root2.getLabel()) &&
			root1.getChildCount() == root2.getChildCount()) {
			GraphPrinter.<L,P>printRootPairDot(out, root1, root2, cache, refid);
			GraphPrinter.<L,P>printRootPairDot(out, root2, root1, cache, refid);
		} else {
			GraphPrinter.<L,P>printRootPairDot(out, root1, null, cache, refid);
			GraphPrinter.<L,P>printRootPairDot(out, root2, null, cache, refid);
		}
		out.println("}");
	}
	private static <L,P> String printRootPairDot(
			PrintStream out,
			Vertex<FlowValue<P,L>> root,
			Vertex<FlowValue<P,L>> pair,
			Map<Vertex<FlowValue<P,L>>,Integer> cache,
			int[] refid) {
		if (cache.containsKey(root)) {
			final int mynodeid = cache.get(root);
			final int myrefid = refid[0]++;
			final String name = "ref" + myrefid;
			final String color = (pair==null ? "blue" : "yellow");
			out.println("   " + name + " [label=\"[" + mynodeid + "]  " + root.getLabel() + "\", shape=box, color=" + color + "];");
			return name;
		}

		final int mynodeid = cache.size();
		cache.put(root, mynodeid);
		
		final String color = (pair==null ? "black" : "yellow");
		
		final String name = "node" + mynodeid;
		out.println("   " + name + " [label=\"[" + mynodeid + "]  " + root.getLabel() + "\", color=" + color + "];");
		for (int i = 0; i < root.getChildCount(); i++) {
			Vertex<FlowValue<P,L>> rootChild = root.getChild(i);
			Vertex<FlowValue<P,L>> pairChild = null;
			if (pair!=null) {
				pairChild = pair.getChild(i);
				if (!(rootChild.getLabel().equals(pairChild.getLabel()) &&
					  rootChild.getChildCount() == pairChild.getChildCount()))
					pairChild = null;
			}
			String childname = GraphPrinter.<L,P>printRootPairDot(out, rootChild, pairChild, cache, refid);
			out.println("   " + name + " -> " + childname + " ;");
		}
		
		return name;
	}
}
