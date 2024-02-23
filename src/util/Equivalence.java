package util;

public interface Equivalence<L, R> {
	public int hashLeft(L left);
	public int hashRight(R right);
	public boolean areEqualLeft(L left, L right);
	public boolean areEqualRight(R left, R right);
	public boolean areEqual(L left, R right);
}
