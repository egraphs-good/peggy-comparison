package util;

import java.util.Iterator;

public abstract class AbstractIterable<E> implements Iterable<E> {
	public String toString() {
		StringBuilder string = new StringBuilder("[");
		for (Iterator<E> iterator = iterator(); iterator.hasNext(); ) {
			string.append(iterator.next());
			if (iterator.hasNext())
				string.append(", ");
		}
		string.append("]");
		return string.toString();
	}
}
