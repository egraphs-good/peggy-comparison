package peggy.pb;

/**
 * This is the most basic interface for a cost model.
 */
public interface CostModel<N,T extends Number> {
	public T cost(N node);
}
