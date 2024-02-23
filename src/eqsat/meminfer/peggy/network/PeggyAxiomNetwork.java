package eqsat.meminfer.peggy.network;

import static eqsat.meminfer.peggy.network.PeggyAxiomNetwork.PeggyAxiomOp.Axiom;
import eqsat.FlowValue;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.network.peg.axiom.AddPEGOpNetwork;
import util.EmptyTag;
import util.Tag;
import util.graph.CExpressionGraph.Vertex;

public class PeggyAxiomNetwork<O> extends AddPEGOpNetwork<O> {
	protected interface PeggyAxiomLabel extends AddPEGOpLabel {}
	
	protected enum PeggyAxiomOp implements PeggyAxiomLabel {Axiom;};
	
	protected static abstract class Node extends AddPEGOpNetwork.Node {
		protected Node(Vertex<NetworkLabel> vertex) {super(vertex);}
	}
	
	public static abstract class AddOpNode<O> extends Node {
		protected AddOpNode(Vertex<NetworkLabel> vertex) {super(vertex);}
		
		public boolean isAddExistingOp() {return false;}
		public AddExistingOpNode getAddExistingOp() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isAddNewOp() {return false;}
		public <P> AddNewOpNode<FlowValue<P,O>> getAddNewOp() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isAddLoopOp() {return false;}
		public AddLoopOpNode getAddLoopOp() {
			throw new UnsupportedOperationException();
		}
	}
	
	private static final class AdaptAddExistingOpNode extends AddOpNode {
		private static final Tag<AdaptAddExistingOpNode> mTag
				= new EmptyTag<AdaptAddExistingOpNode>();
		
		private final AddExistingOpNode mAdapt;
		
		public AdaptAddExistingOpNode(Vertex<NetworkLabel> vertex,
				AddExistingOpNode adapt) {
			super(vertex);
			adapt.setTag(mTag, this);
			mAdapt = adapt;
		}
		
		public boolean isAddExistingOp() {return true;}
		public AddExistingOpNode getAddExistingOp() {return mAdapt;}
	}
	
	private static final class AdaptAddNewOpNode<O> extends AddOpNode<O> {
		private static final Tag<AdaptAddNewOpNode> mTag
				= new EmptyTag<AdaptAddNewOpNode>();
		
		private final AddNewOpNode<FlowValue<?,O>> mAdapt;
		
		public AdaptAddNewOpNode(Vertex<NetworkLabel> vertex,
				AddNewOpNode<FlowValue<?,O>> adapt) {
			super(vertex);
			if (adapt.getOp().isParameter())
				throw new IllegalArgumentException("Cannot add parameters");
			adapt.setTag(mTag, this);
			mAdapt = adapt;
		}
		
		public boolean isAddNewOp() {return true;}
		public <P> AddNewOpNode<FlowValue<P,O>> getAddNewOp() {
			return (AddNewOpNode)mAdapt;
		}
	}
	
	private static final class AdaptAddLoopOpNode extends AddOpNode {
		private static final Tag<AdaptAddLoopOpNode> mTag
				= new EmptyTag<AdaptAddLoopOpNode>();
		
		private final AddLoopOpNode mAdapt;
		
		public AdaptAddLoopOpNode(Vertex<NetworkLabel> vertex,
				AddLoopOpNode adapt) {
			super(vertex);
			adapt.setTag(mTag, this);
			mAdapt = adapt;
		}
		
		public boolean isAddLoopOp() {return true;}
		public AddLoopOpNode getAddLoopOp() {return mAdapt;}
	}
	
	public static final class AxiomNode<O, T> extends Node {
		private static final Tag<AxiomNode> mTag
				= new EmptyTag<AxiomNode>();
		
		private final String mName;
		private final T mTrigger;
		private final ListNode<? extends AddOpNode<O>> mOps;
		private final int mPlaceHolders;
		private final ListNode<? extends ConstructNode> mConstructs;
		private final ListNode<? extends MergeNode> mMerges;
		
		public AxiomNode(Vertex<NetworkLabel> vertex, String name,
				T trigger, ListNode<? extends AddOpNode<O>> ops,
				int placeHolders, ListNode<? extends ConstructNode> constructs,
				ListNode<? extends MergeNode> merges) {
			super(vertex);
			vertex.setTag(mTag, this);
			mName = name;
			mTrigger = trigger;
			mOps = ops;
			mPlaceHolders = placeHolders;
			mConstructs = constructs;
			mMerges = merges;
		}
		
		public String getName() {return mName;}
		public T getTrigger() {return mTrigger;}
		public ListNode<? extends AddOpNode<O>> getOps() {return mOps;}
		public int getPlaceHolders() {return mPlaceHolders;}
		public ListNode<? extends ConstructNode> getConstructs() {
			return mConstructs;
		}
		public ListNode<? extends MergeNode> getMerges() {return mMerges;}
	}
	
	public PeggyAxiomNetwork(Network network) {super(network);}
	
	public AddOpNode<O> adaptAddExistingOp(AddExistingOpNode adapt) {
		AddOpNode<O> node = adapt.getTag(AdaptAddExistingOpNode.mTag);
		return node == null
				? new AdaptAddExistingOpNode(adapt.getVertex(), adapt)
				: node;
	}
	
	public AddOpNode<O> adaptAddNewOp(AddNewOpNode<FlowValue<?,O>> adapt) {
		AddOpNode<O> node = adapt.getTag(AdaptAddNewOpNode.mTag);
		return node == null
				? new AdaptAddNewOpNode<O>(adapt.getVertex(), adapt) : node;
	}
	
	public AddOpNode<O> adaptAddLoopOp(AddLoopOpNode adapt) {
		AddOpNode<O> node = adapt.getTag(AdaptAddLoopOpNode.mTag);
		return node == null
				? new AdaptAddLoopOpNode(adapt.getVertex(), adapt) : node;
	}
	
	public <T extends eqsat.meminfer.network.Network.Node>
			AxiomNode<O,? extends T> axiom(String name, T trigger,
			ListNode<? extends AddOpNode<O>> ops,
			int placeHolders, ListNode<? extends ConstructNode> constructs,
			ListNode<? extends MergeNode> merges) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(Axiom,
				getGraph().getVertex(new StringLabel(name)),
				trigger.getVertex(), ops.getVertex(),
				getGraph().getVertex(IntLabel.get(placeHolders)),
				constructs.getVertex(), merges.getVertex());
		AxiomNode<O,? extends T> node = vertex.getTag(AxiomNode.mTag);
		return node == null ? new AxiomNode<O,T>(
				vertex, name, trigger, ops, placeHolders, constructs, merges)
				: node;
	}
}
