package eqsat.meminfer.network.basic;

import static eqsat.meminfer.network.basic.StructureNetwork.StructureOp.AnyChild;
import static eqsat.meminfer.network.basic.StructureNetwork.StructureOp.CheckArity;
import static eqsat.meminfer.network.basic.StructureNetwork.StructureOp.CheckChildEquality;
import static eqsat.meminfer.network.basic.StructureNetwork.StructureOp.CheckChildIsFalse;
import static eqsat.meminfer.network.basic.StructureNetwork.StructureOp.CheckChildIsKnown;
import static eqsat.meminfer.network.basic.StructureNetwork.StructureOp.CheckEquality;
import static eqsat.meminfer.network.basic.StructureNetwork.StructureOp.False;
import static eqsat.meminfer.network.basic.StructureNetwork.StructureOp.General;
import static eqsat.meminfer.network.basic.StructureNetwork.StructureOp.Join;
import static eqsat.meminfer.network.basic.StructureNetwork.StructureOp.Known;
import static eqsat.meminfer.network.basic.StructureNetwork.StructureOp.ProductJoin;
import static eqsat.meminfer.network.basic.StructureNetwork.StructureOp.Represent;
import eqsat.meminfer.network.Network;
import util.EmptyTag;
import util.Tag;
import util.graph.CExpressionGraph.Vertex;

public class StructureNetwork extends ValueNetwork {
	protected interface StructureValue extends ValueLabel {}
	
	protected enum StructureOp implements StructureValue {
		General, Known, False, CheckArity, CheckChildEquality,
		Represent, AnyChild, GetComponent, GetChild, Join, ProductJoin,
		CheckEquality, CheckChildIsKnown, CheckChildIsFalse;
	};
	
	protected static abstract class Node extends ValueNetwork.Node {
		protected Node(Vertex<NetworkLabel> vertex) {super(vertex);}
	}
	
	public static abstract class StructureNode extends Node {
		public StructureNode(Vertex<NetworkLabel> vertex) {super(vertex);}
		
		public abstract int getTermCount();
		
		public boolean isTerm() {return false;}
		public TermNode getTerm() {throw new UnsupportedOperationException();}
		
		public boolean isJoin() {return false;}
		public JoinNode getJoin() {throw new UnsupportedOperationException();}
		
		public boolean isProductJoin() {return false;}
		public ProductJoinNode getProductJoin() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isCheckEquality() {return false;}
		public CheckEqualityNode getCheckEquality() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isCheckChildIsKnown() {return false;}
		public CheckChildIsKnownNode getCheckChildIsKnown() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isCheckChildIsFalse() {return false;}
		public CheckChildIsFalseNode getCheckChildIsFalse() {
			throw new UnsupportedOperationException();
		}
	}
	
	public static abstract class TermNode extends StructureNode {
		public TermNode(Vertex<NetworkLabel> vertex) {super(vertex);}
		
		public int getTermCount() {return 1;}
		
		public final boolean isTerm() {return true;}
		public final TermNode getTerm() {return this;}
		
		public boolean isGeneral() {return false;}
		public GeneralNode getGeneral() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isKnown() {return false;}
		public KnownNode getKnown() {throw new UnsupportedOperationException();}
		
		public boolean isFalse() {return false;}
		public FalseNode getFalse() {throw new UnsupportedOperationException();}
		
		public boolean isCheckArity() {return false;}
		public CheckArityNode getCheckArity() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isCheckChildEquality() {return false;}
		public CheckChildEqualityNode getCheckChildEquality() {
			throw new UnsupportedOperationException();
		}
	}
	
	public static final class GeneralNode extends TermNode {
		private static final Tag<GeneralNode> mTag
				= new EmptyTag<GeneralNode>();
		
		public GeneralNode(Vertex<NetworkLabel> vertex) {
			super(vertex);
			vertex.setTag(mTag, this);
		}
		
		public boolean isGeneral() {return true;}
		public GeneralNode getGeneral() {return this;}
	}
	
	public static final class KnownNode extends TermNode {
		private static final Tag<KnownNode> mTag = new EmptyTag<KnownNode>();
		
		public KnownNode(Vertex<NetworkLabel> vertex) {
			super(vertex);
			vertex.setTag(mTag, this);
		}
		
		public boolean isKnown() {return true;}
		public KnownNode getKnown() {return this;}
	}
	
	public static final class FalseNode extends TermNode {
		private static final Tag<FalseNode> mTag = new EmptyTag<FalseNode>();
		
		public FalseNode(Vertex<NetworkLabel> vertex) {
			super(vertex);
			vertex.setTag(mTag, this);
		}
		
		public boolean isFalse() {return true;}
		public FalseNode getFalse() {return this;}
	}
	
	public static final class CheckArityNode extends TermNode {
		private static final Tag<CheckArityNode> mTag = new EmptyTag();
		
		private final int mArity;
		private final TermNode mInput;
		
		protected CheckArityNode(Vertex<NetworkLabel> vertex, int arity,
				TermNode input) {
			super(vertex);
			vertex.setTag(mTag, this);
			mArity = arity;
			mInput = input;
		}
		
		public boolean isCheckArity() {return true;}
		public CheckArityNode getCheckArity() {return this;}
		
		public int getArity() {return mArity;}
		public TermNode getInput() {return mInput;}
	}
	
	public static final class CheckChildEqualityNode extends TermNode {
		private static final Tag<CheckChildEqualityNode> mTag = new EmptyTag();
		
		private final int mLeft, mRight;
		private final TermNode mInput;
		
		protected CheckChildEqualityNode(Vertex<NetworkLabel> vertex,
				int left, int right, TermNode input) {
			super(vertex);
			vertex.setTag(mTag, this);
			mLeft = left;
			mRight = right;
			mInput = input;
		}
		
		public boolean isCheckChildEquality() {return true;}
		public CheckChildEqualityNode getCheckChildEquality() {return this;}
		
		public int getLeft() {return mLeft;}
		public int getRight() {return mRight;}
		public TermNode getInput() {return mInput;}
	}
	
	public static final class JoinNode extends StructureNode {
		private static final Tag<JoinNode> mTag = new EmptyTag<JoinNode>();
		
		private final RepresentativeNode mLeft, mRight;
		
		protected JoinNode(Vertex<NetworkLabel> vertex,
				RepresentativeNode left, RepresentativeNode right) {
			super(vertex);
			vertex.setTag(mTag, this);
			mLeft = left;
			mRight = right;
		}

		public int getTermCount() {
			return mLeft.getTermCount() + mRight.getTermCount();
		}
		
		public boolean isJoin() {return true;}
		public JoinNode getJoin() {return this;}
		
		public RepresentativeNode getLeft() {return mLeft;}
		public RepresentativeNode getRight() {return mRight;}
	}
	
	public static final class ProductJoinNode extends StructureNode {
		private static final Tag<ProductJoinNode> mTag = new EmptyTag();
		
		private final StructureNode mLeft, mRight;
		
		protected ProductJoinNode(Vertex<NetworkLabel> vertex,
				StructureNode left, StructureNode right) {
			super(vertex);
			vertex.setTag(mTag, this);
			mLeft = left;
			mRight = right;
		}

		public int getTermCount() {
			return mLeft.getTermCount() + mRight.getTermCount();
		}
		
		public boolean isProductJoin() {return true;}
		public ProductJoinNode getProductJoin() {return this;}
		
		public StructureNode getLeft() {return mLeft;}
		public StructureNode getRight() {return mRight;}
	}
	
	public static final class CheckEqualityNode extends StructureNode {
		private static final Tag<CheckEqualityNode> mTag = new EmptyTag();
		
		private final ValueNode mLeft, mRight;
		private final StructureNode mInput;
		
		public CheckEqualityNode(Vertex<NetworkLabel> vertex,
				ValueNode left, ValueNode right, StructureNode input) {
			super(vertex);
			vertex.setTag(mTag, this);
			mLeft = left;
			mRight = right;
			mInput = input;
		}
		
		public int getTermCount() {return mInput.getTermCount();}
		
		public boolean isCheckEquality() {return true;}
		public CheckEqualityNode getCheckEquality() {return this;}
		
		public ValueNode getLeft() {return mLeft;}
		public ValueNode getRight() {return mRight;}
		public StructureNode getInput() {return mInput;}
	}
	
	public static final class CheckChildIsKnownNode extends StructureNode {
		private static final Tag<CheckChildIsKnownNode> mTag = new EmptyTag();
		
		private final TermValueNode mTerm;
		private final int mChild;
		private final StructureNode mInput;
		
		public CheckChildIsKnownNode(Vertex<NetworkLabel> vertex,
				TermValueNode term, int child, StructureNode input) {
			super(vertex);
			vertex.setTag(mTag, this);
			mTerm = term;
			mChild = child;
			mInput = input;
		}
		
		public int getTermCount() {return mInput.getTermCount();}
		
		public boolean isCheckChildIsKnown() {return true;}
		public CheckChildIsKnownNode getCheckChildIsKnown() {return this;}
		
		public TermValueNode getParentTerm() {return mTerm;}
		public int getChild() {return mChild;}
		public StructureNode getInput() {return mInput;}
	}
	
	public static final class CheckChildIsFalseNode extends StructureNode {
		private static final Tag<CheckChildIsFalseNode> mTag = new EmptyTag();
		
		private final TermValueNode mTerm;
		private final int mChild;
		private final StructureNode mInput;
		
		public CheckChildIsFalseNode(Vertex<NetworkLabel> vertex,
				TermValueNode term, int child, StructureNode input) {
			super(vertex);
			vertex.setTag(mTag, this);
			mTerm = term;
			mChild = child;
			mInput = input;
		}
		
		public int getTermCount() {return mInput.getTermCount();}
		
		public boolean isCheckChildIsFalse() {return true;}
		public CheckChildIsFalseNode getCheckChildIsFalse() {return this;}
		
		public TermValueNode getParentTerm() {return mTerm;}
		public int getChild() {return mChild;}
		public StructureNode getInput() {return mInput;}
	}
	
	public static abstract class RepresentativeNode extends Node {
		public RepresentativeNode(Vertex<NetworkLabel> vertex) {
			super(vertex);
		}
		
		public abstract int getTermCount();
		
		public boolean isTerm() {return false;}
		public TermNode getTerm() {throw new UnsupportedOperationException();}
		
		public boolean isRepresent() {return false;}
		public RepresentNode getRepresent() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isAnyChild() {return false;}
		public AnyChildNode getAnyChild() {
			throw new UnsupportedOperationException();
		}
	}
	
	private static final class AdaptTermNode extends RepresentativeNode {
		private static final Tag<AdaptTermNode> mTag = new EmptyTag();
		
		private final TermNode mTerm;
		
		public AdaptTermNode(Vertex<NetworkLabel> vertex, TermNode term) {
			super(vertex);
			term.setTag(mTag, this);
			mTerm = term;
		}
		
		public int getTermCount() {return 1;}
		
		public boolean isTerm() {return true;}
		public TermNode getTerm() {return mTerm;}
	}
	
	public static final class RepresentNode extends RepresentativeNode {
		private static final Tag<RepresentNode> mTag = new EmptyTag();
		
		private final ValueNode mValue;
		private final StructureNode mStructure;
		
		public RepresentNode(Vertex<NetworkLabel> vertex, ValueNode value,
				StructureNode structure) {
			super(vertex);
			vertex.setTag(mTag, this);
			mValue = value;
			mStructure = structure;
		}

		public int getTermCount() {return mStructure.getTermCount();}
		
		public boolean isRepresent() {return true;}
		public RepresentNode getRepresent() {return this;}
		
		public ValueNode getValue() {return mValue;}
		public StructureNode getStructure() {return mStructure;}
	}
	
	public static final class AnyChildNode extends RepresentativeNode {
		private static final Tag<AnyChildNode> mTag = new EmptyTag();
		
		private final TermValueNode mTerm;
		private final StructureNode mStructure;
		
		public AnyChildNode(Vertex<NetworkLabel> vertex, TermValueNode term,
				StructureNode structure) {
			super(vertex);
			vertex.setTag(mTag, this);
			mTerm = term;
			mStructure = structure;
		}

		public int getTermCount() {return mStructure.getTermCount();}
		
		public boolean isAnyChild() {return true;}
		public AnyChildNode getAnyChild() {return this;}
		
		public TermValueNode getTermValue() {return mTerm;}
		public StructureNode getStructure() {return mStructure;}
	}
	
	public StructureNetwork(Network network) {super(network);}
	
	public GeneralNode general() {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(General);
		GeneralNode node = vertex.getTag(GeneralNode.mTag);
		return node == null ? new GeneralNode(vertex) : node;
	}
	
	public KnownNode known() {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(Known);
		KnownNode node = vertex.getTag(KnownNode.mTag);
		return node == null ? new KnownNode(vertex) : node;
	}
	
	public FalseNode fals() {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(False);
		FalseNode node = vertex.getTag(FalseNode.mTag);
		return node == null ? new FalseNode(vertex) : node;
	}
	
	public CheckArityNode checkArity(int arity, TermNode input) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(CheckArity,
				getGraph().getVertex(IntLabel.get(arity)), input.getVertex());
		CheckArityNode node = vertex.getTag(CheckArityNode.mTag);
		return node == null ? new CheckArityNode(vertex, arity, input) : node;
	}
	
	public CheckChildEqualityNode checkChildEquality(int left, int right,
			TermNode input) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(CheckChildEquality,
				getGraph().getVertex(IntLabel.get(left)),
				getGraph().getVertex(IntLabel.get(right)), input.getVertex());
		CheckChildEqualityNode node = vertex.getTag(
				CheckChildEqualityNode.mTag);
		return node == null
				? new CheckChildEqualityNode(vertex, left, right, input)
				: node;
	}
	
	public JoinNode join(RepresentativeNode left, RepresentativeNode right) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(Join,
				left.getVertex(), right.getVertex());
		JoinNode node = vertex.getTag(JoinNode.mTag);
		return node == null ? new JoinNode(vertex, left, right) : node;
	}
	
	public ProductJoinNode productJoin(StructureNode left, StructureNode right){
		Vertex<NetworkLabel> vertex = getGraph().getVertex(ProductJoin,
				left.getVertex(), right.getVertex());
		ProductJoinNode node = vertex.getTag(ProductJoinNode.mTag);
		return node == null ? new ProductJoinNode(vertex, left, right) : node;
	}
	
	public CheckEqualityNode checkEquality(ValueNode left, ValueNode right,
			StructureNode input) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(CheckEquality,
				left.getVertex(), right.getVertex(), input.getVertex());
		CheckEqualityNode node = vertex.getTag(CheckEqualityNode.mTag);
		return node == null ? new CheckEqualityNode(vertex, left, right, input)
				: node;
	}
	
	public CheckChildIsKnownNode checkChildIsKnown(TermValueNode term,
			int child, StructureNode input) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(CheckChildIsKnown,
				term.getVertex(), getGraph().getVertex(IntLabel.get(child)),
				input.getVertex());
		CheckChildIsKnownNode node = vertex.getTag(CheckChildIsKnownNode.mTag);
		return node == null
				? new CheckChildIsKnownNode(vertex, term, child, input) : node;
	}
	
	public CheckChildIsFalseNode checkChildIsFalse(TermValueNode term,
			int child, StructureNode input) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(CheckChildIsFalse,
				term.getVertex(), getGraph().getVertex(IntLabel.get(child)),
				input.getVertex());
		CheckChildIsFalseNode node = vertex.getTag(CheckChildIsFalseNode.mTag);
		return node == null
				? new CheckChildIsFalseNode(vertex, term, child, input) : node;
	}
	
	public RepresentativeNode adaptTerm(TermNode term) {
		RepresentativeNode node = term.getTag(AdaptTermNode.mTag);
		return node == null ? new AdaptTermNode(term.getVertex(), term) : node;
	}
	
	public RepresentNode represent(ValueNode value, StructureNode structure) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(Represent,
				value.getVertex(), structure.getVertex());
		RepresentNode node = vertex.getTag(RepresentNode.mTag);
		return node == null ? new RepresentNode(vertex, value, structure)
				: node;
	}
	
	public AnyChildNode anyChild(TermValueNode term, StructureNode structure) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(AnyChild,
				term.getVertex(), structure.getVertex());
		AnyChildNode node = vertex.getTag(AnyChildNode.mTag);
		return node == null ? new AnyChildNode(vertex, term, structure) : node;
	}
}
