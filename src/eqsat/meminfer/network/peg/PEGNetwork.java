package eqsat.meminfer.network.peg;

import static eqsat.meminfer.network.peg.PEGNetwork.PEGOp.CheckDistinctLoopDepths;
import static eqsat.meminfer.network.peg.PEGNetwork.PEGOp.CheckEqualLoopDepths;
import static eqsat.meminfer.network.peg.PEGNetwork.PEGOp.IsInvariant;
import static eqsat.meminfer.network.peg.PEGNetwork.PEGOp.OpIsAllLoopLifted;
import static eqsat.meminfer.network.peg.PEGNetwork.PEGOp.OpIsExtendedDomainOp;
import static eqsat.meminfer.network.peg.PEGNetwork.PEGOp.OpIsLoopLifted;
import static eqsat.meminfer.network.peg.PEGNetwork.PEGOp.OpIsLoopOp;
import eqsat.FlowValue;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.network.op.ExpressionNetwork;
import util.EmptyTag;
import util.Tag;
import util.graph.CExpressionGraph.Vertex;

public class PEGNetwork<O> extends ExpressionNetwork<FlowValue<?,O>> {
	private interface PEGLabel<O> extends NetworkLabel {}
	
	protected enum PEGOp implements PEGLabel {
		CheckEqualLoopDepths, CheckDistinctLoopDepths, IsInvariant,
		OpIsExtendedDomainOp, OpIsLoopOp, OpIsLoopLifted, OpIsAllLoopLifted;
	};
	
	public static enum PEGLoopOp implements PEGLabel {
		Theta {
			public boolean isLoopOp(FlowValue<?,?> op) {return op.isTheta();}
			public <P,O> FlowValue<P,O> getFlowValue(int depth) {
				return FlowValue.<P,O>createTheta(depth);
			}
		}, Eval {
			public boolean isLoopOp(FlowValue<?,?> op) {return op.isEval();}
			public <P,O> FlowValue<P,O> getFlowValue(int depth) {
				return FlowValue.<P,O>createEval(depth);
			}
		}, Pass {
			public boolean isLoopOp(FlowValue<?,?> op) {return op.isPass();}
			public <P,O> FlowValue<P,O> getFlowValue(int depth) {
				return FlowValue.<P,O>createPass(depth);
			}
		}, Shift {
			public boolean isLoopOp(FlowValue<?,?> op) {return op.isShift();}
			public <P,O> FlowValue<P,O> getFlowValue(int depth) {
				return FlowValue.<P,O>createShift(depth);
			}
		};
		
		public static PEGLoopOp getLoopOp(FlowValue<?,?> op) {
			if (op.isTheta())
				return Theta;
			else if (op.isEval())
				return Eval;
			else if (op.isPass())
				return Pass;
			else if (op.isShift())
				return Shift;
			else
				throw new IllegalArgumentException();
		}
		
		public abstract boolean isLoopOp(FlowValue<?,?> op);
		
		public abstract <P,O> FlowValue<P,O> getFlowValue(int depth);
	}
	
	private static abstract class Node<O> extends Network.Node {
		protected Node(Vertex<NetworkLabel> vertex) {super(vertex);}
	}
	
	public static abstract class LoopNode extends Node {
		protected LoopNode(Vertex<NetworkLabel> vertex) {super(vertex);}
		
		public boolean isOpLoop() {return false;}
		public OpLoopNode getOpLoop() {
			throw new UnsupportedOperationException();
		}
	}
	
	public static final class OpLoopNode extends LoopNode {
		private static final Tag<OpLoopNode> mTag
				= new EmptyTag<OpLoopNode>();
		
		private final TermValueNode mTermValue;
		
		protected OpLoopNode(TermValueNode termValue) {
			super(termValue.getVertex());
			termValue.setTag(mTag, this);
			mTermValue = termValue;
		}
		
		public boolean isOpLoop() {return true;}
		public OpLoopNode getOpLoop() {return this;}
		
		public TermValueNode getTermValue() {return mTermValue;}
	}
	
	public static abstract class PEGNode<O> extends Node<O> {
		protected PEGNode(Vertex<NetworkLabel> vertex) {super(vertex);}
		
		public boolean isExpression() {return false;}
		public <P> ExpressionNode<FlowValue<P,O>> getExpression() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isCheckEqualLoopDepths() {return false;}
		public CheckEqualLoopDepthsNode<O> getCheckEqualLoopDepths() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isCheckDistinctLoopDepths() {return false;}
		public CheckDistinctLoopDepthsNode<O> getCheckDistinctLoopDepths() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isIsInvariant() {return false;}
		public IsInvariantNode<O> getIsInvariant() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isOpIsExtendedDomainOp() {return false;}
		public OpIsExtendedDomainOpNode<O> getOpIsExtendedDomainOp() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isOpIsLoopOp() {return false;}
		public OpIsLoopOpNode<O> getOpIsLoopOp() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isOpIsLoopLifted() {return false;}
		public OpIsLoopLiftedNode<O> getOpIsLoopLifted() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isOpIsAllLoopLifted() {return false;}
		public OpIsAllLoopLiftedNode<O> getOpIsAllLoopLifted() {
			throw new UnsupportedOperationException();
		}
	}
	
	private static final class AdaptExpressionNode<O> extends PEGNode<O> {
		private static final Tag<AdaptExpressionNode> mTag
				= new EmptyTag<AdaptExpressionNode>();
		
		private final ExpressionNode<FlowValue<?,O>> mExpression;
		
		protected AdaptExpressionNode(
				ExpressionNode<FlowValue<?,O>> expression) {
			super(expression.getVertex());
			expression.setTag(mTag, this);
			mExpression = expression;
		}
		
		public boolean isExpression() {return true;}
		public <P> ExpressionNode<FlowValue<P,O>> getExpression() {
			return (ExpressionNode<FlowValue<P,O>>)(ExpressionNode)mExpression;
		}
	}
	
	public static final class CheckEqualLoopDepthsNode<O> extends PEGNode<O> {
		private static final Tag<CheckEqualLoopDepthsNode> mTag
				= new EmptyTag<CheckEqualLoopDepthsNode>();
		
		private final LoopNode mLeft, mRight;
		private final PEGNode<O> mInput;
		
		protected CheckEqualLoopDepthsNode(Vertex<NetworkLabel> vertex,
				LoopNode left, LoopNode right, PEGNode<O> input) {
			super(vertex);
			vertex.setTag(mTag, this);
			mLeft = left;
			mRight = right;
			mInput = input;
		}
		
		public boolean isCheckEqualLoopDepths() {return true;}
		public CheckEqualLoopDepthsNode<O> getCheckEqualLoopDepths() {
			return this;
		}
		
		public LoopNode getLeft() {return mLeft;}
		public LoopNode getRight() {return mRight;}
		public PEGNode<O> getInput() {return mInput;}
	}
	
	public static final class CheckDistinctLoopDepthsNode<O> extends PEGNode<O> {
		private static final Tag<CheckDistinctLoopDepthsNode> mTag
				= new EmptyTag<CheckDistinctLoopDepthsNode>();
		
		private final LoopNode mLeft, mRight;
		private final PEGNode<O> mInput;
		
		protected CheckDistinctLoopDepthsNode(Vertex<NetworkLabel> vertex,
				LoopNode left, LoopNode right, PEGNode<O> input) {
			super(vertex);
			vertex.setTag(mTag, this);
			mLeft = left;
			mRight = right;
			mInput = input;
		}
		
		public boolean isCheckDistinctLoopDepths() {return true;}
		public CheckDistinctLoopDepthsNode<O> getCheckDistinctLoopDepths() {
			return this;
		}
		
		public LoopNode getLeft() {return mLeft;}
		public LoopNode getRight() {return mRight;}
		public PEGNode<O> getInput() {return mInput;}
	}
	
	public static final class IsInvariantNode<O> extends PEGNode<O> {
		private static final Tag<IsInvariantNode> mTag
				= new EmptyTag<IsInvariantNode>();
		
		private final ValueNode mValue;
		private final LoopNode mLoop;
		private final PEGNode<O> mInput;
		
		protected IsInvariantNode(Vertex<NetworkLabel> vertex,
				ValueNode value, LoopNode loop, PEGNode<O> input) {
			super(vertex);
			vertex.setTag(mTag, this);
			mValue = value;
			mLoop = loop;
			mInput = input;
		}
		
		public boolean isIsInvariant() {return true;}
		public IsInvariantNode<O> getIsInvariant() {return this;}
		
		public ValueNode getValue() {return mValue;}
		public LoopNode getLoop() {return mLoop;}
		public PEGNode<O> getInput() {return mInput;}
	}
	
	public static final class OpIsExtendedDomainOpNode<O> extends PEGNode<O> {
		private static final Tag<OpIsExtendedDomainOpNode> mTag
				= new EmptyTag<OpIsExtendedDomainOpNode>();
		
		private final TermValueNode mTerm;
		private final PEGNode<O> mInput;
		
		protected OpIsExtendedDomainOpNode(Vertex<NetworkLabel> vertex,
				TermValueNode term, PEGNode<O> input) {
			super(vertex);
			vertex.setTag(mTag, this);
			mTerm = term;
			mInput = input;
		}
		
		public boolean isOpIsExtendedDomainOp() {return true;}
		public OpIsExtendedDomainOpNode<O> getOpIsExtendedDomainOp() {
			return this;
		}
		
		public TermValueNode getTerm() {return mTerm;}
		public PEGNode<O> getInput() {return mInput;}
	}
	
	public static final class OpIsLoopOpNode<O> extends PEGNode<O> {
		private static final Tag<OpIsLoopOpNode> mTag
				= new EmptyTag<OpIsLoopOpNode>();
		
		private final TermValueNode mTerm;
		private final PEGLoopOp mOp;
		private final PEGNode<O> mInput;
		
		protected OpIsLoopOpNode(Vertex<NetworkLabel> vertex,
				TermValueNode term, PEGLoopOp op, PEGNode<O> input) {
			super(vertex);
			vertex.setTag(mTag, this);
			mTerm = term;
			mOp = op;
			mInput = input;
		}
		
		public boolean isOpIsLoopOp() {return true;}
		public OpIsLoopOpNode<O> getOpIsLoopOp() {return this;}
		
		public TermValueNode getTerm() {return mTerm;}
		public PEGLoopOp getOp() {return mOp;}
		public PEGNode<O> getInput() {return mInput;}
	}
	
	public static final class OpIsLoopLiftedNode<O> extends PEGNode<O> {
		private static final Tag<OpIsLoopLiftedNode> mTag
				= new EmptyTag<OpIsLoopLiftedNode>();
		
		private final TermValueNode mTerm;
		private final LoopNode mLoop;
		private final PEGNode<O> mInput;
		
		protected OpIsLoopLiftedNode(Vertex<NetworkLabel> vertex,
				TermValueNode term, LoopNode loop, PEGNode<O> input) {
			super(vertex);
			vertex.setTag(mTag, this);
			mTerm = term;
			mLoop = loop;
			mInput = input;
		}
		
		public boolean isOpIsLoopLifted() {return true;}
		public OpIsLoopLiftedNode<O> getOpIsLoopLifted() {return this;}
		
		public TermValueNode getTerm() {return mTerm;}
		public LoopNode getLoop() {return mLoop;}
		public PEGNode<O> getInput() {return mInput;}
	}
	
	public static final class OpIsAllLoopLiftedNode<O> extends PEGNode<O> {
		private static final Tag<OpIsAllLoopLiftedNode> mTag
				= new EmptyTag<OpIsAllLoopLiftedNode>();
		
		private final TermValueNode mTerm;
		private final PEGNode<O> mInput;
		
		protected OpIsAllLoopLiftedNode(Vertex<NetworkLabel> vertex,
				TermValueNode term, PEGNode<O> input) {
			super(vertex);
			vertex.setTag(mTag, this);
			mTerm = term;
			mInput = input;
		}
		
		public boolean isOpIsAllLoopLifted() {return true;}
		public OpIsAllLoopLiftedNode<O> getOpIsAllLoopLifted() {return this;}
		
		public TermValueNode getTerm() {return mTerm;}
		public PEGNode<O> getInput() {return mInput;}
	}
	
	public PEGNetwork(Network network) {super(network);}
	
	public OpLoopNode opLoop(TermValueNode termValue) {
		OpLoopNode node = termValue.getTag(OpLoopNode.mTag);
		return node == null ? new OpLoopNode(termValue) : node;
	}
	
	public PEGNode<O> adaptExpression(
			ExpressionNode<FlowValue<?,O>> expression) {
		AdaptExpressionNode<O> node
				= expression.getTag(AdaptExpressionNode.mTag);
		return node == null ? new AdaptExpressionNode<O>(expression) : node;
	}
	
	public CheckEqualLoopDepthsNode<O> checkEqualLoopDepths(
			LoopNode left, LoopNode right, PEGNode<O> input) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(CheckEqualLoopDepths,
				left.getVertex(), right.getVertex(), input.getVertex());
		CheckEqualLoopDepthsNode<O> node
				= vertex.getTag(CheckEqualLoopDepthsNode.mTag);
		return node == null
				? new CheckEqualLoopDepthsNode<O>(vertex, left, right, input)
				: node;
	}
	
	public CheckDistinctLoopDepthsNode<O> checkDistinctLoopDepths(
			LoopNode left, LoopNode right, PEGNode<O> input) {
		Vertex<NetworkLabel> vertex
				= getGraph().getVertex(CheckDistinctLoopDepths,
				left.getVertex(), right.getVertex(), input.getVertex());
		CheckDistinctLoopDepthsNode<O> node
				= vertex.getTag(CheckDistinctLoopDepthsNode.mTag);
		return node == null
				? new CheckDistinctLoopDepthsNode<O>(vertex, left, right, input)
				: node;
	}
	
	public IsInvariantNode<O> isInvariant(ValueNode value, LoopNode loop,
			PEGNode<O> input) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(IsInvariant,
				value.getVertex(), loop.getVertex(), input.getVertex());
		IsInvariantNode<O> node = vertex.getTag(IsInvariantNode.mTag);
		return node == null
				? new IsInvariantNode<O>(vertex, value, loop, input) : node;
	}
	
	public OpIsExtendedDomainOpNode<O> opIsExtendedDomainOp(TermValueNode term,
			PEGNode<O> input) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(OpIsExtendedDomainOp,
				term.getVertex(), input.getVertex());
		OpIsExtendedDomainOpNode<O> node
				= vertex.getTag(OpIsExtendedDomainOpNode.mTag);
		return node == null
				? new OpIsExtendedDomainOpNode<O>(vertex, term, input) : node;
	}
	
	public OpIsLoopOpNode<O> opIsLoopOp(TermValueNode term, PEGLoopOp op,
			PEGNode<O> input) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(OpIsLoopOp,
				term.getVertex(), getGraph().getVertex(op), input.getVertex());
		OpIsLoopOpNode<O> node = vertex.getTag(OpIsLoopOpNode.mTag);
		return node == null
				? new OpIsLoopOpNode<O>(vertex, term, op, input) : node;
	}
	
	public OpIsLoopLiftedNode<O> opIsLoopLifted(TermValueNode term,
			LoopNode loop, PEGNode<O> input) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(OpIsLoopLifted,
				term.getVertex(), loop.getVertex(), input.getVertex());
		OpIsLoopLiftedNode<O> node = vertex.getTag(OpIsLoopLiftedNode.mTag);
		return node == null
				? new OpIsLoopLiftedNode<O>(vertex, term, loop, input) : node;
	}
	
	public OpIsAllLoopLiftedNode<O> opIsAllLoopLifted(TermValueNode term,
			PEGNode<O> input) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(OpIsAllLoopLifted,
				term.getVertex(), input.getVertex());
		OpIsAllLoopLiftedNode<O> node
				= vertex.getTag(OpIsAllLoopLiftedNode.mTag);
		return node == null
				? new OpIsAllLoopLiftedNode<O>(vertex, term, input) : node;
	}
}
