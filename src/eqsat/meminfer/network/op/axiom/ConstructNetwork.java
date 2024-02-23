package eqsat.meminfer.network.op.axiom;

import static eqsat.meminfer.network.op.axiom.ConstructNetwork.ConstructOp.*;

import java.util.ArrayList;
import java.util.List;

import eqsat.meminfer.network.Network;
import eqsat.meminfer.network.basic.ValueNetwork;
import util.EmptyTag;
import util.Tag;
import util.graph.CExpressionGraph.Vertex;

public class ConstructNetwork extends ValueNetwork {
	protected interface ConstructLabel extends ValueLabel {}
	
	protected enum ConstructOp implements ConstructLabel {
		NewValue, PlaceHolderValueSource, PlaceHolderValue, ConstructValue, Op,
		ConstructPlaceHolder, ConstructExpression,
		ConstructTrue, ConstructFalse;
	};
	
	protected static abstract class Node extends ValueNetwork.Node {
		protected Node(Vertex<NetworkLabel> vertex) {super(vertex);}
	}
	
	public static final class ValueSourceNode extends Node {
		private static final Tag<ValueSourceNode> mTag = new EmptyTag();
		
		private final int mPlaceHolder;
		
		protected ValueSourceNode(Vertex<NetworkLabel> vertex, int placeHolder){
			super(vertex);
			mPlaceHolder = placeHolder;
		}
		
		public boolean isNewValue() {return mPlaceHolder < 0;}
		public boolean isPlaceHolderValue() {return mPlaceHolder >= 0;}
		public int getPlaceHolderValue() {
			if (mPlaceHolder < 0)
				throw new UnsupportedOperationException();
			return mPlaceHolder;
		}
	}
	
	public static abstract class ExtendedValueNode extends Node {
		protected ExtendedValueNode(Vertex<NetworkLabel> vertex) {
			super(vertex);
		}
		
		public boolean isExtendedValue() {return true;}
		public ExtendedValueNode getExtendedValue() {return this;}
		
		public boolean isValue() {return false;}
		public ValueNode getValue() {throw new UnsupportedOperationException();}
		
		public boolean isPlaceHolderValue() {return false;}
		public PlaceHolderValueNode getPlaceHolderValue() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isConstructValue() {return false;}
		public ConstructValueNode getConstructValue() {
			throw new UnsupportedOperationException();
		}
	}
	
	private static final class AdaptValueNode extends ExtendedValueNode {
		private static final Tag<AdaptValueNode> mTag = new EmptyTag();
		
		private final ValueNode mValue;
		
		protected AdaptValueNode(ValueNode value) {
			super(value.getVertex());
			value.setTag(mTag, this);
			mValue = value;
		}
		
		public boolean isValue() {return true;}
		public ValueNode getValue() {return mValue;}
	}
	
	public static final class PlaceHolderValueNode extends ExtendedValueNode {
		private static final Tag<PlaceHolderValueNode> mTag = new EmptyTag();
		
		private final int mPlaceHolder;
		
		protected PlaceHolderValueNode(Vertex<NetworkLabel> vertex,
				int placeHolder) {
			super(vertex);
			vertex.setTag(mTag, this);
			mPlaceHolder = placeHolder;
		}
		
		public boolean isPlaceHolderValue() {return true;}
		public PlaceHolderValueNode getPlaceHolderValue() {return this;}
		
		public int getPlaceHolder() {return mPlaceHolder;}
	}
	
	public static final class ConstructValueNode extends ExtendedValueNode {
		private static final Tag<ConstructValueNode> mTag = new EmptyTag();
		
		private final int mConstruct;
		
		protected ConstructValueNode(Vertex<NetworkLabel> vertex,
				int construct) {
			super(vertex);
			vertex.setTag(mTag, this);
			mConstruct = construct;
		}
		
		public boolean isConstructValue() {return true;}
		public ConstructValueNode getConstructValue() {return this;}
		
		public int getConstruct() {return mConstruct;}
	}
	
	public static final class OpNode extends Node {
		private static final Tag<OpNode> mTag = new EmptyTag<OpNode>();
		
		private final int mOp;
		
		protected OpNode(Vertex<NetworkLabel> vertex, int op) {
			super(vertex);
			vertex.setTag(mTag, this);
			mOp = op;
		}
		
		public int getOp() {return mOp;}
	}
	
	public static abstract class ConstructNode extends Node {
		protected ConstructNode(Vertex<NetworkLabel> vertex) {super(vertex);}
		
		public boolean isConstructExpression() {return false;}
		public ConstructExpressionNode getConstructExpression() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isConstructTrue() {return false;}
		public ConstructTrueNode getConstructTrue() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isConstructFalse() {return false;}
		public ConstructFalseNode getConstructFalse() {
			throw new UnsupportedOperationException();
		}
	}
	
	public static final class ConstructExpressionNode extends ConstructNode {
		private static final Tag<ConstructExpressionNode> mTag = new EmptyTag();
		
		private final ValueSourceNode mValue;
		private final OpNode mOp;
		private final ExtendedValueNode[] mChildren;
		
		protected ConstructExpressionNode(Vertex<NetworkLabel> vertex,
				ValueSourceNode value, OpNode op,
				ExtendedValueNode... children) {
			super(vertex);
			vertex.setTag(mTag, this);
			mValue = value;
			mOp = op;
			mChildren = children;
		}
		
		public boolean isConstructExpression() {return true;}
		public ConstructExpressionNode getConstructExpression() {return this;}
		
		public ValueSourceNode getValue() {return mValue;}
		public OpNode getOp() {return mOp;}
		public int getArity() {return mChildren.length;}
		public ExtendedValueNode getChild(int index) {return mChildren[index];}
	}
	
	public static final class ConstructTrueNode extends ConstructNode {
		private static final Tag<ConstructTrueNode> mTag = new EmptyTag();
		
		protected ConstructTrueNode(Vertex<NetworkLabel> vertex) {
			super(vertex);
			vertex.setTag(mTag, this);
		}
		
		public boolean isConstructTrue() {return true;}
		public ConstructTrueNode getConstructTrue() {return this;}
	}
	
	public static final class ConstructFalseNode extends ConstructNode {
		private static final Tag<ConstructFalseNode> mTag = new EmptyTag();
		
		protected ConstructFalseNode(Vertex<NetworkLabel> vertex) {
			super(vertex);
			vertex.setTag(mTag, this);
		}
		
		public boolean isConstructFalse() {return true;}
		public ConstructFalseNode getConstructFalse() {return this;}
	}
	
	public ConstructNetwork(Network network) {super(network);}
	
	public ValueSourceNode newValue() {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(NewValue);
		ValueSourceNode node = vertex.getTag(ValueSourceNode.mTag);
		return node == null ? new ValueSourceNode(vertex, -1) : node;
	}
	
	public ValueSourceNode placeHolderValueSource(int placeHolder) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(
				PlaceHolderValueSource,
				getGraph().getVertex(IntLabel.get(placeHolder)));
		ValueSourceNode node = vertex.getTag(ValueSourceNode.mTag);
		return node == null ? new ValueSourceNode(vertex, placeHolder) : node;
	}
	
	public PlaceHolderValueNode placeHolderValue(int placeHolder) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(PlaceHolderValue,
				getGraph().getVertex(IntLabel.get(placeHolder)));
		PlaceHolderValueNode node = vertex.getTag(PlaceHolderValueNode.mTag);
		return node == null ? new PlaceHolderValueNode(vertex, placeHolder)
				: node;
	}
	
	public ExtendedValueNode adaptValue(ValueNode value) {
		AdaptValueNode node = value.getTag(AdaptValueNode.mTag);
		return node == null ? new AdaptValueNode(value) : node;
	}
	
	public ConstructValueNode constructValue(int construct) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(ConstructValue,
				getGraph().getVertex(IntLabel.get(construct)));
		ConstructValueNode node = vertex.getTag(ConstructValueNode.mTag);
		return node == null ? new ConstructValueNode(vertex, construct)
				: node;
	}
	
	public OpNode op(int index) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(Op,
				getGraph().getVertex(IntLabel.get(index)));
		OpNode node = vertex.getTag(OpNode.mTag);
		return node == null ? new OpNode(vertex, index) : node;
	}
	
	public ConstructExpressionNode construct(ValueSourceNode value,
			OpNode configure, ExtendedValueNode... children) {
		List<Vertex<NetworkLabel>> params
				= new ArrayList<Vertex<NetworkLabel>>();
		params.add(value.getVertex());
		params.add(configure.getVertex());
		for (ExtendedValueNode child : children)
			params.add(child.getVertex());
		Vertex<NetworkLabel> vertex = getGraph().getVertex(ConstructExpression,
				params);
		ConstructExpressionNode node
				= vertex.getTag(ConstructExpressionNode.mTag);
		return node == null ?
				new ConstructExpressionNode(vertex, value, configure, children)
				: node;
	}

	public ConstructTrueNode constructTrue() {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(ConstructTrue);
		ConstructTrueNode node = vertex.getTag(ConstructTrueNode.mTag);
		return node == null ? new ConstructTrueNode(vertex) : node;
	}

	public ConstructFalseNode constructFalse() {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(ConstructFalse);
		ConstructFalseNode node = vertex.getTag(ConstructFalseNode.mTag);
		return node == null ? new ConstructFalseNode(vertex) : node;
	}
}
