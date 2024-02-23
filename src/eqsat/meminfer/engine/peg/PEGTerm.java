package eqsat.meminfer.engine.peg;

import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.Representative;
import eqsat.meminfer.engine.basic.Value;
import eqsat.meminfer.engine.op.OpTerm;

public abstract class PEGTerm
		<O, P, T extends PEGTerm<O,P,T,V>, V extends Value<T,V>>
		extends OpTerm<FlowValue<P,O>,T,V> {
	public PEGTerm(V value, FlowValue<P,O> op, Representative<V>... children) {
		super(value, op, children);
	}
	public PEGTerm(PEGTermConstructor<O,P,V> constructor) {
		super(constructor.getSuper());
	}
}
