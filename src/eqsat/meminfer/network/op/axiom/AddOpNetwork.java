package eqsat.meminfer.network.op.axiom;

import static eqsat.meminfer.network.op.axiom.AddOpNetwork.AddOpOp.AddExistingOp;
import static eqsat.meminfer.network.op.axiom.AddOpNetwork.AddOpOp.AddNewOp;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.network.basic.axiom.MergeNetwork;
import util.EmptyTag;
import util.Tag;
import util.graph.CExpressionGraph.Vertex;

public class AddOpNetwork<O> extends MergeNetwork {
	protected interface AddOpLabel extends MergeLabel {}
	
	protected enum AddOpOp implements AddOpLabel {
		AddExistingOp, AddNewOp;
	};
	
	protected static abstract class Node extends ConstructNetwork.Node {
		protected Node(Vertex<NetworkLabel> vertex) {super(vertex);}
	}
	
	public static final class AddExistingOpNode extends Node {
		private static final Tag<AddExistingOpNode> mTag = new EmptyTag();
		
		private final TermValueNode mTerm;
		
		protected AddExistingOpNode(Vertex<NetworkLabel> vertex,
				TermValueNode term) {
			super(vertex);
			vertex.setTag(mTag, this);
			mTerm = term;
		}
		
		public TermValueNode getTerm() {return mTerm;}
	}
	
	public static final class AddNewOpNode<O> extends Node {
		private static final Tag<AddNewOpNode> mTag = new EmptyTag();
		
		private final O mOp;
		
		protected AddNewOpNode(Vertex<NetworkLabel> vertex, O op) {
			super(vertex);
			vertex.setTag(mTag, this);
			mOp = op;
		}
		
		public O getOp() {return mOp;}
	}
	
	public AddOpNetwork(Network network) {super(network);}
	
	public AddExistingOpNode addExistingOp(TermValueNode term) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(AddExistingOp,
				term.getVertex());
		AddExistingOpNode node = vertex.getTag(AddExistingOpNode.mTag);
		return node == null ? new AddExistingOpNode(vertex, term) : node;
	}
	
	public AddNewOpNode<O> addNewOp(O op) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(AddNewOp,
				getGraph().getVertex(new OpLabel<O>(op)));
		AddNewOpNode<O> node = vertex.getTag(AddNewOpNode.mTag);
		return node == null ? new AddNewOpNode<O>(vertex, op) : node;
	}
}
