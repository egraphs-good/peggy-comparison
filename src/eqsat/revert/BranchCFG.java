package eqsat.revert;

import java.util.Collection;

import eqsat.OpExpression;
import util.integer.PairInt;

public interface BranchCFG<P, L> {
	RevertBlock<L,P> addToCFG(RevertCFG<L,P,?> cfg,
			RevertBlock<L,P> trueTarget, RevertBlock<L,P> falseTarget);
	void chain(Variable input,
			Collection<? super PairInt<OpExpression<L>>> uses);
}
