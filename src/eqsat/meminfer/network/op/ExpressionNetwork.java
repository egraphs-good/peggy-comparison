package eqsat.meminfer.network.op;

import static eqsat.meminfer.network.op.ExpressionNetwork.ExpressionOp.OpEquals;
import static eqsat.meminfer.network.op.ExpressionNetwork.ExpressionOp.OpsEqual;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.network.basic.StructureNetwork;
import util.EmptyTag;
import util.Tag;
import util.graph.CExpressionGraph.Vertex;

public class ExpressionNetwork<O> extends StructureNetwork {
	private interface ExpressionLabel<O> extends NetworkLabel {}
	
	protected enum ExpressionOp implements ExpressionLabel {
		OpEquals, OpsEqual;
	};
	
	private static abstract class Node<O> extends Network.Node {
		protected Node(Vertex<NetworkLabel> vertex) {super(vertex);}
	}
	
	public static abstract class ExpressionNode<O> extends Node<O> {
		protected ExpressionNode(Vertex<NetworkLabel> vertex) {
			super(vertex);
		}
		
		public boolean isStructure() {return false;}
		public StructureNode getStructure() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isOpEquals() {return false;}
		public OpEqualsNode<O> getOpEquals() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isOpsEqual() {return false;}
		public OpsEqualNode<O> getOpsEqual() {
			throw new UnsupportedOperationException();
		}
	}
	
	private static final class AdaptStructureNode<O> extends ExpressionNode<O> {
		private static final Tag<AdaptStructureNode> mTag
				= new EmptyTag<AdaptStructureNode>();
		
		private final StructureNode mStructure;
		
		protected AdaptStructureNode(StructureNode structure) {
			super(structure.getVertex());
			structure.setTag(mTag, this);
			mStructure = structure;
		}
		
		public boolean isStructure() {return true;}
		public StructureNode getStructure() {return mStructure;}
	}
	
	public static final class OpEqualsNode<O> extends ExpressionNode<O> {
		private static final Tag<OpEqualsNode> mTag = new EmptyTag();
		
		private final TermValueNode mTerm;
		private final O mOp;
		private final ExpressionNode<O> mInput;
		
		protected OpEqualsNode(Vertex<NetworkLabel> vertex, TermValueNode term,
				O op, ExpressionNode<O> input) {
			super(vertex);
			vertex.setTag(mTag, this);
			mTerm = term;
			mOp = op;
			mInput = input;
		}
		
		public boolean isOpEquals() {return true;}
		public OpEqualsNode<O> getOpEquals() {return this;}
		
		public TermValueNode getTerm() {return mTerm;}
		public O getOp() {return mOp;}
		public ExpressionNode<O> getInput() {return mInput;}
	}
	
	public static final class OpsEqualNode<O> extends ExpressionNode<O> {
		private static final Tag<OpsEqualNode> mTag = new EmptyTag();
		
		private final TermValueNode mLeft, mRight;
		private final ExpressionNode<O> mInput;
		
		protected OpsEqualNode(Vertex<NetworkLabel> vertex, TermValueNode left,
				TermValueNode right, ExpressionNode<O> input) {
			super(vertex);
			vertex.setTag(mTag, this);
			mLeft = left;
			mRight = right;
			mInput = input;
		}
		
		public boolean isOpsEqual() {return true;}
		public OpsEqualNode<O> getOpsEqual() {return this;}
		
		public TermValueNode getLeft() {return mLeft;}
		public TermValueNode getRight() {return mRight;}
		public ExpressionNode<O> getInput() {return mInput;}
	}
	
	public ExpressionNetwork(Network network) {super(network);}
	
	public ExpressionNode<O> adaptStructure(StructureNode structure) {
		AdaptStructureNode<O> node = structure.getTag(AdaptStructureNode.mTag);
		return node == null ? new AdaptStructureNode<O>(structure) : node;
	}
	
	public OpEqualsNode<O> opEquals(TermValueNode term, O op,
			ExpressionNode<O> input) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(OpEquals,
				term.getVertex(), getGraph().getVertex(new OpLabel<O>(op)),
				input.getVertex());
		OpEqualsNode<O> node = vertex.getTag(OpEqualsNode.mTag);
		return node == null ? new OpEqualsNode<O>(vertex, term, op, input)
				: node;
	}
	
	public OpsEqualNode<O> opsEqual(TermValueNode left, TermValueNode right,
			ExpressionNode<O> input) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(OpsEqual,
				left.getVertex(), right.getVertex(), input.getVertex());
		OpsEqualNode<O> node = vertex.getTag(OpsEqualNode.mTag);
		return node == null ? new OpsEqualNode<O>(vertex, left, right, input)
				: node;
	}
}
