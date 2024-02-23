package peggy.pb;

import peggy.represent.StickyPredicate;
import eqsat.FlowValue;

public class NondomainStickyPredicate<O,P> 
implements StickyPredicate<FlowValue<P,O>> {
	public boolean isSticky(FlowValue<P,O> label, int childIndex) {
		return label.isEval() && childIndex==1;
	}
	public boolean allowsChild(
			FlowValue<P,O> parentLabel, 
			int childIndex, 
			FlowValue<P,O> childLabel) {
		if (parentLabel.isEval() && childIndex == 1) {
			return childLabel.isPass() && 
				childLabel.getLoopDepth() == parentLabel.getLoopDepth();
		}
		return true;
	}
}
