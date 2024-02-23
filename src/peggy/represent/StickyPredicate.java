package peggy.represent;

/**
 * This sticky predicate tells which types of nodes must be directly attached
 * to their specific children.
 */
public interface StickyPredicate<L> {
	public boolean isSticky(L label, int childIndex);
	public boolean allowsChild(L parentLabel, int childIndex, L childLabel);
}
