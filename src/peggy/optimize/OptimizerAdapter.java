package peggy.optimize;

import peggy.represent.PEGInfo;
import eqsat.revert.CFGReverter;
import eqsat.revert.ReversionGraph;

/**
 * This is an OptimizerListener that provides default implementations
 * of the callbacks, which do nothing.
 */
public class OptimizerAdapter<L,P,R,CFG,M> 
implements OptimizerListener<L,P,R,CFG,M> {
	public void beginFunction(M function) {}
	public void notifyOriginalPEGBuilt(PEGInfo<L,P,R> peginfo) {}
	public void notifyOptimalPEGBuilt(PEGInfo<L,P,R> peginfo) {}
	public void notifyReversionGraphBuilt(ReversionGraph<P,L> result) {}
	public void notifyCFGReverterBuilt(CFGReverter<P,L,R> reverter) {}
	public void notifyOutputCFGBuilt(CFG cfg) {}
	public void endFunction() {}
}
