package eqsat.meminfer.engine.op;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import util.Function;
import util.HashMultiMap;
import util.LinkList;
import util.MultiMap;
import util.UnhandledCaseException;
import util.graph.Graphs;
import util.mapped.MappedList;
import util.pair.Couple;
import eqsat.meminfer.engine.basic.Ambassador;
import eqsat.meminfer.engine.basic.EGraphManager;
import eqsat.meminfer.engine.basic.FutureExpression;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.basic.Representative;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.basic.Value;
import eqsat.meminfer.engine.basic.ValueManager;
import eqsat.meminfer.engine.basic.FutureExpressionGraph.Vertex;
import eqsat.meminfer.engine.event.EventListener;
import eqsat.meminfer.engine.event.ProofEvent;
import eqsat.meminfer.engine.event.ProofPatternEvent;
import eqsat.meminfer.engine.proof.ArityIs;
import eqsat.meminfer.engine.proof.ChildIsEquivalentTo;
import eqsat.meminfer.engine.proof.EquivalentChildren;
import eqsat.meminfer.engine.proof.OpIs;
import eqsat.meminfer.engine.proof.OpsEqual;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.network.op.ExpressionNetwork.ExpressionNode;
import eqsat.meminfer.network.op.ExpressionNetwork.OpEqualsNode;
import eqsat.meminfer.network.op.ExpressionNetwork.OpsEqualNode;

public abstract class
		OpEGraphManager<O, T extends OpTerm<O,T,V>, V extends Value<T,V>>
		extends EGraphManager<T,V> {
	private final MultiMap<V,T> mConstants;
	private final MultiMap<V,T> mFolding;
	private final Map<O,T> mLeaves = new HashMap<O,T>();
	private final MultiMap<O,T> mOps = new HashMultiMap<O,T>();
	private final EventListener<Couple<T>> mCongruenceMerger
			= new EventListener<Couple<T>>() {
		private boolean canUse(T left, T right) {
			if (left == null)
				return right == null || !right.isRemoved();
			else if (left.isRemoved())
				return false;
			if (right == null)
				return true;
			else if (right.isRemoved())
				return false;
			return left.getArity() == right.getArity()
					&& left.getOp().equals(right.getOp())
					&& !left.getValue().equals(right.getValue());
		}
		public boolean canUse(Couple<T> parameter) {
			return canUse(parameter.getLeft(), parameter.getRight());
		}
		public boolean notify(Couple<T> parameter) {
			T left = parameter.getLeft(), right = parameter.getRight();
			if (!canUse(left, right))
				return true;
			int arity = left.getArity();
			for (int child = 0; child < arity; child++)
				if (!watchEquality(left.getChild(child), right.getChild(child),
						this, parameter))
					return true;
			Proof proof = new Proof("Congruence",
					new OpsEqual<O,T>(left, right),
					new ArityIs<T>(left, arity),
					new ArityIs<T>(right, arity));
			for (int child = 0; child < left.getArity(); child++)
				proof.addProperty(
						new EquivalentChildren<T,V>(left, child, right, child));
			makeEqual(left, right, proof);
			mOps.removeEntry(left.getOp(), left);
			return true;
		}
		public String toString() {return "Congruence Merger";}
	};
	
	protected OpEGraphManager(ValueManager<V> manager) {
		super(manager);
		mConstants = manager.createValueMultiMap();
		mFolding = manager.createValueMultiMap();
		manager.getMergedEvent().addListener(new EventListener<V>() {
			public boolean canUse(V parameter) {return true;}
			public boolean notify(V parameter) {
				if (mConstants.containsKey(parameter))
					makeConstant(parameter);
				return true;
			}
			public String toString() {return "Constant folder";}
		});
		manager.getMergedEvent().addListener(new EventListener<V>() {
			public boolean canUse(V parameter) {return true;}
			public boolean notify(V parameter) {
				LinkList<T> removed = LinkList.<T>empty();
				for (T left : parameter.getTerms()) {
					if (removed.contains(left))
						continue;
					nextRight: for (T right : parameter.getTerms())
						if (!left.equals(right)
								&& left.getArity() == right.getArity()
								&& left.getOp().equals(right.getOp())) {
							for (int i = 0; i < left.getArity(); i++)
								if (!left.getChild(i).getValue().equals(
										right.getChild(i).getValue()))
									continue nextRight;
							removed = removed.prepend(right);
						}
				}
				for (T term : removed)
					removeTerm(term);
				//TODO check uses
				return true;
			}
			public String toString() {return "Remove redundant terms";}
		});
	}
	
	protected ProofEvent<T,? extends Structure<T>> setupOpEqualsEvent(
			OpEqualsNode<? extends O> node) {
		final Function<? super Structure<T>,? extends T> termFunction
				= processTermValueNode(node.getTerm());
		final O op = node.getOp();
		ProofEvent<T,Structure<T>> event
				= new ProofPatternEvent<T,Structure<T>>(
				processExpressionNode(node.getInput())) {
			protected boolean matches(Structure<T> pattern) {
				return !pattern.isRemoved()
						&& termFunction.get(pattern).getOp().equals(op);
			}
			protected boolean canMatch(Structure<T> pattern) {
				if (pattern.isRemoved())
					return false;
				T term = termFunction.get(pattern);
				return term == null || term.getOp().equals(op);
			}
			protected void addConstraints(Structure<T> result, Proof proof) {
				proof.addProperty(new OpIs<O,T>(termFunction.get(result), op));
			}
			public String toString() {
				return "Check op of " + termFunction + " equals " + op;
			}
		};
		return event;
	}
	
	protected ProofEvent<T,? extends Structure<T>> setupOpsEqualEvent(
			OpsEqualNode<? extends O> node) {
		final Function<? super Structure<T>,? extends T> leftFunction
				= processTermValueNode(node.getLeft());
		final Function<? super Structure<T>,? extends T> rightFunction
				= processTermValueNode(node.getRight());
		ProofEvent<T,Structure<T>> event
				= new ProofPatternEvent<T,Structure<T>>(
				processExpressionNode(node.getInput())) {
			protected boolean matches(Structure<T> pattern) {
				return !pattern.isRemoved()
						&& leftFunction.get(pattern).getOp().equals(
								rightFunction.get(pattern).getOp());
			}
			protected boolean canMatch(Structure<T> pattern) {
				if (pattern.isRemoved())
					return false;
				T left = leftFunction.get(pattern);
				if (left == null)
					return true;
				T right = rightFunction.get(pattern);
				return right == null || left.getOp().equals(right.getOp());
			}
			protected void addConstraints(Structure<T> result, Proof proof) {
				proof.addProperty(new OpsEqual<O,T>(leftFunction.get(result),
						rightFunction.get(result)));
			}
			public String toString() {
				return "Check op of " + leftFunction
						+ " equals op of " + rightFunction;
			}
		};
		return event;
	}
	
	protected ProofEvent<T,? extends Structure<T>> setupExpressionEvent(
			ExpressionNode<? extends O> node) {
		if (node.isStructure())
			return processStructureNode(node.getStructure());
		else if (node.isOpEquals())
			return setupOpEqualsEvent(node.getOpEquals());
		else if (node.isOpsEqual())
			return setupOpsEqualEvent(node.getOpsEqual());
		else
			return null;
	}

	public final ProofEvent<T, ? extends Structure<T>>
			processExpressionNode(ExpressionNode<? extends O> node) {
		if (node.hasTag(mStructureEventTag))
			return node.getTag(mStructureEventTag);
		ProofEvent<T,? extends Structure<T>> event = setupExpressionEvent(node);
		if (event == null)
			throw new UnhandledCaseException(node);
		node.setTag(mStructureEventTag, event);
		return event;
	}
	
	protected T getTerm(FutureExpression<O,T,V> expression,
			boolean forceRedundants) {
		T term = forceRedundants ? null : getExistingTerm(expression);
		if (term == null) {
			term = createTerm(expression);
			processNewValue(term.getValue(), term);
			processNewTerm(term);
		}
		return term;
	}
	
	protected T getExistingTerm(FutureExpression<O,T,V> expression) {
		O op = expression.getOp();
		if (expression.isLeaf())
			return mLeaves.get(op);
		else {
			processTerm: for (T term : getUses(
					expression.getChild(0).getValue().getValue()))
				if (term.getArity() == expression.getChildCount()
						&& op.equals(term.getOp())) {
					for (int i = term.getArity(); i-- != 0; )
						if (!term.getChild(i).getValue().equals(expression
								.getChild(i).getValue().getValue()))
							continue processTerm;
					return term;
				}
			return null;
		}
	}
	
	protected abstract T createTerm(FutureExpression<O,T,V> expression);
	
	protected OpTermConstructor<O,V> getOpTermConstructor(
			FutureExpression<O,T,V> expression) {
		return new OpTermConstructor<O,V>(getTermConstructor(expression),
				expression.getOp());
	}
	
	protected void processNewTerm(T term) {
		super.processNewTerm(term);
		if (term.getArity() == 0)
			mLeaves.put(term.getOp(), term);
	}
	
	protected void processNewValue(V value, T term) {}
	
	protected boolean canConstantsEqual(O left, O right) {
		return left.equals(right);
	}
	protected boolean canFold(O op) {return false;}
	protected O fold(O op, List<? extends O> children) {return null;}
	
	protected boolean removeComplexConstants() {return false;}
	
	protected void processTerm(T term) {
		if (removeComplexConstants() && mConstants.containsKey(term.getValue()))
			return;
		int arity = term.getArity();
		if (arity == 0) {
			O op = term.getOp();
			for (Entry<O,T> entry : mLeaves.entrySet())
				if (!canConstantsEqual(op, entry.getKey()))
					getValueManager().makeUnequal(entry.getValue().getValue(),
							term.getValue());
			if (removeComplexConstants()) {
				LinkList<T> remove = LinkList.<T>empty();
				for (T other : term.getValue().getTerms())
					if (other.getArity() != 0)
						remove = remove.prepend(other);
				for (T other : remove)
					removeTerm(other);
			}
			mConstants.addValue(term.getValue(), term);
			makeConstant(term.getValue());
		} else {
			nextThat: for (T that : mOps.get(term.getOp()))
				if (arity == that.getArity()) {
					for (int child = 0; child < arity; child++)
						if (!term.getChild(child).getValue().equals(
								that.getChild(child).getValue()))
							continue nextThat;
					Proof proof;
					if (hasProofManager()) {
						proof = new Proof("Congruence",
								new OpsEqual<O,T>(term, that),
								new ArityIs<T>(term, arity),
								new ArityIs<T>(that, arity));
						for (int child = 0; child < term.getArity(); child++)
							proof.addProperty(new EquivalentChildren<T,V>(
									term, child, that, child));
					} else
						proof = null;
					makeEqual(term, that, proof);
					removeTerm(term);
					return;
				}
			for (T that : mOps.get(term.getOp()))
				mCongruenceMerger.notify(new Couple<T>(term, that));
			mOps.addValue(term.getOp(), term);
		}
		super.processTerm(term);
		attemptFold(term);
	}
	
	protected void removeTerm(T term) {
		if (term.getArity() != 0)
			mOps.removeEntry(term.getOp(), term);
		super.removeTerm(term);
	}
	
	public abstract FutureExpression<O,T,V> getTrueFuture(
			FutureExpressionGraph<O,T,V> graph);
	public abstract FutureExpression<O,T,V> getFalseFuture(
			FutureExpressionGraph<O,T,V> graph);
	
	public final void addExpressions(FutureExpressionGraph<O,T,V> expressions) {
		addExpressions(expressions, false);
	}
	public final void addRedundantExpressions(
			FutureExpressionGraph<O,T,V> expressions) {
		addExpressions(expressions, true);
	}
	public final void addExpressions(FutureExpressionGraph<O,T,V> expressions,
			boolean forceRedundants) {
		analyzeExpressions(expressions);
		List<Vertex<O,T,V>> vertices = Graphs.reverseToposort(expressions);
		for (Vertex<O,T,V> vertex : vertices)
			if (vertex.isFutureAmbassador()) {
				Ambassador<T,V> ambassador
						= createAmbassador(vertex.getFutureAmbassador());
				vertex.getFutureAmbassador().setAmbassador(ambassador);
			} else if (vertex.isFutureExpression()) {
				FutureExpression<O,T,V> expression
						= vertex.getFutureExpression();
				T term = getTerm(expression, forceRedundants);
				if (expression.hasFutureValue())
					makeEqual(term,
							expression.getFutureValue().getAmbassador());
				expression.setTerm(term);
			}
	}
	
	protected void analyzeExpressions(FutureExpressionGraph<O,T,V> expressions){
	}
	
	protected void makeConstant(V value) {
		if (removeComplexConstants()) {
			Set<T> remove = new HashSet<T>(value.getTerms());
			remove.removeAll(mConstants.get(value));
			for (T term : remove)
				removeTerm(term);
		}
		if (mFolding.containsKey(value))
			for (T term : mFolding.removeKey(value))
				attemptFold(term);
	}
	
	protected void attemptFold(final T term) {
		if (term.getArity() > 0 && canFold(term.getOp())) {
			V nonConstant = null;
			for (Representative<V> child : term.getChildren())
				if (!mConstants.containsKey(child.getValue())) {
					nonConstant = child.getValue();
					break;
				}
			if (nonConstant == null) {
				final List<T> children = new ArrayList<T>(term.getArity());
				for (Representative<V> child : term.getChildren())
					try {
						children.add(mConstants.get(child.getValue())
								.iterator().next());
					} catch (IllegalStateException err) {
						throw new RuntimeException(
								"Constant value missing constant term");
					}
				O constant = fold(term.getOp(), new MappedList<T,O>() {
					protected List<? extends T> getWrapped() {return children;}
					protected O map(T domain) {return domain.getOp();}
				});
				if (constant != null) {
					FutureExpressionGraph<O,T,V> graph
							= new FutureExpressionGraph<O,T,V>();
					FutureExpression<O,T,V> expression
							= graph.getExpression(constant);
					addExpressions(graph);
					T folded = expression.getTerm();
					Proof proof = new Proof("Constant Fold");
					proof.addProperties(new ArityIs<T>(term, children.size()),
							new OpIs<O,T>(term, term.getOp()));
					for (int i = 0; i < term.getArity(); i++) {
						proof.addProperty(new ChildIsEquivalentTo<T,V>(
								term, i, children.get(i)));
						proof.addProperties(new ArityIs<T>(children.get(i), 0),
								new OpIs<O,T>(children.get(i),
										children.get(i).getOp()));
					}
					proof.addProperties(new ArityIs<T>(folded, 0),
							new OpIs<O,T>(folded, constant));
					makeEqual(term, expression.getTerm(), proof);
				}
			} else
				mFolding.addValue(nonConstant, term);
		}
	}

	protected String getTermInfoString(T term) {return term.getOp().toString();}
}
