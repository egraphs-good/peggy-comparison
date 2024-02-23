package llvm.bitcode;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class is a hashlist that wraps around an existing hashlist.
 * New entries into this hashlist will not affect the inner hashlist.
 */
public class WrapperHashList<V> extends HashList<V> {
	private final HashList<V> inner;
	
	public WrapperHashList(HashList<V> _inner) {
		this.inner = _inner;
	}
	
	public int size() {return this.index2value.size() + this.inner.size();}
	
	public boolean hasValue(V value) {
		return super.hasValue(value) || this.inner.hasValue(value);
	}
	
	public int add(V value) {
		if (this.hasValue(value))
			return this.getIndex(value);

		int result = this.size();
		int index = this.index2value.size();
		this.index2value.add(value);
		this.value2index.put(value, index);
		return result;
	}
	public void addAll(Iterable<? extends V> coll) {
		for (V v : coll) {
			this.add(v);
		}
	}
	
	public int getIndex(V value) {
		if (!super.hasValue(value))
			return this.inner.getIndex(value);
		return this.value2index.get(value) + this.inner.size();
	}
	
	public V getValue(int index) {
		if (index >= this.inner.size())
			return this.index2value.get(index - this.inner.size());
		else
			return this.inner.getValue(index);
	}
	
	/**
	 * This iterator does not support removal.
	 */
	public Iterator<V> iterator() {
		final Iterator<V> first = this.inner.iterator();
		final Iterator<V> second = Collections.unmodifiableList(this.index2value).iterator();
		return new Iterator<V>() {
			public boolean hasNext() {
				return first.hasNext() || second.hasNext();
			}
			public V next() {
				if (first.hasNext())
					return first.next();
				else if (second.hasNext())
					return second.next();
				else
					throw new NoSuchElementException();
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
