package peggy.optimize;

import java.util.Map;

import peggy.represent.PEGInfo;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This is a PEG2PEGListener that times the intervals between the callbacks. 
 * 
 * beginTime < engineSetupTime < engineCompletedTime < revertTime < endTime.
 * All values are relative to the epoch, so you will probably
 * want to compute the difference from beginTime.
 */
public class PEG2PEGTimer<L,P,R,M> implements PEG2PEGListener<L,P,R,M> {
	private long beginTime;
	private long engineSetupTime;
	private long engineCompletedTime;
	private long revertTime;
	private long endTime;
	
	public void beginFunction(M method) {
		beginTime = System.currentTimeMillis();
	}
	public void notifyEngineSetup(
			CPeggyAxiomEngine<L,P> engine,
			Map<Vertex<FlowValue<P,L>>,CPEGTerm<L,P>> rootVertexMap) {
		engineSetupTime = System.currentTimeMillis();
	}
	public void notifyEngineCompleted(CPeggyAxiomEngine<L,P> engine) {
		engineCompletedTime = System.currentTimeMillis();
	}
	public void notifyRevertPEGBuilt(boolean original, PEGInfo<L,P,R> peginfo) { 
		revertTime = System.currentTimeMillis();
	}
	public void endFunction() {
		endTime = System.currentTimeMillis();
	}
	
	////////////////////////////
	
	public long getBeginTime() {
		return beginTime;
	}
	public long getEngineSetupTime() {
		return engineSetupTime;
	}
	public long getEngineCompletedTime() {
		return engineCompletedTime;
	}
	public long getRevertTime() {
		return revertTime;
	}
	public long getEndTime() {
		return endTime;
	}
}
