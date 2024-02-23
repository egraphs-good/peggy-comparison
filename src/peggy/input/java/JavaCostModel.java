package peggy.input.java;

import peggy.analysis.PEGCostCalculator;
import peggy.input.EngineConfigurableCostModel;
import peggy.pb.CostModel;
import peggy.represent.java.JavaLabel;
import peggy.represent.java.JavaOperator;
import peggy.represent.java.JavaParameter;
import peggy.represent.java.JavaReturn;
import peggy.represent.java.MethodJavaLabel;
import peggy.represent.java.SimpleJavaLabel;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;

/**
 * This is the standard cost model for Java.
 */
public class JavaCostModel extends EngineConfigurableCostModel<JavaLabel,JavaParameter,Integer,MethodJavaLabel> {
	protected int defaultCost;

	public JavaCostModel() {this(10, 10, 5);}
	public JavaCostModel(int _varianceMultiplier, int _thetaCost, int _defaultCost) {
		super(_varianceMultiplier);
		this.setThetaCost(_thetaCost);
		this.defaultCost = _defaultCost;
	}
	protected Integer getZeroValue() {return 0;}
	protected Integer getDefaultCost() {return this.defaultCost;}
	public void setDefaultCost(int value) {
		this.defaultCost = value;
	}

	public Integer getBaseCost(CPEGTerm<JavaLabel,JavaParameter> node) {
		FlowValue<JavaParameter,JavaLabel> flow = node.getOp();
		if (flow.isDomain() && (flow.getDomain() instanceof SimpleJavaLabel)) {
			JavaOperator operator = ((SimpleJavaLabel)flow.getDomain()).getOperator();
			if (operator.equals(JavaOperator.INVOKESTATIC) ||
				operator.equals(JavaOperator.INVOKEVIRTUAL) ||
				operator.equals(JavaOperator.INVOKESPECIAL) ||
				operator.equals(JavaOperator.INVOKEINTERFACE)) {
				
				int methodindex = (operator.equals(JavaOperator.INVOKESTATIC) ? 1 : 2);
				for (CPEGTerm<JavaLabel,JavaParameter> methodrep : node.getChild(methodindex).getValue().getTerms()) { 
					if (methodrep.getOp().isDomain() && 
						methodrep.getOp().getDomain() instanceof MethodJavaLabel) {
						MethodJavaLabel method = (MethodJavaLabel)methodrep.getOp().getDomain();
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
	
	public PEGCostCalculator<JavaLabel,JavaParameter,JavaReturn> 
	getPEGCostCalculator() {
		final CostModel<Vertex<FlowValue<JavaParameter, JavaLabel>>, Integer> model = 
			new CostModel<Vertex<FlowValue<JavaParameter, JavaLabel>>, Integer>() {
				public Integer cost(
						Vertex<FlowValue<JavaParameter, JavaLabel>> node) {
					if (node.getChildCount() == 0)
						return getZeroValue();
					FlowValue<JavaParameter,JavaLabel> flow = node.getLabel();
					if (flow.isDomain() && (flow.getDomain() instanceof SimpleJavaLabel)) {
						JavaOperator operator = ((SimpleJavaLabel)flow.getDomain()).getOperator();
						if (operator.equals(JavaOperator.INVOKESTATIC) ||
							operator.equals(JavaOperator.INVOKEVIRTUAL) ||
							operator.equals(JavaOperator.INVOKESPECIAL) ||
							operator.equals(JavaOperator.INVOKEINTERFACE)) {
							
							int methodindex = (operator.equals(JavaOperator.INVOKESTATIC) ? 1 : 2);
							Vertex<FlowValue<JavaParameter,JavaLabel>> methodrep = node.getChild(methodindex); 
							if (methodrep.getLabel().isDomain() && 
								methodrep.getLabel().getDomain() instanceof MethodJavaLabel) {
								MethodJavaLabel method = (MethodJavaLabel)methodrep.getLabel().getDomain();
								if (methodCostMap.containsKey(method)) {
									return methodCostMap.get(method);
								}
							}
						}
					}
					return getLabelCost(node.getLabel());
				}
			};
		
		return new PEGCostCalculator<JavaLabel,JavaParameter,JavaReturn>() {
			public CostModel<Vertex<FlowValue<JavaParameter, JavaLabel>>, Integer> getCostModel() {
				return model;
			}
			public int getVarianceMultiplier() {
				return varianceMultiplier;
			}
		};
	}
}

