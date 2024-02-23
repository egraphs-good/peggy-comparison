package peggy.pb;

import peggy.represent.StickyPredicate;
import eqsat.meminfer.engine.peg.CPEGTerm;

/**
 * This is the default greedy reversion heuristic.
 */
public abstract class DefaultGreedyReversionHeuristic<O,P,R>
extends GreedyReversionHeuristic<O,P,R> {
	protected final StickyPredicate<O> stickyPredicate;
	
	public DefaultGreedyReversionHeuristic(
			StickyPredicate<O> _stickyPredicate) {
		this.stickyPredicate = _stickyPredicate;
	}
	
	protected boolean isUsable(CPEGTerm<O,P> term) {
		if (!term.getOp().isTheta()) {
			// check to see if it points to its own value
			for (int i = 0; i < term.getArity(); i++) {
				if (term.getChild(i).getValue().equals(term.getValue()))
					return false;
			}
		}
		if (!term.getOp().isRevertable())
			return false;
		
		if (term.getOp().isEval() ||
			term.getOp().isPass()) {
			int depth = term.getOp().getLoopDepth();
			int maxVar0 = term.getChild(0).getValue().getMaxVariance();
			if (maxVar0 > depth)
				return false;
		} else if (term.getOp().isTheta()) {
			int depth = term.getOp().getLoopDepth();
			if (term.getValue().getMaxVariance() > depth)
				return false;
		} else if (term.getOp().isDomain()) {
			O label = term.getOp().getDomain();
			if (!this.isRevertible(label))
				return false;
		}
		return true;
	}
	
	protected boolean allowsChild(
			CPEGTerm<O,P> parent,
			int childIndex,
			CPEGTerm<O,P> child) {
		if (parent.getOp().isEval() && childIndex == 1) {
			int depth = parent.getOp().getLoopDepth();
			return child.getOp().isPass() && 
				child.getOp().getLoopDepth() == depth;
		} else if (parent.getOp().isDomain()) {
			O parentLabel = parent.getOp().getDomain();
			if (stickyPredicate.isSticky(parentLabel, childIndex)) {
				if (child.getOp().isDomain()) {
					return stickyPredicate.allowsChild(
							parentLabel, 
							childIndex, 
							child.getOp().getDomain());
				} else {
					return false;
				}
			}
		}
		return true;
	}
	
	protected abstract boolean isRevertible(O label);
}
