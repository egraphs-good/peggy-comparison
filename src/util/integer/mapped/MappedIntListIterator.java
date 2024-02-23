package util.integer.mapped;

import java.util.ListIterator;

import util.integer.IntListIterator;

public abstract class MappedIntListIterator<R>
		extends MappedIntIterator<R> implements ListIterator<R> {
	protected abstract IntListIterator getWrapped();

	public void add(R arg0) {throw new UnsupportedOperationException();}
	public boolean hasPrevious() {return getWrapped().hasPrevious();}
	public int nextIndex() {return getWrapped().nextIndex();}
	public R previous() {return map(getWrapped().previousInt());}
	public int previousIndex() {return getWrapped().previousIndex();}
	public void set(R arg0) {throw new UnsupportedOperationException();}
}
