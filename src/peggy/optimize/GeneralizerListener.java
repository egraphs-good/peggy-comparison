package peggy.optimize;

import java.util.Collection;

import peggy.represent.PEGInfo;
import util.Tag;
import eqsat.meminfer.engine.generalize.PostMultiGenEPEG;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;

/**
 * This is an interface for objects that wish to be informed of events that
 * occur inside the Generalizer.
 */
public interface GeneralizerListener<L,P,R,M> {
	public void beginPEG(
			M function, 
			PEGInfo<L,P,R> inputPEG, 
			Tag<? extends CPEGTerm<L,P>> termTag);
	public void notifyPEG2PEGBuilt(PEG2PEGOptimizer<L,P,R,M> peg2peg);
	public void notifyOptimalPEGBuilt(
			PEGInfo<L,P,R> optimalPEG, boolean lastOriginal);
	public void notifyExpressionsTightened(Tag<? extends CPEGTerm<L,P>> newTag);
	public void notifyReturnEPEGsBuilt(
			R arr,
			Collection<? extends PostMultiGenEPEG<L,CPEGTerm<L,P>,CPEGValue<L,P>>> epegs);
	public void endPEG();
}
