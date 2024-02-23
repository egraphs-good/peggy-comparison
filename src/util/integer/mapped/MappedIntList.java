package util.integer.mapped;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import util.integer.IntList;
import util.integer.IntListIterator;

public abstract class MappedIntList<R> extends MappedIntCollection<R>
		implements List<R> {
	protected abstract IntList getWrapped();

	public void add(int index, R element) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(int index, Collection<? extends R> c) {
		throw new UnsupportedOperationException();
	}

	public R get(int index) {return map(getWrapped().getInt(index));}

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

	public ListIterator<R> listIterator() {
		final IntListIterator wrapped = getWrapped().listIterator();
		return new MappedIntListIterator<R>() {
			protected IntListIterator getWrapped() {return wrapped;}
			protected R map(int domain) {
				return MappedIntList.this.map(domain);
			}
		};
	}

	public ListIterator<R> listIterator(int index) {
		final IntListIterator wrapped = getWrapped().listIterator(index);
		return new MappedIntListIterator<R>() {
			protected IntListIterator getWrapped() {return wrapped;}
			protected R map(int domain) {
				return MappedIntList.this.map(domain);
			}
		};
	}

	public R remove(int index) {return map(getWrapped().removeAt(index));}

	public R set(int index, R element) {
		throw new UnsupportedOperationException();
	}

	public List<R> subList(int fromIndex, int toIndex) {
		final IntList wrapped = getWrapped().subList(fromIndex, toIndex);
		return wrapped == getWrapped() ? this : new MappedIntList<R>() {
			protected IntList getWrapped() {return wrapped;}
			protected R map(int domain) {
				return MappedIntList.this.map(domain);
			}
		};
	}
}
