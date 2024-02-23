package util.integer;

public abstract class AbstractIncreasingIntSet
		extends AbstractSortedIntSet implements IncreasingIntSet {
	public IntComparator comparator() {
		return IntCollections.increasingComparator();
	}
}
