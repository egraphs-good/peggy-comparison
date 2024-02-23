package util.mapped;

import java.util.ListIterator;

public abstract class MappedListIterator<D,R>
		extends MappedIterator<D,R> implements ListIterator<R> {
	protected abstract ListIterator<? extends D> getWrapped();

	public void add(R arg0) {throw new UnsupportedOperationException();}
	public boolean hasPrevious() {return getWrapped().hasPrevious();}
	public int nextIndex() {return getWrapped().nextIndex();}
	public R previous() {return map(getWrapped().previous());}
	public int previousIndex() {return getWrapped().previousIndex();}
	public void set(R arg0) {throw new UnsupportedOperationException();}
}
