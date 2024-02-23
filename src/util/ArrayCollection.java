/*
 * @(#)ArrayList.java	1.47 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package util;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Resizable-array implementation of the <tt>List</tt> interface.  Implements
 * all optional list operations, and permits all elements, including
 * <tt>null</tt>.  In addition to implementing the <tt>List</tt> interface,
 * this class provides methods to manipulate the size of the array that is
 * used internally to store the list.  (This class is roughly equivalent to
 * <tt>Vector</tt>, except that it is unsynchronized.)<p>
 *
 * The <tt>size</tt>, <tt>isEmpty</tt>, <tt>get</tt>, <tt>set</tt>,
 * <tt>iterator</tt>, and <tt>listIterator</tt> operations run in constant
 * time.  The <tt>add</tt> operation runs in <i>amortized constant time</i>,
 * that is, adding n elements requires O(n) time.  All of the other operations
 * run in linear time (roughly speaking).  The constant factor is low compared
 * to that for the <tt>LinkedList</tt> implementation.<p>
 *
 * Each <tt>ArrayList</tt> instance has a <i>capacity</i>.  The capacity is
 * the size of the array used to store the elements in the list.  It is always
 * at least as large as the list size.  As elements are added to an ArrayList,
 * its capacity grows automatically.  The details of the growth policy are not
 * specified beyond the fact that adding an element has constant amortized
 * time cost.<p> 
 *
 * An application can increase the capacity of an <tt>ArrayList</tt> instance
 * before adding a large number of elements using the <tt>ensureCapacity</tt>
 * operation.  This may reduce the amount of incremental reallocation.<p>
 *
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an <tt>ArrayList</tt> instance concurrently, and at
 * least one of the threads modifies the list structurally, it <i>must</i> be
 * synchronized externally.  (A structural modification is any operation that
 * adds or deletes one or more elements, or explicitly resizes the backing
 * array; merely setting the value of an element is not a structural
 * modification.)  This is typically accomplished by synchronizing on some
 * object that naturally encapsulates the list.  If no such object exists, the
 * list should be "wrapped" using the <tt>Collections.synchronizedList</tt>
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the list:
 * <pre>
 *	List list = Collections.synchronizedList(new ArrayList(...));
 * </pre><p>
 *
 * The iterators returned by this class's <tt>iterator</tt> and
 * <tt>listIterator</tt> methods are <i>fail-fast</i>: if list is structurally
 * modified at any time after the iterator is created, in any way except
 * through the iterator's own remove or add methods, the iterator will throw a
 * ConcurrentModificationException.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the
 * future.<p>
 *
 * Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw <tt>ConcurrentModificationException</tt> on a best-effort basis. 
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness: <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i><p>
 *
 * This class is a member of the 
 * <a href="{@docRoot}/../guide/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @version 1.47, 12/19/03
 * @see	    Collection
 * @see	    List
 * @see	    LinkedList
 * @see	    Vector
 * @see	    Collections#synchronizedList(List)
 * @since   1.2
 */

public class ArrayCollection<E> extends AbstractCollection<E>
        implements Collection<E>, Cloneable, java.io.Serializable {
	private static final long serialVersionUID = -3500197483794222953L;

	/**
     * The array buffer into which the elements of the ArrayList are stored.
     * The capacity of the ArrayList is the length of this array buffer.
     */
    protected transient E[] elementData;

    /**
     * The size of the ArrayList (the number of elements it contains).
     *
     * @serial
     */
    protected int size;
    
    protected int modCount;

    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param   initialCapacity   the initial capacity of the list.
     * @exception IllegalArgumentException if the specified initial capacity
     *            is negative
     */
    
	public ArrayCollection(int initialCapacity) {
    	super();
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        this.elementData = (E[])new Object[initialCapacity];
    }

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public ArrayCollection() {this(10);}

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.  The <tt>ArrayList</tt> instance has an initial capacity of
     * 110% the size of the specified collection.
     *
     * @param c the collection whose elements are to be placed into this list.
     * @throws NullPointerException if the specified collection is null.
     */
    
	public ArrayCollection(Collection<? extends E> c) {
        size = c.size();
        // Allow 10% room for growth
        elementData = (E[])new Object[
                      (int)Math.min((size*110L)/100,Integer.MAX_VALUE)]; 
        c.toArray(elementData);
    }

    /**
     * Trims the capacity of this <tt>ArrayList</tt> instance to be the
     * list's current size.  An application can use this operation to minimize
     * the storage of an <tt>ArrayList</tt> instance.
     */
    
	public void trimToSize() {
		modCount++;
		int oldCapacity = elementData.length;
		if (size < oldCapacity) {
		    Object oldData[] = elementData;
		    elementData = (E[])new Object[size];
		    System.arraycopy(oldData, 0, elementData, 0, size);
		}
    }

    /**
     * Increases the capacity of this <tt>ArrayList</tt> instance, if
     * necessary, to ensure  that it can hold at least the number of elements
     * specified by the minimum capacity argument. 
     *
     * @param   minCapacity   the desired minimum capacity.
     */
    
	public void ensureCapacity(int minCapacity) {
		modCount++;
		int oldCapacity = elementData.length;
		if (minCapacity > oldCapacity) {
		    Object oldData[] = elementData;
		    int newCapacity = (oldCapacity * 3)/2 + 1;
			    if (newCapacity < minCapacity)
			newCapacity = minCapacity;
		    elementData = (E[])new Object[newCapacity];
		    System.arraycopy(oldData, 0, elementData, 0, size);
		}
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return  the number of elements in this list.
     */
    public int size() {return size;}

    /**
     * Tests if this list has no elements.
     *
     * @return  <tt>true</tt> if this list has no elements;
     *          <tt>false</tt> otherwise.
     */
    public boolean isEmpty() {return size == 0;}

    /**
     * Returns <tt>true</tt> if this list contains the specified element.
     *
     * @param elem element whose presence in this List is to be tested.
     * @return  <code>true</code> if the specified element is present;
     *		<code>false</code> otherwise.
     */
    public boolean contains(Object elem) {return indexOf(elem) >= 0;}

    /**
     * Searches for the first occurence of the given argument, testing 
     * for equality using the <tt>equals</tt> method. 
     *
     * @param   elem   an object.
     * @return  the index of the first occurrence of the argument in this
     *          list; returns <tt>-1</tt> if the object is not found.
     * @see     Object#equals(Object)
     */
    protected int indexOf(Object elem) {
		if (elem == null) {
		    for (int i = 0; i < size; i++)
			if (elementData[i]==null)
			    return i;
		} else {
		    for (int i = 0; i < size; i++)
			if (elem.equals(elementData[i]))
			    return i;
		}
		return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified object in
     * this list.
     *
     * @param   elem   the desired element.
     * @return  the index of the last occurrence of the specified object in
     *          this list; returns -1 if the object is not found.
     */
    protected int lastIndexOf(Object elem) {
		if (elem == null) {
		    for (int i = size-1; i >= 0; i--)
			if (elementData[i]==null)
			    return i;
		} else {
		    for (int i = size-1; i >= 0; i--)
			if (elem.equals(elementData[i]))
			    return i;
		}
		return -1;
    }

    /**
     * Returns a shallow copy of this <tt>ArrayList</tt> instance.  (The
     * elements themselves are not copied.)
     *
     * @return  a clone of this <tt>ArrayList</tt> instance.
     */
    
	public Object clone() {
		try { 
			ArrayCollection<E> v = (ArrayCollection<E>)super.clone();
		    v.elementData = (E[])new Object[size];
		    System.arraycopy(elementData, 0, v.elementData, 0, size);
		    v.modCount = 0;
		    return v;
		} catch (CloneNotSupportedException e) { 
		    // this shouldn't happen, since we are Cloneable
		    throw new InternalError();
		}
    }

    /**
     * Returns an array containing all of the elements in this list
     * in the correct order.
     *
     * @return an array containing all of the elements in this list
     * 	       in the correct order.
     */
    public Object[] toArray() {
		Object[] result = new Object[size];
		System.arraycopy(elementData, 0, result, 0, size);
		return result;
    }

    /**
     * Returns an array containing all of the elements in this list in the
     * correct order; the runtime type of the returned array is that of the
     * specified array.  If the list fits in the specified array, it is
     * returned therein.  Otherwise, a new array is allocated with the runtime
     * type of the specified array and the size of this list.<p>
     *
     * If the list fits in the specified array with room to spare (i.e., the
     * array has more elements than the list), the element in the array
     * immediately following the end of the collection is set to
     * <tt>null</tt>.  This is useful in determining the length of the list
     * <i>only</i> if the caller knows that the list does not contain any
     * <tt>null</tt> elements.
     *
     * @param a the array into which the elements of the list are to
     *		be stored, if it is big enough; otherwise, a new array of the
     * 		same runtime type is allocated for this purpose.
     * @return an array containing the elements of the list.
     * @throws ArrayStoreException if the runtime type of a is not a supertype
     *         of the runtime type of every element in this list.
     */
    
	public <T> T[] toArray(T[] a) {
        if (a.length < size)
            a = (T[])java.lang.reflect.Array.
            	newInstance(a.getClass().getComponentType(), size);
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    // Positional Access Operations

    /**
     * Appends the specified element to the end of this list.
     *
     * @param o element to be appended to this list.
     * @return <tt>true</tt> (as per the general contract of Collection.add).
     */
    public boolean add(E o) {
		ensureCapacity(size + 1);  // Increments modCount!!
		elementData[size++] = o;
		return true;
    }

    /**
     * Removes a single instance of the specified element from this
     * list, if it is present (optional operation).  More formally,
     * removes an element <tt>e</tt> such that <tt>(o==null ? e==null :
     * o.equals(e))</tt>, if the list contains one or more such
     * elements.  Returns <tt>true</tt> if the list contained the
     * specified element (or equivalently, if the list changed as a
     * result of the call).<p>
     *
     * @param o element to be removed from this list, if present.
     * @return <tt>true</tt> if the list contained the specified element.
     */
    public boolean remove(Object o) {
		if (o == null) {
	        for (int index = 0; index < size; index++)
				if (elementData[index] == null) {
					remove(index);
				    return true;
				}
		} else
		    for (int index = 0; index < size; index++)
				if (o.equals(elementData[index])) {
					remove(index);
				    return true;
				}
		return false;
    }

    /*
     * Private remove method that skips bounds checking and does not
     * return the value removed.
     */
    protected void remove(int index) {
        modCount++;
        elementData[index] = elementData[--size];
        elementData[size] = null; // Let gc do its work
    }

    /**
     * Removes all of the elements from this list.  The list will
     * be empty after this call returns.
     */
    public void clear() {
		modCount++;
		Arrays.fill(elementData, null);
		size = 0;
    }

    /**
     * Appends all of the elements in the specified Collection to the end of
     * this list, in the order that they are returned by the
     * specified Collection's Iterator.  The behavior of this operation is
     * undefined if the specified Collection is modified while the operation
     * is in progress.  (This implies that the behavior of this call is
     * undefined if the specified Collection is this list, and this
     * list is nonempty.)
     *
     * @param c the elements to be inserted into this list.
     * @return <tt>true</tt> if this list changed as a result of the call.
     * @throws    NullPointerException if the specified collection is null.
     */
    public boolean addAll(Collection<? extends E> c) {
		Object[] a = c.toArray();
        int numNew = a.length;
		ensureCapacity(size + numNew);  // Increments modCount
        System.arraycopy(a, 0, elementData, size, numNew);
        size += numNew;
		return numNew != 0;
    }

    /**
     * Inserts all of the elements in the specified Collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified Collection's iterator.
     *
     * @param index index at which to insert first element
     *		    from the specified collection.
     * @param c elements to be inserted into this list.
     * @return <tt>true</tt> if this list changed as a result of the call.
     * @throws    IndexOutOfBoundsException if index out of range <tt>(index
     *		  &lt; 0 || index &gt; size())</tt>.
     * @throws    NullPointerException if the specified Collection is null.
     */
    public boolean addAll(int index, Collection<? extends E> c) {
		if (index > size || index < 0)
		    throw new IndexOutOfBoundsException(
		    		"Index: " + index + ", Size: " + size);
	
		Object[] a = c.toArray();
		int numNew = a.length;
		ensureCapacity(size + numNew);  // Increments modCount
	
		int numMoved = size - index;
		if (numMoved > 0)
		    System.arraycopy(elementData, index, elementData, index + numNew,
				     numMoved);
	
        System.arraycopy(a, 0, elementData, index, numNew);
		size += numNew;
		return numNew != 0;
    }

    /**
     * Save the state of the <tt>ArrayList</tt> instance to a stream (that
     * is, serialize it).
     *
     * @serialData The length of the array backing the <tt>ArrayList</tt>
     *             instance is emitted (int), followed by all of its elements
     *             (each an <tt>Object</tt>) in the proper order.
     */
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException{
		// Write out element count, and any hidden stuff
		s.defaultWriteObject();

        // Write out array length
        s.writeInt(elementData.length);

		// Write out all elements in the proper order.
		for (int i=0; i<size; i++)
	            s.writeObject(elementData[i]);
    }

    /**
     * Reconstitute the <tt>ArrayList</tt> instance from a stream (that is,
     * deserialize it).
     */
    
	private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
		// Read in size, and any hidden stuff
		s.defaultReadObject();

        // Read in array length and allocate array
        int arrayLength = s.readInt();
        Object[] a = elementData = (E[])new Object[arrayLength];

		// Read in all elements in the proper order.
		for (int i=0; i<size; i++)
	            a[i] = s.readObject();
    }

	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private int next = 0;

			public boolean hasNext() {return next < size;}

			public E next() {
				if (!hasNext())
					throw new IllegalStateException();
				return elementData[next++];
			}

			public void remove() {
				if (next == 0)
					throw new IllegalStateException();
				ArrayCollection.this.remove(--next);
			}
		};
	}
}
