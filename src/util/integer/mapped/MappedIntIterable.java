package util.integer.mapped;

import java.util.Iterator;

import util.integer.IntIterable;
import util.integer.IntIterator;

public abstract class MappedIntIterable<R> implements Iterable<R> {
	protected abstract IntIterable getWrapped();
	protected abstract R map(int domain);
	
	public Iterator<R> iterator() {
		final IntIterator wrapped = getWrapped().iterator();
		return new MappedIntIterator<R>() {
			protected IntIterator getWrapped() {return wrapped;}
			protected R map(int domain) {
				return MappedIntIterable.this.map(domain);
			}
		};
	}
}
