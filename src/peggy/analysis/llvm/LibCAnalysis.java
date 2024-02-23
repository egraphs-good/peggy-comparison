package peggy.analysis.llvm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import llvm.types.FloatingPointType;
import llvm.types.FunctionType;
import llvm.types.FloatingPointType.Kind;
import llvm.values.FloatingPointValue;
import peggy.analysis.Analysis;
import peggy.represent.llvm.ConstantValueLLVMLabel;
import peggy.represent.llvm.FunctionLLVMLabel;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMOperator;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.SimpleLLVMLabel;
import util.Function;
import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.event.ProofEvent;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyVertex;

/**
 * This analysis has axioms that refer to standard LIBC functions.
 */
public abstract class LibCAnalysis extends Analysis<LLVMLabel,LLVMParameter> {
	private static final Map<FunctionLLVMLabel,Function<Double,Double>> double2doubleMap = 
		new HashMap<FunctionLLVMLabel, Function<Double,Double>>();

	static {
		final FunctionType double2double = new FunctionType(
				new FloatingPointType(Kind.DOUBLE),
				Arrays.asList(new FloatingPointType(Kind.DOUBLE)),
				false);
		
		FunctionLLVMLabel log = new FunctionLLVMLabel(double2double, "log");
		double2doubleMap.put(log, new Function<Double,Double>() {
			public Double get(Double d) {return Math.log(d);}
		});
		
		FunctionLLVMLabel exp = new FunctionLLVMLabel(double2double, "exp");
		double2doubleMap.put(exp, new Function<Double,Double>() {
			public Double get(Double d) {return Math.exp(d);}
		});
		
		FunctionLLVMLabel sin = new FunctionLLVMLabel(double2double, "sin");
		double2doubleMap.put(sin, new Function<Double,Double>() {
			public Double get(Double d) {return Math.sin(d);}
		});

		FunctionLLVMLabel cos = new FunctionLLVMLabel(double2double, "cos");
		double2doubleMap.put(cos, new Function<Double,Double>() {
			public Double get(Double d) {return Math.cos(d);}
		});

		FunctionLLVMLabel tan = new FunctionLLVMLabel(double2double, "tan");
		double2doubleMap.put(tan, new Function<Double,Double>() {
			public Double get(Double d) {return Math.tan(d);}
		});

		FunctionLLVMLabel sqrt = new FunctionLLVMLabel(double2double, "sqrt");
		double2doubleMap.put(sqrt, new Function<Double,Double>() {
			public Double get(Double d) {return Math.sqrt(d);}
		});
	}
	
	public LibCAnalysis(
			Network _network,
			CPeggyAxiomEngine<LLVMLabel, LLVMParameter> _engine) {
		super(_network, _engine);
	}
	
	public void addAll() {
		for (FunctionLLVMLabel func : double2doubleMap.keySet()) {
			addDouble2DoubleFoldingAxioms(func);
		}
	}
	
	protected void addDouble2DoubleFoldingAxioms(final FunctionLLVMLabel func) {
		final String name = "Evaluate function on constant input (" + func + ")";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> rho_value =
			helper.get("rho_value", SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE),
					helper.get("call", SimpleLLVMLabel.get(LLVMOperator.CALL),
							helper.getVariable(),
							helper.get("func", func),
							helper.getVariable(),
							helper.get(SimpleLLVMLabel.get(LLVMOperator.PARAMS),
									helper.get("value", null))));
		helper.mustExist(rho_value);

		final ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> 
		triggerEvent = helper.getTrigger();
		final StructureFunctions functions = helper.getStructureFunctions();

		ShapeListener listener = new ShapeListener() {
			protected ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> getProofEvent() {return triggerEvent;}
			protected StructureFunctions getFunctions() {return functions;}
			protected String getName() {return name;}
			protected String build(
					Bundle bundle,
					FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> futureGraph) {
				final Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs) addConstantProperties(proof, bundle.getTerm("value"));
				final double value = bundle.getTerm("value").getOp().getDomain().getConstantValueSelf().getValue().getFloatingPointSelf().getDoubleBits();
				final double output = double2doubleMap.get(func).get(value);
				final Node result = node(
						new ConstantValueLLVMLabel(FloatingPointValue.fromDouble(output)));
				result.finish(bundle.getTerm("rho_value"), proof, futureGraph);
				return value + " -> " + output;
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel, LLVMParameter> value = bundle.getTerm("value");
				return value.getOp().isDomain() && 
					value.getOp().getDomain().isConstantValue() &&
					value.getOp().getDomain().getConstantValueSelf().getValue().isFloatingPoint();
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
}
