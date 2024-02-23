package util.integer;

public abstract class AbstractIncreasingIntMap<V>
		extends AbstractSortedIntMap<V> implements IncreasingIntMap<V> {
	public abstract IncreasingIntSet keySet();
}
