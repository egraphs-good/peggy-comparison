package eqsat.meminfer.network.op.axiom;

import java.util.ArrayList;
import java.util.List;

import eqsat.meminfer.network.Network.ListNode;
import eqsat.meminfer.network.Network.Node;
import eqsat.meminfer.network.basic.axiom.SourceMergeMaker;
import eqsat.meminfer.network.op.LabelAmbassador;
import eqsat.meminfer.network.op.axiom.AddOpNetwork.AddExistingOpNode;
import eqsat.meminfer.network.op.axiom.AddOpNetwork.AddNewOpNode;
import eqsat.meminfer.network.op.axiom.ConstructNetwork.OpNode;
import util.Taggable;
import util.UnhandledCaseException;
import util.graph.OrderedVertex;

public abstract class OpMaker<L, O, A extends Node,
		V extends Taggable & OrderedVertex<?,? extends V>>
		extends SourceMergeMaker<V> {
	private final List<L> mOps = new ArrayList<L>();
	
	public abstract AddOpNetwork<O> getNetwork();
	protected abstract LabelAmbassador<L,O> getAmbassador();
	
	protected abstract L getOperator(V vertex);
	
	protected int addOperator(L op) {
		mOps.add(op);
		return mOps.size() - 1;
	}

	protected OpNode getOpNode(V vertex) {
		L op = getOperator(vertex);
		int index = mOps.indexOf(op);
		if (index == -1)
			index = addOperator(op);
		return getNetwork().op(index);
	}
	
	protected A getAddOpNode(L op) {
		for (V vertex : getStructurizer().getVertices())
			if (op == null ? getOperator(vertex) == null
					: op.equals(getOperator(vertex)))
				return getAddExistingOpNode(vertex);
		return getAddNewOpNode(op);
	}
	
	protected abstract A convertAddExistingOpNode(AddExistingOpNode node);
	protected abstract A convertAddNewOpNode(AddNewOpNode<O> node);
	
	protected A getAddExistingOpNode(V vertex) {
		return convertAddExistingOpNode(getNetwork().addExistingOp(
				getStructurizer().getTermValue(vertex)));
	}

	protected A getAddNewOpNode(L op) {
		if (getAmbassador().isConcrete(op))
			return convertAddNewOpNode(getNetwork().addNewOp(
					getAmbassador().getConcrete(op)));
		throw new UnhandledCaseException();
	}

	public ListNode<? extends A> getAddOps() {
		ListNode<? extends A> adds = getNetwork().empty();
		for (L op : mOps)
			adds = getNetwork().postpend(adds, getAddOpNode(op));
		return adds;
	}
}
