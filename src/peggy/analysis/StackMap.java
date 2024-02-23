package peggy.analysis;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * This is a new collection class that is a map but with a stack of edits.
 * each new entry into the map is pushed, and can be popped later. Query 
 * operations behave as normal.
 */
public class StackMap<K,V> {
	private class UpdateInfo {
		boolean contained;
		K key;
		V oldvalue;
	}
	
	protected final Map<K,V> map;
	protected final Stack<UpdateInfo> updates;
	
	public StackMap() {
		this.map = new HashMap<K,V>();
		this.updates = new Stack<UpdateInfo>();
	}

	public void clear() {
		this.map.clear();
		this.updates.clear();
	}
	
	public V push(K key, V value) {
		UpdateInfo info = new UpdateInfo();
		info.key = key;
		info.contained = this.map.containsKey(key);
		info.oldvalue = this.map.get(key); // may be null
		
		this.map.put(key, value);
		this.updates.push(info);
		return info.oldvalue;
	}
	public void pop() {
		UpdateInfo info = this.updates.pop();
		if (info.contained) {
			this.map.put(info.key, info.oldvalue);
		} else {
			this.map.remove(info.key);
		}
	}
	
	public void popToHeight(int height) {
		if (height < 0)
			throw new IllegalArgumentException("height cannot be negative: " + height);
		if (height > getHeight())
			throw new IllegalArgumentException("parameter is greater than current height: " + height); 
		while (getHeight() > height) {
			pop();
		}
	}
	
	public int getHeight() {return this.updates.size();}
	
	public boolean containsKey(K key) {
		return this.map.containsKey(key);
	}
	public boolean containsValue(V value) {
		return this.map.containsKey(value);
	}
	public Set<? extends K> keySet() {
		return Collections.unmodifiableSet(this.map.keySet());
	}
	public Collection<? extends V> values() {
		return Collections.unmodifiableCollection(this.map.values());
	}
	public Set<? extends Map.Entry<K,V>> entrySet() {
		return Collections.unmodifiableSet(this.map.entrySet());
	}
	public V get(K key) {return this.map.get(key);}
	public int size() {return this.map.size();}
	public boolean isEmpty() {return this.map.isEmpty();}
	
	public String toString() {
		return this.map.toString();
	}
}
