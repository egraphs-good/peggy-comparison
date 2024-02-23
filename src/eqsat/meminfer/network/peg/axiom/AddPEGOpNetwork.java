package eqsat.meminfer.network.peg.axiom;

import static eqsat.meminfer.network.peg.axiom.AddPEGOpNetwork.AddPEGOpOp.AddLoopOp;
import eqsat.FlowValue;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.network.op.axiom.AddOpNetwork;
import eqsat.meminfer.network.peg.PEGNetwork.PEGLoopOp;
import util.EmptyTag;
import util.Tag;
import util.graph.CExpressionGraph.Vertex;

public class AddPEGOpNetwork<O> extends AddOpNetwork<FlowValue<?,O>> {
	protected interface AddPEGOpLabel extends AddOpLabel {}
	
	protected enum AddPEGOpOp implements AddPEGOpLabel {AddLoopOp;};
	
	protected static abstract class Node extends AddOpNetwork.Node {
		protected Node(Vertex<NetworkLabel> vertex) {super(vertex);}
	}
	
	public static final class AddLoopOpNode extends Node {
		private static final Tag<AddLoopOpNode> mTag = new EmptyTag();
		
		private final PEGLoopOp mOp;
		private final TermValueNode mLoop;
		
		protected AddLoopOpNode(Vertex<NetworkLabel> vertex,
				PEGLoopOp op, TermValueNode loop) {
			super(vertex);
			vertex.setTag(mTag, this);
			mOp = op;
			mLoop = loop;
		}
		
		public PEGLoopOp getOp() {return mOp;}
		public TermValueNode getLoop() {return mLoop;}
	}
	
	public AddPEGOpNetwork(Network network) {super(network);}
	
	public AddLoopOpNode addLoopOp(PEGLoopOp op, TermValueNode loop) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(AddLoopOp,
				getGraph().getVertex(op), loop.getVertex());
		AddLoopOpNode node = vertex.getTag(AddLoopOpNode.mTag);
		return node == null ? new AddLoopOpNode(vertex, op, loop) : node;
	}
}
