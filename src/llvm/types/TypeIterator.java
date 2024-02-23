package llvm.types;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class is a one-time-use type iterator.
 */
public class TypeIterator implements Iterator<Type> {
	private final Type[] types;
	private int index;
	
	public TypeIterator(Type... _types) {
		this.types = (Type[])_types.clone();
		this.index = 0;
	}
	public boolean hasNext() {
		return this.index < this.types.length;
	}
	public Type next() {
		if (!hasNext())
			throw new NoSuchElementException();
		return this.types[this.index++];
	}
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
