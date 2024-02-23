package util.pair;

import java.util.LinkedList;

public class LinkedPairedList<F, S> extends DoublePairedList<F, S> {
	public LinkedPairedList() {
		super(new LinkedList<F>(), new LinkedList<S>());
	}
	
	public LinkedPairedList(PairedList<? extends F, ? extends S> that) {
		this();
		addAll(that);
	}
}