package peggy.optimize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import peggy.represent.PEGInfo;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.revert.CFGReverter;
import eqsat.revert.ReversionGraph;
import eqsat.revert.RevertCFG;

/**
 * This is an Optimizer that has multiple PEG->PEG phases.
 * Each such phase is implemented by a separate PEG2PEGOptimizer.
 */
public abstract class MultiStageOptimizer<CFG,M,L,P,R> 
extends Optimizer<CFG,M,L,P,R> {
	protected final List<? extends PEG2PEGOptimizer<L,P,R,M>> optimizers;

	protected MultiStageOptimizer(
			List<? extends PEG2PEGOptimizer<L,P,R,M>> _optimizers) {
		this.optimizers = _optimizers;
	}
	
	public Iterable<? extends PEG2PEGOptimizer<L,P,R,M>> 
	getPEG2PEGOptimizers() {
		return this.optimizers;
	}
	
	public void addListener(OptimizerListener<L,P,R,CFG,M> list) {
		super.addListener(list);
	}
	public void removeListener(OptimizerListener<L,P,R,CFG,M> list) {
		super.removeListener(list);
	}
	
	public boolean optimize(M function) throws Throwable {
		if (!canOptimize(function))
			return false;

		for (OptimizerListener<L,P,R,CFG,M> list : this.listeners) {
			list.beginFunction(function);
		}

		OpAmbassador<L> ambassador = this.getOpAmbassador();

		PEGInfo<L,P,R> peginfo = 
			this.getPEGProvider().getPEG(function);
		RevertCFG<L,P,R> revert;

		for (OptimizerListener<L,P,R,CFG,M> list : this.listeners) {
			list.notifyOriginalPEGBuilt(peginfo);
		}

		// apply the various stages
		PEGInfo<L,P,R> revertPEG = peginfo;
		for (PEG2PEGOptimizer<L,P,R,M> p2p : this.optimizers) {
			revertPEG = p2p.optimize(function, revertPEG);
		}
		
		for (OptimizerListener<L,P,R,CFG,M> list : this.listeners) {
			list.notifyOptimalPEGBuilt(revertPEG);
		}

		Map<R,Vertex<FlowValue<P,L>>> revertMap = 
			new HashMap<R,Vertex<FlowValue<P,L>>>();
		for (R arr : peginfo.getReturns())
			revertMap.put(arr, revertPEG.getReturnVertex(arr));

		// last use of peginfo
		peginfo = null;
		
		Map<R,ReversionGraph<P,L>.Vertex> tempmap = 
			new HashMap<R,ReversionGraph<P,L>.Vertex> ();
		ReversionGraph<P,L> result = 
			new ReversionGraph<P,L>(
					ambassador,
					revertPEG.getGraph(), 
					revertMap,
					tempmap);

		for (OptimizerListener<L,P,R,CFG,M> list : this.listeners) {
			list.notifyReversionGraphBuilt(result);
		}

		CFGReverter<P,L,R> reverter = new CFGReverter<P,L,R>(
				result, 
				tempmap, 
				ambassador);
		revert = reverter.getCFG();

		for (OptimizerListener<L,P,R,CFG,M> list : this.listeners) {
			list.notifyCFGReverterBuilt(reverter);
		}

		CFG outputCFG = this.getOutputCFG(function, revert);

		for (OptimizerListener<L,P,R,CFG,M> list : this.listeners) {
			list.notifyOutputCFGBuilt(outputCFG);
		}
		
		this.encodeCFG(outputCFG, function);

		outputCFG = null;
		revert = null;

		for (OptimizerListener<L,P,R,CFG,M> list : this.listeners) {
			list.endFunction();
		}
		
		return true;
	}
}
