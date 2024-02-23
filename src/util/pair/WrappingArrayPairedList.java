package util.pair;

import util.WrappingArrayList;

public class WrappingArrayPairedList<F, S> extends DoublePairedList<F,S> {
	public WrappingArrayPairedList() {
		super(new WrappingArrayList<F>(), new WrappingArrayList<S>());
	}
	
	public WrappingArrayPairedList(PairedList<? extends F, ? extends S> that) {
		this();
		addAll(that);
	}
}
