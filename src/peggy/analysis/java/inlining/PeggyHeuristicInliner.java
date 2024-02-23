package peggy.analysis.java.inlining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import peggy.analysis.InlineAnalysis;
import peggy.analysis.java.ClassAnalysis;
import peggy.represent.PEGInfo;
import peggy.represent.PEGProvider;
import peggy.represent.java.AnnotationJavaLabel;
import peggy.represent.java.JavaLabel;
import peggy.represent.java.JavaLabelOpAmbassador;
import peggy.represent.java.JavaOperator;
import peggy.represent.java.JavaParameter;
import peggy.represent.java.JavaReturn;
import peggy.represent.java.MethodJavaLabel;
import peggy.represent.java.ReferenceResolver;
import peggy.represent.java.SimpleJavaLabel;
import soot.SootMethod;
import soot.SootMethodRef;
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
 * This analysis is the inliner for Java methods.
 */
public abstract class PeggyHeuristicInliner 
extends InlineAnalysis<JavaLabel,JavaParameter> {
	private static final AnnotationJavaLabel INLINE_TUPLE = 
		new AnnotationJavaLabel("inlineTuple");
	protected static final AnnotationJavaLabel GET_VALUE = 
		new AnnotationJavaLabel("getValue");
	protected static final AnnotationJavaLabel GET_EXCEPTION = 
		new AnnotationJavaLabel("getException");
	protected static final AnnotationJavaLabel CHECK_EXCEPTION = 
		new AnnotationJavaLabel("checkException");
	protected static final JavaLabel INJR = 
		SimpleJavaLabel.create(JavaOperator.INJR);
	
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("PeggyHeuristicInliner: " + message);
	}
	
	private final ReferenceResolver resolver;
	private final PEGProvider<SootMethod,JavaLabel,JavaParameter,JavaReturn> pegProvider;
	private final Set<MethodJavaLabel> inlinees;
	private final boolean inlineAll;
	
	public PeggyHeuristicInliner(
			ReferenceResolver _resolver,
			PEGProvider<SootMethod,JavaLabel,JavaParameter,JavaReturn> _pegProvider,
			Network _network, 
			CPeggyAxiomEngine<JavaLabel,JavaParameter> _engine,
			Collection<? extends MethodJavaLabel> _inlinees,
			boolean _inlineAll) {
		super(_network, _engine);
		this.inlineAll = _inlineAll;
		this.pegProvider = _pegProvider;
		this.resolver = _resolver;
		this.inlinees = new HashSet<MethodJavaLabel>(_inlinees);
	}

	public void addStaticInliningAxioms(SootMethod inliner) {
		// TODO add in exception stuff
		
		this.buildStaticInliningTrigger(inliner);

		{// rho_value(inlineTuple(V,S[,M])) = V
			PeggyAxiomizer<JavaLabel,Integer> axiomizer = 
				new PeggyAxiomizer<JavaLabel,Integer>(
						"rho_value(inlineTuple(V,S[,M])) = V", this.getNetwork(), getAmbassador());

			PeggyVertex<JavaLabel,Integer> V = axiomizer.getVariable(0);
			PeggyVertex<JavaLabel,Integer> S = axiomizer.getVariable(1);
			
			PeggyVertex<JavaLabel,Integer> rhoValue = 
				axiomizer.get(
						SimpleJavaLabel.create(JavaOperator.RHO_VALUE),
						axiomizer.get(INLINE_TUPLE, V, S));
			
			axiomizer.mustExist(rhoValue);
			axiomizer.makeEqual(rhoValue, V);
			
			addProofListener(
					this.getEngine().addPEGAxiom(axiomizer.getAxiom()), 
					"rho_value(inlineTuple(V,S[,M])) = V");
		}
		
		{// rho_sigma(inlinePair(V,S[,M])) = S
			PeggyAxiomizer<JavaLabel,Integer> axiomizer = 
				new PeggyAxiomizer<JavaLabel,Integer>(
						"rho_sigma(inlineTuple(V,S[,M])) = S", this.getNetwork(), getAmbassador());

			PeggyVertex<JavaLabel,Integer> V = axiomizer.getVariable(0);
			PeggyVertex<JavaLabel,Integer> S = axiomizer.getVariable(1);
			
			PeggyVertex<JavaLabel,Integer> rhoSigma =
				axiomizer.get(
						SimpleJavaLabel.create(JavaOperator.RHO_SIGMA), 
						axiomizer.get(INLINE_TUPLE, V, S));
			
			axiomizer.mustExist(rhoSigma);
			axiomizer.makeEqual(rhoSigma, S);
			addProofListener(
					this.getEngine().addPEGAxiom(axiomizer.getAxiom()),
					"rho_sigma(inlineTuple(V,S[,M])) = S");
		}
	}

	public JavaLabelOpAmbassador getAmbassador() {
		return (JavaLabelOpAmbassador)super.getAmbassador();
	}
	
	private void buildStaticInliningTrigger(final SootMethod inliner) {
		final String name = "Java static inlining";
		
		AxiomizerHelper helper = new AxiomizerHelper(
			new PeggyAxiomizer<JavaLabel,Integer>(
					name,
					this.getNetwork(), 
					this.getAmbassador()));
		
		// invokestatic(SIGMA,METHOD,params*,COUNTER)
		PeggyVertex<JavaLabel,Integer> SIGMA = helper.getVariable("SIGMA");
		PeggyVertex<JavaLabel,Integer> method = helper.get("method", null); // will be a term but label ignored
		PeggyVertex<JavaLabel,Integer> params = helper.getAnyArity("params", SimpleJavaLabel.create(JavaOperator.PARAMS));		
		
		PeggyVertex<JavaLabel,Integer> invokestatic = 
			helper.get("invoke", 
					SimpleJavaLabel.create(JavaOperator.INVOKESTATIC), 
					SIGMA, method, params);
		helper.mustExist(invokestatic);
		
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

				CPEGTerm<JavaLabel,JavaParameter> methodTerm = bundle.getTerm("method");
				CPEGTerm<JavaLabel,JavaParameter> invokestaticTerm = bundle.getTerm("invoke");

				int loopDepthIncrement = invokestaticTerm.getValue().getMaxVariance();
				
				MethodJavaLabel methodlabel = methodTerm.getOp().getDomain().getMethodSelf();
				SootMethodRef ref = resolver.resolveMethod(
						methodlabel.getClassName(),
						methodlabel.getMethodName(),
						methodlabel.getReturnType(),
						methodlabel.getParameterTypes());
				SootMethod inlinee = ref.resolve();
				SootMethod resolvedInlinee = 
					ClassAnalysis.resolveMethod(
							inliner.getDeclaringClass(), 
							inlinee.getSignature());
				inline(
						resolvedInlinee, 
						loopDepthIncrement,
						bundle,
						futureGraph);
				return methodlabel.toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<JavaLabel,JavaParameter> methodTerm = bundle.getTerm("method");
				if (methodTerm.getOp().isDomain() && methodTerm.getOp().getDomain().isMethod()) {
					MethodJavaLabel methodlabel = methodTerm.getOp().getDomain().getMethodSelf();
					SootMethodRef ref = resolver.resolveMethod(
							methodlabel.getClassName(),
							methodlabel.getMethodName(),
							methodlabel.getReturnType(),
							methodlabel.getParameterTypes());
					SootMethod inlinee = ref.resolve();
					inlinee = ClassAnalysis.resolveMethod(inliner.getDeclaringClass(), inlinee.getSignature());
					if (inlinee == null) {
						debug("resolved method is null");
						return false;
					}
					
					if (!inlineAll) {
						MethodJavaLabel resolvedLabel = 
							new MethodJavaLabel(
									inlinee.getDeclaringClass().getName(),
									inlinee.getName(),
									inlinee.getReturnType(),
									inlinee.getParameterTypes());
						if (!(inlinees.contains(methodlabel) ||
							  inlinees.contains(resolvedLabel)))
							return false;
					}

					InlinerSafetyAnalysis safetyAnalysis = new InlinerSafetyAnalysis();
					if (!safetyAnalysis.isSafeToInline(inliner, inlinee)) {
						debug("!safeToInline");
						return false;
					}

					return true;
				}
				return false;
			}
		};
		
		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	

	private PropertyBuilder addParamChild(
			FutureExpression<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>> result,
			int childIndex,
			JavaParameter parameter,
			Bundle bundle) {
		if (parameter.isSigma()) {
			return new EquivalentChildrenPropertyBuilder(
					result, childIndex, bundle.getTerm("invoke"), 0);
		} else if (parameter.isArgument()) {
			int index = parameter.getArgumentSelf().getIndex();
			return new EquivalentChildrenPropertyBuilder(
					result, childIndex, bundle.getTerm("params"), index); 
		} else {
			// 'this' not supported
			throw new RuntimeException("Bad parameter for inlinee: " + parameter);
		}
	}
	
	
	protected void inline(
			SootMethod actualInlinee,
			final int loopDepthIncrement,
			final Bundle bundle,
			final FutureExpressionGraph<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>> futureGraph) {
		
		final List<? extends Representative<CPEGValue<JavaLabel,JavaParameter>>> paramChildren = 
			bundle.getTerm("params").getChildren();
		final Set<PropertyBuilder> properties = new HashSet<PropertyBuilder>();
		final Map<Integer,FutureExpression<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>> newLoopDepths =  
			new HashMap<Integer,FutureExpression<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>>();
		
		Function<CRecursiveExpressionGraph.Vertex<FlowValue<JavaParameter,JavaLabel>>,
		         FutureExpressionGraph.Vertex<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>> converter = 
	            	new Function<CRecursiveExpressionGraph.Vertex<FlowValue<JavaParameter,JavaLabel>>,
	            	 			 FutureExpressionGraph.Vertex<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>>() {
						private final Tag<FutureExpressionGraph.Vertex<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>> cacheTag =
							new NamedTag("cachedValue");
						private final Tag<FutureAmbassador<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>> ambassadorTag =
							new NamedTag("ambassador");
						
						public FutureExpressionGraph.Vertex<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>> get(
								CRecursiveExpressionGraph.Vertex<FlowValue<JavaParameter,JavaLabel>> vertex) { 
							return eval(vertex);
						}
						
						public FutureExpressionGraph.Vertex<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>> eval(
								CRecursiveExpressionGraph.Vertex<FlowValue<JavaParameter,JavaLabel>> vertex) { 
							if (vertex.hasTag(cacheTag)) {
								return vertex.getTag(cacheTag);
							} else if (vertex.hasTag(ambassadorTag)) {
								if (vertex.getTag(ambassadorTag) == null) {
									vertex.setTag(ambassadorTag, futureGraph.makePlaceHolder());
								} 
								return vertex.getTag(ambassadorTag);
							}
							
							FlowValue<JavaParameter,JavaLabel> flow = vertex.getLabel();
							
							if (flow.isLoopFunction()) {
								FutureExpressionGraph.Vertex[] children =   
									new FutureExpressionGraph.Vertex[vertex.getChildCount()];
								vertex.setTag(ambassadorTag, null);
								for (int i = 0; i < vertex.getChildCount(); i++) {
									children[i] = this.get(vertex.getChild(i));
								}
								FutureExpression<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>> result =								
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
									Vertex<FlowValue<JavaParameter,JavaLabel>> child = 
										vertex.getChild(i);
									if (child.getLabel().isDomain() && child.getLabel().getDomain().equals(INJR)) 
										child = child.getChild(0);
									
									if (child.getLabel().isParameter()) {
										properties.add(addParamChild(
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
								JavaParameter parameter  = flow.getParameter();
								FutureExpressionGraph.Vertex<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>> result;
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
								if (flow.isDomain() && 
									flow.getDomain().equals(INJR)) {
									// remove INJRs!
									FutureExpressionGraph.Vertex<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>> result = 
										eval(vertex.getChild(0));
									vertex.setTag(cacheTag, result);
									return result;
								}

								FutureExpressionGraph.Vertex[] children =   
									new FutureExpressionGraph.Vertex[vertex.getChildCount()];
								vertex.setTag(ambassadorTag, null);
								for (int i = 0; i < vertex.getChildCount(); i++) {
									children[i] = this.get(vertex.getChild(i));
								}
								FutureExpression<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>> result =								
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
									Vertex<FlowValue<JavaParameter,JavaLabel>> child = 
										vertex.getChild(i);
									if (child.getLabel().isDomain() && child.getLabel().getDomain().equals(INJR)) 
										child = child.getChild(0);

									if (child.getLabel().isParameter()) {
										properties.add(addParamChild(
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
		PEGInfo<JavaLabel,JavaParameter,JavaReturn> info = 
			pegProvider.getPEG(actualInlinee);

		
		List<Vertex<FlowValue<JavaParameter,JavaLabel>>> roots = 
			new ArrayList<Vertex<FlowValue<JavaParameter,JavaLabel>>> (5);
		roots.add(info.getReturnVertex(JavaReturn.VALUE));
		roots.add(info.getReturnVertex(JavaReturn.SIGMA));
		
		List<FutureExpressionGraph.Vertex<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>> futureReturns = 
			new ArrayList();
		for (int i = 0; i < roots.size(); i++) {
			futureReturns.add(converter.get(roots.get(i)));
		}
		
		FutureExpression<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>> resultFuture = 
			futureGraph.getExpression(
					FlowValue.<JavaParameter,JavaLabel>createDomain(INLINE_TUPLE, getAmbassador()),
					futureReturns.toArray(new FutureExpressionGraph.Vertex[0]));
		
		// add properties about inlineTuple
		properties.add(new ArityIsPropertyBuilder(resultFuture));
		properties.add(new OpIsPropertyBuilder(resultFuture));
		for (int i = 0; i < roots.size(); i++) {
			Vertex<FlowValue<JavaParameter,JavaLabel>> root = roots.get(i);
			if (root.getLabel().isDomain() && 
				root.getLabel().getDomain().equals(INJR))
				root = root.getChild(0);
			
			if (root.getLabel().isParameter()) {
				properties.add(addParamChild(
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
				new ArityIs<CPEGTerm<JavaLabel,JavaParameter>>(
						bundle.getTerm("params"), paramChildren.size()));
		
		for (PropertyBuilder builder : properties) {
			Property property = builder.build();
			if (enableProofs) proof.addProperty(property);
		}
		// say that new loop depths are distinct
		List<FutureExpression<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>> orderedDepths = 
			new ArrayList<FutureExpression<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>>(
					newLoopDepths.values());
		for (int i = 0; i < orderedDepths.size(); i++) {
			FutureExpression<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>> depthI = 
				orderedDepths.get(i);
			if (enableProofs) 
			for (int j = i+1; j < orderedDepths.size(); j++) {
				proof.addProperty(
					new OpIsDifferentLoop<CPEGTerm<JavaLabel,JavaParameter>>(
							depthI.getTerm(),
							orderedDepths.get(j).getTerm()));
			}
		}
		// say that params are loop-invariant of new loop depths
		if (enableProofs) 
		for (int depth : newLoopDepths.keySet()) {
			for (int i = 0; i < paramChildren.size(); i++) {
				proof.addProperty(
						new ChildIsInvariant<CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>>(
								bundle.getTerm("params"), 
								i,
								newLoopDepths.get(depth).getTerm()));
			}
		}
		// say that the method was the method we inlined
		if (enableProofs) 
			proof.addProperty(
				new OpIs<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>>(
						bundle.getTerm("method"),
						bundle.getTerm("method").getOp()));
		
		if (DEBUG && enableProofs) {
			debug("Proof of inlining:");
			debug(proof.toString());
		}
		
		///////////////////
		
		getEngine().getEGraph().makeEqual(resultFuture.getTerm(), bundle.getTerm("invoke"), proof);
	}
}
