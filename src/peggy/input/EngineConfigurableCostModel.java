package peggy.input;

import peggy.pb.ConfigurableCostModel;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;

/**
 * This class provides a cost model that is specifically designed for
 * use within an equality saturation engine.
 * It stores explicit costs for all the standard non-domain operators.
 * It also allows assigning specific costs to particular functions invocations.
 */
public abstract class EngineConfigurableCostModel<O,P,N extends Number,M> extends ConfigurableCostModel<FlowValue<P,O>,M,CPEGTerm<O,P>,N> {
	protected N varianceMultiplier;
	protected N thetaCost;
	protected N passCost;
	protected N evalCost;
	protected N shiftCost;
	protected N phiCost;
	protected N andCost;
	protected N orCost;
	protected N negateCost;
	protected N equalsCost;
	
	/**
	 * The parameter is the multiplier to be used in the computeVarianceEquation
	 * method. For a node with base cost B, and max variance V, the total cost of that node should be
	 * 
	 * 		B * pow(varianceMultiplier,V)
	 *
	 * Upon construction, all the non-domain operators are given the default cost of this
	 * cost model.
	 * 
	 * @param _varianceMultiplier the variance multiplier
	 */
	public EngineConfigurableCostModel(N _varianceMultiplier) {
		if (_varianceMultiplier == null)
			throw new NullPointerException("variance multiplier is null");
		
		N defaultCost = this.getDefaultCost();
		this.varianceMultiplier = _varianceMultiplier;
		this.thetaCost = defaultCost;
		this.passCost = defaultCost;
		this.evalCost = defaultCost;
		this.shiftCost = defaultCost;
		this.phiCost = defaultCost;
		this.andCost = defaultCost;
		this.orCost = defaultCost;
		this.negateCost = defaultCost;
		this.equalsCost = defaultCost;
	}
	
	public N getVarianceMultiplier() {
		return varianceMultiplier;
	}
	public void setVarianceMultiplier(N cost) {
		if (cost == null)
			throw new NullPointerException("cost is null");
		this.varianceMultiplier = cost;
	}
	public void setThetaCost(N cost) {
		if (cost == null)
			throw new NullPointerException("cost is null");
		this.thetaCost = cost;
	}
	public void setPassCost(N cost) {
		if (cost == null)
			throw new NullPointerException("cost is null");
		this.passCost = cost;
	}
	public void setEvalCost(N cost) {
		if (cost == null)
			throw new NullPointerException("cost is null");
		this.evalCost = cost;
	}
	public void setShiftCost(N cost) {
		if (cost == null)
			throw new NullPointerException("cost is null");
		this.shiftCost = cost;
	}
	public void setPhiCost(N cost) {
		if (cost == null)
			throw new NullPointerException("cost is null");
		this.phiCost = cost;
	}
	public void setAndCost(N cost) {
		if (cost == null)
			throw new NullPointerException("cost is null");
		this.andCost = cost;
	}
	public void setOrCost(N cost) {
		if (cost == null)
			throw new NullPointerException("cost is null");
		this.orCost = cost;
	}
	public void setNegateCost(N cost) {
		if (cost == null)
			throw new NullPointerException("cost is null");
		this.negateCost = cost;
	}
	public void setEqualsCost(N cost) {
		if (cost == null)
			throw new NullPointerException("cost is null");
		this.equalsCost = cost;
	}

	protected N getLabelCost(FlowValue<P,O> flow) {
		if (flow.isDomain()) {
			return this.getConfiguredCost(flow);
		} else if (flow.isParameter()) {
			// should be redundant
			return this.getZeroValue();
		} else if (flow.isRevertable()) {
			if (flow.isTheta()) return this.thetaCost;
			else if (flow.isPass()) return this.passCost;
			else if (flow.isEval()) return this.evalCost;
			else if (flow.isShift()) return this.shiftCost;
			else if (flow.isPhi()) return this.phiCost;
			else if (flow.isAnd()) return this.andCost;
			else if (flow.isOr()) return this.orCost;
			else if (flow.isTrue()) return this.getZeroValue();
			else if (flow.isFalse()) return this.getZeroValue();
			else if (flow.isNegate()) return this.negateCost;
			else if (flow.isEquals()) return this.equalsCost;
         else if (flow.isShortCircuitAnd()) return this.andCost;
         else if (flow.isShortCircuitOr()) return this.orCost;
			else
				throw new RuntimeException("Mike forgot to support " + flow);
		} else {
			return this.getZeroValue();
		}
	}
	
	public N getBaseCost(CPEGTerm<O,P> node) {
		if (node.getArity() == 0)
			return this.getZeroValue();
		else
			return this.getLabelCost(node.getOp());
	}
	
	public N cost(CPEGTerm<O,P> node) {
		N baseCost = this.getBaseCost(node);
		int maxVariance = node.getValue().getMaxVariance();
		return computeVarianceFunction(baseCost, maxVariance, this.varianceMultiplier);
	}
	
	protected abstract N computeVarianceFunction(N baseCost, int maxVariance, N varianceMultiplier);
	protected abstract N getZeroValue();
}

