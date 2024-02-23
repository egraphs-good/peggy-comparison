package peggy.revert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.Pattern;
import util.graph.AbstractGraph;
import util.graph.AbstractVertex;
import static peggy.revert.MiniPEG.Vertex;

/**
 * A MiniPEG is an acylic PEG that is mutable.
 */
public class MiniPEG<L> extends AbstractGraph<MiniPEG<L>,Vertex<L>> {
	protected final Set<Vertex<L>> mVertices = new HashSet<Vertex<L>>();
	protected final Map<L,Set<Vertex<L>>> labelToVertices = 
		new HashMap<L,Set<Vertex<L>>>();
	
	// uses reference equality
	public static class Vertex<L> extends AbstractVertex<MiniPEG<L>,Vertex<L>> {
		protected final MiniPEG<L> mGraph;
		protected final L mLabel;
		protected final List<Vertex<L>> mChildren = new ArrayList<Vertex<L>>();
		
		public Vertex(MiniPEG<L> graph, L label) {
			this.mGraph = graph;
			this.mLabel = label;
		}
		public Vertex(MiniPEG<L> graph, L label, Vertex<L> child) {
			this(graph, label);
			this.addChild(child);
		}
		public Vertex(MiniPEG<L> graph,
				L label, Vertex<L>... children) {
			this(graph, label);
			for (Vertex<L> child : children)
				this.addChild(child);
		}

		public L getLabel() {return this.mLabel;}
		public Vertex<L> getSelf() {return this;}
		public MiniPEG<L> getGraph() {return this.mGraph;}
		
		public List<? extends Vertex<L>> getChildren() {
			return this.mChildren;
		}
		
		public void addChild(Vertex<L> child) {
			if (child == null)
				throw new NullPointerException();
			this.mChildren.add(child);
		}
		public Vertex<L> getChild(int index) {return this.mChildren.get(index);}
		public void setChild(int index, Vertex<L> child) {
			if (child == null)
				throw new NullPointerException();
			this.mChildren.set(index, child);
		}
		public void removeChild(int index) {
			this.mChildren.remove(index);
		}
		
		public void replaceChild(Vertex<L> oldChild, Vertex<L> newChild) {
			for (int i = 0; i < this.mChildren.size(); i++) {
				if (this.mChildren.get(i) == oldChild) {
					this.mChildren.set(i, newChild);
				}
			}
		}
		
		
		/** Includes 'this'.
		 */
		public Collection<? extends Vertex<L>> getAncestors() {
			return getAncestorsHelper(this); 
		}
		private static <L> Collection<? extends Vertex<L>> getAncestorsHelper(Vertex<L> start) {
			LinkedList<Vertex<L>> worklist = new LinkedList<Vertex<L>>();
			worklist.add(start);
			Set<Vertex<L>> seen = new HashSet<Vertex<L>>();
			while (!worklist.isEmpty()) {
				Vertex<L> next = worklist.removeFirst();
				if (seen.contains(next))
					continue;
				seen.add(next);
				worklist.addAll(next.getParents());
			}
			return seen;
		}
		
		
		/**
		 * Includes 'this'.
		 */
		public Collection<? extends Vertex<L>> getDescendents() {
			return getDescendentsHelper(this); 
		}
		private static <L> Collection<? extends Vertex<L>> getDescendentsHelper(Vertex<L> start) {
			LinkedList<Vertex<L>> worklist = new LinkedList<Vertex<L>>();
			worklist.add(start);
			Set<Vertex<L>> seen = new HashSet<Vertex<L>>();
			while (!worklist.isEmpty()) {
				Vertex<L> next = worklist.removeFirst();
				if (seen.contains(next))
					continue;
				seen.add(next);
				worklist.addAll(next.getChildren());
			}
			return seen;
		}
		

		/**
		 * Returns all ancestor vertices that satisfy the given pattern.
		 */
		public Collection<? extends Vertex<L>> findSatisfyingAncestors(Pattern<Vertex<L>> pattern) {
			Collection<? extends Vertex<L>> satisfying = 
				this.getGraph().findSatisfying(pattern);
			satisfying.retainAll(this.getAncestors());
			return satisfying;
		}
		
		
		/**
		 * Returns all descendent vertices that satisfy the given pattern.
		 */
		public Collection<? extends Vertex<L>> findSatisfyingDescendents(Pattern<Vertex<L>> pattern) {
			Collection<? extends Vertex<L>> satisfying = 
				this.getGraph().findSatisfying(pattern);
			satisfying.retainAll(this.getDescendents());
			return satisfying;
		}
		
		public String toString() {
			return this.mLabel + "[" + this.mChildren.size() + "]";
		}
	}

	public MiniPEG<L> getSelf() {return this;}
	public Collection<? extends Vertex<L>> getVertices() {return this.mVertices;}
	

	protected Vertex<L> makeVertex(L label) {
		return makeVertex(label, new ArrayList<Vertex<L>>());
	}
	protected Vertex<L> makeVertex(L label, Vertex<L> child) {
		return makeVertex(label, Collections.singletonList(child));
	}
	protected Vertex<L> makeVertex(L label, Vertex<L>... children) {
		return makeVertex(label, Arrays.asList(children));
	}
	protected Vertex<L> makeVertex(L label,
			List<? extends Vertex<L>> children) {
		Vertex<L>[] array = new Vertex[children.size()];
		children.toArray(array);
		
		Vertex<L> vertex = new Vertex<L>(this, label, array);
		this.mVertices.add(vertex);
		Set<Vertex<L>> set = this.labelToVertices.get(label);
		if (set == null) {
			set = new HashSet<Vertex<L>>();
			this.labelToVertices.put(label, set);
		}
		set.add(vertex);
		return vertex;
	}
	
	
	protected Vertex<L> findVertex(L label, List<? extends Vertex<L>> children) {
		Set<Vertex<L>> set = this.labelToVertices.get(label);
		if (set == null)
			return null;
		toploop:
		for (Vertex<L> vertex : set) {
			if (vertex.getChildCount() != children.size())
				continue;
			for (int i = 0; i < children.size(); i++) {
				if (!children.get(i).equals(vertex.getChild(i)))
					continue toploop;
			}
			return vertex;
		}
		return null;
	}
	
	public Vertex<L> getVertex(L label) {
		return getVertex(label, Collections.EMPTY_LIST);
	}
	public Vertex<L> getVertex(L label, Vertex<L>... vertices) {
		return getVertex(label, Arrays.asList(vertices));
	}
	public Vertex<L> getVertex(L label, List<? extends Vertex<L>> vertices) {
		Vertex<L> vertex = findVertex(label, vertices);
		if (vertex == null)
			vertex = makeVertex(label, vertices);
		return vertex;
	}
	
	public void removeVertex(Vertex<L> vertex) {
		this.removeVertices(Collections.singleton(vertex));
	}
	public void removeVertices(Collection<? extends Vertex<L>> vertices) {
		// remove vertex and all parents (recursively)
		LinkedList<Vertex<L>> queue = new LinkedList<Vertex<L>>();
		queue.addAll(vertices);
		
		while (!queue.isEmpty()) {
			Vertex<L> next = queue.removeFirst();
			if (this.mVertices.contains(next))
				continue;
			queue.addAll(next.getParents());
			this.mVertices.remove(next);
		}
	}
	
	public Collection<? extends Vertex<L>> findSatisfying(Pattern<Vertex<L>> pattern) {
		Set<Vertex<L>> matching = new HashSet<Vertex<L>>();
		for (Vertex<L> vertex : this.mVertices) {
			if (pattern.matches(vertex))
				matching.add(vertex);
		}
		return matching;
	}
}
