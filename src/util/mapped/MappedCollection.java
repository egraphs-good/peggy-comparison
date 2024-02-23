package util.mapped;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

public abstract class MappedCollection<D, R> extends AbstractCollection<R> {
	protected abstract Collection<? extends D> getWrapped();
	protected abstract R map(D domain);
	
	public Iterator<R> iterator() {
		return new MappedIterator<D,R>() {
			final Iterator<? extends D> mIterator
					= MappedCollection.this.getWrapped().iterator();
			
			protected Iterator<? extends D> getWrapped() {return mIterator;}
			
			protected R map(D domain) {
				return MappedCollection.this.map(domain);
			}
		};
	}
	
	public void clear() {getWrapped().clear();}
	
	public boolean isEmpty() {return getWrapped().isEmpty();}
	
	public int size() {return getWrapped().size();}
}
