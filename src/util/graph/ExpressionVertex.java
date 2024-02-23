package util.graph;

import util.Labeled;
import util.VariaticFunction;

public interface ExpressionVertex<G, V, L>
		extends OrderedVertex<G,V>, Labeled<L> {
	public boolean isSignificant();
	public boolean makeSignificant();
	public boolean unmakeSignificant();

	public <E> E evaluate(VariaticFunction<L,E,E> evaluator);
	public <E> E evaluateVertex(VariaticFunction<? super V,E,E> evaluator);
	
	public String toString();
}
