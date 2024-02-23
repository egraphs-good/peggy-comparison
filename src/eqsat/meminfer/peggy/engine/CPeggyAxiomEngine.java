package eqsat.meminfer.peggy.engine;

import eqsat.OpAmbassador;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.peg.EPEGManager;
import eqsat.meminfer.engine.proof.ProofManager;

public final class CPeggyAxiomEngine<O, P>
		extends PeggyAxiomEngine<O,P,CPEGTerm<O,P>,CPEGValue<O,P>> {
	private final EPEGManager<O,P,CPEGTerm<O,P>,CPEGValue<O,P>> mEGraph;
	
	public CPeggyAxiomEngine(OpAmbassador<O> ambassador) {
		mEGraph = new CPeggyManager<O,P>(ambassador);
	}
	public CPeggyAxiomEngine(
			OpAmbassador<O> ambassador, 
			ProofManager<CPEGTerm<O,P>,CPEGValue<O,P>> proofManager) {
		mEGraph = new CPeggyManager<O,P>(ambassador, proofManager);
	}

	public EPEGManager<O,P,CPEGTerm<O,P>,CPEGValue<O,P>> getEGraph() {
		return mEGraph;
	}
}
