package eqsat.revert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.OpExpression;
import util.BackedMultiMap;
import util.MultiMap;
import util.UnhandledCaseException;
import util.integer.ArrayIntMap;
import util.integer.BitIntSet;
import util.integer.IncreasingIntMap;
import util.integer.IncreasingIntSet;
import util.integer.IntCollections;
import util.integer.IntIterator;
import util.integer.IntMap;
import util.integer.IntSet;
import util.integer.PairInt;

public abstract class Value<P, L> {
	public static <P,L> Value<P,L> getValue(final FlowValue<P,L> value) {
		return new BasicFlowValue<P,L>(value);
	}
	
	public static <P,L> BlockValue<P,L> getLoop(FallBlock<P,L> init,
			BranchBlock<P,L> body, List<Variable> inputs,
			IncreasingIntSet volatil) {
		return new LoopBlockValue<P,L>(init, body, inputs, volatil);
	}
	
	public static <P,L> BlockValue<P,L> getBranch(final BranchBlock<P,L> branch,
			List<Variable> inputs, IncreasingIntSet volatil) {
		return new BranchBlockValue<P,L>(branch, inputs, volatil);
	}
	
	public boolean isVariable() {return false;}
	public Variable getVariable() {
		throw new UnsupportedOperationException();
	}
	public boolean isLoop() {return false;}
	public FallBlock<P,L> getInitializer() {
		throw new UnsupportedOperationException();
	}
	public BranchBlock<P,L> getBody() {
		throw new UnsupportedOperationException();
	}
	public Variable getPassVariable() {
		throw new UnsupportedOperationException();
	}
	public boolean isProject() {return false;}
	public boolean isBranch() {return false;}
	public BranchBlock<P,L> getBranch() {
		throw new UnsupportedOperationException();
	}
	public boolean isBlock() {return false;}
	public BlockValue<P,L> getBlockSelf() {
		throw new UnsupportedOperationException();
	}
	
	public boolean isFree(OpAmbassador<L> ambassador) {return false;}
	
	public abstract boolean isAnyVolatile(OpAmbassador<L> ambassador);
	public abstract boolean isVolatile(OpAmbassador<L> ambassador, int child);
	
	public void getUses(ReversionGraph<P,L>.Vertex vertex, int child,
			Collection<? super PairInt<OpExpression<L>>> uses) {
		throw new UnsupportedOperationException();
	}
	
	public Value<P,L> getChainVersion(OpAmbassador<L> ambassador, int child) {
		throw new UnsupportedOperationException();
	}
	public Value<P,L> getChainProjectVolatile(OpAmbassador<L> ambassador,
			int child) {
		throw new UnsupportedOperationException();
	}
	public Value<P,L> getChainProjectValue(OpAmbassador<L> ambassador,
			int child) {
		throw new UnsupportedOperationException();
	}
	
	public boolean isEquivalent(ReversionGraph<P,L>.Vertex vertex, int child,
			ReversionGraph<P,L>.Vertex that, int thatChild) {
		throw new UnsupportedOperationException();
	}
	
	public abstract boolean canPreEvaluate(OpAmbassador<L> ambassador);
	public boolean canBeInlined() {return false;}
	
	public abstract boolean containsEval();
	public abstract boolean containsPhi();
	
	public void getVariance(BitIntSet variance,
			List<? extends BitIntSet> children) {
		for (BitIntSet child : children)
			variance.addAll(child);
	}
	public boolean considerVariant(int loop, int child) {return true;}
	
	public ReversionGraph<P,L>.Vertex getShift(
			final ReversionGraph<P,L>.Vertex vertex, final int loop) {
		ReversionGraph<P,L>.Vertex[] children
				= new ReversionGraph.Vertex[vertex.getChildCount()];
		for (int i = children.length; i-- != 0; )
			children[i] = vertex.getChild(i).getShift(loop);
		return vertex.getGraph().getVertex(this, children);
	}
	public ReversionGraph<P,L>.Vertex getEval0(
			final ReversionGraph<P,L>.Vertex vertex, final int loop) {
		ReversionGraph<P,L>.Vertex[] children
				= new ReversionGraph.Vertex[vertex.getChildCount()];
		for (int i = children.length; i-- != 0; )
			children[i] = vertex.getChild(i).getEval0(loop);
		return vertex.getGraph().getVertex(this, children);
	}
	
	public abstract ReversionGraph<P,L>.Vertex rewrite(
			ReversionGraph<P,L>.Vertex vertex);
	
	public boolean needsChild(OpAmbassador<L> ambassador, int child) {
		return false;
	}
	
	public FlowValue<P,L> getHead() {return null;}
	public ReversionGraph<P,L>.Vertex getTail(ReversionGraph<P,L>.Vertex vertex)
			{
		throw new UnsupportedOperationException();
	}
	public List<? extends ReversionGraph<P,L>.Vertex> getTails(
			ReversionGraph<P,L>.Vertex vertex) {
		throw new UnsupportedOperationException();
	}
	
	public OpExpression<L> getExpression(ReversionGraph<P,L>.Vertex vertex) {
		return null;
	}
	
	public abstract boolean equals(Value that);
	public boolean equals(Object that) {
		return that instanceof Value && equals((Value)that);
	}
	
	public static abstract class BlockValue<P,L> extends Value<P,L> {
		protected final List<Variable> mInputs;
		protected final IncreasingIntSet mVolatile;
		protected final IntMap<Collection<PairInt<OpExpression<L>>>> mUses
				= new ArrayIntMap();
		protected final IntSet mChained = new BitIntSet();
		
		private BlockValue(List<Variable> inputs, IncreasingIntSet volatil) {
			mInputs = inputs;
			mVolatile = volatil;
		}
		
		public boolean isBlock() {return true;}
		public BlockValue<P,L> getBlockSelf() {return this;}
		
		public abstract boolean modifies(Variable variable);
		public Variable getInput(int child) {return mInputs.get(child);}
		public int getInput(Variable variable) {
			return mInputs.indexOf(variable);
		}
		
		public boolean isAnyVolatile(OpAmbassador<L> ambassador) {
			return !mVolatile.isEmpty();
		}
		public boolean isVolatile(OpAmbassador<L> ambassador, int child) {
			return mVolatile.contains(child);
		}
		
		protected abstract void chain(int child);
		
		public BlockValue<P,L> getChainVersion(OpAmbassador<L> ambassador,
				int child) {
			chain(child);
			return this;
		}
		public Value<P,L> getChainProjectVolatile(OpAmbassador<L> ambassador,
				int child) {
			chain(child);
			return mInputs.get(child).<P,L>getProject();
		}
		public Value<P,L> getChainProjectValue(OpAmbassador<L> ambassador,
				int child) {
			chain(child);
			return null;
		}
		
		public Collection<? extends PairInt<OpExpression<L>>> getUses(
				int child) {
			chain(child);
			return mUses.get(child);
		}
		
		public void getUses(ReversionGraph<P,L>.Vertex vertex, int child,
				Collection<? super PairInt<OpExpression<L>>> uses) {
			uses.addAll(getUses(child));
		}
		
		public boolean isEquivalent(ReversionGraph<P,L>.Vertex vertex,
				int child, ReversionGraph<P,L>.Vertex that, int thatChild) {
			OpAmbassador<L> ambassador = vertex.getGraph().getOpAmbassador();
			if (that.getLabel() instanceof BasicFlowValue) {
				BasicFlowValue<P,L> value
						= (BasicFlowValue<P,L>)that.getLabel();
				if (!value.mValue.isDomain())
					return super.isEquivalent(vertex, child, that, thatChild);
				Collection<? extends PairInt<OpExpression<L>>> uses
						= getUses(child);
				OpExpression<L> expression = that.getExpression();
				for (PairInt<OpExpression<L>> use : uses)
					if (!ambassador.isEquivalent(
							use.getFirst(), use.getSecond(),
							expression, thatChild))
						return false;
				return true;
			} else if (that.getLabel() instanceof BlockValue) {
				BlockValue<P,L> value = (BlockValue<P,L>)that.getLabel();
				Collection<? extends PairInt<OpExpression<L>>> uses
						= getUses(child);
				Collection<? extends PairInt<OpExpression<L>>> thatUses
						= value.getUses(thatChild);
				for (PairInt<OpExpression<L>> use : uses)
					for (PairInt<OpExpression<L>> thatUse : thatUses)
						if (!ambassador.isEquivalent(
								use.getFirst(), use.getSecond(),
								thatUse.getFirst(), thatUse.getSecond()))
							return false;
				return true;
			} else if (that.getLabel() instanceof InlinedFlowValue) {
				InlinedFlowValue<P,L> value
						= (InlinedFlowValue<P,L>)that.getLabel();
				int index = value.adjustChildIndex(thatChild);
				if (!value.mValue.mValue.isDomain() || index < 0)
					return super.isEquivalent(vertex, child, that, thatChild);
				Collection<? extends PairInt<OpExpression<L>>> uses
						= getUses(child);
				OpExpression<L> expression = that.getExpression();
				for (PairInt<OpExpression<L>> use : uses)
					if (!ambassador.isEquivalent(
							use.getFirst(), use.getSecond(),
							expression, index))
						return false;
				return true;
			}
			return super.isEquivalent(vertex, child, that, thatChild);
		}
		
		public boolean containsPhi() {return false;}
		public boolean containsEval() {return false;}
		public boolean canPreEvaluate(OpAmbassador<L> ambassador) {
			return false;
		}
		
		public boolean equals(BlockValue<P,L> that) {
			return mInputs.equals(that.mInputs)
					&& mVolatile.equals(that.mVolatile);
		}
		
		protected abstract Value<P,L> inlineChildren(
				IntMap<? extends Value<P,L>> children);
		
		public ReversionGraph<P,L>.Vertex rewrite(
				ReversionGraph<P,L>.Vertex vertex) {
			return vertex;
			/*boolean constant = false;
			for (int i = vertex.getChildCount(); i-- != 0; )
				if (constant |= vertex.getChild(i).isConstant())
					break;
			if (!constant)
				return vertex;
			IntMap<Value<P,L>> inline = new ArrayIntMap<Value<P,L>>();
			List<ReversionGraph<P,L>.Vertex> children = new ArrayList();
			for (int i = 0; i < vertex.getChildCount(); i++)
				if (vertex.getChild(i).isConstant())
					inline.put(i, vertex.getChild(i).getLabel());
				else
					children.add(vertex.getChild(i));
			return vertex.getGraph().getVertex(inlineChildren(inline),
					children);*/
			//TODO handle case when child is variable other than input
		}
	}
	
	private static final class BranchBlockValue<P,L> extends BlockValue<P,L> {
		private final BranchBlock<P,L> mBranch;
		
		public BranchBlockValue(BranchBlock<P,L> branch,
				List<Variable> inputs, IncreasingIntSet volatil) {
			super(inputs, volatil);
			mBranch = branch;
		}
		
		public boolean isBranch() {return true;}
		public BranchBlock<P,L> getBranch() {return mBranch;}
		
		public boolean modifies(Variable variable) {
			return mBranch.modifies(variable);
		}
		
		protected void chain(int child) {
			if (mChained.contains(child))
				return;
			if (!mVolatile.contains(child))
				throw new IllegalArgumentException();
			Collection<PairInt<OpExpression<L>>> uses = new ArrayList();
			mBranch.chain(mInputs.get(child), uses);
			mUses.put(child, uses);
		}
		
		protected Value<P,L> inlineChildren(
				IntMap<? extends Value<P,L>> children) {
			List<Variable> inputs = new ArrayList<Variable>(
					mInputs.size() - children.size());
			for (int i = 0; i < mInputs.size(); i++)
				if (!children.containsKey(i))
					inputs.add(mInputs.get(i));
			IncreasingIntSet volatil = new BitIntSet();
			for (int i = 0, offset = 0; i <= mVolatile.lastInt(); i++)
				if (children.containsKey(i))
					offset++;
				else if (mVolatile.contains(i))
					volatil.add(i - offset);
			Map<Variable,Value<P,L>> inline = new HashMap();
			for (IntMap.Entry<? extends Value<P,L>> entry
					: children.intEntrySet())
				inline.put(mInputs.get(entry.getIntKey()),
						entry.getValue());
			return getBranch(mBranch.inline(inline), inputs, volatil);
		}
		
		public boolean equals(Value value) {
			return value instanceof BranchBlockValue
					&& mBranch.equals(((BranchBlockValue)value).mBranch)
					&& super.equals((BlockValue)value);
		}
		public int hashCode() {return mBranch.hashCode();}
		public String toString() {return "Branch" + mInputs;}
	}
	
	private static final class LoopBlockValue<P,L> extends BlockValue<P,L> {
		private final FallBlock<P,L> mInit;
		private final BranchBlock<P,L> mBody;
		
		public LoopBlockValue(FallBlock<P,L> init, BranchBlock<P,L> body,
				List<Variable> inputs, IncreasingIntSet volatil) {
			super(inputs, volatil);
			mInit = init;
			mBody = body;
		}
		
		public boolean isLoop() {return true;}
		public FallBlock<P,L> getInitializer() {return mInit;}
		public BranchBlock<P,L> getBody() {return mBody;}
		public Variable getPassVariable() {return mBody.getBranchVariable();}
		
		public boolean modifies(Variable variable) {
			return mInit.modifies(variable) || mBody.modifies(variable);
		}
		
		protected void chain(int child) {
			if (mChained.contains(child))
				return;
			if (!mVolatile.contains(child))
				throw new IllegalArgumentException();
			Collection<PairInt<OpExpression<L>>> uses = new ArrayList();
			mInit.chain(mInputs.get(child), uses);
			mBody.chain(mInputs.get(child), uses);
			mUses.put(child, uses);
		}
		
		protected Value<P,L> inlineChildren(
				IntMap<? extends Value<P,L>> children) {
			List<Variable> inputs = new ArrayList<Variable>(
					mInputs.size() - children.size());
			for (int i = 0; i < mInputs.size(); i++)
				if (!children.containsKey(i))
					inputs.add(mInputs.get(i));
			IncreasingIntSet volatil = new BitIntSet();
			for (int i = 0, offset = 0; i <= mVolatile.lastInt(); i++)
				if (children.containsKey(i))
					offset++;
				else if (mVolatile.contains(i))
					volatil.add(i - offset);
			Map<Variable,Value<P,L>> inline = new HashMap();
			for (IntMap.Entry<? extends Value<P,L>> entry
					: children.intEntrySet())
				inline.put(mInputs.get(entry.getIntKey()),
						entry.getValue());
			FallBlock<P,L> init = mInit.inline(inline);
			for (Iterator<Variable> vars = inline.keySet().iterator();
					vars.hasNext(); )
				if (init.modifies(vars.next()))
					vars.remove();
			return getLoop(init, mBody.inline(inline), inputs, volatil);
		}
		
		public boolean equals(Value value) {
			return value instanceof LoopBlockValue
					&& mInit.equals(((LoopBlockValue)value).mInit)
					&& mBody.equals(((LoopBlockValue)value).mBody)
					&& super.equals((BlockValue)value);
		}
		public int hashCode() {return mBody.hashCode();}
		public String toString() {return "Loop" + mInputs;}
	}
	
	private static abstract class HeadFlowValue<P,L> extends Value<P,L> {
		public abstract FlowValue<P,L> getHead();
		public abstract ReversionGraph<P,L>.Vertex getTail(
				ReversionGraph<P,L>.Vertex vertex);
		public abstract List<? extends ReversionGraph<P,L>.Vertex> getTails(
				ReversionGraph<P,L>.Vertex vertex);
		
		public abstract boolean needsAnyChild(OpAmbassador<L> ambassador);
		public abstract boolean needsChild(OpAmbassador<L> ambassador,
				int child);
		public HeadFlowValue<P,L> inlineChildren(
				IncreasingIntMap<? extends HeadFlowValue<P,L>> children) {
			return stickChildren(children,
					IntCollections.map(children.keySet(), 0));
		}
		public abstract HeadFlowValue<P,L> stickChildren(
				IntMap<? extends HeadFlowValue<P,L>> children,
				IncreasingIntMap<? extends Integer> counts);
		
		public abstract boolean canPreEvaluate(OpAmbassador<L> ambassador);
		public boolean canBeInlined() {
			return getHead().isDomain() || getHead().isBasicOp();
		}
		
		public abstract boolean containsEval();
		public abstract boolean containsPhi();
		
		protected abstract ReversionGraph<P,L>.Vertex fold(
				ReversionGraph<P,L>.Vertex leaf);
		protected abstract L fold(OpAmbassador<L> ambassador);
		
		protected abstract OpExpression<L> getExpression(
				OpAmbassador<L> ambassador,
				List<? extends ReversionGraph<P,L>.Vertex> children);
		
		public ReversionGraph<P,L>.Vertex rewrite(
				ReversionGraph<P,L>.Vertex vertex) {
			if (vertex.isLeaf())
				return fold(vertex);
			if (!getHead().isDomain() && !getHead().isBasicOp())
				return vertex;
			OpAmbassador<L> ambassador = vertex.getGraph().getOpAmbassador();
			boolean inline = false;
			boolean free = isFree(ambassador);
			for (int i = vertex.getChildCount(); i-- != 0; )
				if (inline |= vertex.getChild(i).isConstant()
						|| (!free && needsChild(ambassador, i)))
					break;
			if (!inline)
				return vertex;
			IncreasingIntMap<HeadFlowValue<P,L>> inlined
					= new ArrayIntMap<HeadFlowValue<P,L>>();
			List<ReversionGraph<P,L>.Vertex> children
					= new ArrayList<ReversionGraph<P,L>.Vertex>();
			IncreasingIntMap<Integer> counts = null;
			for (int i = 0; i < vertex.getChildCount(); i++)
				if (vertex.getChild(i).isConstant()
						|| (!free && needsChild(ambassador, i))) {
					if (counts == null && !vertex.getChild(i).isLeaf()) {
						counts = new ArrayIntMap<Integer>();
						Integer zero = Integer.valueOf(0);
						for (IntIterator keys = inlined.keySet().iterator();
								keys.hasNext(); )
							counts.put(keys.nextInt(), zero);
					}
					if (counts != null)
						counts.put(i, Integer.valueOf(
								vertex.getChild(i).getChildCount()));
					Value<P,L> label = vertex.getChild(i).getLabel();
					if (!label.canBeInlined())
						throw new IllegalArgumentException();
					inlined.put(i, (HeadFlowValue<P,L>)label);
					children.addAll(vertex.getChild(i).getChildren());
				} else
					children.add(vertex.getChild(i));
			Value<P,L> label;
			if (counts == null)
				label = inlineChildren(inlined);
			else
				label = stickChildren(inlined, counts);
			return vertex.getGraph().getVertex(label, children);
		}
	}
	
	private static final class BasicFlowValue<P,L> extends HeadFlowValue<P,L> {
		protected final FlowValue<P,L> mValue;
		
		protected BasicFlowValue(FlowValue<P,L> value) {mValue = value;}
		
		public FlowValue<P,L> getHead() {return mValue;}
		public ReversionGraph<P,L>.Vertex getTail(
				ReversionGraph<P,L>.Vertex vertex) {
			if (vertex.getChildCount() != 1)
				throw new IllegalArgumentException();
			return vertex.getChild(0);
		}
		public List<? extends ReversionGraph<P,L>.Vertex> getTails(
				ReversionGraph<P,L>.Vertex vertex) {
			return vertex.getChildren();
		}

		public boolean isFree(OpAmbassador<L> ambassador) {
			return mValue.isDomain() && ambassador.isFree(mValue.getDomain());
		}

		public boolean isAnyVolatile(OpAmbassador<L> ambassador) {
			return mValue.isDomain()
					&& ambassador.isAnyVolatile(mValue.getDomain());
		}
		public boolean isVolatile(OpAmbassador<L> ambassador, int child) {
			return mValue.isDomain()
					&& ambassador.isVolatile(mValue.getDomain(), child);
		}
		
		public void getUses(ReversionGraph<P,L>.Vertex vertex, int child,
				Collection<? super PairInt<OpExpression<L>>> uses) {
			if (mValue.isExtendedDomain())
				uses.add(new PairInt<OpExpression<L>>(vertex.getExpression(),
						child));
			else
				throw new UnsupportedOperationException();
		}
		
		public BasicFlowValue<P,L> getChainVersion(OpAmbassador<L> ambassador,
				int child) {
			if (!mValue.isDomain())
				throw new UnsupportedOperationException();
			return new BasicFlowValue<P,L>(FlowValue.<P,L>createDomain(
					ambassador.getChainVersion(mValue.getDomain(), child),
					ambassador));
		}
		public Value<P,L> getChainProjectVolatile(OpAmbassador<L> ambassador,
				int child) {
			if (!mValue.isDomain())
				return super.getChainProjectVolatile(ambassador, child);
			L project = ambassador.getChainProjectVolatile(
					mValue.getDomain(), child);
			return project == null ? null : new BasicFlowValue<P,L>(
					FlowValue.<P,L>createDomain(project, ambassador));
		}
		public Value<P,L> getChainProjectValue(OpAmbassador<L> ambassador,
				int child) {
			if (!mValue.isDomain())
				return super.getChainProjectValue(ambassador, child);
			L project = ambassador.getChainProjectValue(
					mValue.getDomain(), child);
			return project == null ? null : new BasicFlowValue<P,L>(
					FlowValue.<P,L>createDomain(project, ambassador));
		}
		
		public boolean isEquivalent(ReversionGraph<P,L>.Vertex vertex,
				int child, ReversionGraph<P,L>.Vertex that, int thatChild) {
			if (!mValue.isDomain())
				return super.isEquivalent(vertex, child, that, thatChild);
			OpAmbassador<L> ambassador = vertex.getGraph().getOpAmbassador();
			if (that.getLabel() instanceof BasicFlowValue)
				return ambassador.isEquivalent(vertex.getExpression(), child,
						that.getExpression(), thatChild);
			else if (that.getLabel() instanceof BlockValue) {
				OpExpression<L> expression = vertex.getExpression();
				Collection<? extends PairInt<OpExpression<L>>> uses
						= ((BlockValue<P,L>)that.getLabel()).getUses(thatChild);
				for (PairInt<OpExpression<L>> use : uses)
					if (!ambassador.isEquivalent(expression, child,
							use.getFirst(), use.getSecond()))
						return false;
				return true;
			} else if (that.getLabel() instanceof InlinedFlowValue) {
				int index = ((InlinedFlowValue<P,L>)that.getLabel())
						.adjustChildIndex(thatChild);
				if (index < 0)
					return super.isEquivalent(vertex, child, that, thatChild);
				return ambassador.isEquivalent(vertex.getExpression(), child,
						that.getExpression(), index);
			}
			return super.isEquivalent(vertex, child, that, thatChild);
		}
		
		public boolean needsAnyChild(OpAmbassador<L> ambassador) {
			return mValue.isEval() || mValue.isDomain()
					&& ambassador.needsAnyChild(mValue.getDomain());
		}
		public boolean needsChild(OpAmbassador<L> ambassador, int child) {
			return mValue.isEval() && child == 1 || mValue.isDomain()
					&& ambassador.needsChild(mValue.getDomain(), child);
		}
		public HeadFlowValue<P,L> stickChildren(
				IntMap<? extends HeadFlowValue<P,L>> children,
				IncreasingIntMap<? extends Integer> counts) {
			return new InlinedFlowValue<P,L>(this, children, counts);
		}
		
		public boolean canPreEvaluate(OpAmbassador<L> ambassador) {
			return mValue.canPreEvaluate(ambassador);
		}
		public boolean containsEval() {return mValue.isEval();}
		public boolean containsPhi() {return mValue.isPhi();}
		
		public void getVariance(BitIntSet variance,
				List<? extends BitIntSet> children) {
			super.getVariance(variance, children);
			if (mValue.isPass())
				variance.removeInt(mValue.getLoopDepth());
			else if (mValue.isEval()) {
				if (!children.get(1).contains(mValue.getLoopDepth()))
					variance.removeInt(mValue.getLoopDepth());
			} else if (mValue.isTheta())
				variance.add(mValue.getLoopDepth());
		}
		public boolean considerVariant(int loop, int child) {
			if (mValue.isPass())
				return mValue.getLoopDepth() != loop;
			else if (mValue.isEval())
				return mValue.getLoopDepth() != loop || child == 1;
			return true;
		}
		
		protected ReversionGraph<P,L>.Vertex fold(
				ReversionGraph<P,L>.Vertex leaf) {
			return leaf;
		}
		protected L fold(OpAmbassador<L> ambassador) {
			if (mValue.isDomain())
				return mValue.getDomain();
			else if (mValue.isBasicOp())
				return ambassador.getBasicOp(mValue.getBasicOp());
			else
				throw new UnsupportedOperationException();
		}
		
		public ReversionGraph<P,L>.Vertex rewrite(
				ReversionGraph<P,L>.Vertex vertex) {
			ReversionGraph<P,L> graph = vertex.getGraph();
			if (mValue.isDomain())
				;
			else if (mValue.isLoopLiftedAll()) {
				switch (vertex.getChildCount()) {
				case 1:
					if (mValue.isNegate()) {
						FlowValue<P,L> childOp = vertex.getChild(0).getHead();
						if (childOp == null)
							;
						else if (childOp.isTrue())
							return graph.getVertex(
									FlowValue.<P,L>createFalse());
						else if (childOp.isFalse())
							return graph.getVertex(
									FlowValue.<P,L>createTrue());
						else if (childOp.isNegate())
							return vertex.getChild(0).getTail();
					}
					break;
				case 2:
					if (mValue.isShortCircuitAnd())
						return graph.getVertex(FlowValue.<P,L>createPhi(),
								vertex.getChild(0), vertex.getChild(1),
								graph.getVertex(FlowValue.<P,L>createFalse()));
					else if (mValue.isShortCircuitOr())
						return graph.getVertex(FlowValue.<P,L>createPhi(),
								vertex.getChild(0),
								graph.getVertex(FlowValue.<P,L>createTrue()),
								vertex.getChild(1));
					else if (mValue.isAnd()) {
						FlowValue<P,L> leftOp = vertex.getChild(0).getHead();
						if (leftOp == null)
							;
						else if (leftOp.isTrue())
							return vertex.getChild(1);
						else if (leftOp.isFalse())
							return vertex.getChild(0);
						else if (leftOp.isNegate())
							return graph.getVertex(
									FlowValue.<P,L>createNegate(),
									graph.getVertex(FlowValue.<P,L>createOr(),
									vertex.getChild(0).getTail(),
									graph.getVertex(
									FlowValue.<P,L>createNegate(),
									vertex.getChild(1))));
						FlowValue<P,L> rightOp = vertex.getChild(1).getHead();
						if (rightOp == null)
							;
						else if (rightOp.isTrue())
							return vertex.getChild(0);
						else if (rightOp.isFalse())
							return vertex.getChild(1);
					} else if (mValue.isOr()) {
						FlowValue<P,L> leftOp = vertex.getChild(0).getHead();
						if (leftOp == null)
							;
						else if (leftOp.isTrue())
							return vertex.getChild(0);
						else if (leftOp.isFalse())
							return vertex.getChild(1);
						else if (leftOp.isNegate())
							return graph.getVertex(
									FlowValue.<P,L>createNegate(),
									graph.getVertex(FlowValue.<P,L>createAnd(),
									vertex.getChild(0).getTail(),
									graph.getVertex(
									FlowValue.<P,L>createNegate(),
									vertex.getChild(1))));
						FlowValue<P,L> rightOp = vertex.getChild(1).getHead();
						if (rightOp == null)
							;
						else if (rightOp.isTrue())
							return vertex.getChild(1);
						else if (rightOp.isFalse())
							return vertex.getChild(0);
					} else if (mValue.isEquals()) {
						if (vertex.getChild(0).equals(vertex.getChild(1)))
							return graph.getVertex(FlowValue.<P,L>createTrue());
					}
					break;
				case 3:
					if (mValue.isPhi()) {
						if (vertex.getChild(1).equals(vertex.getChild(2)))
							return vertex.getChild(1);
						FlowValue<P,L> condOp = vertex.getChild(0).getHead();
						if (condOp == null)
							;
						else if (condOp.isTrue())
							return vertex.getChild(1);
						else if (condOp.isFalse())
							return vertex.getChild(2);
						else if (condOp.isNegate())
							return graph.getVertex(mValue,
									vertex.getChild(0).getTail(),
									vertex.getChild(2), vertex.getChild(1));
						FlowValue<P,L> trueOp = vertex.getChild(1).getHead();
						FlowValue<P,L> falseOp = vertex.getChild(2).getHead();
						if (trueOp == null || falseOp == null)
							;
						else if (trueOp.isTrue() && falseOp.isFalse())
							return vertex.getChild(0);
						else if (trueOp.isFalse() && falseOp.isTrue())
							return graph.getVertex(
									FlowValue.<P,L>createNegate(),
									vertex.getChild(0));
					}
					break;
				}
			} else {
				int loop = mValue.getLoopDepth();
				if (mValue.isShift()) {
					if (vertex.getChild(0).isVariant(loop))
						return vertex.getChild(0).getLabel()
								.getShift(vertex.getChild(0), loop);
					else
						return vertex.getChild(0);
				} else if (mValue.isTheta()) {
					if (vertex.getChild(0).isVariant(loop))
						return graph.getVertex(mValue,
								vertex.getChild(0).getEval0(loop),
								vertex.getChild(1));
					else if (vertex.equals(vertex.getChild(1)))
						return vertex.getChild(0).getEval0(loop);
					else if (vertex.getChild(0).equals(vertex.getChild(1)))
						return vertex.getChild(0);
				} else if (mValue.isEval()) {
					if (!vertex.getChild(0).isVariant(loop))
						return vertex.getChild(0);
					else if (vertex.getChild(1).getHead() != null) {
						FlowValue<P,L> indexOp = vertex.getChild(1).getHead();
						if (indexOp.isZero())
							return vertex.getChild(0).getLabel()
									.getEval0(vertex.getChild(0), loop);
						else if (indexOp.isSuccessor())
							return graph.getVertex(mValue,
									vertex.getChild(0).getShift(loop),
									vertex.getChild(1).getTail());
						else if (indexOp.isPhi())
							return graph.getVertex(indexOp,
									vertex.getChild(1).getChild(0),
									graph.getVertex(mValue, vertex.getChild(0),
									vertex.getChild(1).getChild(1)),
									graph.getVertex(mValue, vertex.getChild(0),
									vertex.getChild(1).getChild(2)));
						else if (!indexOp.isPass())
							throw new IllegalArgumentException();
					} else if (!vertex.getChild(1).isVariable())
						throw new IllegalArgumentException();
					// these should be avoidable
					/*if (vertex.getChild(0).getHead() == null
							|| vertex.getChild(0).getHead().isExtendedDomain())
							{
						ReversionGraph<P,L>.Vertex child = vertex.getChild(0);
						if (child.getChildCount() == 1)
							return graph.getVertex(child.getLabel(),
									graph.getVertex(mValue, child.getChild(0),
											vertex.getChild(1)));
						else {
							ReversionGraph<P,L>.Vertex[] children
									= new ReversionGraph.Vertex[
									child.getChildCount()];
							for (int i = children.length; i-- != 0; )
								children[i] = child.getChild(i).isVariant(loop)
										? graph.getVertex(mValue,
										child.getChild(i), vertex.getChild(1))
										: child.getChild(i);
							return graph.getVertex(child.getLabel(), children);
						}
					} else if (vertex.getChild(0).getHead().isPhi()
							&& !vertex.getChild(0).getChild(1).isVariant(loop)
							&& !vertex.getChild(0).getChild(2).isVariant(loop))
						return graph.getVertex(FlowValue.<P,L>createPhi(),
								graph.getVertex(mValue,
										vertex.getChild(0).getChild(0),
										vertex.getChild(1)),
								vertex.getChild(0).getChild(1),
								vertex.getChild(0).getChild(2));*/
				} else if (mValue.isPass()) {
					if (vertex.getChild(0).isConstant()
							&& vertex.getChild(0).getHead().isTrue())
						return graph.getVertex(FlowValue.<P,L>createZero());
				}
			}
			return super.rewrite(vertex);
		}
		
		public ReversionGraph<P,L>.Vertex getShift(
				ReversionGraph<P,L>.Vertex vertex, int loop) {
			if (mValue.isLoopLiftedAll() || mValue.getLoopDepth() != loop)
				return super.getShift(vertex, loop);
			else if (mValue.isTheta())
				return vertex.getChild(1);
			else if (mValue.isShift())
				return vertex.getGraph().getVertex(mValue, vertex);
			else if (mValue.isEval())
				return vertex.getGraph().getVertex(mValue, vertex.getChild(0),
						vertex.getChild(1).getShift(loop));
			else if (mValue.isPass())
				return vertex;
			else
				throw new UnhandledCaseException();
		}
		public ReversionGraph<P,L>.Vertex getEval0(
				ReversionGraph<P,L>.Vertex vertex, int loop) {
			if (mValue.isLoopLiftedAll() || mValue.getLoopDepth() != loop)
				return super.getEval0(vertex, loop);
			else if (mValue.isTheta())
				return vertex.getChild(0).getEval0(loop);
			else if (mValue.isShift())
				throw new IllegalArgumentException();
			else if (mValue.isEval())
				return vertex.getGraph().getVertex(mValue, vertex.getChild(0),
						vertex.getChild(1).getEval0(loop));
			else if (mValue.isPass())
				return vertex;
			else
				throw new UnhandledCaseException();
		}
		
		public OpExpression<L> getExpression(
				final ReversionGraph<P,L>.Vertex vertex) {
			if (mValue.isExtendedDomain())
				return new OpExpression<L>() {
					public L getOperation() {
						return mValue.getDomain(
								vertex.getGraph().getOpAmbassador());
					}
					public int getOperandCount() {
						return vertex.getChildCount();
					}
					public OpExpression<L> getOperand(int index) {
						return vertex.getChild(index).getExpression();
					}
				};
			else
				return null;
		}
		
		protected OpExpression<L> getExpression(
				final OpAmbassador<L> ambassador,
				final List<? extends ReversionGraph<P,L>.Vertex> children) {
			if (mValue.isExtendedDomain())
				return new OpExpression<L>() {
					public L getOperation() {
						return mValue.getDomain(ambassador);
					}
					public int getOperandCount() {
						return children.size();
					}
					public OpExpression<L> getOperand(int index) {
						return children.get(index).getExpression();
					}
				};
			else
				return null;
		}
		
		public int hashCode() {return mValue.hashCode();}
		public boolean equals(Value that) {
			if (!(that instanceof BasicFlowValue))
				return false;
			return mValue.equals(((BasicFlowValue)that).mValue);
		}
		public String toString() {return mValue.toString();}
	}
	
	
	private static final class InlinedFlowValue<P,L> extends HeadFlowValue<P,L>
			{
		protected final BasicFlowValue<P,L> mValue;
		protected final IntMap<? extends HeadFlowValue<P,L>> mInlined;
		protected final IncreasingIntMap<? extends Integer> mCounts;
		
		public InlinedFlowValue(BasicFlowValue<P,L> value,
				IntMap<? extends HeadFlowValue<P,L>> inlined,
				IncreasingIntMap<? extends Integer> counts) {
			mValue = value;
			mInlined = inlined;
			mCounts = counts;
		}
		
		public FlowValue<P,L> getHead() {return mValue.getHead();}
		public ReversionGraph<P,L>.Vertex getTail(
				ReversionGraph<P,L>.Vertex vertex) {
			if (mCounts.size() > 1)
				throw new UnsupportedOperationException();
			int count = mCounts.values().iterator().next();
			if (count != vertex.getChildCount())
				throw new IllegalArgumentException();
			return vertex.getGraph().getVertex(
					mInlined.values().iterator().next(), vertex.getChildren());
		}
		public List<? extends ReversionGraph<P,L>.Vertex> getTails(
				ReversionGraph<P,L>.Vertex vertex) {
			ReversionGraph<P,L> graph = vertex.getGraph();
			List<? extends ReversionGraph<P,L>.Vertex> children
					= vertex.getChildren();
			List<ReversionGraph<P,L>.Vertex> actual = new ArrayList();
			int index = 0;
			for (int i = 0; i <= mCounts.keySet().lastInt(); i++) {
				Integer count = mCounts.get(i);
				if (count == null)
					actual.add(children.get(index++));
				else {
					actual.add(graph.getVertex(mInlined.get(i),
							children.subList(index, index + count)));
					index += count;
				}
			}
			for (; index < children.size(); index++)
				actual.add(children.get(index));
			return actual;
		}
		
		protected final int adjustChildIndex(int index) {
			for (IntMap.Entry<? extends Integer> entry : mCounts.intEntrySet())
				if (entry.getIntKey() <= index) {
					int count = entry.getValue();
					if (index - entry.getIntKey() < count)
						return -1 - (entry.getIntKey()
								+ (index - entry.getIntKey())
								* (mCounts.keySet().lastInt() + 1));
					index -= count - 1;
				}
			return index;
		}
		protected final int getChildIndex(int index) {
			return (-1 - index) % (mCounts.keySet().lastInt() + 1);
		}
		protected final int getSubChildIndex(int index) {
			return (-1 - index) / (mCounts.keySet().lastInt() + 1);
		}

		public boolean isFree(OpAmbassador<L> ambassador) {
			if (!mValue.isFree(ambassador))
				return false;
			for (Value<P,L> inlined : mInlined.values())
				if (!inlined.isFree(ambassador))
					return false;
			return true;
		}

		public boolean isAnyVolatile(OpAmbassador<L> ambassador) {
			if (mValue.isAnyVolatile(ambassador))
				return true;
			for (Value<P,L> inlined : mInlined.values())
				if (inlined.isAnyVolatile(ambassador))
					return true;
			return false;
		}
		public boolean isVolatile(OpAmbassador<L> ambassador, int child) {
			int index = adjustChildIndex(child);
			if (index < 0)
				return mInlined.get(getChildIndex(index)).isVolatile(
						ambassador, getSubChildIndex(index));
			else
				return mValue.isVolatile(ambassador, index);
		}
		
		public void getUses(ReversionGraph<P,L>.Vertex vertex, int child,
				Collection<? super PairInt<OpExpression<L>>> uses) {
			int index = adjustChildIndex(child);
			if (index < 0)
				throw new UnsupportedOperationException();
			else
				mValue.getUses(vertex, index, uses);
		}
		
		public Value<P,L> getChainVersion(OpAmbassador<L> ambassador, int child)
				{
			int index = adjustChildIndex(child);
			if (index < 0)
				return super.getChainVersion(ambassador, child);
			else
				return new InlinedFlowValue<P,L>(
						mValue.getChainVersion(ambassador, index), mInlined,
						mCounts);
		}
		public Value<P,L> getChainProjectVolatile(OpAmbassador<L> ambassador,
				int child) {
			int index = adjustChildIndex(child);
			if (index < 0)
				return super.getChainProjectVolatile(ambassador, child);
			else
				return mValue.getChainProjectVolatile(ambassador, index);
		}
		public Value<P,L> getChainProjectValue(OpAmbassador<L> ambassador,
				int child) {
			int index = adjustChildIndex(child);
			if (index < 0)
				return super.getChainProjectValue(ambassador, child);
			else
				return mValue.getChainProjectValue(ambassador, index);
		}
		
		public boolean isEquivalent(ReversionGraph<P,L>.Vertex vertex,
				int child, ReversionGraph<P,L>.Vertex value, int thatChild) {
			int index = adjustChildIndex(child);
			if (index < 0)
				return super.isEquivalent(vertex, child, value, thatChild);
			return mValue.isEquivalent(vertex, index, value, thatChild);
		}
		
		public boolean needsAnyChild(OpAmbassador<L> ambassador) {
			return mValue.needsAnyChild(ambassador);
		}
		public boolean needsChild(OpAmbassador<L> ambassador, int child) {
			int index = adjustChildIndex(child);
			if (index < 0)
				return mInlined.get(getChildIndex(index)).needsChild(
						ambassador, getSubChildIndex(index));
			else
				return mValue.needsChild(ambassador, index);
		}
		
		public HeadFlowValue<P,L> stickChildren(
				IntMap<? extends HeadFlowValue<P,L>> children,
				IncreasingIntMap<? extends Integer> subCounts) {
			IncreasingIntMap<HeadFlowValue<P,L>> inlined
					= new ArrayIntMap<HeadFlowValue<P,L>>();
			inlined.putAll(mInlined);
			MultiMap<Integer,Integer> subs
					= new BackedMultiMap<Integer,Integer>() {
				protected <R> Map<Integer,R> makeKeyMap() {
					return new ArrayIntMap<R>();
				}
				protected Set<Integer> makeValueSet() {
					return new BitIntSet();
				}
			};
			IntMap<Integer> basic = new ArrayIntMap<Integer>(); 
			for (IntIterator keys = children.keySet().iterator();
					keys.hasNext(); ) {
				int key = keys.nextInt();
				int index = adjustChildIndex(key);
				if (index < 0)
					subs.addValue(getChildIndex(index), key);
				else
					basic.put(index, Integer.valueOf(key));
			}
			boolean zero = true;
			for (IntMap.Entry<? extends Integer> entry : basic.intEntrySet())
				if (subCounts.get(entry.getValue()) != 0) {
					zero = false;
					break;
				}
			if (zero)
				for (IntMap.Entry<? extends Integer> entry
						: mCounts.intEntrySet())
					if (entry.getValue() != subs.get(entry.getKey()).size()) {
						zero = false;
						break;
					}
			IncreasingIntMap<Integer> counts = zero
					? IntCollections.map(inlined.keySet(), 0)
					: new ArrayIntMap<Integer>(mCounts);
			for (MultiMap.Entry<Integer,Integer> entry : subs.entrySet()) {
				IncreasingIntMap<HeadFlowValue<P,L>> sub = new ArrayIntMap();
				IncreasingIntMap<Integer> count = new ArrayIntMap();
				boolean subZero = true;
				for (int child : entry.getValues()) {
					int subIndex = getSubChildIndex(adjustChildIndex(child));
					sub.put(subIndex, children.get(child));
					Integer subCount = subCounts.get(child);
					subZero &= subCount == 0;
					count.put(subIndex, subCount);
				}
				inlined.put(entry.getKey(), subZero
						? inlined.get(entry.getKey()).inlineChildren(sub) :
						inlined.get(entry.getKey()).stickChildren(sub, count));
				if (!zero)
					counts.put(entry.getKey(),
							Integer.valueOf(mCounts.get(entry.getKey())
							- entry.getValues().size()));
			}
			for (IntMap.Entry<Integer> entry : basic.intEntrySet()) {
				inlined.put(entry.getIntKey(), children.get(entry.getValue()));
				if (!zero)
					counts.put(entry.getIntKey(),
							subCounts.get(entry.getValue()));
			}
			return new InlinedFlowValue<P,L>(mValue, inlined, counts);
		}
		
		public boolean canPreEvaluate(OpAmbassador<L> ambassador) {
			if (!mValue.canPreEvaluate(ambassador))
				return false;
			for (Value<P,L> inlined : mInlined.values())
				if (!inlined.canPreEvaluate(ambassador))
					return false;
			return true;
		}
		public boolean containsEval() {
			if (mValue.containsEval())
				return true;
			for (Value<P,L> inlined : mInlined.values())
				if (inlined.containsEval())
					return true;
			return false;
		}
		public boolean containsPhi() {
			if (mValue.containsPhi())
				return true;
			for (Value<P,L> inlined : mInlined.values())
				if (inlined.containsPhi())
					return true;
			return false;
		}
		
		public void getVariance(BitIntSet variance,
				List<? extends BitIntSet> children) {
			List<BitIntSet> actual = new ArrayList<BitIntSet>();
			int index = 0;
			for (int i = 0; i <= mCounts.keySet().lastInt(); i++) {
				Integer count = mCounts.get(i);
				if (count == null)
					actual.add(children.get(index++));
				else {
					BitIntSet set = new BitIntSet();
					mInlined.get(i).getVariance(set,
							children.subList(index, index + count));
					index += count;
					actual.add(set);
				}
			}
			for (; index < children.size(); index++)
				actual.add(children.get(index));
			mValue.getVariance(variance, actual);
		}
		public boolean considerVariant(int loop, int child) {
			int index = adjustChildIndex(child);
			if (index < 0)
				return mInlined.get(getChildIndex(index)).considerVariant(
						loop, getSubChildIndex(index));
			else
				return mValue.considerVariant(loop, index);
		}
		
		public ReversionGraph<P,L>.Vertex getShift(
				ReversionGraph<P,L>.Vertex vertex, int loop) {
			FlowValue<P,L> head = mValue.mValue;
			if (head == null || head.isLoopLiftedAll()
					|| head.getLoopDepth() != loop) {
				ReversionGraph<P,L> graph = vertex.getGraph();
				List<? extends ReversionGraph<P,L>.Vertex> children
						= vertex.getChildren();
				List<ReversionGraph<P,L>.Vertex> actual = new ArrayList();
				int index = 0;
				for (int i = 0; i <= mCounts.keySet().lastInt(); i++) {
					Integer count = mCounts.get(i);
					if (count == null)
						actual.add(vertex.getChild(index++).getShift(loop));
					else {
						actual.add(graph.getVertex(mInlined.get(i),
								children.subList(index, index + count))
								.getShift(loop));
						index += count;
					}
				}
				for (; index < children.size(); index++)
					actual.add(children.get(index).getShift(loop));
				return graph.getVertex(mValue, actual);
			} else if (head.isTheta()) {
				int start = mCounts.containsKey(0) ? mCounts.get(0) : 1;
				if (mInlined.containsKey(1))
					return vertex.getGraph().getVertex(mInlined.get(1),
								vertex.getChildren().subList(start,
										start + mCounts.get(1)));
				else
					return vertex.getChild(start);
			} else if (head.isShift())
				return vertex.getGraph().getVertex(head, vertex);
			else if (head.isEval()) {
				ReversionGraph<P,L>.Vertex left;
				int start = 1;
				if (mInlined.containsKey(0))
					left = vertex.getGraph().getVertex(mInlined.get(0),
							vertex.getChildren().subList(0,
							start = mCounts.get(0)));
				else
					left = vertex.getChild(0);
				ReversionGraph<P,L>.Vertex right;
				if (mInlined.containsKey(1))
					right = vertex.getGraph().getVertex(mInlined.get(1),
							vertex.getChildren().subList(start,
							start + mCounts.get(1)));
				else
					right = vertex.getChild(start);
				return vertex.getGraph().getVertex(mValue, left,
						right.getShift(loop));
			} else if (head.isPass())
				return vertex;
			else
				throw new UnhandledCaseException();
		}
		public ReversionGraph<P,L>.Vertex getEval0(
				ReversionGraph<P,L>.Vertex vertex, int loop) {
			FlowValue<P,L> head = mValue.mValue;
			if (head == null || head.isLoopLiftedAll()
					|| head.getLoopDepth() != loop) {
				ReversionGraph<P,L> graph = vertex.getGraph();
				List<? extends ReversionGraph<P,L>.Vertex> children
						= vertex.getChildren();
				List<ReversionGraph<P,L>.Vertex> actual = new ArrayList();
				int index = 0;
				for (int i = 0; i <= mCounts.keySet().lastInt(); i++) {
					Integer count = mCounts.get(i);
					if (count == null)
						actual.add(vertex.getChild(index++).getEval0(loop));
					else {
						actual.add(graph.getVertex(mInlined.get(i),
								children.subList(index, index + count))
								.getEval0(loop));
						index += count;
					}
				}
				for (; index < children.size(); index++)
					actual.add(children.get(index).getEval0(loop));
				return graph.getVertex(mValue, actual);
			} else if (head.isTheta()) {
				if (mInlined.containsKey(0))
					return vertex.getGraph().getVertex(mInlined.get(0),
								vertex.getChildren().subList(0, mCounts.get(0)))
								.getEval0(loop);
				else
					return vertex.getChild(0).getEval0(loop);
			} else if (head.isShift())
				throw new IllegalArgumentException();
			else if (head.isEval()) {
				ReversionGraph<P,L>.Vertex left;
				int start = 1;
				if (mInlined.containsKey(0))
					left = vertex.getGraph().getVertex(mInlined.get(0),
							vertex.getChildren().subList(0,
							start = mCounts.get(0)));
				else
					left = vertex.getChild(0);
				ReversionGraph<P,L>.Vertex right;
				if (mInlined.containsKey(1))
					right = vertex.getGraph().getVertex(mInlined.get(1),
							vertex.getChildren().subList(start,
							start + mCounts.get(1)));
				else
					right = vertex.getChild(start);
				return vertex.getGraph().getVertex(mValue, left,
						right.getEval0(loop));
			} else if (head.isPass())
				return vertex;
			else
				throw new UnhandledCaseException();
		}
		
		protected ReversionGraph<P,L>.Vertex fold(
				ReversionGraph<P,L>.Vertex leaf) {
			if (!canPreEvaluate(leaf.getGraph().getOpAmbassador()))
				return leaf;
			else {
				L value = fold(leaf.getGraph().getOpAmbassador());
				return value == null ? leaf : leaf.getGraph().getVertex(value);
			}
		}
		protected L fold(OpAmbassador<L> ambassador) {
			L op = mValue.fold(ambassador);
			if (mInlined.size() == 1)
				return ambassador.get(op, mInlined.get(0).fold(ambassador));
			List<L> operands = new ArrayList<L>(mInlined.size());
			for (int i = 0; i < mInlined.size(); i++) {
				L operand = mInlined.get(i).fold(ambassador);
				if (operand == null)
					return null;
				operands.add(operand);
			}
			return ambassador.get(op, operands);
		}
		
		public OpExpression<L> getExpression(
				final ReversionGraph<P,L>.Vertex vertex) {
			if (mValue.mValue.isExtendedDomain())
				return new OpExpression<L>() {
					public L getOperation() {
						return mValue.mValue.getDomain(
								vertex.getGraph().getOpAmbassador());
					}
					public int getOperandCount() {
						int count = vertex.getChildCount();
						for (int subCount : mCounts.values())
							count += 1 - subCount;
						return count;
					}
					public OpExpression<L> getOperand(int index) {
						int child = index;
						for (int key : mCounts.keySet()) {
							if (index < key)
								return vertex.getChild(child).getExpression();
							else if (index == key)
								return mInlined.get(key).getExpression(
										vertex.getGraph().getOpAmbassador(),
										vertex.getChildren().subList(child,
												child + mCounts.get(key)));
							else
								child += mCounts.get(key);
						}
						return vertex.getChild(child).getExpression();
					}
				};
			else
				return null;
		}
		
		protected OpExpression<L> getExpression(
				final OpAmbassador<L> ambassador,
				final List<? extends ReversionGraph<P,L>.Vertex> children) {
			if (mValue.mValue.isExtendedDomain())
				return new OpExpression<L>() {
				public L getOperation() {
					return mValue.mValue.getDomain(ambassador);
				}
				public int getOperandCount() {
					int count = children.size();
					for (int subCount : mCounts.values())
						count += 1 - subCount;
					return count;
				}
				public OpExpression<L> getOperand(int index) {
					int child = index;
					for (int key : mCounts.keySet()) {
						if (index < key)
							return children.get(child).getExpression();
						else if (index == key)
							return mInlined.get(key).getExpression(ambassador,
									children.subList(child,
											child + mCounts.get(key)));
						else
							child += mCounts.get(key);
					}
					return children.get(child).getExpression();
				}
			};
			else
				return null;
		}
		
		public int hashCode() {return mValue.hashCode() + mInlined.size();}
		public boolean equals(Value that) {
			if (!(that instanceof InlinedFlowValue))
				return false;
			InlinedFlowValue value = (InlinedFlowValue)that;
			return mValue.equals(value.mValue)
					&& mInlined.equals(value.mInlined)
					&& mCounts.equals(value.mCounts);
		}
		public String toString() {
			String string = mValue.mValue.toString() + "(";
			for (int i = 0; i <= mCounts.keySet().lastInt(); i++) {
				Integer count = mCounts.get(i);
				if (count == null)
					string += ".";
				else {
					string += mInlined.get(i).toString();
					if (count > 1) {
						string += "(";
						for (int j = count; --j != 0; )
							string += ".,";
						string += ".)";
					}
				}
				string += ",";
			}
			string += "...)";
			return string;
		}
	}
}
