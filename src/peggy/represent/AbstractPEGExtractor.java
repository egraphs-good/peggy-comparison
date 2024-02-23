package peggy.represent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import peggy.Logger;
import peggy.pb.CostModel;
import peggy.revert.GraphReconstructor;
import peggy.revert.ReversionHeuristic;
import util.Tag;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This is a PEG extractor that has several default implementations of methods.
 */
public abstract class AbstractPEGExtractor<L,P,R> 
implements PEGExtractor<L,P,R> {
	protected final ReversionHeuristic<L,P,R,Integer> heuristic;
	protected boolean lastOriginal = false;
	protected final Tag<CPEGTerm<L,P>> termTag;
	protected long maxCost = -1L;
	protected Logger logger;

	protected AbstractPEGExtractor(
			Tag<CPEGTerm<L,P>> _termTag,
			ReversionHeuristic<L,P,R,Integer> _heuristic) {
		this.termTag = _termTag;
		this.heuristic = _heuristic;
	}
	
	public Logger getLogger() {return this.logger;}
	public void setLogger(Logger _logger) {this.logger = _logger;}
	
	public boolean lastChoseOriginal() {return this.lastOriginal;}
	public long getMaxCost() {return this.maxCost;}
	public void setMaxCost(long l) {this.maxCost = l;}
	
	protected abstract Map<? extends CPEGValue<L,P>, ? extends CPEGTerm<L,P>> 
	getNodeMap(
			CPeggyAxiomEngine<L,P> engine, 
			PEGInfo<L,P,R> peginfo,
			Map<Vertex<FlowValue<P,L>>, CPEGTerm<L,P>> rootVertexMap);
	
	public PEGInfo<L,P,R> extractPEG(
			CPeggyAxiomEngine<L,P> engine, 
			PEGInfo<L,P,R> peginfo,
			Map<Vertex<FlowValue<P,L>>, CPEGTerm<L,P>> rootVertexMap) {
		Map<? extends CPEGValue<L,P>, ? extends CPEGTerm<L,P>> nodeMap =
			getNodeMap(engine, peginfo, rootVertexMap);
		
		if (nodeMap == null) {
			this.lastOriginal = true;
			return peginfo;
		} else {
			if (maxCost > 0) {
				// make sure the cost of the chosen PEG is less than the original
				CostModel<CPEGTerm<L,P>,Integer> costModel = 
					heuristic.getCostModel();
				int newcost = 0;
				for (CPEGTerm<L,P> term : nodeMap.values()) {
					newcost += costModel.cost(term);
				}
				if (newcost > maxCost) {
					// revert!
					this.lastOriginal = true;
					return peginfo;
				}
			}
			
			Map<R,CPEGValue<L,P>> outputs = new HashMap<R,CPEGValue<L,P>>();
			for (R arr : peginfo.getReturns())
				outputs.put(arr,
						rootVertexMap.get(peginfo.getReturnVertex(arr)).getValue());
			
			GraphReconstructor<L,P,R> recons = 
				new GraphReconstructor<L,P,R>(
						this.termTag, nodeMap, outputs);
			if (this.getLogger()!=null)
				recons.setLogger(this.getLogger());
			final Map<R,Vertex<FlowValue<P,L>>> revertMap = recons.getReturnMap();
			
			this.lastOriginal = false;
			
			return new PEGInfo<L,P,R>(
					recons.getReversionGraph(), 
					revertMap) {
				public Collection<? extends R> getReturns() {return revertMap.keySet();}
			};
		}
	}
}
