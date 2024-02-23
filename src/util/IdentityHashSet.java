package util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;

public class IdentityHashSet<E> extends AbstractSet<E> {
	protected IdentityHashMap<E,E> mSet = new IdentityHashMap<E,E>();
	
	public IdentityHashSet() {}
	public IdentityHashSet(Collection<? extends E> elements) {
		addAll(elements);
	}

	public Iterator<E> iterator() {return mSet.keySet().iterator();}

	public int size() {return mSet.size();}
	
	public boolean contains(Object element) {return mSet.containsKey(element);}
	
	public boolean add(E element) {return mSet.put(element, element) == null;}
	
	public boolean remove(Object element) {return mSet.remove(element) != null;}
	
	public void clear() {mSet.clear();}
}
