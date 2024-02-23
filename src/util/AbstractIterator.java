package util;

import java.util.Iterator;

public abstract class AbstractIterator<E> implements Iterator<E> {
	public void remove() {throw new UnsupportedOperationException();}
}
