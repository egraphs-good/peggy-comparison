package eqsat.meminfer.network.op;

import java.util.HashMap;
import java.util.Map;

import eqsat.meminfer.network.basic.MassSourceStructurizer;
import eqsat.meminfer.network.op.ExpressionNetwork.ExpressionNode;
import util.Taggable;
import util.graph.OrderedVertex;

public abstract class Expressionizer<L, O,
		V extends Taggable & OrderedVertex<?,? extends V>>
		extends MassSourceStructurizer<V> {
	public abstract ExpressionNetwork<O> getNetwork();
	protected abstract LabelAmbassador<L,O> getAmbassador();
	
	protected abstract L getOperator(V vertex);
	
	public ExpressionNode<O> getExpression() {
		ExpressionNode<O> expression
				= getNetwork().adaptStructure(getStructure());
		Map<L,V> chosen = new HashMap<L,V>();
		for (V vertex : getVertices()) {
			L label = getOperator(vertex);
			if (chosen.containsKey(label))
				expression = getNetwork().opsEqual(
						getTermValue(chosen.get(label)),
						getTermValue(vertex), expression);
			else {
				chosen.put(label, vertex);
				expression = constrainOperator(vertex, expression);
			}
		}
		return expression;
	}
	
	protected ExpressionNode<O> constrainOperator(V vertex,
			ExpressionNode<O> expression) {
		L label = getOperator(vertex);
		if (getAmbassador().isConcrete(label))
			return getNetwork().opEquals(getTermValue(vertex),
					getAmbassador().getConcrete(label), expression);
		else
			return expression;
	}
}