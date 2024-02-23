package util.graph;

import java.util.Arrays;
import java.util.List;

public class CExpressionGraph<L> extends ExpressionGraph<CExpressionGraph<L>,
		CExpressionGraph.Vertex<L>,L> {
	public CExpressionGraph<L> getSelf() {return this;}
	
	protected Vertex<L> makeVertex(L label) {return new Vertex<L>(this, label);}
	protected Vertex<L> makeVertex(L label, Vertex<L> child) {
		return new Vertex<L>(this, label, child);
	}
	protected Vertex<L> makeVertex(L label, Vertex<L>... children) {
		return makeVertex(label, Arrays.asList(children));
	}
	
	protected Vertex<L> makeVertex(L label,
			List<? extends Vertex<L>> children) {
		Vertex<L>[] array = new Vertex[children.size()];
		children.toArray(array);
		return new Vertex<L>(this, label, array);
	}
	
	public static class Vertex<L>
			extends ExpressionGraph.Vertex<CExpressionGraph<L>,Vertex<L>,L> {
		private final CExpressionGraph<L> mGraph;
		
		public Vertex(CExpressionGraph<L> graph, L label) {
			super(label);
			mGraph = graph;
		}
		public Vertex(CExpressionGraph<L> graph, L label, Vertex<L> child) {
			super(label, child);
			mGraph = graph;
		}
		public Vertex(CExpressionGraph<L> graph,
				L label, Vertex<L>... children) {
			super(label, children);
			mGraph = graph;
		}
		
		public Vertex getSelf() {return this;}
		
		public CExpressionGraph<L> getGraph() {return mGraph;}
	}
}
