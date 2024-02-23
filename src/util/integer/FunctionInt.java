package util.integer;

import util.Function;

public interface FunctionInt<D> extends Function<D,Integer> {
	public int getInt(D domain);
}
