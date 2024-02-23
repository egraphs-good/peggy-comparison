package peggy.optimize;

import java.util.Map;

import peggy.represent.PEGInfo;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This is a PEG2PEGListener that keeps references to the last copy
 * of everything that is passed back through the callback methods, and
 * has methods to access them after the optimization is complete.
 */
public class PEG2PEGLastDataListener<L,P,R,M> extends PEG2PEGAdapter<L,P,R,M> {
	private M lastFunction;
	private CPeggyAxiomEngine<L,P> lastEngine;
	private Map<Vertex<FlowValue<P,L>>,CPEGTerm<L,P>> lastRootVertexMap;
	private boolean lastOriginal;
	private PEGInfo<L,P,R> lastRevertPeginfo;
	
	public void beginFunction(M function) {
		lastFunction = function;
	}
	public void notifyEngineSetup(
			CPeggyAxiomEngine<L,P> engine,
			Map<Vertex<FlowValue<P,L>>,CPEGTerm<L,P>> rootVertexMap) {
		lastEngine = engine;
		lastRootVertexMap = rootVertexMap;
	}
	public void notifyRevertPEGBuilt(
			boolean original, PEGInfo<L,P,R> peginfo) {
		lastOriginal = original;
		lastRevertPeginfo = peginfo;
	}

	// getters 
	
	public M getLastFunction() {return lastFunction;}
	public CPeggyAxiomEngine<L,P> getLastEngine() {return lastEngine;}
	public Map<Vertex<FlowValue<P,L>>,CPEGTerm<L,P>> getLastRootVertexMap() {
		return lastRootVertexMap;
	}
	public boolean getLastOriginal() {return lastOriginal;}
	public PEGInfo<L,P,R> getLastRevertPeginfo() {
		return lastRevertPeginfo;
	}
}
