package peggy.tv;

import java.util.Map;

import peggy.represent.MergedPEGInfo;
import peggy.represent.PEGInfo;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This is an interface for objects that want to be notified of certain
 * occurrences in a TranslationValidator.
 */
public interface TVListener<L,P,R> {
	void beginValidation(
			String functionName1, 
			String functionName2,
			PEGInfo<L,P,R> peginfo1,
			PEGInfo<L,P,R> peginfo2);
	
	void notifyMergedPEGBuilt(MergedPEGInfo<L,P,R> merged);
	
	/**
	 * Means that the merged PEG roots are already equal.
	 * Followed by endValidation.
	 */
	void notifyMergedPEGEqual(MergedPEGInfo<L,P,R> merged);
	
	void notifyEngineSetup(
			CPeggyAxiomEngine<L,P> engine,
			Map<Vertex<FlowValue<P,L>>,CPEGTerm<L,P>> rootVertexMap);
	
	void notifyEngineCompleted(CPeggyAxiomEngine<L,P> engine);
	
	void notifyReturnsEqual(R arr, CPEGTerm<L,P> root1, CPEGTerm<L,P> root2);
	
	void endValidation();
}
