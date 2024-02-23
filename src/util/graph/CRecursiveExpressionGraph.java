package util.graph;

import java.util.List;

public class CRecursiveExpressionGraph<L>
		extends RecursiveExpressionGraph<CRecursiveExpressionGraph<L>,
		CRecursiveExpressionGraph.Vertex<L>,L> {
	public CRecursiveExpressionGraph<L> getSelf() {return this;}
	
	protected Vertex<L> makeHolderVertex() {
		return new HolderVertex(null);
	}
	protected Vertex<L> makeHolderVertex(L label) {
		return new HolderVertex(label);
	}
	
	protected Vertex<L> makeVertex(L label) {
		return new NormalVertex(label);
	}
	
	protected Vertex<L> makeVertex(L label, Vertex<L> child) {
		return new NormalVertex(label, child);
	}
	
	protected Vertex<L> makeVertex(L label, Vertex<L>... children) {
		return new NormalVertex(label, children);
	}
	
	protected Vertex<L> makeVertex(L label,
			List<? extends Vertex<L>> children) {
		Vertex<L>[] array = new Vertex[children.size()];
		children.toArray(array);
		return new NormalVertex(label, array);
	}
	
	public interface Vertex<L> extends RecursiveExpressionGraph.IVertex
			<CRecursiveExpressionGraph<L>,Vertex<L>,L> {
	}
	
	protected class HolderVertex extends RecursiveExpressionGraph.HolderVertex
			<CRecursiveExpressionGraph<L>,Vertex<L>,L> implements Vertex<L> {
		public HolderVertex(L label) {super(label);}
		
		public Vertex<L> getSelf() {return this;}

		public CRecursiveExpressionGraph<L> getGraph() {
			return CRecursiveExpressionGraph.this;
		}
	}
	
	protected class NormalVertex extends RecursiveExpressionGraph.Vertex
			<CRecursiveExpressionGraph<L>,Vertex<L>,L> implements Vertex<L> {
		public NormalVertex(L label) {super(label);}
		public NormalVertex(L label, Vertex<L> child) {super(label, child);}
		public NormalVertex(L label, Vertex<L>... children) {
			super(label, children);
		}
		
		public Vertex<L> getSelf() {return this;}

		public CRecursiveExpressionGraph<L> getGraph() {
			return CRecursiveExpressionGraph.this;
		}
	}
	
	public String toString(boolean tags) {
		if (!tags)
			super.toString();
		StringBuilder string = new StringBuilder("digraph {\nordering=out;\n");
		for (Vertex<L> vertex : getVertices()) {
			string.append(vertex.hashCode());
			string.append(" [label=\"");
			string.append(vertex.toString());
			string.append(" ");
			string.append(vertex.getTags());
			string.append("\"];\n");
		}
		for (Vertex<L> vertex : getVertices()) {
			for (Vertex<L> child : vertex.getChildren()) {
				string.append(vertex.hashCode());
				string.append(" -> ");
				string.append(child.hashCode());
				string.append(";\n");
			}
		}
		string.append("}\n");
		return string.toString();
	}
}
