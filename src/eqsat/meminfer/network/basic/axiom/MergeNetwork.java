package eqsat.meminfer.network.basic.axiom;

import static eqsat.meminfer.network.basic.axiom.MergeNetwork.MergeOp.Merge;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.network.basic.ValueNetwork;
import eqsat.meminfer.network.op.axiom.ConstructNetwork;
import util.EmptyTag;
import util.Tag;
import util.graph.CExpressionGraph.Vertex;

public abstract class MergeNetwork extends ConstructNetwork {
	protected interface MergeLabel extends ConstructLabel {}
	
	protected enum MergeOp implements MergeLabel {Merge;}
	
	protected static abstract class Node extends ValueNetwork.Node {
		protected Node(Vertex<NetworkLabel> vertex) {super(vertex);}
	}
	
	public static final class MergeNode extends Node {
		private static final Tag<MergeNode> mTag = new EmptyTag<MergeNode>();
		
		private final ExtendedValueNode mLeft, mRight;
		
		protected MergeNode(Vertex<NetworkLabel> vertex, ExtendedValueNode left,
				ExtendedValueNode right) {
			super(vertex);
			vertex.setTag(mTag, this);
			mLeft = left;
			mRight = right;
		}
		
		public ExtendedValueNode getLeft() {return mLeft;}
		public ExtendedValueNode getRight() {return mRight;}
	}
	
	public MergeNetwork(Network network) {super(network);}
	
	public MergeNode merge(ExtendedValueNode left, ExtendedValueNode right) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(Merge,
				left.getVertex(), right.getVertex());
		MergeNode node = vertex.getTag(MergeNode.mTag);
		return node == null ? new MergeNode(vertex, left, right) : node;
	}
}
