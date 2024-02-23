package eqsat.meminfer.engine.op.axiom;

import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.basic.Value;
import eqsat.meminfer.engine.basic.axiom.MergeEngine;
import eqsat.meminfer.engine.op.OpTerm;
import eqsat.meminfer.engine.proof.OpIs;
import eqsat.meminfer.engine.proof.OpsEqual;
import eqsat.meminfer.network.op.axiom.AddOpNetwork.AddExistingOpNode;
import eqsat.meminfer.network.op.axiom.AddOpNetwork.AddNewOpNode;
import util.Action;
import util.Function;
import util.NamedTag;
import util.Tag;
import util.UnhandledCaseException;

public abstract class OpEngine<O, T extends OpTerm<O,T,V>, V extends Value<T,V>>
		extends MergeEngine<O,T,V> {
	protected final Tag<Action<? super AxiomInstance<O,T,V>>> mSetupActionTag
			= new NamedTag("Setup Action");

	protected Action<? super AxiomInstance<O,T,V>> setupAddExistingOpAction(
			AddExistingOpNode node) {
		final Function<? super Structure<T>,? extends T> term
				= getEGraph().processTermValueNode(node.getTerm());
		return new Action<AxiomInstance<O,T,V>>() {
			public void execute(final AxiomInstance<O,T,V> parameter) {
				parameter.addOp(term.get(parameter.getTrigger()).getOp(),
						new Action<T>() {
					public void execute(T construct) {
						parameter.getProof().addProperty(new OpsEqual<O,T>(
								term.get(parameter.getTrigger()), construct));
					}
				});
			}
		};
	}

	protected Action<? super AxiomInstance<O,T,V>> setupAddNewOpAction(
			AddNewOpNode<O> node) {
		final O op = node.getOp();
		return new Action<AxiomInstance<O,T,V>>() {
			public void execute(final AxiomInstance<O,T,V> parameter) {
				parameter.addOp(op, new Action<T>() {
					public void execute(T construct) {
						parameter.getProof().addProperty(
								new OpIs<O,T>(construct, op));
					}
				});
			}
		};
	}

	protected final Action<? super AxiomInstance<O,T,V>>
			processAddExistingOpNode(AddExistingOpNode node) {
		if (node.hasTag(mSetupActionTag))
			return node.getTag(mSetupActionTag);
		Action<? super AxiomInstance<O,T,V>> action
				= setupAddExistingOpAction(node);
		if (action == null)
			throw new UnhandledCaseException(node);
		node.setTag(mSetupActionTag, action);
		return action;
	}

	protected final Action<? super AxiomInstance<O,T,V>>
			processAddExistingOpNode(AddNewOpNode<O> node) {
		if (node.hasTag(mSetupActionTag))
			return node.getTag(mSetupActionTag);
		Action<? super AxiomInstance<O,T,V>> action
				= setupAddNewOpAction(node);
		if (action == null)
			throw new UnhandledCaseException(node);
		node.setTag(mSetupActionTag, action);
		return action;
	}
}
