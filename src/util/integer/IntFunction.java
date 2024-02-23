package util.integer;

import util.Function;

public interface IntFunction<R> extends Function<Integer,R> {
	public R get(int domain);
}
