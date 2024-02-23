package eqsat.meminfer.engine.peg;

import eqsat.FlowValue;
import eqsat.meminfer.engine.op.OpTermConstructor;

public final class PEGTermConstructor<O, P, V> {
	private final OpTermConstructor<FlowValue<P,O>,V> mSuper;
	
	public PEGTermConstructor(OpTermConstructor<FlowValue<P,O>,V> sup) {
		mSuper = sup;
	}
	
	public OpTermConstructor<FlowValue<P,O>,V> getSuper() {return mSuper;}
}
