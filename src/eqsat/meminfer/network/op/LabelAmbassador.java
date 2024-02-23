package eqsat.meminfer.network.op;

public interface LabelAmbassador<L, O> {
	boolean isConcrete(L label);
	O getConcrete(L label);
}
