package util;

public interface Labeled<L> {
	L getLabel();
	boolean hasLabel(L label);
}
