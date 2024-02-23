package peggy.pb;

/* Translation from boolean to PB:
 * 	 - All formulas should be in CNF
 * 	 - Each conjunct is a separate constraint
 * 	 - Constraint for (x1 \/ ~x2 \/ x3 \/ ~x4) is "x1 + (1-x2) + x3 + (1-x4) >= 1"
 */
public abstract class PseudoBooleanFormulation<N> {
	public abstract void setObjectiveFunction(ObjectiveFunction<N> function);
	public abstract void addConstraint(Constraint<N> cons);
	public abstract void addConstraint(Constraint<N> cons, String comment);
	/** Always returns a new variable (no caching).
	 *  Param can be null for non-node variables.
	 */
	public abstract Variable<N> getFreshVariable(N node); 

	/**
	 * Returns the variables that are being used in the formulation.
	 * Must include those that participate in a constraint or the
	 * objective function, but it may include more. 
	 */
	public abstract Iterable<Variable<N>> getVariables();
	
	/**
	 * Closes the formulation and allows it to flush any streams, etc.
	 * 
	 * After closing, the only method you can call on a formulation is
	 * getVariables(). All others will cause a runtime exception.
	 */
	public abstract void close();
	
	public abstract boolean isClosed();
}
