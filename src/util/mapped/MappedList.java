package util.mapped;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public abstract class MappedList<D,R>
		extends MappedCollection<D,R> implements List<R> {
	protected abstract List<? extends D> getWrapped();

	public void add(int index, R element) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(int index, Collection<? extends R> c) {
		throw new UnsupportedOperationException();
	}

	public R get(int index) {return map(getWrapped().get(index));}

	public int indexOf(Object o) {
		int i = 0;
		for (R mapped : this) {
			if (mapped == null ? o == null : mapped.equals(o))
				return i;
			i++;
		}
		return -1;
	}

	public int lastIndexOf(Object o) {
		int i = size();
		for (R mapped : this) {
			i--;
			if (mapped == null ? o == null : mapped.equals(o))
				return i;
		}
		return -1;
	}

	public ListIterator<R> listIterator() {return listIterator(0);}

	public ListIterator<R> listIterator(final int index) {
		return new MappedListIterator<D,R>() {
			final ListIterator<? extends D> mIterator
					= MappedList.this.getWrapped().listIterator(index);
			
			protected ListIterator<? extends D> getWrapped() {return mIterator;}
			
			protected R map(D domain) {return MappedList.this.map(domain);}
		};
	}

	public R remove(int index) {
		return map(getWrapped().remove(index));
	}

	public R set(int index, R element) {
		throw new UnsupportedOperationException();
	}

	public List<R> subList(final int fromIndex, final int toIndex) {
		return new MappedList<D,R>() {
			final List<? extends D> mWrapped
					= MappedList.this.getWrapped().subList(fromIndex, toIndex);
			
			protected List<? extends D> getWrapped() {return mWrapped;}
			
			protected R map(D domain) {return MappedList.this.map(domain);}
		};
	}
}