package eqsat.meminfer.network.op.axiom;

import java.util.ArrayList;
import java.util.List;

import eqsat.meminfer.network.Network.ListNode;
import eqsat.meminfer.network.basic.Structurizer;
import eqsat.meminfer.network.op.axiom.ConstructNetwork.ConstructNode;
import eqsat.meminfer.network.op.axiom.ConstructNetwork.ExtendedValueNode;
import eqsat.meminfer.network.op.axiom.ConstructNetwork.OpNode;
import eqsat.meminfer.network.op.axiom.ConstructNetwork.ValueSourceNode;
import util.NamedTag;
import util.Tag;
import util.Taggable;
import util.graph.OrderedVertex;

public abstract class ConstructMaker
		<V extends Taggable & OrderedVertex<?,? extends V>> {
	private int mPlaceHolders = 0;
	private int mTrueConstruct = -1, mFalseConstruct = -1;
	private final List<ConstructNode> mConstructs = new ArrayList();
	private final Tag<ExtendedValueNode> mValueTag = new NamedTag("Value");
	private final Tag<ValueSourceNode> mPlaceHolderTag
			= new NamedTag("Place Holder");
	
	public abstract ConstructNetwork getNetwork();
	public abstract Structurizer<V> getStructurizer();
	protected abstract OpNode getOpNode(V vertex);

	protected final ExtendedValueNode getValue(V vertex) {
		return vertex.getTag(mValueTag);
	}
	
	protected final int constructTrue() {
		if (mTrueConstruct < 0) {
			mTrueConstruct = mConstructs.size();
			mConstructs.add(getNetwork().constructTrue());
		}
		return mTrueConstruct;
	}
	protected final int constructFalse() {
		if (mFalseConstruct < 0) {
			mFalseConstruct = mConstructs.size();
			mConstructs.add(getNetwork().constructFalse());
		}
		return mFalseConstruct;
	}
	
	private final ExtendedValueNode determineValue(V vertex) {
		if (vertex.hasTag(mValueTag)) {
			if (vertex.getTag(mValueTag) == null) {
				vertex.setTag(mPlaceHolderTag,
						getNetwork().placeHolderValueSource(mPlaceHolders));
				vertex.setTag(mValueTag,
						getNetwork().placeHolderValue(mPlaceHolders));
				mPlaceHolders++;
			}
		} else if (getStructurizer().isConnected(vertex))
			vertex.setTag(mValueTag, getNetwork().adaptValue(
					getStructurizer().getValue(vertex)));
		else {
			vertex.setTag(mValueTag, null);
			ExtendedValueNode[] children
					= new ExtendedValueNode[vertex.getChildCount()];
			for (int i = 0; i < children.length; i++)
				children[i] = determineValue(vertex.getChild(i));
			ValueSourceNode value;
			if (vertex.hasTag(mPlaceHolderTag))
				value = vertex.getTag(mPlaceHolderTag);
			else
				value = getNetwork().newValue();
			mConstructs.add(getNetwork().construct(value,
					getOpNode(vertex), children));
			vertex.setTag(mValueTag,
					getNetwork().constructValue(mConstructs.size() - 1));
		}
		return vertex.getTag(mValueTag);
	}
	
	public void addVertex(V vertex) {determineValue(vertex);}
	
	public int getPlaceHolders() {return mPlaceHolders;}
	public ListNode<? extends ConstructNode> getConstructs() {
		ListNode<? extends ConstructNode> constructs = getNetwork().empty();
		for (ConstructNode construct : mConstructs)
			constructs = getNetwork().postpend(constructs, construct);
		return constructs;
	}
}
