package peggy.input.llvm;

import peggy.analysis.PEGCostCalculator;
import peggy.input.EngineConfigurableCostModel;
import peggy.pb.CostModel;
import peggy.represent.llvm.FunctionLLVMLabel;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMOperator;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.LLVMReturn;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;

/**
 * This is the standard cost model for LLVM.
 */
public class LLVMCostModel extends EngineConfigurableCostModel<LLVMLabel,LLVMParameter,Integer,FunctionLLVMLabel> {
	protected int defaultCost;

	public LLVMCostModel() {this(10, 10, 5);}
	public LLVMCostModel(int _varianceMultiplier, int _thetaCost, int _defaultCost) {
		super(_varianceMultiplier);
		this.setThetaCost(_thetaCost);
		this.defaultCost = _defaultCost;
	}
	protected Integer getZeroValue() {return 0;}
	protected Integer getDefaultCost() {return this.defaultCost;}
	public void setDefaultCost(int value) {
		this.defaultCost = value;
	}

	public Integer getBaseCost(CPEGTerm<LLVMLabel,LLVMParameter> node) { 
		FlowValue<LLVMParameter,LLVMLabel> flow = node.getOp();
		if (flow.isDomain() && flow.getDomain().isSimple()) {
			LLVMOperator operator = flow.getDomain().getSimpleSelf().getOperator();
			if (operator.equals(LLVMOperator.CALL) ||
				operator.equals(LLVMOperator.TAILCALL) ||
				operator.equals(LLVMOperator.INVOKE)) {
				
				for (CPEGTerm<LLVMLabel,LLVMParameter> methodrep : node.getChild(1).getValue().getTerms()) { 
					if (methodrep.getOp().isDomain() && 
						methodrep.getOp().getDomain().isFunction()) {
						FunctionLLVMLabel method = methodrep.getOp().getDomain().getFunctionSelf();
						
						if (this.methodCostMap.containsKey(method)) {
							return this.methodCostMap.get(method);
						}
					}
				}
			}
		}
	
		return super.getBaseCost(node);
	}
	
	protected Integer computeVarianceFunction(Integer baseCost, int maxVariance, Integer varMult) {
		return baseCost * (int)Math.pow(varMult, maxVariance);
	}
	
	public PEGCostCalculator<LLVMLabel,LLVMParameter,LLVMReturn> 
	getPEGCostCalculator() {
		final CostModel<Vertex<FlowValue<LLVMParameter, LLVMLabel>>, Integer> model = 
			new CostModel<Vertex<FlowValue<LLVMParameter, LLVMLabel>>, Integer> () {
				public Integer cost(
						Vertex<FlowValue<LLVMParameter, LLVMLabel>> node) {
					if (node.getChildCount() == 0)
						return getZeroValue();
					
					FlowValue<LLVMParameter,LLVMLabel> flow = node.getLabel();
					if (flow.isDomain() && flow.getDomain().isSimple()) {
						LLVMOperator operator = flow.getDomain().getSimpleSelf().getOperator();
						if (operator.equals(LLVMOperator.CALL) ||
							operator.equals(LLVMOperator.TAILCALL) ||
							operator.equals(LLVMOperator.INVOKE)) {
							
							Vertex<FlowValue<LLVMParameter, LLVMLabel>> methodrep = node.getChild(1); 
							if (methodrep.getLabel().isDomain() && 
								methodrep.getLabel().getDomain().isFunction()) {
								FunctionLLVMLabel method = methodrep.getLabel().getDomain().getFunctionSelf();
								if (methodCostMap.containsKey(method)) {
									return methodCostMap.get(method);
								}
							}
						}
					}
				
					return getLabelCost(node.getLabel());
				}
			};
		
		return new PEGCostCalculator<LLVMLabel,LLVMParameter,LLVMReturn>() {
			public CostModel<Vertex<FlowValue<LLVMParameter, LLVMLabel>>, Integer> getCostModel() {
				return model;
			}
			public int getVarianceMultiplier() {
				return varianceMultiplier;
			}
		};
	}
}
