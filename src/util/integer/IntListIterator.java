package util.integer;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 *
 * An iterator for lists that allows the programmer
 * to traverse the list in either direction, modify
 * the list during iteration, and obtain the iterator's
 * current position in the list. A <TT>ListIterator</TT>
 * has no current element; its <I>cursor position</I> always
 * lies between the element that would be returned by a call
 * to <TT>previous()</TT> and the element that would be
 * returned by a call to <TT>next()</TT>. In a list of
 * length <TT>n</TT>, there are <TT>n+1</TT> valid
 * index values, from <TT>0</TT> to <TT>n</TT>, inclusive.
 * <PRE>
 *
 *          Element(0)   Element(1)   Element(2)   ... Element(n)
 *        ^            ^            ^            ^               ^
 * Index: 0            1            2            3               n+1
 *
 * </PRE>
 * <P>
 * Note that the {@link #remove} and {@link #set(Object)} methods are
 * <i>not</i> defined in terms of the cursor position;  they are defined to
 * operate on the last element returned by a call to {@link #next} or {@link
 * #previous()}.
 * <P>
 * This interface is a member of the
 * <a href="{@docRoot}/../guide/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author  Josh Bloch
 * @version 1.23, 12/19/03
 * @see Collection
 * @see List
 * @see Iterator
 * @see Enumeration
 * @since   1.2
 */
public interface IntListIterator extends IntIterator, ListIterator<Integer> {
   // Query Operations

   /**
    * Returns the next element in the list.  This method may be called
    * repeatedly to iterate through the list, or intermixed with calls to
    * <tt>previous</tt> to go back and forth.  (Note that alternating calls
    * to <tt>next</tt> and <tt>previous</tt> will return the same element
    * repeatedly.)
    *
    * @return the next element in the list.
    * @exception NoSuchElementException if the iteration has no next element.
    */
   int nextInt();

   /**
    * Returns the previous element in the list.  This method may be called
    * repeatedly to iterate through the list backwards, or intermixed with
    * calls to <tt>next</tt> to go back and forth.  (Note that alternating
    * calls to <tt>next</tt> and <tt>previous</tt> will return the same
    * element repeatedly.)
    *
    * @return the previous element in the list.
    *
    * @exception NoSuchElementException if the iteration has no previous
    *            element.
    */
   int previousInt();

   // Modification Operations

   /**
    * Replaces the last element returned by <tt>next</tt> or
    * <tt>previous</tt> with the specified element (optional operation).
    * This call can be made only if neither <tt>ListIterator.remove</tt> nor
    * <tt>ListIterator.add</tt> have been called after the last call to
    * <tt>next</tt> or <tt>previous</tt>.
    *
    * @param o the element with which to replace the last element returned by
    *          <tt>next</tt> or <tt>previous</tt>.
    * @exception UnsupportedOperationException if the <tt>set</tt> operation
    *            is not supported by this list iterator.
    * @exception ClassCastException if the class of the specified element
    *            prevents it from being added to this list.
    * @exception IllegalArgumentException if some aspect of the specified
    *            element prevents it from being added to this list.
    * @exception IllegalStateException if neither <tt>next</tt> nor
    *            <tt>previous</tt> have been called, or <tt>remove</tt> or
    *            <tt>add</tt> have been called after the last call to
    *            <tt>next</tt> or <tt>previous</tt>.
    */
   void set(int o);

   /**
    * Inserts the specified element into the list (optional operation).  The
    * element is inserted immediately before the next element that would be
    * returned by <tt>next</tt>, if any, and after the next element that
    * would be returned by <tt>previous</tt>, if any.  (If the list contains
    * no elements, the new element becomes the sole element on the list.)
    * The new element is inserted before the implicit cursor: a subsequent
    * call to <tt>next</tt> would be unaffected, and a subsequent call to
    * <tt>previous</tt> would return the new element.  (This call increases
    * by one the value that would be returned by a call to <tt>nextIndex</tt>
    * or <tt>previousIndex</tt>.)
    *
    * @param o the element to insert.
    * @exception UnsupportedOperationException if the <tt>add</tt> method is
    *            not supported by this list iterator.
    *
    * @exception ClassCastException if the class of the specified element
    *            prevents it from being added to this list.
    *
    * @exception IllegalArgumentException if some aspect of this element
    *            prevents it from being added to this list.
    */
   void add(int o);
}