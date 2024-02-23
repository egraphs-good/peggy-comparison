package peggy.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.FutureExpression;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.proof.ArityIs;
import eqsat.meminfer.engine.proof.ChildIsEquivalentTo;
import eqsat.meminfer.engine.proof.EquivalentChildren;
import eqsat.meminfer.engine.proof.OpIs;
import eqsat.meminfer.engine.proof.Proof;

/**
 * This class contains info for how to construct a new axiom-created EPEG node.
 * It has the FlowValue label and ChildSources for each child.
 */
public abstract class FutureNode<L,P> {
	private final List<ChildSource<L,P>> sources;
	private final FlowValue<P,L> label;
	private FutureExpression<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> cached;
	
	public FutureNode(L _label, ChildSource<L,P>... _sources) {
		this.label = getDomain(_label);
		this.sources = new ArrayList<ChildSource<L,P>>(Arrays.asList(_sources));
	}
	public FutureNode(FlowValue<P,L> _label, ChildSource<L,P>... _sources) {
		this.label = _label;
		this.sources = new ArrayList<ChildSource<L,P>>(Arrays.asList(_sources));
	}
	
	protected abstract FlowValue<P,L> getDomain(L label);
	
	public CPEGTerm<L,P> getTerm() {
		if (this.cached == null || this.cached.getTerm() == null)
			throw new UnsupportedOperationException("Cannot build proof until vertex has been added");
		return this.cached.getTerm();
	}
	
	public void buildProof(Proof proof) {
		// add opis, arity, and child relations
		CPEGTerm<L,P> term = this.getTerm();
		proof.addProperty(new OpIs<FlowValue<P,L>,CPEGTerm<L,P>>(term, term.getOp()));
		proof.addProperty(new ArityIs<CPEGTerm<L,P>>(term, this.sources.size()));
		for (int i = 0; i < this.sources.size(); i++) {
			ChildSource<L,P> source = this.sources.get(i);
			if (source.isConcrete()) {
				ConcreteSource<L,P> concrete = source.getConcreteSelf();
				proof.addProperty(new ChildIsEquivalentTo<CPEGTerm<L,P>,CPEGValue<L,P>>(
						term, i, concrete.getTerm()));
			} else {
				StolenSource<L,P> stolen = source.getStolenSelf();
				proof.addProperty(new EquivalentChildren<CPEGTerm<L,P>,CPEGValue<L,P>>(
						term, i, stolen.getTerm(), stolen.getIndex()));
			}
			source.buildProof(proof);
		}
	}
	public FutureExpressionGraph.Vertex<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> 
		buildFutureVertex(FutureExpressionGraph<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> futureGraph) {
		if (cached == null) {
			FutureExpressionGraph.Vertex<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>>[] children = 
				new FutureExpressionGraph.Vertex[this.sources.size()];
			for (int i = 0; i < this.sources.size(); i++) {
				children[i] = this.sources.get(i).buildFutureVertex(futureGraph);
			}
			cached = futureGraph.getExpression(this.label, children);
		}
		return cached;
	}
}


