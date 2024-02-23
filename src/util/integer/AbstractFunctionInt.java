package util.integer;

public abstract class AbstractFunctionInt<D> implements FunctionInt<D> {
	public Integer get(D domain) {return getInt(domain);}
}
