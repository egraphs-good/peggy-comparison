package peggy.optimize;

import peggy.represent.PEGInfo;
import eqsat.revert.CFGReverter;
import eqsat.revert.ReversionGraph;

/**
 * This is an OptimizerListener that keeps copies of references to the 
 * objects that are passed to the callback methods.
 */
public class OptimizerLastDataListener<L,P,R,CFG,M> 
implements OptimizerListener<L,P,R,CFG,M> {
	private M lastFunction;
	private PEGInfo<L,P,R> lastOriginalPEG;
	private PEGInfo<L,P,R> lastOptimalPEG;
	private ReversionGraph<P,L> lastReversionGraph;
	private CFGReverter<P,L,R> lastCFGReverter;
	private CFG lastOutputCFG;
	
	public void beginFunction(M function) {
		lastFunction = function;
	}
	public void notifyOriginalPEGBuilt(PEGInfo<L,P,R> peginfo) {
		this.lastOriginalPEG = peginfo;
	}
	public void notifyOptimalPEGBuilt(PEGInfo<L,P,R> peginfo) {
		this.lastOptimalPEG = peginfo;
	}
	public void notifyReversionGraphBuilt(ReversionGraph<P,L> result) {
		this.lastReversionGraph = result;
	}
	public void notifyCFGReverterBuilt(CFGReverter<P,L,R> reverter) {
		this.lastCFGReverter = reverter;
	}
	public void notifyOutputCFGBuilt(CFG cfg) {
		this.lastOutputCFG = cfg;
	}
	public void endFunction() {}
	
	////////////////////////////
	
	public M getLastFunctionName() {
		return this.lastFunction;
	}
	public PEGInfo<L,P,R> getLastOriginalPEG() {
		return this.lastOriginalPEG;
	}
	public PEGInfo<L,P,R> getLastOptimalPEG() {
		return this.lastOptimalPEG;
	}
	public ReversionGraph<P,L> getLastReversionGraph() {
		return this.lastReversionGraph;
	}
	public CFGReverter<P,L,R> getLastCFGReverter() {
		return this.lastCFGReverter;
	}
	public CFG getLastOutputCFG() {
		return this.lastOutputCFG;
	}
}
