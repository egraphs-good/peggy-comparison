package util.integer;

public abstract class AbstractSortedIntMap<V> extends AbstractIntMap<V>
		implements SortedIntMap<V> {
	public abstract SortedIntSet keySet();
}
