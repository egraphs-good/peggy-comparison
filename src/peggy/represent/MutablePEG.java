package peggy.represent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.Tag;
import util.graph.CRecursiveExpressionGraph;
import eqsat.FlowValue;

/**
 * This is an implementation of PEG that is explicitly mutable.
 */
public class MutablePEG<T,R> extends AbstractPEG<T,R,MutablePEG<T,R>,MutablePEG<T,R>.MutableVertex> {
	public class MutableVertex implements PEG.Vertex<T,R,MutablePEG<T,R>,MutablePEG<T,R>.MutableVertex> {
		protected final Set<MutableVertex> parents = new HashSet<MutableVertex>();
		protected final T label;
		protected final List<MutableVertex> children = new ArrayList<MutableVertex>();
		public MutableVertex(T _label) {this.label = _label;}
		public int getChildCount() {return this.children.size();}
		public MutableVertex getChild(int i) {return this.children.get(i);}
		public T getLabel() {return this.label;}
		public MutablePEG<T,R> getPEG() {return MutablePEG.this;}
		public void addChild(MutableVertex v) {
			this.children.add(v);
			v.parents.add(this);
		}
		public void removeChild(int i) {
			MutableVertex v = this.children.remove(i);
			if (!this.children.contains(v))
				v.parents.remove(this);
		}
		public void replaceChild(MutableVertex oldV, MutableVertex newV) {
			for (int i = 0; i < this.children.size(); i++) {
				if (this.children.get(i).equals(oldV)) {
					this.children.set(i, newV);
				}
			}
			oldV.parents.remove(this);
			newV.parents.add(this);
		}
		
		public Set<? extends MutableVertex> getParents() {return this.parents;}
		public boolean isRoot() {
			return getPEG().getReturnVertices().contains(this);
		}

		protected final Map<Tag,Object> tags = new HashMap<Tag,Object>();
		public <E> E getTag(Tag<E> label) {return (E)tags.get(label);}
		public boolean hasTag(Tag label) {return tags.containsKey(label);}
		public <E> E removeTag(Tag<E> label) {return (E)tags.remove(label);}
		public void setTag(Tag<Void> label) {setTag(label, null);}
		public <E> E setTag(Tag<E> label, E tag) {
			E result = getTag(label);
			tags.put(label, tag);
			return result;
		}
		public String tagsToString() {return tags.toString();}
	}
	
	protected final Map<R,MutableVertex> rootMap = new HashMap<R,MutableVertex>();
	public MutableVertex getReturnVertex(R arr) {return this.rootMap.get(arr);}
	public Set<? extends R> getReturns() {return this.rootMap.keySet();}
	public void setReturnVertex(R arr, MutableVertex v) {this.rootMap.put(arr, v);}
	public void removeReturn(R arr) {this.rootMap.remove(arr);}

	/**
	 * Makes a new MutablePEG from a PEGInfo.
	 */
	public static <L,P,R> MutablePEG<FlowValue<P,L>,R> fromPEGInfo(PEGInfo<L,P,R> peg) {
		final MutablePEG<FlowValue<P,L>,R> result = new MutablePEG<FlowValue<P,L>,R>();
		final Map<CRecursiveExpressionGraph.Vertex<FlowValue<P,L>>,MutablePEG<FlowValue<P,L>,R>.MutableVertex> cache = 
			new HashMap<CRecursiveExpressionGraph.Vertex<FlowValue<P,L>>, MutablePEG<FlowValue<P,L>,R>.MutableVertex>();
		for (R arr : peg.getReturns()) {
			result.setReturnVertex(arr, MutablePEG.<L,P,R>getVertex(result, peg.getReturnVertex(arr), cache));
		}
		return result;
	}
	private static <L,P,R> MutablePEG<FlowValue<P,L>,R>.MutableVertex getVertex(
			MutablePEG<FlowValue<P,L>,R> peg,
			CRecursiveExpressionGraph.Vertex<FlowValue<P,L>> vertex,
			Map<CRecursiveExpressionGraph.Vertex<FlowValue<P,L>>,MutablePEG<FlowValue<P,L>,R>.MutableVertex> cache) {
		if (cache.containsKey(vertex))
			return cache.get(vertex);
		MutablePEG<FlowValue<P,L>,R>.MutableVertex mv = 
			peg.new MutableVertex(vertex.getLabel());
		cache.put(vertex, mv);
		for (int i = 0; i < vertex.getChildCount(); i++) {
			mv.addChild(getVertex(peg, vertex.getChild(i), cache));
		}
		return mv;
	}
}
