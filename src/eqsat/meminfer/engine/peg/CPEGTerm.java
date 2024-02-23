package eqsat.meminfer.engine.peg;

import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.Representative;

public final class CPEGTerm<O, P>
		extends PEGTerm<O,P,CPEGTerm<O,P>,CPEGValue<O,P>> {
	public CPEGTerm(CPEGValue<O,P> value, FlowValue<P,O> op,
			Representative<CPEGValue<O,P>>... children) {
		super(value, op, children);
	}
	public CPEGTerm(PEGTermConstructor<O,P,CPEGValue<O,P>> constructor) {
		super(constructor);
	}

	protected CPEGTerm<O,P> getSelf() {return this;}
}
