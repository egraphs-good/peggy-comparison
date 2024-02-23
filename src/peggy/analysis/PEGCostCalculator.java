package peggy.analysis;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import peggy.pb.CostModel;
import peggy.represent.PEGInfo;
import util.NamedTag;
import util.Tag;
import util.UnhandledCaseException;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;

/**
 * This class computes the cost of a PEG given a particular cost model.
 */
public abstract class PEGCostCalculator<L,P,R> {
	private static final Tag<Integer> INVARIANCE_TAG = 
		new NamedTag<Integer>("INVARIANCE");
	
	public abstract CostModel<Vertex<FlowValue<P,L>>,Integer> getCostModel();
	public abstract int getVarianceMultiplier();
	
	private final void assignVariance(PEGInfo<L,P,R> peg) {
		// assign full invariance
		for (Vertex<FlowValue<P,L>> vertex : peg.getGraph().getVertices()) {
			vertex.setTag(INVARIANCE_TAG, ~0);
		}
		
		// iterate to fixpoint
		for (boolean progress = true; progress; ) {
			progress = false;
			
			for (Vertex<FlowValue<P,L>> vertex : peg.getGraph().getVertices()) {
				int invariance = ~0;
				FlowValue<P,L> op = vertex.getLabel();
				for (Vertex<FlowValue<P,L>> child : vertex.getChildren())
					invariance &= child.getTag(INVARIANCE_TAG);
					
				if (op.isLoopLiftedAll() || op.isShift()) {}
				else if (op.isTheta())
					invariance = invariance & ~(1 << op.getLoopDepth());
				else if (op.isPass())
					invariance = invariance | (1 << op.getLoopDepth());
				else if (op.isEval())
					invariance = invariance | ((1 << op.getLoopDepth()) & vertex.getChild(1).getTag(INVARIANCE_TAG));
				else
					throw new UnhandledCaseException();

				if (invariance != vertex.getTag(INVARIANCE_TAG)) {
					vertex.setTag(INVARIANCE_TAG, invariance);
					progress = true;
				}
			}
		}
	}
	
	private void removeTags(PEGInfo<L,P,R> peg) {
		for (Vertex<FlowValue<P,L>> vertex : peg.getGraph().getVertices()) {
			vertex.removeTag(INVARIANCE_TAG);
		}
	}
	
	// assume it has the tag
	private int getMaxVariance(Vertex<FlowValue<P,L>> vertex) {
		int invariance = vertex.getTag(INVARIANCE_TAG);
		for (int depth = 32; --depth != 0; )
			if ((invariance & (1 << depth)) == 0)
				return depth;
		return 0;
	}

	/**
	 * Computes the cost of a node, after it has been tagged with its
	 * variance information.
	 */
	protected int computeCost(Vertex<FlowValue<P,L>> vertex) {
		int vm = this.getVarianceMultiplier();
		int baseCost = this.getCostModel().cost(vertex);
		int mult = (int)Math.pow(vm, this.getMaxVariance(vertex));
		return baseCost*mult;
	}

	/**
	 * Computes the cost of the given PEG.
	 * This performs an initial phase to assign variance
	 * values to every node.
	 */
	public final int cost(PEGInfo<L,P,R> peg) {
		this.assignVariance(peg);
		
		Set<Vertex<FlowValue<P,L>>> seen = 
			new HashSet<Vertex<FlowValue<P,L>>>();
		LinkedList<Vertex<FlowValue<P,L>>> worklist = 
			new LinkedList<Vertex<FlowValue<P,L>>> ();
		for (R arr : peg.getReturns()) {
			worklist.addLast(peg.getReturnVertex(arr));
		}

		int cost = 0;
		while (!worklist.isEmpty()) {
			Vertex<FlowValue<P,L>> next = worklist.removeFirst();
			if (seen.contains(next)) continue;
			seen.add(next);
			
			cost += computeCost(next);
			worklist.addAll(next.getChildren());
		}
		
		this.removeTags(peg);
		return cost;
	}

}
