package peggy.analysis;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import peggy.represent.PEG;
import peggy.represent.PEGInfo;
import util.Tag;
import util.UnhandledCaseException;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;

/**
 * This class is used to annotate a PEG with its loop-invariance values.
 * These are stored as bitmasks and annotated on the vertices using tags.
 * Implementations exist for both PEG objects (PEG.Vertex) 
 * and PEGInfo objects (CREG.Vertex).
 */
public class PEGInvarianceTagger<L,P,R> {
	public void tagInvariance(Tag<Integer> tag, PEGInfo<L,P,R> info) {
		final Set<Vertex<FlowValue<P,L>>> seen = new HashSet<Vertex<FlowValue<P,L>>>();
		final LinkedList<Vertex<FlowValue<P,L>>> worklist = 
			new LinkedList<Vertex<FlowValue<P,L>>>();
		for (R arr : info.getReturns()) {
			worklist.add(info.getReturnVertex(arr));
		}
		
		// find all vertices
		while (worklist.size() > 0) {
			Vertex<FlowValue<P,L>> next = worklist.removeFirst();
			if (seen.contains(next))
				continue;
			seen.add(next);
			next.setTag(tag, ~0);
			for (int i = 0; i < next.getChildCount(); i++) {
				worklist.addLast(next.getChild(i));
			}
		}
		
		for (boolean progress = true; progress; ) {
			progress = false;
			for (Vertex<FlowValue<P,L>> vertex : seen) {
				final int oldI = vertex.getTag(tag);
				FlowValue<P,L> op = vertex.getLabel();
				int invariance = ~0;
				for (int i = 0; i < vertex.getChildCount(); i++)
					invariance &= vertex.getChild(i).getTag(tag);
				if (op.isLoopLiftedAll() || op.isShift()) 
				{/* skip */}
				else if (op.isTheta())
					invariance = invariance & ~(1 << op.getLoopDepth());
				else if (op.isPass())
					invariance = invariance | (1 << op.getLoopDepth());
				else if (op.isEval())
					invariance = invariance | ((1 << op.getLoopDepth()) & vertex.getChild(1).getTag(tag));
				else
					throw new UnhandledCaseException();

				if (oldI != invariance) {
					progress = true;
					vertex.setTag(tag, invariance);
				}
			}
		}
	}
	
	
	public <PP extends PEG<FlowValue<P,L>,R,PP,VV>, VV extends PEG.Vertex<FlowValue<P,L>,R,PP,VV>> 
	void tagInvariance(Tag<Integer> tag, PP peg) {
		final Set<VV> seen = new HashSet<VV>();
		final LinkedList<VV> worklist = new LinkedList<VV>();
		for (R arr : peg.getReturns()) {
			worklist.add(peg.getReturnVertex(arr));
		}
		
		// find all vertices
		while (worklist.size() > 0) {
			VV next = worklist.removeFirst();
			if (seen.contains(next))
				continue;
			seen.add(next);
			next.setTag(tag, ~0);
			for (int i = 0; i < next.getChildCount(); i++) {
				worklist.addLast(next.getChild(i));
			}
		}
		
		for (boolean progress = true; progress; ) {
			progress = false;
			for (VV vertex : seen) {
				final int oldI = vertex.getTag(tag);
				FlowValue<P,L> op = vertex.getLabel();
				int invariance = ~0;
				for (int i = 0; i < vertex.getChildCount(); i++)
					invariance &= vertex.getChild(i).getTag(tag);
				if (op.isLoopLiftedAll() || op.isShift()) 
				{/* skip */}
				else if (op.isTheta())
					invariance = invariance & ~(1 << op.getLoopDepth());
				else if (op.isPass())
					invariance = invariance | (1 << op.getLoopDepth());
				else if (op.isEval())
					invariance = invariance | ((1 << op.getLoopDepth()) & vertex.getChild(1).getTag(tag));
				else
					throw new UnhandledCaseException();

				if (oldI != invariance) {
					progress = true;
					vertex.setTag(tag, invariance);
				}
			}
		}
	}

	/**
	 * Does a local update of the invariance for this one vertex,
	 * assuming the children are correct.
	 */
	public <PP extends PEG<FlowValue<P,L>,R,PP,VV>, VV extends PEG.Vertex<FlowValue<P,L>,R,PP,VV>> 
	void updateInvariance(Tag<Integer> tag, VV vertex) {
		FlowValue<P,L> op = vertex.getLabel();
		int invariance = ~0;
		for (int i = 0; i < vertex.getChildCount(); i++)
			invariance &= vertex.getChild(i).getTag(tag);
		if (op.isLoopLiftedAll() || op.isShift()) 
		{/* skip */}
		else if (op.isTheta())
			invariance = invariance & ~(1 << op.getLoopDepth());
		else if (op.isPass())
			invariance = invariance | (1 << op.getLoopDepth());
		else if (op.isEval())
			invariance = invariance | ((1 << op.getLoopDepth()) & vertex.getChild(1).getTag(tag));
		else
			throw new UnhandledCaseException();
		vertex.setTag(tag, invariance);
	}
}
