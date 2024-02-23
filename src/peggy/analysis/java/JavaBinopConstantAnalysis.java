package peggy.analysis.java;

import peggy.analysis.Analysis;
import peggy.represent.java.ConstantValueJavaLabel;
import peggy.represent.java.JavaLabel;
import peggy.represent.java.JavaOperator;
import peggy.represent.java.JavaParameter;
import peggy.represent.java.SimpleJavaLabel;
import soot.jimple.Constant;
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
 * This analysis has axioms that deal with binary operators and constants.
 */
public abstract class JavaBinopConstantAnalysis extends Analysis<JavaLabel,JavaParameter> {
	public JavaBinopConstantAnalysis(
			Network _network,
			CPeggyAxiomEngine<JavaLabel, JavaParameter> _engine) {
		super(_network, _engine);
	}

	public void addAll() {
		addBinopRightConstantAxiom(SimpleJavaLabel.create(JavaOperator.PLUS));
		addBinopRightConstantAxiom(SimpleJavaLabel.create(JavaOperator.MINUS));
		addBinopRightConstantAxiom(SimpleJavaLabel.create(JavaOperator.TIMES));
		// TODO div, rem
		addBinopRightConstantAxiom(SimpleJavaLabel.create(JavaOperator.BITWISE_AND));
		addBinopRightConstantAxiom(SimpleJavaLabel.create(JavaOperator.BITWISE_OR));
		addBinopRightConstantAxiom(SimpleJavaLabel.create(JavaOperator.XOR));
		addBinopRightConstantAxiom(SimpleJavaLabel.create(JavaOperator.SHIFT_LEFT));
		addBinopRightConstantAxiom(SimpleJavaLabel.create(JavaOperator.SHIFT_RIGHT));
		addBinopRightConstantAxiom(SimpleJavaLabel.create(JavaOperator.UNSIGNED_SHIFT_RIGHT));

		addBinopRightConstantAxiom(SimpleJavaLabel.create(JavaOperator.EQUAL));
		addBinopRightConstantAxiom(SimpleJavaLabel.create(JavaOperator.NOT_EQUAL));
		addBinopRightConstantAxiom(SimpleJavaLabel.create(JavaOperator.GREATER_THAN));
		addBinopRightConstantAxiom(SimpleJavaLabel.create(JavaOperator.GREATER_THAN_EQUAL));
		addBinopRightConstantAxiom(SimpleJavaLabel.create(JavaOperator.LESS_THAN));
		addBinopRightConstantAxiom(SimpleJavaLabel.create(JavaOperator.LESS_THAN_EQUAL));
		// TODO addBinopRightConstantAxiom(SimpleJavaLabel.create(JavaOperator.CMP));
		//addBinopRightConstantAxiom(SimpleJavaLabel.create(JavaOperator.CMPL));
		//addBinopRightConstantAxiom(SimpleJavaLabel.create(JavaOperator.CMPG));
		
		
		addBinopLeftConstantAxiom(SimpleJavaLabel.create(JavaOperator.MINUS));
		addBinopLeftConstantAxiom(SimpleJavaLabel.create(JavaOperator.SHIFT_LEFT));
		addBinopLeftConstantAxiom(SimpleJavaLabel.create(JavaOperator.SHIFT_RIGHT));
		addBinopLeftConstantAxiom(SimpleJavaLabel.create(JavaOperator.UNSIGNED_SHIFT_RIGHT));
		// TODO div,rem
	}
	
	private void addBinopRightConstantAxiom(JavaLabel operator) {
		// operator(X,c)
		final String name = operator + "(X,c)";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<JavaLabel, Integer>(name, getNetwork(), getAmbassador()));

		PeggyVertex<JavaLabel, Integer> X = helper.getVariable("X");
		PeggyVertex<JavaLabel, Integer> c = helper.get("C", null);
		PeggyVertex<JavaLabel, Integer> opnode = 
			helper.get("op", operator, X, c);
		helper.mustExist(opnode);

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
				simplifyBinopRight(bundle, futureGraph);
				return bundle.getTerm("C").getOp().getDomain().toString();
			}
			protected boolean matches(Bundle bundle) {
				return simplifyBinopRight(bundle, null);
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	

	private void addBinopLeftConstantAxiom(JavaLabel operator) {
		// operator(c,X)
		final String name = operator + "(c,X)";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<JavaLabel, Integer>(name, getNetwork(), getAmbassador()));

		PeggyVertex<JavaLabel, Integer> X = helper.getVariable("X");
		PeggyVertex<JavaLabel, Integer> c = helper.get("C", null);
		PeggyVertex<JavaLabel, Integer> opnode = 
			helper.get("op", operator, c, X);
		helper.mustExist(opnode);

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
				simplifyBinopLeft(bundle, futureGraph);
				return bundle.getTerm("C").getOp().getDomain().toString();
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
			FutureExpressionGraph<FlowValue<JavaParameter, JavaLabel>, CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel, JavaParameter>> futureGraph) {
		CPEGTerm<JavaLabel, JavaParameter> opTerm = bundle.getTerm("op"); 
		CPEGTerm<JavaLabel, JavaParameter> constantTerm = bundle.getTerm("C");
		TermOrTermChild<CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel, JavaParameter>> xRep =
			bundle.getRep("X"); 
		if (!(opTerm.getOp().isDomain() && constantTerm.getOp().isDomain()))
			return false;
		
		JavaLabel opLabel = opTerm.getOp().getDomain();
		JavaLabel constantLabel = constantTerm.getOp().getDomain();
		Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
		if (enableProofs) addConstantProperties(proof, constantTerm);
		
		if (opLabel.isSimple()) {
			switch (opLabel.getSimpleSelf().getOperator()) {
			case PLUS:
				if (constantLabel.isConstant()) {
					if (constantLabel.getConstantSelf().getValue() instanceof IntConstant) {
						int C = ((IntConstant)constantLabel.getConstantSelf().getValue()).value;
						if (C == 0) {
							// add A 0 = A
							if (futureGraph != null) {
								getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
							}
							return true;
						} 
						else if (C == ((-1)<<31)) {
							// add X (signbit) = xor X (signbit)
							if (futureGraph != null) {
								Node result = node(
										SimpleJavaLabel.create(JavaOperator.XOR),
										steal(opTerm,0),
										steal(opTerm,1));
								result.finish(opTerm, proof, futureGraph);
							}
							return true;
						}
					} 
					else if (constantLabel.getConstantSelf().getValue() instanceof LongConstant) {
						long C = ((LongConstant)constantLabel.getConstantSelf().getValue()).value;
						if (C == 0L) {
							// add A 0 = A
							if (futureGraph != null) {
								getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
							}
							return true;
						} 
						else if (C == ((-1L)<<63)) {
							// add X (signbit) = xor X (signbit)
							if (futureGraph != null) {
								Node result = node(
										SimpleJavaLabel.create(JavaOperator.XOR),
										steal(opTerm,0),
										steal(opTerm,1));
								result.finish(opTerm, proof, futureGraph);
							}
							return true;
						}
					}

				}
				break;
				
			case TIMES:
				if (constantLabel.isConstant()) {
					if (constantLabel.getConstantSelf().getValue() instanceof IntConstant) {
						int C = ((IntConstant)constantLabel.getConstantSelf().getValue()).value;
						if (C == 0) {
							// mul A 0 = 0
							if (futureGraph != null)
								getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
							return true;
						} else if (C == 1) {
							// mul A 1 = A
							if (futureGraph != null)
								getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
							return true;
						} else if (C == -1) {
							// mul A (-1) = sub 0 A
							if (futureGraph != null) {
								Node result = node(
										SimpleJavaLabel.create(JavaOperator.MINUS),
										conc(node(new ConstantValueJavaLabel(IntConstant.v(0)))),
										steal(opTerm,0));
								result.finish(opTerm, proof, futureGraph);
							}
							return true;
						}
					}
					else if (constantLabel.getConstantSelf().getValue() instanceof LongConstant) {
						long C = ((LongConstant)constantLabel.getConstantSelf().getValue()).value;
						if (C == 0L) {
							// mul A 0 = 0
							if (futureGraph != null)
								getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
							return true;
						} else if (C == 1L) {
							// mul A 1 = A
							if (futureGraph != null)
								getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
							return true;
						} else if (C == -1L) {
							// mul A (-1) = sub 0 A
							if (futureGraph != null) {
								Node result = node(
										SimpleJavaLabel.create(JavaOperator.MINUS),
										conc(node(new ConstantValueJavaLabel(LongConstant.v(0L)))),
										steal(opTerm,0));
								result.finish(opTerm, proof, futureGraph);
							}
							return true;
						}
					}
				}
				break;
				
			case MINUS:
				if (constantLabel.isConstant()) {
					if (constantLabel.getConstantSelf().getValue() instanceof IntConstant) {
						int C = ((IntConstant)constantLabel.getConstantSelf().getValue()).value;
						if (C == 0) {
							// sub A 0 = A
							if (futureGraph != null)
								getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
							return true;
						}
					}
					else if (constantLabel.getConstantSelf().getValue() instanceof LongConstant) {
						long C = ((LongConstant)constantLabel.getConstantSelf().getValue()).value;
						if (C == 0L) {
							// sub A 0 = A
							if (futureGraph != null)
								getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
							return true;
						}
					}
				}
				break;

				/*
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
									new BinopJavaLabel(Binop.LShr),
									steal(opTerm,0),
									conc(node(new ConstantValueJavaLabel(newC))));
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
									new BinopJavaLabel(Binop.Sub),
									conc(node(new ConstantValueJavaLabel(Value.getNullValue(C.getType())))),
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
							Node result = node(new ConstantValueJavaLabel(new UndefValue(C.getType())));
							result.finish(opTerm, proof, futureGraph);
						}
						return true;
					}
					else if (count == 1) {
						if (futureGraph != null) {
							if (C.getBit(0)) {
								// urem X 1 = 0
								Node result = node(new ConstantValueJavaLabel(IntegerValue.getNullValue(C.getType())));
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
										new BinopJavaLabel(Binop.And),
										steal(opTerm, 0),
										conc(node(new ConstantValueJavaLabel(newC))));
								result.finish(opTerm, proof, futureGraph);
							}
						}
						return true;
					}
				}
				break;
				*/
				
			case BITWISE_AND:
				if (constantLabel.isConstant()) {
					if (constantLabel.getConstantSelf().getValue() instanceof IntConstant) {
						int C = ((IntConstant)constantLabel.getConstantSelf().getValue()).value;				
						if (C == -1) {
							// and X -1 = X
							if (futureGraph != null)
								getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
							return true;
						} else {
							// check for (2^n-1)
							int bottomZero = -1;
							boolean twoNMinus1 = ((C & 1) == 0);
							if (twoNMinus1) {
								int index = 1;
								while (index < 32 && ((C & (1<<index)) != 0))
									index++;
								bottomZero = index;
								for (; index < 32; index++) {
									if ((C & (1<<index)) != 0) {
										twoNMinus1 = false;
										break;
									}
								}
							}
							if (twoNMinus1) {
								// and X (2^n-1) = urem X (2^n)
								if (futureGraph != null) {
									int twoN = 1<<bottomZero;
									Node result = node(
											SimpleJavaLabel.create(JavaOperator.MOD),
											steal(opTerm,0),
											conc(node(new ConstantValueJavaLabel(IntConstant.v(twoN)))));
									result.finish(opTerm,proof,futureGraph);
								}
								return true;
							}
						}
					}
					else if (constantLabel.getConstantSelf().getValue() instanceof LongConstant) {
						long C = ((LongConstant)constantLabel.getConstantSelf().getValue()).value;				
						if (C == -1L) {
							// and X -1 = X
							if (futureGraph != null)
								getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
							return true;
						} else {
							// check for (2^n-1)
							int bottomZero = -1;
							boolean twoNMinus1 = ((C & 1L) == 0L);
							if (twoNMinus1) {
								int index = 1;
								while (index < 64 && ((C & (1L<<index)) != 0L))
									index++;
								bottomZero = index;
								for (; index < 64; index++) {
									if ((C & (1L<<index)) != 0L) {
										twoNMinus1 = false;
										break;
									}
								}
							}
							if (twoNMinus1) {
								// and X (2^n-1) = urem X (2^n)
								if (futureGraph != null) {
									long twoN = 1L<<bottomZero;
									Node result = node(
											SimpleJavaLabel.create(JavaOperator.MOD),
											steal(opTerm,0),
											conc(node(new ConstantValueJavaLabel(LongConstant.v(twoN)))));
									result.finish(opTerm,proof,futureGraph);
								}
								return true;
							}
						}
					}
				}
				break;
				
			case BITWISE_OR:
				if (constantLabel.isConstant()) {
					if (constantLabel.getConstantSelf().getValue() instanceof IntConstant) {
						int C = ((IntConstant)constantLabel.getConstantSelf().getValue()).value;				
						if (C == -1) {
							// or X -1 = -1
							if (futureGraph != null)
								getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
							return true;
						}
					} 
					else if (constantLabel.getConstantSelf().getValue() instanceof LongConstant) {
						long C = ((LongConstant)constantLabel.getConstantSelf().getValue()).value;				
						if (C == -1L) {
							// or X -1 = -1
							if (futureGraph != null)
								getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
							return true;
						}
					} 
				}
				break;

			case SHIFT_LEFT:
				if (constantLabel.isConstant()) {
					Constant C = constantLabel.getConstantSelf().getValue();
					if (C instanceof IntConstant && ((IntConstant)C).value == 0) {
						// shl X 0 = X
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
						return true;
					} 
					else if (C instanceof LongConstant && ((LongConstant)C).value == 0L) {
						// shl X 0 = X
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
						return true;
					} 
				}
				break;
				
			case UNSIGNED_SHIFT_RIGHT:
				if (constantLabel.isConstant()) {
					Constant C = constantLabel.getConstantSelf().getValue();
					if (C instanceof IntConstant) {
						int CI = ((IntConstant)C).value;
						if (CI == 0) {
							// lshr X 0 = X
							if (futureGraph != null)
								getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
							return true;
						} 
						/*else if (CI < 32) {
							// lshr X C = udiv X (2^C)
							if (futureGraph != null) {
								long shiftAmount = CI.getLongBits();
								BitSet bits = new BitSet(CI.getWidth());
								bits.set((int)shiftAmount);
								IntegerValue twoToTheC = new IntegerValue(CI.getWidth(), bits);
								Node result = node(
										new BinopJavaLabel(Binop.UDiv),
										steal(opTerm,0),
										conc(node(new ConstantValueJavaLabel(twoToTheC))));
								result.finish(opTerm, proof, futureGraph);
							}
							return true;
						}*/
					}
					else if (C instanceof LongConstant) {
						long CI = ((LongConstant)C).value;
						if (CI == 0L) {
							// lshr X 0 = X
							if (futureGraph != null)
								getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
							return true;
						} 
					}
				}
				break;
				
			case SHIFT_RIGHT:
				if (constantLabel.isConstant()) {
					Constant C = constantLabel.getConstantSelf().getValue();
					if (C instanceof IntConstant && ((IntConstant)C).value == 0) {
						// ashr X 0 = X
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
						return true;
					}
					else if (C instanceof LongConstant && ((LongConstant)C).value == 0L) {
						// ashr X 0 = X
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
						return true;
					}
				}
				break;
				
			case XOR:
				if (constantLabel.isConstant()) {
					if (constantLabel.getConstantSelf().getValue() instanceof IntConstant) {
						int C = ((IntConstant)constantLabel.getConstantSelf().getValue()).value;
						if (C == -1) {
							// xor X -1 = sub -1 X
							if (futureGraph != null) {
								Node result = node(
										SimpleJavaLabel.create(JavaOperator.MINUS),
										steal(opTerm,1),
										steal(opTerm,0));
								result.finish(opTerm, proof, futureGraph);
							}
							return true;
						} else if (C == 0) {
							// xor X 0 = X
							if (futureGraph != null)
								getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
							return true;
						} else if (C == ((-1)<<31)) {
							// check for 10000...0 (signbit only)
							// xor X (signbit) = add X (signbit)
							if (futureGraph != null) {
								Node result = node(
										SimpleJavaLabel.create(JavaOperator.PLUS),
										steal(opTerm,0),
										steal(opTerm,1));
								result.finish(opTerm, proof, futureGraph);
							}
							return true;
						}
					}
					else if (constantLabel.getConstantSelf().getValue() instanceof LongConstant) {
						long C = ((LongConstant)constantLabel.getConstantSelf().getValue()).value;
						if (C == -1L) {
							// xor X -1 = sub -1 X
							if (futureGraph != null) {
								Node result = node(
										SimpleJavaLabel.create(JavaOperator.MINUS),
										steal(opTerm,1),
										steal(opTerm,0));
								result.finish(opTerm, proof, futureGraph);
							}
							return true;
						} else if (C == 0L) {
							// xor X 0 = X
							if (futureGraph != null)
								getEngine().getEGraph().makeEqual(xRep, opTerm, proof);
							return true;
						} else if (C == ((-1L)<<63)) {
							// check for 10000...0 (signbit only)
							// xor X (signbit) = add X (signbit)
							if (futureGraph != null) {
								Node result = node(
										SimpleJavaLabel.create(JavaOperator.PLUS),
										steal(opTerm,0),
										steal(opTerm,1));
								result.finish(opTerm, proof, futureGraph);
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
	

	
	// C op X
	private boolean simplifyBinopLeft(
			Bundle bundle,
			FutureExpressionGraph<FlowValue<JavaParameter, JavaLabel>, CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel, JavaParameter>> futureGraph) {
		CPEGTerm<JavaLabel, JavaParameter> opTerm = bundle.getTerm("op"); 
		CPEGTerm<JavaLabel, JavaParameter> constantTerm = bundle.getTerm("C");
		TermOrTermChild<CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel, JavaParameter>> xRep =
			bundle.getRep("X"); 
		if (!(opTerm.getOp().isDomain() && constantTerm.getOp().isDomain()))
			return false;
		
		JavaLabel opLabel = opTerm.getOp().getDomain();
		JavaLabel constantLabel = constantTerm.getOp().getDomain();
		Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
		if (enableProofs) addConstantProperties(proof, constantTerm);
		
		if (opLabel.isSimple()) {
			switch (opLabel.getSimpleSelf().getOperator()) {
			case MINUS:
				if (constantLabel.isConstant()) {
					if (constantLabel.getConstantSelf().getValue() instanceof IntConstant) {
						int C = ((IntConstant)constantLabel.getConstantSelf().getValue()).value;
						if (C == -1) {
							// sub -1 X = xor -1 X
							if (futureGraph != null) {
								Node result = node(
										SimpleJavaLabel.create(JavaOperator.XOR),
										steal(opTerm,0),
										steal(opTerm,1));
								result.finish(opTerm, proof, futureGraph);
							}
							return true;
						} else if (C == 0) {
							// sub 0 X = mul X -1
							// sub 0 X = sdiv X -1
							if (futureGraph != null) {
								// sub 0 X = mul X -1
								Node result = node(
										SimpleJavaLabel.create(JavaOperator.TIMES),
										steal(opTerm,1),
										conc(node(new ConstantValueJavaLabel(IntConstant.v(1)))));
								result.finish(opTerm, proof, futureGraph);


								/* TODO this pattern is equivalent to 2 things!
							// sub 0 X = sdiv X -1
							result = futureGraph.getExpression(
									getDomain(new BinopJavaLabel(Binop.SDiv)),
									futureGraph.getVertex(xRep.getRepresentative()),
									futureGraph.getExpression(
											getDomain(new ConstantValueJavaLabel(minus_one))));
							result.setValue(opTerm);
							getEngine().getEGraph().addExpressions(futureGraph);
								 */
							}
							return true;
						}
					}
					else if (constantLabel.getConstantSelf().getValue() instanceof LongConstant) {
						long C = ((LongConstant)constantLabel.getConstantSelf().getValue()).value;
						if (C == -1L) {
							// sub -1 X = xor -1 X
							if (futureGraph != null) {
								Node result = node(
										SimpleJavaLabel.create(JavaOperator.XOR),
										steal(opTerm,0),
										steal(opTerm,1));
								result.finish(opTerm, proof, futureGraph);
							}
							return true;
						} else if (C == 0L) {
							// sub 0 X = mul X -1
							// sub 0 X = sdiv X -1
							if (futureGraph != null) {
								// sub 0 X = mul X -1
								Node result = node(
										SimpleJavaLabel.create(JavaOperator.TIMES),
										steal(opTerm,1),
										conc(node(new ConstantValueJavaLabel(IntConstant.v(1)))));
								result.finish(opTerm, proof, futureGraph);
							}
							return true;
						}
					}
				}
				break;

				/*
			case URem:
			case SRem:
				if (constantLabel.isConstantValue()) {
					if (constantLabel.getConstantValueSelf().getValue().isUndef()) {
						// {u,s}rem undef X = 0
						if (futureGraph != null) {
							IntegerType type = constantLabel.getConstantValueSelf().getValue().getType().getIntegerSelf();
							Node result = node(new ConstantValueJavaLabel(IntegerValue.getNullValue(type)));
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
				*/
				
			case SHIFT_LEFT:
				if (constantLabel.isConstant()) {
					Constant C = constantLabel.getConstantSelf().getValue();
					if (C instanceof IntConstant && ((IntConstant)C).value == 0) {
						// shl 0 X = 0
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
						return true;
					}
					else if (C instanceof LongConstant && ((LongConstant)C).value == 0L) {
						// shl 0 X = 0
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
						return true;
					}
				}
				break;
				
			case UNSIGNED_SHIFT_RIGHT:
				if (constantLabel.isConstant()) {
					Constant C = constantLabel.getConstantSelf().getValue();
					if (C instanceof IntConstant && ((IntConstant)C).value == 0) {
						// lshr 0 X = 0
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
						return true;
					}
					else if (C instanceof LongConstant && ((LongConstant)C).value == 0L) {
						// lshr 0 X = 0
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
						return true;
					}
				}
				break;
				
			case SHIFT_RIGHT:
				if (constantLabel.isConstant()) {
					Constant C = constantLabel.getConstantSelf().getValue();
					if (C instanceof IntConstant && ((IntConstant)C).value == 0) {
						// ashr 0 X = 0
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
						return true;
					}
					else if (C instanceof IntConstant && ((IntConstant)C).value == -1) {
						// ashr -1 X = -1
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
						return true;
					}
					else if (C instanceof LongConstant && ((LongConstant)C).value == 0L) {
						// ashr 0 X = 0
						if (futureGraph != null)
							getEngine().getEGraph().makeEqual(constantTerm, opTerm, proof);
						return true;
					}
					else if (C instanceof LongConstant && ((LongConstant)C).value == -1L) {
						// ashr -1 X = -1
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
