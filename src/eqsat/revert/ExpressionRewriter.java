package eqsat.revert;

import java.util.List;

import util.Function;
import util.VariaticFunction;


public interface ExpressionRewriter<L,P> {
	<E> E rewriteExpression(VariaticFunction<L,E,E> converter,
			Function<P,E> parameterConverter,
			L label, List<? extends E> children);
	<E> E rewriteParameter(VariaticFunction<L,E,E> converter,
			Function<P,E> parameterConverter, P parameter);
}
