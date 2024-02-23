package eqsat.meminfer.network.peg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import eqsat.FlowValue;
import eqsat.meminfer.network.basic.TermValueNetwork.TermValueNode;
import eqsat.meminfer.network.op.Expressionizer;
import eqsat.meminfer.network.op.ExpressionNetwork.ExpressionNode;
import eqsat.meminfer.network.peg.PEGNetwork.PEGNode;
import util.Labeled;
import util.NamedTag;
import util.Tag;
import util.Taggable;
import util.graph.OrderedVertex;

public abstract class PEGExpressionizer<L, D, O,
		V extends Taggable & OrderedVertex<?,? extends V>
				& Labeled<? extends L>>
		extends Expressionizer<L, FlowValue<?,O>,V> {
	private final Set<V> mExtendedDomain = new HashSet<V>();
	private final Set<V> mLoops = new HashSet<V>();
	private final Set<V> mToLoopLift = new HashSet<V>();
	
	public abstract PEGNetwork<O> getNetwork();
	protected abstract PEGLabelAmbassador<L,D,O> getAmbassador();
	
	protected abstract boolean mustBeInvariant(V vertex, D depth);
	
	protected ExpressionNode<FlowValue<?,O>> constrainOperator(V vertex,
			ExpressionNode<FlowValue<?,O>> expression) {
		L label = getOperator(vertex);
		if (getAmbassador().isConcrete(label))
			return super.constrainOperator(vertex, expression);
		if (getAmbassador().mustBeExtendedDomain(label))
			mExtendedDomain.add(vertex);
		else if (getAmbassador().isLoopOp(label))
			mLoops.add(vertex);
		else
			mToLoopLift.add(vertex);
		return super.constrainOperator(vertex, expression);
	}
	
	public PEGNode<O> getPEGExpression() {
		PEGNode<O> expression
				= getNetwork().adaptExpression(getExpression());
		for (V vertex : mExtendedDomain)
			expression = getNetwork().opIsExtendedDomainOp(getTermValue(vertex),
					expression);
		Map<D,V> chosen = new HashMap<D,V>();
		for (V vertex : mLoops) {
			L label = getOperator(vertex);
			TermValueNode term = getTermValue(vertex);
			expression = getNetwork().opIsLoopOp(term,
					getAmbassador().getLoopOp(label), expression);
			D depth = getAmbassador().getLoopDepth(label);
			if (chosen.containsKey(depth))
				expression = getNetwork().checkEqualLoopDepths(
						getNetwork().opLoop(getTermValue(chosen.get(depth))),
						getNetwork().opLoop(term),
						expression);
			else {
				for (Entry<D,V> entry : chosen.entrySet())
					if (getAmbassador().mustBeDistinctLoops(
							entry.getKey(), depth))
						expression = getNetwork().checkDistinctLoopDepths(
								getNetwork().opLoop(
										getTermValue(entry.getValue())),
								getNetwork().opLoop(term),
								expression);
				chosen.put(depth, vertex);
			}
		}
		for (V vertex : mToLoopLift) {
			L label = getOperator(vertex);
			TermValueNode term = getTermValue(vertex);
			for (Entry<D,V> entry : chosen.entrySet())
				if (getAmbassador().mustBeLoopLifted(label, entry.getKey()))
					expression = getNetwork().opIsLoopLifted(term,
							getNetwork().opLoop(getTermValue(entry.getValue())),
							expression);
		}
		Tag<Void> processed = new NamedTag<Void>("Processed");
		for (V vertex : getVertices()) {
			expression = constrainInvariance(vertex, chosen, expression);
			for (V child : vertex.getChildren())
				if (!child.hasTag(processed) && !isAdded(child)) {
					expression = constrainInvariance(child, chosen, expression);
					child.setTag(processed);
				}
		}
		return expression;
	}
	
	private PEGNode<O> constrainInvariance(V vertex, Map<D,V> chosen,
			PEGNode<O> expression) {
		for (Entry<D,V> entry : chosen.entrySet())
			if (mustBeInvariant(vertex, entry.getKey()))
				expression = getNetwork().isInvariant(getValue(vertex),
						getNetwork().opLoop(getTermValue(entry.getValue())),
						expression);
		return expression;
	}
}
