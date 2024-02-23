package peggy.represent;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This is a sticky predicate that acts as the disjunction of several
 * other sticky predicates.
 */
public class CombinedStickyPredicate<L> implements StickyPredicate<L> {
	private final Collection<? extends StickyPredicate<L>> preds;
	public CombinedStickyPredicate(
			Collection<? extends StickyPredicate<L>> _preds) {
		this.preds = new ArrayList<StickyPredicate<L>>(_preds);
	}
	/**
	 * Disjunction
	 */
	public boolean isSticky(L label, int childIndex) {
		for (StickyPredicate<L> pred : this.preds) {
			if (pred.isSticky(label, childIndex)) 
				return true;
		}
		return false;
	}
	/**
	 * Conjunction. 
	 * For each pred that thinks the parent is sticky, we and the allowsChild.
	 */
	public boolean allowsChild(L parent, int index, L child) {
		for (StickyPredicate<L> pred : this.preds) {
			if (pred.isSticky(parent, index) && 
				!pred.allowsChild(parent, index, child)) {
				return false;
			}
		}
		return true;
	}
}
