package peggy.revert;

import java.util.HashMap;
import java.util.Map;

import peggy.represent.PEGInfo;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This is a reversion heuristic that simply returns the original PEG.
 */
public abstract class OriginalReversionHeuristic<O,P,R,N extends Number> 
implements ReversionHeuristic<O,P,R,N> {
	public Map<? extends CPEGValue<O,P>, ? extends CPEGTerm<O,P>> chooseReversionNodes(
			CPeggyAxiomEngine<O,P> engine, 
			PEGInfo<O,P,R> original, 
			Map<? extends Vertex<FlowValue<P,O>>, ? extends CPEGTerm<O,P>> originalMap) {
		Map<CPEGValue<O,P>,CPEGTerm<O,P>> result = 
			new HashMap<CPEGValue<O,P>, CPEGTerm<O,P>> ();
		for (Vertex<FlowValue<P,O>> vertex : originalMap.keySet()) {
			CPEGTerm<O,P> term = originalMap.get(vertex);
			result.put(term.getValue(), term);
		}
		return result;
	}
}
