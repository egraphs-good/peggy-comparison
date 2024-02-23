package util.integer;

public abstract class AbstractIntComparator implements IntComparator {
	public int compare(Integer left, Integer right) {
		if (left == null || right == null)
			throw new IllegalArgumentException();
		return compare(left.intValue(), right.intValue());
	}
}
