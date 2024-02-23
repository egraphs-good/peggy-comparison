package peggy.optimize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import peggy.Loggable;
import peggy.Logger;
import peggy.represent.PEGInfo;
import util.Tag;
import eqsat.meminfer.engine.generalize.ExpressionTightener;
import eqsat.meminfer.engine.generalize.PostMultiGenEPEG;
import eqsat.meminfer.engine.generalize.ProofPostMultiGeneralizer;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This is an abstract framework for EPEG proof generalization.
 * A generalizer starts with a PEG, puts it into an EPEG, runs equality 
 * saturation, then attempts to generalize the proof of equality for the
 * before and after terms.
 */
public abstract class Generalizer<L,P,R,M> implements Loggable {
	private final List<GeneralizerListener<L,P,R,M>> listeners = 
		new ArrayList<GeneralizerListener<L,P,R,M>> ();
	protected Logger logger;

	public Logger getLogger() {
		return this.logger;
	}
	public void setLogger(Logger _logger) {
		this.logger = _logger;
	}
	
	
	public void addListener(GeneralizerListener<L,P,R,M> listener) {
		if (!this.listeners.contains(listener))
			this.listeners.add(listener);
	}
	public void removeListener(GeneralizerListener<L,P,R,M> listener) {
		this.listeners.remove(listener);
	}
	
	protected abstract PEG2PEGOptimizer<L,P,R,M> getPEG2PEGOptimizer();
	
	// may return null if no generalizer can be created
	protected abstract ProofPostMultiGeneralizer<L,CPEGTerm<L,P>,CPEGValue<L,P>>  
	getGeneralizer(
			CPeggyAxiomEngine<L,P> engine,
			CPEGTerm<L,P> left,
			CPEGTerm<L,P> right);
	
	public void generateEPEGs(
			M function,
			Tag<? extends CPEGTerm<L,P>> termTag,
			PEGInfo<L,P,R> inputPEG,
			Collection<? super PostMultiGenEPEG<L,CPEGTerm<L,P>,CPEGValue<L,P>>> epegs) {
		for (GeneralizerListener<L,P,R,M> listener : this.listeners) {
			listener.beginPEG(function, inputPEG, termTag);
		}
		
		PEG2PEGOptimizer<L,P,R,M> peg2peg = this.getPEG2PEGOptimizer();

		for (GeneralizerListener<L,P,R,M> listener : this.listeners) {
			listener.notifyPEG2PEGBuilt(peg2peg);
		}
		
		// add data listener
		PEG2PEGLastDataListener<L,P,R,M> dataListener = 
			new PEG2PEGLastDataListener<L,P,R,M>();
		peg2peg.addListener(dataListener);
		
		// optimize!
		PEGInfo<L,P,R> optimized = peg2peg.optimize(function, inputPEG);
		
		boolean lastOriginal = dataListener.getLastOriginal();
		
		for (GeneralizerListener<L,P,R,M> listener : this.listeners) {
			listener.notifyOptimalPEGBuilt(optimized, lastOriginal);
		}
		
		if (!lastOriginal) {
			// tighten expressions
			Tag<? extends CPEGTerm<L,P>> newTag = 
				ExpressionTightener.tighten(
						dataListener.getLastEngine().getEGraph(),
						optimized.getGraph(),
						termTag);
			
			for (GeneralizerListener<L,P,R,M> listener : this.listeners) {
				listener.notifyExpressionsTightened(newTag);
			}
	
			// add the EPEGs to the list 
			for (R arr : inputPEG.getReturns()) {
				ProofPostMultiGeneralizer<L,CPEGTerm<L,P>,CPEGValue<L,P>> generalizer = 
					this.getGeneralizer(
							dataListener.getLastEngine(),
							dataListener.getLastRootVertexMap().get(inputPEG.getReturnVertex(arr)),
							optimized.getReturnVertex(arr).getTag(newTag));
				if (generalizer != null) {
					epegs.addAll(generalizer.getEPEGs());
					for (GeneralizerListener<L,P,R,M> listener : this.listeners) {
						listener.notifyReturnEPEGsBuilt(
								arr,
								Collections.unmodifiableCollection(generalizer.getEPEGs()));
					}
				}
			}
		}
		
		for (GeneralizerListener<L,P,R,M> listener : this.listeners) {
			listener.endPEG();
		}
	}
}
