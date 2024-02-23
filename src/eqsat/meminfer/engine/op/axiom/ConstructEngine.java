package eqsat.meminfer.engine.op.axiom;

import eqsat.meminfer.engine.basic.FutureExpression;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.basic.TermOrTermChild;
import eqsat.meminfer.engine.basic.Value;
import eqsat.meminfer.engine.basic.FutureExpressionGraph.Vertex;
import eqsat.meminfer.engine.event.ProofAction;
import eqsat.meminfer.engine.event.ProofActions;
import eqsat.meminfer.engine.op.OpEGraphManager;
import eqsat.meminfer.engine.op.OpTerm;
import eqsat.meminfer.engine.proof.ArityIs;
import eqsat.meminfer.engine.proof.ChildIsEquivalentTo;
import eqsat.meminfer.engine.proof.EquivalentChildren;
import eqsat.meminfer.network.Network.EmptyNode;
import eqsat.meminfer.network.Network.ListNode;
import eqsat.meminfer.network.Network.PostpendNode;
import eqsat.meminfer.network.basic.ValueNetwork.ValueNode;
import eqsat.meminfer.network.op.axiom.FutureValueFunction;
import eqsat.meminfer.network.op.axiom.ConstructNetwork.ConstructExpressionNode;
import eqsat.meminfer.network.op.axiom.ConstructNetwork.ConstructFalseNode;
import eqsat.meminfer.network.op.axiom.ConstructNetwork.ConstructNode;
import eqsat.meminfer.network.op.axiom.ConstructNetwork.ConstructTrueNode;
import eqsat.meminfer.network.op.axiom.ConstructNetwork.ConstructValueNode;
import eqsat.meminfer.network.op.axiom.ConstructNetwork.ExtendedValueNode;
import eqsat.meminfer.network.op.axiom.ConstructNetwork.PlaceHolderValueNode;
import eqsat.meminfer.network.op.axiom.ConstructNetwork.ValueSourceNode;
import util.Action;
import util.Actions;
import util.Function;
import util.NamedTag;
import util.Tag;
import util.UnhandledCaseException;
import util.pair.Pair;

public abstract class ConstructEngine
		<O, T extends OpTerm<O,T,V>, V extends Value<T,V>> {
	private final
			Tag<Action<Pair<AxiomInstance<O,T,V>,FutureExpression<O,T,V>>>>
			mValueActionTag = new NamedTag("Value Action");
	private final Tag<FutureValueFunction<O,T,V>>
			mValueFunctionTag = new NamedTag("Value Function");
	private final Tag<ProofAction<O,T,V>> mConstructActionTag
			= new NamedTag("Construct Action");
	
	protected abstract OpEGraphManager<O,T,V> getEGraph();
	
	protected <P> Action<? super P> setupEmptyAction(EmptyNode node) {
		return Actions.<P>empty();
	}

	protected Action<Pair<AxiomInstance<O,T,V>,FutureExpression<O,T,V>>>
			setupPlaceHolderValueSourceAction(ValueSourceNode node) {
		final int placeHolder = node.getPlaceHolderValue();
		return new Action<Pair<AxiomInstance<O,T,V>,FutureExpression<O,T,V>>>(){
			public void execute(
					Pair<AxiomInstance<O,T,V>,FutureExpression<O,T,V>>
					parameter) {
				parameter.getSecond().setFutureValue(
						parameter.getFirst().getPlaceHolder(placeHolder));
				parameter.getFirst().getPlaceHolder(placeHolder)
						.setIntendedExpression(parameter.getSecond());
			}
		};
	}

	protected Action<Pair<AxiomInstance<O,T,V>,FutureExpression<O,T,V>>>
			setupNewValueAction(ValueSourceNode node) {
		return Actions
				.<Pair<AxiomInstance<O,T,V>,FutureExpression<O,T,V>>>empty();
	}

	protected Action<Pair<AxiomInstance<O,T,V>,FutureExpression<O,T,V>>>
			setupValueSourceAction(ValueSourceNode node) {
		if (node.isNewValue())
			return setupNewValueAction(node);
		else if (node.isPlaceHolderValue())
			return setupPlaceHolderValueSourceAction(node);
		else
			return null;
	}

	protected FutureValueFunction<O,T,V> setupPlaceHolderValueFunction(
			PlaceHolderValueNode node) {
		final int placeHolder = node.getPlaceHolder();
		return new FutureValueFunction<O,T,V>() {
			public Vertex<O,T,V> getVertex(AxiomInstance<O,T,V> instance) {
				return instance.getPlaceHolder(placeHolder);
			}
			public TermOrTermChild<T,V> getValue(AxiomInstance<O,T,V> instance){
				return instance.getPlaceHolder(placeHolder)
						.getIntendedExpression().getTerm();
			}
		};
	}

	protected FutureValueFunction<O,T,V> setupConstructValueFunction(
			ConstructValueNode node) {
		final int construct = node.getConstruct();
		return new FutureValueFunction<O,T,V>() {
			public Vertex<O,T,V> getVertex(AxiomInstance<O,T,V> instance) {
				return instance.getConstruct(construct);
			}
			public TermOrTermChild<T,V> getValue(AxiomInstance<O,T,V> instance){
				return instance.getConstruct(construct).getTerm();
			}
		};
	}

	protected FutureValueFunction<O,T,V> setupValueToValueFunction(
			ValueNode node) {
		final Function<? super Structure<T>,? extends TermOrTermChild<T,V>>
				value = getEGraph().processValueNode(node);
		return new FutureValueFunction<O,T,V>() {
			public Vertex<O,T,V> getVertex(AxiomInstance<O,T,V> instance) {
				return instance.getGraph().getVertex(
						value.get(instance.getTrigger()).getRepresentative());
			}
			public TermOrTermChild<T,V> getValue(AxiomInstance<O,T,V> instance){
				return value.get(instance.getTrigger());
			}
		};
	}

	protected FutureValueFunction<O,T,V> setupExtendedValueFunction(
			ExtendedValueNode node) {
		if (node.isPlaceHolderValue())
			return setupPlaceHolderValueFunction(node.getPlaceHolderValue());
		else if (node.isConstructValue())
			return setupConstructValueFunction(node.getConstructValue());
		else if (node.isValue())
			return setupValueToValueFunction(node.getValue());
		else
			return null;
	}
	
	private ProofAction<O,T,V> setupConstructAction(ConstructNode node) {
		if (node.isConstructExpression())
			return setupConstructExpressionAction(
					node.getConstructExpression());
		else if (node.isConstructTrue())
			return setupConstructTrueAction(node.getConstructTrue());
		else if (node.isConstructFalse())
			return setupConstructFalseAction(node.getConstructFalse());
		else
			return null;
	}

	private ProofAction<O,T,V> setupConstructExpressionAction(
			ConstructExpressionNode node) {
		final Action<Pair<AxiomInstance<O,T,V>,FutureExpression<O,T,V>>>
				value = processValueSourceNode(node.getValue());
		final int op = node.getOp().getOp();
		final FutureValueFunction<O,T,V>[] children
				= new FutureValueFunction[node.getArity()];
		for (int i = 0; i < children.length; i++)
			children[i] = processExtendedValueNode(node.getChild(i));
		return new ProofAction<O,T,V>() {
			private FutureExpression<O,T,V> getExpression(
					AxiomInstance<O,T,V> instance) {
				Vertex<O,T,V>[] expressions = new Vertex[children.length];
				for (int i = 0; i < children.length; i++)
					expressions[i] = children[i].getVertex(instance);
				return instance.getGraph().getExpression(
						instance.getOp(op), expressions);
			}
			public void execute(AxiomInstance<O,T,V> instance) {
				FutureExpression<O,T,V> expression = getExpression(instance);
				value.execute(
						new Pair<AxiomInstance<O,T,V>,FutureExpression<O,T,V>>(
								instance, expression));
				instance.addConstruct(expression);
			}
			public void generateProof(AxiomInstance<O,T,V> instance) {
				FutureExpression<O,T,V> expression = getExpression(instance);
				T term = expression.getTerm();
				instance.getProof().addProperty(
						new ArityIs<T>(term, expression.getChildCount()));
				instance.constrainOp(op, term);
				for (int i = 0; i < children.length; i++) {
					TermOrTermChild<T,V> child = children[i].getValue(instance);
					if (child.isTerm())
						instance.getProof().addProperty(
								new ChildIsEquivalentTo(term, i,
										child.getTerm()));
					else if (child.isTermChild())
						instance.getProof().addProperty(
								new EquivalentChildren(term, i,
								child.getParentTerm(), child.getChildIndex()));
					else
						throw new UnhandledCaseException();
				}
			}
		};
	}

	private ProofAction<O,T,V> setupConstructTrueAction(ConstructTrueNode node){
		return new ProofAction<O,T,V>() {
			public void execute(AxiomInstance<O,T,V> instance) {
				instance.addConstruct(
						getEGraph().getTrueFuture(instance.getGraph()));
			}
			public void generateProof(AxiomInstance<O,T,V> instance) {
				getEGraph().constrainTrue(instance.getProof());
			}
		};
	}

	private ProofAction<O,T,V> setupConstructFalseAction(
			ConstructFalseNode node) {
		return new ProofAction<O,T,V>() {
			public void execute(AxiomInstance<O,T,V> instance) {
				instance.addConstruct(
						getEGraph().getFalseFuture(instance.getGraph()));
			}
			public void generateProof(AxiomInstance<O,T,V> instance) {
				getEGraph().constrainFalse(instance.getProof());
			}
		};
	}
	
	protected ProofAction<O,T,V> setupPostpendConstructAction(
			PostpendNode<? extends ConstructNode> node) {
		return ProofActions.sequence(processConstructListNode(node.getHead()),
				processConstructNode(node.getTail()));
	}

	protected ProofAction<O,T,V> setupConstructListAction(
			ListNode<? extends ConstructNode> node) {
		if (node.isEmpty())
			return ProofActions.<O,T,V>empty();
		else if (node.isPostpend())
			return setupPostpendConstructAction(node.getPostpend());
		else
			return null;
	}

	protected final Action<Pair<AxiomInstance<O,T,V>,FutureExpression<O,T,V>>>
			processValueSourceNode(ValueSourceNode node) {
		if (node.hasTag(mValueActionTag))
			return node.getTag(mValueActionTag);
		Action<Pair<AxiomInstance<O,T,V>,FutureExpression<O,T,V>>> action
				= setupValueSourceAction(node);
		if (action == null)
			throw new UnhandledCaseException(node);
		node.setTag(mValueActionTag, action);
		return action;
	}

	protected final FutureValueFunction<O,T,V>
			processExtendedValueNode(ExtendedValueNode node) {
		if (node.hasTag(mValueFunctionTag))
			return node.getTag(mValueFunctionTag);
		FutureValueFunction<O,T,V> function = setupExtendedValueFunction(node);
		if (function == null)
			throw new UnhandledCaseException(node);
		node.setTag(mValueFunctionTag, function);
		return function;
	}

	protected final ProofAction<O,T,V> processConstructNode(ConstructNode node){
		if (node.hasTag(mConstructActionTag))
			return node.getTag(mConstructActionTag);
		ProofAction<O,T,V> action = setupConstructAction(node);
		if (action == null)
			throw new UnhandledCaseException(node);
		node.setTag(mConstructActionTag, action);
		return action;
	}

	protected final ProofAction<O,T,V>
			processConstructListNode(ListNode<? extends ConstructNode> node) {
		if (node.hasTag(mConstructActionTag))
			return node.getTag(mConstructActionTag);
		ProofAction<O,T,V> action = setupConstructListAction(node);
		if (action == null)
			throw new UnhandledCaseException(node);
		node.setTag(mConstructActionTag, action);
		return action;
	}
}
