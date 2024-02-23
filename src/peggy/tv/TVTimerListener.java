package peggy.tv;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import peggy.represent.MergedPEGInfo;
import peggy.represent.PEGInfo;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This is a TVListener that times the intervals between the callbacks.
 */
public class TVTimerListener<L,P,R> implements TVListener<L,P,R> {
	private long beginValidationTime;
	private long mergedPEGBuiltTime;
	private long engineSetupTime;
	private long engineCompletedTime;
	private long endValidationTime;
	private final Map<R,Long> returnsValidatedTimeMap = new HashMap<R,Long>();
	
	public void beginValidation(
			String functionName1, 
			String functionName2,
			PEGInfo<L,P,R> peginfo1,
			PEGInfo<L,P,R> peginfo2) {
		beginValidationTime = System.currentTimeMillis();
		returnsValidatedTimeMap.clear();
	}
	public void notifyMergedPEGBuilt(MergedPEGInfo<L,P,R> merged) {
		mergedPEGBuiltTime = System.currentTimeMillis();
	}
	public void notifyMergedPEGEqual(MergedPEGInfo<L, P, R> merged) {}
	public void notifyEngineSetup(
			CPeggyAxiomEngine<L,P> engine,
			Map<Vertex<FlowValue<P,L>>,CPEGTerm<L,P>> rootVertexMap) {
		engineSetupTime = System.currentTimeMillis();
	}
	public void notifyEngineCompleted(CPeggyAxiomEngine<L,P> engine) {
		engineCompletedTime = System.currentTimeMillis();
	}
	public void notifyReturnsEqual(R arr, CPEGTerm<L,P> root1, CPEGTerm<L,P> root2) {
		returnsValidatedTimeMap.put(arr, System.currentTimeMillis());
	}
	public void endValidation() {
		endValidationTime = System.currentTimeMillis();
	}
	
	///////////////////
	
	public long getBeginValidationTime() {return beginValidationTime;}
	public long getMergedPEGBuiltTime() {return mergedPEGBuiltTime;}
	public long getEngineSetupTime() {return engineSetupTime;}
	public long getEngineCompletedTime() {return engineCompletedTime;}
	public long getEndValidationTime() {return endValidationTime;}
	public Set<R> getValidatedReturns() {
		return Collections.unmodifiableSet(returnsValidatedTimeMap.keySet());
	}
	public boolean hasValidatedReturn(R arr) {
		return returnsValidatedTimeMap.containsKey(arr);
	}
	public long getValidatedTime(R arr) {
		return returnsValidatedTimeMap.get(arr);
	}
}
