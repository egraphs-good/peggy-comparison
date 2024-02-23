package peggy.analysis.llvm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import peggy.analysis.InlineAnalysis;
import peggy.analysis.java.inlining.EngineInlineHeuristic;
import peggy.represent.PEGInfo;
import peggy.represent.PEGProvider;
import peggy.represent.llvm.AnnotationLLVMLabel;
import peggy.represent.llvm.FunctionLLVMLabel;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMOperator;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.LLVMReturn;
import peggy.represent.llvm.SimpleLLVMLabel;
import peggy.represent.llvm.StringAnnotationLLVMLabel;
import util.Function;
import util.NamedTag;
import util.Tag;
import util.graph.CRecursiveExpressionGraph;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.FutureAmbassador;
import eqsat.meminfer.engine.basic.FutureExpression;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.basic.Representative;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.event.ProofEvent;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.proof.ArityIs;
import eqsat.meminfer.engine.proof.ChildIsInvariant;
import eqsat.meminfer.engine.proof.OpIs;
import eqsat.meminfer.engine.proof.OpIsDifferentLoop;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.engine.proof.Property;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyVertex;

/**
 * This analysis implements inlining for LLVM.
 */
public abstract class LLVMInliner extends InlineAnalysis<LLVMLabel,LLVMParameter> {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("LLVMInliner: " + message);
	}

	protected static final StringAnnotationLLVMLabel GET_VALUE = 
		new StringAnnotationLLVMLabel("getValue");
	protected static final StringAnnotationLLVMLabel GET_EXCEPTION = 
		new StringAnnotationLLVMLabel("getException");
	protected static final StringAnnotationLLVMLabel CHECK_EXCEPTION = 
		new StringAnnotationLLVMLabel("checkException");
	private static final AnnotationLLVMLabel INLINE_TUPLE = 
		new StringAnnotationLLVMLabel("inlineTuple");
	protected static final LLVMLabel INJR = 
		SimpleLLVMLabel.get(LLVMOperator.INJR);
	protected static final LLVMLabel INJL = 
		SimpleLLVMLabel.get(LLVMOperator.INJL);
	protected static final LLVMLabel NONSTACK = 
		SimpleLLVMLabel.get(LLVMOperator.NONSTACK);
	

	private final PEGProvider<FunctionLLVMLabel,LLVMLabel,LLVMParameter,LLVMReturn> pegProvider;
	protected final EngineInlineHeuristic<FunctionLLVMLabel,Integer> inlineHeuristic;
	
	public LLVMInliner(
			Network _network, 
			CPeggyAxiomEngine<LLVMLabel,LLVMParameter> _engine,
			PEGProvider<FunctionLLVMLabel,LLVMLabel,LLVMParameter,LLVMReturn> _pegProvider,
			EngineInlineHeuristic<FunctionLLVMLabel,Integer> _inlineHeuristic) {
		super(_network, _engine);
		this.pegProvider = _pegProvider;
		this.inlineHeuristic = _inlineHeuristic;
	}
	
	public void addInliningAxioms() {
		this.buildCallInliningTrigger();
		this.buildTailCallInliningTrigger();
		
		// TODO inlining invoke is more complicated!
		// it requires converting all calls into invokes and so forth
//		this.buildInvokeInliningTrigger();
		
		this.buildGetresultTrigger();
	}
	
	private void buildGetresultTrigger() {
		final String name = "getresult(returnstructure(v1,...,vN),i) = i-th value";
		
		final AxiomizerHelper helper = new AxiomizerHelper(
			new PeggyAxiomizer<LLVMLabel,Integer>(
					name, this.getNetwork(), getAmbassador()));
		
		// getresult(returnstructure(*),index)
		PeggyVertex<LLVMLabel,Integer> getresult =
			helper.get("getresult",
					SimpleLLVMLabel.get(LLVMOperator.GETRESULT),
					helper.getAnyArity("returnstructure", SimpleLLVMLabel.get(LLVMOperator.RETURNSTRUCTURE)),
					helper.getVariable("index"));
		helper.mustExist(getresult);
		
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
				final Integer index = findValue(bundle.getRep("index").getValue());
				CPEGTerm<LLVMLabel,LLVMParameter> returnstructure = 
					bundle.getTerm("returnstructure");
				final Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				getEngine().getEGraph().makeEqual(bundle.getTerm("getresult"), returnstructure.getChildAsTerm(index), proof);
				return index+"";
			}
			private Integer findValue(CPEGValue<LLVMLabel,LLVMParameter> value) {
				for (CPEGTerm<LLVMLabel,LLVMParameter> term : value.getTerms()) {
					if (term.getOp().isDomain() &&
						term.getOp().getDomain().isNumeral()) {
						return term.getOp().getDomain().getNumeralSelf().getValue();
					}
				}
				return null;
			}
			protected boolean matches(Bundle bundle) {
				Integer result = findValue(bundle.getRep("index").getValue());
				return result != null;
			}
		};

		addStringListener(listener, "getresult(returnstructure(v1,...,vN),i) = i-th value");
		triggerEvent.addListener(listener);
	}

	private void buildCallInliningTrigger() {
		final String name = "Inlining call";
		
		AxiomizerHelper helper = new AxiomizerHelper(
			new PeggyAxiomizer<LLVMLabel,Integer>(
					name, this.getNetwork(), getAmbassador()));
		
		// call(SIGMA,func,CC,params*,ATTRS)
		PeggyVertex<LLVMLabel,Integer> call =
			helper.get("call",
					SimpleLLVMLabel.get(LLVMOperator.CALL),
					helper.getVariable("SIGMA"),
					helper.get("func", null),
					helper.getVariable("CC"),
					helper.getAnyArity("params", SimpleLLVMLabel.get(LLVMOperator.PARAMS)));
//					helper.getVariable("ATTRS"));
		
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
				CPEGTerm<LLVMLabel,LLVMParameter> funcTerm = bundle.getTerm("func");
				CPEGTerm<LLVMLabel,LLVMParameter> callTerm = bundle.getTerm("call");
				int loopDepthIncrement = callTerm.getValue().getMaxVariance();
				FunctionLLVMLabel funclabel = 
					funcTerm.getOp().getDomain().getFunctionSelf();
				inline("call", funclabel, loopDepthIncrement, bundle, futureGraph);
				return funclabel.toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel,LLVMParameter> func = bundle.getTerm("func");
				if (func.getOp().isDomain() && func.getOp().getDomain().isFunction()) {
					FunctionLLVMLabel funclabel = func.getOp().getDomain().getFunctionSelf();
					return inlineHeuristic.shouldInline(funclabel) &&
						pegProvider.canProvidePEG(funclabel);
				}
				return false;
			}
		};

		addStringListener(listener, "inlining call");
		triggerEvent.addListener(listener);
	}
	
	
	private void buildTailCallInliningTrigger() {
		final String name = "Inlining tail call";
		
		AxiomizerHelper helper = new AxiomizerHelper(
			new PeggyAxiomizer<LLVMLabel,Integer>(
					name, this.getNetwork(), getAmbassador()));
		
		// call(SIGMA,func,CC,params*,ATTRS)
		PeggyVertex<LLVMLabel,Integer> call =
			helper.get("call",
					SimpleLLVMLabel.get(LLVMOperator.TAILCALL),
					helper.getVariable("SIGMA"),
					helper.get("func", null),
					helper.getVariable("CC"),
					helper.getAnyArity("params", SimpleLLVMLabel.get(LLVMOperator.PARAMS)));
//					helper.getVariable("ATTRS"));
		
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
				CPEGTerm<LLVMLabel,LLVMParameter> funcTerm = bundle.getTerm("func");
				CPEGTerm<LLVMLabel,LLVMParameter> callTerm = bundle.getTerm("call");
				int loopDepthIncrement = callTerm.getValue().getMaxVariance();
				FunctionLLVMLabel funclabel = 
					funcTerm.getOp().getDomain().getFunctionSelf();
				inline("call", funclabel, loopDepthIncrement, bundle, futureGraph);
				return funclabel.toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel,LLVMParameter> func = bundle.getTerm("func");
				if (func.getOp().isDomain() && func.getOp().getDomain().isFunction()) {
					FunctionLLVMLabel funclabel = func.getOp().getDomain().getFunctionSelf();
					return inlineHeuristic.shouldInline(funclabel) &&
						pegProvider.canProvidePEG(funclabel);
				}
				return false;
			}
		};

		addStringListener(listener, "inlining tail call");
		triggerEvent.addListener(listener);
	}
	
	
	private void buildInvokeInliningTrigger() {
		final String name = "Inlining invoke";
		
		AxiomizerHelper helper = new AxiomizerHelper(
			new PeggyAxiomizer<LLVMLabel,Integer>(
					name, this.getNetwork(), getAmbassador()));
		
		// invoke(SIGMA,func,CC,params*,ATTRS)
		PeggyVertex<LLVMLabel,Integer> invoke =
			helper.get("invoke",
					SimpleLLVMLabel.get(LLVMOperator.INVOKE),
					helper.getVariable("SIGMA"),
					helper.get("func", null),
					helper.getVariable("CC"),
					helper.getAnyArity("params", SimpleLLVMLabel.get(LLVMOperator.PARAMS)),
					helper.getVariable("ATTRS"));
		
		helper.mustExist(invoke);
		
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
				CPEGTerm<LLVMLabel,LLVMParameter> funcTerm = bundle.getTerm("func");
				CPEGTerm<LLVMLabel,LLVMParameter> invokeTerm = bundle.getTerm("invoke");
				int loopDepthIncrement = invokeTerm.getValue().getMaxVariance();
				FunctionLLVMLabel funclabel = 
					funcTerm.getOp().getDomain().getFunctionSelf();
				inline("invoke", funclabel, loopDepthIncrement, bundle, futureGraph);
				return funclabel.toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<LLVMLabel,LLVMParameter> func = bundle.getTerm("func");
				if (func.getOp().isDomain() && func.getOp().getDomain().isFunction()) {
					FunctionLLVMLabel funclabel = func.getOp().getDomain().getFunctionSelf();
					return inlineHeuristic.shouldInline(funclabel) &&
						pegProvider.canProvidePEG(funclabel);
				}
				return false;
			}
		};

		addStringListener(listener, "inlining invoke");
		triggerEvent.addListener(listener);
	}

	
	
	private PropertyBuilder addParamChild(
			String label,
			FutureExpression<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> result,
			int childIndex,
			LLVMParameter parameter,
			Bundle bundle) {
		if (parameter.isSigma()) {
			return new EquivalentChildrenPropertyBuilder(
					result, childIndex, bundle.getTerm(label), 0);
		} else if (parameter.isArgument()) {
			int index = parameter.getArgumentSelf().getIndex();
			return new EquivalentChildrenPropertyBuilder(
					result, childIndex, bundle.getTerm("params"), index); 
		} else {
			// 'this' not supported
			throw new RuntimeException("Bad parameter for inlinee: " + parameter);
		}
	}
	
	
	protected boolean removeLabel(LLVMLabel label) {
		return label.equals(NONSTACK);
	}
	
	protected void inline(
			final String topLabel, // "call" or "invoke"
			FunctionLLVMLabel actualInlinee,
			final int loopDepthIncrement,
			final Bundle bundle,
			final FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> futureGraph) {
		
		final List<? extends Representative<CPEGValue<LLVMLabel,LLVMParameter>>> paramChildren = 
			bundle.getTerm("params").getChildren();
		final Set<PropertyBuilder> properties = new HashSet<PropertyBuilder>();
		final Map<Integer,FutureExpression<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>> newLoopDepths =  
			new HashMap<Integer,FutureExpression<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>>();
		
		Function<CRecursiveExpressionGraph.Vertex<FlowValue<LLVMParameter,LLVMLabel>>,
		         FutureExpressionGraph.Vertex<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>> converter = 
	            	new Function<CRecursiveExpressionGraph.Vertex<FlowValue<LLVMParameter,LLVMLabel>>,
	            	 			 FutureExpressionGraph.Vertex<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>>() {
						private final Tag<FutureExpressionGraph.Vertex<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>> cacheTag =
							new NamedTag("cachedValue");
						private final Tag<FutureAmbassador<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>> ambassadorTag =
							new NamedTag("ambassador");
						
						public FutureExpressionGraph.Vertex<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> get(
								CRecursiveExpressionGraph.Vertex<FlowValue<LLVMParameter,LLVMLabel>> vertex) { 
							return eval(vertex);
						}
						
						public FutureExpressionGraph.Vertex<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> eval(
								CRecursiveExpressionGraph.Vertex<FlowValue<LLVMParameter,LLVMLabel>> vertex) { 
							if (vertex.hasTag(cacheTag)) {
								return vertex.getTag(cacheTag);
							} else if (vertex.hasTag(ambassadorTag)) {
								if (vertex.getTag(ambassadorTag) == null) {
									vertex.setTag(ambassadorTag, futureGraph.makePlaceHolder());
								} 
								return vertex.getTag(ambassadorTag);
							}
							
							FlowValue<LLVMParameter,LLVMLabel> flow = vertex.getLabel();
							
							if (flow.isLoopFunction()) {
								FutureExpressionGraph.Vertex[] children =   
									new FutureExpressionGraph.Vertex[vertex.getChildCount()];
								vertex.setTag(ambassadorTag, null);
								for (int i = 0; i < vertex.getChildCount(); i++) {
									children[i] = this.get(vertex.getChild(i));
								}
								FutureExpression<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> result =								
									futureGraph.getExpression(updateLoopIndex(flow, loopDepthIncrement), children);
								int newDepth = flow.getLoopDepth() + loopDepthIncrement;
								if (!newLoopDepths.containsKey(newDepth))
									newLoopDepths.put(newDepth, result);
								
								if (vertex.getTag(ambassadorTag) != null) {
									result.setFutureValue(vertex.getTag(ambassadorTag));
									vertex.getTag(ambassadorTag).setIntendedExpression(result);
								}
								vertex.setTag(cacheTag, result);
								
								// proof stuff
								properties.add(new ArityIsPropertyBuilder(result));
								properties.add(new OpIsLoopOpPropertyBuilder(result));
								
								for (int i = 0; i < vertex.getChildCount(); i++) {
									Vertex<FlowValue<LLVMParameter,LLVMLabel>> child = 
										vertex.getChild(i);
									if (child.getLabel().isDomain()) {
										LLVMLabel mylabel = child.getLabel().getDomain();
										if (removeLabel(mylabel))
											child = child.getChild(0);
									}
									
									if (child.getLabel().isParameter()) {
										properties.add(addParamChild(
												topLabel,
												result, 
												i, 
												child.getLabel().getParameter(),
												bundle));
									} else {
										properties.add(new ChildIsEquivalentPropertyBuilder(result, i, children[i]));
									}
								}
								
								return result;
							} else if (flow.isParameter()) {
								LLVMParameter parameter  = flow.getParameter();
								FutureExpressionGraph.Vertex<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> result;
								if (parameter.isArgument()) {
									int index = parameter.getArgumentSelf().getIndex();
									result = futureGraph.getVertex(paramChildren.get(index));
								} else if (parameter.isSigma()) {
									result = futureGraph.getVertex(bundle.getRep("SIGMA").getRepresentative());
								} else {
									// no 'this'
									throw new RuntimeException("Invalid parameter: " + parameter);
								}
								
								vertex.setTag(cacheTag, result);
								return result;
							} else {
								if (flow.isDomain()) {
									final LLVMLabel mylabel = flow.getDomain();
									if (removeLabel(mylabel)) {
										// remove INJRs!
										FutureExpressionGraph.Vertex<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> result = 
											eval(vertex.getChild(0));
										vertex.setTag(cacheTag, result);
										return result;
									}
								}

								FutureExpressionGraph.Vertex[] children =   
									new FutureExpressionGraph.Vertex[vertex.getChildCount()];
								vertex.setTag(ambassadorTag, null);
								for (int i = 0; i < vertex.getChildCount(); i++) {
									children[i] = this.get(vertex.getChild(i));
								}
								FutureExpression<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> result =								
									futureGraph.getExpression(flow, children);

								if (vertex.getTag(ambassadorTag) != null) {
									result.setFutureValue(vertex.getTag(ambassadorTag));
									vertex.getTag(ambassadorTag).setIntendedExpression(result);
								}
								vertex.setTag(cacheTag, result);

								// proof stuff
								properties.add(new ArityIsPropertyBuilder(result));
								properties.add(new OpIsPropertyBuilder(result));
								for (int i = 0; i < vertex.getChildCount(); i++) {
									// we skipped injrs, remember?
									Vertex<FlowValue<LLVMParameter,LLVMLabel>> child = 
										vertex.getChild(i);
									if (child.getLabel().isDomain()) {
										final LLVMLabel mylabel = child.getLabel().getDomain();
										if (removeLabel(mylabel)) 
											child = child.getChild(0);
									}

									if (child.getLabel().isParameter()) {
										properties.add(addParamChild(
												topLabel,
												result, 
												i, 
												child.getLabel().getParameter(),
												bundle));
									} else {
										properties.add(new ChildIsEquivalentPropertyBuilder(result, i, children[i]));
									}
								}

								return result;
							}
						}
					};
							
		// now build the input PEG and run the converter on it
		PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> info = 
			pegProvider.getPEG(actualInlinee);

		
		List<Vertex<FlowValue<LLVMParameter,LLVMLabel>>> roots = 
			new ArrayList<Vertex<FlowValue<LLVMParameter,LLVMLabel>>> (5);
		roots.add(info.getReturnVertex(LLVMReturn.VALUE));
		roots.add(info.getReturnVertex(LLVMReturn.SIGMA));
		
		List<FutureExpressionGraph.Vertex<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>> futureReturns = 
			new ArrayList();
		for (int i = 0; i < roots.size(); i++) {
			futureReturns.add(converter.get(roots.get(i)));
		}
		
		FutureExpression<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> resultFuture = 
			futureGraph.getExpression(
					FlowValue.<LLVMParameter,LLVMLabel>createDomain(INLINE_TUPLE, getAmbassador()),
					futureReturns.toArray(new FutureExpressionGraph.Vertex[0]));
		
		// add properties about inlineTuple
		properties.add(new ArityIsPropertyBuilder(resultFuture));
		properties.add(new OpIsPropertyBuilder(resultFuture));
		for (int i = 0; i < roots.size(); i++) {
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> root = roots.get(i);
			if (root.getLabel().isDomain()) {
				final LLVMLabel mylabel = root.getLabel().getDomain();
				if (removeLabel(mylabel))
					root = root.getChild(0);
			}
			
			if (root.getLabel().isParameter()) {
				properties.add(addParamChild(
						topLabel,
						resultFuture, 
						i, 
						root.getLabel().getParameter(),
						bundle));
			} else {
				properties.add(
						new ChildIsEquivalentPropertyBuilder(
								resultFuture,
								i,
								futureReturns.get(i)));
			}
		}
		////////////////////////////////////////////////////////
		
		getEngine().getEGraph().addExpressions(futureGraph);
		getEngine().getEGraph().processEqualities();

		// build the proof
		Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
		if (enableProofs) 
			proof.addProperty(
				new ArityIs<CPEGTerm<LLVMLabel,LLVMParameter>>(
						bundle.getTerm("params"), paramChildren.size()));
		for (PropertyBuilder builder : properties) {
			Property property = builder.build();
			if (enableProofs) proof.addProperty(property);
		}
		// say that new loop depths are distinct
		List<FutureExpression<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>> orderedDepths = 
			new ArrayList<FutureExpression<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>>(
					newLoopDepths.values());
		if (enableProofs)
		for (int i = 0; i < orderedDepths.size(); i++) {
			FutureExpression<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>> depthI = 
				orderedDepths.get(i);
			for (int j = i+1; j < orderedDepths.size(); j++) {
				proof.addProperty(
					new OpIsDifferentLoop<CPEGTerm<LLVMLabel,LLVMParameter>>(
							depthI.getTerm(),
							orderedDepths.get(j).getTerm()));
			}
		}
		// say that params are loop-invariant of new loop depths
		if (enableProofs)
		for (int depth : newLoopDepths.keySet()) {
			for (int i = 0; i < paramChildren.size(); i++) {
				proof.addProperty(
						new ChildIsInvariant<CPEGTerm<LLVMLabel,LLVMParameter>,CPEGValue<LLVMLabel,LLVMParameter>>(
								bundle.getTerm("params"), 
								i,
								newLoopDepths.get(depth).getTerm()));
			}
		}
		// say that the method was the method we inlined
		if (enableProofs)
		proof.addProperty(
				new OpIs<FlowValue<LLVMParameter,LLVMLabel>,CPEGTerm<LLVMLabel,LLVMParameter>>(
						bundle.getTerm("func"),
						bundle.getTerm("func").getOp()));
		
		if (DEBUG && enableProofs) {
			debug("Proof of inlining:");
			debug(proof.toString());
		}
		
		///////////////////
		
		getEngine().getEGraph().makeEqual(resultFuture.getTerm(), bundle.getTerm(topLabel), proof);
	}
}
