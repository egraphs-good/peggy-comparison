package peggy.analysis.java.inlining;

/**
 * This is a type of inlining heuristic based around an axiom engine.
 */
public interface EngineInlineHeuristic<M,N extends Number> {
	public boolean shouldInline(M function);
	public N getInlinedCallCost(M function);
}
