package util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

public class WeakCollection<E> extends AbstractCollection<E> {
	protected final ReferenceQueue<E> mQueue = new ReferenceQueue<E>();
	protected final Collection<Reference<E>> mCollection;
	
	protected WeakCollection(Collection<Reference<E>> collection) {
		mCollection = collection;
	}
	
	protected WeakReference<E> makeReference(E element) {
		return element == null ? null : new WeakReference<E>(element, mQueue);
	}
	
	protected Collection<Reference<E>> getCollection() {
		Reference<? extends E> ref;
		while ((ref = mQueue.poll()) != null)
			mCollection.remove(ref);
		return mCollection;
	}

	public boolean add(E element) {
		return getCollection().add(makeReference(element));
	}

	public void clear() {mCollection.clear();}

	public boolean isEmpty() {return getCollection().isEmpty();}

	public Iterator<E> iterator() {
		final Iterator<Reference<E>> iterator = getCollection().iterator();
		return new Iterator<E>() {
			private boolean mValid = false;
			private E mNext = null;

			public boolean hasNext() {
				if (mValid)
					return true;
				while (iterator.hasNext()) {
					Reference<E> nextRef = iterator.next();
					if (nextRef == null) {
						mNext = null;
						mValid = true;
						return true;
					}
					E next = nextRef.get();
					if (next != null) {
						mNext = next;
						mValid = true;
						return true;
					}
				}
				return false;
			}

			public E next() {
				if (!hasNext())
					throw new IllegalStateException();
				mValid = false;
				E next = mNext;
				mNext = null;
				return next;
			}

			public void remove() {throw new UnsupportedOperationException();}
		};
	}

	public int size() {return getCollection().size();}
}
