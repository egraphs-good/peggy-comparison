package util.integer;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractIntMap<V> implements IntMap<V> {
    /**
     * Tests if this hashtable maps no keys to values.
     *
     * @return <code>true</code> if this hashtable maps no keys to values;
     *         <code>false</code> otherwise.
     */
    public boolean isEmpty() {return size() == 0;}
    
    public V get(Object element) {
    	return element == null || !(element instanceof Integer) ? null
    			: get(((Integer)element).intValue());
    }
    
    public boolean containsKey(Object element) {
    	return element != null && element instanceof Integer
    			&& containsKey(((Integer)element).intValue());
    }
    
    public V put(Integer element, V value) {
    	if (element == null)
    		throw new IllegalArgumentException();
    	return put(element.intValue(), value);
    }
    
    public V remove(Object element) {
    	return element == null || !(element instanceof Integer) ? null
    			: remove(((Integer)element).intValue());
    }

    /**
     * Returns true if this Hashtable maps one or more keys to this value.
     * <p>
     *
     * Note that this method is identical in functionality to contains (which
     * predates the Map interface).
     *
     * @param value
     *            value whose presence in this Hashtable is to be tested.
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value.
     * @throws NullPointerException
     *             if the value is <code>null</code>.
     * @see Map
     * @since 1.2
     */
    public boolean containsValue(Object value) {
    	for (IntMap.Entry<V> entry : intEntrySet())
    		if (value == null ? entry.getValue() == null
    				: value.equals(entry.getValue()))
    			return true;
    	return false;
    }

    /**
     * Copies all of the mappings from the specified Map to this Hashtable These
     * mappings will replace any mappings that this Hashtable had for any of the
     * keys currently in the specified Map.
     *
     * @param t
     *            Mappings to be stored in this map.
     * @throws NullPointerException
     *             if the specified map is null.
     * @since 1.2
     */
    public void putAll(IntMap<? extends V> that) {
        for (IntMap.Entry<? extends V> entry : that.intEntrySet())
            put(entry.getIntKey(), entry.getValue());
    }

    /**
     * Copies all of the mappings from the specified Map to this Hashtable These
     * mappings will replace any mappings that this Hashtable had for any of the
     * keys currently in the specified Map.
     *
     * @param t
     *            Mappings to be stored in this map.
     * @throws NullPointerException
     *             if the specified map is null.
     * @since 1.2
     */
    public void putAll(Map<? extends Integer, ? extends V> that) {
        for (Map.Entry<? extends Integer, ? extends V> entry : that.entrySet())
            put(entry.getKey(), entry.getValue());
    }
    
    public void clear() {
    	for (IntIterator keys = keySet().iterator(); keys.hasNext(); ) {
    		keys.next();
    		keys.remove();
    	}
    }

    /**
     * Returns a string representation of this <tt>Hashtable</tt> object in
     * the form of a set of entries, enclosed in braces and separated by the
     * ASCII characters "<tt>,&nbsp;</tt>" (comma and space). Each entry is
     * rendered as the key, an equals sign <tt>=</tt>, and the associated
     * element, where the <tt>toString</tt> method is used to convert the key
     * and element to strings.
     * <p>
     * Overrides to <tt>toString</tt> method of <tt>Object</tt>.
     *
     * @return a string representation of this hashtable.
     */
    public String toString() {
        int max = size() - 1;
        StringBuffer buf = new StringBuffer();
        Iterator<? extends IntMap.Entry<V>> it = intEntrySet().iterator();

        buf.append("{");
        for (int i = 0; i <= max; i++) {
            IntMap.Entry<V> e = it.next();
            int key = e.getIntKey();
            V value = e.getValue();
            buf.append(key + "="
         		   + (value == this ? "(this Map)" : ("" + value)));

            if (i < max)
                buf.append(", ");
        }
        buf.append("}");
        return buf.toString();
    }

    /**
     * Compares the specified Object with this Map for equality, as per the
     * definition in the Map interface.
     *
     * @param o
     *            object to be compared for equality with this Hashtable
     * @return true if the specified Object is equal to this Map.
     * @see Map#equals(Object)
     * @since 1.2
     */
    
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o == null)
        	return false;
        if (o instanceof IntMap)
        	return equals((IntMap)o);
        if (o instanceof Map)
        	return equals((Map)o);
        return false;
    }
    
    public boolean equals(IntMap that) {
    	return this == that ||
    			that != null && intEntrySet().containsAll(that.intEntrySet());
    }
    
    public boolean equals(Map that) {
    	return this == that
    			|| that != null && intEntrySet().containsAll(that.entrySet());
    }

    /**
     * Returns the hash code value for this Map as per the definition in the Map
     * interface.
     *
     * @see Map#hashCode()
     * @since 1.2
     */
    public int hashCode() {return intEntrySet().hashCode();}
    
	public Set<Map.Entry<Integer,V>> entrySet() {
    	return (Set<Map.Entry<Integer,V>>)intEntrySet(); 
    }
    
    public IntSet keySet() {return new KeySet();}
    
    public Collection<V> values() {return new Values();}
    
    public static abstract class Entry<V> implements IntMap.Entry<V> {
    	public Integer getKey() {return getIntKey();}

        public boolean equals(Object that) {
            if (!(that instanceof Map.Entry))
                return false;
            Map.Entry entry = (Map.Entry)that;
            Object key = entry.getKey();
            
            if (key == null || !(key instanceof Integer))
         	   return false;

            return getIntKey() == ((Integer)key).intValue()
            		&& (getValue() == null ? entry.getValue() == null
            		: getValue().equals(entry.getValue()));
        }
    	
    	public int hashCode() {
    		return getIntKey()
    				^ (getValue() == null ? 0 : getValue().hashCode());
    	}

        public String toString() {
            return getIntKey() + "=" + getValue().toString();
        }
    }
    
    public class KeySet extends AbstractIntSet {
        public IntIterator iterator() {
            return new AbstractIntIterator() {
            	private final Iterator<? extends IntMap.Entry<V>> mIterator
            			= intEntrySet().iterator();

                public boolean hasNext() {return mIterator.hasNext();}

                public int nextInt() {return mIterator.next().getIntKey();}

                public void remove() {mIterator.remove();}
            };
        }
    	
 	   public int size() {return AbstractIntMap.this.size();}
 	
 	   public boolean contains(int o) {return containsKey(o);}
 	
 	   public boolean remove(int o) {
 		   return AbstractIntMap.this.remove(o) != null;
 	   }
 	
 	   public void clear() {AbstractIntMap.this.clear();}
    }
    
    public class Values extends AbstractCollection<V> {
        public Iterator<V> iterator() {
            return new Iterator<V>() {
            	private final Iterator<? extends IntMap.Entry<V>> mIterator
            			= intEntrySet().iterator();

                public boolean hasNext() {return mIterator.hasNext();}

                public V next() {return mIterator.next().getValue();}

                public void remove() {mIterator.remove();}
            };
        }
    	
 	   public int size() {return AbstractIntMap.this.size();}
 	
 	   public boolean contains(int o) {return containsKey(o);}
 	
 	   public boolean remove(int o) {
 		   return AbstractIntMap.this.remove(o) != null;
 	   }
 	
 	   public void clear() {AbstractIntMap.this.clear();}
    }
}
