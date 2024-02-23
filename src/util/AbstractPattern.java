package util;

public abstract class AbstractPattern<P> implements Pattern<P> {
	public Boolean get(P object) {return matches(object);}
}
