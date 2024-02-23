package eqsat.meminfer.network.op;

public class DirectLabelAmbassador<O> implements LabelAmbassador<O,O> {
	public boolean isConcrete(O label) {return label != null;}
	public O getConcrete(O label) {
		if (label == null)
			throw new IllegalArgumentException();
		else
			return label;
	}
}
