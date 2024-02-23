package eqsat.revert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.OpExpression;
import util.Function;
import util.IdentityHashSet;
import util.NamedTag;
import util.Tag;
import util.Taggable;
import util.VariaticFunction;
import util.graph.AbstractVertex;
import util.graph.ExpressionGraph;
import util.graph.ExpressionVertex;
import util.graph.RecursiveExpressionGraph;
import util.graph.RecursiveExpressionVertex;
import util.integer.BitIntSet;
import util.integer.IntIterator;
import util.integer.PairInt;
import util.mapped.MappedList;

public final class ReversionGraph<P, L> extends RecursiveExpressionGraph
		<ReversionGraph<P,L>,ReversionGraph<P,L>.Vertex,Value<P,L>> {
	private final OpAmbassador<L> mAmbassador;
	private boolean mContainedEvals = false;
	private boolean mContainedPhis = false;
	private boolean mContainedBlocks = false;
	private int mPlaceHolders = 0;
	
	public ReversionGraph(OpAmbassador<L> ambassador) {
		mAmbassador = ambassador;
	}
	public <G extends ExpressionGraph<G,N,FlowValue<P,L>>,
			N extends ExpressionVertex<? super G,N,FlowValue<P,L>> & Taggable,R>
			ReversionGraph(OpAmbassador<L> ambassador, final G graph,
			Map<? extends R,? extends N> returns,
			Map<? super R,? super ReversionGraph<P,L>.Vertex> converted) {
		this(ambassador);
		final Tag<Vertex> cache = new NamedTag("Cached");
		for (Entry<? extends R,? extends N> entry : returns.entrySet()) {
			Vertex convert = this.<N>convert(cache, entry.getValue());
			convert.makeSignificant();
			converted.put(entry.getKey(), convert);
		}
	}
	protected ReversionGraph(ReversionGraph<P,L> that) {
		this(that.mAmbassador);
		Function<Vertex,Vertex> convert = new Function<Vertex,Vertex>() {
			final Tag<Vertex> mTag = new Tag() {
				public String toString() {return "Converted";}
			};
			
			public Vertex get(Vertex vertex) {
				if (vertex.hasTag(mTag)) {
					if (vertex.getTag(mTag) == null)
						vertex.setTag(mTag, createPlaceHolder());
					return vertex.getTag(mTag);
				}
				vertex.setTag(mTag, null);
				Vertex result;
				if (vertex.isLeaf())
					result = getVertex(vertex.getLabel());
				else if (vertex.getChildCount() == 1)
					result = getVertex(vertex.getLabel(),
							get(vertex.getChild(0)));
				else {
					Vertex[] children
							= new ReversionGraph.Vertex[vertex.getChildCount()];
					for (int i = children.length; i-- != 0; )
						children[i] = get(vertex.getChild(i));
					result = getVertex(vertex.getLabel(), children);
				}
				if (vertex.getTag(mTag) != null)
					vertex.getTag(mTag).rewrite(result);
				vertex.setTag(mTag, result);
				return result;
			}
		};
		for (Vertex vertex : that.getSignificant())
			convert.get(vertex).makeSignificant();
	}
	
	public ReversionGraph<P,L> getSelf() {return this;}
	
	public OpAmbassador<L> getOpAmbassador() {return mAmbassador;}
	
	public boolean containsEvals() {
		if (!mContainedEvals)
			return false;
		mContainedEvals = false;
		for (Vertex vertex : getVertices())
			if (mContainedEvals |= vertex.getLabel().containsEval())
				break;
		return mContainedEvals;
	}
	public void clearEvals() {mContainedEvals = false;}
	public boolean containsPhis() {
		if (!mContainedPhis)
			return false;
		mContainedPhis = false;
		for (Vertex vertex : getVertices())
			if (mContainedPhis |= vertex.getLabel().containsPhi())
				break;
		return mContainedPhis;
	}
	public void clearPhis() {mContainedPhis = false;}
	public boolean containsBlocks() {
		if (!mContainedBlocks)
			return false;
		mContainedBlocks = false;
		for (Vertex vertex : getVertices())
			if (mContainedBlocks |= (vertex.getLabel().isBranch()
					|| vertex.getLabel().isLoop()))
				break;
		return mContainedBlocks;
	}
	public void clearBlocks() {mContainedBlocks = false;}
	public boolean containsThetas() {
		for (Vertex vertex : getVertices())
			if (vertex.getHead() != null && vertex.getHead().isTheta())
				return true;
		return false;
	}
	
	public int getMaxVariant() {
		int max = 0;
		for (Vertex vertex : getVertices())
			max = Math.max(max, vertex.getMaxVariant());
		return max;
	}
	
	private <N extends ExpressionVertex<?,N,FlowValue<P,L>> & Taggable> Vertex
			convert(Tag<Vertex> cache, N node) {
		if (node.hasTag(cache)) {
			if (node.getTag(cache) == null)
				node.setTag(cache, createPlaceHolder());
			return node.getTag(cache);
		}
		node.setTag(cache, null);
		if (node.getLabel() == null)
			throw new IllegalArgumentException();
		Vertex vertex;
		if (node.isLeaf())
			vertex = getVertex(node.getLabel());
		else if (node.getChildCount() == 1)
			vertex = getVertex(node.getLabel(),
					convert(cache, node.getChild(0)));
		else {
			Vertex[] children
					= new ReversionGraph.Vertex[node.getChildCount()];
			for (int i = children.length; i-- != 0; )
				children[i] = convert(cache, node.getChild(i));
			vertex = getVertex(node.getLabel(), children);
		}
		if (node.getTag(cache) != null)
			node.getTag(cache).rewrite(vertex);
		node.setTag(cache, vertex);
		return vertex;
	}
	
	public void trimRewritten() {
		for (Iterator<? extends Vertex> vertices = getVertices().iterator();
				vertices.hasNext(); )
			if (vertices.next().isRewritten())
				vertices.remove();
	}
	
	protected void checkRewrite() {
		if (mPlaceHolders != 0)
			return;
		List<Vertex> vertices = new ArrayList<Vertex>(getVertices());
		for (Vertex vertex : vertices)
			vertex.getRewriteSelf().checkRewrite();
	}
	
	public Vertex createPlaceHolder() {return new HolderVertex();}
	public Vertex makeHolderVertex() {return createPlaceHolder();}
	public Vertex makeHolderVertex(Value<P,L> label) {
		throw new UnsupportedOperationException();
	}
	public Vertex getVertex(Value<P,L> label) {
		return super.getVertex(label).getRewriteSelf();
	}
	public Vertex getVertex(Value<P,L> label, Vertex child) {
		return super.getVertex(label, child).getRewriteSelf();
	}
	public Vertex getVertex(Value<P,L> label, Vertex... children) {
		return getVertex(label, Arrays.asList(children));
	}
	public Vertex getVertex(Value<P,L> label,
			List<? extends Vertex> children) {
		return super.getVertex(label, children).getRewriteSelf();
	}
	public Vertex getVertex(Variable label) {
		return getVertex(label.<P,L>getValue());
	}
	public Vertex getVertex(FlowValue<P,L> label) {
		return getVertex(Value.<P,L>getValue(label));
	}
	public Vertex getVertex(FlowValue<P,L> label, Vertex child) {
		return getVertex(Value.<P,L>getValue(label), child);
	}
	public Vertex getVertex(FlowValue<P,L> label, Vertex... children) {
		return getVertex(Value.<P,L>getValue(label), children);
	}
	public Vertex getVertex(FlowValue<P,L> label,
			List<? extends Vertex> children) {
		return getVertex(Value.<P,L>getValue(label), children);
	}
	public Vertex getVertex(L label) {
		return getVertex(FlowValue.<P,L>createDomain(label, mAmbassador));
	}
	public Vertex getVertex(L label, Vertex child) {
		return getVertex(FlowValue.<P,L>createDomain(label, mAmbassador),
				child);
	}
	public Vertex getVertex(L label, Vertex... children) {
		return getVertex(FlowValue.<P,L>createDomain(label, mAmbassador),
				children);
	}
	public Vertex getVertex(L label, List<? extends Vertex> children) {
		return getVertex(FlowValue.<P,L>createDomain(label, mAmbassador),
				children);
	}
	
	public boolean containsVariable(Variable variable) {
		return mLeaves.containsKey(variable.<P,L>getValue());
	}
	
	protected Vertex makeVertex(Value<P,L> label) {
		return new LeafVertex(label);
	}
	protected Vertex makeVertex(Value<P,L> label, Vertex child) {
		return new UniVertex(label, child.getRewriteSelf());
	}
	protected Vertex makeVertex(Value<P,L> label, Vertex... children) {
		return new MultiVertex(label, children);
	}
	protected Vertex makeVertex(Value<P,L> label,
			List<? extends Vertex> children) {
		Vertex[] array = new ReversionGraph.Vertex[children.size()];
		for (int i = array.length; i-- != 0; )
			array[i] = children.get(i).getRewriteSelf();
		return new MultiVertex(label, array);
	}
	
	public abstract class Vertex
			extends AbstractVertex<ReversionGraph<P,L>,Vertex> implements
			RecursiveExpressionVertex<ReversionGraph<P,L>,Vertex,Value<P,L>>,
			Taggable {
		protected final Value<P,L> mLabel;
		protected final Set<Vertex> mParents = new IdentityHashSet<Vertex>();
		protected Vertex mRewrite = null;
		protected BitIntSet mVariance = new BitIntSet();
		protected boolean mLocked = false;
		protected Tag mTagLabel;
		protected Object mTag;
		protected Variable mVariable;

		public Vertex(Value<P,L> label) {
			mLabel = label;
			if (mLabel != null) {
				mContainedBlocks |= mLabel.isLoop() || mLabel.isBranch();
				mContainedEvals |= label.containsEval();
				mContainedPhis |= label.containsPhi();
			}
		}

		public ReversionGraph<P,L> getGraph() {return ReversionGraph.this;}
		public Vertex getSelf() {return this;}

		public Value<P,L> getLabel() {return mLabel;}
		public boolean isConstant() {return false;}

		public boolean hasLabel(Value<P,L> label) {
			return label == null ? getLabel() == null : label
					.equals(getLabel());
		}

		public Collection<? extends Vertex> getParents() {return mParents;}
		
		public boolean isRewritten() {return mRewrite != null;}
		public Vertex getRewrite() {return mRewrite.getRewriteSelf();}
		public Vertex getRewriteSelf() {
			return mRewrite == null ? this : mRewrite.getRewriteSelf();
		}

		protected void addParent(Vertex parent) {
			if (mRewrite != null)
				throw new IllegalStateException();
			mParents.add(parent);
		}

		protected void addParents(Collection<? extends Vertex> parents) {
			if (mRewrite != null)
				throw new IllegalStateException();
			mParents.addAll(parents);
		}

		public abstract List<? extends Vertex> getChildren();

		public boolean isSignificant() {
			return getGraph().mSignificant.contains(this);
		}

		public boolean makeSignificant() {
			if (mRewrite != null)
				throw new IllegalStateException();
			return getGraph().mSignificant.add(getSelf());
		}

		public boolean unmakeSignificant() {
			if (mRewrite != null)
				throw new IllegalStateException();
			return getGraph().mSignificant.remove(getSelf());
		}

		public String toString() {
			return (mLabel == null ? "<null>" : mLabel.toString()) + " "
					+ (isSignificant() ? "$ " : "")
					//+ mVariance
					//+ (mTagLabel == null ? "" : mTagLabel)
					//+ (mTagLabel == null ? "" : "[" + mTagLabel
					+ (mTag == null ? "" : ":" + mTag)
					//+ (mTag == null ? "" : ":...")
					//+ "]")
					//+ isRewritten()
					//+ (mVariable == null ? "" : "[" + mVariable + "]")
					;
		}
		
		public boolean isPlaceHolder() {return false;}
		public void replaceWith(Vertex replace) {rewrite(replace);}
		public void rewrite(Vertex rewrite) {
			if (mRewrite != null) {
				if (mRewrite.equals(rewrite))
					return;
				throw new IllegalStateException();
			}
			if (rewrite.isRewritten() || rewrite == this)
				throw new IllegalArgumentException();
			if (isSignificant()) {
				rewrite.makeSignificant();
				unmakeSignificant();
			}
			mRewrite = rewrite;
			if ((mLabel == null && !mRewrite.mVariance.isEmpty())
					|| !mVariance.containsAll(mRewrite.mVariance))
				for (IntIterator variance = mRewrite.mVariance.iterator();
						variance.hasNext(); ) {
					int variant = variance.nextInt();
					for (Vertex parent : getParents())
						parent.considerVariant(variant, this);
				}
			for (Vertex parent : mParents)
				parent.rewriteChild(this);
			mVariance = mRewrite.mVariance;
			mParents.clear();
			if (mRewrite.mVariable == null)
				mRewrite.mVariable = mVariable;
		}
		
		protected void considerVariant(int loop, Vertex child) {
			if (mVariance.add(loop))
				for (Vertex parent : getParents())
					parent.considerVariant(loop, this);
		}
		
		protected void rewriteChild(Vertex child) {
			getRewriteSelf().checkRewrite();
		}
		
		public boolean isVariant() {return !mVariance.isEmpty();}
		public boolean isVariant(int loop) {return mVariance.contains(loop);}
		public boolean isOnlyVariant(int loop) {
			return isVariant(loop) && mVariance.size() == 1;
		}
		public int getMaxVariant() {
			return mVariance.isEmpty() ? 0 : mVariance.lastInt();
		}
		
		public boolean isLocked() {return mLocked;}
		public void lock() {mLocked = true;}
		public void unlock() {mLocked = false;}
		
		public boolean hasTag(Tag label) {return label.equals(mTagLabel);}
		public <T> T getTag(Tag<T> label) {
			return hasTag(label) ? (T)mTag : null;
		}
		public void setTag(Tag<Void> label) {setTag(label, null);}
		public <T> T setTag(Tag<T> label, T tag) {
			T old = mTagLabel != null && mTagLabel.equals(label)
					? (T)mTag : null;
			mTagLabel = label;
			mTag = tag;
			return old;
		}
		public <T> T removeTag(Tag<T> label) {
			if (mTagLabel != null && mTagLabel.equals(label)) {
				mTagLabel = null;
				T old = (T)mTag;
				mTag = null;
				return old;
			} else
				return null;
		}
		public String tagsToString() {
			return mTagLabel == null ? "[]"
					: "[" + mTagLabel + (mTag == null ? "" : "->" + mTag) + "]";
		}
		public void clearTag() {
			mTagLabel = null;
			mTag = null;
		}
		
		public void setVariable(Variable variable) {
			if (mVariable != null && !mVariable.equals(variable))
				throw new IllegalStateException();
			mVariable = variable;
		}
		public boolean hasVariable() {return mVariable != null;}
		public Variable getVariable() {
			if (mVariable == null)
				mVariable = new Variable();
			return mVariable;
		}
		
		public boolean isFree() {return mLabel.isFree(mAmbassador);}
		
		public boolean isAnyVolatile() {
			return mLabel.isAnyVolatile(mAmbassador);
		}
		public boolean isVolatile(int child) {
			return mLabel.isVolatile(mAmbassador, child);
		}
		
		public boolean isEquivalent(int child, Vertex that, int thatChild) {
			return mLabel.isEquivalent(this, child, that, thatChild);
		}
		
		public Value<P,L> getChainVersion(int child) {
			return mLabel.getChainVersion(mAmbassador, child);
		}
		
		public Vertex getChainProjectVolatile(int child, Vertex chained) {
			Value<P,L> project
					= mLabel.getChainProjectVolatile(mAmbassador, child);
			if (project == null)
				return chained;
			else
				return getVertex(project, chained);
		}
		
		public void rewriteChainProjectValue(int child, Vertex chained) {
			Value<P,L> project
					= mLabel.getChainProjectValue(mAmbassador, child);
			if (project != null)
				rewrite(getVertex(project, chained));
			else if (!equals(chained))
				rewrite(chained);
		}
		
		public void getUses(int child,
				Collection<? super PairInt<OpExpression<L>>> uses) {
			mLabel.getUses(this, child, uses);
		}
		
		protected void checkRewrite() {
			if (mLabel == null || mPlaceHolders != 0)
				return;
			for (int i = getChildCount(); i-- != 0; )
				if (getChild(i).mLabel == null)
					return;
			Vertex rewrite = mLabel.rewrite(this);
			if (rewrite != this)
				rewrite(rewrite);
		}
		
		public FlowValue<P,L> getHead() {return mLabel.getHead();}
		public Vertex getTail() {return mLabel.getTail(this);}
		public List<? extends Vertex> getTails() {return mLabel.getTails(this);}
		public Vertex getTail(int tail) {return getTails().get(tail);}
		public boolean isPhi() {
			return mLabel.getHead() != null && mLabel.getHead().isPhi();
		}
		public boolean isTheta() {
			return mLabel.getHead() != null && mLabel.getHead().isTheta();
		}
		public boolean isEval() {
			return mLabel.getHead() != null && mLabel.getHead().isEval();
		}
		public boolean isPass() {
			return mLabel.getHead() != null && mLabel.getHead().isPass();
		}
		
		public Vertex getShift(int loop) {
			if (!mVariance.contains(loop))
				return this;
			else
				return getVertex(FlowValue.<P,L>createShift(loop), this);
		}
		public Vertex getEval0(int loop) {
			if (!mVariance.contains(loop))
				return this;
			else
				return getVertex(FlowValue.<P,L>createEval(loop), this,
						getVertex(FlowValue.<P,L>createZero()));
		}
		
		public boolean isVariable() {return mLabel.isVariable();}
		public boolean isParameter() {
			return mLabel.getHead() != null && mLabel.getHead().isParameter();
		}
		
		public boolean needsAnyChild() {
			for (int i = getChildCount(); i-- != 0; )
				if (needsChild(i))
					return true;
			return false;
		}
		public boolean needsChild(int child) {
			return mLabel != null && mLabel.needsChild(mAmbassador, child);
		}
		
		public OpExpression<L> getExpression() {
			return mLabel.getExpression(this);
		}
	}
	
	protected class HolderVertex extends Vertex {
		public HolderVertex() {super(null); mPlaceHolders++;}

		public List<? extends Vertex> getChildren() {
			return Collections.<Vertex>emptyList();
		}
		
		public void rewrite(Vertex rewrite) {
			if (mRewrite == null) {
				super.rewrite(rewrite);
				if (mRewrite != null && --mPlaceHolders == 0)
					ReversionGraph.this.checkRewrite();
			} else
				super.rewrite(rewrite);
		}

		protected void rewriteChild(Vertex child) {
			throw new UnsupportedOperationException();
		}

		public <E> E evaluate(VariaticFunction<Value<P,L>,E,E> evaluator) {
			throw new UnsupportedOperationException();
		}
		public <E> E evaluateVertex(
				VariaticFunction<? super Vertex,E,E> evaluator) {
			throw new UnsupportedOperationException();
		}

		public Vertex getChild(int i) {
			throw new IndexOutOfBoundsException();
		}

		public boolean hasChildren(Vertex... children) {
			return children.length == 0;
		}

		public boolean hasChildren(List<? extends Vertex> children) {
			return children.isEmpty();
		}
		
		protected void checkStickyOrConstants() {}
		
		public boolean isConstant() {throw new UnsupportedOperationException();}
		
		public boolean isPlaceHolder() {return true;}
	}

	protected class LeafVertex extends Vertex {
		public LeafVertex(Value<P,L> label) {
			super(label);
			if (label.isVariable())
				mVariable = label.getVariable();
			mLabel.getVariance(mVariance, Collections.<BitIntSet>emptyList());
			checkRewrite();
		}

		public boolean hasChildren() {return false;}

		public boolean hasChildren(Vertex... children) {
			return children.length == 0;
		}

		public boolean hasChildren(List<? extends Vertex> children) {
			return children.isEmpty();
		}

		public List<? extends Vertex> getChildren() {
			return Collections.<Vertex>emptyList();
		}

		public Vertex getChild(int i) {return getChildren().get(i);}

		public int getChildCount() {return 0;}

		public <E> E evaluate(VariaticFunction<Value<P,L>,E,E> evaluator) {
			return evaluator.get(getLabel());
		}
		public <E> E evaluateVertex(
				VariaticFunction<? super Vertex,E,E> evaluator) {
			return evaluator.get(this);
		}
		
		protected void rewriteChild(Vertex child) {super.rewriteChild(child);}
		
		public boolean isConstant() {
			return mLabel.canPreEvaluate(mAmbassador)
					|| mLabel.isFree(mAmbassador);
		}
	}

	protected class UniVertex extends Vertex {
		protected Vertex mChild;

		public UniVertex(Value<P,L> label, Vertex child) {
			super(label);
			mChild = child;
			mChild.addParent(this);
			if (label.isProject())
				mVariable = label.getVariable();
			mLabel.getVariance(mVariance,
					Collections.singletonList(mChild.mVariance));
			checkRewrite();
		}

		public boolean hasChildren() {return true;}

		public boolean hasChildren(Vertex... children) {
			return children.length == 1 && mChild.equals(children[0]);
		}

		public boolean hasChildren(List<? extends Vertex> children) {
			return children.size() == 1 && mChild.equals(children.get(0));
		}

		public List<? extends Vertex> getChildren() {
			return Collections.singletonList(mChild);
		}

		public Vertex getChild(int i) {
			if (i == 0)
				return mChild;
			return getChildren().get(i);
		}

		public int getChildCount() {return 1;}

		public <E> E evaluate(VariaticFunction<Value<P,L>,E,E> evaluator) {
			return evaluator.get(getLabel(), mChild.evaluate(evaluator));
		}
		public <E> E evaluateVertex(
				VariaticFunction<? super Vertex,E,E> evaluator) {
			return evaluator.get(this, mChild.evaluateVertex(evaluator));
		}
		
		protected void rewriteChild(Vertex child) {
			mChild = child.getRewrite();
			mChild.addParent(this);
			super.rewriteChild(child);
		}
		
		protected void considerVariant(int loop, Vertex child) {
			if (mLabel.considerVariant(loop, 0))
				super.considerVariant(loop, child);
		}
	}

	protected class MultiVertex extends Vertex {
		protected final Vertex[] mChildren;

		public MultiVertex(Value<P,L> label, Vertex[] children) {
			super(label);
			mChildren = children;
			for (Vertex child : children)
				child.addParent(this);
			mLabel.getVariance(mVariance, new MappedList<Vertex,BitIntSet>() {
				MultiVertex poop = MultiVertex.this;
				private final List<Vertex> mWrapped = Arrays.asList(poop.mChildren);
				public List<Vertex> getWrapped() {return mWrapped;}
				public BitIntSet map(Vertex child) {return child.mVariance;}
			});
			checkRewrite();
		}

		public boolean hasChildren() {return true;}

		public boolean hasChildren(Vertex... children) {
			return Arrays.equals(mChildren, children);
		}

		public boolean hasChildren(List<? extends Vertex> children) {
			return Arrays.asList(mChildren).equals(children);
		}

		public List<? extends Vertex> getChildren() {
			return Arrays.asList(mChildren);
		}

		public Vertex getChild(int i) {return mChildren[i];}

		public int getChildCount() {return mChildren.length;}

		public <E> E evaluate(VariaticFunction<Value<P,L>,E,E> evaluator) {
			List<E> values = new ArrayList<E>(mChildren.length);
			for (int child = 0; child != mChildren.length; child++)
				values.add(mChildren[child].evaluate(evaluator));
			return evaluator.get(getLabel(), values);
		}
		public <E> E evaluateVertex(
				VariaticFunction<? super Vertex,E,E> evaluator) {
			List<E> values = new ArrayList<E>(mChildren.length);
			for (int child = 0; child != mChildren.length; child++)
				values.add(mChildren[child].evaluateVertex(evaluator));
			return evaluator.get(this, values);
		}
		
		protected void rewriteChild(Vertex child) {
			for (int i = mChildren.length; i-- != 0; )
				if (mChildren[i].equals(child))
					mChildren[i] = child.getRewrite();
			child.getRewrite().addParent(this);
			super.rewriteChild(child);
		}
		
		protected void considerVariant(int loop, Vertex child) {
			for (int i = mChildren.length; i-- != 0; )
				if (mChildren[i].equals(child)
						&& mLabel.considerVariant(loop, i)) {
					super.considerVariant(loop, child);
					return;
				}
		}
	}
	
	public String toString() {
		StringBuilder string = new StringBuilder("digraph {\nordering=out;\n");
		for (Vertex vertex : getVertices()) {
			string.append(vertex.hashCode());
			string.append(" [label=\"");
			string.append(vertex.toString());
			string.append("\"];\n");
		}
		for (Vertex vertex : getVertices()) {
			for (Vertex child : vertex.getChildren()) {
				string.append(vertex.hashCode());
				string.append(" -> ");
				string.append(child.hashCode());
				string.append(";\n");
			}
			if (vertex.isRewritten()) {
				string.append(vertex.hashCode());
				string.append(" -> ");
				string.append(vertex.mRewrite.hashCode());
				string.append(" [style=dashed,constraint=false];\n");
			}
		}
		string.append("}\n");
		return string.toString();
	}
}
