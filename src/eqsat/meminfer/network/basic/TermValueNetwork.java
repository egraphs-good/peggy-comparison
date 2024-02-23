package eqsat.meminfer.network.basic;

import static eqsat.meminfer.network.basic.TermValueNetwork.TermValueOp.ComponentTerm;
import eqsat.meminfer.network.Network;
import util.EmptyTag;
import util.Tag;
import util.graph.CExpressionGraph.Vertex;

public class TermValueNetwork extends Network {
	protected interface TermValueLabel extends NetworkLabel {}
	
	protected enum TermValueOp implements TermValueLabel {
		ComponentTerm;
	};
	
	protected static abstract class Node extends Network.Node {
		protected Node(Vertex<NetworkLabel> vertex) {super(vertex);}
	}
	
	public static abstract class TermValueNode extends Node {
		protected TermValueNode(Vertex<NetworkLabel> vertex) {
			super(vertex);
		}
		
		public boolean isComponentValue() {return false;}
		public ComponentValueNode getComponentValue() {
			throw new UnsupportedOperationException();
		}
	}
	
	public static final class ComponentValueNode extends TermValueNode {
		private static final Tag<ComponentValueNode> mTag
				= new EmptyTag<ComponentValueNode>();
		
		private final int mComponent;
		
		protected ComponentValueNode(Vertex<NetworkLabel> vertex,
				int component) {
			super(vertex);
			vertex.setTag(mTag, this);
			mComponent = component;
		}
		
		public boolean isComponentValue() {return true;}
		public ComponentValueNode getComponentValue() {return this;}
		
		public int getComponent() {return mComponent;}
	}
	
	public TermValueNetwork(Network network) {super(network);}
	
	public ComponentValueNode componentValue(int component) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(ComponentTerm,
				getGraph().getVertex(IntLabel.get(component)));
		ComponentValueNode node = vertex.getTag(ComponentValueNode.mTag);
		return node == null ? new ComponentValueNode(vertex, component)
				: node;
	}
}
