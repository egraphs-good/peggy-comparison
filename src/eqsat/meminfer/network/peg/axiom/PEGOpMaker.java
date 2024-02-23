package eqsat.meminfer.network.peg.axiom;

import eqsat.FlowValue;
import eqsat.meminfer.network.Network.Node;
import eqsat.meminfer.network.op.axiom.OpMaker;
import eqsat.meminfer.network.peg.PEGLabelAmbassador;
import eqsat.meminfer.network.peg.axiom.AddPEGOpNetwork.AddLoopOpNode;
import util.Taggable;
import util.graph.OrderedVertex;

public abstract class PEGOpMaker<L, D, O, A extends Node,
		V extends Taggable & OrderedVertex<?,? extends V>>
		extends OpMaker<L,FlowValue<?,O>,A,V> {
	public abstract AddPEGOpNetwork<O> getNetwork();
	protected abstract PEGLabelAmbassador<L,D,O> getAmbassador();

	protected abstract A convertAddLoopOpNode(AddLoopOpNode node);
	
	protected A getAddNewOpNode(L op) {
		if (getAmbassador().isLoopOp(op)) {
			D depth = getAmbassador().getLoopDepth(op);
			for (V vertex : getStructurizer().getVertices())
				if (getAmbassador().isLoopOp(getOperator(vertex))
						&& getAmbassador().getLoopDepth(getOperator(vertex))
								.equals(depth))
					return convertAddLoopOpNode(getNetwork().addLoopOp(
							getAmbassador().getLoopOp(op),
							getStructurizer().getTermValue(vertex)));
			throw new IllegalArgumentException("Created loop op needs depth");
		} else
			return super.getAddNewOpNode(op);
	}
}
