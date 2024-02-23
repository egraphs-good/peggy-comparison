package eqsat.meminfer.network.basic.axiom;

import java.util.HashSet;
import java.util.Set;

import eqsat.meminfer.network.Network.ListNode;
import eqsat.meminfer.network.basic.axiom.MergeNetwork.MergeNode;
import eqsat.meminfer.network.op.axiom.ConstructMaker;
import util.Taggable;
import util.graph.OrderedVertex;
import util.pair.Couple;

public abstract class MergeMaker
		<V extends Taggable & OrderedVertex<?,? extends V>>
		extends ConstructMaker<V> {
	private final Set<Couple<V>> mMerges = new HashSet<Couple<V>>();
	
	public abstract MergeNetwork getNetwork();
	
	public final void makeEqual(V vertex, V original) {
		if (vertex.equals(original))
			throw new IllegalArgumentException("Inputs are already equal.");
		mMerges.add(new Couple<V>(vertex, original));
	}
	
	public ListNode<? extends MergeNode> getMerges() {
		ListNode<? extends MergeNode> merges = getNetwork().empty();
		for (Couple<V> merge : mMerges)
			merges = getNetwork().postpend(merges,
					getNetwork().merge(getValue(merge.getLeft()),
					getValue(merge.getRight())));
		return merges;
	}
}
