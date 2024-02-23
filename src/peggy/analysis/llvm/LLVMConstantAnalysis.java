package peggy.analysis.llvm;

import llvm.instructions.Binop;
import llvm.types.PointerType;
import llvm.values.ConstantVectorValue;
import llvm.values.IntegerValue;
import llvm.values.UndefValue;
import llvm.values.Value;
import peggy.analysis.Analysis;
import peggy.represent.llvm.BinopLLVMLabel;
import peggy.represent.llvm.ConstantValueLLVMLabel;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMOperator;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.SimpleLLVMLabel;
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
 * This analysis includes axiom about constants and operations over them.
 */
public abstract class LLVMConstantAnalysis extends Analysis<LLVMLabel,LLVMParameter> {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("LLVMConstantAnalysis: " + message);
	}

	public LLVMConstantAnalysis(
			Network _network,
			CPeggyAxiomEngine<LLVMLabel, LLVMParameter> _engine) {
		super(_network, _engine);
	}
	
	public void addAll() {
		addTriopConstantAxioms();
		
		LLVMLabel sub = new BinopLLVMLabel(Binop.Sub);
		LLVMLabel add = new BinopLLVMLabel(Binop.Add);
		LLVMLabel xor = new BinopLLVMLabel(Binop.Xor);
		LLVMLabel srem = new BinopLLVMLabel(Binop.SRem);
		LLVMLabel sdiv = new BinopLLVMLabel(Binop.SDiv);
		
		addDoubleBinopLeft(add, sub, 1);

		addDoubleBinopRight(sub, xor, 2);
		addDoubleBinopRight(sub, sub, 2);
		addDoubleBinopRight(sub, sdiv, 1);
		addDoubleBinopRight(sub, sub, 1);
		addDoubleBinopRight(srem, sub, 2);
	}

	////////////////////////////////////
	
	private void addTriopConstantAxioms() {
		addTriopConstantAxiom(SimpleLLVMLabel.get(LLVMOperator.INSERTELEMENT), 0);
		addTriopConstantAxiom(SimpleLLVMLabel.get(LLVMOperator.INSERTELEMENT), 1);
		
		addTriopConstantAxiom(SimpleLLVMLabel.get(LLVMOperator.SHUFFLEVECTOR), 2);
		
		addTriopConstantAxiom(SimpleLLVMLabel.get(LLVMOperator.SELECT), 1);
		addTriopConstantAxiom(SimpleLLVMLabel.get(LLVMOperator.SELECT), 2);
		
		addTriopConstantAxiom(SimpleLLVMLabel.get(LLVMOperator.LOAD), 1);
		
		// TODO more?
	}
	private void addTriopConstantAxiom(LLVMLabel operator, final int constantIndex) {
		// operator A B C (one is constant)
		if (!(0 <= constantIndex && constantIndex < 3))
			throw new IllegalArgumentException("Constant index must be in [0,3)");

		final String name = operator + "(A,B,C) one constant";
		
		String[][] names = {
				{"C","first","second"},
				{"first","C","second"},
				{"first","second","C"}
		};

		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));

		PeggyVertex<LLVMLabel, Integer> A = (constantIndex==0 ? helper.get(names[constantIndex][0],null) : helper.getVariable(names[constantIndex][0]));
		PeggyVertex<LLVMLabel, Integer> B = (constantIndex==1 ? helper.get(names[constantIndex][1],null) : helper.getVariable(names[constantIndex][1]));
		PeggyVertex<LLVMLabel, Integer> C = (constantIndex==2 ? helper.get(names[constantIndex][2],null) : helper.getVariable(names[constantIndex][2]));

		PeggyVertex<LLVMLabel, Integer> op =
			helper.get("op", operator, A, B, C); 
		helper.mustExist(op);

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
				simplifyTriop(constantIndex, bundle, futureGraph);
				return bundle.getTerm("C").getOp().getDomain().toString();
			}
			protected boolean matches(Bundle bundle) {
				return simplifyTriop(constantIndex, bundle, null);
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	

	// op(a,b,c) = d  (a, b, or c is constant)
	private boolean simplifyTriop(
			int constantIndex, 
			Bundle bundle,
			FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> futureGraph) {
		final CPEGTerm<LLVMLabel, LLVMParameter> opTerm = bundle.getTerm("op"); 
		final CPEGTerm<LLVMLabel, LLVMParameter> constantTerm = bundle.getTerm("C");
		final TermOrTermChild<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> firstRep =
			bundle.getRep("first");			
		final TermOrTermChild<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> secondRep =
			bundle.getRep("second");
		if (!(opTerm.getOp().isDomain() && constantTerm.getOp().isDomain()))
			return false;
		
		LLVMLabel opLabel = opTerm.getOp().getDomain();
		LLVMLabel constantLabel = constantTerm.getOp().getDomain();
		Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
		if (enableProofs) addConstantProperties(proof, constantTerm);

		if (opLabel.isSimple()) {
			switch (opLabel.getSimpleSelf().getOperator()) {
			case INSERTELEMENT:
				if (constantIndex==1 && constantLabel.isConstantValue() && constantLabel.getConstantValueSelf().getValue().isUndef()) {
					// insertelement VEC undef INDEX = VEC
					if (futureGraph != null)
						getEngine().getEGraph().makeEqual(firstRep, opTerm, proof);
					return true;
				} 
				else if (constantIndex==0 && constantLabel.isConstantValue() && constantLabel.getConstantValueSelf().getValue().isUndef()) {
					// insertelement undef VAL INDEX = undef
					if (futureGraph != null)
						getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
					return true;
				}
				break;
				
			case SHUFFLEVECTOR:
				if (constantIndex==2 && constantLabel.isConstantValue()) {
					Value C = constantLabel.getConstantValueSelf().getValue();
					if (C.isUndef()) {
						// shufflevector V1 V2 undef = undef
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
						return true;
					}
					else if (C.isConstantVector()) {
						ConstantVectorValue cvv = C.getConstantVectorSelf();
						boolean missedlow = false;
						boolean missedhigh = false;
						for (int i = 0; i < cvv.getNumElements().signedValue(); i++) {
							if (cvv.getElement(i).isUndef())
								continue;
							else if (cvv.getElement(i).isInteger()) {
								IntegerValue low = IntegerValue.get(32, new long[]{i});
								IntegerValue high = IntegerValue.get(32, new long[]{cvv.getNumElements().signedValue()+i});
								if (!cvv.getElement(i).equalsValue(low))
									missedlow = true;
								if (!cvv.getElement(i).equalsValue(high))
									missedhigh = true;
							}
						}
						
						if (!missedlow) {
							// shufflevector V1 V2 <0,1,2,3,...> = V1
							if (futureGraph != null)
								getEngine().getEGraph().makeEqual(firstRep, opTerm, proof);
							return true;
						}
						else if (!missedhigh) {
							// shufflevector V1 V2 <n,n+1,n+2,n+3,...> = V2
							if (futureGraph != null)
								getEngine().getEGraph().makeEqual(secondRep, opTerm, proof);
							return true;
						}
					}
				}
				break;
				
			case SELECT:
				if ((constantIndex==1 || constantIndex==2) && 
					constantLabel.isConstantValue() && 
					constantLabel.getConstantValueSelf().getValue().isUndef()) {
					// select C undef X = X
					// select C X undef = X
					if (futureGraph != null)
						getEngine().getEGraph().makeEqual(secondRep, opTerm, proof);					
					return true;
				}
				else if (constantIndex==2 && constantLabel.isConstantValue() && 
						constantLabel.getConstantValueSelf().getValue().equalsValue(IntegerValue.FALSE)) {
					// select B C false = and B C
					if (futureGraph != null) {
						Node result = node(
								new BinopLLVMLabel(Binop.And),
								steal(opTerm, 0),
								steal(opTerm, 1));
						result.finish(opTerm, proof, futureGraph);
					}
					return true;	
				}
				else if (constantIndex==1 && constantLabel.isConstantValue() && 
						constantLabel.getConstantValueSelf().getValue().equalsValue(IntegerValue.FALSE)) {
					// select B false C = and !B C
					if (futureGraph != null) {
						Node result = node(
								new BinopLLVMLabel(Binop.And),
								conc(nodeFlow(
										FlowValue.<LLVMParameter,LLVMLabel>createNegate(),
										steal(opTerm,0))),
								steal(opTerm,2));
						result.finish(opTerm, proof, futureGraph);
					}
					return true;
				}
				else if (constantIndex==2 && constantLabel.isConstantValue() && 
						constantLabel.getConstantValueSelf().getValue().equalsValue(IntegerValue.TRUE)) {
					// select B C true = or !B C
					if (futureGraph != null) {
						Node result = node(
								new BinopLLVMLabel(Binop.Or),
								conc(nodeFlow(
										FlowValue.<LLVMParameter,LLVMLabel>createNegate(),
										steal(opTerm,0))),
								steal(opTerm,1));
						result.finish(opTerm, proof, futureGraph);
					}
					return true;
				}
				break;
				
			case LOAD:
				if (constantIndex==1 && constantLabel.isConstantValue()) {
					Value C = constantLabel.getConstantValueSelf().getValue();
					PointerType type = C.getType().getCompositeSelf().getPointerSelf();
					if (C.isUndef() || C.isConstantNullPointer()) {
						// load null/undef = undef
						if (futureGraph != null) {
							Node result = node(new ConstantValueLLVMLabel(new UndefValue(type.getPointeeType())));
							result.finish(opTerm, proof, futureGraph);
						}
						return true;
					} 
				} 
				break;
			}
			
		}

		return false;
	}
	
	
	// (op1 (op2 A B) C)
	private void addDoubleBinopLeft(final LLVMLabel op1, final LLVMLabel op2, final int constantIndex) {
		final String name = "(" + op1 + " (" + op2 + " A B) C)";
		
		AxiomizerHelper helper = new AxiomizerHelper(
			new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		PeggyVertex<LLVMLabel, Integer> A;
		PeggyVertex<LLVMLabel, Integer> B;
		PeggyVertex<LLVMLabel, Integer> C;
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
		
		PeggyVertex<LLVMLabel, Integer> op1node = 
			helper.get("op1",
					op1,
					helper.get("op2", op2, A, B),
					C);
		helper.mustExist(op1node);
		
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
			LLVMLabel op1, LLVMLabel op2,
			Bundle bundle,
			int constantIndex,
			FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> futureGraph) {
		CPEGTerm<LLVMLabel, LLVMParameter> topTerm = bundle.getTerm("op1");
		CPEGTerm<LLVMLabel, LLVMParameter> innerTerm = bundle.getTerm("op2");
		CPEGTerm<LLVMLabel, LLVMParameter> constantTerm = bundle.getTerm("constantTerm");
		TermOrTermChild<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> firstRep = bundle.getRep("firstRep");
		TermOrTermChild<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> secondRep = bundle.getRep("secondRep");
		if (!constantTerm.getOp().isDomain())
			return false;
		LLVMLabel constantLabel = constantTerm.getOp().getDomain();
		Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
		if (enableProofs) addConstantProperties(proof, constantTerm);
		
		if (op1.isBinop()) {
			switch (op1.getBinopSelf().getOperator()) {
			case Add:
				if (constantIndex==1 && 
					constantLabel.isConstantValue() &&
					constantLabel.getConstantValueSelf().getValue().isInteger()) {
					IntegerValue C = constantLabel.getConstantValueSelf().getValue().getIntegerSelf();
					if (C.isZero() && op2.equals(new BinopLLVMLabel(Binop.Sub))) {
						// add (sub 0 A) B = sub B A
						if (futureGraph != null) {
							Node result = node(
									new BinopLLVMLabel(Binop.Sub),
									steal(topTerm, 1),
									steal(innerTerm, 1));
							result.finish(topTerm, proof, futureGraph);
						}
						return true;
					}
				}
				break;
			}
		}
		
		return false;
	}
	
	
	// (op1 A (op2 B C))
	private void addDoubleBinopRight(final LLVMLabel op1, final LLVMLabel op2, final int constantIndex) {
		final String name ="(" + op1 + " A (" + op2 + " B C))"; 
		
		AxiomizerHelper helper = new AxiomizerHelper(
			new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		PeggyVertex<LLVMLabel, Integer> A;
		PeggyVertex<LLVMLabel, Integer> B;
		PeggyVertex<LLVMLabel, Integer> C;
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
		
		PeggyVertex<LLVMLabel, Integer> op1node = 
			helper.get("op1",
					op1,
					A,
					helper.get("op2", op2, B, C));
		helper.mustExist(op1node);
		
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
			LLVMLabel op1, LLVMLabel op2,
			Bundle bundle, 
			int constantIndex,
			FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> futureGraph) {
		CPEGTerm<LLVMLabel, LLVMParameter> topTerm = bundle.getTerm("op1");
		CPEGTerm<LLVMLabel, LLVMParameter> innerTerm = bundle.getTerm("op2");
		CPEGTerm<LLVMLabel, LLVMParameter> constantTerm = bundle.getTerm("constantTerm");
		TermOrTermChild<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> firstRep = bundle.getRep("firstRep");
		TermOrTermChild<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> secondRep = bundle.getRep("secondRep");
		if (!constantTerm.getOp().isDomain())
			return false;
		LLVMLabel constantLabel = constantTerm.getOp().getDomain();
		Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
		if (enableProofs) addConstantProperties(proof, constantTerm);
		
		if (op1.isBinop()) {
			switch (op1.getBinopSelf().getOperator()) {
			case Sub:
				if (constantIndex==1 && 
					constantLabel.isConstantValue() &&
					constantLabel.getConstantValueSelf().getValue().isInteger()) {
					IntegerValue C = constantLabel.getConstantValueSelf().getValue().getIntegerSelf();
					if (C.isNegativeOne() && op2.equals(new BinopLLVMLabel(Binop.Xor))) {
						// sub A (xor -1 C) = add C (add 1 A)
						if (futureGraph != null) {
							Node result = node(
									new BinopLLVMLabel(Binop.Add),
									steal(innerTerm,1),
									conc(node(
											new BinopLLVMLabel(Binop.Add),
											conc(node(new ConstantValueLLVMLabel(IntegerValue.getOne(C.getWidth())))),
											steal(topTerm,0))));
							result.finish(topTerm, proof, futureGraph);
						}
						return true;
					} else if (C.isZero() && op2.equals(new BinopLLVMLabel(Binop.Sub))) {
						// sub A (sub 0 C) = add A C
						if (futureGraph != null) {
							Node result = node(
									new BinopLLVMLabel(Binop.Add),
									steal(topTerm,0),
									steal(innerTerm,1));
							result.finish(topTerm, proof, futureGraph);
						}
						return true;
					}
				}
				else if (constantIndex==0 &&
						 constantLabel.isConstantValue() &&
						 constantLabel.getConstantValueSelf().getValue().isInteger()) {
					IntegerValue C = constantLabel.getConstantValueSelf().getValue().getIntegerSelf();
					if (C.isZero() && op2.equals(new BinopLLVMLabel(Binop.SDiv))) {
						// sub 0 (sdiv B C) = sdiv B (sub 0 C)
						if (futureGraph != null) {
							Node result = node(
									new BinopLLVMLabel(Binop.SDiv),
									steal(innerTerm,0),
									conc(node(
											new BinopLLVMLabel(Binop.Sub),
											steal(topTerm,0),
											steal(innerTerm,1))));
							result.finish(topTerm, proof, futureGraph);
						}
						return true;
					} else if (C.isZero() && op2.equals(new BinopLLVMLabel(Binop.Sub))) {
						// sub 0 (sub B C) = sub C B
						if (futureGraph != null) {
							Node result = node(
									new BinopLLVMLabel(Binop.Sub),
									steal(innerTerm,1),
									steal(innerTerm,0));
							result.finish(topTerm, proof, futureGraph);
						}
						return true;
					}
				}
				break;

			case SRem:
				if (constantIndex==1 &&
					constantLabel.isConstantValue() &&
					constantLabel.getConstantValueSelf().getValue().isInteger()) {
					IntegerValue C = constantLabel.getConstantValueSelf().getValue().getIntegerSelf();
					if (C.isZero() && op2.equals(new BinopLLVMLabel(Binop.Sub))) {
						// srem A (sub 0 C) = srem A C
						if (futureGraph != null) {
							Node result = node(
									new BinopLLVMLabel(Binop.SRem),
									steal(topTerm,0),
									steal(innerTerm,1));
							result.finish(topTerm, proof, futureGraph);
						}
						return true;
					}
				}
				break;
			}
		}
		
		return false;
	}
}
