package eqsat.meminfer.engine.basic;

import eqsat.meminfer.engine.basic.FutureExpressionGraph.Label;
import eqsat.meminfer.engine.basic.FutureExpressionGraph.Vertex;
import eqsat.meminfer.engine.op.OpTerm;
import util.graph.ExpressionGraph;

public class FutureAmbassador<O,T extends OpTerm<O,T,V>,V extends Value<T,V>>
		extends ExpressionGraph.Vertex
		<FutureExpressionGraph<O,T,V>,Vertex<O,T,V>,Label<O,T,V>>
		implements Vertex<O,T,V> {
	private final FutureExpressionGraph<O,T,V> mGraph;
	private FutureExpression<O,T,V> mIntendedExpression;
	private Ambassador<T,V> mAmbassador;
	
	protected FutureAmbassador(FutureExpressionGraph<O,T,V> graph,
			Label<O,T,V> label) {
		super(label);
		if (!label.isPlaceHolder())
			throw new IllegalArgumentException();
		mGraph = graph;
	}

	public FutureExpressionGraph<O,T,V> getGraph() {return mGraph;}
	public Vertex<O,T,V> getSelf() {return this;}

	public boolean isRepresentative() {return false;}
	public Representative<V> getRepresentative() {
		throw new UnsupportedOperationException();
	}
	public boolean isFutureAmbassador() {return true;}
	public FutureAmbassador<O,T,V> getFutureAmbassador() {return this;}
	public boolean isFutureExpression() {return false;}
	public FutureExpression<O,T,V> getFutureExpression() {
		throw new UnsupportedOperationException();
	}
	
	public Representative<V> getValue() {return mAmbassador;}
	
	public FutureExpression<O,T,V> getIntendedExpression() {
		return mIntendedExpression;
	}
	public void setIntendedExpression(
			FutureExpression<O,T,V> intendedExpression) {
		if (mIntendedExpression != null)
			throw new IllegalStateException();
		if (!intendedExpression.getFutureValue().equals(this))
			throw new IllegalArgumentException();
		mIntendedExpression = intendedExpression;
	}
	
	public Ambassador<T,V> getAmbassador() {return mAmbassador;}
	public void setAmbassador(Ambassador<T,V> ambassador) {
		if (mAmbassador != null)
			throw new IllegalStateException();
		if (!ambassador.getValue().getTerms().isEmpty())
			throw new IllegalArgumentException();
		mAmbassador = ambassador;
	}
}
