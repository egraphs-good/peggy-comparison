package eqsat.meminfer.peggy.network;

import eqsat.FlowValue;
import util.graph.RecursiveExpressionGraph;

public interface PeggyVertex<O, P> 
		extends RecursiveExpressionGraph.IVertex<
		PeggyExpressionGraph<O,P>,PeggyVertex<O,P>,FlowValue<P,O>> {
}
