package eqsat.meminfer.engine.peg;

import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.meminfer.engine.basic.FutureExpression;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.basic.Value;

public class FuturePEG<O, P, T extends PEGTerm<O,P,T,V>, V extends Value<T,V>>
		extends FutureExpressionGraph<FlowValue<P,O>,T,V> {
	private final OpAmbassador<O> mOpAmbassador;
	
	public FuturePEG(OpAmbassador<O> ambassador) {mOpAmbassador = ambassador;}
	
	public FutureExpression<FlowValue<P,O>,T,V> getParameter(P parameter) {
		return getExpression(FlowValue.<P,O>createParameter(parameter));
	}
	public FutureExpression<FlowValue<P,O>,T,V> getDomain(O op) {
		return getExpression(FlowValue.<P,O>createDomain(op, mOpAmbassador));
	}
	public FutureExpression<FlowValue<P,O>,T,V> getDomain(O op,
			Vertex<FlowValue<P,O>,T,V> child) {
		return getExpression(FlowValue.<P,O>createDomain(op, mOpAmbassador),
				child);
	}
	public FutureExpression<FlowValue<P,O>,T,V> getDomain(O op,
			Vertex<FlowValue<P,O>,T,V>... children) {
		return getExpression(FlowValue.<P,O>createDomain(op, mOpAmbassador),
				children);
	}

	public FutureExpression<FlowValue<P,O>,T,V> getTrue() {
		return getExpression(FlowValue.<P,O>createTrue());
	}
	public FutureExpression<FlowValue<P,O>,T,V> getFalse() {
		return getExpression(FlowValue.<P,O>createFalse());
	}
	public FutureExpression<FlowValue<P,O>,T,V> getNegate(
			Vertex<FlowValue<P,O>,T,V> child) {
		return getExpression(FlowValue.<P,O>createNegate(), child);
	}
	public FutureExpression<FlowValue<P,O>,T,V> getAnd(
			Vertex<FlowValue<P,O>,T,V> left, Vertex<FlowValue<P,O>,T,V> right) {
		return getExpression(FlowValue.<P,O>createAnd(), left, right);
	}
	public FutureExpression<FlowValue<P,O>,T,V> getOr(
			Vertex<FlowValue<P,O>,T,V> left, Vertex<FlowValue<P,O>,T,V> right) {
		return getExpression(FlowValue.<P,O>createOr(), left, right);
	}
	public FutureExpression<FlowValue<P,O>,T,V> getEquals(
			Vertex<FlowValue<P,O>,T,V> left, Vertex<FlowValue<P,O>,T,V> right) {
		return getExpression(FlowValue.<P,O>createEquals(), left, right);
	}
	public FutureExpression<FlowValue<P,O>,T,V> getPhi(
			Vertex<FlowValue<P,O>,T,V> condition,
			Vertex<FlowValue<P,O>,T,V> trueCase,
			Vertex<FlowValue<P,O>,T,V> falseCase) {
		return getExpression(FlowValue.<P,O>createPhi(),
				condition, trueCase, falseCase);
	}
	
	public FutureExpression<FlowValue<P,O>,T,V> getTheta(int depth,
			Vertex<FlowValue<P,O>,T,V> base,
			Vertex<FlowValue<P,O>,T,V> shift) {
		return getExpression(FlowValue.<P,O>createTheta(depth), base, shift);
	}
	public FutureExpression<FlowValue<P,O>,T,V> getShift(int depth,
			Vertex<FlowValue<P,O>,T,V> child) {
		return getExpression(FlowValue.<P,O>createShift(depth), child);
	}
	public FutureExpression<FlowValue<P,O>,T,V> getEval(int depth,
			Vertex<FlowValue<P,O>,T,V> value,
			Vertex<FlowValue<P,O>,T,V> index) {
		return getExpression(FlowValue.<P,O>createEval(depth), value, index);
	}
	public FutureExpression<FlowValue<P,O>,T,V> getPass(int depth,
			Vertex<FlowValue<P,O>,T,V> condition) {
		return getExpression(FlowValue.<P,O>createPass(depth), condition);
	}
}
