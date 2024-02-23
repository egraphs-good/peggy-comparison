package peggy.pb;

import java.util.HashMap;
import java.util.Map;

/**
 * This is an implementation of CostModel that allows you to store a hash of 
 * labels to costs. The labels do not have to be the same type as the nodes that
 * will eventually be passed to cost(). In general, the type that is hashed is some 
 * component of the type that will ultimately be attributed costs. This works well
 * in the case of FlowValues and Representations (see EngineConfigurableCostModel).
 * Note that this class does not force any kind of relationship between the H and C
 * type parameters. That relationship can be equality or any other one that the user wishes.
 * It will be solidified by subclasses.
 * 
 * @author steppm
 *
 * @param <H> The type for which we will store costs in the hash
 * @param <M> The type for the method representation
 * @param <C> The type of input for the cost() method of this CostModel
 * @param <N> The numeric type that represents cost values
 */
public abstract class ConfigurableCostModel<H,M,C,N extends Number> implements CostModel<C,N> {
	protected final Map<H,N> operatorCostMap;
	protected final Map<M,N> methodCostMap;

	public ConfigurableCostModel() {
		this.operatorCostMap = new HashMap<H,N>();
		this.methodCostMap = new HashMap<M,N>();
	}
	
	public void setConfiguredCost(H node, N cost) {
		if (node == null)
			throw new NullPointerException("node is null");
		if (cost == null)
			throw new NullPointerException("cost is null");
		this.operatorCostMap.put(node, cost);
	}
	public void setMethodInvokeCost(M method, N cost) {
		if (method == null)
			throw new NullPointerException("method is null");
		if (cost == null)
			throw new NullPointerException("cost is null");
		this.methodCostMap.put(method, cost);
	}
	
	protected N getConfiguredCost(H node) {
		if (this.operatorCostMap.containsKey(node))
			return this.operatorCostMap.get(node);
		else
			return this.getDefaultCost();
	}

	protected boolean hasMethodEntry(M method) {
		return this.methodCostMap.containsKey(method);
	}
	protected N getMethodInvokeCost(M method) {
		if (this.methodCostMap.containsKey(method))
			return this.methodCostMap.get(method);
		else
			return this.getDefaultCost();
	}
	
	protected abstract N getDefaultCost();
}
