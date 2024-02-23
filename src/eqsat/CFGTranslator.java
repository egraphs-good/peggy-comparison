package eqsat;

import util.Function;

/** CFGTranslator as an intermediary that allows the CFG to take safe notes.
 *
 * @param <B> The type of the CFG's blocks.
 * @param <V> The type of the CFG's variables.
 * @param <E> The type of the caller's value representation.
 */
public interface CFGTranslator<B, V, E> {
	/** Produces a function from variables to their output value
	 * using the caller's representation.
	 * For that function, if the variable is null,
	 * then it returns the branch condition of the block,
	 * throwing an UnsupportedOperationException if the block does not branch. 
	 * 
	 * @param inputs A function from variables to the caller's representation
	 * of the value of that variable being input to this block.
	 */
	Function<V,E> getOutputs(B block, Function<V,E> inputs);
}
