package peggy.analysis.llvm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import llvm.bitcode.ReferenceResolver;
import llvm.instructions.Cast;
import llvm.types.FunctionType;
import llvm.types.Type;
import llvm.values.FunctionValue;
import llvm.values.IntegerValue;
import llvm.values.ParameterAttributeMap;
import llvm.values.ParameterAttributes;
import llvm.values.Value;
import peggy.analysis.Analysis;
import peggy.analysis.ChildSource;
import peggy.represent.llvm.CastLLVMLabel;
import peggy.represent.llvm.FunctionLLVMLabel;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMOperator;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.SimpleLLVMLabel;
import peggy.represent.llvm.StringAnnotationLLVMLabel;
import util.graph.CRecursiveExpressionGraph.Vertex;
import util.pair.Pair;
import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.event.ProofEvent;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.proof.OpIs;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyVertex;

/**
 * This analysis has axioms about alias analysis and the NONSTACK operator.
 */
public abstract class NonstackFunctionAnalysis extends Analysis<LLVMLabel,LLVMParameter> {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("NonstackFunctionAnalysis: " + message);
	}
	
	/////////////////////////////////////////////////////
	
	private static final LLVMLabel derivedPointer = 
		new StringAnnotationLLVMLabel("derivedPointer");
	private static final LLVMLabel stackPointer = 
		new StringAnnotationLLVMLabel("stackPointer");
	private static final LLVMLabel doesNotAlias = 
		new StringAnnotationLLVMLabel("doesNotAlias");
	private static final LLVMLabel nonstackCall = 
		new StringAnnotationLLVMLabel("nonstackCall");
	private static final LLVMLabel allNonstack = 
		new StringAnnotationLLVMLabel("allNonstack");
	private static final LLVMLabel sigmaInvariant = 
		new StringAnnotationLLVMLabel("sigmaInvariant");
	
	protected final Set<FunctionLLVMLabel> functions;
	protected final Set<FunctionLLVMLabel> modifyingFunctions;
	protected final Set<Pair<Integer,Set<Integer>>> modifyingSignatures;
	protected final Set<FunctionLLVMLabel> sigmaInvariants;
	protected final ReferenceResolver resolver;
	protected boolean madeListeners = false;
	private final boolean disableStackAndAlias;

	public NonstackFunctionAnalysis(
			Network _network,
			Collection<FunctionLLVMLabel> sigmas,
			CPeggyAxiomEngine<LLVMLabel, LLVMParameter> _engine,
			ReferenceResolver _resolver,
			boolean disableStackAlias) {
		super(_network, _engine);
		this.resolver = _resolver;
		this.disableStackAndAlias = disableStackAlias;
		this.sigmaInvariants = new HashSet<FunctionLLVMLabel>(sigmas);
		this.functions = new HashSet<FunctionLLVMLabel>();
		this.modifyingFunctions = new HashSet<FunctionLLVMLabel>();
		this.modifyingSignatures = new HashSet<Pair<Integer,Set<Integer>>>(); 
	}
	
	public void addAll(
			Collection<FunctionLLVMLabel> newfunctions,
			Collection<FunctionModifies> functionMods) {
		if (!madeListeners) {
			addNonstackFunctionMakesNonstackCall(LLVMOperator.CALL);
			addNonstackFunctionMakesNonstackCall(LLVMOperator.TAILCALL);
			addNonstackFunctionMakesNonstackCall(LLVMOperator.INVOKE);
			
			addBitcastNonstackFunctionMakesNonstackCall(LLVMOperator.CALL);
			addBitcastNonstackFunctionMakesNonstackCall(LLVMOperator.TAILCALL);
			addBitcastNonstackFunctionMakesNonstackCall(LLVMOperator.INVOKE);

			if (!disableStackAndAlias) {
				addBitcastPreservesStackpointer();
				addNullIsNonstackpointer();
				addLoadPointerAllocaDoesNotAliasAlloca(); // THIS RULE IS VERY QUESTIONABLE
//				addGEPNonzeroDoesNotAlias();
				
				addCallMakeAllNonstack(LLVMOperator.CALL);
				addCallMakeAllNonstack(LLVMOperator.TAILCALL);
				addCallMakeAllNonstack(LLVMOperator.INVOKE);
			}

			addOppositeGEPsCancel();
			addGEPDifferentOffsetsDNA1();
			addGEPDifferentOffsetsDNA2();
			addGEPDifferentOffsetsBitcastDNA2();
			
			addCallNoPointersIsNonstackCall(LLVMOperator.CALL);
			addCallNoPointersIsNonstackCall(LLVMOperator.TAILCALL);
			addCallNoPointersIsNonstackCall(LLVMOperator.INVOKE);
			
			addReadonlyReadnoneCallIsSigmaInvariant(LLVMOperator.CALL);			
			addReadonlyReadnoneCallIsSigmaInvariant(LLVMOperator.TAILCALL);			
			addReadonlyReadnoneCallIsSigmaInvariant(LLVMOperator.INVOKE);
			
			addUnaryDistributesThroughPhi();
			
			if (!disableStackAndAlias) {
				addAllocDoesNotAliasNull(LLVMOperator.ALLOCA);
				addAllocDoesNotAliasNull(LLVMOperator.MALLOC);
			}
			
			{// derived pointer rules
				addBitcastAllocIsDerivedPointer(LLVMOperator.ALLOCA);
				addBitcastAllocIsDerivedPointer(LLVMOperator.MALLOC);
				
				addBitcastGlobalIsDerived();
				addGEPGlobalIsDerived();
				
	//			addGEPAllocIsDerivedPointer(LLVMOperator.ALLOCA); // in file
	//			addGEPAllocIsDerivedPointer(LLVMOperator.MALLOC);
				
				addBitcastDerivedIsDerived();
	//			addGEPDerivedIsDerived(); // in file
				
	//			addDNADerivedImpliesDNA(); // in file
			}
				
			madeListeners = true;
		}
		this.functions.addAll(newfunctions);
		
		for (FunctionModifies mods : functionMods) {
			Pair<Integer,Set<Integer>> pair = 
				new Pair<Integer,Set<Integer>>(mods.getArgumentCount(), mods.modifies());
			if (!modifyingSignatures.contains(pair)) {
				if (!disableStackAndAlias) {
					addModifyingFunctionMakesNonstackCall(pair);
					addStackLoadSkipsModifyingFunction(
							LLVMOperator.CALL,
							pair);
					addStackLoadSkipsModifyingFunction(
							LLVMOperator.TAILCALL,
							pair);
					addStackLoadSkipsModifyingFunction(
							LLVMOperator.INVOKE,
							pair);
				}
				modifyingSignatures.add(pair);
			}
			this.modifyingFunctions.add(mods.getFunction());
		}
		
		// do sigma invariants
		addSigmaInvariantAxioms();
	}

	/**
	 * G:GEP(P:global,*,*)
	 * ==>
	 * derivedPointer(G,P)
	 */
	public void addGEPGlobalIsDerived() {
		final String name = "GEP of global is derived";
		
		WildcardAxiomizerHelper helper = new WildcardAxiomizerHelper(
				new peggy.analysis.WildcardPeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));

		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> gep =  
			helper.get("gep", SimpleLLVMLabel.get(LLVMOperator.GETELEMENTPTR),
					helper.get("G", 1),
					helper.getVariable(),
					helper.getVariable());
		helper.mustExist(gep);

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
				if (enableProofs) {
					addConstantProperties(proof, bundle.getTerm("G"));
				}
				
				Node result = node(derivedPointer,
								concOld(bundle.getTerm("gep")),
								steal(bundle.getTerm("gep"), 0));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);
				
				return bundle.getTerm("G").getOp().toString();
			}
			protected boolean matches(Bundle bundle) {
				FlowValue<LLVMParameter,LLVMLabel> G = bundle.getTerm("G").getOp();
				return G.isDomain() && G.getDomain().isGlobal();
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	/**
	 * B:bitcast(T,G:global)
	 * [T is pointer type]
	 * ==>
	 * derivedPointer(B,G)
	 */
	public void addBitcastGlobalIsDerived() {
		final String name = "Bitcast of global is derived";
		
		WildcardAxiomizerHelper helper = new WildcardAxiomizerHelper(
				new peggy.analysis.WildcardPeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));

		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> bitcast = 
			helper.get("bitcast", new CastLLVMLabel(Cast.Bitcast),
					helper.get("T", 1),
					helper.get("G", 2));
		helper.mustExist(bitcast);

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
				if (enableProofs) {
					addConstantProperties(proof, bundle.getTerm("T"));
					addConstantProperties(proof, bundle.getTerm("G"));
				}
				
				Node result = node(derivedPointer,
								concOld(bundle.getTerm("bitcast")),
								steal(bundle.getTerm("bitcast"), 1));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);
				
				return bundle.getTerm("G").getOp().toString();
			}
			protected boolean matches(Bundle bundle) {
				FlowValue<LLVMParameter,LLVMLabel> T = bundle.getTerm("T").getOp();
				FlowValue<LLVMParameter,LLVMLabel> G = bundle.getTerm("G").getOp();
				if (T.isDomain() && T.getDomain().isType()) {
					Type type = T.getDomain().getTypeSelf().getType();
					return type.isComposite() &&
						type.getCompositeSelf().isPointer() &&
						G.isDomain() && G.getDomain().isGlobal();
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	
//	/**
//	 * G:GEP(D,*,*)
//	 * derivedPointer(D,P)
//	 * ==>
//	 * derivedPointer(G,P)
//	 */
//	public void addGEPDerivedIsDerived() {
//		final String name = "GEP of derived is derived";
//		
//		WildcardAxiomizerHelper helper = new WildcardAxiomizerHelper(
//				new peggy.analysis.WildcardPeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
//
//		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> D = 
//			helper.getVariable("D");
//		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> gep = 
//			helper.get("gep", SimpleLLVMLabel.get(LLVMOperator.GETELEMENTPTR),
//					D,
//					helper.getVariable(),
//					helper.getVariable());
//		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> derived = 
//			helper.get("derived", derivedPointer,
//					D,
//					helper.getVariable("P"));
//		helper.mustExist(gep);
//		helper.mustBeTrue(derived);
//
//		final ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> 
//		triggerEvent = helper.getTrigger();
//		final StructureFunctions functions = helper.getStructureFunctions();
//
//		ShapeListener listener = new ShapeListener() {
//			protected ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> getProofEvent() {return triggerEvent;}
//			protected StructureFunctions getFunctions() {return functions;}
//			protected String getName() {return name;}
//			protected String build(
//					Bundle bundle,
//					FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> futureGraph) {
//				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
//				
//				Node result = node(derivedPointer,
//								concOld(bundle.getTerm("gep")),
//								steal(bundle.getTerm("derived"), 1));
//				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);
//				
//				return "";
//			}
//			protected boolean matches(Bundle bundle) {
//				return true;
//			}
//		};
//
//		addStringListener(listener, name);
//		triggerEvent.addListener(listener);
//	}
	
	
	/**
	 * B:bitcast(T,D)
	 * derivedPointer(D,P)
	 * [T is pointer type]
	 * ==>
	 * derivedPointer(B,P)
	 */
	public void addBitcastDerivedIsDerived() {
		final String name = "Bitcast of derived is derived";
		
		WildcardAxiomizerHelper helper = new WildcardAxiomizerHelper(
				new peggy.analysis.WildcardPeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));

		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> D = 
			helper.getVariable("D");
		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> bitcast = 
			helper.get("bitcast", new CastLLVMLabel(Cast.Bitcast),
					helper.get("T", 1),
					D);
		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> derived = 
			helper.get("derived", derivedPointer,
					D,
					helper.getVariable("P"));
		helper.mustExist(bitcast);
		helper.mustBeTrue(derived);

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
				if (enableProofs) {
					addConstantProperties(proof, bundle.getTerm("T"));
				}
				
				Node result = node(derivedPointer,
								concOld(bundle.getTerm("bitcast")),
								steal(bundle.getTerm("derived"), 1));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);
				
				return bundle.getTerm("T").getOp().toString();
			}
			protected boolean matches(Bundle bundle) {
				FlowValue<LLVMParameter,LLVMLabel> T = bundle.getTerm("T").getOp();
				if (T.isDomain() && T.getDomain().isType()) {
					Type type = T.getDomain().getTypeSelf().getType();
					return type.isComposite() && type.getCompositeSelf().isPointer();
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	
//	/**
//	 * G:GEP(P:rho_value(alloc(*)),*,*)
//	 * ==>
//	 * derivedPointer(G,P)
//	 */
//	public void addGEPAllocIsDerivedPointer(final LLVMOperator allocop) {
//		final String name = "GEP of allocation is a derived pointer";
//		
//		WildcardAxiomizerHelper helper = new WildcardAxiomizerHelper(
//				new peggy.analysis.WildcardPeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
//
//		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> gep = 
//			helper.get("gep", SimpleLLVMLabel.get(LLVMOperator.GETELEMENTPTR),
//					helper.get("rho_value", SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE),
//							helper.getAnyArity("alloc", SimpleLLVMLabel.get(allocop))),
//					helper.getVariable(),
//					helper.getVariable());
//		helper.mustExist(gep);
//
//		final ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> 
//		triggerEvent = helper.getTrigger();
//		final StructureFunctions functions = helper.getStructureFunctions();
//
//		ShapeListener listener = new ShapeListener() {
//			protected ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> getProofEvent() {return triggerEvent;}
//			protected StructureFunctions getFunctions() {return functions;}
//			protected String getName() {return name;}
//			protected String build(
//					Bundle bundle,
//					FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> futureGraph) {
//				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
//
//				Node result = node(derivedPointer,
//								concOld(bundle.getTerm("gep")),
//								steal(bundle.getTerm("gep"), 0));
//				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);
//				
//				return allocop.toString();
//			}
//			protected boolean matches(Bundle bundle) {
//				return true;
//			}
//		};
//
//		addStringListener(listener, name);
//		triggerEvent.addListener(listener);
//	}

	
	/**
	 * B:bitcast(T,P:rho_value(alloc(*)))
	 * [T is pointer type]
	 * ==>
	 * derivedPointer(B,P)
	 */
	public void addBitcastAllocIsDerivedPointer(final LLVMOperator allocop) {
		final String name = "Bitcast of allocation is a derived pointer";
		
		WildcardAxiomizerHelper helper = new WildcardAxiomizerHelper(
				new peggy.analysis.WildcardPeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));

		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> bitcast = 
			helper.get("bitcast", new CastLLVMLabel(Cast.Bitcast),
					helper.get("T", 1),
					helper.get("rho_value", SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE),
							helper.getAnyArity("alloc", SimpleLLVMLabel.get(allocop))));
		helper.mustExist(bitcast);

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
				if (enableProofs) {
					addConstantProperties(proof, bundle.getTerm("T"));
				}

				Node result = node(derivedPointer,
								concOld(bundle.getTerm("bitcast")),
								steal(bundle.getTerm("bitcast"), 1));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);
				
				return allocop.toString();
			}
			protected boolean matches(Bundle bundle) {
				FlowValue<LLVMParameter,LLVMLabel> T = bundle.getTerm("T").getOp();
				if (T.isDomain() && T.getDomain().isType()) {
					Type type = T.getDomain().getTypeSelf().getType();
					return type.isComposite() && type.getCompositeSelf().isPointer();
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	/**
	 * G1:gep(A,indexes(B,C))
	 * G2:gep(A,indexes(B,D))
	 * C!=D (constants)
	 * ==>
	 * {DNA(G1,G2)}
	 */
	private void addGEPDifferentOffsetsDNA2() {
		final String name = "GEPs of different offsets DNA (2)";
		
		WildcardAxiomizerHelper helper = new WildcardAxiomizerHelper(
				new peggy.analysis.WildcardPeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));

		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> A = 
			helper.getVariable("A");
		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> B = 
			helper.getVariable("B");
		
		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> gep1 = 
			helper.get("gep1", SimpleLLVMLabel.get(LLVMOperator.GETELEMENTPTR),
					A,
					helper.getVariable(),
					helper.get(SimpleLLVMLabel.get(LLVMOperator.INDEXES),
							B,
							helper.get("C", 1)));
		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> gep2 = 
			helper.get("gep2", SimpleLLVMLabel.get(LLVMOperator.GETELEMENTPTR),
					A,
					helper.getVariable(),
					helper.get(SimpleLLVMLabel.get(LLVMOperator.INDEXES),
							B,
							helper.get("D", 2)));
		helper.mustExist(gep1);
		helper.mustExist(gep2);

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
				if (enableProofs) {
					addConstantProperties(proof, bundle.getTerm("C"));
					addConstantProperties(proof, bundle.getTerm("D"));
				}

				Node result = node(doesNotAlias,
								concOld(bundle.getTerm("gep1")),
								concOld(bundle.getTerm("gep2")));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);
				
				return bundle.getTerm("C").getOp().toString() + 
					"!=" + 
					bundle.getTerm("D").getOp().toString();
			}
			protected boolean matches(Bundle bundle) {
				FlowValue<LLVMParameter,LLVMLabel> C = bundle.getTerm("C").getOp();
				FlowValue<LLVMParameter,LLVMLabel> D = bundle.getTerm("D").getOp();
				if (C.isDomain() && 
					C.getDomain().isConstantValue() &&
					C.getDomain().getConstantValueSelf().getValue().isInteger() &&
					D.isDomain() && 
					D.getDomain().isConstantValue() &&
					D.getDomain().getConstantValueSelf().getValue().isInteger()) {
					IntegerValue Cint = C.getDomain().getConstantValueSelf().getValue().getIntegerSelf();
					IntegerValue Dint = D.getDomain().getConstantValueSelf().getValue().getIntegerSelf();
					return !Cint.getAsBigInteger().equals(Dint.getAsBigInteger()); 
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	

	/**
	 * G1:gep(bitcast(A),indexes(B,C))
	 * G2:gep(A,indexes(B,D))
	 * C!=D (constants)
	 * ==>
	 * {DNA(G1,G2)}
	 */
	private void addGEPDifferentOffsetsBitcastDNA2() {
		final String name = "GEPs of different offsets through bitcast DNA (2)";
		
		WildcardAxiomizerHelper helper = new WildcardAxiomizerHelper(
				new peggy.analysis.WildcardPeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));

		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> A = 
			helper.getVariable("A");
		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> B = 
			helper.getVariable("B");
		
		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> gep1 = 
			helper.get("gep1", SimpleLLVMLabel.get(LLVMOperator.GETELEMENTPTR),
					helper.get(new CastLLVMLabel(Cast.Bitcast), 
							helper.getVariable(),
							A),
					helper.getVariable(),
					helper.get(SimpleLLVMLabel.get(LLVMOperator.INDEXES),
							B,
							helper.get("C", 1)));
		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> gep2 = 
			helper.get("gep2", SimpleLLVMLabel.get(LLVMOperator.GETELEMENTPTR),
					A,
					helper.getVariable(),
					helper.get(SimpleLLVMLabel.get(LLVMOperator.INDEXES),
							B,
							helper.get("D", 2)));
		helper.mustExist(gep1);
		helper.mustExist(gep2);

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
				if (enableProofs) {
					addConstantProperties(proof, bundle.getTerm("C"));
					addConstantProperties(proof, bundle.getTerm("D"));
				}

				Node result = node(doesNotAlias,
								concOld(bundle.getTerm("gep1")),
								concOld(bundle.getTerm("gep2")));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);
				
				return bundle.getTerm("C").getOp().toString() + 
					"!=" + 
					bundle.getTerm("D").getOp().toString();
			}
			protected boolean matches(Bundle bundle) {
				FlowValue<LLVMParameter,LLVMLabel> C = bundle.getTerm("C").getOp();
				FlowValue<LLVMParameter,LLVMLabel> D = bundle.getTerm("D").getOp();
				if (C.isDomain() && 
					C.getDomain().isConstantValue() &&
					C.getDomain().getConstantValueSelf().getValue().isInteger() &&
					D.isDomain() && 
					D.getDomain().isConstantValue() &&
					D.getDomain().getConstantValueSelf().getValue().isInteger()) {
					IntegerValue Cint = C.getDomain().getConstantValueSelf().getValue().getIntegerSelf();
					IntegerValue Dint = D.getDomain().getConstantValueSelf().getValue().getIntegerSelf();
					return !Cint.getAsBigInteger().equals(Dint.getAsBigInteger()); 
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	
	
	/**
	 * G1:gep(A,indexes(C))
	 * G2:gep(A,indexes(D))
	 * C!=D (constants)
	 * ==>
	 * {DNA(G1,G2)}
	 */
	private void addGEPDifferentOffsetsDNA1() {
		final String name = "GEPs of different offsets DNA (1)";
		
		WildcardAxiomizerHelper helper = new WildcardAxiomizerHelper(
				new peggy.analysis.WildcardPeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));

		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> A = 
			helper.getVariable("A");
		
		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> gep1 = 
			helper.get("gep1", SimpleLLVMLabel.get(LLVMOperator.GETELEMENTPTR),
					A,
					helper.getVariable(),
					helper.get(SimpleLLVMLabel.get(LLVMOperator.INDEXES),
							helper.get("C", 1)));
		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> gep2 = 
			helper.get("gep2", SimpleLLVMLabel.get(LLVMOperator.GETELEMENTPTR),
					A,
					helper.getVariable(),
					helper.get(SimpleLLVMLabel.get(LLVMOperator.INDEXES),
							helper.get("D", 2)));
		helper.mustExist(gep1);
		helper.mustExist(gep2);

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
				if (enableProofs) {
					addConstantProperties(proof, bundle.getTerm("C"));
					addConstantProperties(proof, bundle.getTerm("D"));
				}

				Node result = node(doesNotAlias,
								concOld(bundle.getTerm("gep1")),
								concOld(bundle.getTerm("gep2")));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);
				
				return bundle.getTerm("C").getOp().toString() + 
					"!=" + 
					bundle.getTerm("D").getOp().toString();
			}
			protected boolean matches(Bundle bundle) {
				FlowValue<LLVMParameter,LLVMLabel> C = bundle.getTerm("C").getOp();
				FlowValue<LLVMParameter,LLVMLabel> D = bundle.getTerm("D").getOp();
				if (C.isDomain() && 
					C.getDomain().isConstantValue() &&
					C.getDomain().getConstantValueSelf().getValue().isInteger() &&
					D.isDomain() && 
					D.getDomain().isConstantValue() &&
					D.getDomain().getConstantValueSelf().getValue().isInteger()) {
					IntegerValue Cint = C.getDomain().getConstantValueSelf().getValue().getIntegerSelf();
					IntegerValue Dint = D.getDomain().getConstantValueSelf().getValue().getIntegerSelf();
					return !Cint.getAsBigInteger().equals(Dint.getAsBigInteger()); 
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	
	
	private void addOppositeGEPsCancel() {
		final String name = "GEP(GEP(A,T,indexes(B)),T,indexes(-B)) = A";
		
		WildcardAxiomizerHelper helper = new WildcardAxiomizerHelper(
				new peggy.analysis.WildcardPeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> T = 
			helper.getVariable("T");
		Vertex<peggy.analysis.WildcardPeggyAxiomizer.AxiomValue<LLVMLabel,Integer>> gep1 = 
			helper.get("gep1", SimpleLLVMLabel.get(LLVMOperator.GETELEMENTPTR),
					helper.get("gep2", SimpleLLVMLabel.get(LLVMOperator.GETELEMENTPTR),
							helper.getVariable("A"),
							T,
							helper.get(SimpleLLVMLabel.get(LLVMOperator.INDEXES), 
									helper.get("B", 1))),
					T,
					helper.get(SimpleLLVMLabel.get(LLVMOperator.INDEXES), 
							helper.get("C", 2)));
							
		helper.mustExist(gep1);

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
				if (enableProofs) {
					addConstantProperties(proof, bundle.getTerm("B"));
					addConstantProperties(proof, bundle.getTerm("C"));
				}

				getEngine().getEGraph().makeEqual(bundle.getTerm("gep1"), bundle.getRep("A"), proof);

				return bundle.getTerm("B").getOp().toString();
			}
			protected boolean matches(Bundle bundle) {
				FlowValue<LLVMParameter,LLVMLabel> B = bundle.getTerm("B").getOp();
				FlowValue<LLVMParameter,LLVMLabel> C = bundle.getTerm("C").getOp();
				if (B.isDomain() && 
					B.getDomain().isConstantValue() &&
					B.getDomain().getConstantValueSelf().getValue().isInteger() &&
					C.isDomain() && 
					C.getDomain().isConstantValue() &&
					C.getDomain().getConstantValueSelf().getValue().isInteger()) {
					IntegerValue Bint = B.getDomain().getConstantValueSelf().getValue().getIntegerSelf();
					IntegerValue Cint = C.getDomain().getConstantValueSelf().getValue().getIntegerSelf();
					return Bint.getAsBigInteger().negate().equals(Cint.getAsBigInteger()); 
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	
	private void addUnaryDistributesThroughPhi() {
		final String name = "Unary domain op distributes through phi";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		PeggyVertex<LLVMLabel,Integer> op = 
			helper.get("op", null,
					helper.getPhi("phi",
							helper.getVariable("A"),
							helper.getVariable("B"),
							helper.getVariable("C")));
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
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs) addConstantProperties(proof, bundle.getTerm("op"));
				LLVMLabel op = bundle.getTerm("op").getOp().getDomain();
				CPEGTerm<LLVMLabel,LLVMParameter> phi = bundle.getTerm("phi");
				Node result = nodeFlow(FlowValue.<LLVMParameter,LLVMLabel>createPhi(),
						steal(phi,0),
						conc(node(op, steal(phi,1))),
						conc(node(op, steal(phi,2))));
				result.finish(bundle.getTerm("op"), proof, futureGraph);
				return op.toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel, LLVMParameter> op = bundle.getTerm("op");
				return op.getOp().isDomain();
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	/**
	 * call(*,func,*,P:params,*) where func has pointer params in positions 0,1,2
	 * ==>
	 * allNonstack(P,stackPointer(P0) || stackPointer(P1) || ... || stackPointer(Pn))
	 */
	private void addCallMakeAllNonstack(LLVMOperator callop) {
		final String name = callop + " makes allNonstack annotation";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		PeggyVertex<LLVMLabel,Integer> call = 
			helper.get("call", SimpleLLVMLabel.get(callop),
					helper.getVariable(),
					helper.get("func", null),
					helper.getVariable(),
					helper.getAnyArity("params", SimpleLLVMLabel.get(LLVMOperator.PARAMS)));
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
				
				ChildSource<LLVMLabel,LLVMParameter> child = concOld(getEngine().getEGraph().getFalse());
				FunctionLLVMLabel label = bundle.getTerm("func").getOp().getDomain().getFunctionSelf();
				FunctionType type = label.getType();
				for (int i = 0; i < type.getNumParams(); i++) {
					Type paramtype = type.getParamType(i);
					if (paramtype.isComposite() && paramtype.getCompositeSelf().isPointer()) {
						child = conc(nodeFlow(FlowValue.<LLVMParameter,LLVMLabel>createOr(),
									conc(node(stackPointer,
											steal(bundle.getTerm("params"),i))),
									child));
					}
				}
				Node result = node(allNonstack,
						concOld(bundle.getTerm("params")),
						child);
				result.finish(null, proof, futureGraph);
				return label.getFunctionName();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel, LLVMParameter> func = bundle.getTerm("func");
				if (func.getOp().isDomain() && func.getOp().getDomain().isFunction()) {
					FunctionType type = func.getOp().getDomain().getFunctionSelf().getType();
					if (type.isVararg())
						return false;
					for (int i = 0; i < type.getNumParams(); i++) {
						Type param = type.getParamType(i);
						if (param.isComposite() && param.getCompositeSelf().isPointer())
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
	 * R:rho_value([alloca|malloc](*))
	 * N:nullptr
	 * =>
	 * {doesNotAlias(R,N)}
	 */
	private void addAllocDoesNotAliasNull(LLVMOperator alloc) {
		final String name = alloc + " does not alias null";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		PeggyVertex<LLVMLabel,Integer> rho_value = 
			helper.get("rho_value", SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE),
					helper.get(SimpleLLVMLabel.get(alloc),
							helper.getVariable(),
							helper.getVariable(),
							helper.getVariable(),
							helper.getVariable()));
		helper.mustExist(rho_value);
		helper.mustExist(helper.get("null", null));

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
				if (enableProofs) addConstantProperties(proof, bundle.getTerm("null"));
				Node result = node(doesNotAlias,
						concOld(bundle.getTerm("rho_value")),
						concOld(bundle.getTerm("null")));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);
				return bundle.getTerm("null").getOp().getDomain().toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel, LLVMParameter> nul = bundle.getTerm("null");
				return nul.getOp().isDomain() &&
					nul.getOp().getDomain().isConstantValue() &&
					nul.getOp().getDomain().getConstantValueSelf().getValue().isConstantNullPointer();
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	
	
	/**
	 * F:<invariant function label> 
	 * ==>
	 * {sigmaInvariant(F)} 
	 */
	private void addSigmaInvariantAxioms() {
		final String name = "Tagging sigma invariant function";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		PeggyVertex<LLVMLabel,Integer> function = helper.get("function", null);
		helper.mustExist(function);

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
				if (enableProofs) addConstantProperties(proof, bundle.getTerm("function"));
				Node result = node(sigmaInvariant,
						concOld(bundle.getTerm("function")));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);
				return bundle.getTerm("function").getOp().getDomain().toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel, LLVMParameter> function = bundle.getTerm("function");
				if (function.getOp().isDomain() && function.getOp().getDomain().isFunction()) {
					return sigmaInvariants.contains(function.getOp().getDomain().getFunctionSelf());
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	private void addReadonlyReadnoneCallIsSigmaInvariant(LLVMOperator operator) {
		final String name = "Readonly/readnone call is sigma invariant";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> rho_sigma =
			helper.get("rho_sigma", SimpleLLVMLabel.get(LLVMOperator.RHO_SIGMA),
					helper.get("call", SimpleLLVMLabel.get(operator), 
							helper.getVariable("SIGMA"),
							helper.get("function", null),
							helper.getVariable(),
							helper.getVariable()));
		helper.mustExist(rho_sigma);

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
				if (enableProofs) addConstantProperties(proof, bundle.getTerm("function"));
				getEngine().getEGraph().makeEqual(
						bundle.getTerm("rho_sigma"),
						bundle.getRep("SIGMA"),
						proof);
				getEngine().getEGraph().processEqualities();
				
				return bundle.getTerm("function").getOp().toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel, LLVMParameter> func = bundle.getTerm("function");
				if (func.getOp().isDomain() && func.getOp().getDomain().isFunction()) {
					FunctionLLVMLabel label = func.getOp().getDomain().getFunctionSelf();
					FunctionValue value = resolver.resolveFunction(label.getFunctionName(), label.getType());
					ParameterAttributeMap map = value.getParameterAttributeMap();
					if (map.hasReturnAttributes()) {
						int bits = map.getReturnAttributes().getBits();
						if ((bits & ParameterAttributes.ReadNone) != 0 ||
							(bits & ParameterAttributes.ReadOnly) != 0) {
							return true;
						}
					} 
					
					if (map.hasFunctionAttributes()) {
						int bits = map.getFunctionAttributes().getBits();
						if ((bits & ParameterAttributes.ReadNone) != 0 ||
							(bits & ParameterAttributes.ReadOnly) != 0) {
							return true;
						}
					}
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	
	/**
	 * C:call(*,F,*,*,*)
	 * where F does not have any pointer parameters 
	 * ==>
	 * nonstackCall(C)
	 * 
	 * (same for tailcall and invoke)
	 */
	protected void addCallNoPointersIsNonstackCall(LLVMOperator callop) {
		final String name = "Call with no pointer params => nonstackCall";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> call =
			helper.get("call", SimpleLLVMLabel.get(callop), 
					helper.getVariable(),
					helper.get("function", null),
					helper.getVariable(),
					helper.getVariable());
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
				if (enableProofs) 
					proof.addProperty(new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(
						bundle.getTerm("function"),
						bundle.getTerm("function").getOp()));
				
				Node result = node(nonstackCall,
						concOld(bundle.getTerm("call")));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);
				
				return bundle.getTerm("function").getOp().getDomain().toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel, LLVMParameter> function = bundle.getTerm("function");
				if (function.getOp().isDomain() && function.getOp().getDomain().isFunction()) {
					FunctionType type = function.getOp().getDomain().getFunctionSelf().getType();
					for (int i = 0; i < type.getNumParams(); i++) {
						if (type.getParamType(i).isComposite() && 
							type.getParamType(i).getCompositeSelf().isPointer())
							return false;
					}
					return !type.isVararg();
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	
	/**
	 * G:GEP(A,*,indexes(NONZERO)) 
	 * ==>
	 * doesNotAlias(G,A)
	 */
	protected void addGEPNonzeroDoesNotAlias() {
		final String name = "Nonzero GEP doesNotAlias pointer";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> top =
			helper.get("gep", SimpleLLVMLabel.get(LLVMOperator.GETELEMENTPTR), 
					helper.getVariable("A"),
					helper.getVariable("B"),
					helper.get(SimpleLLVMLabel.get(LLVMOperator.INDEXES),
							helper.get("value", null)));
		helper.mustExist(top);

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
				if (enableProofs) 
					proof.addProperty(new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(
						bundle.getTerm("value"),
						bundle.getTerm("value").getOp()));
				
				Node result = node(doesNotAlias,
						concOld(bundle.getTerm("gep")),
						steal(bundle.getTerm("gep"), 0));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);
				
				return bundle.getTerm("value").getOp().getDomain().toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel, LLVMParameter> nonzero = bundle.getTerm("value");
				if (nonzero.getOp().isDomain() && nonzero.getOp().getDomain().isConstantValue()) {
					Value value = nonzero.getOp().getDomain().getConstantValueSelf().getValue();
					if (value.isInteger() && !value.getIntegerSelf().isZero())
						return true;
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	
	/**
	 * @TOP:rho_value(load(*,@V:rho_value(alloca(*,pointertype,*,*)),*))
	 * ==>
	 * {doesNotAlias(@TOP,@V)}
	 */
	protected void addLoadPointerAllocaDoesNotAliasAlloca() {
		final String name = "@TOP:load(*,@V:rho_value(alloca(*,pointertype,*,*)),*) " +
		"==> " +
		"{doesNotAlias(@TOP,@V)}";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> top =
			helper.get("top", SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE), 
					helper.get("load",
							SimpleLLVMLabel.get(LLVMOperator.LOAD),
							helper.getVariable(),
							helper.get("rho_value",
									SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE),
									helper.get(
											SimpleLLVMLabel.get(LLVMOperator.ALLOCA),
											helper.getVariable(),
											helper.get("type", null),
											helper.getVariable(),
											helper.getVariable())),
							helper.getVariable()));
		helper.mustExist(top);

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
				if (enableProofs) 
					proof.addProperty(new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(
						bundle.getTerm("type"),
						bundle.getTerm("type").getOp()));
				
				Node result = node(doesNotAlias,
						concOld(bundle.getTerm("top")),
						concOld(bundle.getTerm("rho_value")));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);
				
				return bundle.getTerm("type").getOp().getDomain().toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel, LLVMParameter> type = bundle.getTerm("type");
				if (type.getOp().isDomain() && type.getOp().getDomain().isType()) {
					Type t = type.getOp().getDomain().getTypeSelf().getType();
					return t.isComposite() && t.getCompositeSelf().isPointer();
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	/**
	 * N:nullpointer
	 * ==>
	 * !{stackPointer(N)}!
	 */
	protected void addNullIsNonstackpointer() {
		final String name = "N:nullpointer ==> !{stackPointer(N)}!";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> nullP = 
			helper.get("null", null);
		helper.mustExist(nullP);

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
				if (enableProofs) addConstantProperties(proof, bundle.getTerm("null"));

				Node result = node(
						stackPointer,
						concOld(bundle.getTerm("null")));
				result.finish(getEngine().getEGraph().getFalse(), proof, futureGraph);

				return bundle.getTerm("null").getOp().getDomain().toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel, LLVMParameter> nullP = bundle.getTerm("null");
				return nullP.getOp().isDomain() && 
					nullP.getOp().getDomain().isConstantValue() &&
					nullP.getOp().getDomain().getConstantValueSelf().getValue().isConstantNullPointer();
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}

	
	protected void addStackLoadSkipsModifyingFunction(
			LLVMOperator operator,
			Pair<Integer,Set<Integer>> pair) {
		int top = (1<<pair.getSecond().size());
		for (int i = 0; i < top; i++)
			addStackLoadSkipsModifyingFunction(operator, pair, i);
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
			Pair<Integer,Set<Integer>> pair,
			int mask) {
		final String name = "Non-aliasing stack load may skip modifying function call";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel,Integer>(name, getNetwork(), getAmbassador()));
		List<PeggyVertex<LLVMLabel,Integer>> Pi = 
			new ArrayList<PeggyVertex<LLVMLabel,Integer>>(pair.getFirst());
		for (int i = 0; i < pair.getFirst(); i++)
			Pi.add(helper.getVariable("P" + i)); 
		
		PeggyVertex<LLVMLabel,Integer> call = 
			helper.get("call",
					SimpleLLVMLabel.get(operator),
					helper.getVariable("SIGMA"),
					helper.get("func", null),
					helper.getVariable(),
					helper.get("params", SimpleLLVMLabel.get(LLVMOperator.PARAMS), Pi),
					helper.getVariable());

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
		helper.mustBeTrue(helper.get(stackPointer, P));
		int index = 0;
		for (int i : pair.getSecond()) {
			if (((mask>>index)&1) == 0)
				helper.mustBeTrue(helper.get(doesNotAlias, Pi.get(i), P));
			else
				helper.mustBeTrue(helper.get(doesNotAlias, P, Pi.get(i)));
			index++;
		}

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
				return func.getOp().isDomain() && func.getOp().getDomain().isFunction() &&
				 	modifyingFunctions.contains(func.getOp().getDomain().getFunctionSelf());
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
	protected void addModifyingFunctionMakesNonstackCall(Pair<Integer,Set<Integer>> pair) {
		final String name = "Call on non-stackPointers for all modifying arguments is a nonstackCall"; 

		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel,Integer>(name, getNetwork(), getAmbassador()));
		List<PeggyVertex<LLVMLabel,Integer>> Pi = 
			new ArrayList<PeggyVertex<LLVMLabel,Integer>>(pair.getFirst());
		for (int i = 0; i < pair.getFirst(); i++)
			Pi.add(helper.getVariable("P" + i)); 
		
		PeggyVertex<LLVMLabel,Integer> call = 
			helper.get("call",
					SimpleLLVMLabel.get(LLVMOperator.CALL),
					helper.getVariable(),
					helper.get("func", null),
					helper.getVariable(),
					helper.get("params", SimpleLLVMLabel.get(LLVMOperator.PARAMS), Pi),
					helper.getVariable());

		helper.mustExist(call);
		for (int i : pair.getSecond()) {
			helper.mustBeFalse(helper.get(stackPointer, Pi.get(i)));
		}

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
						nonstackCall,
						concOld(bundle.getTerm("call")));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);

				return bundle.getTerm("func").getOp().getDomain().toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel, LLVMParameter> func = bundle.getTerm("func");
				return func.getOp().isDomain() && func.getOp().getDomain().isFunction() &&
				 	modifyingFunctions.contains(func.getOp().getDomain().getFunctionSelf());
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	

	/**
	 * C:call(*,bitcast(*,nonstackfunc),*,*,*)
	 * ==>
	 * {nonstackCall(C)}
	 */
	protected void addBitcastNonstackFunctionMakesNonstackCall(LLVMOperator operator) {
		final String name =	"C:call(*,bitcast(*,nonstackfunc),*,*,*) " +
		 "==> " +
		 "{nonstackCall(C)}";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));

		PeggyVertex<LLVMLabel,Integer> call = 
			helper.get("call",
					SimpleLLVMLabel.get(operator),
					helper.getVariable(),
					helper.get(
							new CastLLVMLabel(Cast.Bitcast),
							helper.getVariable(),
							helper.get("func", null)),
					helper.getVariable(),
					helper.getVariable());
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
						nonstackCall,
						concOld(bundle.getTerm("call")));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);

				return bundle.getTerm("func").getOp().getDomain().toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel, LLVMParameter> func = bundle.getTerm("func");
				return func.getOp().isDomain() && func.getOp().getDomain().isFunction() &&
				 	NonstackFunctionAnalysis.this.functions.contains(func.getOp().getDomain().getFunctionSelf());
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	
	
	/**
	 * C:call(*,nonstackfunc,*,*,*)
	 * ==>
	 * {nonstackCall(C)}
	 */
	protected void addNonstackFunctionMakesNonstackCall(LLVMOperator operator) {
		final String name = "C:call(*,nonstackfunc,*,*,*) " +
		 "==> " +
		 "{nonstackCall(C)}";

		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));

		PeggyVertex<LLVMLabel,Integer> call = 
			helper.get("call",
					SimpleLLVMLabel.get(operator),
					helper.getVariable(),
					helper.get("func", null),
					helper.getVariable(),
					helper.getVariable());
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
						nonstackCall,
						concOld(bundle.getTerm("call")));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);

				return bundle.getTerm("func").getOp().getDomain().toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel, LLVMParameter> func = bundle.getTerm("func");
				return func.getOp().isDomain() && func.getOp().getDomain().isFunction() &&
				 	NonstackFunctionAnalysis.this.functions.contains(func.getOp().getDomain().getFunctionSelf());
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}

	
	
	
	/**
	 * B:bitcast(type,ptr)
	 * S:stackPointer(ptr)
	 * and type is a pointer type
	 * ==>
	 * S = stackPointer(B)
	 */
	private void addBitcastPreservesStackpointer() {
		final String name = "bitcast to ptr preserves stackPointer";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		
		PeggyVertex<LLVMLabel,Integer> ptr;
		PeggyVertex<LLVMLabel,Integer> bitcast = 
			helper.get("bitcast",
					new CastLLVMLabel(Cast.Bitcast),
					helper.get("type", null),
					ptr = helper.getVariable("PTR"));
		
		PeggyVertex<LLVMLabel,Integer> stackpointerV = 
			helper.get("S", stackPointer, ptr);
					
		helper.mustExist(stackpointerV);
		helper.mustExist(bitcast);
			
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
				if (enableProofs) addConstantProperties(proof, bundle.getTerm("type"));
				
				Node result = node(
						stackPointer,
						concOld(bundle.getTerm("bitcast")));
				result.finish(bundle.getTerm("S"), proof, futureGraph);

				return bundle.getTerm("type").getOp().getDomain().toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel,LLVMParameter> type = bundle.getTerm("type");
				if (type.getOp().isDomain() &&
					type.getOp().getDomain().isType()) {
					Type t = type.getOp().getDomain().getTypeSelf().getType();
					return t.isComposite() && t.getCompositeSelf().isPointer();
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
}
