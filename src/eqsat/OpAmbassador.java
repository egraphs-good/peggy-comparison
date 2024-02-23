package eqsat;

import util.VariaticFunction;

public interface OpAmbassador<L> extends VariaticFunction<L,L,L> {
	L getBasicOp(BasicOp op);
	boolean canPreEvaluate(L op);
	boolean isFree(L op);
	boolean needsAnyChild(L op);
	boolean needsChild(L op, int child);
	/** The inverse function of getBasicOp.
	 * Returns null if the there is no inverse.
	 * 
	 * @param op the operator in question
	 * @return the BasicOp equivalent to op or null if there is no such BasicOp
	 */
	BasicOp getBasicOp(L op);
	
	boolean isAnyVolatile(L op);
	boolean isVolatile(L op, int child);
	L getChainVersion(L op, int child);
	L getChainProjectValue(L op, int child);
	L getChainProjectVolatile(L op, int child);
	/** Returns whether chain(first) operating on firstChild
	 * then second operating on the result is the same as
	 * had second operated on secondChild.
	 */
	boolean isEquivalent(OpExpression<L> first, int firstChild,
			OpExpression<L> second, int secondChild);
}
