package peggy.analysis;

import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.proof.Proof;

/**
 * This is a child source that is "stolen" from another node.
 * It names a node and a child index 'i', and refers the i-th child of that node.
 */
public class StolenSource<L,P> extends ChildSource<L,P> {
	private final CPEGTerm<L,P> term;
	private final int index;
	
	public StolenSource(CPEGTerm<L,P> _term, int _index) {
		this.term = _term;
		this.index = _index;
	}
	public boolean isStolen() {return true;}
	public StolenSource<L,P> getStolenSelf() {return this;}
	
	public CPEGTerm<L,P> getTerm() {return this.term;}
	public int getIndex() {return this.index;}
	
	public void buildProof(Proof proof) {} // parent takes care of properties
	public FutureExpressionGraph.Vertex<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> 
		buildFutureVertex(FutureExpressionGraph<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> futureGraph) {
		return futureGraph.getVertex(this.term.getChild(this.index));
	}
}
