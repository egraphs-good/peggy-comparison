package peggy.represent;

import eqsat.FlowValue;

/**
 * This is a sticky predicate that acts over FlowValues.
 */
public class FlowValueStickyPredicate<L,P> 
implements StickyPredicate<FlowValue<P,L>> {
	protected final StickyPredicate<L> labelPredicate;
	public FlowValueStickyPredicate(StickyPredicate<L> _label) {
		this.labelPredicate = _label;
	}
	public boolean isSticky(FlowValue<P,L> flow, int childIndex) {
		return flow.isDomain() && 
		this.labelPredicate.isSticky(flow.getDomain(), childIndex);
	}
	public boolean allowsChild(
			FlowValue<P,L> parentFlow, 
			int childIndex,
			FlowValue<P,L> childFlow) {
		if (parentFlow.isDomain() && childFlow.isDomain()) {
			return this.labelPredicate.allowsChild(
					parentFlow.getDomain(),
					childIndex,
					childFlow.getDomain());
		} else if (parentFlow.isDomain() && 
				this.labelPredicate.isSticky(parentFlow.getDomain(), childIndex)) {
			return false;
		} else {
			return true;
		}
	}
}
