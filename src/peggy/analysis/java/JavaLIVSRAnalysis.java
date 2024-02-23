package peggy.analysis.java;

import peggy.Logger;
import peggy.analysis.Analysis;
import peggy.represent.java.JavaLabel;
import peggy.represent.java.JavaOperator;
import peggy.represent.java.JavaParameter;
import peggy.represent.java.SimpleJavaLabel;
import soot.jimple.IntConstant;
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
 * This class contains axioms that help when performing the 
 * loop-induction-variable strength reduction (LIVSR) optimization.
 */
public abstract class JavaLIVSRAnalysis extends Analysis<JavaLabel,JavaParameter> {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("JavaLIVSRAnalysis: " + message);
	}

	private static final JavaLabel MUL = SimpleJavaLabel.create(JavaOperator.TIMES);
	private static final JavaLabel GE = SimpleJavaLabel.create(JavaOperator.GREATER_THAN_EQUAL);
	
	public JavaLIVSRAnalysis(
			Network _network, 
			CPeggyAxiomEngine<JavaLabel,JavaParameter> _engine) {
		super( _network, _engine);
	}
	
	public void addAll(Logger logger) {
		final String name = "[A >= B] = [(A*C) >= (B*C)]  (if C is a nonnegative constant int)";

		AxiomizerHelper helper = new AxiomizerHelper(
			new PeggyAxiomizer<JavaLabel,Integer>(name, getNetwork(), getAmbassador()));

		PeggyVertex<JavaLabel,Integer> A = helper.getVariable("A");
		PeggyVertex<JavaLabel,Integer> B = helper.getVariable("B");
		PeggyVertex<JavaLabel,Integer> C = helper.get("C", null); // will be a term but label ignored
		PeggyVertex<JavaLabel,Integer> AgeB = helper.get("AgeB", GE, A, B);
		PeggyVertex<JavaLabel,Integer> AtimesC = helper.get("AtimesC", MUL, A, C);
		
		helper.mustExist(AgeB);
		helper.mustExist(AtimesC);
		
		final ProofEvent<CPEGTerm<JavaLabel,JavaParameter>,? extends Structure<CPEGTerm<JavaLabel, JavaParameter>>> 
		triggerEvent = helper.getTrigger();
		final StructureFunctions functions = helper.getStructureFunctions();
		
		ShapeListener listener = new ShapeListener() {
			protected ProofEvent<CPEGTerm<JavaLabel,JavaParameter>,? extends Structure<CPEGTerm<JavaLabel, JavaParameter>>> getProofEvent() {return triggerEvent;}
			protected StructureFunctions getFunctions() {return functions;}
			protected String getName() {return name;}
			protected String build(
					Bundle bundle,
					FutureExpressionGraph<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>> futureGraph) {
				// >= (mul A C) (mul B C)
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs) addConstantProperties(proof, bundle.getTerm("C"));

				Node result = node(
						GE,
						conc(node(
								MUL,
								steal(bundle.getTerm("AgeB"),0),
								steal(bundle.getTerm("AtimesC"), 1))),
						conc(node(
								MUL,
								steal(bundle.getTerm("AgeB"), 1),
								steal(bundle.getTerm("AtimesC"), 1))));
				result.buildFutureVertex(futureGraph);
				getEngine().getEGraph().addExpressions(futureGraph);
				if (enableProofs) result.buildProof(proof);
				getEngine().getEGraph().makeEqual(result.getTerm(), bundle.getTerm("AgeB"), proof);
				
				return bundle.getTerm("C").getOp().toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<JavaLabel,JavaParameter> cTerm = bundle.getTerm("C");
				return cTerm.getOp().isDomain() && cTerm.getOp().getDomain().isConstant() &&
					(cTerm.getOp().getDomain().getConstantSelf().getValue() instanceof IntConstant) &&
					((IntConstant)cTerm.getOp().getDomain().getConstantSelf().getValue()).value >= 0;
			}
		};
		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
}
