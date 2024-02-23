package peggy.pb;

import java.util.Collection;
import java.util.NoSuchElementException;

import util.AbstractPattern;

public class RemoveValueMap<V,N> implements ValueMap<V,N> {
	private final ValueMap<V,N> inner;
	private final Collection<? extends N> removed;
	
	public RemoveValueMap(
			ValueMap<V,N> _inner, 
			Collection<? extends N> _removed) {
		this.inner = _inner;
		this.removed = _removed;
	}
	
	public boolean containsNode(N node) {
		if (this.removed.contains(node))
			return false;
		else
			return this.inner.containsNode(node);
	}

	public V getValue(N node) {
		if (!this.containsNode(node))
			throw new NoSuchElementException("Node not in graph");
		else
			return this.inner.getValue(node);
	}
	public int getArity(N node) {
		if (!this.containsNode(node))
			throw new NoSuchElementException("Node not in graph");
		else
			return this.inner.getArity(node);
	}
	public V getChildValue(N node, int index) {
		if (!this.containsNode(node))
			throw new NoSuchElementException("Node not in graph");
		else
			return this.inner.getChildValue(node, index);
	}
	
	public Iterable<? extends N> getParentNodes(final V value) {
		return util.Collections.filterIterable(
				inner.getParentNodes(value),
				new AbstractPattern<N>() {
					public boolean matches(N node) {
						return !removed.contains(node);
					}
				});
	}

	public Iterable<? extends N> getNodes(V value) {
		return util.Collections.filterIterable(
				inner.getNodes(value),
				new AbstractPattern<N>() {
					public boolean matches(N node) {
						return !removed.contains(node);
					}
				});
	}
}
