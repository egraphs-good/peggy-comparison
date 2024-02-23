package peggy.analysis.java;

import peggy.analysis.Analysis;
import peggy.represent.java.ConstantValueJavaLabel;
import peggy.represent.java.JavaLabel;
import peggy.represent.java.JavaOperator;
import peggy.represent.java.JavaParameter;
import peggy.represent.java.SimpleJavaLabel;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.basic.TermOrTermChild;
import eqsat.meminfer.engine.event.ProofEvent;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyVertex;

/**
 * This analysis deals with common optimizations over constants and binary
 * operations. (i.e. X+0 = X)
 */
public abstract class JavaConstantAnalysis extends Analysis<JavaLabel,JavaParameter> {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("JavaConstantAnalysis: " + message);
	}

	private static final JavaLabel sub = SimpleJavaLabel.create(JavaOperator.MINUS);
	private static final JavaLabel add = SimpleJavaLabel.create(JavaOperator.PLUS);
	private static final JavaLabel xor = SimpleJavaLabel.create(JavaOperator.XOR);
	
	public JavaConstantAnalysis(
			Network _network,
			CPeggyAxiomEngine<JavaLabel, JavaParameter> _engine) {
		super(_network, _engine);
	}
	
	public void addAll() {
		//JavaLabel srem = new BinopJavaLabel(Binop.SRem);
		//JavaLabel sdiv = new BinopJavaLabel(Binop.SDiv);
		addDoubleBinopLeft(add, sub, 1);

		addDoubleBinopRight(sub, xor, 2);
		addDoubleBinopRight(sub, sub, 2);
		//addDoubleBinopRight(sub, sdiv, 1);
		addDoubleBinopRight(sub, sub, 1);
		//addDoubleBinopRight(srem, sub, 2);
	}

	////////////////////////////////////
	
	// (op1 (op2 A B) C)
	private void addDoubleBinopLeft(
			final JavaLabel op1, 
			final JavaLabel op2, 
			final int constantIndex) {
		final String name = "(" + op1 + " (" + op2 + " A B) C)";
		
		AxiomizerHelper helper = new AxiomizerHelper(
			new PeggyAxiomizer<JavaLabel, Integer>(name, getNetwork(), getAmbassador()));
		PeggyVertex<JavaLabel, Integer> A;
		PeggyVertex<JavaLabel, Integer> B;
		PeggyVertex<JavaLabel, Integer> C;
		switch (constantIndex) {
		case 0:
			A = helper.get("constantTerm", null);
			B = helper.getVariable("firstRep");
			C = helper.getVariable("secondRep");
			break;
		case 1: 
			A = helper.getVariable("firstRep");
			B = helper.get("constantTerm", null);
			C = helper.getVariable("secondRep");
			break;
		default:
			A = helper.getVariable("firstRep");
			B = helper.getVariable("secondRep");
			C = helper.get("constantTerm", null);
			break;
		}
		
		PeggyVertex<JavaLabel, Integer> op1node = 
			helper.get("op1",
					op1,
					helper.get("op2", op2, A, B),
					C);
		helper.mustExist(op1node);
		
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
				simplifyDoubleBinopLeft(op1, op2, bundle, constantIndex, null);
				return null;
			}
			protected boolean matches(Bundle bundle) {
				return simplifyDoubleBinopLeft(op1, op2, bundle, constantIndex, null);
			}
		};
		
		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}

	
	// (op1 (op2 A B) C)
	private boolean simplifyDoubleBinopLeft(
			JavaLabel op1, JavaLabel op2,
			Bundle bundle,
			int constantIndex,
			FutureExpressionGraph<FlowValue<JavaParameter, JavaLabel>, CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel, JavaParameter>> futureGraph) {
		CPEGTerm<JavaLabel, JavaParameter> topTerm = bundle.getTerm("op1");
		CPEGTerm<JavaLabel, JavaParameter> innerTerm = bundle.getTerm("op2");
		CPEGTerm<JavaLabel, JavaParameter> constantTerm = bundle.getTerm("constantTerm");
		TermOrTermChild<CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel, JavaParameter>> firstRep = bundle.getRep("firstRep");
		TermOrTermChild<CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel, JavaParameter>> secondRep = bundle.getRep("secondRep");
		if (!constantTerm.getOp().isDomain())
			return false;
		JavaLabel constantLabel = constantTerm.getOp().getDomain();
		Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
		if (enableProofs) addConstantProperties(proof, constantTerm);
		
		if (op1.isSimple()) {
			switch (op1.getSimpleSelf().getOperator()) {
			case PLUS:
				if (constantIndex==1 && 
					constantLabel.isConstant()) {
					if (constantLabel.getConstantSelf().getValue() instanceof IntConstant) {
						int C = ((IntConstant)constantLabel.getConstantSelf().getValue()).value;
						if (C == 0 && op2.equals(sub)) {
							// add (sub 0 A) B = sub B A
							if (futureGraph != null) {
								Node result = node(
										sub,
										steal(topTerm, 1),
										steal(innerTerm, 1));
								result.finish(topTerm, proof, futureGraph);
							}
							return true;
						}
					}
					else if (constantLabel.getConstantSelf().getValue() instanceof LongConstant) {
						long C = ((LongConstant)constantLabel.getConstantSelf().getValue()).value;
						if (C == 0L && op2.equals(sub)) {
							// add (sub 0 A) B = sub B A
							if (futureGraph != null) {
								Node result = node(
										sub,
										steal(topTerm, 1),
										steal(innerTerm, 1));
								result.finish(topTerm, proof, futureGraph);
							}
							return true;
						}
					}
				}
				break;
			}
		}
		
		return false;
	}
	
	
	// (op1 A (op2 B C))
	private void addDoubleBinopRight(
			final JavaLabel op1, 
			final JavaLabel op2, 
			final int constantIndex) {
		final String name ="(" + op1 + " A (" + op2 + " B C))"; 
		
		AxiomizerHelper helper = new AxiomizerHelper(
			new PeggyAxiomizer<JavaLabel, Integer>(name, getNetwork(), getAmbassador()));
		PeggyVertex<JavaLabel, Integer> A;
		PeggyVertex<JavaLabel, Integer> B;
		PeggyVertex<JavaLabel, Integer> C;
		switch (constantIndex) {
		case 0:
			A = helper.get("constantTerm", null);
			B = helper.getVariable("firstRep");
			C = helper.getVariable("secondRep");
			break;
		case 1: 
			A = helper.getVariable("firstRep");
			B = helper.get("constantTerm", null);
			C = helper.getVariable("secondRep");
			break;
		default:
			A = helper.getVariable("firstRep");
			B = helper.getVariable("secondRep");
			C = helper.get("constantTerm", null);
			break;
		}
		
		PeggyVertex<JavaLabel, Integer> op1node = 
			helper.get("op1",
					op1,
					A,
					helper.get("op2", op2, B, C));
		helper.mustExist(op1node);
		
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
				simplifyDoubleBinopRight(op1, op2, bundle, constantIndex, null);
				return null;
			}
			protected boolean matches(Bundle bundle) {
				return simplifyDoubleBinopRight(op1, op2, bundle, constantIndex, null);
			}
		};
		
		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	// (op1 A (op2 B C))
	private boolean simplifyDoubleBinopRight(
			JavaLabel op1, JavaLabel op2,
			Bundle bundle, 
			int constantIndex,
			FutureExpressionGraph<FlowValue<JavaParameter, JavaLabel>, CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel, JavaParameter>> futureGraph) {
		CPEGTerm<JavaLabel, JavaParameter> topTerm = bundle.getTerm("op1");
		CPEGTerm<JavaLabel, JavaParameter> innerTerm = bundle.getTerm("op2");
		CPEGTerm<JavaLabel, JavaParameter> constantTerm = bundle.getTerm("constantTerm");
		TermOrTermChild<CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel, JavaParameter>> firstRep = bundle.getRep("firstRep");
		TermOrTermChild<CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel, JavaParameter>> secondRep = bundle.getRep("secondRep");
		if (!constantTerm.getOp().isDomain())
			return false;
		JavaLabel constantLabel = constantTerm.getOp().getDomain();
		Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
		if (enableProofs) addConstantProperties(proof, constantTerm);
		
		if (op1.isSimple()) {
			switch (op1.getSimpleSelf().getOperator()) {
			case MINUS:
				if (constantIndex==1 && 
					constantLabel.isConstant()) {
					if (constantLabel.getConstantSelf().getValue() instanceof IntConstant) {
						int C = ((IntConstant)constantLabel.getConstantSelf().getValue()).value;
						if (C == -1 && op2.equals(xor)) {
							// sub A (xor -1 C) = add C (add 1 A)
							if (futureGraph != null) {
								Node result = node(
										add,
										steal(innerTerm,1),
										conc(node(
												add,
												conc(node(new ConstantValueJavaLabel(IntConstant.v(1)))),
												steal(topTerm,0))));
								result.finish(topTerm, proof, futureGraph);
							}
							return true;
						} else if (C == 0 && op2.equals(sub)) {
							// sub A (sub 0 C) = add A C
							if (futureGraph != null) {
								Node result = node(
										add,
										steal(topTerm,0),
										steal(innerTerm,1));
								result.finish(topTerm, proof, futureGraph);
							}
							return true;
						}
					}
					else if (constantLabel.getConstantSelf().getValue() instanceof LongConstant) {
						long C = ((LongConstant)constantLabel.getConstantSelf().getValue()).value;
						if (C == -1L && op2.equals(xor)) {
							// sub A (xor -1 C) = add C (add 1 A)
							if (futureGraph != null) {
								Node result = node(
										add,
										steal(innerTerm,1),
										conc(node(
												add,
												conc(node(new ConstantValueJavaLabel(LongConstant.v(1L)))),
												steal(topTerm,0))));
								result.finish(topTerm, proof, futureGraph);
							}
							return true;
						} else if (C == 0L && op2.equals(sub)) {
							// sub A (sub 0 C) = add A C
							if (futureGraph != null) {
								Node result = node(
										add,
										steal(topTerm,0),
										steal(innerTerm,1));
								result.finish(topTerm, proof, futureGraph);
							}
							return true;
						}
					}
				}
				else if (constantIndex==0 &&
						 constantLabel.isConstant()) {
					if (constantLabel.getConstantSelf().getValue() instanceof IntConstant) {
						int C = ((IntConstant)constantLabel.getConstantSelf().getValue()).value;
						/*
						if (C == 0 && op2.equals(new BinopJavaLabel(Binop.SDiv))) {
						// sub 0 (sdiv B C) = sdiv B (sub 0 C)
						if (futureGraph != null) {
							Node result = node(
									new BinopJavaLabel(Binop.SDiv),
									steal(innerTerm,0),
									conc(node(
											new BinopJavaLabel(Binop.Sub),
											steal(topTerm,0),
											steal(innerTerm,1))));
							result.finish(topTerm, proof, futureGraph);
						}
						return true;
						 */
						if (C == 0 && op2.equals(sub)) {
							// sub 0 (sub B C) = sub C B
							if (futureGraph != null) {
								Node result = node(
										sub,
										steal(innerTerm,1),
										steal(innerTerm,0));
								result.finish(topTerm, proof, futureGraph);
							}
							return true;
						}
					}
					else if (constantLabel.getConstantSelf().getValue() instanceof LongConstant) {
						long C = ((LongConstant)constantLabel.getConstantSelf().getValue()).value;
						if (C == 0L && op2.equals(sub)) {
							// sub 0 (sub B C) = sub C B
							if (futureGraph != null) {
								Node result = node(
										sub,
										steal(innerTerm,1),
										steal(innerTerm,0));
								result.finish(topTerm, proof, futureGraph);
							}
							return true;
						}
					}
				}
				break;
				/*
			case SRem:
				if (constantIndex==1 &&
					constantLabel.isConstantValue() &&
					constantLabel.getConstantValueSelf().getValue().isInteger()) {
					IntegerValue C = constantLabel.getConstantValueSelf().getValue().getIntegerSelf();
					if (C.isZero() && op2.equals(new BinopJavaLabel(Binop.Sub))) {
						// srem A (sub 0 C) = srem A C
						if (futureGraph != null) {
							Node result = node(
									new BinopJavaLabel(Binop.SRem),
									steal(topTerm,0),
									steal(innerTerm,1));
							result.finish(topTerm, proof, futureGraph);
						}
						return true;
					}
				}
				break;
				*/
			}
		}
		
		return false;
	}
}
