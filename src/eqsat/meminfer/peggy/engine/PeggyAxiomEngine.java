package eqsat.meminfer.peggy.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.FutureAmbassador;
import eqsat.meminfer.engine.basic.FutureExpression;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.basic.Representative;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.basic.FutureExpressionGraph.Vertex;
import eqsat.meminfer.engine.event.AbstractChainEvent;
import eqsat.meminfer.engine.event.ChainEvent;
import eqsat.meminfer.engine.event.Event;
import eqsat.meminfer.engine.event.ProofAction;
import eqsat.meminfer.engine.event.ProofEvent;
import eqsat.meminfer.engine.op.axiom.AxiomInstance;
import eqsat.meminfer.engine.peg.EPEGManager;
import eqsat.meminfer.engine.peg.FuturePEG;
import eqsat.meminfer.engine.peg.PEGTerm;
import eqsat.meminfer.engine.peg.PEGValue;
import eqsat.meminfer.engine.peg.axiom.PEGOpEngine;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.network.Network.ListNode;
import eqsat.meminfer.network.Network.PostpendNode;
import eqsat.meminfer.network.basic.StructureNetwork.StructureNode;
import eqsat.meminfer.network.op.ExpressionNetwork.ExpressionNode;
import eqsat.meminfer.network.peg.PEGNetwork.PEGNode;
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork.AddOpNode;
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork.AxiomNode;
import util.Action;
import util.Actions;
import util.Labeled;
import util.NamedTag;
import util.Operation;
import util.Tag;
import util.Taggable;
import util.UnhandledCaseException;
import util.graph.OrderedVertex;

public abstract class PeggyAxiomEngine<O, P,
		T extends PEGTerm<O,P,T,V>, V extends PEGValue<T,V>>
		extends PEGOpEngine<O,P,T,V> {
	protected final Tag<Event<? extends Proof>> mAxiomActionTag
			= new NamedTag("Axiom Action");
	
	protected abstract EPEGManager<O,P,T,V> getEGraph();
	protected final boolean hasProofManager() {
		return getEGraph().hasProofManager();
	}

	protected final Action<? super AxiomInstance<FlowValue<P,O>,T,V>>
			setupAddOpAction(AddOpNode<O> node) {
		if (node.isAddExistingOp())
			return setupAddExistingOpAction(node.getAddExistingOp());
		else if (node.isAddNewOp())
			return setupAddNewOpAction(node.<P>getAddNewOp());
		else if (node.isAddLoopOp())
			return setupAddLoopOpAction(node.getAddLoopOp());
		else
			return null;
	}

	protected final Action<? super AxiomInstance<FlowValue<P,O>,T,V>>
			setupAddOpListAction(ListNode<? extends AddOpNode<O>> node) {
		if (node.isEmpty())
			return this.<AxiomInstance<FlowValue<P,O>,T,V>>setupEmptyAction(
					node.getEmpty());
		else if (node.isPostpend())
			return setupPostpendAddOpAction(node.getPostpend());
		else
			return null;
	}
	
	protected final Action<? super AxiomInstance<FlowValue<P,O>,T,V>>
			setupPostpendAddOpAction(
			PostpendNode<? extends AddOpNode<O>> postpend) {
		return Actions.sequence(processAddOpListNode(postpend.getHead()),
				processAddOpNode(postpend.getTail()));
	}

	protected final Event<? extends Proof> setupAxiomAction(
			final ProofEvent<T,? extends Structure<T>> trigger,
					AxiomNode<O,?> node) {
		final String name = node.getName();
		final int placeHolders = node.getPlaceHolders();
		final Action<? super AxiomInstance<FlowValue<P,O>,T,V>> setup
				= processAddOpListNode(node.getOps());
		final ProofAction<FlowValue<P,O>,T,V> constructs
				= processConstructListNode(node.getConstructs());
		final Action<? super AxiomInstance<FlowValue<P,O>,T,V>> merges
				= processMergeListNode(node.getMerges());
		ChainEvent<Structure<T>,Proof> event
				= new AbstractChainEvent<Structure<T>,Proof>() {
			public boolean canUse(Structure<T> parameter) {return true;}
			public boolean notify(Structure<T> parameter) {
				AxiomInstance<FlowValue<P,O>,T,V> instance =
						new AxiomInstance<FlowValue<P,O>,T,V>(name, parameter,
								placeHolders);
				setup.execute(instance);
				if (hasProofManager())
					trigger.generateProof(parameter, instance.getProof());
				constructs.execute(instance);
				getEGraph().addExpressions(instance.getGraph());
				if (hasProofManager())
					constructs.generateProof(instance);
				merges.execute(instance);
				trigger(hasProofManager() ? instance.getProof() : null);
				return true;
			}
		};
		trigger.addListener(event);
		return event;
	}

	protected final Action<? super AxiomInstance<FlowValue<P,O>,T,V>>
			processAddOpNode(AddOpNode<O> node) {
		if (node.hasTag(mSetupActionTag))
			return node.getTag(mSetupActionTag);
		Action<? super AxiomInstance<FlowValue<P,O>,T,V>> action
				= setupAddOpAction(node);
		if (action == null)
			throw new UnhandledCaseException(node);
		node.setTag(mSetupActionTag, action);
		return action;
	}

	protected final Action<? super AxiomInstance<FlowValue<P,O>,T,V>>
			processAddOpListNode(ListNode<? extends AddOpNode<O>> node) {
		if (node.hasTag(mSetupActionTag))
			return node.getTag(mSetupActionTag);
		Action<? super AxiomInstance<FlowValue<P,O>,T,V>> action
				= setupAddOpListAction(node);
		if (action == null)
			throw new UnhandledCaseException(node);
		node.setTag(mSetupActionTag, action);
		return action;
	}

	protected final Event<? extends Proof> processAxiomNode(
			ProofEvent<T,? extends Structure<T>> trigger, AxiomNode<O,?> node) {
		if (node.hasTag(mAxiomActionTag))
			return node.getTag(mAxiomActionTag);
		Event<? extends Proof> action = setupAxiomAction(trigger, node);
		if (action == null)
			throw new UnhandledCaseException(node);
		node.setTag(mAxiomActionTag, action);
		return action;
	}
	
	public final Event<? extends Proof> addStructureAxiom(
			AxiomNode<O,? extends StructureNode> node) {
		return processAxiomNode(
				getEGraph().processStructureNode(node.getTrigger()), node);
	}
	
	public final Event<? extends Proof> addExpressionAxiom(
			AxiomNode<O,? extends ExpressionNode<FlowValue<P,O>>> node) {
		return processAxiomNode(
				getEGraph().processExpressionNode(node.getTrigger()), node);
	}
	
	public final Event<? extends Proof> addPEGAxiom(
			AxiomNode<O,? extends PEGNode<O>> node) {
		return processAxiomNode(
				getEGraph().processPEGNode(node.getTrigger()), node);
	}
	
	public <E extends
			Labeled<? extends FlowValue<P,O>> & OrderedVertex<?,E> & Taggable>
			Representative<V> addExpression(E expression) {
		return addExpressions(Collections.singletonList(expression)).get(0);
	}
	
	public <E extends
			Labeled<? extends FlowValue<P,O>> & OrderedVertex<?,E> & Taggable>
			List<? extends T> addExpressions(
			List<? extends E> expressions) {
		final FuturePEG<O,P,T,V> graph
				= new FuturePEG<O,P,T,V>(getEGraph().getOpAmbassador());
		Operation<E,FutureExpression<FlowValue<P,O>,T,V>> createFuture
				= new Operation<E,FutureExpression<FlowValue<P,O>,T,V>>() {
			final Tag<FutureExpression<FlowValue<P,O>,T,V>> mTag
					= new NamedTag("Future Expression");
			final Tag<FutureAmbassador<FlowValue<P,O>,T,V>> mAmbassadorTag
					= new NamedTag("Future Ambassador");
			
			private FutureExpression<FlowValue<P,O>,T,V> convert(E parameter) {
				parameter.setTag(mTag, null);
				FutureExpressionGraph.Vertex<FlowValue<P,O>,T,V>[] children
						= new FutureExpressionGraph.Vertex[
						parameter.getChildCount()];
				FutureExpression<FlowValue<P,O>,T,V> expression;
				FlowValue<P,O> label = parameter.getLabel();
				if (label.isTrue())
					expression = graph.getTrue();
				else if (label.isFalse())
					expression = graph.getFalse();
				else if (label.isNegate())
					expression
							= graph.getNegate(get(parameter.getChild(0)));
				else if (label.isAnd())
					expression = graph.getAnd(get(parameter.getChild(0)),
							get(parameter.getChild(1)));
				else if (label.isOr())
					expression = graph.getOr(get(parameter.getChild(0)),
							get(parameter.getChild(1)));
				else if (label.isEquals())
					expression = graph.getEquals(get(parameter.getChild(0)),
							get(parameter.getChild(1)));
				else if (label.isPhi())
					expression = graph.getPhi(get(parameter.getChild(0)),
							get(parameter.getChild(1)),
							get(parameter.getChild(2)));
				else if (label.isShortCircuitAnd())
					expression = graph.getPhi(get(parameter.getChild(0)),
							get(parameter.getChild(1)),
							graph.getVertex(getEGraph().getFalse()));
				else if (label.isShortCircuitOr())
					expression = graph.getPhi(get(parameter.getChild(0)),
							graph.getVertex(getEGraph().getTrue()),
							get(parameter.getChild(1)));
				else if (label.isTheta())
					expression = graph.getTheta(label.getLoopDepth(),
							get(parameter.getChild(0)),
							get(parameter.getChild(1)));
				else if (label.isEval())
					expression = graph.getEval(label.getLoopDepth(),
							get(parameter.getChild(0)),
							get(parameter.getChild(1)));
				else if (label.isPass())
					expression = graph.getPass(label.getLoopDepth(),
							get(parameter.getChild(0)));
				else if (label.isParameter())
					expression = graph.getParameter(label.getParameter());
				else if (label.isDomain()) {
					for (int child = 0; child < children.length; child++)
						children[child] = get(parameter.getChild(child));
					expression = graph.getDomain(label.getDomain(), children);
				} else
					throw new UnhandledCaseException();
				if (parameter.hasTag(mAmbassadorTag)) {
					expression.setFutureValue(parameter.getTag(mAmbassadorTag));
					parameter.removeTag(mAmbassadorTag)
							.setIntendedExpression(expression);
				}
				parameter.setTag(mTag, expression);
				return expression;
			}
			
			public Vertex<FlowValue<P,O>,T,V> get(E parameter) {
				if (parameter.hasTag(mAmbassadorTag))
					return parameter.getTag(mAmbassadorTag);
				else if (parameter.hasTag(mTag)) {
					if (parameter.getTag(mTag) != null)
						return parameter.getTag(mTag);
					else {
						FutureAmbassador<FlowValue<P,O>,T,V> ambassador
								= graph.makePlaceHolder();
						parameter.setTag(mAmbassadorTag, ambassador);
						return ambassador;
					}
				} else
					return convert(parameter);
			}
			
			public FutureExpression<FlowValue<P,O>,T,V> execute(E parameter) {
				return get(parameter).getFutureExpression();
			}
		};
		List<FutureExpression<FlowValue<P,O>,T,V>> futures
				= new ArrayList(expressions.size());
		for (int i = 0; i < expressions.size(); i++)
			futures.add(createFuture.execute(expressions.get(i)));
		getEGraph().addExpressions(graph);
		List<T> representatives = new ArrayList<T>(expressions.size());
		for (FutureExpression<FlowValue<P,O>,T,V> future : futures)
			representatives.add(future.getTerm());
		return representatives;
	}
	
	/*public <E extends
			Labeled<? extends FlowValue<P,O>> & OrderedVertex<?,E> & Taggable>
			List<? extends Representative<V>> addExpressions(
			List<? extends E> expressions,
			List<? extends Representative<V>> values, Proof proof) {
		final FuturePEG<O,P,V> graph
				= new FuturePEG<O,P,V>(getEGraph().getOpAmbassador());
		Operation<E,Vertex<FlowValue<P,O>,V>> createFuture
				= new Operation<E,Vertex<FlowValue<P,O>,V>>() {
			final Tag<FutureExpression<FlowValue<P,O>,V>> mTag
					= new NamedTag("Future Expression");
			final Tag<FutureAmbassador<FlowValue<P,O>,V>> mAmbassadorTag
					= new NamedTag("Future Ambassador");
			
			private FutureExpression<FlowValue<P,O>,V> convert(E parameter) {
				parameter.setTag(mTag, null);
				FutureExpressionGraph.Vertex<FlowValue<P,O>,V>[] children
						= new FutureExpressionGraph.Vertex[
						parameter.getChildCount()];
				FutureExpression<FlowValue<P,O>,V> expression;
				FlowValue<P,O> label = parameter.getLabel();
				if (label.isTrue())
					expression = graph.getTrue();
				else if (label.isFalse())
					expression = graph.getFalse();
				else if (label.isNegate())
					expression
							= graph.getNegate(execute(parameter.getChild(0)));
				else if (label.isAnd())
					expression = graph.getAnd(execute(parameter.getChild(0)),
							execute(parameter.getChild(1)));
				else if (label.isOr())
					expression = graph.getOr(execute(parameter.getChild(0)),
							execute(parameter.getChild(1)));
				else if (label.isEquals())
					expression = graph.getEquals(execute(parameter.getChild(0)),
							execute(parameter.getChild(1)));
				else if (label.isPhi())
					expression = graph.getPhi(execute(parameter.getChild(0)),
							execute(parameter.getChild(1)),
							execute(parameter.getChild(2)));
				else if (label.isShortCircuitAnd())
					expression = graph.getPhi(execute(parameter.getChild(0)),
							execute(parameter.getChild(1)),
							graph.getVertex(getEGraph().getFalse()));
				else if (label.isShortCircuitOr())
					expression = graph.getPhi(execute(parameter.getChild(0)),
							graph.getVertex(getEGraph().getTrue()),
							execute(parameter.getChild(1)));
				else if (label.isTheta())
					expression = graph.getTheta(label.getLoopDepth(),
							execute(parameter.getChild(0)),
							execute(parameter.getChild(1)));
				else if (label.isEval())
					expression = graph.getEval(label.getLoopDepth(),
							execute(parameter.getChild(0)),
							execute(parameter.getChild(1)));
				else if (label.isPass())
					expression = graph.getPass(label.getLoopDepth(),
							execute(parameter.getChild(0)));
				else if (label.isParameter())
					expression = graph.getParameter(label.getParameter());
				else if (label.isDomain()) {
					for (int child = 0; child < children.length; child++)
						children[child] = execute(parameter.getChild(child));
					expression = graph.getDomain(label.getDomain(), children);
				} else
					throw new UnhandledCaseException();
				if (parameter.hasTag(mAmbassadorTag)) {
					expression.setFutureValue(parameter.getTag(mAmbassadorTag));
					parameter.removeTag(mAmbassadorTag)
							.setIntendedExpression(expression);
				}
				parameter.setTag(mTag, expression);
				return expression;
			}
			
			public Vertex<FlowValue<P,O>,V> execute(E parameter) {
				if (parameter.hasTag(mAmbassadorTag))
					return parameter.getTag(mAmbassadorTag);
				else if (parameter.hasTag(mTag)) {
					if (parameter.getTag(mTag) != null)
						return parameter.getTag(mTag);
					else {
						FutureAmbassador<FlowValue<P,O>,V> ambassador
								= graph.makePlaceHolder();
						parameter.setTag(mAmbassadorTag, ambassador);
						return ambassador;
					}
				} else
					return convert(parameter);
			}
		};
		List<Vertex<FlowValue<P,O>,V>> futures
				= new ArrayList<Vertex<FlowValue<P,O>,V>>(expressions.size());
		for (int i = 0; i < expressions.size(); i++) {
			Vertex<FlowValue<P,O>,V> future
					= createFuture.execute(expressions.get(i));
			if (values.get(i) != null)
				future.getFutureExpression().setValue(values.get(i));
			futures.add(future);
		}
		getEGraph().addExpressions(graph, proof);
		List<Representative<V>> representatives
				= new ArrayList<Representative<V>>(expressions.size());
		for (Vertex<FlowValue<P,O>,V> future : futures)
			representatives.add(future.getRepresentative());
		return representatives;
	}*/
}
