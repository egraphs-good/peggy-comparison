package peggy.represent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import util.Tag;
import util.graph.CRecursiveExpressionGraph;
import eqsat.FlowValue;

/**
 * This is a PEG implementation that uses a CREG underneath.
 */
public class CREGPEG<L,P,R> extends AbstractPEG<FlowValue<P,L>,R,CREGPEG<L,P,R>,CREGPEG<L,P,R>.Vertex> {
	protected final Map<CRecursiveExpressionGraph.Vertex<FlowValue<P,L>>,Vertex> inner2outer = 
		new HashMap<CRecursiveExpressionGraph.Vertex<FlowValue<P,L>>, Vertex>();
	protected final Map<R,Vertex> returnMap = new HashMap<R,Vertex>();
	protected final Set<R> returns;
	
	public class Vertex implements PEG.Vertex<FlowValue<P,L>,R,CREGPEG<L,P,R>,CREGPEG<L,P,R>.Vertex> {
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
		
		protected final CRecursiveExpressionGraph.Vertex<FlowValue<P,L>> inner;
		protected Vertex(CRecursiveExpressionGraph.Vertex<FlowValue<P,L>> _inner) {
			this.inner = _inner;
		}
		public Vertex getChild(int i) {return fetchOuter(this.inner.getChild(i));}
		public int getChildCount() {return inner.getChildCount();}
		public FlowValue<P,L> getLabel() {return this.inner.getLabel();}
		public CREGPEG<L,P,R> getPEG() {return CREGPEG.this;}
		public boolean isRoot() {
			return getPEG().getReturnVertices().contains(this);
		}
	}
	
	public CREGPEG(Map<R,CRecursiveExpressionGraph.Vertex<FlowValue<P,L>>> map) {
		for (R arr : map.keySet()) {
			this.returnMap.put(arr, fetchOuter(map.get(arr)));
		}
		this.returns = Collections.unmodifiableSet(this.returnMap.keySet());
	}
	protected Vertex fetchOuter(CRecursiveExpressionGraph.Vertex<FlowValue<P,L>> inner) {
		if (inner2outer.containsKey(inner)) {
			return inner2outer.get(inner);
		} else {
			Vertex result = new Vertex(inner);
			inner2outer.put(inner, result);
			return result;
		}
	}
	public Vertex getReturnVertex(R arr) {return this.returnMap.get(arr);}
	public Set<? extends R> getReturns() {return this.returns;}
}
