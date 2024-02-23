package peggy.analysis;

import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.proof.Proof;

/**
 * This describes where an axiom-created EPEG node got one of its children.
 * A child source either names another node specifically, or it can
 * describe a node indirectly.
 */
public abstract class ChildSource<L,P> {
	public boolean isConcrete() {return false;}
	public ConcreteSource<L,P> getConcreteSelf() {throw new UnsupportedOperationException();}
	
	public boolean isStolen() {return false;}
	public StolenSource<L,P> getStolenSelf() {throw new UnsupportedOperationException();}
	
	public abstract void buildProof(Proof proof);
	public abstract FutureExpressionGraph.Vertex<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> 
		buildFutureVertex(
			FutureExpressionGraph<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> futureGraph);
}
