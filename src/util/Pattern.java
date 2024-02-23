/**
 * 
 */
package util;

public interface Pattern<P> extends Function<P,Boolean> {
	public boolean matches(P object);
}