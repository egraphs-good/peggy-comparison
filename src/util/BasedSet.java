package util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

import util.integer.BitIntSet;
import util.integer.IntIterator;

public class BasedSet<E> extends AbstractSet<E> {
	protected final Basis<E> mBasis;
	protected final BitIntSet mBits = new BitIntSet();
	
	public BasedSet(Basis<E> basis) {mBasis = basis;}
	
	public boolean add(E element) {return mBits.add(mBasis.getIndex(element));}
	
	public void clear() {mBits.clear();}
	
	public boolean contains(Object element) {
		E based = mBasis.getElement(element);
		return based != null && mBits.contains(mBasis.getIndex(based));
	}
	
	public boolean equals(BasedSet<E> that) {
		return that != null && (mBasis.equals(that.mBasis)
				? mBits.equals(that.mBits) : super.equals(that));
	}
	
	public boolean equals(Object that) {
		return that instanceof BasedSet ? equals((BasedSet<E>)that)
				: super.equals(that);
	}
	
	public int hashCode() {return mBits.hashCode();}
	
	public boolean isEmpty() {return mBits.isEmpty();}
	
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private final IntIterator mIterator = mBits.iterator();
			
			public boolean hasNext() {return mIterator.hasNext();}
			
			public E next() {return mBasis.getElement(mIterator.nextInt());}
			
			public void remove() {mIterator.remove();}
		};
	}
	
	public boolean remove(Object element) {
		E based = mBasis.getElement(element);
		return based != null && mBits.remove(mBasis.getIndex(based));
	}
	
	public int size() {return mBits.size();}
	
	public boolean addAll(Collection<? extends E> that) {
		if (that instanceof BasedSet) {
			BasedSet based = (BasedSet)that;
			if (mBasis.equals(based.mBasis))
				return mBits.addAll(based.mBits);
		}
		return super.addAll(that);
	}
	
	public boolean retainAll(Collection<?> that) {
		if (that instanceof BasedSet) {
			BasedSet based = (BasedSet)that;
			if (mBasis.equals(based.mBasis))
				return mBits.retainAll(based.mBits);
		}
		return super.retainAll(that);
	}
	
	public boolean removeAll(Collection<?> that) {
		if (that instanceof BasedSet) {
			BasedSet based = (BasedSet)that;
			if (mBasis.equals(based.mBasis))
				return mBits.removeAll(based.mBits);
		}
		return super.removeAll(that);
	}
}
