package eqsat.meminfer.network.basic;

import static eqsat.meminfer.network.basic.ValueNetwork.ValueOp.ChildValue;
import eqsat.meminfer.network.Network;
import util.EmptyTag;
import util.Tag;
import util.graph.CExpressionGraph.Vertex;

public class ValueNetwork extends TermValueNetwork {
	protected interface ValueLabel extends TermValueLabel {}
	
	protected enum ValueOp implements ValueLabel {ChildValue;};
	
	protected static abstract class Node extends TermValueNetwork.Node {
		protected Node(Vertex<NetworkLabel> vertex) {super(vertex);}
	}
	
	public static abstract class ValueNode extends Node {
		protected ValueNode(Vertex<NetworkLabel> vertex) {super(vertex);}
		
		public boolean isTermValue() {return false;}
		public TermValueNode getTermValue() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isChildValue() {return false;}
		public ChildValueNode getChildValue() {
			throw new UnsupportedOperationException();
		}
	}
	
	public static final class ChildValueNode extends ValueNode {
		private static final Tag<ChildValueNode> mTag
				= new EmptyTag<ChildValueNode>();
		
		private final int mChild;
		private final TermValueNode mInput;
		
		protected ChildValueNode(Vertex<NetworkLabel> vertex, int child,
				TermValueNode input) {
			super(vertex);
			vertex.setTag(mTag, this);
			mChild = child;
			mInput = input;
		}
		
		public boolean isChildValue() {return true;}
		public ChildValueNode getChildValue() {return this;}
		
		public int getChild() {return mChild;}
		public TermValueNode getInput() {return mInput;}
	}
	
	private static final class AdaptTermValueNode extends ValueNode {
		private static final Tag<AdaptTermValueNode> mTag
				= new EmptyTag<AdaptTermValueNode>();
		
		private final TermValueNode mTerm;
		
		protected AdaptTermValueNode(TermValueNode term) {
			super(term.getVertex());
			term.setTag(mTag, this);
			mTerm = term;
		}
		
		public boolean isTermValue() {return true;}
		public TermValueNode getTermValue() {return mTerm;}
	}
	
	public ValueNetwork(Network network) {super(network);}
	
	public ChildValueNode childValue(int child, TermValueNode input) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(ChildValue,
				getGraph().getVertex(IntLabel.get(child)), input.getVertex());
		ChildValueNode node = vertex.getTag(ChildValueNode.mTag);
		return node == null ? new ChildValueNode(vertex, child, input)
				: node;
	}
	
	public ValueNode adaptTermValue(TermValueNode term) {
		AdaptTermValueNode node = term.getTag(AdaptTermValueNode.mTag);
		return node == null ? new AdaptTermValueNode(term) : node;
	}
}
