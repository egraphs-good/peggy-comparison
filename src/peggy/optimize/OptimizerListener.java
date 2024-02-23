package peggy.optimize;

import peggy.represent.PEGInfo;
import eqsat.revert.CFGReverter;
import eqsat.revert.ReversionGraph;

/**
 * This interface is for objects that want to be informed of events that occur
 * inside of an Optimizer.
 */
public interface OptimizerListener<L,P,R,CFG,M> {
	public void beginFunction(M function);
	public void notifyOriginalPEGBuilt(PEGInfo<L,P,R> peginfo);
	public void notifyOptimalPEGBuilt(PEGInfo<L,P,R> peginfo);
	public void notifyReversionGraphBuilt(ReversionGraph<P,L> result);
	public void notifyCFGReverterBuilt(CFGReverter<P,L,R> reverter);
	public void notifyOutputCFGBuilt(CFG cfg);
	public void endFunction();
}
