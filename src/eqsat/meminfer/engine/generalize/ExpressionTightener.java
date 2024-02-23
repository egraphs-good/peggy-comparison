package eqsat.meminfer.engine.generalize;

import java.util.HashSet;
import java.util.Set;

import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.FutureAmbassador;
import eqsat.meminfer.engine.basic.FutureExpression;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.basic.TermChild;
import eqsat.meminfer.engine.basic.FutureExpressionGraph.Vertex;
import eqsat.meminfer.engine.peg.EPEGManager;
import eqsat.meminfer.engine.peg.FuturePEG;
import eqsat.meminfer.engine.peg.PEGTerm;
import eqsat.meminfer.engine.peg.PEGValue;
import eqsat.meminfer.engine.proof.ArityIs;
import eqsat.meminfer.engine.proof.ChildIsEquivalentTo;
import eqsat.meminfer.engine.proof.EquivalentChildren;
import eqsat.meminfer.engine.proof.OpIsDifferentLoop;
import eqsat.meminfer.engine.proof.OpIsLoopLifted;
import eqsat.meminfer.engine.proof.OpIsLoopOp;
import eqsat.meminfer.engine.proof.OpIsSameLoop;
import eqsat.meminfer.engine.proof.OpsEqual;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.network.peg.PEGNetwork.PEGLoopOp;
import util.Action;
import util.HashMultiMap;
import util.Labeled;
import util.MultiMap;
import util.NamedTag;
import util.Tag;
import util.Taggable;
import util.UnhandledCaseException;
import util.graph.Graph;
import util.graph.OrderedVertex;
import util.integer.ArrayIntMap;
import util.integer.IntMap;

public final class ExpressionTightener {
	public static <O, P, T extends PEGTerm<O,P,T,V>, V extends PEGValue<T,V>,
			E extends OrderedVertex<?,? extends E>
					& Labeled<? extends FlowValue<P,O>> & Taggable>
		Tag<? extends T> tighten(final EPEGManager<O,P,T,V> epeg,
				Graph<?,? extends E> peg, final Tag<? extends T> termTag) {
		final Tag<T> newTermTag = new NamedTag<T>("New Term");
		for (E expression : peg.getVertices()) {
			boolean exact = true;
			for (int i = 0; i < expression.getChildCount(); i++)
				exact &= expression.getChild(i).getTag(termTag).equals(
						expression.getTag(termTag).getChildAsTerm(i));
			if (exact)
				expression.setTag(newTermTag, expression.getTag(termTag));
		}
		boolean changed = true;
		while (changed) {
			changed = false;
			for (E expression : peg.getVertices())
				if (expression.hasTag(newTermTag))
					for (int i = 0; i < expression.getChildCount(); i++)
						if (!expression.getChild(i).hasTag(newTermTag)) {
							expression.removeTag(newTermTag);
							changed = true;
							break;
						}
		}
		final MultiMap<E,E> components = new HashMultiMap<E,E>();
		final Tag<E> componentTag = new NamedTag<E>("Component");
		for (final E expression : peg.getVertices())
			if (!expression.hasTag(newTermTag)
					&& expression.getLabel().isTheta()) {
				final Set<E> reached = new HashSet<E>();
				new Action<E>() {
					public void execute(E parameter) {
						if (!reached.add(parameter))
							return;
						if (parameter.equals(expression))
							return;
						for (E child : parameter.getChildren())
							execute(child);
					}
				}.execute(expression.getChild(1));
				new Action<E>() {
					public void execute(E parameter) {
						if (!reached.contains(parameter))
							return;
						E old = parameter.setTag(componentTag, expression);
						if (old != null && old.equals(expression))
							return;
						components.addValue(expression, parameter);
						for (E parent : parameter.getParents())
							execute(parent);
					}
				}.execute(expression);
			}
		final FuturePEG<O,P,T,V> future
				= new FuturePEG<O,P,T,V>(epeg.getOpAmbassador());
		final Tag<FutureExpression<FlowValue<P,O>,T,V>> futureTag
				= new NamedTag("Future Expression");
		Action<E> createFuture = new Action<E>() {
			final Tag<FutureAmbassador<FlowValue<P,O>,T,V>> mAmbassadorTag
					= new NamedTag("Future Ambassador");
			
			private FutureExpression<FlowValue<P,O>,T,V> convert(E parameter) {
				parameter.setTag(futureTag, null);
				FutureExpressionGraph.Vertex<FlowValue<P,O>,T,V>[] children
						= new FutureExpressionGraph.Vertex[
						parameter.getChildCount()];
				FutureExpression<FlowValue<P,O>,T,V> expression;
				FlowValue<P,O> label = parameter.getLabel();
				if (label.isTrue())
					expression = future.getTrue();
				else if (label.isFalse())
					expression = future.getFalse();
				else if (label.isNegate())
					expression
							= future.getNegate(get(parameter.getChild(0)));
				else if (label.isAnd())
					expression = future.getAnd(get(parameter.getChild(0)),
							get(parameter.getChild(1)));
				else if (label.isOr())
					expression = future.getOr(get(parameter.getChild(0)),
							get(parameter.getChild(1)));
				else if (label.isEquals())
					expression = future.getEquals(get(parameter.getChild(0)),
							get(parameter.getChild(1)));
				else if (label.isPhi())
					expression = future.getPhi(get(parameter.getChild(0)),
							get(parameter.getChild(1)),
							get(parameter.getChild(2)));
				else if (label.isShortCircuitAnd())
					expression = future.getPhi(get(parameter.getChild(0)),
							get(parameter.getChild(1)),
							future.getVertex(epeg.getFalse()));
				else if (label.isShortCircuitOr())
					expression = future.getPhi(get(parameter.getChild(0)),
							future.getVertex(epeg.getTrue()),
							get(parameter.getChild(1)));
				else if (label.isTheta())
					expression = future.getTheta(label.getLoopDepth(),
							get(parameter.getChild(0)),
							get(parameter.getChild(1)));
				else if (label.isEval())
					expression = future.getEval(label.getLoopDepth(),
							get(parameter.getChild(0)),
							get(parameter.getChild(1)));
				else if (label.isPass())
					expression = future.getPass(label.getLoopDepth(),
							get(parameter.getChild(0)));
				else if (label.isParameter())
					expression = future.getParameter(label.getParameter());
				else if (label.isDomain()) {
					for (int child = 0; child < children.length; child++)
						children[child] = get(parameter.getChild(child));
					expression = future.getDomain(label.getDomain(), children);
				} else
					throw new UnhandledCaseException();
				if (parameter.hasTag(mAmbassadorTag)) {
					expression.setFutureValue(parameter.getTag(mAmbassadorTag));
					parameter.removeTag(mAmbassadorTag)
							.setIntendedExpression(expression);
				}
				parameter.setTag(futureTag, expression);
				return expression;
			}
			
			public Vertex<FlowValue<P,O>,T,V> get(E parameter) {
				if (parameter.hasTag(mAmbassadorTag))
					return parameter.getTag(mAmbassadorTag);
				else if (parameter.hasTag(futureTag)) {
					if (parameter.getTag(futureTag) != null)
						return parameter.getTag(futureTag);
					else {
						FutureAmbassador<FlowValue<P,O>,T,V> ambassador
								= future.makePlaceHolder();
						parameter.setTag(mAmbassadorTag, ambassador);
						return ambassador;
					}
				} else if (parameter.hasTag(newTermTag))
					return future.getVertex(parameter.getTag(newTermTag));
				else
					return convert(parameter);
			}
			
			public void execute(E parameter) {
				get(parameter);
			}
		};
		for (E expression : peg.getVertices())
			createFuture.execute(expression);
		epeg.addRedundantExpressions(future);
		Action<E> createTerm = new Action<E>() {
			public void execute(E parameter) {
				if (parameter.hasTag(newTermTag))
					return;
				if (parameter.hasTag(componentTag)) {
					executeComponent(parameter.getTag(componentTag));
					return;
				}
				for (int i = 0; i < parameter.getChildCount(); i++)
					execute(parameter.getChild(i));
				T original = parameter.getTag(termTag);
				T tight = parameter.getTag(futureTag).getTerm();
				Proof proof;
				if (epeg.hasProofManager()) {
					proof = new Proof("Congruence",
							new ArityIs<T>(original, original.getArity()),
							new ArityIs<T>(tight, original.getArity()),
							new OpsEqual<FlowValue<P,O>,T>(original, tight));
					for (int i = 0; i < original.getArity(); i++)
						proof.addProperty(new EquivalentChildren<T,V>(
								original, i, tight, i));
				} else
					proof = null;
				epeg.makeEqual(original, tight, proof);
				parameter.setTag(newTermTag, tight);
				epeg.processEqualities();
			}
			
			public void executeComponent(E theta) {
				Set<E> component = components.get(theta);
				Proof proof = new Proof("Inductive Congruence");
				IntMap<T> depths = new ArrayIntMap<T>();
				for (E expression : component)
					if (expression.getLabel().isTheta() && !depths.containsKey(
							expression.getLabel().getLoopDepth())) {
						T term = expression.getTag(termTag);
						for (T depth : depths.values())
							proof.addProperty(
									new OpIsDifferentLoop<T>(term, depth));
						depths.put(expression.getLabel().getLoopDepth(), term);
					}
				for (E expression : component) {
					T original = expression.getTag(termTag);
					T tight = expression.getTag(futureTag).getTerm();
					proof.addProperties(
							new ArityIs<T>(original, original.getArity()),
							new ArityIs<T>(tight, original.getArity()),
							new OpsEqual<FlowValue<P,O>,T>(original, tight));
					for (int i = 0; i < expression.getChildCount(); i++)
						if (component.contains(expression.getChild(i)))
							proof.addProperties(
									new ChildIsEquivalentTo<T,V>(original, i,
											expression.getChild(i)
											.getTag(termTag)),
									new ChildIsEquivalentTo<T,V>(tight, i,
											expression.getChild(i)
											.getTag(futureTag).getTerm()));
						else {
							execute(expression.getChild(i));
							proof.addProperty(
									new EquivalentChildren<T,V>(original, i,
											tight, i));
						}
					if (expression.getLabel().isLoopFunction()
							&& depths.containsKey(
									expression.getLabel().getLoopDepth()))
						proof.addProperties(new OpIsLoopOp<T>(tight,
								PEGLoopOp.getLoopOp(expression.getLabel())),
								new OpIsSameLoop<T>(tight, depths.get(
										expression.getLabel().getLoopDepth())));
					else
						for (T depth : depths.values())
							proof.addProperty(
									new OpIsLoopLifted<T>(tight, depth));
				}
				for (E expression : component) {
					T original = expression.getTag(termTag);
					T tight = expression.getTag(futureTag).getTerm();
					epeg.makeEqual(original, tight, proof);
					expression.setTag(newTermTag, tight);
				}
				epeg.processEqualities();
			}
		};
		for (E expression : peg.getVertices())
			createTerm.execute(expression);
		return newTermTag;
	}
	
	public static <O, P, T extends PEGTerm<O,P,T,V>, V extends PEGValue<T,V>,
			E extends OrderedVertex<?,? extends E>
			& Labeled<? extends FlowValue<P,O>> & Taggable>
			int getTimeOfExpression(final EPEGManager<O,P,T,V> epeg,
			Graph<?,? extends E> peg, final Tag<? extends T> termTag) {
		int time = Integer.MIN_VALUE;
		for (E expression : peg.getVertices())
			for (int i = 0; i < expression.getChildCount(); i++)
				time = Math.max(time, epeg.getProofManager().getTimeOfEquality(
						new TermChild<T,V>(expression.getTag(termTag), i),
						expression.getChild(i).getTag(termTag)));
		return time;
	}
}
