package peggy.optimize;

import java.util.HashMap;
import java.util.Map;

import peggy.represent.PEGInfo;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.revert.CFGReverter;
import eqsat.revert.ReversionGraph;
import eqsat.revert.RevertCFG;

/**
 * This is an Optimizer that supports only a single phase of equality
 * saturation. It starts with a PEG, puts it into an EPEG, performs
 * saturation, chooses an optimal PEG from the EPEG, and returns this final PEG.
 */
public abstract class SingleStageOptimizer<CFG,M,L,P,R> 
extends Optimizer<CFG,M,L,P,R> {
	public static enum Level {
		PARSE_AND_REWRITE,
		PEG_AND_BACK,
		RUN_ENGINE_FULL;
	}
	
	protected Level optimizationLevel = Level.RUN_ENGINE_FULL;
	protected final PEG2PEGOptimizer<L,P,R,M> peg2peg;

	protected SingleStageOptimizer(
			PEG2PEGOptimizer<L,P,R,M> _peg2peg) {
		this.peg2peg = _peg2peg;
	}

	public PEG2PEGOptimizer<L,P,R,M> getPEG2PEGOptimizer() {
		return this.peg2peg;
	}

	public void setOptimizationLevel(Level level) {
		if (level == null)
			throw new NullPointerException();
		this.optimizationLevel = level;
	}
	public Level getOptimizationLevel() {return this.optimizationLevel;}
	
	protected abstract PEGInfo<L,P,R> sanitizePEG(PEGInfo<L,P,R> peg);
	
	public boolean optimize(M function) throws Throwable {
		if (!canOptimize(function))
			return false;
		
		for (OptimizerListener<L,P,R,CFG,M> list : this.listeners) {
			list.beginFunction(function);
		}
		
		OpAmbassador<L> ambassador = this.getOpAmbassador();
		
		if (optimizationLevel.equals(Level.PEG_AND_BACK) ||
			optimizationLevel.equals(Level.RUN_ENGINE_FULL)) {
			PEGInfo<L,P,R> peginfo = 
				this.getPEGProvider().getPEG(function);
			RevertCFG<L,P,R> revert = null;
			
			for (OptimizerListener<L,P,R,CFG,M> list : this.listeners) {
				list.notifyOriginalPEGBuilt(peginfo);
			}

			if (optimizationLevel.equals(Level.RUN_ENGINE_FULL)) {
				if (this.getLogger() != null)
					this.peg2peg.setLogger(this.getLogger());
				
				PEGInfo<L,P,R> revertPEG =
					this.peg2peg.optimize(function, peginfo);
				
				for (OptimizerListener<L,P,R,CFG,M> list : this.listeners) {
					list.notifyOptimalPEGBuilt(revertPEG);
				}

				// sanitize the PEG
				revertPEG = this.sanitizePEG(revertPEG);
				
				Map<R,Vertex<FlowValue<P,L>>> revertMap = 
					new HashMap<R,Vertex<FlowValue<P,L>>>();
				for (R arr : revertPEG.getReturns())
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
				
				CFGReverter<P,L,R> reverter =
					new CFGReverter<P,L,R>(
							result, 
							tempmap, 
							ambassador);
				revert = reverter.getCFG();
				
				for (OptimizerListener<L,P,R,CFG,M> list : this.listeners) {
					list.notifyCFGReverterBuilt(reverter);
				}
			} else {
				for (OptimizerListener<L,P,R,CFG,M> list : this.listeners) {
					list.notifyOptimalPEGBuilt(peginfo);
				}

				// sanitize the PEG
				PEGInfo<L,P,R> revertPEG = this.sanitizePEG(peginfo);
				
				Map<R,Vertex<FlowValue<P,L>>> outputs = 
					new HashMap<R,Vertex<FlowValue<P,L>>>();
				for (R arr : revertPEG.getReturns())
					outputs.put(arr, revertPEG.getReturnVertex(arr));
				
				Map<R,ReversionGraph<P,L>.Vertex> returns = 
					new HashMap<R,ReversionGraph<P,L>.Vertex>();
				ReversionGraph<P,L> result = 
					new ReversionGraph<P,L>(ambassador, revertPEG.getGraph(), outputs, returns);
				
				// last use of graph
				peginfo = null;
				revertPEG = null;
				
				for (OptimizerListener<L,P,R,CFG,M> list : this.listeners) {
					list.notifyReversionGraphBuilt(result);
				}
				
				CFGReverter<P,L,R> reverter =
					new CFGReverter<P,L,R>(
						result, 
						returns, 
						ambassador); 
				revert = reverter.getCFG();
				
				for (OptimizerListener<L,P,R,CFG,M> list : this.listeners) {
					list.notifyCFGReverterBuilt(reverter);
				}
			}
			
			CFG outputCFG = this.getOutputCFG(function, revert);

			for (OptimizerListener<L,P,R,CFG,M> list : this.listeners) {
				list.notifyOutputCFGBuilt(outputCFG);
			}
			
			this.encodeCFG(outputCFG, function);
			
			outputCFG = null;
			revert = null;
		}

		for (OptimizerListener<L,P,R,CFG,M> list : this.listeners) {
			list.endFunction();
		}
		
		return true;
	}
}

