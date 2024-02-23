package peggy.tv;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import peggy.represent.MergedPEGInfo;
import peggy.represent.PEGInfo;
import util.graph.CRecursiveExpressionGraph.Vertex;
import util.pair.Pair;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This is a TVListener that holds on to the most recent copies of the
 * objects it is passed in the callbacks.
 */
public class TVLastDataListener<L,P,R> implements TVListener<L,P,R> {
	private String lastFunctionName1;
	private String lastFunctionName2;
	private PEGInfo<L,P,R> lastOriginalPEG1;
	private PEGInfo<L,P,R> lastOriginalPEG2;
	private MergedPEGInfo<L,P,R> lastMergedPEG;
	private CPeggyAxiomEngine<L,P> lastEngine;
	private Map<Vertex<FlowValue<P,L>>,CPEGTerm<L,P>> lastRootVertexMap;
	private Map<R,Pair<CPEGTerm<L,P>,CPEGTerm<L,P>>> lastReturnsEqualMap =
		new HashMap<R,Pair<CPEGTerm<L,P>,CPEGTerm<L,P>>>();
	private boolean lastMergedEqual = false;
	
	public void beginValidation(
			String functionName1, 
			String functionName2,
			PEGInfo<L,P,R> peginfo1,
			PEGInfo<L,P,R> peginfo2) {
		this.lastFunctionName1 = functionName1;
		this.lastFunctionName2 = functionName2;
		this.lastOriginalPEG1 = peginfo1;
		this.lastOriginalPEG2 = peginfo2;
		this.lastReturnsEqualMap.clear();
	}
	public void notifyMergedPEGBuilt(MergedPEGInfo<L,P,R> merged) {
		this.lastMergedPEG = merged;
	}
	public void notifyMergedPEGEqual(MergedPEGInfo<L, P, R> merged) {
		lastMergedEqual = true;
	}
	public void notifyEngineSetup(
			CPeggyAxiomEngine<L,P> engine,
			Map<Vertex<FlowValue<P,L>>,CPEGTerm<L,P>> rootVertexMap) {
		this.lastEngine = engine;
		this.lastRootVertexMap = rootVertexMap;
	}
	public void notifyEngineCompleted(CPeggyAxiomEngine<L,P> engine) {}
	public void notifyReturnsEqual(R arr, CPEGTerm<L,P> root1, CPEGTerm<L,P> root2) {
		this.lastReturnsEqualMap.put(
				arr, 
				new Pair<CPEGTerm<L,P>,CPEGTerm<L,P>>(root1, root2)); 
	}
	public void endValidation() {}
	
	///////////////////////////////
	
	public String getLastFunctionName1() {return lastFunctionName1;}
	public String getLastFunctionName2() {return lastFunctionName2;}
	public PEGInfo<L,P,R> getLastOriginalPEG1() {return lastOriginalPEG1;}
	public PEGInfo<L,P,R> getLastOriginalPEG2() {return lastOriginalPEG2;}
	public MergedPEGInfo<L,P,R> getLastMergedPEG() {return lastMergedPEG;}
	public boolean getLastMergedEqual() {return lastMergedEqual;}
	public CPeggyAxiomEngine<L,P> getLastEngine() {return lastEngine;}
	public Map<Vertex<FlowValue<P,L>>,CPEGTerm<L,P>> getLastRootVertexMap() {
		return lastRootVertexMap;
	}
	public Set<R> getValidatedReturns() {
		return Collections.unmodifiableSet(lastReturnsEqualMap.keySet());
	}
	public boolean hasValidatedReturn(R arr) {
		return lastReturnsEqualMap.containsKey(arr);
	}
	public Pair<CPEGTerm<L,P>,CPEGTerm<L,P>> getValidatedPair(R arr) {
		return lastReturnsEqualMap.get(arr);
	}
}
