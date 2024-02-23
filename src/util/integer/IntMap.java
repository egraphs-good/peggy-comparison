/*
 * @(#)Map.java 1.48 04/06/28
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package util.integer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * An object that maps keys to values.  A map cannot contain duplicate keys;
 * each key can map to at most one value.
 *
 * <p>This interface takes the place of the <tt>Dictionary</tt> class, which
 * was a totally abstract class rather than an interface.
 *
 * <p>The <tt>Map</tt> interface provides three <i>collection views</i>, which
 * allow a map's contents to be viewed as a set of keys, collection of values,
 * or set of key-value mappings.  The <i>order</i> of a map is defined as
 * the order in which the iterators on the map's collection views return their
 * elements.  Some map implementations, like the <tt>TreeMap</tt> class, make
 * specific guarantees as to their order; others, like the <tt>HashMap</tt>
 * class, do not.
 *
 * <p>Note: great care must be exercised if mutable objects are used as map
 * keys.  The behavior of a map is not specified if the value of an object is
 * changed in a manner that affects equals comparisons while the object is a
 * key in the map.  A special case of this prohibition is that it is not
 * permissible for a map to contain itself as a key.  While it is permissible
 * for a map to contain itself as a value, extreme caution is advised: the
 * equals and hashCode methods are no longer well defined on a such a map.
 *
 * <p>All general-purpose map implementation classes should provide two
 * "standard" constructors: a void (no arguments) constructor which creates an
 * empty map, and a constructor with a single argument of type <tt>Map</tt>,
 * which creates a new map with the same key-value mappings as its argument.
 * In effect, the latter constructor allows the user to copy any map,
 * producing an equivalent map of the desired class.  There is no way to
 * enforce this recommendation (as interfaces cannot contain constructors) but
 * all of the general-purpose map implementations in the JDK comply.
 *
 * <p>The "destructive" methods contained in this interface, that is, the
 * methods that modify the map on which they operate, are specified to throw
 * <tt>UnsupportedOperationException</tt> if this map does not support the
 * operation.  If this is the case, these methods may, but are not required
 * to, throw an <tt>UnsupportedOperationException</tt> if the invocation would
 * have no effect on the map.  For example, invoking the {@link #putAll(Map)}
 * method on an unmodifiable map may, but is not required to, throw the
 * exception if the map whose mappings are to be "superimposed" is empty.
 *
 * <p>Some map implementations have restrictions on the keys and values they
 * may contain.  For example, some implementations prohibit null keys and
 * values, and some have restrictions on the types of their keys.  Attempting
 * to insert an ineligible key or value throws an unchecked exception,
 * typically <tt>NullPointerException</tt> or <tt>ClassCastException</tt>.
 * Attempting to query the presence of an ineligible key or value may throw an
 * exception, or it may simply return false; some implementations will exhibit
 * the former behavior and some will exhibit the latter.  More generally,
 * attempting an operation on an ineligible key or value whose completion
 * would not result in the insertion of an ineligible element into the map may
 * throw an exception or it may succeed, at the option of the implementation.
 * Such exceptions are marked as "optional" in the specification for this
 * interface.
 *
 * <p>This interface is a member of the
 * <a href="{@docRoot}/../guide/collections/index.html">
 * Java Collections Framework</a>.
 *
 * <p>Many methods in Collections Framework interfaces are defined
 * in terms of the {@link Object#equals(Object) equals} method.  For
 * example, the specification for the {@link #containsKey(Object)
 * contains(Object key)} method says: "returns <tt>true</tt> if and
 * only if this map contain a mapping for a key <tt>k</tt> such that
 * <tt>(key==null ? k==null : key.equals(k))</tt>." This specification should
 * <i>not</i> be construed to imply that invoking <tt>Map.containsKey</tt>
 * with a non-null argument <tt>key</tt> will cause <tt>key.equals(k)</tt> to
 * be invoked for any key <tt>k</tt>.  Implementations are free to
 * implement optimizations whereby the <tt>equals</tt> invocation is avoided,
 * for example, by first comparing the hash codes of the two keys.  (The
 * {@link Object#hashCode()} specification guarantees that two objects with
 * unequal hash codes cannot be equal.)  More generally, implementations of
 * the various Collections Framework interfaces are free to take advantage of
 * the specified behavior of underlying {@link Object} methods wherever the
 * implementor deems it appropriate.
 *
 * @author  Josh Bloch
 * @version 1.48, 06/28/04
 * @see HashMap
 * @see TreeMap
 * @see Hashtable
 * @see SortedMap
 * @see Collection
 * @see Set
 * @since 1.2
 */
public interface IntMap<V> extends Map<Integer,V> {
   // Query Operations

   /**
    * Returns <tt>true</tt> if this map contains a mapping for the specified
    * key.  More formally, returns <tt>true</tt> if and only if
    * this map contains a mapping for a key <tt>k</tt> such that
    * <tt>(key==null ? k==null : key.equals(k))</tt>.  (There can be
    * at most one such mapping.)
    *
    * @param key key whose presence in this map is to be tested.
    * @return <tt>true</tt> if this map contains a mapping for the specified
    *         key.
    *
    * @throws ClassCastException if the key is of an inappropriate type for
    *            this map (optional).
    * @throws NullPointerException if the key is <tt>null</tt> and this map
    *            does not permit <tt>null</tt> keys (optional).
    */
   boolean containsKey(int key);

   /**
    * Returns the value to which this map maps the specified key.  Returns
    * <tt>null</tt> if the map contains no mapping for this key.  A return
    * value of <tt>null</tt> does not <i>necessarily</i> indicate that the
    * map contains no mapping for the key; it's also possible that the map
    * explicitly maps the key to <tt>null</tt>.  The <tt>containsKey</tt>
    * operation may be used to distinguish these two cases.
    *
    * <p>More formally, if this map contains a mapping from a key
    * <tt>k</tt> to a value <tt>v</tt> such that <tt>(key==null ? k==null :
    * key.equals(k))</tt>, then this method returns <tt>v</tt>; otherwise
    * it returns <tt>null</tt>.  (There can be at most one such mapping.)
    *
    * @param key key whose associated value is to be returned.
    * @return the value to which this map maps the specified key, or
    *         <tt>null</tt> if the map contains no mapping for this key.
    *
    * @throws ClassCastException if the key is of an inappropriate type for
    *            this map (optional).
    * @throws NullPointerException if the key is <tt>null</tt> and this map
    *            does not permit <tt>null</tt> keys (optional).
    *
    * @see #containsKey(Object)
    */
   V get(int key);

   // Modification Operations

   /**
    * Associates the specified value with the specified key in this map
    * (optional operation).  If the map previously contained a mapping for
    * this key, the old value is replaced by the specified value.  (A map
    * <tt>m</tt> is said to contain a mapping for a key <tt>k</tt> if and only
    * if {@link #containsKey(Object) m.containsKey(k)} would return
    * <tt>true</tt>.))
    *
    * @param key key with which the specified value is to be associated.
    * @param value value to be associated with the specified key.
    * @return previous value associated with specified key, or <tt>null</tt>
    *         if there was no mapping for key.  A <tt>null</tt> return can
    *         also indicate that the map previously associated <tt>null</tt>
    *         with the specified key, if the implementation supports
    *         <tt>null</tt> values.
    *
    * @throws UnsupportedOperationException if the <tt>put</tt> operation is
    *            not supported by this map.
    * @throws ClassCastException if the class of the specified key or value
    *            prevents it from being stored in this map.
    * @throws IllegalArgumentException if some aspect of this key or value
    *            prevents it from being stored in this map.
    * @throws NullPointerException if this map does not permit <tt>null</tt>
    *            keys or values, and the specified key or value is
    *            <tt>null</tt>.
    */
   V put(int key, V value);

   /**
    * Removes the mapping for this key from this map if it is present
    * (optional operation).   More formally, if this map contains a mapping
    * from key <tt>k</tt> to value <tt>v</tt> such that
    * <code>(key==null ?  k==null : key.equals(k))</code>, that mapping
    * is removed.  (The map can contain at most one such mapping.)
    *
    * <p>Returns the value to which the map previously associated the key, or
    * <tt>null</tt> if the map contained no mapping for this key.  (A
    * <tt>null</tt> return can also indicate that the map previously
    * associated <tt>null</tt> with the specified key if the implementation
    * supports <tt>null</tt> values.)  The map will not contain a mapping for
    * the specified  key once the call returns.
    *
    * @param key key whose mapping is to be removed from the map.
    * @return previous value associated with specified key, or <tt>null</tt>
    *         if there was no mapping for key.
    *
    * @throws ClassCastException if the key is of an inappropriate type for
    *            this map (optional).
    * @throws NullPointerException if the key is <tt>null</tt> and this map
    *            does not permit <tt>null</tt> keys (optional).
    * @throws UnsupportedOperationException if the <tt>remove</tt> method is
    *         not supported by this map.
    */
   V remove(int key);

   // Bulk Operations

   /**
    * Copies all of the mappings from the specified map to this map
    * (optional operation).  The effect of this call is equivalent to that
    * of calling {@link #put(Object,Object) put(k, v)} on this map once
    * for each mapping from key <tt>k</tt> to value <tt>v</tt> in the
    * specified map.  The behavior of this operation is unspecified if the
    * specified map is modified while the operation is in progress.
    *
    * @param t Mappings to be stored in this map.
    *
    * @throws UnsupportedOperationException if the <tt>putAll</tt> method is
    *            not supported by this map.
    *
    * @throws ClassCastException if the class of a key or value in the
    *            specified map prevents it from being stored in this map.
    *
    * @throws IllegalArgumentException some aspect of a key or value in the
    *            specified map prevents it from being stored in this map.
    * @throws NullPointerException if the specified map is <tt>null</tt>, or if
    *         this map does not permit <tt>null</tt> keys or values, and the
    *         specified map contains <tt>null</tt> keys or values.
    */
   void putAll(IntMap<? extends V> t);

   // Views

   /**
    * Returns a set view of the keys contained in this map.  The set is
    * backed by the map, so changes to the map are reflected in the set, and
    * vice-versa.  If the map is modified while an iteration over the set is
    * in progress (except through the iterator's own <tt>remove</tt>
    * operation), the results of the iteration are undefined.  The set
    * supports element removal, which removes the corresponding mapping from
    * the map, via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
    * <tt>removeAll</tt> <tt>retainAll</tt>, and <tt>clear</tt> operations.
    * It does not support the add or <tt>addAll</tt> operations.
    *
    * @return a set view of the keys contained in this map.
    */
   IntSet keySet();

   /**
    * Returns a collection view of the values contained in this map.  The
    * collection is backed by the map, so changes to the map are reflected in
    * the collection, and vice-versa.  If the map is modified while an
    * iteration over the collection is in progress (except through the
    * iterator's own <tt>remove</tt> operation), the results of the
    * iteration are undefined.  The collection supports element removal,
    * which removes the corresponding mapping from the map, via the
    * <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
    * <tt>removeAll</tt>, <tt>retainAll</tt> and <tt>clear</tt> operations.
    * It does not support the add or <tt>addAll</tt> operations.
    *
    * @return a collection view of the values contained in this map.
    */
   Collection<V> values();

   /**
    * Returns a set view of the mappings contained in this map.  Each element
    * in the returned set is a {@link Map.Entry}.  The set is backed by the
    * map, so changes to the map are reflected in the set, and vice-versa.
    * If the map is modified while an iteration over the set is in progress
    * (except through the iterator's own <tt>remove</tt> operation, or through
    * the <tt>setValue</tt> operation on a map entry returned by the iterator)
    * the results of the iteration are undefined.  The set supports element
    * removal, which removes the corresponding mapping from the map, via the
    * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>,
    * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not support
    * the <tt>add</tt> or <tt>addAll</tt> operations.
    *
    * @return a set view of the mappings contained in this map.
    */
   Set<? extends IntMap.Entry<V>> intEntrySet();

   /**
    * A map entry (key-value pair).  The <tt>Map.entrySet</tt> method returns
    * a collection-view of the map, whose elements are of this class.  The
    * <i>only</i> way to obtain a reference to a map entry is from the
    * iterator of this collection-view.  These <tt>Map.Entry</tt> objects are
    * valid <i>only</i> for the duration of the iteration; more formally,
    * the behavior of a map entry is undefined if the backing map has been
    * modified after the entry was returned by the iterator, except through
    * the <tt>setValue</tt> operation on the map entry.
    *
    * @see Map#entrySet()
    * @since 1.2
    */
   interface Entry<V> extends Map.Entry<Integer,V> {
       /**
        * Returns the key corresponding to this entry.
        *
        * @return the key corresponding to this entry.
        * @throws IllegalStateException implementations may, but are not
        *         required to, throw this exception if the entry has been
        *         removed from the backing map
        */
       int getIntKey();
   }

   // Comparison and hashing

   /**
    * Compares the specified object with this map for equality.  Returns
    * <tt>true</tt> if the given object is also a map and the two Maps
    * represent the same mappings.  More formally, two maps <tt>t1</tt> and
    * <tt>t2</tt> represent the same mappings if
    * <tt>t1.entrySet().equals(t2.entrySet())</tt>.  This ensures that the
    * <tt>equals</tt> method works properly across different implementations
    * of the <tt>Map</tt> interface.
    *
    * @param o object to be compared for equality with this map.
    * @return <tt>true</tt> if the specified object is equal to this map.
    */
   boolean equals(IntMap that);
}