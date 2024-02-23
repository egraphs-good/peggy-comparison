package peggy.analysis;

import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.proof.ArityIs;
import eqsat.meminfer.engine.proof.ChildIsEquivalentTo;
import eqsat.meminfer.engine.proof.EquivalentChildren;
import eqsat.meminfer.engine.proof.OpIs;
import eqsat.meminfer.engine.proof.OpIsLoopOp;
import eqsat.meminfer.engine.proof.Property;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.network.peg.PEGNetwork.PEGLoopOp;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This is an abstract parent class of all inlining analyses.
 * It has useful methods that help when inlining methods/functions.
 */
public abstract class InlineAnalysis<L,P> extends Analysis<L,P> {
	public InlineAnalysis(Network _network, CPeggyAxiomEngine<L,P> _engine) {
		super(_network, _engine);
	}
	
	protected FlowValue<P,L> updateLoopIndex(
			FlowValue<P,L> flow, int increment) {
		if (flow.isTheta())
			return FlowValue.createTheta(flow.getLoopDepth() + increment);
		else if (flow.isEval())
			return FlowValue.createEval(flow.getLoopDepth() + increment);
		else if (flow.isShift())
			return FlowValue.createShift(flow.getLoopDepth() + increment);
		else if (flow.isPass())
			return FlowValue.createPass(flow.getLoopDepth() + increment);
		else
			throw new IllegalArgumentException("Not a loop operator: " + flow);
	}
	
	protected abstract class PropertyBuilder {
		public abstract Property build();
		protected CPEGTerm<L,P> getTerm(
				FutureExpressionGraph.Vertex<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> vertex) {
			if (vertex.isFutureAmbassador()) {
				return vertex.getFutureAmbassador().getIntendedExpression().getTerm();
			} else if (vertex.isFutureExpression()) {
				return vertex.getFutureExpression().getTerm();
			} else {
				throw new IllegalArgumentException("Bad vertex type: " + vertex.getClass());
			}
		}
	}
	protected class ArityIsPropertyBuilder extends PropertyBuilder {
		FutureExpressionGraph.Vertex<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> expr;
		public ArityIsPropertyBuilder(
				FutureExpressionGraph.Vertex<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> _expr) {
			this.expr = _expr;
		}
		public Property build() {
			CPEGTerm<L,P> term = getTerm(expr); 
			return new ArityIs<CPEGTerm<L,P>>(term, term.getArity());
		}
	}
	protected class OpIsPropertyBuilder extends PropertyBuilder {
		FutureExpressionGraph.Vertex<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> expr;
		public OpIsPropertyBuilder(
				FutureExpressionGraph.Vertex<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> _expr) {
			this.expr = _expr;
		}
		public Property build() {
			CPEGTerm<L,P> term = getTerm(expr);
			return new OpIs<FlowValue<P,L>,CPEGTerm<L,P>>(term, term.getOp());
		}
	}
	protected class OpIsLoopOpPropertyBuilder extends PropertyBuilder {
		FutureExpressionGraph.Vertex<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> expr;
		public OpIsLoopOpPropertyBuilder(
				FutureExpressionGraph.Vertex<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> _expr) {
			this.expr = _expr;
		}
		public Property build() {
			CPEGTerm<L,P> term = getTerm(expr);
			PEGLoopOp loopOp = null;
			if (term.getOp().isTheta()) {
				loopOp = PEGLoopOp.Theta;
			} else if (term.getOp().isEval()) {
				loopOp = PEGLoopOp.Eval;
			} else if (term.getOp().isPass()) {
				loopOp = PEGLoopOp.Pass;
			} else if (term.getOp().isShift()) {
				loopOp = PEGLoopOp.Shift;
			} else {
				throw new RuntimeException("Invalid loop op: " + term.getOp());
			}
			return new OpIsLoopOp<CPEGTerm<L,P>>(term, loopOp); 
		}
	}
	protected class ChildIsEquivalentPropertyBuilder extends PropertyBuilder {
		FutureExpressionGraph.Vertex<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> parent, child;
		int index;
		public ChildIsEquivalentPropertyBuilder(
				FutureExpressionGraph.Vertex<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> _parent,
				int _index,
				FutureExpressionGraph.Vertex<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> _child) {
			this.parent = _parent;
			this.child = _child;
			this.index = _index;
		}
		public Property build() {
			CPEGTerm<L,P> parentterm = getTerm(parent);
			CPEGTerm<L,P> childterm = getTerm(child);
			return new ChildIsEquivalentTo<CPEGTerm<L,P>,CPEGValue<L,P>>(parentterm, index, childterm);
		}
	}
	protected class EquivalentChildrenPropertyBuilder extends PropertyBuilder {
		FutureExpressionGraph.Vertex<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> node1;
		int index1, index2;
		CPEGTerm<L,P> node2;
		
		public EquivalentChildrenPropertyBuilder(
				FutureExpressionGraph.Vertex<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> _node1,
				int _index1,
				CPEGTerm<L,P> _node2,
				int _index2) {
			this.node1 = _node1;
			this.node2 = _node2;
			this.index1 = _index1;
			this.index2 = _index2;
		}
		public Property build() {
			return new EquivalentChildren<CPEGTerm<L,P>,CPEGValue<L,P>>(
					getTerm(node1), index1, node2, index2);
		}
	}
}
