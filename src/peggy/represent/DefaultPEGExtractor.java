package peggy.represent;

import java.util.Map;

import peggy.revert.ReversionHeuristic;
import util.Tag;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This is the default PEG extractor class.
 */
public class DefaultPEGExtractor<L,P,R> extends AbstractPEGExtractor<L,P,R> {
	public DefaultPEGExtractor(ReversionHeuristic<L,P,R,Integer> _heuristic) {
		super(null, _heuristic);
	}
	public DefaultPEGExtractor(
			Tag<CPEGTerm<L,P>> _termTag,
			ReversionHeuristic<L,P,R,Integer> _heuristic) {
		super(_termTag, _heuristic);
	}
	
	protected Map<? extends CPEGValue<L,P>, ? extends CPEGTerm<L,P>> 
	getNodeMap(
			CPeggyAxiomEngine<L,P> engine, 
			PEGInfo<L,P,R> peginfo,
			Map<Vertex<FlowValue<P,L>>, CPEGTerm<L,P>> rootVertexMap) {
		return heuristic.chooseReversionNodes(engine, peginfo, rootVertexMap);
	}
}
