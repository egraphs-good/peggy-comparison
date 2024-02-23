package util.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import util.Collections;
import util.Function;

public class UniqueLabeledGraph<L> extends
		GenericGraph<UniqueLabeledGraph<L>,UniqueLabeledGraph.Vertex<L>> {
	protected final Map<L,Vertex<L>> mLabels = new HashMap<L,Vertex<L>>();
	
	public UniqueLabeledGraph<L> getSelf() {return this;}
	
	public Vertex<L> getVertex(L label) {
		Vertex<L> vertex = mLabels.get(label);
		if (vertex != null)
			return vertex;
		vertex = new Vertex<L>(this, label);
		mLabels.put(label, vertex);
		addVertex(vertex);
		return vertex;
	}
	
	public Collection<? extends L> getLabels(Collection<? extends Vertex<L>>
			vertices) {
		return Collections.mapCollection(vertices, new Function<Vertex<L>,L>() {
			public L get (Vertex<L> vertex) {
				return vertex.getLabel();
			}
		});
	}
	
	public static class Vertex<L>
			extends LabeledGenericVertex<UniqueLabeledGraph<L>,Vertex<L>,L> {
		public Vertex(UniqueLabeledGraph<L> graph, L label) {
			super(graph, label);
		}
		
		public Vertex<L> getSelf() {return this;}
	}
}
