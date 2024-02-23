package llvm.values;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This is just an iterator that returns Values.
 */
public class ValueIterator implements Iterator<Value> {
	private final Value[] values;
	private int index;
	
	public ValueIterator(Value... _values) {
		this.values = (Value[])_values.clone();
		this.index = 0;
	}
	
	public boolean hasNext() {
		return this.index < this.values.length;
	}
	public Value next() {
		if (!this.hasNext())
			throw new NoSuchElementException();
		return this.values[this.index++];
	}
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
