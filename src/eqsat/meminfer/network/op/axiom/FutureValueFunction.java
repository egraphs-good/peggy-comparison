package eqsat.meminfer.network.op.axiom;

import eqsat.meminfer.engine.basic.TermOrTermChild;
import eqsat.meminfer.engine.basic.Value;
import eqsat.meminfer.engine.basic.FutureExpressionGraph.Vertex;
import eqsat.meminfer.engine.op.OpTerm;
import eqsat.meminfer.engine.op.axiom.AxiomInstance;

public interface FutureValueFunction
		<O, T extends OpTerm<O,T,V>, V extends Value<T,V>> {
	Vertex<O,T,V> getVertex(AxiomInstance<O,T,V> instance);
	TermOrTermChild<T,V> getValue(AxiomInstance<O,T,V> instance);
}
