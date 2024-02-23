package util.integer;

public abstract class AbstractIntFunction<R> implements IntFunction<R> {
	public R get(Integer domain) {
		if (domain == null)
			throw new IllegalArgumentException();
		return get(domain.intValue());
	}
}
