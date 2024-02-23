package eqsat.revert;

import java.util.Collection;

import eqsat.OpExpression;
import util.integer.PairInt;

public interface FallCFG<P, L> {
	RevertBlock<L,P> addToCFG(RevertCFG<L,P,?> cfg, RevertBlock<L,P> target);
	void chain(Variable input,
			Collection<? super PairInt<OpExpression<L>>> uses);
}
