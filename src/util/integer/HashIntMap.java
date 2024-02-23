/*
 * @(#)Hashtable.java   1.105 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package util.integer;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class implements a hashtable, which maps keys to values. Any non-<code>null</code>
 * object can be used as a key or as a value.
 * <p>
 *
 * To successfully store and retrieve objects from a hashtable, the objects used
 * as keys must implement the <code>hashCode</code> method and the
 * <code>equals</code> method.
 * <p>
 *
 * An instance of <code>Hashtable</code> has two parameters that affect its
 * performance: <i>initial capacity</i> and <i>load factor</i>. The
 * <i>capacity</i> is the number of <i>buckets</i> in the hash table, and the
 * <i>initial capacity</i> is simply the capacity at the time the hash table is
 * created. Note that the hash table is <i>open</i>: in the case of a "hash
 * collision", a single bucket stores multiple entries, which must be searched
 * sequentially. The <i>load factor</i> is a measure of how full the hash table
 * is allowed to get before its capacity is automatically increased. The initial
 * capacity and load factor parameters are merely hints to the implementation.
 * The exact details as to when and whether the rehash method is invoked are
 * implementation-dependent.
 * <p>
 *
 * Generally, the default load factor (.75) offers a good tradeoff between time
 * and space costs. Higher values decrease the space overhead but increase the
 * time cost to look up an entry (which is reflected in most <tt>Hashtable</tt>
 * operations, including <tt>get</tt> and <tt>put</tt>).
 * <p>
 *
 * The initial capacity controls a tradeoff between wasted space and the need
 * for <code>rehash</code> operations, which are time-consuming. No
 * <code>rehash</code> operations will <i>ever</i> occur if the initial
 * capacity is greater than the maximum number of entries the <tt>Hashtable</tt>
 * will contain divided by its load factor. However, setting the initial
 * capacity too high can waste space.
 * <p>
 *
 * If many entries are to be made into a <code>Hashtable</code>, creating it
 * with a sufficiently large capacity may allow the entries to be inserted more
 * efficiently than letting it perform automatic rehashing as needed to grow the
 * table.
 * <p>
 *
 * This example creates a hashtable of numbers. It uses the names of the numbers
 * as keys:
 * <p>
 * <blockquote>
 *
 * <pre>
 * Hashtable numbers = new Hashtable();
 * numbers.put(&quot;one&quot;, new Integer(1));
 * numbers.put(&quot;two&quot;, new Integer(2));
 * numbers.put(&quot;three&quot;, new Integer(3));
 * </pre>
 *
 * </blockquote>
 * <p>
 * To retrieve a number, use the following code:
 * <p>
 * <blockquote>
 *
 * <pre>
 * Integer n = (Integer) numbers.get(&quot;two&quot;);
 * if (n != null) {
 *      System.out.println(&quot;two = &quot; + n);
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * As of the Java 2 platform v1.2, this class has been retrofitted to implement
 * Map, so that it becomes a part of Java's collection framework. Unlike the new
 * collection implementations, Hashtable is synchronized.
 * <p>
 *
 * The Iterators returned by the iterator and listIterator methods of the
 * Collections returned by all of Hashtable's "collection view methods" are
 * <em>fail-fast</em>: if the Hashtable is structurally modified at any time
 * after the Iterator is created, in any way except through the Iterator's own
 * remove or add methods, the Iterator will throw a
 * ConcurrentModificationException. Thus, in the face of concurrent
 * modification, the Iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the future.
 * The Enumerations returned by Hashtable's keys and values methods are
 * <em>not</em> fail-fast.
 *
 * <p>
 * Note that the fail-fast behavior of an iterator cannot be guaranteed as it
 * is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification. Fail-fast iterators throw
 * <tt>ConcurrentModificationException</tt> on a best-effort basis. Therefore,
 * it would be wrong to write a program that depended on this exception for its
 * correctness: <i>the fail-fast behavior of iterators should be used only to
 * detect bugs.</i>
 * <p>
 *
 * This class is a member of the <a href="{@docRoot}/../guide/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author Arthur van Hoff
 * @author Josh Bloch
 * @author Neal Gafter
 * @version 1.105, 12/19/03
 * @see Object#equals(java.lang.Object)
 * @see Object#hashCode()
 * @see Hashtable#rehash()
 * @see Collection
 * @see Map
 * @see HashMap
 * @see TreeMap
 * @since JDK1.0
 */
public class HashIntMap<V> extends AbstractIntMap<V> implements Cloneable {

   /**
    * The hash table data.
    */
   private transient Entry<V>[] table;

   /**
    * The total number of entries in the hash table.
    */
   private transient int count;

   /**
    * The table is rehashed when its size exceeds this threshold. (The value of
    * this field is (int)(capacity * loadFactor).)
    *
    * @serial
    */
   private int threshold;

   /**
    * The load factor for the hashtable.
    *
    * @serial
    */
   private float loadFactor;

   /**
    * The number of times this Hashtable has been structurally modified
    * Structural modifications are those that change the number of entries in
    * the Hashtable or otherwise modify its internal structure (e.g., rehash).
    * This field is used to make iterators on Collection-views of the Hashtable
    * fail-fast. (See ConcurrentModificationException).
    */
   private transient int modCount = 0;

   /** use serialVersionUID from JDK 1.0.2 for interoperability */
   private static final long serialVersionUID = 1421746759512286392L;

   /**
    * Constructs a new, empty hashtable with the specified initial capacity and
    * the specified load factor.
    *
    * @param initialCapacity
    *            the initial capacity of the hashtable.
    * @param loadFactor
    *            the load factor of the hashtable.
    * @exception IllegalArgumentException
    *                if the initial capacity is less than zero, or if the load
    *                factor is nonpositive.
    */
   
   public HashIntMap(int initialCapacity, float loadFactor) {
       if (initialCapacity < 0)
           throw new IllegalArgumentException("Illegal Capacity: "
                           + initialCapacity);
       if (loadFactor <= 0 || Float.isNaN(loadFactor))
           throw new IllegalArgumentException("Illegal Load: " + loadFactor);

       if (initialCapacity == 0)
           initialCapacity = 1;
       this.loadFactor = loadFactor;
       table = new Entry[initialCapacity];
       threshold = (int) (initialCapacity * loadFactor);
   }

   /**
    * Constructs a new, empty hashtable with the specified initial capacity and
    * default load factor, which is <tt>0.75</tt>.
    *
    * @param initialCapacity
    *            the initial capacity of the hashtable.
    * @exception IllegalArgumentException
    *                if the initial capacity is less than zero.
    */
   public HashIntMap(int initialCapacity) {
       this(initialCapacity, 0.75f);
   }

   /**
    * Constructs a new, empty hashtable with a default initial capacity (11)
    * and load factor, which is <tt>0.75</tt>.
    */
   public HashIntMap() {
       this(11, 0.75f);
   }

   /**
    * Constructs a new hashtable with the same mappings as the given Map. The
    * hashtable is created with an initial capacity sufficient to hold the
    * mappings in the given Map and a default load factor, which is
    * <tt>0.75</tt>.
    *
    * @param t
    *            the map whose mappings are to be placed in this map.
    * @throws NullPointerException
    *             if the specified map is null.
    * @since 1.2
    */
   public HashIntMap(IntMap<? extends V> t) {
       this(Math.max(2 * t.size(), 11), 0.75f);
       putAll(t);
   }

   /**
    * Returns the number of keys in this hashtable.
    *
    * @return the number of keys in this hashtable.
    */
   public int size() {
       return count;
   }

   /**
    * Tests if the specified object is a key in this hashtable.
    *
    * @param key
    *            possible key.
    * @return <code>true</code> if and only if the specified object is a key
    *         in this hashtable, as determined by the <tt>equals</tt> method;
    *         <code>false</code> otherwise.
    * @throws NullPointerException
    *             if the key is <code>null</code>.
    * @see #contains(Object)
    */
   public boolean containsKey(int key) {
       Entry<V> tab[] = table;
       int index = (key & 0x7FFFFFFF) % tab.length;
       for (Entry<V> e = tab[index]; e != null; e = e.next)
           if (e.key == key)
               return true;
       return false;
   }

   /**
    * Returns the value to which the specified key is mapped in this hashtable.
    *
    * @param key
    *            a key in the hashtable.
    * @return the value to which the key is mapped in this hashtable;
    *         <code>null</code> if the key is not mapped to any value in this
    *         hashtable.
    * @throws NullPointerException
    *             if the key is <code>null</code>.
    * @see #put(Object, Object)
    */
   public V get(int key) {
       Entry<V> tab[] = table;
       int index = (key & 0x7FFFFFFF) % tab.length;
       for (Entry<V> e = tab[index]; e != null; e = e.next)
           if (e.key == key)
               return e.value;
       return null;
   }

   /**
    * Increases the capacity of and internally reorganizes this hashtable, in
    * order to accommodate and access its entries more efficiently. This method
    * is called automatically when the number of keys in the hashtable exceeds
    * this hashtable's capacity and load factor.
    */
   
   protected void rehash() {
       int oldCapacity = table.length;
       Entry<V>[] oldMap = table;

       int newCapacity = oldCapacity * 2 + 1;
       Entry<V>[] newMap = new Entry[newCapacity];

       modCount++;
       threshold = (int) (newCapacity * loadFactor);
       table = newMap;

       for (int i = oldCapacity; i-- > 0;)
           for (Entry<V> old = oldMap[i]; old != null;) {
               Entry<V> e = old;
               old = old.next;

               int index = (e.key & 0x7FFFFFFF) % newCapacity;
               e.next = newMap[index];
               newMap[index] = e;
           }
   }

   /**
    * Maps the specified <code>key</code> to the specified <code>value</code>
    * in this hashtable. Neither the key nor the value can be <code>null</code>.
    * <p>
    *
    * The value can be retrieved by calling the <code>get</code> method with
    * a key that is equal to the original key.
    *
    * @param key
    *            the hashtable key.
    * @param value
    *            the value.
    * @return the previous value of the specified key in this hashtable, or
    *         <code>null</code> if it did not have one.
    * @exception NullPointerException
    *                if the key or value is <code>null</code>.
    * @see Object#equals(Object)
    * @see #get(Object)
    */
   public V put(int key, V value) {
       // Makes sure the key is not already in the hashtable.
       Entry<V> tab[] = table;
       int index = (key & 0x7FFFFFFF) % tab.length;
       for (Entry<V> e = tab[index]; e != null; e = e.next)
           if (e.key == key) {
               V old = e.value;
               e.value = value;
               return old;
           }

       modCount++;
       if (count >= threshold) {
           // Rehash the table if the threshold is exceeded
           rehash();

           tab = table;
           index = (key & 0x7FFFFFFF) % tab.length;
       }

       // Creates the new entry.
       Entry<V> e = tab[index];
       tab[index] = new Entry<V>(key, value, e);
       count++;
       return null;
   }

   /**
    * Removes the key (and its corresponding value) from this hashtable. This
    * method does nothing if the key is not in the hashtable.
    *
    * @param key
    *            the key that needs to be removed.
    * @return the value to which the key had been mapped in this hashtable, or
    *         <code>null</code> if the key did not have a mapping.
    * @throws NullPointerException
    *             if the key is <code>null</code>.
    */
   public V remove(int key) {
       Entry<V> tab[] = table;
       int index = (key & 0x7FFFFFFF) % tab.length;
       for (Entry<V> e = tab[index], prev = null; e != null;
       			prev = e, e = e.next) {
           if (e.key == key) {
               modCount++;
               if (prev != null) {
                       prev.next = e.next;
               } else {
                       tab[index] = e.next;
               }
               count--;
               V oldValue = e.value;
               e.value = null;
               return oldValue;
           }
       }
       return null;
   }

   /**
    * Clears this hashtable so that it contains no keys.
    */
   public void clear() {
       Entry tab[] = table;
       modCount++;
       for (int index = tab.length; --index >= 0;)
           tab[index] = null;
       count = 0;
   }

   /**
    * Creates a shallow copy of this hashtable. All the structure of the
    * hashtable itself is copied, but the keys and values are not cloned. This
    * is a relatively expensive operation.
    *
    * @return a clone of the hashtable.
    */
   
   public Object clone() {
       try {
           HashIntMap<V> t = (HashIntMap<V>) super.clone();
           t.table = new Entry[table.length];
           for (int i = table.length; i-- > 0;)
               t.table[i] = (table[i] != null) ? (Entry<V>) table[i].clone()
                       : null;
           t.keySet = null;
           t.values = null;
           t.modCount = 0;
           return t;
       } catch (CloneNotSupportedException e) {
           // this shouldn't happen, since we are Cloneable
           throw new InternalError();
       }
   }

   // Views

   /**
    * Each of these fields are initialized to contain an instance of the
    * appropriate view the first time this view is requested. The views are
    * stateless, so there's no reason to create more than one of each.
    */
   private transient volatile Set<IntMap.Entry<V>> mEntries = null;
   private transient volatile IntSet keySet = null;
   private transient volatile Collection<V> values = null;

   /**
    * Returns a Set view of the keys contained in this Hashtable. The Set is
    * backed by the Hashtable, so changes to the Hashtable are reflected in the
    * Set, and vice-versa. The Set supports element removal (which removes the
    * corresponding entry from the Hashtable), but not element addition.
    *
    * @return a set view of the keys contained in this map.
    * @since 1.2
    */
   public IntSet keySet() {
       if (keySet == null)
           keySet = super.keySet();
       return keySet;
   }

   /**
    * Returns a Set view of the entries contained in this Hashtable. Each
    * element in this collection is a Map.Entry. The Set is backed by the
    * Hashtable, so changes to the Hashtable are reflected in the Set, and
    * vice-versa. The Set supports element removal (which removes the
    * corresponding entry from the Hashtable), but not element addition.
    *
    * @return a set view of the mappings contained in this map.
    * @see Map.Entry
    * @since 1.2
    */
   public Set<? extends IntMap.Entry<V>> intEntrySet() {
       if (mEntries == null)
    	   mEntries = new EntrySet();
       return mEntries;
   }
   
   protected class EntrySet extends AbstractSet<IntMap.Entry<V>> {
	   public int size() {return count;}
	   
	   public Iterator<IntMap.Entry<V>> iterator() {
           return new Iterator<IntMap.Entry<V>>() {
               int expectedModCount = modCount;

               Entry<V> current = null, next = null;

               public boolean hasNext() {
                   return getNext();
               }

               public Entry<V> next() {
                   if (!getNext())
                       throw new ConcurrentModificationException();
                   current = next;
                   next = null;
                   return current;
               }

               public void remove() {
                   if (modCount >= 0 && modCount != expectedModCount)
                       throw new ConcurrentModificationException();
                   if (current == null)
                       throw new IllegalStateException();
                   getNext();
                   int index = (current.key & 0x7FFFFFFF) % table.length;
                   Entry<V> prev = table[index];
                   if (prev == current)
                       table[index] = current.next;
                   else {
                       while (prev.next != current)
                           prev = prev.next;
                       prev.next = current.next;
                   }
                   current.value = null;
                   current = null;
                   expectedModCount = ++modCount;
                   count--;
               }

               boolean getNext() {
                   if (expectedModCount < 0)
                       return false;
                   if (expectedModCount != modCount)
                       throw new ConcurrentModificationException();
                   if (next != null)
                       return true;
                   if (current != null && current.next != null) {
                       next = current.next;
                       return true;
                   }
                   int index = current == null ? -1 : (current.key & 0x7FFFFFFF)
                           % table.length;
                   while (++index != table.length)
                       if (table[index] != null) {
                           next = table[index];
                           return true;
                       }
                   expectedModCount = -1;
                   return false;
               }
           };
	   }
	
	   public boolean contains(IntMap.Entry<V> entry) {
	       int key = entry.getKey();
	       Entry<V>[] tab = table;
	       int index = (key & 0x7FFFFFFF) % tab.length;
	
	       for (Entry<V> e = tab[index]; e != null; e = e.next)
	           if (e.key == key && (e.value == null ? entry.getValue() == null
	        		   : e.value.equals(entry.getValue())))
	               return true;
	       return false;
	   }
	
	   public boolean remove(IntMap.Entry<V> entry) {
           int key = entry.getKey();
           Entry<V>[] tab = table;
           int index = (key & 0x7FFFFFFF) % tab.length;

           for (Entry<V> e = tab[index], prev = null; e != null;
           			prev = e, e = e.next)
               if (e.key == key && (e.value == null ? entry.getValue() == null
            		   : e.value.equals(entry.getValue()))) {
                       modCount++;
                   if (prev != null)
                       prev.next = e.next;
                   else
                       tab[index] = e.next;

                   count--;
                   e.value = null;
                   return true;
               }
           return false;
	   }
   }

   /**
    * Returns a Collection view of the values contained in this Hashtable. The
    * Collection is backed by the Hashtable, so changes to the Hashtable are
    * reflected in the Collection, and vice-versa. The Collection supports
    * element removal (which removes the corresponding entry from the
    * Hashtable), but not element addition.
    *
    * @return a collection view of the values contained in this map.
    * @since 1.2
    */
   public Collection<V> values() {
       if (values == null)
           values = super.values();
       return values;
   }

   /**
    * Hashtable collision list.
    */
   private static class Entry<V> extends AbstractIntMap.Entry<V>
   			implements Cloneable {
       int key;
       V value;
       Entry<V> next;

       protected Entry(int key, V value, Entry<V> next) {
           this.key = key;
           this.value = value;
           this.next = next;
       }

       protected Object clone() {
           return new Entry<V>(key, value, (next == null ? null
                   : (Entry<V>) next.clone()));
       }

       // Map.Entry Ops

       public int getIntKey() {return key;}

       public V getValue() {return value;}

       public V setValue(V value) {
           if (value == null)
               throw new NullPointerException();

           V oldValue = this.value;
           this.value = value;
           return oldValue;
       }
   }
}