package util.mapped;

import java.util.Iterator;

import util.AbstractIterable;

public abstract class MappedIterable<D, R> extends AbstractIterable<R> {
	protected abstract Iterable<? extends D> getWrapped();
	protected abstract R map(D domain);
	
	public Iterator<R> iterator() {
		return new MappedIterator<D,R>() {
			final Iterator<? extends D> mIterator
					= MappedIterable.this.getWrapped().iterator();
			
			protected Iterator<? extends D> getWrapped() {return mIterator;}
			
			protected R map(D domain) {return MappedIterable.this.map(domain);}
		};
	}
}