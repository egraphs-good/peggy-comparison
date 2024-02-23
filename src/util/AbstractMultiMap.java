package util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AbstractMultiMap<K, V> implements MultiMap<K, V> {
   public int numKeys() {return keySet().size();}
   
   public int numEntries() {return entries().size();}

   public boolean isEmpty() {return keySet().isEmpty();}

   public boolean containsKey(K key) {return keySet().contains(key);}

   public boolean containsValue(V value) {
   	for (Set<V> valueSet : valueSets())
   		if (valueSet.contains(value))
   			return true;
   	return false;
   }

   public boolean containsEntry(K key, V value) {
	   Set<V> values = get(key);
	   return values != null && values.contains(value);
   }

   public boolean containsValueSet(Set<? extends V> valueSet) {
   	return valueSets().contains(valueSet);
   }

   public Set<V> addValue(K key, V value) {
	   Set<V> values = get(key);
	   values.add(value);
	   return values;
   }

   public Set<V> addValues(K key, Collection<? extends V> values) {
	   for (V value : values)
		   addValue(key, value);
	   return get(key);
   }

   public Set<V> putValue(K key, V value) {
   	return putValues(key, Collections.singleton(value));
   }

    public Set<V> removeEntry(K key, V value) {
		Set<V> valueSet = get(key);
		if (valueSet != null)
			valueSet.remove(value);
		return valueSet;
    }

   public Set<V> removeValues(K key, Collection<? extends V> values) {
   	Set<V> valueSet = get(key);
   	if (valueSet != null)
   		valueSet.removeAll(values);
   	return valueSet;
   }

   public boolean removeValue(V value) {
	   boolean removed = false;
	   for (Set<V> values : valueSets())
	   		removed |= values.remove(value);
	   return removed;
   }

   public boolean removeValues(Collection<? extends V> values) {
	   boolean removed = false;
	   for (Set<V> valueSet : valueSets())
	   		removed |= valueSet.removeAll(values);
	   return removed;
   }

   public void putAll(MultiMap<? extends K, ? extends V> t) {
   	for (Entry<? extends K,? extends V> entry : t.entrySet())
   		putValues(entry.getKey(), entry.getValues());
   }

   public void putEach(Map<? extends K, ? extends V> t) {
   	for (Map.Entry<? extends K, ? extends V> entry : t.entrySet())
   		putValue(entry.getKey(), entry.getValue());
   }

   public void putAll(Map<? extends K, ? extends Collection<? extends V>> t) {
   	for (Map.Entry<? extends K, ? extends Collection<? extends V>> entry
   	 : t.entrySet())
   		putValues(entry.getKey(), entry.getValue());
   }

   public void addAll(MultiMap<? extends K, ? extends V> t) {
   	for (Entry<? extends K,? extends V> entry : t.entrySet())
   		addValues(entry.getKey(), entry.getValues());
   }

   public void addEach(Map<? extends K, ? extends V> t) {
   	for (Map.Entry<? extends K, ? extends V> entry : t.entrySet())
   		addValue(entry.getKey(), entry.getValue());
   }

   public void addAll(Map<? extends K, ? extends Collection<? extends V>> t) {
   	for (Map.Entry<? extends K, ? extends Collection<? extends V>> entry
   	 : t.entrySet())
   		addValues(entry.getKey(), entry.getValue());
   }

   public void clear() {keySet().clear();}

   public Iterable<V> values() {
	   return new Iterable<V>() {
		   public Iterator<V> iterator() {
			   return new Iterator<V>() {
				   private boolean canRemove = false;
				   private Set<V> set = null;
				   private Iterator<? extends Set<V>> sets
						   = valueSets().iterator();
				   private Iterator<V> values = null;

				   public boolean hasNext() {
					    canRemove = false;
					    if (values != null && values.hasNext())
					   		return true;
					   	while (sets.hasNext()) {
							set = sets.next();
							if (!set.isEmpty()) {
								values = set.iterator();
								return true;
							}
						}
					   	set = null;
						return false;
				   }

				    public V next() {
					    if (hasNext()) {
					    	canRemove = true;
					   		return values.next();
						}
					   	sets.next();
					   	return null;
					}

					public void remove() {
						if (!canRemove)
							throw new IllegalStateException();
						if (set.size() == 1)
							sets.remove();
						values.remove();
					}
				};
			}
		};
	}

   public boolean equals(Object that) {
   	return that instanceof MultiMap && equals((MultiMap)that);
   }

   public boolean equals(MultiMap that) {
   	if (that == null || numKeys() != that.numKeys())
   		return false;

	for (Entry entry : entrySet()) {
		Set values = that.get(entry.getKey());
		if (values == null || !values.equals(entry.getValues()))
			return false;
	}

   	return true;
   }

   public int hashCode() {return keySet().hashCode() ^ valueSets().hashCode();}

   public String toString() {
   	StringBuilder output = new StringBuilder("{");
   	boolean firstKey = true;
   	for (Entry<K,V> entry : entrySet()) {
   		if (firstKey)
   			firstKey = false;
   		else
	   		output.append("; ");
   		output.append(entry.getKey());
   		output.append(" -> ");
   		output.append(entry.getValues());
   	}
   	output.append('}');
   	return output.toString();
   }
}