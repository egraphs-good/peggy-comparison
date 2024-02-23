package peggy.optimize;

import java.util.Map;

import peggy.represent.PEGInfo;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This PEG2PEGListener provides default implementations of the callback
 * methods that do nothing.
 */
public class PEG2PEGAdapter<L,P,R,M> implements PEG2PEGListener<L,P,R,M> {
	public void beginFunction(M function) {}
	public void notifyEngineSetup(
			CPeggyAxiomEngine<L,P> engine,
			Map<Vertex<FlowValue<P,L>>,CPEGTerm<L,P>> rootVertexMap) {}
	public void notifyEngineCompleted(CPeggyAxiomEngine<L,P> engine) {}
	public void notifyRevertPEGBuilt(boolean original, PEGInfo<L,P,R> peginfo) {}
	public void endFunction() {}
}
