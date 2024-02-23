package peggy.analysis.llvm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import llvm.instructions.IntegerComparisonPredicate;
import peggy.analysis.Analysis;
import peggy.represent.llvm.CmpLLVMLabel;
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
 * This analysis has axioms about alias analysis and load/store operators, 
 * and relies on the statically-computed alias analysis.
 */
public abstract class LoadStoreAnalysis extends Analysis<LLVMLabel,LLVMParameter> {
	public LoadStoreAnalysis(
			Network network,
			CPeggyAxiomEngine<LLVMLabel, LLVMParameter> engine) {
		super(network, engine);
	}
	
	protected abstract Collection<FunctionModifies> getFunctionModifies();
	protected abstract boolean doesNotAlias(
			CPEGValue<LLVMLabel,LLVMParameter> left,
			CPEGValue<LLVMLabel,LLVMParameter> right);
	protected abstract boolean isStackPointer(
			CPEGValue<LLVMLabel,LLVMParameter> value);
	protected abstract boolean isNonStackPointer(
			CPEGValue<LLVMLabel,LLVMParameter> value);
	
	public void addAll() {
		addLoadStackPointerSkipsNonstackCall(LLVMOperator.CALL);
		addLoadStackPointerSkipsNonstackCall(LLVMOperator.TAILCALL);
		addLoadStackPointerSkipsNonstackCall(LLVMOperator.INVOKE);
		
		addLoadSkipsNonAliasingAlloca();
		
		addLoadSkipsNonAliasingStore(LLVMOperator.STORE);
		addLoadSkipsNonAliasingStore(LLVMOperator.VOLATILE_STORE);

		addNonstackDestroysStoreStackPointer();

		addNonstackSkipStoreNonStackPointer(LLVMOperator.STORE);
		addNonstackSkipStoreNonStackPointer(LLVMOperator.VOLATILE_STORE);
		
		addNonAliasingStoresCanSwap();
		
		addDoesNotAliasImpliesNotEQ();
		addDoesNotAliasImpliesNE();
		
		for (FunctionModifies fm : getFunctionModifies()) {
			addStackLoadSkipsModifyingFunction(LLVMOperator.CALL, fm);
			addStackLoadSkipsModifyingFunction(LLVMOperator.TAILCALL, fm);
			addStackLoadSkipsModifyingFunction(LLVMOperator.INVOKE, fm);
			
			addModifyingFunctionMakesNonstackCall(LLVMOperator.CALL, fm);
			addModifyingFunctionMakesNonstackCall(LLVMOperator.TAILCALL, fm);
			addModifyingFunctionMakesNonstackCall(LLVMOperator.INVOKE, fm);
		}
	}
	
	
	
	/**
	 * I:icmp_ne(A,B)
	 * {doesNotAlias(A,B)}
	 * ==>
	 * I = true
	 */
	private void addDoesNotAliasImpliesNE() {
		final String name = "If A and B do not alias, icmp_ne(A,B) = true";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> icmp =
			helper.get("icmp", new CmpLLVMLabel(IntegerComparisonPredicate.ICMP_NE), 
					helper.getVariable("A"),
					helper.getVariable("B"));
		helper.mustExist(icmp);

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
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				getEngine().getEGraph().makeEqual(
						getEngine().getEGraph().getTrue(),
						bundle.getTerm("icmp"),
						proof);
				getEngine().getEGraph().processEqualities();
				return "";
			}
			protected boolean matches(Bundle bundle) {
				return doesNotAlias(
						bundle.getRep("A").getValue(),
						bundle.getRep("B").getValue());
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);		
	}
	
	
	
	/**
	 * I:icmp_eq(A,B)
	 * {doesNotAlias(A,B)}
	 * ==>
	 * I = false
	 */
	private void addDoesNotAliasImpliesNotEQ() {
		final String name = "If A and B do not alias, icmp_eq(A,B) = false";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> icmp =
			helper.get("icmp", new CmpLLVMLabel(IntegerComparisonPredicate.ICMP_EQ), 
					helper.getVariable("A"),
					helper.getVariable("B"));
		helper.mustExist(icmp);

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
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				getEngine().getEGraph().makeEqual(
						getEngine().getEGraph().getFalse(),
						bundle.getTerm("icmp"),
						proof);
				getEngine().getEGraph().processEqualities();
				return "";
			}
			protected boolean matches(Bundle bundle) {
				return doesNotAlias(
						bundle.getRep("A").getValue(),
						bundle.getRep("B").getValue());
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);		
	}
	
	
	/**
	 * C:call(*,modifyingfunc,*,params(P1,...,PN),*)
	 * for all i in mods(func): !{stackPointer(Pi)}!
	 * ==>
	 * {nonstackCall(C)}
	 */
	protected void addModifyingFunctionMakesNonstackCall(
			LLVMOperator callop,
			final FunctionModifies fm) {
		final String name = "Modifying function with all nonstackpointers is a nonstack call";

		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel,Integer>(name, getNetwork(), getAmbassador()));
		List<PeggyVertex<LLVMLabel,Integer>> Pi = 
			new ArrayList<PeggyVertex<LLVMLabel,Integer>>(fm.getArgumentCount());
		for (int i = 0; i < fm.getArgumentCount(); i++)
			Pi.add(helper.getVariable("P" + i)); 
		
		PeggyVertex<LLVMLabel,Integer> call = 
			helper.get("call",
					SimpleLLVMLabel.get(callop),
					helper.getVariable(),
					helper.get("func", null),
					helper.getVariable(),
					helper.get("params", SimpleLLVMLabel.get(LLVMOperator.PARAMS), Pi));
//					helper.getVariable());
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
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs) addConstantProperties(proof, bundle.getTerm("func"));
				
				Node result = node(
						new StringAnnotationLLVMLabel("nonstackCall"),
						concOld(bundle.getTerm("call")));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);

				return bundle.getTerm("func").getOp().getDomain().toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel, LLVMParameter> func = bundle.getTerm("func");
				if (func.getOp().isDomain() && func.getOp().getDomain().isFunction()) {
					final FunctionLLVMLabel label = func.getOp().getDomain().getFunctionSelf();
					final FunctionModifies fm2 = 
						new FunctionModifies(label, fm.getArgumentCount(), fm.modifies());
					if (getFunctionModifies().contains(fm2)) {
						for (int i : fm.modifies()) {
							if (!isNonStackPointer(bundle.getRep("P" + i).getValue()))
								return false;
						}
						return true;
					}
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	
	/**
	 * C:call(S,modifyingfunc,*,params(P1,...,PN),*)
	 * V:rho_value(load(rho_sigma(C),P,A))
	 * {stackPointer(P)}
	 * for all mods i: {doesNotAlias(Pi, P)}
	 * ==>
	 * V = rho_value(load(S,P,A))
	 */
	protected void addStackLoadSkipsModifyingFunction(
			LLVMOperator operator,
			final FunctionModifies fm) {
		final String name = "Non-aliasing stack load may skip modifying function call";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel,Integer>(name, getNetwork(), getAmbassador()));
		List<PeggyVertex<LLVMLabel,Integer>> Pi = 
			new ArrayList<PeggyVertex<LLVMLabel,Integer>>(fm.getArgumentCount());
		for (int i = 0; i < fm.getArgumentCount(); i++)
			Pi.add(helper.getVariable("P" + i)); 
		
		PeggyVertex<LLVMLabel,Integer> call = 
			helper.get("call",
					SimpleLLVMLabel.get(operator),
					helper.getVariable("SIGMA"),
					helper.get("func", null),
					helper.getVariable(),
					helper.get("params", SimpleLLVMLabel.get(LLVMOperator.PARAMS), Pi));
//					helper.getVariable());

		PeggyVertex<LLVMLabel,Integer> P = helper.getVariable("P");
		PeggyVertex<LLVMLabel,Integer> rho_value = 
			helper.get("rho_value",
					SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE),
					helper.get("load",
							SimpleLLVMLabel.get(LLVMOperator.LOAD),
							helper.get(SimpleLLVMLabel.get(LLVMOperator.RHO_SIGMA), call),
							P,
							helper.getVariable("A")));
		
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
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs) addConstantProperties(proof, bundle.getTerm("func"));
				
				Node result = node(
						SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE),
						conc(node(
								SimpleLLVMLabel.get(LLVMOperator.LOAD),
								steal(bundle.getTerm("call"), 0),
								steal(bundle.getTerm("load"), 1),
								steal(bundle.getTerm("load"), 2))));
						
				result.finish(bundle.getTerm("rho_value"), proof, futureGraph);

				return bundle.getTerm("func").getOp().getDomain().toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel, LLVMParameter> func = bundle.getTerm("func");
				if (func.getOp().isDomain() && 
					func.getOp().getDomain().isFunction()) {
					final FunctionLLVMLabel label = 
						func.getOp().getDomain().getFunctionSelf();
					final FunctionModifies fm2 = 
						new FunctionModifies(label, fm.getArgumentCount(), fm.modifies());
				 	if (getFunctionModifies().contains(fm2) && 
				 		isStackPointer(bundle.getRep("P").getValue())) {
						for (int i : fm.modifies()) {
							if (!doesNotAlias(
									bundle.getRep("P").getValue(),
									bundle.getRep("P" + i).getValue()))
								return false;
						}
						return true;
					}
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	/**
	 * store(
	 *    store(SIGMA,PTR1,V1,A1),
	 *    PTR2,
	 *    V2,
	 *    A2)
	 * {doesNotAlias(PTR1,PTR2)}
	 * ==>
	 * store(store(SIGMA,PTR2,V2,A2),PTR1,V1,A2)
	 */
	private void addNonAliasingStoresCanSwap() {
		final String name = "Non-aliasing stores can swap";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> store =
			helper.get("topstore", SimpleLLVMLabel.get(LLVMOperator.STORE), 
					helper.get("bottomstore", SimpleLLVMLabel.get(LLVMOperator.STORE),
							helper.getVariable("SIGMA"),
							helper.getVariable("PTR1"),
							helper.getVariable("V1"),
							helper.getVariable("A1")),
					helper.getVariable("PTR2"),
					helper.getVariable("V2"),
					helper.getVariable("A2"));
		helper.mustExist(store);

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
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				
				Node result = node(
								SimpleLLVMLabel.get(LLVMOperator.STORE),
								conc(node(
										SimpleLLVMLabel.get(LLVMOperator.STORE),
										steal(bundle.getTerm("bottomstore"), 0),
										steal(bundle.getTerm("topstore"), 1),
										steal(bundle.getTerm("topstore"), 2),
										steal(bundle.getTerm("topstore"), 3))),
								steal(bundle.getTerm("bottomstore"), 1),
								steal(bundle.getTerm("bottomstore"), 2),
								steal(bundle.getTerm("bottomstore"), 3));
				result.finish(bundle.getTerm("topstore"), proof, futureGraph);
				
				return "";
			}
			protected boolean matches(Bundle bundle) {
				return doesNotAlias(
						bundle.getRep("PTR1").getValue(),
						bundle.getRep("PTR2").getValue());
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	private void addLoadStackPointerSkipsNonstackCall(LLVMOperator callop) {
		final String name = "Load of stackPointer can skip nonstack-" + callop;
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> call =
			helper.get("call", SimpleLLVMLabel.get(callop), 
					helper.getVariable("SIGMA"),
					helper.get("function", null),
					helper.getVariable(),
					helper.getVariable());
		PeggyVertex<LLVMLabel,Integer> rho_value =
			helper.get("rho_value", SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE),
				helper.get("load", SimpleLLVMLabel.get(LLVMOperator.LOAD),
						helper.get(SimpleLLVMLabel.get(LLVMOperator.RHO_SIGMA), call),
						helper.getVariable("P"),
						helper.getVariable("A")));

		helper.mustExist(rho_value);
		helper.mustBeTrue(
				helper.get(new StringAnnotationLLVMLabel("nonstackCall"), call));

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
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				
				Node result = node(
						SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE),
						conc(node(
								SimpleLLVMLabel.get(LLVMOperator.LOAD),
								steal(bundle.getTerm("call"), 0),
								steal(bundle.getTerm("load"), 1),
								steal(bundle.getTerm("load"), 2))));
				result.finish(bundle.getTerm("rho_value"), proof, futureGraph);
				
				return bundle.getTerm("function").getOp().getDomain().toString();
			}
			protected boolean matches(Bundle bundle) {
				return isStackPointer(bundle.getRep("P").getValue());
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	
	private void addLoadSkipsNonAliasingAlloca() {
		final String name = "Load can skip non-aliasing alloca";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> alloca =
			helper.get("alloca", SimpleLLVMLabel.get(LLVMOperator.ALLOCA), 
					helper.getVariable("SIGMA"),
					helper.getVariable(),
					helper.getVariable(),
					helper.getVariable());
		PeggyVertex<LLVMLabel,Integer> rho_value =
			helper.get("rho_value", SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE),
				helper.get("load", SimpleLLVMLabel.get(LLVMOperator.LOAD),
						helper.get(SimpleLLVMLabel.get(LLVMOperator.RHO_SIGMA), alloca),
						helper.getVariable("P"),
						helper.getVariable("A")));

		helper.mustExist(rho_value);
		helper.mustExist(
				helper.get("rho_value_alloca", SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE),
						alloca));

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
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				
				Node result = node(
						SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE),
						conc(node(
								SimpleLLVMLabel.get(LLVMOperator.LOAD),
								steal(bundle.getTerm("alloca"), 0),
								steal(bundle.getTerm("load"), 1),
								steal(bundle.getTerm("load"), 2))));
				result.finish(bundle.getTerm("rho_value"), proof, futureGraph);
				
				return "";
			}
			protected boolean matches(Bundle bundle) {
				return doesNotAlias(
						bundle.getRep("P").getValue(),
						bundle.getTerm("rho_value_alloca").getValue());
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	private void addLoadSkipsNonAliasingStore(LLVMOperator storeop) {
		final String name = "Load can skip non-aliasing store";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> store =
			helper.get("store", SimpleLLVMLabel.get(storeop), 
					helper.getVariable("SIGMA"),
					helper.getVariable("PTR1"),
					helper.getVariable(),
					helper.getVariable());
		PeggyVertex<LLVMLabel,Integer> rho_value =
			helper.get("rho_value", SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE),
				helper.get("load", SimpleLLVMLabel.get(LLVMOperator.LOAD),
						store,
						helper.getVariable("PTR2"),
						helper.getVariable("A")));

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
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				Node result = node(
						SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE),
						conc(node(
								SimpleLLVMLabel.get(LLVMOperator.LOAD),
								steal(bundle.getTerm("store"), 0),
								steal(bundle.getTerm("load"), 1),
								steal(bundle.getTerm("load"), 2))));
				result.finish(bundle.getTerm("rho_value"), proof, futureGraph);
				return "";
			}
			protected boolean matches(Bundle bundle) {
				return doesNotAlias(
						bundle.getRep("PTR1").getValue(),
						bundle.getRep("PTR2").getValue());
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}

	private void addNonstackSkipStoreNonStackPointer(LLVMOperator storeop) {
		final String name = "Nonstack skips store to nonStackPointer";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> nonstack =
			helper.get("nonstack", SimpleLLVMLabel.get(LLVMOperator.NONSTACK),
				helper.get("store", SimpleLLVMLabel.get(storeop), 
						helper.getVariable("SIGMA"),
						helper.getVariable("PTR"),
						helper.getVariable("V"),
						helper.getVariable("A")));

		helper.mustExist(nonstack);

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
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				
				Node result = node(
						SimpleLLVMLabel.get(LLVMOperator.STORE),
						conc(node(
								SimpleLLVMLabel.get(LLVMOperator.NONSTACK),
								steal(bundle.getTerm("store"), 0))),
						steal(bundle.getTerm("store"), 1),
						steal(bundle.getTerm("store"), 2),
						steal(bundle.getTerm("store"), 3));
				
				result.finish(bundle.getTerm("nonstack"), proof, futureGraph);
				
				return "";
			}
			protected boolean matches(Bundle bundle) {
				return isNonStackPointer(
						bundle.getRep("PTR").getValue());
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	
	private void addNonstackDestroysStoreStackPointer() {
		final String name = "Nonstack destroys store to stackPointer";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> nonstack =
			helper.get("nonstack", SimpleLLVMLabel.get(LLVMOperator.NONSTACK),
				helper.get("store", SimpleLLVMLabel.get(LLVMOperator.STORE), 
						helper.getVariable("SIGMA"),
						helper.getVariable("PTR"),
						helper.getVariable(),
						helper.getVariable()));

		helper.mustExist(nonstack);

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
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				
				Node result = node(
						SimpleLLVMLabel.get(LLVMOperator.NONSTACK),
						steal(bundle.getTerm("store"), 0));
				
				result.finish(bundle.getTerm("nonstack"), proof, futureGraph);
				
				return "";
			}
			protected boolean matches(Bundle bundle) {
				return isStackPointer(
						bundle.getRep("PTR").getValue());
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
}
