package eqsat.meminfer.peggy.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.Representative;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.basic.TermOrTermChild;
import eqsat.meminfer.engine.event.ConvertEvent;
import eqsat.meminfer.engine.event.Event;
import eqsat.meminfer.engine.event.EventListener;
import eqsat.meminfer.engine.event.ProofEvent;
import eqsat.meminfer.engine.event.ProofPatternEvent;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.peg.EPEGManager;
import eqsat.meminfer.engine.proof.AreEquivalent;
import eqsat.meminfer.engine.proof.ChildIsEquivalentTo;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.peggy.axiom.PeggyAxiomSetup;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyVertex;
import util.Function;
import util.Labeled;
import util.NamedTag;
import util.Tag;
import util.Taggable;
import util.UnhandledCaseException;
import util.WrappingArrayList;
import util.graph.OrderedVertex;
import util.graph.RecursiveExpressionGraph;
import util.integer.ArrayIntMap;
import util.integer.Bit32IntSet;
import util.integer.IntMap;
import util.integer.IntSet;
import util.pair.ArrayPairedList;
import util.pair.Pair;
import util.pair.PairedList;

public final class PeggyWatcher<O,P> {
	public static <O, P, V extends
			Labeled<? extends FlowValue<P,O>> & OrderedVertex<?,V> & Taggable>
			Event<Pair<TermOrTermChild<CPEGTerm<O,P>,CPEGValue<O,P>>,Proof>>
			watchForExpression(final PeggyAxiomSetup<O,P> setup, V expression,
			final Map<? super P,? extends CPEGTerm<O,P>> parameters) {
		final PeggyAxiomizer<O,Integer> axiomizer
				= setup.<Integer>createAxiomizer(null);
		final PairedList<P,PeggyVertex<O,Integer>> params
				= new ArrayPairedList<P,PeggyVertex<O,Integer>>();
		final IntMap<PeggyVertex<O,Integer>> loops = new ArrayIntMap();
		PeggyVertex<O,Integer> watch
				= new Function<V,PeggyVertex<O,Integer>>() {
			final Tag<PeggyVertex<O,Integer>> mTag = new NamedTag("Converted");
			final Map<P,Integer> mParams = new HashMap<P,Integer>();
			int mNextParam = 0;
			final int mTrue = -1, mFalse = -2;
			public PeggyVertex<O,Integer> get(V expression) {
				PeggyVertex<O,Integer> vertex;
				if (expression.hasTag(mTag)) {
					vertex = expression.getTag(mTag);
					if (vertex == null) {
						vertex = axiomizer.createPlaceHolder();
						expression.setTag(mTag, vertex);
					}
					return vertex;
				}
				expression.setTag(mTag, null);
				FlowValue<P,O> op = expression.getLabel();
				if (op.isParameter()) {
					if (!mParams.containsKey(op.getParameter()))
						mParams.put(op.getParameter(), mNextParam++);
					vertex = axiomizer.getVariable(
							mParams.get(op.getParameter()));
					params.add(op.getParameter(), vertex);
				} else if (op.isTrue()) {
					vertex = axiomizer.getVariable(mTrue);
					axiomizer.makeTrue(vertex);
				} else if (op.isFalse()) {
					vertex = axiomizer.getVariable(mFalse);
					axiomizer.makeFalse(vertex);
				} else if (op.isNegate())
					vertex = axiomizer.getNegate(get(expression.getChild(0)));
				else if (op.isExtendedDomain()) {
					O domainOp = op.getDomain(setup.getOpAmbassador());
					PeggyVertex<O,Integer>[] children
							= new PeggyVertex[expression.getChildCount()];
					for (int i = 0; i < children.length; i++)
						children[i] = get(expression.getChild(i));
					vertex = axiomizer.get(domainOp, children);
				} else if (op.isPhi())
					vertex = axiomizer.getPhi(get(expression.getChild(0)),
							get(expression.getChild(1)),
							get(expression.getChild(2)));
				else if (op.isLoopFunction()) {
					int loop = op.getLoopDepth();
					if (op.isTheta())
						vertex = axiomizer.getTheta(loop,
								get(expression.getChild(0)),
								get(expression.getChild(1)));
					else if (op.isShift())
						vertex = axiomizer.getShift(loop,
								get(expression.getChild(0)));
					else if (op.isEval())
						vertex = axiomizer.getEval(loop,
								get(expression.getChild(0)),
								get(expression.getChild(1)));
					else if (op.isPass())
						vertex = axiomizer.getPass(loop,
								get(expression.getChild(0)));
					else
						throw new UnhandledCaseException();
					loops.put(loop, vertex);
				} else
					throw new UnhandledCaseException();
				if (expression.getTag(mTag) != null)
					expression.getTag(mTag).replaceWith(vertex);
				expression.setTag(mTag, vertex);
				return vertex;
			}
		}.get(expression);
		for (int loop : loops.keySet())
			for (int other : loops.keySet())
				if (loop != other)
					axiomizer.mustBeDistinctLoops(loop, other);
		axiomizer.mustExist(watch);
		final EPEGManager<O,P,CPEGTerm<O,P>,CPEGValue<O,P>> egraph
				= setup.getEngine().getEGraph();
		ProofEvent<CPEGTerm<O,P>,? extends Structure<CPEGTerm<O,P>>> event
				= egraph.processPEGNode(axiomizer.getTrigger());
		for (final int loop : loops.keySet()) {
			final Function<? super Structure<CPEGTerm<O,P>>,
					? extends CPEGTerm<O,P>> termFunction
					= egraph.processTermValueNode(
					axiomizer.getStructurizer().getTermValue(loops.get(loop)));
			event = new ProofPatternEvent
					<CPEGTerm<O,P>,Structure<CPEGTerm<O,P>>>(event) {
				protected boolean canMatch(Structure<CPEGTerm<O,P>> pattern) {
					CPEGTerm<O,P> term = termFunction.get(pattern);
					return term == null || term.getOp().getLoopDepth() == loop;
				}
				protected boolean matches(Structure<CPEGTerm<O,P>> pattern) {
					return canMatch(pattern);
				}
				protected void addConstraints(Structure<CPEGTerm<O,P>> result,
						Proof proof) {
					// TODO Auto-generated method stub
				}
			};
		}
		for (int i = 0; i < params.size(); i++) {
			final P param = params.getFirst(i);
			final Function<? super Structure<CPEGTerm<O,P>>,
					? extends TermOrTermChild<CPEGTerm<O,P>,CPEGValue<O,P>>>
					termFunction = egraph.processValueNode(
					axiomizer.getStructurizer().getValue(
					params.getSecond(i)));
			event = new ProofPatternEvent
					<CPEGTerm<O,P>,Structure<CPEGTerm<O,P>>>(event) {
				protected boolean canMatch(Structure<CPEGTerm<O,P>> pattern) {
					Representative<CPEGValue<O,P>> term
							= termFunction.get(pattern).getRepresentative();
					return term == null
							|| egraph.canEqual(parameters.get(param), term);
				}
				protected boolean matches(Structure<CPEGTerm<O,P>> pattern) {
					Representative<CPEGValue<O,P>> term
							= termFunction.get(pattern).getRepresentative();
					return egraph.watchEquality(parameters.get(param), term,
							this, pattern);
				}
				protected void addConstraints(Structure<CPEGTerm<O,P>> result,
						Proof proof) {
					TermOrTermChild<CPEGTerm<O,P>,CPEGValue<O,P>> term
							= termFunction.get(result);
					if (term.isTerm())
						proof.addProperty(
								new AreEquivalent<CPEGTerm<O,P>,CPEGValue<O,P>>(
										parameters.get(param), term.getTerm()));
					else if (term.isTermChild())
						proof.addProperty(new ChildIsEquivalentTo
								<CPEGTerm<O,P>,CPEGValue<O,P>>(
								term.getParentTerm(), term.getChildIndex(),
								parameters.get(param)));
				}
			};
		}
		final Function<? super Structure<CPEGTerm<O,P>>,
				? extends TermOrTermChild<CPEGTerm<O,P>,CPEGValue<O,P>>>
				watchFunction = egraph.processValueNode(
				axiomizer.getStructurizer().getValue(watch));
		final ProofEvent<CPEGTerm<O,P>,? extends Structure<CPEGTerm<O,P>>>
				expressionEvent = event;
		ConvertEvent<Structure<CPEGTerm<O,P>>,
				Pair<TermOrTermChild<CPEGTerm<O,P>,CPEGValue<O,P>>,Proof>>
				convert = new ConvertEvent<Structure<CPEGTerm<O,P>>,
				Pair<TermOrTermChild<CPEGTerm<O,P>,CPEGValue<O,P>>,Proof>>() {
			protected boolean mayConvert(Structure<CPEGTerm<O,P>> source) {
				return true;
			}
			protected boolean canConvert(Structure<CPEGTerm<O,P>> source) {
				return source.isComplete();
			}
			protected Pair<TermOrTermChild<CPEGTerm<O,P>,CPEGValue<O,P>>,Proof>
					convert(Structure<CPEGTerm<O,P>> source) {
				Proof proof = new Proof("Match Expression");
				expressionEvent.generateProof(source, proof);
				return new Pair<TermOrTermChild<CPEGTerm<O,P>,CPEGValue<O,P>>,
						Proof>(watchFunction.get(source), proof);
			}
		};
		expressionEvent.addListener(convert);
		return convert;
	}
	
	private final List<CPEGTerm<O,P>> mInputs;
	private final CPEGTerm<O,P>[] mExpected;
	private final Proof[] mExpectedProofs;
	private final IntSet mMissing;
	
	public <V extends Labeled<FlowValue<P,O>> & OrderedVertex<?,V> & Taggable>
			PeggyWatcher(PeggyAxiomSetup<O,P> setup,
			RecursiveExpressionGraph<?,V,FlowValue<P,O>> graph,
			List<? extends V> inputs, Map<P,? extends V> parameters,
			List<? extends V> expected) {
		final EPEGManager<O,P,CPEGTerm<O,P>,CPEGValue<O,P>> egraph
				= setup.getEngine().getEGraph();
		mExpected = new CPEGTerm[expected.size()];
		mExpectedProofs = new Proof[expected.size()];
		mMissing = new Bit32IntSet();
		Map<P,CPEGTerm<O,P>> params = new HashMap();
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null
					|| expected.get(i).getLabel().isParameter())
				continue;
			final int index = i;
			mMissing.add(index);
			PeggyWatcher.<O,P,V>watchForExpression(
					setup, expected.get(i), params).addListener(
					new EventListener
					<Pair<TermOrTermChild<CPEGTerm<O,P>,CPEGValue<O,P>>,
					Proof>>() {
				public boolean canUse(
						Pair<TermOrTermChild<CPEGTerm<O,P>,CPEGValue<O,P>>,
						Proof> parameter) {
					return true;
				}
				public boolean notify(
						Pair<TermOrTermChild<CPEGTerm<O,P>,CPEGValue<O,P>>,
						Proof> parameter) {
					if (egraph.watchEquality(
							parameter.getFirst().getRepresentative(),
							mInputs.get(index), this, parameter)) {
						mExpected[index] = parameter.getFirst().getTerm();
						mExpectedProofs[index] = parameter.getSecond();
						mMissing.removeInt(index);
						return false;
					} else
						return true;
				}
			});
		}
		List<V> expressions = new ArrayList(inputs);
		for (P param : parameters.keySet())
			expressions.add(parameters.get(param));
		List<CPEGTerm<O,P>> terms = new WrappingArrayList<CPEGTerm<O,P>>(
				setup.getEngine().addExpressions(inputs));
		mInputs = new ArrayList<CPEGTerm<O,P>>(terms.subList(0, inputs.size()));
		terms.subList(0, inputs.size()).clear();
		for (P param : parameters.keySet())
			params.put(param, terms.remove(0));
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) == null
					|| !expected.get(i).getLabel().isParameter())
				continue;
			final int index = i;
			mExpected[index]
					= params.get(expected.get(index).getLabel().getParameter());
			mExpectedProofs[index] = new Proof("Reflexivity");
			mMissing.add(index);
			EventListener<Void> listener = new EventListener<Void>() {
				public boolean canUse(Void parameter) {return true;}
				public boolean notify(Void parameter) {
					mMissing.removeInt(index);
					return false;
				}
			};
			if (egraph.watchEquality(mInputs.get(index), mExpected[index],
					listener, null))
				listener.notify(null);
		}
	}
	
	public boolean matched() {return mMissing.isEmpty();}
	public CPEGTerm<O,P> getInputTerm(int index) {
		return mInputs.get(index);
	}
	public CPEGTerm<O,P> getExpectedTerm(int index) {return mExpected[index];}
	public Proof getExpectedMatchProof(int index) {
		return mExpectedProofs[index];
	}
}
