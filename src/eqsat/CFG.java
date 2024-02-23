package eqsat;

import java.util.Collection;

import util.Function;
import util.VariaticFunction;
import util.graph.Graph;

/** CFG is used to represent the control flow graph of a function.
 *
 * @param <G> The type of this CFG
 * @param <B> The type of the blocks of this CFG
 * @param <V> The type used to represent the variables of the CFG
 * @param <L> The type used to represent values and functions inside the CFG
 * @param <P> The type used to represent parameters of the CFG
 * @param <R> The type used to represent returns/outputs of the CFG
 */
public interface CFG<G, B, V, L, P, R> extends Graph<G,B> {
	/** A CFG uses variables to communicate between its blocks.
	 * There should be no null variables.
	 * @return the variables used in the CFG */
	Collection<? extends V> getVariables();
	/** Parameters are the input to the function.
	 * If the start block requests a variable as input,
	 * this method is used to determine which parameter that variable
	 * is associated with
	 * @param variable the variable whose input was requested by the start block
	 * @return the parameter associated with variable
	 * @throws IllegalArgumentException
	 * if no parameter is associated with variable
	 */
	P getParameter(V variable) throws IllegalArgumentException;
	/** A return maps to a variable whose value is significant
	 * once the CFG finishes executing.
	 * @return the collection of return variables
	 */
	Collection<? extends R> getReturns();
	/** Provides the variable corresponding to the return.
	 * @param ret the return in question
	 * @return the variable corresponding to ret
	 */
	V getReturnVariable(R ret);
	
	/** The start of a CFG is the one and only block which has no parents
	 * whose inputs are the parameters of the function.
	 * @return the start block of the CFG
	 */
	B getStart();
	/** The end of a CFG is the one and only block which has no children
	 * whose outputs of the return variables are the returned values of the CFG.
	 * @return the end block of the CFG
	 */
	B getEnd();
	
	/** Returns a translator for the CFG into the caller's representation E.
	 * 
	 * @param <E> The caller's representation of values
	 * @param parameterConverter the function to convert parameters
	 * @param converter the function to convert into the caller's representation
	 * @param known a collection to add expressions known to evaluate to true to
	 * @return A translator for the CFG using converter
	 */
	<E> CFGTranslator<B,V,E> getTranslator(Function<P,E> parameterConverter,
			VariaticFunction<L,E,E> converter, Collection<? super E> known);
	
	OpAmbassador<L> getOpAmbassador();
}
