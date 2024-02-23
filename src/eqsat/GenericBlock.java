package eqsat;

import util.DisjointUnion;
import util.Function;
import util.VariaticFunction;
import util.graph.CExpressionGraph.Vertex;

public interface GenericBlock<G, B, V, L> extends Block<G,B,V,L> {
	public Vertex<DisjointUnion<L,V>> getInput(V variable);
	
	public Vertex<DisjointUnion<L,V>> getOutput(V variable);
	
	public void setModification(V variable, Vertex<DisjointUnion<L,V>> mod);
	
	public void setBranchCondition(Vertex<DisjointUnion<L,V>> condition);
	public <E> E getOutput(V variable, VariaticFunction<L,E,E> converter,
			Function<V,E> inputs);
	public <E> E getBranchCondition(
			VariaticFunction<L,E,E> converter, Function<V,E> inputs);
	
	public int getIndex();
	public String toString();
}
