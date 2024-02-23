package peggy.analysis.llvm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import llvm.bitcode.ReferenceResolver;
import llvm.types.CompositeType;
import llvm.types.Type;
import llvm.values.AliasValue;
import llvm.values.ConstantArrayValue;
import llvm.values.ConstantInlineASM;
import llvm.values.ConstantStructureValue;
import llvm.values.ConstantVectorValue;
import llvm.values.FunctionValue;
import llvm.values.GlobalVariable;
import llvm.values.Value;
import peggy.analysis.Analysis;
import peggy.represent.llvm.AliasLLVMLabel;
import peggy.represent.llvm.ConstantValueLLVMLabel;
import peggy.represent.llvm.FunctionLLVMLabel;
import peggy.represent.llvm.GlobalLLVMLabel;
import peggy.represent.llvm.InlineASMLLVMLabel;
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
 * This analysis has axioms that deal with global variables, such as replacing
 * a constant global with its value.
 */
public abstract class GlobalAnalysis extends Analysis<LLVMLabel,LLVMParameter> {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("GlobalAnalysis: " + message);
	}

	private static final LLVMLabel doesNotAlias = 
		new StringAnnotationLLVMLabel("doesNotAlias");
	private final Map<AliasLLVMLabel,LLVMLabel> aliasExpansionMap;
	private boolean hasAliaseeExpansions = false;
	private final ReferenceResolver resolver;
	private final boolean disableStackAndAlias;
	
	public GlobalAnalysis(
			boolean _disableStackAndAlias,
			ReferenceResolver _resolver,
			Network _network,
			CPeggyAxiomEngine<LLVMLabel, LLVMParameter> _engine) {
		super(_network, _engine);
		this.disableStackAndAlias = _disableStackAndAlias;
		this.resolver = _resolver;
		this.aliasExpansionMap = new HashMap<AliasLLVMLabel,LLVMLabel>();
	}
	
	public void addAll(Map<? extends AliasLLVMLabel,? extends LLVMLabel> map) {
		addAliaseeExpansions(map);
		addLoadConstGlobalAxiom();
		addLoadConstGlobalStructElementAxioms();
		if (!this.disableStackAndAlias) {
			addGlobalNonStackPointerAxioms();
			addLoadGlobalDNAGlobal(); // this rule is highly suspect
		}
	}
	
	public void addAliaseeExpansions(
			Map<? extends AliasLLVMLabel,? extends LLVMLabel> map) {
		this.aliasExpansionMap.putAll(map);
		if (!hasAliaseeExpansions) {
			buildAliaseeExpansionAxioms();
			hasAliaseeExpansions = true;
		}
	}

	
	/**
	 * R:rho_value(load(S,G:global,A))
	 * global has pointer type
	 * ==>
	 * {doesNotAlias(R,G)}
	 */
	private void addLoadGlobalDNAGlobal() {
		final String name = "Load of global DNA global";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
			PeggyVertex<LLVMLabel,Integer> rho_value =
				helper.get("rho_value", SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE),
						helper.get(SimpleLLVMLabel.get(LLVMOperator.LOAD),
								helper.getVariable(),
								helper.get("global", null),
								helper.getVariable()));
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

				final CPEGTerm<LLVMLabel,LLVMParameter> global = bundle.getTerm("global");
				final CPEGTerm<LLVMLabel,LLVMParameter> rho_value = bundle.getTerm("rho_value");

				if (enableProofs) addConstantProperties(proof, global);

				final Node result = node(
						doesNotAlias,
						concOld(global),
						concOld(rho_value));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);

				return global.getOp().toString();
			}
			protected boolean matches(Bundle bundle) {
				final CPEGTerm<LLVMLabel, LLVMParameter> globalTerm = bundle.getTerm("global");
				if (globalTerm.getOp().isDomain() && globalTerm.getOp().getDomain().isGlobal()) {
					Type type = globalTerm.getOp().getDomain().getGlobalSelf().getType();
					return type.isComposite() && type.getCompositeSelf().isPointer();
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	/**
	 * For each global G,
	 *   stackPointer(G) = False
	 */
	private void addGlobalNonStackPointerAxioms() {
		final String name = "Global is not a stackPointer";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
			PeggyVertex<LLVMLabel,Integer> global =
				helper.get("global", null);
		helper.mustExist(global);

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
				final CPEGTerm<LLVMLabel,LLVMParameter> global = bundle.getTerm("global");

				final Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs) addConstantProperties(proof, global);

				final Node result = node(
						new StringAnnotationLLVMLabel("stackPointer"),
						concOld(global));
				result.finish(getEngine().getEGraph().getFalse(), proof, futureGraph);

				return global.getOp().toString();
			}
			protected boolean matches(Bundle bundle) {
				final CPEGTerm<LLVMLabel, LLVMParameter> globalTerm = bundle.getTerm("global");
				return globalTerm.getOp().isDomain() && globalTerm.getOp().getDomain().isGlobal();
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	
	/**
	 * Load of an offset into a const struct
	 */
	public void addLoadConstGlobalStructElementAxioms() {
		final String name = "Load from element of global const struct";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
			PeggyVertex<LLVMLabel,Integer> rho_value =
				helper.get("rho_value",
						SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE),
						helper.get("load",
							SimpleLLVMLabel.get(LLVMOperator.LOAD),
							helper.getVariable(),
							helper.get("gep",
									SimpleLLVMLabel.get(LLVMOperator.GETELEMENTPTR),
									helper.get("global", null),
									helper.getVariable("type"),
									helper.getAnyArity("indexes", SimpleLLVMLabel.get(LLVMOperator.INDEXES))),
							helper.getVariable()));
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
				final GlobalLLVMLabel global = bundle.getTerm("global").getOp().getDomain().getGlobalSelf();
				final Value constValue = 
					resolver.resolveGlobal(global.getName(), global.getType()).getInitialValue();
				final List<Value> indexes = getIndexes(bundle.getTerm("indexes"));
				final Value elt = getGEPValue(indexes, 1, constValue);

				final Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs) addConstantProperties(proof, bundle.getTerm("global"));

				final Node result = node(new ConstantValueLLVMLabel(elt));
				result.finish(bundle.getTerm("rho_value"), proof, futureGraph);

				return global.toString();
			}
			protected boolean matches(Bundle bundle) {
				final CPEGTerm<LLVMLabel, LLVMParameter> globalTerm = bundle.getTerm("global");
				debug("Checking for globals");
				if (globalTerm.getOp().isDomain() && globalTerm.getOp().getDomain().isGlobal()) {
					GlobalLLVMLabel label = globalTerm.getOp().getDomain().getGlobalSelf();
					// look for type of pointer-to-struct
					final CPEGTerm<LLVMLabel,LLVMParameter> typeTerm = 
						findTypeTerm(bundle.getRep("type").getValue());
					if (typeTerm == null) {
						debug("Global type is not right");
						return false;
					}
					// check for all constant indexes
					final List<Value> indexes = getIndexes(bundle.getTerm("indexes"));
					if (indexes == null) {
						debug("GEP has bad indexes");
						return false;
					}
					// check that first index is 0
					if (!indexes.get(0).getIntegerSelf().isZero()) {
						debug("First index is not zero");
						return false;
					}
					// try to resolve global
					try {
						GlobalVariable global = resolver.resolveGlobal(label.getName(), label.getType());
						// check that global is constant
						return global.isConstant() && global.getInitialValue()!=null;
					} catch (Throwable t) {
						debug("Cannot resolve global");
					}
				}

				debug("No globals");
				return false;
			}
			private Value getGEPValue(List<Value> indexes, int start, Value into) {
				if (start >= indexes.size())
					return into;
				final Type type = into.getType();
				if (!type.isComposite())
					throw new RuntimeException("Not a composite type: " + type);
				final CompositeType ctype = type.getCompositeSelf();
				if (ctype.isPointer()) {
					throw new RuntimeException("The only constant pointer is NULL!");
				} else if (ctype.isArray()) {
					final int index = (int)indexes.get(start).getIntegerSelf().getLongBits();
					final ConstantArrayValue array = into.getConstantArraySelf();
					return getGEPValue(indexes, start+1, array.getElement(index));
				} else if (ctype.isVector()) {
					final int index = (int)indexes.get(start).getIntegerSelf().getLongBits();
					final ConstantVectorValue vector = into.getConstantVectorSelf();
					return getGEPValue(indexes, start+1, vector.getElement(index));
				} else if (ctype.isStructure()) {
					final int index = (int)indexes.get(start).getIntegerSelf().getLongBits();
					final ConstantStructureValue struct = into.getConstantStructureSelf();
					return getGEPValue(indexes, start+1, struct.getFieldValue(index));
				} else 
					throw new RuntimeException("Not sure what type this is: " + ctype);
			}
			private List<Value> getIndexes(CPEGTerm<LLVMLabel,LLVMParameter> term) {
				final List<Value> indexes = new ArrayList<Value>();
				for (int i = 0; i < term.getArity(); i++) {
					Value best = null;
					for (CPEGTerm<LLVMLabel,LLVMParameter> child : term.getChild(i).getValue().getTerms()) {
						if (child.getOp().isDomain() &&
								child.getOp().getDomain().isConstantValue() &&
								child.getOp().getDomain().getConstantValueSelf().getValue().isInteger()) {
							best = child.getOp().getDomain().getConstantValueSelf().getValue();
							break;
						}
					}
					if (best == null)
						return null;
					indexes.add(best);
				}
				return indexes;
			}
			private CPEGTerm<LLVMLabel,LLVMParameter> findTypeTerm(CPEGValue<LLVMLabel,LLVMParameter> value) {
				for (CPEGTerm<LLVMLabel,LLVMParameter> term : value.getTerms()) {
					if (term.getOp().isDomain() &&
							term.getOp().getDomain().isType()) {
						final Type type = term.getOp().getDomain().getTypeSelf().getType();
						if (type.isComposite() && type.getCompositeSelf().isPointer()) {
							Type pointee = type.getCompositeSelf().getPointerSelf().getPointeeType();
							if (pointee.isComposite()) {
								return term;
							}
						}
					}
				}
				return null;
			}
		};

		debug("Adding listener");
		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	/**
	 * load(*,constglobal,*) = value of const global
	 */
	public void addLoadConstGlobalAxiom() {
		final String name = "load(*,constglobal,*) = value of constglobal";
		
		AxiomizerHelper helper = new AxiomizerHelper(
			new PeggyAxiomizer<LLVMLabel, Integer>(name, getNetwork(), getAmbassador()));
		PeggyVertex<LLVMLabel,Integer> rho_value =  
			helper.get("rho_value",
					SimpleLLVMLabel.get(LLVMOperator.RHO_VALUE),
					helper.get("load",
							SimpleLLVMLabel.get(LLVMOperator.LOAD),
							helper.getVariable(),
							helper.get("global", null),
							helper.getVariable()));
		helper.mustExist(rho_value);

		final ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> 
		triggerEvent = helper.getTrigger();
		final StructureFunctions functions = helper.getStructureFunctions();
		
		ShapeListener listener = new ShapeListener() {
			protected ProofEvent<CPEGTerm<LLVMLabel,LLVMParameter>,? extends Structure<CPEGTerm<LLVMLabel, LLVMParameter>>> getProofEvent() {return triggerEvent;}
			protected StructureFunctions getFunctions() {return functions;}
			protected String getName() {return name;}
			protected LLVMLabel getConstantLabel(Value constant) {
				if (constant.isGlobalVariable()) {
					GlobalVariable global = constant.getGlobalVariableSelf();
					String name = resolver.getGlobalName(global);
					return new GlobalLLVMLabel(constant.getType(), name);
				} else if (constant.isFunction()) {
					FunctionValue function = constant.getFunctionSelf();
					String name = resolver.getFunctionName(function);
					return new FunctionLLVMLabel(
							function.getType().getPointeeType().getFunctionSelf(),
							name);
				} else if (constant.isInlineASM()) {
					ConstantInlineASM asm = constant.getInlineASMSelf();
					return new InlineASMLLVMLabel(asm);
				} else {
					return new ConstantValueLLVMLabel(constant);
				}
			}
			protected String build(
					Bundle bundle,
					FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> futureGraph) {
				GlobalLLVMLabel global = bundle.getTerm("global").getOp().getDomain().getGlobalSelf();
				Value constValue = 
					resolver.resolveGlobal(global.getName(), global.getType()).getInitialValue();
				
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs) addConstantProperties(proof, bundle.getTerm("global"));

				Node result = node(getConstantLabel(constValue));
				result.finish(bundle.getTerm("rho_value"), proof, futureGraph);

				return global.toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel, LLVMParameter> globalTerm = bundle.getTerm("global");
				if (globalTerm.getOp().isDomain() && globalTerm.getOp().getDomain().isGlobal()) {
					GlobalLLVMLabel label = globalTerm.getOp().getDomain().getGlobalSelf();
					try {
						GlobalVariable gv = resolver.resolveGlobal(label.getName(), label.getType());
						return gv.isConstant() && gv.getInitialValue()!=null;
					} catch (Throwable t) {}
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}

	
	// alias = aliasee
	private void buildAliaseeExpansionAxioms() {
		final String name = "alias expansion";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<LLVMLabel,Integer>(name, getNetwork(), getAmbassador()));
		PeggyVertex<LLVMLabel,Integer> alias = helper.get("alias", null);
		helper.mustExist(alias);
		
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

				AliasLLVMLabel label = bundle.getTerm("alias").getOp().getDomain().getAliasSelf();
				AliasValue av = resolver.resolveAlias(label.getName(), label.getType());
				Value aliasee = av.getAliaseeValue();
				
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs) addConstantProperties(proof, bundle.getTerm("alias"));

				LLVMLabel resultLabel;
				if (aliasee.isGlobalVariable()) {
					GlobalVariable gv = aliasee.getGlobalVariableSelf();
					resultLabel = new GlobalLLVMLabel(gv.getType().getPointeeType(), resolver.getGlobalName(gv)); 
				} else if (aliasee.isAlias()) {
					AliasValue av2 = aliasee.getAliasSelf();
					resultLabel = new AliasLLVMLabel(av2.getType().getPointeeType(), resolver.getAliasName(av2)); 
				} else if (aliasee.isFunction()) {
					FunctionValue fv = aliasee.getFunctionSelf();
					resultLabel = new FunctionLLVMLabel(
									fv.getType().getPointeeType().getFunctionSelf(), 
									resolver.getFunctionName(fv)); 
				} else {
					throw new RuntimeException("Not a valid aliasee: " + aliasee);
				}
					
				Node result = node(resultLabel);
				result.finish(bundle.getTerm("alias"), proof, futureGraph);

				return label.toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel, LLVMParameter> aliasTerm = bundle.getTerm("alias");
				if (aliasTerm.getOp().isDomain() && aliasTerm.getOp().getDomain().isAlias()) {
					AliasLLVMLabel label = aliasTerm.getOp().getDomain().getAliasSelf();
					try {
						resolver.resolveAlias(label.getName(), label.getType());
						return true;
					} catch (Throwable t) {}
				}
				return false;
			}
		};
		
		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
}
