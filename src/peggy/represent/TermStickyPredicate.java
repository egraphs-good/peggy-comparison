package peggy.represent;

import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;

/**
 * This is a StickyPredicate that acts over CPEGTerms.
 */
public class TermStickyPredicate<L,P> 
implements StickyPredicate<CPEGTerm<L,P>> {
	protected final StickyPredicate<FlowValue<P,L>> flowPredicate;
	
	public TermStickyPredicate(StickyPredicate<FlowValue<P,L>> _flow) {
		this.flowPredicate = _flow;
	}
	public boolean isSticky(CPEGTerm<L,P> term, int childIndex) {
		return this.flowPredicate.isSticky(term.getOp(), childIndex);
	}
	public boolean allowsChild(
			CPEGTerm<L,P> parentTerm,
			int childIndex,
			CPEGTerm<L,P> childTerm) {
		return this.flowPredicate.allowsChild(
				parentTerm.getOp(),
				childIndex,
				childTerm.getOp());
	}
}
