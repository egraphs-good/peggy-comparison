package peggy.pb;

import java.util.HashMap;
import java.util.Map;

import peggy.represent.PEGInfo;
import peggy.revert.AbstractReversionHeuristic;
import peggy.revert.ReversionHeuristic;
import util.Tag;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This class assumes that the input PEG has been tagged with its 
 * Terms.
 */
public abstract class NonPessimizingHeuristic<O,P,R,N extends Number> 
extends AbstractReversionHeuristic<O,P,R,N> {

	private final ReversionHeuristic<O,P,R,N> attempt;
	private final Tag<CPEGTerm<O,P>> term_tag;
	
	public NonPessimizingHeuristic(
			ReversionHeuristic<O,P,R,N> _attempt,
			Tag<CPEGTerm<O,P>> _tag) {
		this.attempt = _attempt;
		this.term_tag = _tag;
	}
	
	public Map<? extends CPEGValue<O, P>, ? extends CPEGTerm<O, P>> chooseReversionNodes(
			CPeggyAxiomEngine<O, P> engine,
			PEGInfo<O, P, R> original,
			Map<? extends Vertex<FlowValue<P, O>>, ? extends CPEGTerm<O, P>> originalMap) {
		Map<? extends CPEGValue<O, P>, ? extends CPEGTerm<O, P>> result =
			this.attempt.chooseReversionNodes(engine, original, originalMap);
		CostModel<CPEGTerm<O,P>,N> costModel = this.getCostModel();

		boolean useOriginal = false;
		if (result == null) {
			useOriginal = true;
		} else {
			int originalCost = 0;
			for (Vertex<FlowValue<P,O>> vertex : original.getGraph().getVertices()) {
				originalCost += costModel.cost(vertex.getTag(term_tag)).intValue();
			}
			
			int newCost = 0;
			for (CPEGValue<O,P> value : result.keySet()) {
				newCost += costModel.cost(result.get(value)).intValue();
			}
			
			if (originalCost < newCost)
				useOriginal = true;
		}
		
		if (useOriginal) {
			Map<CPEGValue<O, P>, CPEGTerm<O, P>> newresult = 
				new HashMap<CPEGValue<O,P>,CPEGTerm<O,P>>();
			newresult.clear();
			for (Vertex<FlowValue<P,O>> vertex : original.getGraph().getVertices()) {
				CPEGTerm<O,P> term = vertex.getTag(term_tag);
				newresult.put(term.getValue(), term);
			}
			return newresult;
		} else {
			return result;
		}
	}
}
