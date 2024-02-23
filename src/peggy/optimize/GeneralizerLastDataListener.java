package peggy.optimize;

import java.util.Collection;

import peggy.represent.PEGInfo;
import util.Tag;
import eqsat.meminfer.engine.generalize.PostMultiGenEPEG;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;

/**
 * This is a GeneralizerListener that keeps copies of references to the 
 * objects that are passed back through the callbacks.
 */
public class GeneralizerLastDataListener<L,P,R,M> 
implements GeneralizerListener<L,P,R,M> {
	private M lastFunction;
	private PEGInfo<L,P,R> lastInputPEG;
	private Tag<? extends CPEGTerm<L,P>> lastInputTag;
	private PEG2PEGOptimizer<L,P,R,M> lastPeg2peg;
	private PEGInfo<L,P,R> lastOptimalPEG;
	private Tag<? extends CPEGTerm<L,P>> lastTightenedTag;
	private Collection<? extends PostMultiGenEPEG<L,CPEGTerm<L,P>,CPEGValue<L,P>>> lastEpegs;
	
	public void beginPEG(
			M function,
			PEGInfo<L,P,R> inputPEG, 
			Tag<? extends CPEGTerm<L,P>> termTag) {
		this.lastFunction = function;
		this.lastInputPEG = inputPEG;
		this.lastInputTag = termTag;
	}
	public void notifyPEG2PEGBuilt(PEG2PEGOptimizer<L,P,R,M> peg2peg) {
		this.lastPeg2peg = peg2peg;
	}
	public void notifyOptimalPEGBuilt(PEGInfo<L,P,R> optimalPEG, boolean lastOriginal) {
		this.lastOptimalPEG = optimalPEG;
	}
	public void notifyExpressionsTightened(Tag<? extends CPEGTerm<L,P>> newTag) {
		this.lastTightenedTag = newTag;
	}
	public void notifyReturnEPEGsBuilt(
			R arr,
			Collection<? extends PostMultiGenEPEG<L,CPEGTerm<L,P>,CPEGValue<L,P>>> epegs) {
		this.lastEpegs = epegs;
	}
	public void endPEG() {}
	
	////////////////////////
	
	public M getLastPEGName() {return this.lastFunction;}
	public PEGInfo<L,P,R> getLastInputPEG() {return this.lastInputPEG;}
	public Tag<? extends CPEGTerm<L,P>> getLastInputTag() {
		return this.lastInputTag;
	}
	public PEG2PEGOptimizer<L,P,R,M> getLastPEG2PEG() {
		return this.lastPeg2peg;
	}
	public PEGInfo<L,P,R> getLastOptimalPEG() {return this.lastOptimalPEG;}
	public Tag<? extends CPEGTerm<L,P>> getLastTightenedTag() {
		return this.lastTightenedTag;
	}
	public Collection<? extends PostMultiGenEPEG<L,CPEGTerm<L,P>,CPEGValue<L,P>>> getLastEpegs() {
		return this.lastEpegs;
	}
}
