package util.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import util.ArrayCollection;
import util.Grouping;
import util.HashGrouping;
import util.IdentityHashGrouping;
import util.Labeled;
import util.Tag;
import util.VariaticFunction;

public abstract class RecursiveExpressionGraph
		<G, V extends OrderedVertex<?,V> & Labeled<L>, L>
		extends ExpressionGraph<G,V,L> {
	protected final Grouping<V> mGrouping = new IdentityHashGrouping<V>();

	public void trimInsignificant() {
		super.trimInsignificant();
		Collection<V> trimmed = new ArrayCollection<V>();
		for (V rep : mGrouping.getRepresentatives())
			if (!getVertices().contains(rep))
				trimmed.add(rep);
		for (V rep : trimmed)
			mGrouping.ungroup(rep);
	}
	
	public V createPlaceHolder() {
		return makeHolderVertex();
	}
	public V createPlaceHolder(L label) {
		return makeHolderVertex(label);
	}
	
	protected abstract V makeHolderVertex();
	protected abstract V makeHolderVertex(L label);
	
	public interface IVertex<G extends RecursiveExpressionGraph<G,V,L>,
			V extends IVertex<G,V,L>,L> extends ExpressionGraph.IVertex<G,V,L>,
			RecursiveExpressionVertex<G,V,L> {
		public void setParents(Set<V> parents);
		public Set<V> getParents();
		public Map<Tag,Object> getTags();
		public void setTags(Map<Tag,Object> tags);
	}
	
	protected static abstract class HolderVertex
			<G extends RecursiveExpressionGraph<G,V,L>,
			V extends IVertex<G,V,L>, L> extends ExpressionGraph.Vertex<G,V,L>
			implements IVertex<G,V,L> {
		protected V mReplacement = null;
		
		public HolderVertex(L label) {super(label);}
		
		public L getLabel() {
			return mReplacement == null ? super.getLabel()
					: mReplacement.getLabel();
		}
		
		public boolean hasChildren() {
			return mReplacement != null && mReplacement.hasChildren();
		}
		public boolean hasChildren(V... children) {
			return mReplacement != null && mReplacement.hasChildren(children);
		}
		public boolean hasChildren(List<? extends V> children) {
			return mReplacement != null && mReplacement.hasChildren(children);
		}
		public List<? extends V> getChildren() {
			return mReplacement == null ? Collections.<V>emptyList()
					: mReplacement.getChildren();
		}
		public V getChild(int i) {
			return mReplacement == null ? getChildren().get(i)
					: mReplacement.getChild(i);
		}
		public int getChildCount() {
			return mReplacement == null ? 0 : mReplacement.getChildCount();
		}
		
		public <E> E evaluate(VariaticFunction<L,E,E> evaluator) {
			return mReplacement == null ? evaluator.get(getLabel())
					: mReplacement.evaluate(evaluator);
		}
		public <E> E evaluateVertex(VariaticFunction<? super V,E,E> evaluator) {
			return mReplacement == null ? evaluator.get(getSelf())
					: mReplacement.evaluateVertex(evaluator);
		}
		
		public boolean isPlaceHolder() {
			return mReplacement == null || mReplacement.isPlaceHolder();
		}
		
		public void replaceWith(V replacement) {
			if (mReplacement != null)
				throw new IllegalStateException();
			if (replacement == this)
				throw new IllegalArgumentException();
			boolean hadParents = replacement.hasParents();
			mReplacement = replacement;
			replacement.addParents(mParents);
			mParents = replacement.getParents();
			mReplacement.getTags().putAll(mTags);
			mTags = mReplacement.getTags();
			getGraph().mGrouping.group(mReplacement, getSelf());
			if (hadParents) {
				Grouping<V> grouping = new HashGrouping<V>();
				mergeParents(replacement, replacement, grouping);
				applyGrouping(grouping);
			}
			if (mReplacement.isPlaceHolder())
				return;
			for (V similar : getGraph().getVertices())
				if (similar.getChildCount() == replacement.getChildCount()
						&& similar.hasLabel(replacement.getLabel())) {
					if (similar.equals(replacement))
						continue;
					Map<V,V> mapping = new HashMap<V,V>();
					if (merge(replacement, similar, mapping)) {
						Grouping<V> grouping = new HashGrouping<V>();
						for (Entry<V,V> entry : mapping.entrySet())
							grouping.group(entry.getKey(), entry.getValue());
						for (Entry<V,V> entry : mapping.entrySet())
							mergeParents(entry.getKey(), entry.getValue(),
									grouping);
						applyGrouping(grouping);
						break;
					}
				}
		}
		
		protected void mergeParents(V left, V right, Grouping<V> grouping) {
			for (V leftParent : left.getParents())
				for (V rightParent : right.getParents())
					if (leftParent.getChildCount()
							== rightParent.getChildCount()
							&& leftParent.hasLabel(rightParent.getLabel())
							&& !grouping.isGrouped(leftParent, rightParent)) {
						boolean different = false;
						for (int i = leftParent.getChildCount(); i-- != 0; )
							if (different = !grouping.isGrouped(
									leftParent.getChild(i),
									rightParent.getChild(i)))
								break;
						if (!different) {
							grouping.group(leftParent, rightParent);
							mergeParents(leftParent, rightParent, grouping);
						}
					}
		}
		
		protected boolean merge(V left, V right, Map<V,V> mapping) {
			if (left.equals(right))
				return true;
			if (left.isPlaceHolder() || right.isPlaceHolder())
				return false;
			if (left.getChildCount() != right.getChildCount())
				return false;
			if (!left.hasLabel(right.getLabel()))
				return false;
			V mapped = mapping.get(left);
			if (mapped != null)
				return mapped.equals(right);
			mapping.put(left, right);
			for (int child = 0; child < left.getChildCount(); child++)
				if (!merge(left.getChild(child), right.getChild(child),
						mapping))
					return false;
			return true;
		}
		
		protected void applyGrouping(Grouping<V> grouping) {
			G graph = getGraph();
			Set<V> vertices = graph.mVertices;
			Map<L,V> leaves = graph.mLeaves;
			Grouping<V> masterGrouping = graph.mGrouping;
			for (V rep : grouping.getRepresentatives())
				for (V friend : grouping.getGroup(rep))
					if (!rep.equals(friend)) {
						vertices.remove(friend);
						if (friend.isLeaf())
							leaves.remove(friend);
						if (friend.unmakeSignificant())
							rep.makeSignificant();
						rep.addParents(friend.getParents());
						rep.getTags().putAll(friend.getTags());
						for (V associate : masterGrouping.getGroup(friend)) {
							associate.setParents(rep.getParents());
							associate.setTags(rep.getTags());
						}
						masterGrouping.group(rep, friend);
					}
		}
		
		public String toString() {
			return mReplacement == null ? super.toString()
					: mReplacement.toString();
		}
	}
	
	public static abstract class Vertex
			<G extends RecursiveExpressionGraph<G,V,L>,
			V extends IVertex<G,V,L>, L>
			extends ExpressionGraph.Vertex<G,V,L> implements IVertex<G,V,L> {
		public Vertex(L label) {super(label);}
		public Vertex(L label, V child) {super(label, child);}
		public Vertex(L label, V... children) {super(label, children);}
		
		public boolean isPlaceHolder() {return false;}
		public void replaceWith(V replacement) {
			throw new UnsupportedOperationException();
		}
	}
}
