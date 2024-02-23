package util.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.IdentityHashSet;
import util.Labeled;
import util.Tag;
import util.Taggable;
import util.VariaticFunction;

public abstract class ExpressionGraph
		<G, V extends OrderedVertex<?,V> & Labeled<L>, L>
		extends AbstractGraph<G,V> {
	protected final Set<V> mVertices = new HashSet<V>();
	protected final Map<L,V> mLeaves = new HashMap<L,V>();
	protected final Set<V> mSignificant = new HashSet<V>();
	
	public Set<? extends V> getVertices() {return mVertices;}
	
	public Collection<? extends V> getLeaves() {return mLeaves.values();}
	
	public void makeSignificant(V vertex) {mSignificant.add(vertex);}
	public void clearSignificance() {mSignificant.clear();}

	public void trimInsignificant() {
		mVertices.clear();
		getSignificantDescendents(mVertices);
		for (V vertex : mVertices)
			vertex.getParents().retainAll(mVertices);
		mLeaves.values().retainAll(mVertices);
	}
	
	public Set<? extends V> getSignificantDescendents() {
		Set<V> descendents = new HashSet<V>();
		getSignificantDescendents(descendents);
		return descendents;
	}
	
	public void getSignificantDescendents(Set<? super V> descendents) {
		for (V vertex : mSignificant)
			getDescendents(vertex, descendents);
	}
	
	public Set<? extends V> getDescendents(V vertex) {
		Set<V> descendents = new HashSet<V>();
		getDescendents(vertex, descendents);
		return descendents;
	}
	
	public void getDescendents(V vertex, Set<? super V> descendents) {
		if (descendents.add(vertex))
			for (V child : vertex.getChildren())
				getDescendents(child, descendents);
	}
	
	public Set<? extends V> getSignificant() {return mSignificant;}
	
	protected V addVertex(V vertex) {
		mVertices.add(vertex);
		return vertex;
	}
	
	public V getVertex(L label) {
		V leaf = mLeaves.get(label);
		if (leaf == null)
			mLeaves.put(label, leaf = addVertex(makeVertex(label)));
		return leaf;
	}
	
	public V getVertex(L label, V child) {
		for (V parent : child.getParents())
			if (parent.getChildCount() == 1 && parent.hasLabel(label))
				return parent;
		V vertex = addVertex(makeVertex(label, child));
		return vertex;
	}
	
	public V getVertex(L label, V... children) {
		if (children.length == 0)
			return getVertex(label);
		if (children.length == 1)
			return getVertex(label, children[0]);
		for (V parent : children[0].getParents())
			if (parent.hasLabel(label) && parent.hasChildren(children))
				return parent;
		return addVertex(makeVertex(label, children));
	}
	
	public V getVertex(L label, List<? extends V> children) {
		if (children.isEmpty())
			return getVertex(label);
		if (children.size() == 1)
			return getVertex(label, children.get(0));
		for (V parent : children.get(0).getParents())
			if (parent.hasLabel(label) && parent.hasChildren(children))
				return parent;
		return addVertex(makeVertex(label, children));
	}
	
	protected abstract V makeVertex(L label);
	protected abstract V makeVertex(L label, V child);
	protected abstract V makeVertex(L label, V... children);
	protected abstract V makeVertex(L label, List<? extends V> children);
	
	public String toString() {
		StringBuilder string = new StringBuilder("digraph {\nordering=out;\n");
		for (V vertex : getVertices())
			if (vertex.hasChildren()) {
				string.append(vertex.hashCode());
				string.append(" [label=\"");
				string.append(vertex.toString().replace("\n", "\\n"));
				string.append("\"];\n");
			} else
				for (V parent : vertex.getParents()) {
					string.append(parent.hashCode());
					string.append(vertex.hashCode());
					string.append(" [label=\"");
					string.append(vertex.toString().replace("\n", "\\n"));
					string.append("\"];\n");
				}
		for (V vertex : getVertices())
			for (V child : vertex.getChildren()) {
				string.append(vertex.hashCode());
				string.append(" -> ");
				if (!child.hasChildren())
					string.append(vertex.hashCode());
				string.append(child.hashCode());
				string.append(";\n");
			}
		string.append("}\n");
		return string.toString();
	}
	
	public interface IVertex<G extends ExpressionGraph<G,V,L>,
			V extends IVertex<G,V,L>, L>
			extends ExpressionVertex<G,V,L>, Taggable {
		public void addParent(V parent);
		public void addParents(Collection<? extends V> parents);
	}

	public static abstract class Vertex<G extends ExpressionGraph<G,V,L>,
			V extends IVertex<G,V,L>, L> extends TaggableVertex<G,V>
			implements IVertex<G,V,L> {
		protected final L mLabel;
		protected final List<? extends V> mChildren;
		protected Set<V> mParents = new IdentityHashSet<V>();
		
		private Vertex(L label, List<? extends V> children) {
			mLabel = label;
			checkTags();
			mChildren = children;
			for (V child : mChildren)
				child.addParent(getSelf());
		}
		public Vertex(L label) {this(label, Collections.<V>emptyList());}
		public Vertex(L label, V child) {
			this(label, Collections.singletonList(child));
		}
		public Vertex(L label, V... children) {
			this(label, Arrays.asList(children));
		}
		
		public L getLabel() {return mLabel;}
		public boolean hasLabel(L label) {
			return label == null ? getLabel() == null
					: label.equals(getLabel());
		}
		
		public Set<V> getParents() {return mParents;}
		public void addParent(V parent) {mParents.add(parent);}
		public void addParents(Collection<? extends V> parents) {
			mParents.addAll(parents);
		}
		public void setParents(Set<V> parents) {mParents = parents;}
		
		public Map<Tag,Object> getTags() {return mTags;}
		public void setTags(Map<Tag,Object> tags) {mTags = tags;}
		
		public List<? extends V> getChildren() {return mChildren;}
		public V getChild(int i) {return mChildren.get(i);}
		public int getChildCount() {return mChildren.size();}
		public boolean hasChildren(V... children) {
			return mChildren.equals(Arrays.asList(children));
		}
		public boolean hasChildren(List<? extends V> children) {
			return mChildren.equals(children);
		}
		
		public boolean isSignificant() {
			return getGraph().mSignificant.contains(this);
		}
		public boolean makeSignificant() {
			return getGraph().mSignificant.add(getSelf());
		}
		public boolean unmakeSignificant() {
			return getGraph().mSignificant.remove(getSelf());
		}

		public <E> E evaluate(VariaticFunction<L,E,E> evaluator) {
			List<E> values = new ArrayList<E>(mChildren.size());
			for (V child : mChildren)
				values.add(child.evaluate(evaluator));
			return evaluator.get(getLabel(), values);
		}
		public <E> E evaluateVertex(VariaticFunction<? super V,E,E> evaluator) {
			List<E> values = new ArrayList<E>(mChildren.size());
			for (V child : mChildren)
				values.add(child.evaluateVertex(evaluator));
			return evaluator.get(getSelf(), values);
		}
		
		public String toString() {
			return mLabel == null ? "<null>" : mLabel.toString();
		}
		
		public boolean equals(Object that) {
			return this == that || that != null && that instanceof Vertex
					&& equals(((Vertex<G,V,L>)that).getSelf());
		}
		public boolean equals(V that) {return mParents == that.getParents();}
		public int hashCode() {return System.identityHashCode(mParents);}
	}
}
