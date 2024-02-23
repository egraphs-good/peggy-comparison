package util.integer;

public interface IncreasingIntMap<V> extends SortedIntMap<V> {
	IncreasingIntSet keySet();
}
