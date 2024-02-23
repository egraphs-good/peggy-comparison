package util.integer.mapped;

import java.util.AbstractCollection;
import java.util.Iterator;

import util.integer.IntCollection;
import util.integer.IntIterator;

public abstract class MappedIntCollection<R> extends AbstractCollection<R> {
	protected abstract IntCollection getWrapped();
	protected abstract R map(int domain);
	
	public Iterator<R> iterator() {
		return new MappedIntIterator<R>() {
			final IntIterator mIterator
					= MappedIntCollection.this.getWrapped().iterator();
			protected IntIterator getWrapped() {return mIterator;}
			protected R map(int domain) {
				return MappedIntCollection.this.map(domain);
			}
		};
	}
	
	public void clear() {getWrapped().clear();}
	
	public boolean isEmpty() {return getWrapped().isEmpty();}
	
	public int size() {return getWrapped().size();}
}
