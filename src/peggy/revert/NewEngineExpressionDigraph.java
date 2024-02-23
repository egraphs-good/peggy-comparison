package peggy.revert;

import peggy.pb.EngineExpressionDigraph;
import peggy.represent.StickyPredicate;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;

/**
 * This is an EngineExpressionDigraph that has default implementations 
 * for many methods.
 */
public abstract class NewEngineExpressionDigraph<O,P> extends EngineExpressionDigraph<CPEGValue<O,P>, CPEGTerm<O,P>> {
	private final StickyPredicate<CPEGTerm<O,P>> stickyPredicate;
	public NewEngineExpressionDigraph(StickyPredicate<CPEGTerm<O,P>> _sticky) {
		this.stickyPredicate = _sticky;
	}
	public Iterable<? extends CPEGTerm<O,P>> getUnfilteredValueElements(CPEGValue<O,P> group) {
		return group.getTerms();
	}
	public CPEGValue<O,P> getElementValue(CPEGTerm<O,P> rep) {
		return rep.getValue();
	}
	public boolean isThetaNode(CPEGTerm<O,P> rep) {
		return rep.getOp().isTheta();
	}
	public boolean isEvalNode(CPEGTerm<O,P> rep) {
		return rep.getOp().isEval();
	}
	public boolean isPassNode(CPEGTerm<O,P> rep) {
		return rep.getOp().isPass();
	}
	public int getLoopDepth(CPEGTerm<O,P> rep) {
		return rep.getOp().getLoopDepth();
	}
	public int getMaxVariance(CPEGValue<O,P> group) {
		return group.getMaxVariance();
	}
	public boolean isRevertible(CPEGTerm<O,P> rep) {
		return rep.getOp().isRevertable();
	}
	public int getArity(CPEGTerm<O,P> rep) {
		return rep.getArity();
	}
	public CPEGValue<O,P> getChildValue(CPEGTerm<O,P> rep, int index) {
		return rep.getChild(index).getValue();
	}
	public StickyPredicate<CPEGTerm<O,P>> getStickyPredicate() {
		return this.stickyPredicate;
	}
}