package peggy.revert;

import java.util.Map;

import peggy.Loggable;
import peggy.pb.CostModel;
import peggy.represent.PEGInfo;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This is a heuristic to revert an EPEG to a PEG.
 */
public interface ReversionHeuristic<O,P,R,N extends Number> extends Loggable {
	public Map<? extends CPEGValue<O,P>, ? extends CPEGTerm<O,P>> chooseReversionNodes(
			CPeggyAxiomEngine<O,P> engine, 
			PEGInfo<O,P,R> original, 
			Map<? extends Vertex<FlowValue<P,O>>, ? extends CPEGTerm<O,P>> originalMap);
	public CostModel<CPEGTerm<O,P>,N> getCostModel();
}
