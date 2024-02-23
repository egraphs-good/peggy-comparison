package eqsat.meminfer.engine.basic;

import eqsat.meminfer.engine.basic.FutureExpressionGraph.Label;
import eqsat.meminfer.engine.basic.FutureExpressionGraph.Vertex;
import eqsat.meminfer.engine.op.OpTerm;
import util.graph.ExpressionGraph;

public class FutureExpression<O,T extends OpTerm<O,T,V>,V extends Value<T,V>>
		extends ExpressionGraph.Vertex
		<FutureExpressionGraph<O,T,V>,Vertex<O,T,V>,Label<O,T,V>>
		implements Vertex<O,T,V> {
	private final FutureExpressionGraph<O,T,V> mGraph;
	private FutureAmbassador<O,T,V> mFutureValue;
	private T mTerm;
	
	protected FutureExpression(FutureExpressionGraph<O,T,V> graph,
			Label<O,T,V> label) {
		super(label);
		if (!label.isOp())
			throw new IllegalArgumentException();
		mGraph = graph;
	}
	protected FutureExpression(FutureExpressionGraph<O,T,V> graph,
			Label<O,T,V> label, Vertex<O,T,V> child) {
		super(label, child);
		if (!label.isOp())
			throw new IllegalArgumentException();
		mGraph = graph;
	}
	protected FutureExpression(FutureExpressionGraph<O,T,V> graph,
			Label<O,T,V> label, Vertex<O,T,V>... children) {
		super(label, children);
		if (!label.isOp())
			throw new IllegalArgumentException();
		mGraph = graph;
	}

	public FutureExpressionGraph<O,T,V> getGraph() {return mGraph;}
	public Vertex<O,T,V> getSelf() {return this;}
	
	public boolean isRepresentative() {return false;}
	public Representative<V> getRepresentative() {
		throw new UnsupportedOperationException();
	}
	public boolean isFutureAmbassador() {return false;}
	public FutureAmbassador<O,T,V> getFutureAmbassador() {
		throw new UnsupportedOperationException();
	}
	public boolean isFutureExpression() {return true;}
	public FutureExpression<O,T,V> getFutureExpression() {return this;}
	
	public Representative<V> getValue() {return mTerm;}
	
	public O getOp() {return getLabel().getOp();}

	public boolean hasFutureValue() {return mFutureValue != null;}
	public FutureAmbassador<O,T,V> getFutureValue() {return mFutureValue;}
	public void setFutureValue(FutureAmbassador<O,T,V> future) {
		mFutureValue = future;
	}
	
	public T getTerm() {return mTerm;}
	public void setTerm(T term) {
		if (mTerm != null)
			throw new IllegalStateException();
		if (!term.getOp().equals(getOp()) || term.getArity() != getChildCount())
			throw new IllegalArgumentException();
		for (int i = 0; i < getChildCount(); i++)
			if (!term.getChild(i).getValue().equals(
					getChild(i).getValue().getValue()))
				throw new IllegalArgumentException();
		mTerm = term;
	}
}
