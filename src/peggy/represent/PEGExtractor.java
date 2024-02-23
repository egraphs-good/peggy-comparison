package peggy.represent;

import java.util.Map;

import peggy.Loggable;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/** 
 * This class is used to come up with an output PEG
 * based on the input PEG and the engine. The revertMap
 * is passed in and filled with the return mappings for the 
 * resulting PEG.
 * 
 * @author steppm
 */
public interface PEGExtractor<L,P,R> extends Loggable {
	/**
	 * Returns true if the last extraction that this 
	 * instance performed ended up returning the original 
	 * PEG that was used to created the EPEG
	 */
	public boolean lastChoseOriginal();
	
	/**
	 * Returns a CREG which represents the PEG that was
	 * extracted from the engine.
	 * 
	 * @param peginfo should be the original PEG that was used to build the EPEG
	 */
	public PEGInfo<L,P,R> extractPEG(
			CPeggyAxiomEngine<L,P> engine, 
			PEGInfo<L,P,R> peginfo,
			Map<Vertex<FlowValue<P,L>>, CPEGTerm<L,P>> rootVertexMap);
}


