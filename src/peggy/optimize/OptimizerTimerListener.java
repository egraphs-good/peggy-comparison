package peggy.optimize;

import peggy.represent.PEGInfo;
import eqsat.revert.CFGReverter;
import eqsat.revert.ReversionGraph;

/**
 * This is an OptimizerListener that times the intervals between callbacks.
 */
public class OptimizerTimerListener<L,P,R,CFG,M> 
implements OptimizerListener<L,P,R,CFG,M> {
	private long beginFunctionTime;
	private long originalPEGBuiltTime;
	private long optimalPEGBuiltTime;
	private long reversionGraphBuiltTime;
	private long cfgReverterBuiltTime;
	private long outputCFGBuiltTime;
	private long endFunctionTime;
	
	public void beginFunction(M function) {
		beginFunctionTime = System.currentTimeMillis();
	}
	public void notifyOriginalPEGBuilt(PEGInfo<L,P,R> peginfo) {
		originalPEGBuiltTime = System.currentTimeMillis();
	}
	public void notifyOptimalPEGBuilt(PEGInfo<L,P,R> peginfo) {
		optimalPEGBuiltTime = System.currentTimeMillis();
	}
	public void notifyReversionGraphBuilt(ReversionGraph<P,L> result) {
		reversionGraphBuiltTime = System.currentTimeMillis();
	}
	public void notifyCFGReverterBuilt(CFGReverter<P,L,R> reverter) {
		cfgReverterBuiltTime = System.currentTimeMillis();
	}
	public void notifyOutputCFGBuilt(CFG cfg) {
		outputCFGBuiltTime = System.currentTimeMillis();
	}
	public void endFunction() {
		endFunctionTime = System.currentTimeMillis();
	}
	
	//////////////////////
	
	public long getBeginFunctionTime() {return beginFunctionTime;}
	public long getOriginalPEGBuiltTime() {return originalPEGBuiltTime;}
	public long getOptimalPEGBuiltTime() {return optimalPEGBuiltTime;}
	public long getReversionGraphBuiltTime() {return reversionGraphBuiltTime;}
	public long getCFGReverterBuiltTime() {return cfgReverterBuiltTime;}
	public long getOutputCFGBuiltTime() {return outputCFGBuiltTime;}
	public long getEndFunctionTime() {return endFunctionTime;}
}
