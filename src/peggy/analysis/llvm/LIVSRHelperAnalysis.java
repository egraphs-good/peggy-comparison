package peggy.analysis.llvm;

import llvm.instructions.Binop;
import llvm.instructions.IntegerComparisonPredicate;
import peggy.analysis.Analysis;
import peggy.represent.llvm.BinopLLVMLabel;
import peggy.represent.llvm.CmpLLVMLabel;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMParameter;
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
 * This analysis has axioms that are useful when performing the 
 * loop-induction-variable strength reduction (LIVSR) optimization.
 */
public abstract class LIVSRHelperAnalysis extends Analysis<LLVMLabel,LLVMParameter> {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("LIVSRHelperAnalysis: " + message);
	}

	private static final LLVMLabel MUL = new BinopLLVMLabel(Binop.Mul);
	private static final LLVMLabel SGE = new CmpLLVMLabel(IntegerComparisonPredicate.ICMP_SGE);
	
	public LIVSRHelperAnalysis(
			Network _network, 
			CPeggyAxiomEngine<LLVMLabel,LLVMParameter> _engine) {
		super(_network, _engine);
	}
	
	public void addAll() {
		final String name = "[A >= B] = [(A*C) >= (B*C)]  (if C is a nonnegative constant int)";

		AxiomizerHelper helper = new AxiomizerHelper(
			new PeggyAxiomizer<LLVMLabel,Integer>(name, getNetwork(), getAmbassador()));

		PeggyVertex<LLVMLabel,Integer> A = helper.getVariable("A");
		PeggyVertex<LLVMLabel,Integer> B = helper.getVariable("B");
		PeggyVertex<LLVMLabel,Integer> C = helper.get("C", null); // will be a term but label ignored
		PeggyVertex<LLVMLabel,Integer> AgeB = helper.get("AgeB", SGE, A, B);
		PeggyVertex<LLVMLabel,Integer> AtimesC = helper.get("AtimesC", MUL, A, C);
		
		helper.mustExist(AgeB);
		helper.mustExist(AtimesC);
		
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
				// >= (mul A C) (mul B C)
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs) addConstantProperties(proof, bundle.getTerm("C"));

				Node result = node(
						SGE,
						conc(node(
								MUL,
								steal(bundle.getTerm("AgeB"),0),
								steal(bundle.getTerm("AtimesC"), 1))),
						conc(node(
								MUL,
								steal(bundle.getTerm("AgeB"), 1),
								steal(bundle.getTerm("AtimesC"), 1))));
				result.finish(bundle.getTerm("AgeB"), proof, futureGraph);
				
				return bundle.getTerm("C").getOp().toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel,LLVMParameter> cTerm = bundle.getTerm("C");
				return cTerm.getOp().isDomain() && cTerm.getOp().getDomain().isConstantValue() &&
					cTerm.getOp().getDomain().getConstantValueSelf().getValue().isInteger() &&
					!cTerm.getOp().getDomain().getConstantValueSelf().getValue().getIntegerSelf().isNegative();
			}
		};
		
		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
}
