package eqsat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.AbstractVariaticFunction;
import util.DisjointUnion;
import util.Function;
import util.VariaticFunction;
import util.graph.AbstractGraph;
import util.graph.AbstractVertex;
import util.graph.CExpressionGraph;
import util.graph.Graph;
import util.graph.CExpressionGraph.Vertex;

public abstract class GenericCFG<G extends Graph<?,? extends B>,
		B extends GenericBlock<?,? super B,V,L>, V, L>
		extends AbstractGraph<G,B> implements CFG<G,B,V,L,V,V> {
	protected final int mSize;
	protected final B[] mBlocks;
	protected final Set<V> mVariables = new HashSet<V>();
	protected final Set<V> mReturns = new HashSet<V>();
	protected final CExpressionGraph<DisjointUnion<L,V>> mModificationGraph
			= new CExpressionGraph<DisjointUnion<L,V>>();
	
	public GenericCFG(int size) {
		mSize = size;
		mBlocks = makeBlockArray(size);
		mBlocks[mSize - 1] = makeBlock(mSize - 1);
	}

	public List<? extends B> getVertices() {
		return Arrays.asList(mBlocks);
	}
	
	public Collection<? extends V> getVariables() {return mVariables;}
	public V getParameter(V variable) {return variable;}
	public Collection<? extends V> getReturns() {return mReturns;}
	public V getReturnVariable(V ret) {return ret;}
	
	public B getStart() {return mBlocks[0];}
	public B getEnd() {return mBlocks[mSize - 1];}
	public B getBlock(int block) {return mBlocks[block];}
	
	public B setBlock(int block, int target) {
		if (block == mSize - 1 || target == 0)
			throw new IllegalArgumentException();
		return mBlocks[block] = makeBlock(block, target);
	}
	
	public B setBlock(int block, int trueTarget, int falseTarget) {
		if (block == mSize - 1 || trueTarget == 0 || falseTarget == 0)
			throw new IllegalArgumentException();
		return mBlocks[block] = makeBlock(block, trueTarget, falseTarget);
	}
	
	public void addVariable(V variable) {mVariables.add(variable);}
	public void addReturn(V variable) {
		addVariable(variable);
		mReturns.add(variable);
	}
	
	public Vertex<DisjointUnion<L,V>> getValue(L value,
			Vertex<DisjointUnion<L,V>>... children) {
		return mModificationGraph.getVertex(
				DisjointUnion.<L,V>injectLeft(value), children);
	}
	
	public <E> CFGTranslator<B,V,E> getTranslator(
			final Function<V,E> parameterConverter,
			final VariaticFunction<L,E,E> converter,
			Collection<? super E> known) {
		return new CFGTranslator<B,V,E>() {
			public Function<V,E> getOutputs(final B block,
					final Function<V,E> inputs) {
				return new Function<V,E>() {
					public E get(V variable) {
						if (variable == null)
							return block.getBranchCondition(converter, inputs);
						else
							return block.getOutput(variable, converter, inputs);
					}
				};
			}
		};
	}
	
	protected abstract B[] makeBlockArray(int size);
	protected abstract B makeBlock(int index);
	protected abstract B makeBlock(int index, int child);
	protected abstract B makeBlock(int index, int trueChild, int falseChild);
	
	protected abstract class Block extends AbstractVertex<G,B>
			implements GenericBlock<G,B,V,L> {
		protected final int mIndex;
		protected final Map<V,Vertex<DisjointUnion<L,V>>>
				mModifications = new HashMap<V,Vertex<DisjointUnion<L,V>>>();
		
		public Block(int index) {mIndex = index;}
		
		public abstract List<? extends B> getChildren();
		public abstract B getChild(int child);
		public boolean isStart() {return isRoot();}
		public boolean isEnd() {return isLeaf();}
		
		public Vertex<DisjointUnion<L,V>> getInput(V variable) {
			return mModificationGraph.getVertex(
					DisjointUnion.<L,V>injectRight(variable));
		}
		
		public Vertex<DisjointUnion<L,V>> getOutput(V variable) {
			if (modifies(variable))
				return mModifications.get(variable);
			else
				return getInput(variable);
		}
		
		public boolean modifies(V variable) {
			return mModifications.containsKey(variable);
		}
		public void setModification(V variable, Vertex<DisjointUnion<L,V>> mod)
				{
			if (mod.equals(getInput(variable)))
				mModifications.remove(variable);
			else
				mModifications.put(variable, mod);
		}
		
		public <E> E getOutput(V variable,
				final VariaticFunction<L,E,E> converter,
				final Function<V,E> inputs) {
			if (!modifies(variable))
				return inputs.get(variable);
			Vertex<DisjointUnion<L,V>> modification
					= mModifications.get(variable);
			VariaticFunction<DisjointUnion<L,V>,E,E> map
					= new AbstractVariaticFunction<DisjointUnion<L,V>,E,E>() {
				public E get(DisjointUnion<L,V> label, List<? extends E> values)
						{
					return label.isLeft()
							? converter.get(label.getLeft(), values)
							: inputs.get(label.getRight());
				}
			};
			return modification.evaluate(map);
		}
		
		public void setBranchCondition(Vertex<DisjointUnion<L,V>> condition) {
			throw new UnsupportedOperationException();
		}
		public <E> E getBranchCondition(
				VariaticFunction<L,E,E> converter, Function<V,E> inputs) {
			throw new UnsupportedOperationException();
		}
		
		public int getIndex() {return mIndex;}
		public String toString() {return Integer.toString(mIndex + 1);}
	}
	
	protected abstract class EndBlock extends Block {
		public EndBlock(int index) {super(index);}
		
		public boolean isLeaf() {return true;}
		public boolean hasChildren() {return false;}
		
		public List<? extends B> getChildren() {
			return Collections.<B>emptyList();
		}
		public B getChild(int child) {return getChildren().get(child);}
	}
	
	protected abstract class FallBlock extends Block {
		protected final int mChild;
		
		public FallBlock(int index, int child) {super(index); mChild = child;}
		
		public List<? extends B> getChildren() {
			return Collections.singletonList(mBlocks[mChild]);
		}
		public B getChild(int child) {
			if (child == 0)
				return mBlocks[mChild];
			return getChildren().get(child);
		}
	}
	
	protected abstract class BranchBlock extends Block {
		protected final int mTrueChild, mFalseChild;
		protected CExpressionGraph.Vertex<DisjointUnion<L,V>> mCondition;
		
		public BranchBlock(int index, int trueChild, int falseChild) {
			super(index);
			mTrueChild = trueChild;
			mFalseChild = falseChild;
		}
		
		public List<? extends B> getChildren() {
			List<B> children = new ArrayList<B>(2);
			children.add(mBlocks[mTrueChild]);
			children.add(mBlocks[mFalseChild]);
			return children;
		}
		public B getChild(int child) {
			if (child == 0)
				return mBlocks[mTrueChild];
			if (child == 1)
				return mBlocks[mFalseChild];
			return getChildren().get(child);
		}
		
		public void setBranchCondition(Vertex<DisjointUnion<L,V>> condition) {
			mCondition = condition;
		}
		public <E> E getBranchCondition(
				final VariaticFunction<L,E,E> converter,
				final Function<V,E> inputs) {
			VariaticFunction<DisjointUnion<L,V>,E,E> map
					= new AbstractVariaticFunction<DisjointUnion<L,V>,E,E>() {
				public E get(DisjointUnion<L,V> label, List<? extends E> values)
						{
					return label.isLeft()
							? converter.get(label.getLeft(), values)
							: inputs.get(label.getRight());
				}
			};
			return mCondition.evaluate(map);
		}
	}
}
