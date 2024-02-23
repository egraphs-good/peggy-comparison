package peggy.optimize;

import java.util.Map;

import peggy.represent.PEGInfo;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This interface listens to a PEG2PEGOptimizer and provides callbacks
 * for the various events it produces. 
 */
public interface PEG2PEGListener<L,P,R,M> {
	public void beginFunction(M method);
	public void notifyEngineSetup(
			CPeggyAxiomEngine<L,P> engine,
			Map<Vertex<FlowValue<P,L>>,CPEGTerm<L,P>> rootVertexMap);
	public void notifyEngineCompleted(CPeggyAxiomEngine<L,P> engine);
	public void notifyRevertPEGBuilt(boolean original, PEGInfo<L,P,R> peginfo);
	public void endFunction();
}
