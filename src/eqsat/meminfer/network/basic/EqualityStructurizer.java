package eqsat.meminfer.network.basic;

import java.util.HashSet;
import java.util.Set;

import eqsat.meminfer.network.basic.StructureNetwork.StructureNode;
import util.Taggable;
import util.graph.OrderedVertex;
import util.pair.Couple;

public abstract class EqualityStructurizer
		<V extends Taggable & OrderedVertex<?,? extends V>>
		extends Structurizer<V> {
	private final Set<Couple<V>> mEqualities = new HashSet();
	
	public void makeEqual(V left, V right) {
		if (!left.equals(right))
			mEqualities.add(new Couple<V>(left, right));
	}

	public StructureNode getStructure() {
		StructureNode structure = super.getStructure();
		for (Couple<V> equality : mEqualities)
			structure = getNetwork().checkEquality(getValue(equality.getLeft()),
					getValue(equality.getRight()), structure);
		return structure;
	}
}
