package peggy.analysis.llvm;

import java.util.ArrayList;
import java.util.List;

import llvm.types.FloatingPointType;
import llvm.types.FunctionType;
import llvm.types.Type;
import llvm.values.FloatingPointValue;
import llvm.values.Value;
import peggy.analysis.Analysis;
import peggy.represent.llvm.ConstantValueLLVMLabel;
import peggy.represent.llvm.FunctionLLVMLabel;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMOperator;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.SimpleLLVMLabel;
import peggy.represent.llvm.StringAnnotationLLVMLabel;
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
 * This analysis performs the body of certain LLVM intrinsic functions
 * (given constant params).
 */
public abstract class LLVMIntrinsicAnalysis extends Analysis<LLVMLabel,LLVMParameter> {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("LLVMIntrinsicAnalysis: " + message);
	}
	
	private static final FunctionLLVMLabel SIN, COS, TAN, SQRT;	
	
	static {
		final Type doubleType = FloatingPointType.getFloatingPointType(FloatingPointType.Kind.DOUBLE);
		final List<Type> ds = new ArrayList<Type>();
		ds.add(doubleType);
		final FunctionType funcType = new FunctionType(doubleType, ds, false);
		SIN = new FunctionLLVMLabel(funcType, "sin");
		COS = new FunctionLLVMLabel(funcType, "cos");
		TAN = new FunctionLLVMLabel(funcType, "tan");
		SQRT = new FunctionLLVMLabel(funcType, "sqrt");
	}
	
	private boolean added = false;
	public LLVMIntrinsicAnalysis(
			Network network,
			CPeggyAxiomEngine<LLVMLabel,LLVMParameter> engine) {
		super(network, engine);
	}
	
	public void addAll() {
		if (!added) {
			addDouble2DoubleAxioms();
			added = true;
		}
	}
	
	/**
	 * double sin/cos/tan/sqrt(double)
	 */
	private void addDouble2DoubleAxioms() {
		final String name = "Inline intrinsic function";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		PeggyVertex<LLVMLabel,Integer> call =
			helper.get("call",
					SimpleLLVMLabel.get(LLVMOperator.CALL),
					helper.getVariable("sigma"),
					helper.get("function", null),
					helper.getVariable("cc"),
					helper.get("params", 
							SimpleLLVMLabel.get(LLVMOperator.PARAMS),
							helper.getVariable("arg")));
		helper.mustExist(call);

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
				final CPEGValue<LLVMLabel,LLVMParameter> argValue = bundle.getRep("arg").getValue();
				final Value constant = getConstantDouble(argValue);
				final String funcName = bundle.getTerm("function").getOp().getDomain().getFunctionSelf().getFunctionName();
				
				debug("constant = " + constant);
				
				double resultValue;
				if (funcName.equals("sin")) {
					resultValue = Math.sin(constant.getFloatingPointSelf().getDoubleBits());
				} else if (funcName.equals("cos")) {
					resultValue = Math.cos(constant.getFloatingPointSelf().getDoubleBits());
				} else if (funcName.equals("tan")) {
					resultValue = Math.tan(constant.getFloatingPointSelf().getDoubleBits());
				} else if (funcName.equals("sqrt")) {
					resultValue = Math.sqrt(constant.getFloatingPointSelf().getDoubleBits());
				} else 
					throw new RuntimeException("Unexpected function name: " + funcName);
				
				debug("resultValue = " + resultValue);
				
				final Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs) addConstantProperties(proof, bundle.getTerm("function"));
				
				final LLVMLabel resultLabel = 
					new ConstantValueLLVMLabel(FloatingPointValue.fromDouble(resultValue));
				
				debug("resultLabel = " + resultLabel);
				
				final Node result = node(
						new StringAnnotationLLVMLabel("inlineTuple"),
						conc(node(
								SimpleLLVMLabel.get(LLVMOperator.INJR), 
								conc(node(resultLabel)))),
						steal(bundle.getTerm("call"), 0));
				result.finish(bundle.getTerm("call"), proof, futureGraph);
				
				return funcName;
			}
			private Value getConstantDouble(CPEGValue<LLVMLabel,LLVMParameter> value) {
				for (CPEGTerm<LLVMLabel,LLVMParameter> term : value.getTerms()) {
					if (term.getOp().isDomain() &&
						term.getOp().getDomain().isConstantValue() &&
						term.getOp().getDomain().getConstantValueSelf().getValue().isFloatingPoint())
						return term.getOp().getDomain().getConstantValueSelf().getValue();
				}
				return null;
			}
			protected boolean matches(Bundle bundle) {
				final CPEGTerm<LLVMLabel,LLVMParameter> functionTerm = bundle.getTerm("function");
				// "function" must be FunctionLLVMLabel
				if (functionTerm.getOp().isDomain() &&
					functionTerm.getOp().getDomain().isFunction()) {
					final FunctionLLVMLabel funcLabel = functionTerm.getOp().getDomain().getFunctionSelf();
					// "function" must be recognized function
					if (funcLabel.equals(SIN) ||
						funcLabel.equals(TAN) ||
						funcLabel.equals(COS) ||
						funcLabel.equals(SQRT)) {
						// "arg" must be constant double value
						final CPEGValue<LLVMLabel,LLVMParameter> child = 
							bundle.getRep("arg").getValue();
						final Value constant = getConstantDouble(child);
						if (constant != null)
							return true;
					}
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}	
}

