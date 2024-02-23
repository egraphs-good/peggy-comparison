package util;

/** Function is used to represent mathematical functions,
 * meaning given the "same" input it produces the "same" value.
 * Unfortunately, the meaning of "same" can vary.
 * Typically it should mean that if two inputs would be equal via
 * {@link Object.equals(Object)} then their output would be equal via
 * {@link Object.equals(Object)} as well.
 *
 * @param <D> The type of the Domain of the Function
 * @param <R> The type of the Range of the Function
 */
public interface Function<D, R> {
	/** Evaluates this function on a given parameter.
	 * 
	 * @param parameter The parameter to this function
	 * @return The value of this function for parameter
	 */
	public R get(D parameter);
	
	/** Two functions are equal if given the "same" input they always produce
	 * the "same" output.
	 * 
	 * @param that The object being compared with
	 * @return True if that object is a function known to be equivalent to this
	 */
	public boolean equals(Object that);
}
