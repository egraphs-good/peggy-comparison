package peggy.analysis.java;

import peggy.analysis.Lattice;

/**
 * This is a lattice of ClassMaps.
 */
public class ClassMapLattice<K> implements Lattice<ClassMap<K>>{
	public ClassMap<K> top() {
		return ClassMap.<K>getAllMap();
	}
	public ClassMap<K> bottom() {
		return ClassMap.<K>getEmptyMap();
	}
	public ClassMap<K> lub(ClassMap<K> left, ClassMap<K> right) {
		return left.union(right);
	}
	public ClassMap<K> glb(ClassMap<K> left, ClassMap<K> right) {
		return left.intersection(right);
	}
	public boolean isLower(ClassMap<K> lower, ClassMap<K> higher) {
		return lower.isSubMap(higher);
	}
}
