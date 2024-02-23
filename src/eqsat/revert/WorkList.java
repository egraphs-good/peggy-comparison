package eqsat.revert;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class WorkList<E> {
	protected final Set<E> mWorkList = new HashSet<E>();
	
	public void add(E work) {mWorkList.add(work);}
	public void addAll(Collection<? extends E> works) {mWorkList.addAll(works);}
	public void addAll(Iterable<? extends E> works) {
		for (E work : works)
			add(work);
	}
	
	public E pop() {
		Iterator<E> works = mWorkList.iterator();
		E work = works.next();
		works.remove();
		return work;
	}
	
	public boolean isEmpty() {return mWorkList.isEmpty();}
}
