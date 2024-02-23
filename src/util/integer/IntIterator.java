package util.integer;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * An iterator over a collection.  Iterator takes the place of Enumeration in
 * the Java collections framework.  Iterators differ from enumerations in two
 * ways: <ul>
 *      <li> Iterators allow the caller to remove elements from the
 *           underlying collection during the iteration with well-defined
 *           semantics.
 *      <li> Method names have been improved.
 * </ul><p>
 *
 * This interface is a member of the
 * <a href="{@docRoot}/../guide/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author  Josh Bloch
 * @version 1.24, 01/17/04
 * @see Collection
 * @see ListIterator
 * @see Enumeration
 * @since 1.2
 */
public interface IntIterator extends Iterator<Integer> {
   /**
    * Returns the next element in the iteration.  Calling this method
    * repeatedly until the {@link #hasNext()} method returns false will
    * return each element in the underlying collection exactly once.
    *
    * @return the next element in the iteration.
    * @exception NoSuchElementException iteration has no more elements.
    */
   int nextInt();
}