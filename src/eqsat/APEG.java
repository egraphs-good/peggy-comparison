package eqsat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.AbstractVariaticFunction;
import util.Function;
import util.Tag;
import util.graph.CRecursiveExpressionGraph;
import util.graph.CRecursiveExpressionGraph.Vertex;
import util.mapped.MappedList;

public final class APEG<G, B, V, L, P> {
	protected abstract class Label {
		public APEG<G,B,V,L,P> getAPEG() {return APEG.this;}
		public boolean isNegate() {return false;}
		public abstract Vertex<FlowValue<P,L>> evaluate(Vertex<Label> node,
				V variable);
		public boolean isConstant() {return false;}
		public Vertex<FlowValue<P,L>> evaluate(Vertex<Label> node) {
			throw new UnsupportedOperationException();
		}
		public abstract String toString();
	}
	protected abstract class CachedLabel extends Label
			implements Tag<Map<V,Vertex<FlowValue<P,L>>>> {
		public Vertex<FlowValue<P,L>> evaluate(Vertex<Label> node,
				V variable) {
			Map<V,Vertex<FlowValue<P,L>>> cache = node.getTag(this);
			if (cache == null) {
				cache = new HashMap<V,Vertex<FlowValue<P,L>>>();
				node.setTag(this, cache);
			}
			Vertex<FlowValue<P,L>> value = cache.get(variable);
			if (value == null) {
				if (cache.containsKey(variable))
					value = mValues.createPlaceHolder();
				else {
					cache.put(variable, null);
					value = evaluateUncached(node, variable);
				}
				Vertex<FlowValue<P,L>> holder = cache.put(variable, value);
				if (holder != null)
					holder.replaceWith(value);
			}
			return value;
		}
		public abstract Vertex<FlowValue<P,L>> evaluateUncached(
				Vertex<Label> node, V variable);
	}
	protected abstract class ConstantLabel extends Label
			implements Tag<Vertex<FlowValue<P,L>>> {
		public Vertex<FlowValue<P,L>> evaluate(Vertex<Label> node,
				V variable) {
			return evaluate(node);
		}
		public Vertex<FlowValue<P,L>> evaluate(Vertex<Label> node) {
			Vertex<FlowValue<P,L>> value = node.getTag(this);
			if (value == null) {
				value = evaluateUncached(node);
				node.setTag(this, value);
			}
			return value;
		}
		public abstract Vertex<FlowValue<P,L>> evaluateUncached(
				Vertex<Label> node);
		public boolean isConstant() {return true;}
	}
	
	private abstract class BlockLabel extends Label {
		public final Function<V,Vertex<FlowValue<P,L>>> mBlock;
		public BlockLabel(Function<V,Vertex<FlowValue<P,L>>> block) {
			mBlock = block;
		}
		public Vertex<FlowValue<P,L>> evaluate(Vertex<Label> node, V variable) {
			return mBlock.get(variable);
		}
		public abstract String toString();
	}
	private final class CheckUnmodifiedLabel extends CachedLabel {
		protected final Set<? super V> mUnmodified;
		public CheckUnmodifiedLabel(Set<? super V> unmodified) {
			mUnmodified = unmodified;
		}
		public Vertex<FlowValue<P,L>> evaluateUncached(
				Vertex<Label> node, V variable) {
			if (mUnmodified.contains(variable))
				return APEG.this.evaluate(node.getChild(0), variable);
			else
				return APEG.this.evaluate(node.getChild(1), variable);
		}
		public boolean equals(Object that) {
			return that instanceof APEG.CheckUnmodifiedLabel
					&& ((CheckUnmodifiedLabel)that).getAPEG().equals(getAPEG())
					&& ((CheckUnmodifiedLabel)that).mUnmodified
					.equals(mUnmodified);
		}
		public int hashCode() {return mUnmodified.hashCode();}
		public String toString() {return "Check Unmodified: " + mUnmodified;}
	}
	private interface FlowValueLabel<P, L> {
		APEG<?,?,?,L,P> getAPEG();
		FlowValue<P,L> getValue();
	}
	private class CachedFlowValueLabel extends CachedLabel
			implements FlowValueLabel<P,L> {
		protected final FlowValue<P,L> mValue;
		public CachedFlowValueLabel(FlowValue<P,L> value) {mValue = value;}
		public FlowValue<P,L> getValue() {return mValue;}
		public boolean isNegate() {return mValue.isNegate();}
		public Vertex<FlowValue<P,L>> evaluateUncached(
				Vertex<Label> node, V variable) {
			if (node.getChildCount() == 1)
				return mValues.getVertex(mValue,
						APEG.this.evaluate(node.getChild(0), variable));
			Vertex<FlowValue<P,L>>[] children
					= new Vertex[node.getChildCount()];
			for (int i = node.getChildCount(); i-- != 0; )
				children[i] = APEG.this.evaluate(node.getChild(i), variable);
			return mValues.getVertex(mValue, children);
		}
		public boolean equals(Object that) {
			return that instanceof APEG.FlowValueLabel
					&& ((FlowValueLabel)that).getAPEG().equals(getAPEG())
					&& ((FlowValueLabel<P,L>)that).getValue().equals(mValue);
		}
		public int hashCode() {return mValue.hashCode();}
		public String toString() {return mValue.toString();}
	}
	private final class ConstantFlowValueLabel extends ConstantLabel
			implements FlowValueLabel<P,L>  {
		protected final FlowValue<P,L> mValue;
		public ConstantFlowValueLabel(FlowValue<P,L> value) {mValue = value;}
		public FlowValue<P,L> getValue() {return mValue;}
		public boolean isNegate() {return mValue.isNegate();}
		public Vertex<FlowValue<P,L>> evaluateUncached(Vertex<Label> node) {
			if (node.isLeaf())
				return mValues.getVertex(mValue);
			if (node.getChildCount() == 1)
				return mValues.getVertex(mValue,
						APEG.this.evaluate(node.getChild(0)));
			Vertex<FlowValue<P,L>>[] children
					= new Vertex[node.getChildCount()];
			for (int i = node.getChildCount(); i-- != 0; )
				children[i] = APEG.this.evaluate(node.getChild(i));
			return mValues.getVertex(mValue, children);
		}
		public boolean equals(Object that) {
			return that instanceof APEG.FlowValueLabel
					&& ((FlowValueLabel)that).getAPEG().equals(getAPEG())
					&& ((FlowValueLabel<P,L>)that).getValue().equals(mValue);
		}
		public int hashCode() {return mValue.hashCode();}
		public String toString() {return mValue.toString();}
	}
	
	public class Node {
		protected final Vertex<Label> mVertex;
		private Node(Vertex<Label> vertex) {mVertex = vertex;}
		public APEG<G,B,V,L,P> getAPEG() {return APEG.this;}
		public boolean equals(Object that) {
			return that instanceof APEG.Node
					&& ((Node)that).mVertex.equals(mVertex);
		}
		public int hashCode() {return mVertex.hashCode();}
		public String toString() {return mVertex.toString();}
		private Node getNegated() {
			return !mVertex.isPlaceHolder() && mVertex.getLabel().isNegate()
					? new Node(mVertex.getChild(0)) : null;
		}
		private boolean isConstant() {return mVertex.getLabel().isConstant();}
		public Vertex<FlowValue<P,L>> evaluate(V variable) {
			return APEG.this.evaluate(mVertex, variable);
		}
		public Vertex<FlowValue<P,L>> evaluateSignificant(V variable) {
			Vertex<FlowValue<P,L>> value = evaluate(variable);
			value.makeSignificant();
			return value;
		}
	}
	public final class BlockNode extends Node {
		private BlockNode(Vertex<Label> vertex) {super(vertex);}
		public void setChild(Node child) {
			mVertex.getChild(0).replaceWith(child.mVertex);
		}
	}
	
	protected final CRecursiveExpressionGraph<Label> mGraph
			= new CRecursiveExpressionGraph<Label>();
	protected final Map<B,BlockNode> mBlocks
			= new HashMap<B,BlockNode>();
	protected final Node mParameters = new Node(mGraph.getVertex(
			new CachedLabel() {
		public Vertex<FlowValue<P,L>> evaluateUncached(Vertex<Label> node,
				V variable) {
			return mValues.getVertex(FlowValue.<P,L>createParameter(
					mCFG.getParameter(variable)));
		}
		public String toString() {return "Parameters";}
	}));
	protected final Label mOutput = new CachedLabel() {
		public Vertex<FlowValue<P,L>> evaluateUncached(Vertex<Label> node,
				V variable) {
			return APEG.this.evaluate(node.getChild(0), variable);
		}
		public String toString() {return "Output";}
	};
	protected final Label mBranchCondition = new ConstantLabel() {
		public Vertex<FlowValue<P,L>> evaluateUncached(Vertex<Label> node) {
			return APEG.this.evaluate(node.getChild(0), null);
		}
		public String toString() {return "Branch Condition";}
	};
	protected final Node mTrue = getFlowValue(FlowValue.<P,L>createTrue());
	protected final Node mFalse = getFlowValue(FlowValue.<P,L>createFalse());
	
	protected final CFG<?,B,V,?,P,?> mCFG;
	protected final CFGTranslator<B,V,Vertex<FlowValue<P,L>>> mTranslator;
	protected final CRecursiveExpressionGraph<FlowValue<P,L>> mValues
			= new CRecursiveExpressionGraph<FlowValue<P,L>>();
	protected final Set<Vertex<FlowValue<P,L>>> mKnown
			= new HashSet<Vertex<FlowValue<P,L>>>();
	protected final Node mReturn;
	
	public APEG(CFG<?,B,V,L,P,?> cfg) {
		mCFG = cfg;
		final OpAmbassador<? super L> ambassador = cfg.getOpAmbassador();
		mTranslator = cfg.getTranslator(
				new Function<P,Vertex<FlowValue<P,L>>>() {
			public Vertex<FlowValue<P,L>> get(P parameter) {
				return mValues.getVertex(
						FlowValue.<P,L>createParameter(parameter));
			}
		}, new AbstractVariaticFunction
				<L,Vertex<FlowValue<P,L>>,Vertex<FlowValue<P,L>>>() {
			public Vertex<FlowValue<P,L>> get(L label,
					List<? extends Vertex<FlowValue<P,L>>> children) {
				return mValues.getVertex(
						FlowValue.<P,L>createDomain(label, ambassador),
						children);
			}
		}, mKnown);
		mReturn = getOutput(mCFG.getEnd());
		mReturn.mVertex.makeSignificant();
	}
	
	public Node getReturn() {return mReturn;}
	public CRecursiveExpressionGraph<FlowValue<P,L>> getValues() {
		return mValues;
	}
	
	public Node getParameters() {return mParameters;}
	
	public BlockNode getBlock(final B block) {
		BlockNode node = mBlocks.get(block);
		if (node == null) {
			final Vertex<Label> input = mGraph.createPlaceHolder();
			node = new BlockNode(mGraph.getVertex(new BlockLabel(
					mTranslator.getOutputs(block,
					new Function<V,Vertex<FlowValue<P,L>>>() {
						public Vertex<FlowValue<P,L>> get(V variable) {
							return evaluate(input, variable);
						}
					})) {
				public String toString() {return block.toString();}
			}, input));
			mBlocks.put(block, node);
		}
		return node;
	}
	
	public Node getOutput(B block) {
		return new Node(mGraph.getVertex(mOutput, getBlock(block).mVertex));
	}
	
	public Node getBranchCondition(B block) {
		return new Node(mGraph.getVertex(mBranchCondition,
				getBlock(block).mVertex));
	}
	
	public Node getCheckUnmodified(Set<? super V> unmodifieds,
			Node unmodified, Node modified) {
		return new Node(mGraph.getVertex(new CheckUnmodifiedLabel(unmodifieds),
				unmodified.mVertex, modified.mVertex));
	}
	
	private Node getFlowValue(FlowValue<P,L> value, Node... children) {
		boolean constant = true;
		for (Node child : children)
			constant &= child.isConstant();
		return getNode(constant ? new ConstantFlowValueLabel(value)
				: new CachedFlowValueLabel(value), children);
	}
	
	private Node getNode(Label label, Node... children) {
		return new Node(mGraph.getVertex(label, getVertices(children)));
	}
	
	public Node getTheta(int depth, Node base, Node next) {
		return getFlowValue(FlowValue.<P,L>createTheta(depth), base, next);
	}
	public Node getPass(int depth, Node condition) {
		return getFlowValue(FlowValue.<P,L>createPass(depth), condition);
	}
	public Node getEval(int depth, Node value, Node index) {
		return getFlowValue(FlowValue.<P,L>createEval(depth), value, index);
	}
	
	private Vertex<FlowValue<P,L>> getNegate(Vertex<FlowValue<P,L>> child) {
		if (child.isPlaceHolder())
			;
		else if (child.getLabel().isNegate())
			return child.getChild(0);
		else if (child.getLabel().isTrue())
			return mValues.getVertex(FlowValue.<P,L>createFalse());
		else if (child.getLabel().isFalse())
			return mValues.getVertex(FlowValue.<P,L>createTrue());
		return mValues.getVertex(FlowValue.<P,L>createNegate(), child);
	}
	
	private Node getNegate(Node condition) {
		return new Node(mGraph.getVertex(
				new CachedFlowValueLabel(FlowValue.<P,L>createNegate()) {
			public Vertex<FlowValue<P,L>> evaluateUncached(Vertex<Label> node,
					V variable) {
				return getNegate(
						APEG.this.evaluate(node.getChild(0), variable));
			}
		}, condition.mVertex));
	}
	
	private Vertex<FlowValue<P,L>> getShortCircuitAnd(
			Vertex<FlowValue<P,L>> left, Vertex<FlowValue<P,L>> right) {
		if (left.equals(right))
			return left;
		if (left.isPlaceHolder()) {
			if (right.isPlaceHolder())
				;
			else if (right.getLabel().isNegate()
					&& left.equals(right.getChild(0)))
				return mValues.getVertex(FlowValue.<P,L>createFalse());
		}
		else if (left.getLabel().isTrue())
			return right;
		else if (left.getLabel().isFalse())
			return mValues.getVertex(FlowValue.<P,L>createFalse());
		else if (left.getLabel().isNegate()) {
			if (left.equals(right.getChild(0)))
				return mValues.getVertex(FlowValue.<P,L>createFalse());
			else
				return getNegate(getShortCircuitOr(left.getChild(0),
						getNegate(right)));
		}
		else if (right.isPlaceHolder())
			;
		else if (right.getLabel().isTrue())
			return left;
		else if (right.getLabel().isFalse())
			return mValues.getVertex(FlowValue.<P,L>createFalse());
		return mValues.getVertex(FlowValue.<P,L>createShortCircuitAnd(),
				left, right);
	}
	
	private Node getShortCircuitAnd(Node left, Node right) {
		return new Node(mGraph.getVertex(
				new CachedFlowValueLabel(FlowValue.<P,L>createShortCircuitAnd())
				{
			public Vertex<FlowValue<P,L>> evaluateUncached(Vertex<Label> node,
					V variable) {
				return getShortCircuitAnd(
						APEG.this.evaluate(node.getChild(0), variable),
						APEG.this.evaluate(node.getChild(1), variable));
			}
		}, left.mVertex, right.mVertex));
	}
	
	private Vertex<FlowValue<P,L>> getShortCircuitOr(
			Vertex<FlowValue<P,L>> left, Vertex<FlowValue<P,L>> right) {
		if (left.equals(right))
			return left;
		if (left.isPlaceHolder()) {
			if (right.isPlaceHolder())
				;
			else if (right.getLabel().isNegate()
					&& left.equals(right.getChild(0)))
				return mValues.getVertex(FlowValue.<P,L>createTrue());
		}
		else if (left.getLabel().isTrue())
			return mValues.getVertex(FlowValue.<P,L>createTrue());
		else if (left.getLabel().isFalse())
			return right;
		else if (left.getLabel().isNegate()) {
			if (left.equals(right.getChild(0)))
				return mValues.getVertex(FlowValue.<P,L>createTrue());
			else
				return getNegate(getShortCircuitAnd(left.getChild(0),
						getNegate(right)));
		}
		else if (right.isPlaceHolder())
			;
		else if (right.getLabel().isTrue())
			return mValues.getVertex(FlowValue.<P,L>createTrue());
		else if (right.getLabel().isFalse())
			return left;
		return mValues.getVertex(FlowValue.<P,L>createShortCircuitOr(),
				left, right);
	}
	
	private Node getShortCircuitOr(Node left, Node right) {
		return new Node(mGraph.getVertex(
				new CachedFlowValueLabel(FlowValue.<P,L>createShortCircuitOr())
				{
			public Vertex<FlowValue<P,L>> evaluateUncached(Vertex<Label> node,
					V variable) {
				return getShortCircuitOr(
						APEG.this.evaluate(node.getChild(0), variable),
						APEG.this.evaluate(node.getChild(1), variable));
			}
		}, left.mVertex, right.mVertex));
	}
	
	public Vertex<FlowValue<P,L>> getPhi(Vertex<FlowValue<P,L>> condition,
			Vertex<FlowValue<P,L>> left, Vertex<FlowValue<P,L>> right) {
		if (left.equals(right))
			return left;
		/*else if (condition.equals(left))
			return getShortCircuitOr(condition, right);
		else if (condition.equals(right))
			return getShortCircuitAnd(condition, left);*/
		else if (condition.isPlaceHolder()) {
			/*if (left.isPlaceHolder()) {
				if (right.isPlaceHolder())
					;
				else if (right.getLabel().isTrue())
					return getNegate(getShortCircuitAnd(condition,
							getNegate(left)));
				else if (right.getLabel().isFalse())
					return getShortCircuitAnd(condition, left);
			} else if (left.getLabel().isTrue())
				return getShortCircuitOr(condition, right);
			else if (left.getLabel().isFalse())
				return getNegate(getShortCircuitOr(condition,
						getNegate(right)));*/
		}
		else if (condition.getLabel().isTrue())
			return left;
		else if (condition.getLabel().isFalse())
			return right;
		else if (condition.getLabel().isNegate())
			return getPhi(condition.getChild(0), right, left);
		/*else if (left.isPlaceHolder()) {
			if (right.isPlaceHolder())
				;
			else if (right.getLabel().isTrue())
				return getNegate(getShortCircuitAnd(condition,
						getNegate(left)));
			else if (right.getLabel().isFalse())
				return getShortCircuitAnd(condition, left);
		}
		else if (left.getLabel().isTrue())
			return getShortCircuitOr(condition, right);
		else if (left.getLabel().isFalse())
			return getNegate(getShortCircuitOr(condition, getNegate(right)));
		else if (right.isPlaceHolder())
			;
		else if (right.getLabel().isTrue())
			return getNegate(getShortCircuitAnd(condition, getNegate(left)));
		else if (right.getLabel().isFalse())
			return getShortCircuitAnd(condition, left);*/
		return mValues.getVertex(FlowValue.<P,L>createPhi(), condition,
				left, right);
	}
	
	private Node getPhi(Node condition, Node left, Node right) {
		return new Node(mGraph.getVertex(
				new CachedFlowValueLabel(FlowValue.<P,L>createPhi()) {
			public Vertex<FlowValue<P,L>> evaluateUncached(Vertex<Label> node,
					V variable) {
				return getPhi(
						APEG.this.evaluate(node.getChild(0), variable),
						APEG.this.evaluate(node.getChild(1), variable),
						APEG.this.evaluate(node.getChild(2), variable));
			}
		}, condition.mVertex, left.mVertex, right.mVertex));
	}
	
	public Node getPhiEquivalent(Node condition, Node left, Node right) {
		if (left.equals(right))
			return left;
		if (condition.getNegated() != null)
			return getPhiEquivalent(condition.getNegated(), right, left);
		if (mTrue.equals(condition))
			return left;
		if (mFalse.equals(condition))
			return right;
		if (mTrue.equals(left) && mFalse.equals(right))
			return condition;
		if (mFalse.equals(left) && mTrue.equals(right))
			return getNegate(condition);
		if (mTrue.equals(left))
			return getShortCircuitOr(condition, right);
		if (mFalse.equals(left))
			return getNegate(getShortCircuitOr(condition,
					right.getNegated() != null ? right.getNegated()
					: getNegate(right)));
		if (mTrue.equals(right))
			return getNegate(getShortCircuitAnd(condition,
					left.getNegated() != null ? left.getNegated()
					: getNegate(left)));
		if (mFalse.equals(right))
			return getShortCircuitAnd(condition, left);
		return getPhi(condition, left, right);
	}
	
	public Node getTrue() {return mTrue;}
	public Node getFalse() {return mFalse;}
	
	protected final List<Vertex<Label>> getVertices(
			final Node... nodes) {
		return new MappedList<Node,Vertex<Label>>() {
			public List<Node> getWrapped() {return Arrays.asList(nodes);}
			public Vertex<Label> map(Node node) {return node.mVertex;}
		};
	}
	
	protected Vertex<FlowValue<P,L>> evaluate(Vertex<Label> node, V variable) {
		return node.getLabel().evaluate(node, variable);
	}
	
	protected Vertex<FlowValue<P,L>> evaluate(Vertex<Label> node) {
		return node.getLabel().evaluate(node);
	}
	
	public String toString() {return mGraph.toString();}
}
