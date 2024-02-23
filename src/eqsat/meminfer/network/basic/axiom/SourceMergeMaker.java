package eqsat.meminfer.network.basic.axiom;

import java.util.HashMap;
import java.util.Map;

import eqsat.meminfer.network.Network.ListNode;
import eqsat.meminfer.network.basic.axiom.MergeNetwork.MergeNode;
import util.Taggable;
import util.graph.OrderedVertex;

public abstract class SourceMergeMaker
		<V extends Taggable & OrderedVertex<?,? extends V>>
		extends MergeMaker<V> {
	private final Map<V,Boolean> mMerges = new HashMap<V,Boolean>();
	
	public final void makeInferred(V vertex) {
		if (mMerges.put(vertex, true) != null)
			throw new IllegalStateException();
		constructTrue();
	}
	
	public final void makeInconsistent(V vertex) {
		if (mMerges.put(vertex, false) != null)
			throw new IllegalStateException();
		constructFalse();
	}
	
	public ListNode<? extends MergeNode> getMerges() {
		ListNode<? extends MergeNode> merges = super.getMerges();
		for (Map.Entry<V,Boolean> merge : mMerges.entrySet())
			merges = getNetwork().postpend(merges,
					getNetwork().merge(getValue(merge.getKey()),
					getNetwork().constructValue(merge.getValue()
							? constructTrue() : constructFalse())));
		return merges;
	}
}
