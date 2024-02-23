package llvm.bitcode;

import java.util.*;

/**
 * This is an ordered HashSet. For any value in the set,
 * you can ask for its 0-based position. Similarly, for any index
 * you can ask for the value at that index.
 * It's like a list of values crossed with a map from value to index
 * (which is exactly how it's implemented)
 * 
 * This structure does not support removal.
 * 
 * @author steppm
 */
public class HashList<V> implements Iterable<V> {
	protected final List<V> index2value;
	protected final Map<V,Integer> value2index;
	
	public HashList() {
		this.index2value = new ArrayList<V>();
		this.value2index = new HashMap<V,Integer>();
	}
	public HashList(HashList<? extends V> other) {
		this.index2value = new ArrayList<V>(other.index2value);
		this.value2index = new HashMap<V,Integer>(other.value2index);
	}
	
	public int size() {return this.index2value.size();}
	
	public boolean hasValue(V value) {
		return this.value2index.containsKey(value);
	}
	
	public int add(V value) {
		if (this.hasValue(value))
			return this.getIndex(value);

		int index = this.index2value.size();
		this.index2value.add(value);
		this.value2index.put(value, index);
		return index;
	}
	public void addAll(Iterable<? extends V> coll) {
		for (V v : coll) {
			this.add(v);
		}
	}
	
	public int getIndex(V value) {
		if (!this.value2index.containsKey(value))
			throw new NoSuchElementException("Value is not in list: " + value.toString());
		return this.value2index.get(value);
	}
	
	public V getValue(int index) {
		return this.index2value.get(index);
	}
	
	/**
	 * This iterator does not support removal.
	 */
	public Iterator<V> iterator() {
		return Collections.unmodifiableList(this.index2value).iterator();
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append("[");
		for (int i = 0; i < this.size(); i++) {
			if (i > 0) buffer.append(", ");
			V v = this.getValue(i);
			buffer.append(i).append(":").append(v);
		}
		buffer.append("]");
		return buffer.toString();
	}
}
