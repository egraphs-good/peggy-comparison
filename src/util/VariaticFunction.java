package util;

import java.util.List;

/** VariaticFunction is used to represent mathematical functions,
 * meaning given the "same" input it produces the "same" value.
 * Unfortunately, the meaning of "same" can vary.
 * Typically it should mean that if two inputs would be equal via
 * {@link Object.equals(Object)} then their output would be equal via
 * {@link Object.equals(Object)} as well.
 *
 * @param <F> The type of the First element of the Domain of the Function
 * @param <V> The type of the remaining elements of the Domain of the Function
 * @param <R> The type of the Range of the Function
 */
public interface VariaticFunction<F, V, R> {
	/** Evaluates this function on a given list of parameters.
	 * (Object... is used rather than V... to avoid issues with array casting.)
	 * 
	 * @param first The first parameter to this function
	 * @param remaining The remaining parameters to this function
	 * @return The value of this function for first with remaining
	 */
	public R get(F first, Object... remaining);
	/** Evaluates this function on a given list of parameters.
	 * 
	 * @param first The first parameter to this function
	 * @param remaining The remaining parameters to this function
	 * @return The value of this function for first with remaining
	 */
	public R get(F first, List<? extends V> remaining);
	/** Evaluates this function on a given parameter.
	 * 
	 * @param first The parameter to this function
	 * @return The value of this function for first
	 */
	public R get(F first);
	/** Evaluates this function on given parameters.
	 * 
	 * @param first The first parameter to this function
	 * @param second The second parameter to this function
	 * @return The value of this function for first with second
	 */
	public R get(F first, V second);
	/** Informs whether the variatic arguments are necessary.
	 * If they are not, then a properly sized List/Array is passed but
	 * possibly containing null values.
	 * 
	 * @param first The first parameter to this function
	 * @return False if the variatic arguments are not needed
	 */
	//public boolean usesAllArguments(F first);
	/** Informs whether the variatic arguments are necessary.
	 * If they are not, then either a null value is passed
	 * or a properly sized List/Array is passed but
	 * possibly containing null values.
	 * 
	 * @param first The first parameter to this function
	 * @return False if the variatic arguments are not needed
	 */
	//public boolean usesAnyArguments(F first);
	/** Informs whether an index of the variatic arguments is necessary.
	 * If it is not, then the List/Array passed may have a null value
	 * for that index.
	 * 
	 * @param first The first parameter to this function
	 * @param index The index of the variatic argument in question
	 * @return False if the appropriate variatic argument is not needed
	 */
	//public boolean usesArgument(F first, int index);
}
