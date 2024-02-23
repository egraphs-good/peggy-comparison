package eqsat.meminfer.engine.op.axiom;

import java.util.ArrayList;
import java.util.List;

import eqsat.meminfer.engine.basic.FutureAmbassador;
import eqsat.meminfer.engine.basic.FutureExpression;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.basic.Value;
import eqsat.meminfer.engine.op.OpTerm;
import eqsat.meminfer.engine.proof.Proof;
import util.Action;

public final class AxiomInstance
		<O, T extends OpTerm<O,T,V>, V extends Value<T,V>> {
	private final Structure<T> mTrigger;
	private final List<O> mOps = new ArrayList<O>();
	private final List<Action<T>> mOpConstraints = new ArrayList<Action<T>>();
	private final FutureExpressionGraph<O,T,V> mGraph
			= new FutureExpressionGraph<O,T,V>();
	private final FutureAmbassador<O,T,V>[] mPlaceHolders;
	private final List<FutureExpression<O,T,V>> mConstructs = new ArrayList();
	private Proof mProof;
	
	public AxiomInstance(String name, Structure<T> trigger, int placeHolders) {
		mProof = new Proof(name);
		mTrigger = trigger;
		mPlaceHolders = new FutureAmbassador[placeHolders];
		for (int i = 0; i < placeHolders; i++)
			mPlaceHolders[i] = mGraph.makePlaceHolder();
	}
	
	public Structure<T> getTrigger() {return mTrigger;}
	public O getOp(int index) {return mOps.get(index);}
	public FutureExpressionGraph<O,T,V> getGraph() {return mGraph;}
	public FutureAmbassador<O,T,V> getPlaceHolder(int index) {
		return mPlaceHolders[index];
	}
	public FutureExpression<O,T,V> getConstruct(int index) {
		return mConstructs.get(index);
	}
	
	public void addOp(O op, Action<T> constrainOp) {
		mOps.add(op);
		mOpConstraints.add(constrainOp);
	}
	public void addConstruct(FutureExpression<O,T,V> vertex) {
		mConstructs.add(vertex);
	}
	
	public Proof getProof() {return mProof;}
	public void setProof(Proof proof) {
		if (mProof != null)
			throw new IllegalStateException();
		mProof = proof;
	}
	public void constrainOp(int op, T construct) {
		mOpConstraints.get(op).execute(construct);
	}
}
