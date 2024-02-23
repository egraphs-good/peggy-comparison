package peggy.analysis.java;

import java.util.*;

/**
 * This class associates keys with classsets.
 */
public abstract class ClassMap<K> {
	private static final int THRESHOLD = 10;
	
	private Set<K> explicitKeys_cache;

	protected abstract int getWrappingDepth();
	protected abstract ClassSet defaultValue();
	protected abstract Set<K> computeExplicitKeys();
	protected final Set<K> explicitKeys() {
		if (this.explicitKeys_cache == null) {
			this.explicitKeys_cache = Collections.unmodifiableSet(this.computeExplicitKeys());
		}
		return this.explicitKeys_cache;
	}

	
	public final String toString() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append("[default=");
		buffer.append(this.defaultValue().toString());
		for (K key : this.explicitKeys()) {
			buffer.append(", ");
			buffer.append(key.toString());
			buffer.append("=");
			buffer.append(this.getClassSet(key).toString());
		}
		buffer.append(']');
		return buffer.toString();
	}
	
	
	/**
	 * Returns true iff for all keys K,
	 * this.getClassSet(K) is a subset of lcm.getClassSet(K)
	 */
	public final boolean isSubMap(ClassMap<K> cm) {
		if (!this.defaultValue().isSubSet(cm.defaultValue()))
			return false;
		for (K k : this.explicitKeys()) {
			if (!this.getClassSet(k).isSubSet(cm.getClassSet(k)))
				return false;
		}
		return true;
	}

	/** 
	 * Returns the ClassSet associated with the given key 
	 * in this map. 
	 */
	public abstract ClassSet getClassSet(K key);

	
	/**
	 * Returns a new ClassMap that is the result of taking this
	 * map and changing the entry for 'key' to be 'set'.
	 */
	public final ClassMap<K> updateClassSet(K key, ClassSet set) {
		ClassMap<K> result = new UpdateClassMap<K>(this, key, set);
		if (result.getWrappingDepth() > THRESHOLD)
			return new CompressedClassMap<K>(result);
		else
			return result;
	}
	
	/**
	 * Returns a new ClassMap that is the result of 
	 * unioning the ClassSets for each local in the two maps.
	 * i.e. for each key k, the mapped value in the result
	 * will be this.getClassSet(k).union(cm.getClassSet(k))
	 */
	public final ClassMap<K> union(ClassMap<K> cm) {
		ClassMap<K> result = new UnionClassMap<K>(this, cm);
		if (result.getWrappingDepth() > THRESHOLD)
			return new CompressedClassMap<K>(result);
		else
			return result;
	}
	
	/**
	 * Returns a new LocalClassMap that is the result of 
	 * intersecting the ClassSets for each local in the two maps.
	 * i.e. for each local k, the mapped value in the result
	 * will be this.getClassSet(k).intersect(lcm.getClassSet(k))
	 */
	public final ClassMap<K> intersection(ClassMap<K> lcm) {
		ClassMap<K> result = new IntersectionClassMap<K>(this, lcm);
		if (result.getWrappingDepth() > THRESHOLD)
			return new CompressedClassMap<K>(result);
		else
			return result;
	}
	
	/**
	 * Returns the LocalClassMap mapping all locals
	 * to the empty ClassSet.
	 */
	public static <K> ClassMap<K> getEmptyMap() {
		if (EMPTY_MAP == null) {
			EMPTY_MAP = new EmptyClassMap<K>();
		}
		return EMPTY_MAP;
	}
	private static ClassMap EMPTY_MAP;
	

	private static class EmptyClassMap<K> extends ClassMap<K> {
		protected Set<K> computeExplicitKeys() {
			return new HashSet<K>();
		}
		public ClassSet getClassSet(K key) {
			return ClassSet.EMPTY_CLASSES;
		}
		protected int getWrappingDepth() {return 0;}
		protected ClassSet defaultValue() {return ClassSet.EMPTY_CLASSES;}
	}


	/**
	 * Returns the LocalClassMap mapping all
	 * locals to the ClassSet of all classes.
	 */
	public static <K> ClassMap<K> getAllMap() {
		if (ALL_MAP == null) {
			ALL_MAP = new AllClassMap<K>();
		}
		return ALL_MAP;
	}
	private static ClassMap ALL_MAP;
	
	private static class AllClassMap<K> extends ClassMap<K> {
		protected Set<K> computeExplicitKeys() {
			return new HashSet<K>();
		}
		public ClassSet getClassSet(K key) {
			return ClassSet.ALL_CLASSES;
		}
		protected int getWrappingDepth() {return 0;}
		protected ClassSet defaultValue() {return ClassSet.ALL_CLASSES;}
	}
	
	private static class CompressedClassMap<K> extends ClassMap<K> {
		private final Map<K,ClassSet> map;
		private ClassSet defaultValue;
		
		public CompressedClassMap(ClassMap<K> cm) {
			this.map = new HashMap<K,ClassSet>();
			for (K key : cm.explicitKeys()) {
				this.map.put(key, cm.getClassSet(key));
			}
			this.defaultValue = cm.defaultValue();
		}
		protected Set<K> computeExplicitKeys() {
			return this.map.keySet();
		}
		public ClassSet getClassSet(K key) {
			if (this.map.containsKey(key))
				return this.map.get(key);
			else
				return ClassSet.EMPTY_CLASSES;
		}
		protected int getWrappingDepth() {return 0;}
		protected ClassSet defaultValue() {return this.defaultValue;}
	}
	
	private static class UpdateClassMap<K> extends ClassMap<K> {
		protected final int wrappingDepth;
		protected final K key;
		protected final ClassSet value;
		protected final ClassMap<K> inner;
		
		protected UpdateClassMap(ClassMap<K> _inner, K _key, ClassSet _value) {
			this.inner = _inner;
			this.key = _key;
			this.value = _value;
			this.wrappingDepth = this.inner.getWrappingDepth() + 1;
		}
		protected Set<K> computeExplicitKeys() {
			Set<K> result = new HashSet<K>(this.inner.explicitKeys());
			if (this.value.isEmpty())
				result.remove(this.key);
			else
				result.add(this.key);
			return result;
		}
		public ClassSet getClassSet(K key) {
			if (this.key.equals(key))
				return this.value;
			else
				return this.inner.getClassSet(key);
		}
		protected int getWrappingDepth() {return this.wrappingDepth;}
		protected ClassSet defaultValue() {return ClassSet.EMPTY_CLASSES;}
	}
	
	private static class UnionClassMap<K> extends ClassMap<K> {
		protected final ClassMap<K> left, right;
		protected final int wrappingDepth;
		
		protected UnionClassMap(ClassMap<K> _left, ClassMap<K> _right) {
			this.left = _left;
			this.right = _right;
			this.wrappingDepth = Math.max(this.left.getWrappingDepth(), this.right.getWrappingDepth()) + 1;
		}
		protected Set<K> computeExplicitKeys() {
			Set<K> result = new HashSet<K>(this.left.explicitKeys());
			result.addAll(this.right.explicitKeys());
			return result;
		}
		public ClassSet getClassSet(K key) {
			return this.left.getClassSet(key).union(this.right.getClassSet(key));
		}
		protected int getWrappingDepth() {return this.wrappingDepth;}
		protected ClassSet defaultValue() {
			return this.left.defaultValue().union(this.right.defaultValue());
		}
	}
	
	protected static class IntersectionClassMap<K> extends ClassMap<K> {
		protected final ClassMap<K> left, right;
		protected final int wrappingDepth;
		
		protected IntersectionClassMap(ClassMap<K> _left, ClassMap<K> _right) {
			this.left = _left;
			this.right = _right;
			this.wrappingDepth = Math.max(this.left.getWrappingDepth(), this.right.getWrappingDepth()) + 1;
		}
		protected Set<K> computeExplicitKeys() {
			Set<K> result = new HashSet<K>(this.left.explicitKeys());
			result.addAll(this.right.explicitKeys());
			return result;
		}
		public ClassSet getClassSet(K key) {
			return this.left.getClassSet(key).intersection(this.right.getClassSet(key));
		}
		protected int getWrappingDepth() {return this.wrappingDepth;}
		protected ClassSet defaultValue() {
			return this.left.defaultValue().intersection(this.right.defaultValue());
		}
	}
}
