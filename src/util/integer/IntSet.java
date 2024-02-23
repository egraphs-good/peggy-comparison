/*
 * @(#)Set.java 1.35 04/02/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package util.integer;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A collection that contains no duplicate elements.  More formally, sets
 * contain no pair of elements <code>e1</code> and <code>e2</code> such that
 * <code>e1.equals(e2)</code>, and at most one null element.  As implied by
 * its name, this interface models the mathematical <i>set</i> abstraction.<p>
 *
 * The <tt>Set</tt> interface places additional stipulations, beyond those
 * inherited from the <tt>Collection</tt> interface, on the contracts of all
 * constructors and on the contracts of the <tt>add</tt>, <tt>equals</tt> and
 * <tt>hashCode</tt> methods.  Declarations for other inherited methods are
 * also included here for convenience.  (The specifications accompanying these
 * declarations have been tailored to the <tt>Set</tt> interface, but they do
 * not contain any additional stipulations.)<p>
 *
 * The additional stipulation on constructors is, not surprisingly,
 * that all constructors must create a set that contains no duplicate elements
 * (as defined above).<p>
 *
 * Note: Great care must be exercised if mutable objects are used as set
 * elements.  The behavior of a set is not specified if the value of an object
 * is changed in a manner that affects equals comparisons while the object is
 * an element in the set.  A special case of this prohibition is that it is
 * not permissible for a set to contain itself as an element.
 *
 * <p>Some set implementations have restrictions on the elements that
 * they may contain.  For example, some implementations prohibit null elements,
 * and some have restrictions on the types of their elements.  Attempting to
 * add an ineligible element throws an unchecked exception, typically
 * <tt>NullPointerException</tt> or <tt>ClassCastException</tt>.  Attempting
 * to query the presence of an ineligible element may throw an exception,
 * or it may simply return false; some implementations will exhibit the former
 * behavior and some will exhibit the latter.  More generally, attempting an
 * operation on an ineligible element whose completion would not result in
 * the insertion of an ineligible element into the set may throw an
 * exception or it may succeed, at the option of the implementation.
 * Such exceptions are marked as "optional" in the specification for this
 * interface.
 *
 * <p>This interface is a member of the
 * <a href="{@docRoot}/../guide/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @version 1.35, 02/19/04
 * @see Collection
 * @see List
 * @see SortedSet
 * @see HashSet
 * @see TreeSet
 * @see AbstractSet
 * @see Collections#singleton(java.lang.Object)
 * @see Collections#EMPTY_SET
 * @since 1.2
 */

public interface IntSet extends IntCollection, Set<Integer> {
   // Modification Operations

   /**
    * Adds the specified element to this set if it is not already present
    * (optional operation).  More formally, adds the specified element,
    * <code>o</code>, to this set if this set contains no element
    * <code>e</code> such that <code>(o==null ? e==null :
    * o.equals(e))</code>.  If this set already contains the specified
    * element, the call leaves this set unchanged and returns <tt>false</tt>.
    * In combination with the restriction on constructors, this ensures that
    * sets never contain duplicate elements.<p>
    *
    * The stipulation above does not imply that sets must accept all
    * elements; sets may refuse to add any particular element, including
    * <tt>null</tt>, and throwing an exception, as described in the
    * specification for <tt>Collection.add</tt>.  Individual set
    * implementations should clearly document any restrictions on the
    * elements that they may contain.
    *
    * @param o element to be added to this set.
    * @return <tt>true</tt> if this set did not already contain the specified
    *         element.
    *
    * @throws UnsupportedOperationException if the <tt>add</tt> method is not
    *         supported by this set.
    * @throws ClassCastException if the class of the specified element
    *         prevents it from being added to this set.
    * @throws NullPointerException if the specified element is null and this
    *         set does not support null elements.
    * @throws IllegalArgumentException if some aspect of the specified element
    *         prevents it from being added to this set.
    */
   boolean add(int o);

   // Bulk Operations

   /**
    * Adds all of the elements in the specified collection to this set if
    * they're not already present (optional operation).  If the specified
    * collection is also a set, the <tt>addAll</tt> operation effectively
    * modifies this set so that its value is the <i>union</i> of the two
    * sets.  The behavior of this operation is unspecified if the specified
    * collection is modified while the operation is in progress.
    *
    * @param c collection whose elements are to be added to this set.
    * @return <tt>true</tt> if this set changed as a result of the call.
    *
    * @throws UnsupportedOperationException if the <tt>addAll</tt> method is
    *            not supported by this set.
    * @throws ClassCastException if the class of some element of the
    *            specified collection prevents it from being added to this
    *            set.
    * @throws NullPointerException if the specified collection contains one
    *           or more null elements and this set does not support null
    *           elements, or if the specified collection is <tt>null</tt>.
    * @throws IllegalArgumentException if some aspect of some element of the
    *            specified collection prevents it from being added to this
    *            set.
    * @see #add(Object)
    */
   boolean addAll(IntCollection c);

   /**
    * Retains only the elements in this set that are contained in the
    * specified collection (optional operation).  In other words, removes
    * from this set all of its elements that are not contained in the
    * specified collection.  If the specified collection is also a set, this
    * operation effectively modifies this set so that its value is the
    * <i>intersection</i> of the two sets.
    *
    * @param c collection that defines which elements this set will retain.
    * @return <tt>true</tt> if this collection changed as a result of the
    *         call.
    * @throws UnsupportedOperationException if the <tt>retainAll</tt> method
    *            is not supported by this Collection.
    * @throws ClassCastException if the types of one or more elements in this
    *            set are incompatible with the specified collection
    *            (optional).
    * @throws NullPointerException if this set contains a null element and
    *            the specified collection does not support null elements
    *            (optional).
    * @throws NullPointerException if the specified collection is
    *           <tt>null</tt>.
    * @see #remove(Object)
    */
   boolean retainAll(IntCollection c);

   /**
    * Removes from this set all of its elements that are contained in the
    * specified collection (optional operation).  If the specified
    * collection is also a set, this operation effectively modifies this
    * set so that its value is the <i>asymmetric set difference</i> of
    * the two sets.
    *
    * @param  c collection that defines which elements will be removed from
    *           this set.
    * @return <tt>true</tt> if this set changed as a result of the call.
    *
    * @throws UnsupportedOperationException if the <tt>removeAll</tt>
    *            method is not supported by this Collection.
    * @throws ClassCastException if the types of one or more elements in this
    *            set are incompatible with the specified collection
    *            (optional).
    * @throws NullPointerException if this set contains a null element and
    *            the specified collection does not support null elements
    *            (optional).
    * @throws NullPointerException if the specified collection is
    *           <tt>null</tt>.
    * @see    #remove(Object)
    */
   boolean removeAll(IntCollection c);

   // Comparison and hashing

   /**
    * Compares the specified object with this set for equality.  Returns
    * <tt>true</tt> if the specified object is also a set, the two sets
    * have the same size, and every member of the specified set is
    * contained in this set (or equivalently, every member of this set is
    * contained in the specified set).  This definition ensures that the
    * equals method works properly across different implementations of the
    * set interface.
    *
    * @param o Object to be compared for equality with this set.
    * @return <tt>true</tt> if the specified Object is equal to this set.
    */
   boolean equals(IntSet o);
}