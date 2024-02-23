package eqsat.meminfer.engine.peg.axiom;

import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.op.axiom.AxiomInstance;
import eqsat.meminfer.engine.op.axiom.OpEngine;
import eqsat.meminfer.engine.peg.PEGTerm;
import eqsat.meminfer.engine.peg.PEGValue;
import eqsat.meminfer.engine.proof.OpIsLoopOp;
import eqsat.meminfer.engine.proof.OpIsSameLoop;
import eqsat.meminfer.network.peg.PEGNetwork.PEGLoopOp;
import eqsat.meminfer.network.peg.axiom.AddPEGOpNetwork.AddLoopOpNode;
import util.Action;
import util.Function;
import util.UnhandledCaseException;

public abstract class PEGOpEngine<O, P,
		T extends PEGTerm<O,P,T,V>, V extends PEGValue<T,V>>
		extends OpEngine<FlowValue<P,O>,T,V> {
	protected Action<? super AxiomInstance<FlowValue<P,O>,T,V>>
			setupAddLoopOpAction(AddLoopOpNode node) {
		final PEGLoopOp op = node.getOp();
		final Function<? super Structure<T>,? extends T> loop
				= getEGraph().processTermValueNode(node.getLoop());
		return new Action<AxiomInstance<FlowValue<P,O>,T,V>>() {
			public void execute(
					final AxiomInstance<FlowValue<P,O>,T,V> parameter) {
				parameter.addOp(op.<P,O>getFlowValue(
						loop.get(parameter.getTrigger())
						.getOp().getLoopDepth()), new Action<T>() {
					public void execute(T construct) {
						parameter.getProof().addProperty(
								new OpIsLoopOp<T>(construct, op));
						parameter.getProof().addProperty(new OpIsSameLoop<T>(
								loop.get(parameter.getTrigger()), construct));
					}
				});
			}
		};
	}

	protected final Action<? super AxiomInstance<FlowValue<P,O>,T,V>>
			processAddLoopOpNode(AddLoopOpNode node) {
		if (node.hasTag(mSetupActionTag))
			return node.getTag(mSetupActionTag);
		Action<? super AxiomInstance<FlowValue<P,O>,T,V>> action
				= setupAddLoopOpAction(node);
		if (action == null)
			throw new UnhandledCaseException(node);
		node.setTag(mSetupActionTag, action);
		return action;
	}
}
