package util.mapped;

import java.util.Iterator;

public abstract class MappedIterator<D, R> implements Iterator<R> {
	protected abstract Iterator<? extends D> getWrapped();
	protected abstract R map(D domain);
	
	public boolean hasNext() {return getWrapped().hasNext();}
	public R next() {return map(getWrapped().next());}
	public void remove() {getWrapped().remove();}
}
