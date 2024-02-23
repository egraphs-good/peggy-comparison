package util.pair;

import java.util.ArrayList;

public class ArrayPairedList<F, S> extends DoublePairedList<F, S> {
	public ArrayPairedList() {super(new ArrayList<F>(), new ArrayList<S>());}
	public ArrayPairedList(int start) {
		super(new ArrayList<F>(start), new ArrayList<S>(start));
	}
	public ArrayPairedList(PairedList<? extends F, ? extends S> that) {
		this(that.size());
		addAll(that);
	}
}