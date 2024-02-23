package peggy.analysis.llvm;

import peggy.analysis.Analysis;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMParameter;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This analysis has axioms about implications.
 */
public abstract class ImplicationAnalysis extends Analysis<LLVMLabel,LLVMParameter> {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("ImplicationAnalysis: " + message);
	}

	public ImplicationAnalysis(
			Network _network,
			CPeggyAxiomEngine<LLVMLabel, LLVMParameter> _engine) {
		super(_network, _engine);
	}
	
	public void addAll() {
		// TODO add stuff!
	}
}
