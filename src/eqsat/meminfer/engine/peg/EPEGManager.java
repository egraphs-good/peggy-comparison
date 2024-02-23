package eqsat.meminfer.engine.peg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.meminfer.engine.basic.Ambassador;
import eqsat.meminfer.engine.basic.FutureAmbassador;
import eqsat.meminfer.engine.basic.FutureExpression;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.basic.Representative;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.basic.TermChild;
import eqsat.meminfer.engine.basic.TermOrTermChild;
import eqsat.meminfer.engine.basic.ValueManager;
import eqsat.meminfer.engine.basic.FutureExpressionGraph.Vertex;
import eqsat.meminfer.engine.event.Event;
import eqsat.meminfer.engine.event.EventListener;
import eqsat.meminfer.engine.event.EventListenerClosure;
import eqsat.meminfer.engine.event.ProofEvent;
import eqsat.meminfer.engine.event.ProofPatternEvent;
import eqsat.meminfer.engine.op.OpEGraphManager;
import eqsat.meminfer.engine.proof.ArityIs;
import eqsat.meminfer.engine.proof.ChildIsEquivalentTo;
import eqsat.meminfer.engine.proof.ChildIsInvariant;
import eqsat.meminfer.engine.proof.IsInvariant;
import eqsat.meminfer.engine.proof.OpIs;
import eqsat.meminfer.engine.proof.OpIsAllLoopLifted;
import eqsat.meminfer.engine.proof.OpIsDifferentLoop;
import eqsat.meminfer.engine.proof.OpIsExtendedDomain;
import eqsat.meminfer.engine.proof.OpIsLoopLifted;
import eqsat.meminfer.engine.proof.OpIsLoopOp;
import eqsat.meminfer.engine.proof.OpIsSameLoop;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.network.peg.PEGNetwork.CheckDistinctLoopDepthsNode;
import eqsat.meminfer.network.peg.PEGNetwork.CheckEqualLoopDepthsNode;
import eqsat.meminfer.network.peg.PEGNetwork.IsInvariantNode;
import eqsat.meminfer.network.peg.PEGNetwork.LoopNode;
import eqsat.meminfer.network.peg.PEGNetwork.OpIsAllLoopLiftedNode;
import eqsat.meminfer.network.peg.PEGNetwork.OpIsExtendedDomainOpNode;
import eqsat.meminfer.network.peg.PEGNetwork.OpIsLoopLiftedNode;
import eqsat.meminfer.network.peg.PEGNetwork.OpIsLoopOpNode;
import eqsat.meminfer.network.peg.PEGNetwork.OpLoopNode;
import eqsat.meminfer.network.peg.PEGNetwork.PEGLoopOp;
import eqsat.meminfer.network.peg.PEGNetwork.PEGNode;
import util.Function;
import util.MultiMap;
import util.NamedTag;
import util.Tag;
import util.Triple;
import util.UnhandledCaseException;
import util.graph.Graphs;
import util.integer.ArrayIntMap;
import util.integer.Bit32IntSet;
import util.integer.IntMap;
import util.mapped.MappedList;

public abstract class
		EPEGManager<O, P, T extends PEGTerm<O,P,T,V>, V extends PEGValue<T,V>>
		extends OpEGraphManager<FlowValue<P,O>,T,V> {
	private final Tag<Function<? super Structure<T>,? extends T>>
			mLoopFunctionTag = new NamedTag("Loop Function");
	
	private final Tag<Integer> mInvarianceTag = new NamedTag("Invariance");
	private final IntMap<MultiMap<V,EventListener<Void>>> mWaiting
			= new ArrayIntMap<MultiMap<V,EventListener<Void>>>();
	
	protected EPEGManager(ValueManager<V> manager) {
		super(manager);
		manager.getPreMergeEvent().addListener(
				new EventListener<Triple<V,V,Event<Void>>>() {
			public boolean canUse(Triple<V,V,Event<Void>> parameter) {
				return true;
			}
			public boolean notify(Triple<V,V,Event<Void>> parameter) {
				final V left = parameter.getFirst();
				V right = parameter.getSecond();
				if (left.getInvariance() == right.getInvariance())
					return true;
				final int invariance
						= left.getInvariance() | right.getInvariance();
				left.setInvariance(
						left.getInvariance() & right.getInvariance());
				right.setInvariance(left.getInvariance());
				parameter.getThird().addListener(new EventListener<Void>() {
					public boolean canUse(Void parameter) {return true;}
					public boolean notify(Void parameter) {
						makeInvariants(left, invariance);
						return false;
					}
				});
				return true;
			}
			public String toString() {return "Merge Invariances";}
		});
		getRemoveTermEvent().addListener(new EventListener<T>() {
			public boolean canUse(T parameter) {return true;}
			public boolean notify(T parameter) {
				for (MultiMap<V,EventListener<Void>> map : mWaiting.values())
					for (Iterator<EventListener<Void>> waiting
							= map.values().iterator(); waiting.hasNext(); )
						if (!waiting.next().canUse(null))
							waiting.remove();
				return true;
			}
			public String toString() {return "Remove term from invariances";}
		});
	}
	
	public void constrainTrue(Proof proof) {
		proof.addProperties(new ArityIs<T>(getTrue(), 0),
				new OpIs<FlowValue<P,O>,T>(getTrue(),
						FlowValue.<P,O>createTrue()));
	}
	public void constrainFalse(Proof proof) {
		proof.addProperties(new ArityIs<T>(getFalse(), 0),
				new OpIs<FlowValue<P,O>,T>(getFalse(),
						FlowValue.<P,O>createFalse()));
	}

	public FutureExpression<FlowValue<P,O>,T,V> getTrueFuture(
			FutureExpressionGraph<FlowValue<P,O>,T,V> graph) {
		return graph.getExpression(FlowValue.<P,O>createTrue());
	}
	public FutureExpression<FlowValue<P,O>,T,V> getFalseFuture(
			FutureExpressionGraph<FlowValue<P,O>,T,V> graph) {
		return graph.getExpression(FlowValue.<P,O>createFalse());
	}
	
	public <E> boolean watchInvariance(int depth,
			Representative<V> representative,
			EventListener<? super E> listener, E parameter) {
		V value = representative.getValue();
		if (value.isInvariant(depth))
			return true;
		MultiMap<V,EventListener<Void>> waiting = mWaiting.get(depth);
		if (waiting == null) {
			waiting = getValueManager().createValueMultiMap();
			mWaiting.put(depth, waiting);
		}
		waiting.addValue(value,
				new EventListenerClosure<E>(listener, parameter));
		return false;
	}
	
	protected Function<? super Structure<T>,? extends T> setupOpLoopFunction(
			OpLoopNode node) {
		return processTermValueNode(node.getTermValue());
	}
	
	protected Function<? super Structure<T>,? extends T> setupLoopFunction(
			LoopNode node) {
		if (node.isOpLoop())
			return setupOpLoopFunction(node.getOpLoop());
		else
			return null;
	}
	
	protected ProofEvent<T,? extends Structure<T>>
			setupOpIsExtendedDomainOpEvent(OpIsExtendedDomainOpNode<O> node) {
		final Function<? super Structure<T>,? extends T> termFunction
				= processTermValueNode(node.getTerm());
		return new ProofPatternEvent<T,Structure<T>>(
				processPEGNode(node.getInput())) {
			protected boolean matches(Structure<T> pattern) {
				return !pattern.isRemoved()
						&& termFunction.get(pattern).getOp().isExtendedDomain();
			}
			protected boolean canMatch(Structure<T> pattern) {
				if (pattern.isRemoved())
					return false;
				T term = termFunction.get(pattern);
				return term == null || term.getOp().isExtendedDomain();
			}
			protected void addConstraints(Structure<T> result, Proof proof) {
				proof.addProperty(
						new OpIsExtendedDomain<T>(termFunction.get(result)));
			}
			public String toString() {
				return "Op of " + termFunction + " is Extended Domain"; 
			}
		};
	}
	
	protected ProofEvent<T,? extends Structure<T>> setupOpIsLoopOpEvent(
			OpIsLoopOpNode<O> node) {
		final Function<? super Structure<T>,? extends T> termFunction
				= processTermValueNode(node.getTerm());
		final PEGLoopOp op = node.getOp();
		return new ProofPatternEvent<T,Structure<T>>(
				processPEGNode(node.getInput())) {
			protected boolean matches(Structure<T> pattern) {
				return !pattern.isRemoved()
						&& op.isLoopOp(termFunction.get(pattern).getOp());
			}
			protected boolean canMatch(Structure<T> pattern) {
				if (pattern.isRemoved())
					return false;
				T term = termFunction.get(pattern);
				return term == null || op.isLoopOp(term.getOp());
			}
			protected void addConstraints(Structure<T> result, Proof proof) {
				proof.addProperty(
						new OpIsLoopOp<T>(termFunction.get(result), op));
			}
			public String toString() {
				return "Op of " + termFunction + " is " + op;
			}
		};
	}
	
	protected ProofEvent<T,? extends Structure<T>> setupOpIsLoopLiftedEvent(
			OpIsLoopLiftedNode<O> node) {
		final Function<? super Structure<T>,? extends T> termFunction
				= processTermValueNode(node.getTerm());
		final Function<? super Structure<T>,? extends T> loopFunction
				= processLoopNode(node.getLoop());
		return new ProofPatternEvent<T,Structure<T>>(
				processPEGNode(node.getInput())) {
			protected boolean matches(Structure<T> pattern) {
				return !pattern.isRemoved()
						&& termFunction.get(pattern).getOp().isLoopLiftedAll(
						loopFunction.get(pattern).getOp().getLoopDepth());
			}
			protected boolean canMatch(Structure<T> pattern) {
				if (pattern.isRemoved())
					return false;
				T term = termFunction.get(pattern);
				if (term == null)
					return true;
				T loop = loopFunction.get(pattern);
				if (loop == null)
					return true;
				return term.getOp().isLoopLiftedAll(
						loop.getOp().getLoopDepth());
			}
			protected void addConstraints(Structure<T> result, Proof proof) {
				proof.addProperty(
						new OpIsLoopLifted<T>(termFunction.get(result),
								loopFunction.get(result)));
			}
			public String toString() {
				return "Op of " + termFunction + " is Loop-Lifted w.r.t. "
						+ loopFunction;
			}
		};
	}
	
	protected ProofEvent<T,? extends Structure<T>> setupOpIsAllLoopLiftedEvent(
			OpIsAllLoopLiftedNode<O> node) {
		final Function<? super Structure<T>,? extends T> termFunction
				= processTermValueNode(node.getTerm());
		return new ProofPatternEvent<T,Structure<T>>(
				processPEGNode(node.getInput())) {
			protected boolean matches(Structure<T> pattern) {
				return !pattern.isRemoved()
						&& termFunction.get(pattern).getOp().isLoopLiftedAll();
			}
			protected boolean canMatch(Structure<T> pattern) {
				if (pattern.isRemoved())
					return false;
				T term = termFunction.get(pattern);
				return term == null || term.getOp().isLoopLiftedAll();
			}
			protected void addConstraints(Structure<T> result, Proof proof) {
				proof.addProperty(
						new OpIsAllLoopLifted<T>(termFunction.get(result)));
			}
			public String toString() {
				return "Op of " + termFunction + " is Loop-Lifted"; 
			}
		};
	}
	
	protected ProofEvent<T,? extends Structure<T>>
			setupCheckEqualLoopDepthsEvent(CheckEqualLoopDepthsNode<O> node) {
		final Function<? super Structure<T>,? extends T>
				left = processLoopNode(node.getLeft()),
				right = processLoopNode(node.getRight());
		return new ProofPatternEvent<T,Structure<T>>(
				processPEGNode(node.getInput())) {
			protected boolean matches(Structure<T> pattern) {
				return !pattern.isRemoved()
						&& left.get(pattern).getOp().getLoopDepth()
						== right.get(pattern).getOp().getLoopDepth();
			}
			protected boolean canMatch(Structure<T> pattern) {
				if (pattern.isRemoved())
					return false;
				T l = left.get(pattern);
				if (l == null)
					return true;
				T r = right.get(pattern);
				return r == null
						|| l.getOp().getLoopDepth() == r.getOp().getLoopDepth();
			}
			protected void addConstraints(Structure<T> result, Proof proof) {
				proof.addProperty(new OpIsSameLoop<T>(
						left.get(result), right.get(result)));
			}
			public String toString() {
				return "Check " + left + " loop depth = "
						+ right + " loop depth";
			}
		};
	}
	
	protected ProofEvent<T,? extends Structure<T>>
			setupCheckDistinctLoopDepthsEvent(
					CheckDistinctLoopDepthsNode<O> node) {
		final Function<? super Structure<T>,? extends T>
				left = processLoopNode(node.getLeft()),
				right = processLoopNode(node.getRight());
		return new ProofPatternEvent<T,Structure<T>>(
				processPEGNode(node.getInput())) {
			protected boolean matches(Structure<T> pattern) {
				return !pattern.isRemoved()
						&& left.get(pattern).getOp().getLoopDepth()
						!= right.get(pattern).getOp().getLoopDepth();
			}
			protected boolean canMatch(Structure<T> pattern) {
				if (pattern.isRemoved())
					return false;
				T l = left.get(pattern);
				if (l == null)
					return true;
				T r = right.get(pattern);
				return r == null
						|| l.getOp().getLoopDepth() != r.getOp().getLoopDepth();
			}
			protected void addConstraints(Structure<T> result, Proof proof) {
				proof.addProperty(new OpIsDifferentLoop<T>(
						left.get(result), right.get(result)));
			}
			public String toString() {
				return "Check " + left + " loop depth != "
						+ right + " loop depth";
			}
		};
	}
	
	protected ProofEvent<T,? extends Structure<T>> setupIsInvariantEvent(
			IsInvariantNode<O> node) {
		final Function<? super Structure<T>,? extends TermOrTermChild<T,V>>
				valueFunction = processValueNode(node.getValue());
		final Function<? super Structure<T>,? extends T> loopFunction
				= processLoopNode(node.getLoop());
		return new ProofPatternEvent<T,Structure<T>>(
				processPEGNode(node.getInput())) {
			protected boolean matches(Structure<T> pattern) {
				return !pattern.isRemoved()
						&& watchInvariance(
						loopFunction.get(pattern).getOp().getLoopDepth(),
						valueFunction.get(pattern).getRepresentative(),
						this, pattern);
			}
			protected boolean canMatch(Structure<T> pattern) {
				return !pattern.isRemoved(); //TODO add canBeInvariant
			}
			protected void addConstraints(Structure<T> result, Proof proof) {
				TermOrTermChild<T,V> value = valueFunction.get(result);
				if (value.isTerm())
					proof.addProperty(new IsInvariant<T,V>(
							value.getTerm(), loopFunction.get(result)));
				else if (value.isTermChild())
					proof.addProperty(new ChildIsInvariant<T,V>(
							value.getParentTerm(), value.getChildIndex(),
							loopFunction.get(result)));
				else
					throw new UnhandledCaseException();
			}
			public String toString() {
				return "Check invariance of " + valueFunction
						+ " w.r.t. " + loopFunction;
			}
		};
	}
	
	protected ProofEvent<T,? extends Structure<T>> setupPEGEvent(
			PEGNode<O> node) {
		if (node.isExpression())
			return processExpressionNode(node.<P>getExpression());
		else if (node.isOpIsExtendedDomainOp())
			return setupOpIsExtendedDomainOpEvent(
					node.getOpIsExtendedDomainOp());
		else if (node.isOpIsLoopOp())
			return setupOpIsLoopOpEvent(node.getOpIsLoopOp());
		else if (node.isOpIsLoopLifted())
			return setupOpIsLoopLiftedEvent(node.getOpIsLoopLifted());
		else if (node.isOpIsAllLoopLifted())
			return setupOpIsAllLoopLiftedEvent(node.getOpIsAllLoopLifted());
		else if (node.isCheckEqualLoopDepths())
			return setupCheckEqualLoopDepthsEvent(
					node.getCheckEqualLoopDepths());
		else if (node.isCheckDistinctLoopDepths())
			return setupCheckDistinctLoopDepthsEvent(
					node.getCheckDistinctLoopDepths());
		else if (node.isIsInvariant())
			return setupIsInvariantEvent(node.getIsInvariant());
		else
			return null;
	}

	protected final Function<? super Structure<T>,? extends T> processLoopNode(
			LoopNode node) {
		if (node.hasTag(mLoopFunctionTag))
			return node.getTag(mLoopFunctionTag);
		Function<? super Structure<T>,? extends T> function
				= setupLoopFunction(node);
		if (function == null)
			throw new UnhandledCaseException(node);
		node.setTag(mLoopFunctionTag, function);
		return function;
	}

	public final ProofEvent<T,? extends Structure<T>>
			processPEGNode(PEGNode<O> node) {
		if (node.hasTag(mStructureEventTag))
			return node.getTag(mStructureEventTag);
		ProofEvent<T,? extends Structure<T>> event = setupPEGEvent(node);
		if (event == null)
			throw new UnhandledCaseException(node);
		node.setTag(mStructureEventTag, event);
		return event;
	}
	
	protected PEGTermConstructor<O,P,V> getPEGTermConstructor(
			FutureExpression<FlowValue<P,O>,T,V> expression) {
		return new PEGTermConstructor<O,P,V>(
				getOpTermConstructor(expression));
	}
	
	protected Ambassador<T,V> createAmbassador(FutureAmbassador<?,?,V> future) {
		Ambassador<T,V> ambassador = super.createAmbassador(future);
		makeInvariants(ambassador, future.getTag(mInvarianceTag));
		return ambassador;
	}
	
	protected void processNewValue(V value, T term) {
		final List<? extends Representative<V>> children
				= term.getChildren();
		makeInvariants(term, getInvariance(term.getOp(),
				new MappedList<Representative<V>,Integer>() {
			protected List<? extends Representative<V>> getWrapped() {
				return children;
			}
			protected Integer map(Representative<V> domain) {
				return domain.getValue().getInvariance();
			}
		}));
	}
	
	protected void processTerm(T term) {
		final List<? extends Representative<V>> children
				= term.getChildren();
		makeInvariants(term, getInvariance(term.getOp(),
				new MappedList<Representative<V>,Integer>() {
			protected List<? extends Representative<V>> getWrapped() {
				return children;
			}
			protected Integer map(Representative<V> domain) {
				return domain.getValue().getInvariance();
			}
		}));
		super.processTerm(term);
	}
	
	protected void analyzeExpressions(
			FutureExpressionGraph<FlowValue<P,O>,T,V> expressions) {
		super.analyzeExpressions(expressions);
		List<? extends Vertex<FlowValue<P,O>,T,V>> sorted
				= Graphs.reverseToposort(expressions);
		List<FutureExpression<FlowValue<P,O>,T,V>> futures = new ArrayList();
		List<FutureAmbassador<FlowValue<P,O>,T,V>> ambassadors
				= new ArrayList();
		for (Vertex<FlowValue<P,O>,T,V> vertex : sorted)
			if (vertex.isRepresentative())
				vertex.setTag(mInvarianceTag,
						vertex.getRepresentative().getValue().getInvariance());
			else if (vertex.isFutureExpression())
				futures.add(vertex.getFutureExpression());
			else if (vertex.isFutureAmbassador()) {
				ambassadors.add(vertex.getFutureAmbassador());
				vertex.setTag(mInvarianceTag, ~0);
			}
		boolean changed;
		do {
			for (FutureExpression<FlowValue<P,O>,T,V> expression : futures) {
				final List<? extends Vertex<FlowValue<P,O>,T,V>> children
						= expression.getChildren();
				expression.setTag(mInvarianceTag,
						getInvariance(expression.getOp(),
						new MappedList<Vertex<FlowValue<P,O>,T,V>,Integer>() {
					protected List<? extends Vertex<FlowValue<P,O>,T,V>>
							getWrapped() {
						return children;
					}
					protected Integer map(Vertex<FlowValue<P,O>,T,V> domain) {
						return domain.getTag(mInvarianceTag);
					}
				}));
			}
			changed = false;
			for (FutureAmbassador<FlowValue<P,O>,T,V> ambassador : ambassadors){
				Integer next = ambassador.getIntendedExpression()
						.getTag(mInvarianceTag);
				if (!ambassador.getTag(mInvarianceTag).equals(next)) {
					changed = true;
					ambassador.setTag(mInvarianceTag, next);
				}
			}
		} while (changed);
	}
	
	private int getInvariance(FlowValue<P,O> op, List<Integer> children) {
		int invariance = ~0;
		for (int child : children)
			invariance &= child;
		if (op.isLoopLiftedAll() || op.isShift())
			return invariance;
		else if (op.isTheta())
			return invariance & ~(1 << op.getLoopDepth());
		else if (op.isPass())
			return invariance | (1 << op.getLoopDepth());
		else if (op.isEval())
			return invariance | ((1 << op.getLoopDepth()) & children.get(1));
		else
			throw new UnhandledCaseException();
	}
	
	private void makeInvariants(Representative<V> representative,
			int invariance) {
		makeInvariants(representative.getValue(), invariance);
	}
	private void makeInvariants(V value, int invariance) {
		if ((invariance & ~value.getInvariance()) == 0)
			return;
		value.makeInvariants(invariance);
		Bit32IntSet depths = new Bit32IntSet(invariance);
		depths.retainAll(mWaiting.keySet());
		for (int depth : depths)
			addPostProcesses(mWaiting.get(depth).removeKey(value));
		for (T term : getUses(value)) {
			final List<? extends Representative<V>> children
					= term.getChildren();
			makeInvariants(term, getInvariance(term.getOp(),
					new MappedList<Representative<V>,Integer>() {
				protected List<? extends Representative<V>> getWrapped() {
					return children;
				}
				protected Integer map(Representative<V> domain) {
					return domain.getValue().getInvariance();
				}
			}));
		}
	}
	
	protected boolean allowSelfLoop(T term) {
		if (!term.getOp().isTheta())
			return false;
		if (term.getChild(0).getValue().equals(term.getValue()))
			return false;
		if (term.getChild(0).getValue().isInvariant(
				term.getOp().getLoopDepth())) {
			Proof proof;
			if (hasProofManager())
				proof = new Proof("x = Theta(c, x) and inv(c) => x = c",
						new ArityIs<T>(term, 2),
						new OpIsLoopOp<T>(term, PEGLoopOp.Theta),
						new ChildIsInvariant<T,V>(term, 0, term),
						new ChildIsEquivalentTo<T,V>(term, 1, term));
			else
				proof = null;
			makeEqual(term, new TermChild<T,V>(term, 0), proof);
		}
		//TODO handle when not invariant
		return false;
	}
	
	public abstract OpAmbassador<O> getOpAmbassador();
	
	protected boolean canFold(FlowValue<P,O> op) {
		return op.isExtendedDomain() && getOpAmbassador().canPreEvaluate(
				op.getDomain(getOpAmbassador()));
	}
	
	protected FlowValue<P,O> fold(FlowValue<P,O> op,
			List<? extends FlowValue<P,O>> children) {
		if (!op.isExtendedDomain())
			return null;
		O domainOp = op.getDomain(getOpAmbassador());
		if (!getOpAmbassador().canPreEvaluate(domainOp))
			return null;
		List<O> domainChildren = new ArrayList<O>(children.size());
		for (FlowValue<P,O> child : children) {
			if (!child.isExtendedDomain())
				return null;
			domainChildren.add(child.getDomain(getOpAmbassador()));
		}
		O folded = getOpAmbassador().get(domainOp, domainChildren);
		return folded == null ? null
				: FlowValue.<P,O>createDomain(folded, getOpAmbassador());
	}
	
	protected String getValueInfoString(V value) {
		if (value.getInvariance() == 0)
			return "[*]";
		else
			return new Bit32IntSet(~value.getInvariance()).toString();
	}
}
