package eqsat.meminfer.engine.basic.axiom;

import eqsat.meminfer.engine.basic.Value;
import eqsat.meminfer.engine.op.OpTerm;
import eqsat.meminfer.engine.op.axiom.AxiomInstance;
import eqsat.meminfer.engine.op.axiom.ConstructEngine;
import eqsat.meminfer.network.Network.ListNode;
import eqsat.meminfer.network.Network.PostpendNode;
import eqsat.meminfer.network.basic.axiom.MergeNetwork.MergeNode;
import eqsat.meminfer.network.op.axiom.FutureValueFunction;
import util.Action;
import util.Actions;
import util.NamedTag;
import util.Tag;
import util.UnhandledCaseException;

public abstract class MergeEngine
		<O, T extends OpTerm<O,T,V>, V extends Value<T,V>>
		extends ConstructEngine<O,T,V> {
	private final Tag<Action<? super AxiomInstance<O,T,V>>> mMergeActionTag =
			new NamedTag<Action<? super AxiomInstance<O,T,V>>>("Merge Action");

	protected Action<? super AxiomInstance<O,T,V>> setupMergeAction(
			MergeNode node) {
		final FutureValueFunction<O,T,V> left
				= processExtendedValueNode(node.getLeft());
		final FutureValueFunction<O,T,V> right
				= processExtendedValueNode(node.getRight());
		return new Action<AxiomInstance<O,T,V>>() {
			public void execute(AxiomInstance<O,T,V> parameter) {
				getEGraph().makeEqual(left.getValue(parameter),
						right.getValue(parameter), parameter.getProof());
			}
		};
	}
	
	protected Action<? super AxiomInstance<O,T,V>> setupPostpendMergeAction(
			PostpendNode<? extends MergeNode> node) {
		return Actions.sequence(processMergeListNode(node.getHead()),
				processMergeNode(node.getTail()));
	}

	protected Action<? super AxiomInstance<O,T,V>> setupMergeListAction(
			ListNode<? extends MergeNode> node) {
		if (node.isEmpty())
			return this.<AxiomInstance<O,T,V>>setupEmptyAction(node.getEmpty());
		else if (node.isPostpend())
			return setupPostpendMergeAction(node.getPostpend());
		else
			return null;
	}

	protected final Action<? super AxiomInstance<O,T,V>> processMergeNode(
			MergeNode node) {
		if (node.hasTag(mMergeActionTag))
			return node.getTag(mMergeActionTag);
		Action<? super AxiomInstance<O,T,V>> action = setupMergeAction(node);
		if (action == null)
			throw new UnhandledCaseException(node);
		node.setTag(mMergeActionTag, action);
		return action;
	}
	
	protected final Action<? super AxiomInstance<O,T,V>> processMergeListNode(
			ListNode<? extends MergeNode> node) {
		if (node.hasTag(mMergeActionTag))
			return node.getTag(mMergeActionTag);
		Action<? super AxiomInstance<O,T,V>> action
				= setupMergeListAction(node);
		if (action == null)
			throw new UnhandledCaseException(node);
		node.setTag(mMergeActionTag, action);
		return action;
	}
}
