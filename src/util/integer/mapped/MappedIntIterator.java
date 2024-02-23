package util.integer.mapped;

import java.util.Iterator;

import util.integer.IntIterator;

public abstract class MappedIntIterator<R> implements Iterator<R> {
	protected abstract IntIterator getWrapped();
	protected abstract R map(int domain);
	
	public boolean hasNext() {return getWrapped().hasNext();}
	public R next() {return map(getWrapped().nextInt());}
	public void remove() {getWrapped().remove();}
}
