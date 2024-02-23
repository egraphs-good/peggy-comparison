package peggy.analysis.llvm;

import java.util.BitSet;

import llvm.instructions.Binop;
import llvm.instructions.ComparisonPredicate;
import llvm.instructions.FloatingPointComparisonPredicate;
import llvm.types.IntegerType;
import llvm.values.ConstantVectorValue;
import llvm.values.IntegerValue;
import llvm.values.UndefValue;
import llvm.values.Value;
import peggy.analysis.Analysis;
import peggy.represent.llvm.BinopLLVMLabel;
import peggy.represent.llvm.CmpLLVMLabel;
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
 * This analysis has axioms about constants and binary operations over them.
 */
public abstract class LLVMBinopConstantAnalysis extends Analysis<LLVMLabel,LLVMParameter> {
	public LLVMBinopConstantAnalysis(
			Network _network,
			CPeggyAxiomEngine<LLVMLabel, LLVMParameter> _engine) {
		super(_network, _engine);
	}

	public void addAll() {
		addBinopRightConstantAxiom(new BinopLLVMLabel(Binop.Add));
		addBinopRightConstantAxiom(new BinopLLVMLabel(Binop.Sub));
		addBinopRightConstantAxiom(new BinopLLVMLabel(Binop.Mul));
		addBinopRightConstantAxiom(new BinopLLVMLabel(Binop.UDiv));
		addBinopRightConstantAxiom(new BinopLLVMLabel(Binop.SDiv));
		addBinopRightConstantAxiom(new BinopLLVMLabel(Binop.FRem));
		addBinopRightConstantAxiom(new BinopLLVMLabel(Binop.URem));
		addBinopRightConstantAxiom(new BinopLLVMLabel(Binop.And));
		addBinopRightConstantAxiom(new BinopLLVMLabel(Binop.Or));
		addBinopRightConstantAxiom(new BinopLLVMLabel(Binop.AShr));
		addBinopRightConstantAxiom(new BinopLLVMLabel(Binop.LShr));
		addBinopRightConstantAxiom(new BinopLLVMLabel(Binop.Shl));
		addBinopRightConstantAxiom(new BinopLLVMLabel(Binop.Xor));
		for (FloatingPointComparisonPredicate fcmp : FloatingPointComparisonPredicate.values()) {
			addBinopRightConstantAxiom(new CmpLLVMLabel(fcmp));
		}
		
		addBinopLeftConstantAxiom(SimpleLLVMLabel.get(LLVMOperator.EXTRACTELEMENT));
		addBinopLeftConstantAxiom(new BinopLLVMLabel(Binop.Sub));
		addBinopLeftConstantAxiom(new BinopLLVMLabel(Binop.UDiv));
		addBinopLeftConstantAxiom(new BinopLLVMLabel(Binop.SDiv));
		addBinopLeftConstantAxiom(new BinopLLVMLabel(Binop.URem));
		addBinopLeftConstantAxiom(new BinopLLVMLabel(Binop.SRem));
		addBinopLeftConstantAxiom(new BinopLLVMLabel(Binop.Shl));
		addBinopLeftConstantAxiom(new BinopLLVMLabel(Binop.AShr));
		addBinopLeftConstantAxiom(new BinopLLVMLabel(Binop.LShr));
	}
	
	private void addBinopRightConstantAxiom(LLVMLabel operator) {
		// operator(X,c)
		final String name = operator + "(X,c)";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));

		PeggyVertex<LLVMLabel, Integer> opnode = 
			helper.get("op", operator, 
					helper.getVariable("X"), 
					helper.get("C", null));
		helper.mustExist(opnode);

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
				simplifyBinopRight(bundle, futureGraph);
				return bundle.getTerm("C").getOp().toString();
			}
			protected boolean matches(Bundle bundle) {
				return simplifyBinopRight(bundle, null);
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	

	private void addBinopLeftConstantAxiom(LLVMLabel operator) {
		// operator(c,X)
		final String name = operator + "(c,X)";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));

		PeggyVertex<LLVMLabel, Integer> X = helper.getVariable("X");
		PeggyVertex<LLVMLabel, Integer> c = helper.get("C", null);
		PeggyVertex<LLVMLabel, Integer> opnode = 
			helper.get("op", operator, c, X);
		helper.mustExist(opnode);

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
				simplifyBinopLeft(bundle, futureGraph);
				return bundle.getTerm("C").getOp().toString();
			}
			protected boolean matches(Bundle bundle) {
				return simplifyBinopLeft(bundle, null);
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	// X op C
	private boolean simplifyBinopRight(
			Bundle bundle,
			FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> futureGraph) {
		CPEGTerm<LLVMLabel, LLVMParameter> opTerm = bundle.getTerm("op"); 
		CPEGTerm<LLVMLabel, LLVMParameter> constantTerm = bundle.getTerm("C");
		TermOrTermChild<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> xRep =
			bundle.getRep("X"); 
		if (!opTerm.getOp().isDomain())
			return false;
		final LLVMLabel opLabel = opTerm.getOp().getDomain();
		
		final LLVMLabel constantLabel;
		if (constantTerm.getOp().isDomain())
			constantLabel = constantTerm.getOp().getDomain();
		else if (constantTerm.getOp().isBasicOp())
			constantLabel = getAmbassador().getBasicOp(constantTerm.getOp().getBasicOp());
		else
			return false;
		
		Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
		if (enableProofs) addConstantProperties(proof, constantTerm);
		
		if (opLabel.isCmp()) {
			ComparisonPredicate pred = opLabel.getCmpSelf().getPredicate();
			if (pred.isFloatingPoint()) {
				if (constantLabel.isConstantValue() && constantLabel.getConstantValueSelf().getValue().isUndef()) {
					// fcmp * A undef = undef
					if (futureGraph != null) {
						getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
					}
					return true;
				}
			}
		} 
		else if (opLabel.isBinop()) {
			switch (opLabel.getBinopSelf().getOperator()) {
			case Add:
				if (constantLabel.isConstantValue()) {
					if (constantLabel.getConstantValueSelf().getValue().isUndef()) {
						// add A undef = undef
						if (futureGraph != null) {
							getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
						}
						return true;
					} 
					else if (constantLabel.getConstantValueSelf().getValue().isInteger()) {
						IntegerValue C = constantLabel.getConstantValueSelf().getValue().getIntegerSelf();
						int count = 0;
						for (int i = 0; i < C.getWidth(); i++)
							if (C.getBit(i))
								count++;
						
						if (C.isZero()) {
							// add A 0 = A
							if (futureGraph != null) {
								getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
							}
							return true;
						} 
						else if (count == 1 && C.getBit(C.getWidth()-1)) {
							// add X (signbit) = xor X (signbit)
							if (futureGraph != null) {
								Node result = node(
										new BinopLLVMLabel(Binop.Xor),
										steal(opTerm,0),
										steal(opTerm,1));
								result.finish(opTerm, proof, futureGraph);
							}
							return true;
						}
					}
				}
				break;
				
			case Mul:
				if (constantLabel.isConstantValue() && constantLabel.getConstantValueSelf().getValue().isInteger()) {
					IntegerValue C = constantLabel.getConstantValueSelf().getValue().getIntegerSelf();
					if (C.isZero()) {
						// mul A 0 = 0
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
						return true;
					} else if (C.isOne()) {
						// mul A 1 = A
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
						return true;
					} else if (C.isNegativeOne()) {
						// mul A (-1) = sub 0 A
						if (futureGraph != null) {
							Value zero = Value.getNullValue(C.getType());
							Node result = node(
									new BinopLLVMLabel(Binop.Sub),
									conc(node(new ConstantValueLLVMLabel(zero))),
									steal(opTerm,0));
							result.finish(opTerm, proof, futureGraph);
						}
						return true;
					}
				}
				break;
				
			case Sub:
				if (constantLabel.isConstantValue() && constantLabel.getConstantValueSelf().getValue().isInteger()) {
					IntegerValue C = constantLabel.getConstantValueSelf().getValue().getIntegerSelf();
					if (C.isZero()) {
						// sub A 0 = A
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
						return true;
					}
				}
				break;
				
			case UDiv:
				if (constantLabel.isConstantValue() && constantLabel.getConstantValueSelf().getValue().isInteger()) {
					IntegerValue C = constantLabel.getConstantValueSelf().getValue().getIntegerSelf();
					
					if (C.isOne()) {
						// udiv X 1 = X
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
						return true;
					}
					
					int count = 0;
					int last = 0;
					for (int i = 0; i < C.getWidth(); i++) {
						if (C.getBit(i)) {
							count++;
							last = i;
						}
					}
					// if C is a power of 2...
					if (count == 1 && !C.getBit(C.getWidth()-1)) {
						// udiv X (2^C) = ushr X C
						if (futureGraph != null) {
							IntegerValue newC = IntegerValue.get(C.getWidth(), new long[]{last});
							Node result = node(
									new BinopLLVMLabel(Binop.LShr),
									steal(opTerm,0),
									conc(node(new ConstantValueLLVMLabel(newC))));
							result.finish(opTerm, proof, futureGraph);
						}
						return true;
					}
				}
				break;
				
			case SDiv:
				if (constantLabel.isConstantValue() && constantLabel.getConstantValueSelf().getValue().isInteger()) {
					IntegerValue C = constantLabel.getConstantValueSelf().getValue().getIntegerSelf();				
					if (C.isOne()) {
						// sdiv X 1 = X
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
						return true;
					}
					else if (C.equalsInt(-1)) {
						// sdiv X -1 = sub 0 X
						if (futureGraph != null) {
							Node result = node(
									new BinopLLVMLabel(Binop.Sub),
									conc(node(new ConstantValueLLVMLabel(Value.getNullValue(C.getType())))),
									steal(opTerm,0));
							result.finish(opTerm, proof, futureGraph);
						}
						return true;
					}
				}
				break;
				
			case FRem:
				if (constantLabel.isConstantValue() && constantLabel.getConstantValueSelf().getValue().isUndef()) {
					// frem X undef = undef
					if (futureGraph != null)
						getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
					return true;
				}
				break;
				
			case URem:
				if (constantLabel.isConstantValue() && constantLabel.getConstantValueSelf().getValue().isInteger()) {
					IntegerValue C = constantLabel.getConstantValueSelf().getValue().getIntegerSelf();				
					
					int count = 0;
					int last = 0;
					for (int i = 0; i < C.getWidth(); i++) {
						if (C.getBit(i)) {
							count++;
							last = i;
						}
					}
					if (count == 0) {
						// urem X 0 = undef
						if (futureGraph != null) {
							Node result = node(new ConstantValueLLVMLabel(new UndefValue(C.getType())));
							result.finish(opTerm, proof, futureGraph);
						}
						return true;
					}
					else if (count == 1) {
						if (futureGraph != null) {
							if (C.getBit(0)) {
								// urem X 1 = 0
								Node result = node(new ConstantValueLLVMLabel(IntegerValue.getNullValue(C.getType())));
								result.finish(opTerm, proof, futureGraph);
							} else {
								// urem X (2^n) = and X (2^n-1)
								long[] words = C.getAsLongArray();
								words[last>>6] &= ~(1L<<(last&63));
								last--;
								while (last >= 0) {
									words[last>>6] |= (1L<<(last&63));
									last--;
								}
								IntegerValue newC = IntegerValue.get(C.getWidth(), words); 
								Node result = node(
										new BinopLLVMLabel(Binop.And),
										steal(opTerm, 0),
										conc(node(new ConstantValueLLVMLabel(newC))));
								result.finish(opTerm, proof, futureGraph);
							}
						}
						return true;
					}
				}
				break;
				
			case And:
				if (constantLabel.isConstantValue() && constantLabel.getConstantValueSelf().getValue().isInteger()) {
					IntegerValue C = constantLabel.getConstantValueSelf().getValue().getIntegerSelf();				
					if (C.equalsInt(-1)) {
						// and X -1 = X
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
						return true;
					} else if (C.equalsInt(0)) {
						// and X 0 = 0
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
						return true;
					} else {
						// check for (2^n-1)
						boolean twoNMinus1 = C.getBit(0);
						int bottomZero = -1;
						if (twoNMinus1) {
							int index = 1;
							while (index < C.getWidth() && C.getBit(index))
								index++;
							bottomZero = index;
							for (; index < C.getWidth(); index++) {
								if (C.getBit(index)) {
									twoNMinus1 = false;
									break;
								}
							}
						}
						if (twoNMinus1) {
							// and X (2^n-1) = urem X (2^n)
							if (futureGraph != null) {
								BitSet bits = new BitSet(C.getWidth());
								bits.set(bottomZero);
								IntegerValue twoN = new IntegerValue(C.getWidth(), bits);
								Node result = node(
										new BinopLLVMLabel(Binop.URem),
										steal(opTerm,0),
										conc(node(new ConstantValueLLVMLabel(twoN))));
								result.finish(opTerm,proof,futureGraph);
							}
							return true;
						}
					}
				} else if (constantLabel.isConstantValue() && constantLabel.getConstantValueSelf().getValue().isUndef()) {
					// and X undef = undef
					if (futureGraph != null)
						getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
					return true;
				}
				break;
				
			case Or:
				if (constantLabel.isConstantValue()) {
					if (constantLabel.getConstantValueSelf().getValue().isInteger()) {
						IntegerValue C = constantLabel.getConstantValueSelf().getValue().getIntegerSelf();				
						if (C.equalsInt(-1)) {
							// or X -1 = -1
							if (futureGraph != null)
								getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
							return true;
						}
					} 
					else if (constantLabel.getConstantValueSelf().getValue().isUndef()) {
						// or X undef = -1
						if (futureGraph != null) {
							IntegerType type = constantLabel.getConstantValueSelf().getValue().getType().getIntegerSelf();
							long[] words = new long[(type.getWidth()>>6) + 1];
							for (int i = 0; i < words.length; i++) 
								words[i] = -1L;
							Node result = node(new ConstantValueLLVMLabel(IntegerValue.get(type.getWidth(), words)));
							result.finish(opTerm, proof, futureGraph);		
						}
						return true;
					}
				}
				break;

			case Shl:
				if (constantLabel.isConstantValue()) {
					Value C = constantLabel.getConstantValueSelf().getValue();
					if (C.isInteger() && C.getIntegerSelf().isZero()) {
						// shl X 0 = X
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
						return true;
					} 
					else if (C.isUndef()) {
						// shl X undef = 0 (of whatever type)
						if (futureGraph != null) {
							Value zero = Value.getNullValue(C.getType());
							Node result = node(new ConstantValueLLVMLabel(zero));
							result.finish(opTerm, proof, futureGraph);
						}
						return true;
					}
				}
				break;
				
			case LShr:
				if (constantLabel.isConstantValue()) {
					Value C = constantLabel.getConstantValueSelf().getValue();
					if (C.isInteger()) {
						IntegerValue CI = C.getIntegerSelf();
						if (CI.isZero()) {
							// lshr X 0 = X
							if (futureGraph != null)
								getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
							return true;
						} else if (CI.getWidth() <= 64 && CI.getLongBits() < CI.getWidth()) {
							// lshr X C = udiv X (2^C)
							if (futureGraph != null) {
								long shiftAmount = CI.getLongBits();
								BitSet bits = new BitSet(CI.getWidth());
								bits.set((int)shiftAmount);
								IntegerValue twoToTheC = new IntegerValue(CI.getWidth(), bits);
								Node result = node(
										new BinopLLVMLabel(Binop.UDiv),
										steal(opTerm,0),
										conc(node(new ConstantValueLLVMLabel(twoToTheC))));
								result.finish(opTerm, proof, futureGraph);
							}
							return true;
						}
					}
					else if (C.isUndef()) {
						// lshr X undef = 0
						if (futureGraph != null) {  
							Value zero = Value.getNullValue(C.getType());
							Node result = node(new ConstantValueLLVMLabel(zero));
							result.finish(opTerm, proof, futureGraph);
						}
						return true;
					}
				}
				break;
				
			case AShr:
				if (constantLabel.isConstantValue()) {
					Value C = constantLabel.getConstantValueSelf().getValue();
					if (C.isInteger() && C.getIntegerSelf().isZero()) {
						// ashr X 0 = X
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
						return true;
					}
					else if (C.isUndef()){
						// ashr X undef = X
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
						return true;
					}
				}
				break;
				
			case Xor:
				if (constantLabel.isConstantValue()) {
					if (constantLabel.getConstantValueSelf().getValue().isUndef()) {
						// xor X undef = undef
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
						return true;
					} else if (constantLabel.getConstantValueSelf().getValue().isInteger()) {
						IntegerValue C = constantLabel.getConstantValueSelf().getValue().getIntegerSelf();
						if (C.isNegativeOne()) {
							// xor X -1 = sub -1 X
							if (futureGraph != null) {
								Node result = node(
										new BinopLLVMLabel(Binop.Sub),
										steal(opTerm,1),
										steal(opTerm,0));
								result.finish(opTerm, proof, futureGraph);
							}
							return true;
						} else if (C.isZero()) {
							// xor X 0 = X
							if (futureGraph != null)
								getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
							return true;
						} else if (C.getBit(C.getWidth()-1)) {
							// check for 10000...0 (signbit only)
							boolean allZeros = true;
							for (int i = 0; i < C.getWidth()-1; i++) {
								if (C.getBit(i)) {
									allZeros = false;
									break;
								}
							}
							
							if (allZeros) {
								// xor X (signbit) = add X (signbit)
								if (futureGraph != null) {
									Node result = node(
											new BinopLLVMLabel(Binop.Add),
											steal(opTerm,0),
											steal(opTerm,1));
									result.finish(opTerm, proof, futureGraph);
								}
								return true;
							}
						}
					}
				}
				break;
			}
		}
		
		return false;
	}
	

	
	// C op X
	private boolean simplifyBinopLeft(
			Bundle bundle,
			FutureExpressionGraph<FlowValue<LLVMParameter, LLVMLabel>, CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> futureGraph) {
		final CPEGTerm<LLVMLabel, LLVMParameter> opTerm = bundle.getTerm("op"); 
		final CPEGTerm<LLVMLabel, LLVMParameter> constantTerm = bundle.getTerm("C");
		final TermOrTermChild<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel, LLVMParameter>> xRep =
			bundle.getRep("X"); 
		
		final LLVMLabel opLabel = opTerm.getOp().getDomain();
		if (!opTerm.getOp().isDomain())
			return false;

		final LLVMLabel constantLabel;
		if (constantTerm.getOp().isDomain())
			constantLabel = constantTerm.getOp().getDomain();
		else if (constantTerm.getOp().isBasicOp())
			constantLabel = getAmbassador().getBasicOp(constantTerm.getOp().getBasicOp());
		else
			return false;
		
		final Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
		if (enableProofs) addConstantProperties(proof, constantTerm);
		
		if (opLabel.isSimple()) {
			switch (opLabel.getSimpleSelf().getOperator()) {
			case EXTRACTELEMENT:
				if (constantLabel.isConstantValue()) {
					if (constantLabel.getConstantValueSelf().getValue().isUndef()) {
						// extractelement undef INDEX = undef
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
						return true;
					}
					else if (constantLabel.getConstantValueSelf().getValue().isConstantVector()) {
						ConstantVectorValue C = constantLabel.getConstantValueSelf().getValue().getConstantVectorSelf();
						Value elt = C.getElement(0);
						boolean different = false;
						for (int i = 1; i < C.getNumElements().signedValue(); i++) {
							if (!C.getElement(i).equalsValue(elt)) {
								different = true;
								break;
							}
						}
						if (!different) {
							// extractelement <c,c,c,c,c,...> INDEX = c
							if (futureGraph != null) {
								Node result = node(new ConstantValueLLVMLabel(elt));
								result.finish(opTerm, proof, futureGraph);
							}
							return true;
						}
					}
				} 
				 
				break;
			}
		} 
		else if (opLabel.isBinop()) {
			switch (opLabel.getBinopSelf().getOperator()) {
			case Sub:
				if (constantLabel.isConstantValue() && constantLabel.getConstantValueSelf().getValue().isInteger()) {
					IntegerValue C = constantLabel.getConstantValueSelf().getValue().getIntegerSelf();
					if (C.isNegativeOne()) {
						// sub -1 X = xor -1 X
						if (futureGraph != null) {
							Node result = node(
									new BinopLLVMLabel(Binop.Xor),
									steal(opTerm,0),
									steal(opTerm,1));
							result.finish(opTerm, proof, futureGraph);
						}
						return true;
					} else if (C.isZero()) {
						// sub 0 X = mul X -1
						// sub 0 X = sdiv X -1
						if (futureGraph != null) {
							IntegerValue minus_one = C.negate();
							// sub 0 X = mul X -1
							Node result = node(
									new BinopLLVMLabel(Binop.Mul),
									steal(opTerm,1),
									conc(node(new ConstantValueLLVMLabel(minus_one))));
							result.finish(opTerm, proof, futureGraph);

							
							/* TODO this pattern is equivalent to 2 things!
							// sub 0 X = sdiv X -1
							result = futureGraph.getExpression(
									getDomain(new BinopLLVMLabel(Binop.SDiv)),
									futureGraph.getVertex(xRep.getRepresentative()),
									futureGraph.getExpression(
											getDomain(new ConstantValueLLVMLabel(minus_one))));
							result.setValue(opTerm);
							getEngine().getEGraph().addExpressions(futureGraph);
							*/
						}
						return true;
					}
				}
				break;
				
			case URem:
			case SRem:
				if (constantLabel.isConstantValue()) {
					if (constantLabel.getConstantValueSelf().getValue().isUndef()) {
						// {u,s}rem undef X = 0
						if (futureGraph != null) {
							IntegerType type = constantLabel.getConstantValueSelf().getValue().getType().getIntegerSelf();
							Node result = node(new ConstantValueLLVMLabel(IntegerValue.getNullValue(type)));
							result.finish(opTerm, proof, futureGraph);
						}
						return true;
					} 
					else if (constantLabel.getConstantValueSelf().getValue().isInteger()) {
						IntegerValue C = constantLabel.getConstantValueSelf().getValue().getIntegerSelf();
						if (C.isZero()) {
							// {u,s}rem 0 X = 0
							if (futureGraph != null)
								getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
							return true;
						}
					}
				}
				break;
				
			case UDiv:
			case SDiv:
				if (constantLabel.isConstantValue() && constantLabel.getConstantValueSelf().getValue().isInteger()) {
					IntegerValue C = constantLabel.getConstantValueSelf().getValue().getIntegerSelf();
					if (C.isZero()) {
						// {u,s}div 0 X = 0
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
						return true;
					}
				}
				break;
				
			case Shl:
				if (constantLabel.isConstantValue()) {
					Value C = constantLabel.getConstantValueSelf().getValue();
					if (C.isInteger() && C.getIntegerSelf().isZero()) {
						// shl 0 X = 0
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
						return true;
					}
					else if (C.isUndef()) {
						// shl undef X = 0
						if (futureGraph != null) {
							Value zero = Value.getNullValue(C.getType());
							Node result = node(new ConstantValueLLVMLabel(zero));
							result.finish(opTerm, proof, futureGraph);
						}
						return true;
					}
				}
				break;
				
			case LShr:
				if (constantLabel.isConstantValue()) {
					Value C = constantLabel.getConstantValueSelf().getValue();
					if (C.isInteger() && C.getIntegerSelf().isZero()) {
						// lshr 0 X = 0
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
						return true;
					}
					else if (C.isUndef()) {
						// lshr undef X = 0
						if (futureGraph != null) {
							Value zero = Value.getNullValue(C.getType());
							Node result = node(new ConstantValueLLVMLabel(zero));
							result.finish(opTerm, proof, futureGraph);
						}
						return true;
					}
				}
				break;
				
			case AShr:
				if (constantLabel.isConstantValue()) {
					Value C = constantLabel.getConstantValueSelf().getValue();
					if (C.isInteger() && C.getIntegerSelf().isZero()) {
						// ashr 0 X = 0
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
						return true;
					}
					else if (C.isInteger() && C.getIntegerSelf().equalsInt(-1)) {
						// ashr -1 X = -1
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
						return true;
					}
					else if (C.isUndef()) {
						// ashr undef X = undef
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
						return true;
					}
				}
				break;
			}
		}
		
		return false;
	}
}
