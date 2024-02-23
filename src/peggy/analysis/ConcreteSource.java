package peggy.analysis;

import util.DisjointUnion;
import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.proof.Proof;

/**
 * This class represents a child node of an EPEG node, 
 * by specifying the node concretely.
 */
public class ConcreteSource<L,P> extends ChildSource<L,P> {
	private final DisjointUnion<FutureNode<L,P>,CPEGTerm<L,P>> node;

	public ConcreteSource(FutureNode<L,P> _node) {
		this.node = DisjointUnion.injectLeft(_node);
	}
	public ConcreteSource(CPEGTerm<L,P> _node) {
		this.node = DisjointUnion.injectRight(_node);
	}
	
	public boolean isConcrete() {return true;}
	public ConcreteSource<L,P> getConcreteSelf() {return this;}
	
	protected CPEGTerm<L,P> getTerm() {
		if (node.isLeft())
			return this.node.getLeft().getTerm();
		else
			return this.node.getRight();
	}
	
	public void buildProof(Proof proof) {
		if (node.isLeft())
			node.getLeft().buildProof(proof);
	}
	public FutureExpressionGraph.Vertex<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> 
		buildFutureVertex(FutureExpressionGraph<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> futureGraph) {
		if (node.isLeft())
			return node.getLeft().buildFutureVertex(futureGraph);
		else
			return futureGraph.getVertex(node.getRight());
	}
}
