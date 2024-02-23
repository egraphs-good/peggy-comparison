package util.graph;

public interface RecursiveExpressionVertex<G, V, L>
		extends ExpressionVertex<G,V,L> {
	public boolean isPlaceHolder();
	public void replaceWith(V replacement);
}
