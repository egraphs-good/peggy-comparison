package eqsat.meminfer.engine.op;

import eqsat.meminfer.engine.basic.TermConstructor;

public final class OpTermConstructor<O, V> {
	private final TermConstructor<V> mSuper;
	private final O mOp;
	
	public OpTermConstructor(TermConstructor<V> sup, O op) {
		mSuper = sup;
		mOp = op;
	}
	
	public TermConstructor<V> getSuper() {return mSuper;}
	public O getOp() {return mOp;}
}
