package util;

import java.util.AbstractCollection;
import java.util.Iterator;

public abstract class LinkList<E> extends AbstractCollection<E> {
	private static final LinkList mEmpty = new LinkList() {
		public boolean isEmpty() {return true;}
		public int size() {return 0;}
		
		public Object getHead() {throw new UnsupportedOperationException();}
		public LinkList getTail() {throw new UnsupportedOperationException();}
		
		public boolean contains(Object o) {return false;}
		
		public Iterator iterator() {
			return java.util.Collections.EMPTY_LIST.iterator();
		}
	};
	
	private static final class Link<E> extends LinkList<E> {
		private final E mHead;
		private final LinkList<E> mTail;
		
		public Link(E head, LinkList<E> tail) {mHead = head; mTail = tail;}
		
		public boolean isEmpty() {return false;}
		public int size() {return 1 + mTail.size();}
		
		public E getHead() {return mHead;}
		public LinkList<E> getTail() {return mTail;}
		
		public boolean contains(Object o) {
			return (mHead == null ? o == null : mHead.equals(o))
					|| mTail.contains(o);
		}

		public Iterator<E> iterator() {return new LinkIterator<E>(this);}
	}
	
	private static final class LinkIterator<E> extends AbstractIterator<E> {
		private LinkList<E> mList;
		
		public LinkIterator(LinkList<E> list) {mList = list;}
		
		public boolean hasNext() {return !mList.isEmpty();}

		public E next() throws IllegalStateException {
			if (!hasNext())
				throw new IllegalStateException();
			E next = mList.getHead();
			mList = mList.getTail();
			return next;
		}
	}
	
	public static <E> LinkList<E> empty() {return mEmpty;}
	
	public abstract E getHead();
	public abstract LinkList<E> getTail();
	
	public LinkList<E> prepend(E element) {return new Link<E>(element, this);}
}
