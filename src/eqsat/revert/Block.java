package eqsat.revert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.OpExpression;
import eqsat.revert.Value.BlockValue;
import util.AbstractPattern;
import util.ArrayCollection;
import util.Collections;
import util.Function;
import util.Grouping;
import util.HashGrouping;
import util.HashMultiMap;
import util.MultiMap;
import util.NamedTag;
import util.Pattern;
import util.Tag;
import util.UnhandledCaseException;
import util.WrappingArrayList;
import util.graph.GenericGraph;
import util.graph.LabeledGenericVertex;
import util.graph.UniqueLabeledGraph;
import util.graph.CExpressionGraph.Vertex;
import util.integer.ArrayIntMap;
import util.integer.ArrayIntPartition;
import util.integer.BitIntSet;
import util.integer.IncreasingIntSet;
import util.integer.IntCollections;
import util.integer.IntIterator;
import util.integer.IntMap;
import util.integer.IntSet;
import util.integer.PairInt;
import util.mapped.MappedIterable;
import util.pair.Pair;

public abstract class Block<P, L> {
	protected final CFGReverter<P,L,?> mReverter;
	protected final ReversionGraph<P,L> mGraph;
	protected final Map<Variable,ReversionGraph<P,L>.Vertex> mModifications
			= new HashMap<Variable,ReversionGraph<P,L>.Vertex>();
	
	protected Block(CFGReverter<P,L,?> reverter, OpAmbassador<L> ambassador) {
		this(reverter, new ReversionGraph<P,L>(ambassador));
	}
	protected Block(CFGReverter<P,L,?> reverter,
			ReversionGraph<P,L> graph) {
		mReverter = reverter;
		mGraph = graph;
	}
	protected Block(CFGReverter<P,L,?> reverter, OpAmbassador<L> ambassador,
			Collection<? extends Block<P,L>> blocks) {
		this(reverter, ambassador, blocks, new NamedTag("Converted"));
	}
	protected Block(CFGReverter<P,L,?> reverter, OpAmbassador<L> ambassador,
			Collection<? extends Block<P,L>> blocks,
			Tag<ReversionGraph<P,L>.Vertex> convertTag) {
		this(reverter, ambassador);
		BlockInliner<P,L> convert = getConverter();
		for (Block<P,L> block : blocks) {
			for (Entry<Variable,ReversionGraph<P,L>.Vertex> entry
					: block.mModifications.entrySet())
				if (!mModifications.containsKey(entry.getKey()))
					modify(entry.getKey(), convert.get(entry.getValue()));
		}
	}
	
	protected OpAmbassador<L> getOpAmbassador() {
		return mGraph.getOpAmbassador();
	}
	
	protected BlockInliner<P,L> getConverter() {
		return new BlockInliner<P,L>(this) {
			protected boolean inline(ReversionGraph<P,L>.Vertex vertex) {
				return false;
			}
		};
	}
	
	public boolean modifies(Variable variable) {
		return mModifications.containsKey(variable);
	}
	
	public void modify(Variable variable, ReversionGraph<P,L>.Vertex value) {
		if (value.getLabel().isVariable()
				&& value.getLabel().getVariable().equals(variable))
			mModifications.remove(variable);
		else {
			mModifications.put(variable, value);
			value.makeSignificant();
		}
	}
	
	protected void processRedundants() {
		if (!mGraph.containsEvals())
			return;

		final int max = mGraph.getMaxVariant();
		final Tag<IntMap<ReversionGraph<P,L>.Vertex>> tag
				= new NamedTag("Reaching Passes");
		Evaluator<P,L,Void> evalUses
				= new Evaluator<P,L,Void>() {
			public Void get(ReversionGraph<P,L>.Vertex vertex) {
				if (vertex.hasTag(tag) && vertex.getTag(tag) == null)
					return null;
				IntMap<ReversionGraph<P,L>.Vertex> passes
						= vertex.hasTag(tag) ? vertex.getTag(tag)
						: new ArrayIntMap();
				int oldSize = passes.size();
				boolean changed = false;
				for (ReversionGraph<P,L>.Vertex parent : vertex.getParents()) {
					if (parent.getHead() != null
							&& !parent.getHead().isLoopLiftedAll()) {
						int loop = parent.getHead().getLoopDepth();
						if (parent.getHead().isEval()
								&& parent.getChild(0).equals(vertex)) {
							if (parent.getChild(1).isVariant())
								changed |= passes.put(loop, null) != null;
							else if (passes.containsKey(loop)) {
								ReversionGraph<P,L>.Vertex old
										= passes.get(loop);
								if (old == null
										|| !old.equals(parent.getChild(1)))
									changed |= passes.put(loop, null) != null;
							} else
								passes.put(loop, parent.getChild(1));
						} else
							changed |= passes.put(loop, null) != null;
						if (!parent.hasTag(tag))
							;
						else if (parent.getTag(tag) == null) {
							if (passes.get(loop) == null) {
								passes = null;
								changed = true;
								break;
							} else
								for (int i = max; i != 0; i--)
									if (i != loop)
										changed |= passes.put(i, null) != null;
						} else {
							IntMap<ReversionGraph<P,L>.Vertex> parents
									= parent.getTag(tag);
							for (int depth : parents.keySet())
								if (depth != loop) {
									ReversionGraph<P,L>.Vertex pass
											= parents.get(depth);
									if (pass == null
											|| !passes.containsKey(depth))
										changed |=
											passes.put(depth, pass) != null;
									else if (!pass.equals(passes.get(depth)))
										changed |=
											passes.put(depth, null) != null;
								}
						}
					} else if (!parent.hasTag(tag))
						;
					else if (parent.getTag(tag) == null) {
						passes = null;
						changed = true;
						break;
					} else {
						IntMap<ReversionGraph<P,L>.Vertex> parents
								= parent.getTag(tag);
						for (int depth : parents.keySet()) {
							ReversionGraph<P,L>.Vertex pass
									= parents.get(depth);
							if (pass == null || !passes.containsKey(depth))
								changed |= passes.put(depth, pass) != null;
							else if (!pass.equals(passes.get(depth)))
								changed |= passes.put(depth, null) != null;
						}
					}
				}
				vertex.setTag(tag, passes);
				if (changed || oldSize != passes.size())
					for (ReversionGraph<P,L>.Vertex child
							: vertex.getChildren())
						get(child);
				return null;
			}
		};
		for (ReversionGraph<P,L>.Vertex vertex : mGraph.getSignificant())
			vertex.setTag(tag, null);
		for (ReversionGraph<P,L>.Vertex vertex : mGraph.getSignificant())
			for (ReversionGraph<P,L>.Vertex child : vertex.getChildren())
				evalUses.get(child);
		Collection<ReversionGraph<P,L>.Vertex> rewrite = new ArrayCollection();
		for (ReversionGraph<P,L>.Vertex vertex : mGraph.getVertices())
			if (vertex.getHead() != null && vertex.getHead().isEval()
					&& !vertex.getChild(1).isVariant()) {
				int loop = vertex.getHead().getLoopDepth();
				IntMap<ReversionGraph<P,L>.Vertex> passes = vertex.getTag(tag);
				if (passes != null && passes.get(loop) != null
						&& passes.get(loop).equals(vertex.getChild(1)))
					rewrite.add(vertex);
			}
		for (ReversionGraph<P,L>.Vertex eval : rewrite)
			eval.rewrite(eval.getChild(0));
		rewriteModifications();
		mGraph.trimInsignificant();
	}
	
	protected void processEvals() {
		Collection<ReversionGraph<P,L>.Vertex> evals = new ArrayCollection();
		for (ReversionGraph<P,L>.Vertex vertex : mGraph.getVertices())
			if (vertex.isEval() && !vertex.isVariant())
				evals.add(vertex);
		for (ReversionGraph<P,L>.Vertex eval : evals)
			eval.rewrite(processEval(eval));
		rewriteModifications();
		mGraph.trimInsignificant();
		mGraph.clearEvals();
	}
	
	protected ReversionGraph<P,L>.Vertex processEval(
			ReversionGraph<P,L>.Vertex eval) {
		final int loop = eval.getHead().getLoopDepth();
		final Collection<ReversionGraph<P,L>.Vertex> thetas
				= new ArrayCollection();
		Evaluator<P,L,Void> find = new Evaluator<P,L,Void>() {
			final Tag<Void> mTag = new NamedTag("Find Theta-" + loop);
			public Void get(ReversionGraph<P,L>.Vertex vertex) {
				if (vertex.hasTag(mTag))
					return null;
				vertex.setTag(mTag, null);
				if (vertex.getHead() != null) {
					FlowValue<P,L> op = vertex.getHead();
					if (op.isTheta() && op.getLoopDepth() == loop) {
						thetas.add(vertex);
						get(vertex.getChild(1));
						return null;
					}
				}
				if (vertex.isVariant())
					for (int i = vertex.getChildCount(); i-- != 0; )
						get(vertex.getChild(i));
				return null;
			}
		};
		find.get(eval.getChild(0));
		find.get(eval.getChild(1).getChild(0));
		final Set<ReversionGraph<P,L>.Vertex> inputs
				= new HashSet<ReversionGraph<P,L>.Vertex>();
		final FallBlock<P,L> init
				= new FallBlock<P,L>(mReverter, getOpAmbassador());
		BlockInliner<P,L> convertInit = new BlockInliner<P,L>(init) {
			protected boolean inline(ReversionGraph<P,L>.Vertex vertex) {
				return true;
			}
			protected void inlined(ReversionGraph<P,L>.Vertex vertex) {
				inputs.add(vertex);
			}
		};
		for (ReversionGraph<P,L>.Vertex theta : thetas)
			init.modify(theta.getVariable(),
					convertInit.get(theta.getChild(0)));
		init.rewriteModifications();
		final BranchBlock<P,L> body
				= new BranchBlock<P,L>(mReverter, getOpAmbassador());
		BlockInliner<P,L> convertBody = new BlockInliner<P,L>(body) {
			protected boolean inline(ReversionGraph<P,L>.Vertex vertex) {
				return  fullInline(vertex) || vertex.getHead() != null
						&& vertex.getHead().isTheta()
						&& vertex.getHead().getLoopDepth() == loop;
			}
			protected boolean fullInline(ReversionGraph<P,L>.Vertex vertex) {
				return !vertex.isVariant();
			}
			protected void inlined(ReversionGraph<P,L>.Vertex vertex) {
				if (!vertex.isVariant())
					inputs.add(vertex);
			}
			protected void reconsidered(ReversionGraph<P,L>.Vertex vertex) {
				inputs.remove(vertex);
			}
		};
		body.setBranchCondition(
				convertBody.get(eval.getChild(1).getChild(0)));
		ReversionGraph<P,L>.Vertex result
				= convertBody.get(eval.getChild(0));
		body.modifyIfTrue(eval.getVariable(), result);
		for (ReversionGraph<P,L>.Vertex theta : thetas)
			body.modifyIfFalse(theta.getVariable(),
					convertBody.get(theta.getChild(1)));
		body.rewriteModifications();
		List<Variable> injects = new ArrayList<Variable>(inputs.size());
		IncreasingIntSet volatil = new BitIntSet();
		for (ReversionGraph<P,L>.Vertex input : inputs) {
			injects.add(input.getVariable());
			boolean done = false;
			if (init.mGraph.containsVariable(input.getVariable())) {
				ReversionGraph<P,L>.Vertex var
						= init.mGraph.getVertex(input.getVariable());
				parents: for (ReversionGraph<P,L>.Vertex parent
						: var.getParents())
					if (parent.isAnyVolatile())
						for (int i = parent.getChildCount(); i-- != 0; )
							if (done |= (parent.isVolatile(i) &&
									parent.getChild(i).equals(var)))
								break parents;
			}
			if (!done) {
				Collection<ReversionGraph<P,L>.Vertex> vars = new ArrayList();
				if (body.mGraph.containsVariable(input.getVariable()))
					vars.add(body.mGraph.getVertex(input.getVariable()));
				for (ReversionGraph<P,L>.Vertex theta : thetas)
					if (theta.getChild(0).equals(input)
							&& body.mGraph.containsVariable(theta.getVariable()))
						vars.add(body.mGraph.getVertex(theta.getVariable()));
				uses: for (ReversionGraph<P,L>.Vertex var : vars)
					for (ReversionGraph<P,L>.Vertex parent : var.getParents())
						if (parent.isAnyVolatile())
							for (int i = parent.getChildCount(); i-- != 0; )
								if (done |= (parent.isVolatile(i) &&
										parent.getChild(i).equals(var)))
									break uses;
			}
			if (done)
				volatil.add(injects.size() - 1);
		}
		Value<P,L> block = Value.getLoop(init, body, injects, volatil);
		return mGraph.getVertex(eval.getVariable().<P,L>getProject(),
				mGraph.getVertex(block,
				new ArrayList<ReversionGraph<P,L>.Vertex>(inputs)));
	}
	
	protected interface Evaluator<P,L,E>
			extends Function<ReversionGraph<P,L>.Vertex,E> {
		public E get(ReversionGraph<P,L>.Vertex vertex);
	}
	
	protected enum Context {True, False, Partial;}
	
	protected void markAlways(
			final Tag<Map<ReversionGraph<P,L>.Vertex,Context>> tag,
			final Set<? super ReversionGraph<P,L>.Vertex> always,
			final MultiMap<? super ReversionGraph<P,L>.Vertex,
					? super ReversionGraph<P,L>.Vertex> conds,
			final boolean throughThetas, final boolean variantConditions) {
		final WorkList<ReversionGraph<P,L>.Vertex> work = new WorkList();
		final Set<ReversionGraph<P,L>.Vertex> backwards = new HashSet();
		final Evaluator<P,L,Void> eval = new Evaluator<P,L,Void>() {
			public Void get(ReversionGraph<P,L>.Vertex vertex) {
				Map<ReversionGraph<P,L>.Vertex,Context> evaluation;
				boolean changed;
				if (vertex.hasTag(tag)) {
					evaluation = vertex.getTag(tag);
					if (evaluation == null)
						return null;
					changed = false;
				} else {
					evaluation = new
							HashMap<ReversionGraph<P,L>.Vertex,Context>() {
						public String toString() {
							return new MappedIterable
									<Entry<ReversionGraph<P,L>.Vertex,Context>,
									String>() {
								protected Set<Entry<ReversionGraph<P,L>.Vertex,
										Context>> getWrapped() {
									return entrySet();
								}
								protected String map(Entry
										<ReversionGraph<P,L>.Vertex,Context>
										entry) {
									return "[" + entry.getKey().getLabel()
											+ "->" + entry.getValue() + "]";
								}
							}.toString();
						}
					};
					if (vertex.getChildCount() == 1 && vertex.needsAnyChild()
							&& vertex.isFree())
						backwards.add(vertex);
					changed = true;
				}
				Set<ReversionGraph<P,L>.Vertex> keep = new HashSet();
				join: for (ReversionGraph<P,L>.Vertex parent
						: vertex.getParents()) {
					Map<ReversionGraph<P,L>.Vertex,Context> parents;
					if (!parent.hasTag(tag))
						parents = java.util.Collections.emptyMap();
					else
						parents = parent.getTag(tag);
					//TODO better handling of loop-dependent code
					if (parent.getHead() != null && parent.getHead().isPhi()
							&& !parent.getChild(0).equals(vertex)
							&& (variantConditions
							|| !parent.getChild(0).isVariant())) {
						if (parents != null) {
							for (ReversionGraph<P,L>.Vertex cond
									: parents.keySet()) {
								keep.add(cond);
								if (!evaluation.containsKey(cond)) {
									changed = true;
									evaluation.put(cond, Context.Partial);
								}
							}
						} else {
							Context context
									= parent.getChild(1).equals(vertex)
									? Context.True : Context.False;
							keep.add(parent.getChild(0));
							Context old = evaluation.put(parent.getChild(0),
									context);
							if (old != context) {
								changed = true;
								if (old != null && old != Context.Partial) {
									evaluation = null;
									break join;
								}
							}
						}
						continue;
					} else if (parent.getHead() != null
							&& parent.getHead().isTheta()
							&& !parent.getChild(0).equals(vertex))
						continue;
					if (parents == null) {
						evaluation = null;
						changed = true;
						break;
					}
					for (Entry<ReversionGraph<P,L>.Vertex,Context> entry
							: parents.entrySet()) {
						keep.add(entry.getKey());
						if (entry.getValue() == Context.Partial) {
							if (!evaluation.containsKey(entry.getKey())) {
								changed = true;
								evaluation.put(entry.getKey(),
										entry.getValue());
							}
						} else {
							Context old = evaluation.put(entry.getKey(),
									entry.getValue());
							if (old != entry.getValue()) {
								changed = true;
								if (old != null && old != Context.Partial) {
									evaluation = null;
									break join;
								}
							}
						}
					}
				}
				if (evaluation != null && keep.size() < evaluation.size()) {
					changed = true;
					evaluation.keySet().retainAll(keep);
				}
				vertex.setTag(tag, evaluation);
				if (changed) {
					work.addAll(vertex.getChildren());
					if (evaluation == null) {
						always.add(vertex);
						if (vertex.getHead() != null
								&& vertex.getHead().isPhi())
							conds.addValue(vertex.getChild(0), vertex);
					}
				}
				return null;
			}
		};
		for (ReversionGraph<P,L>.Vertex vertex : mGraph.getSignificant()) {
			vertex.setTag(tag, null);
			always.add(vertex);
			if (vertex.getHead() != null && vertex.getHead().isPhi())
				conds.addValue(vertex.getChild(0), vertex);
			work.addAll(vertex.getChildren());
		}
		while (!work.isEmpty())
			eval.get(work.pop());
		for (ReversionGraph<P,L>.Vertex backward : backwards) {
			backward.setTag(tag, backward.getChild(0).getTag(tag));
			if (backward.getTag(tag) == null)
				always.add(backward);
		}
	}
	
	protected void processUnlooping() {
		if (!mGraph.containsEvals())
			return;
		
		redo: while (true) {
			final Tag<Map<ReversionGraph<P,L>.Vertex,Context>> safe
					= new NamedTag("Safe");
			markAlways(safe, new HashSet(), new HashMultiMap(), false, false);
			
			for (ReversionGraph<P,L>.Vertex vertex : mGraph.getVertices())
				if (vertex.isVariant() || vertex.getHead() != null)
					vertex.clearTag();
				else if (vertex.isConstant() || vertex.isVariable()
						|| vertex.isParameter())
					vertex.setTag(safe, null);

			for (final ReversionGraph<P,L>.Vertex eval : mGraph.getVertices())
				if (!eval.isVariant() && eval.getHead() != null
						&& (eval.getHead().isEval() || eval.getHead().isPass())
						&& eval.hasTag(safe) && new Evaluator<P,L,Boolean>() {
							private final Tag<Void> mChecked
									= new NamedTag("Checked");
							public Boolean get(
									ReversionGraph<P,L>.Vertex vertex) {
								if (vertex.isVariant()
										|| vertex.getHead() != null
										&& vertex.getHead().isPass()) {
									if (vertex.hasTag(mChecked))
										return false;
									vertex.setTag(mChecked);
									for (ReversionGraph<P,L>.Vertex child
											: vertex.getChildren())
										if (get(child))
											return true;
									return false;
								}
								if (!vertex.hasTag(safe))
									return true;
								if (vertex.getTag(safe) == null)
									return false;
								if (eval.getTag(safe) == null)
									return true;
								Map<ReversionGraph<P,L>.Vertex,Context>
										evaluation = vertex.getTag(safe);
								for (Entry<ReversionGraph<P,L>.Vertex,Context>
										entry : eval.getTag(safe).entrySet())
									if (!evaluation.containsKey(entry.getKey()))
										return true;
									else if (entry.getValue()
											== Context.Partial)
										;
									else if (evaluation.get(entry.getKey())
											!= entry.getValue())
										return true;
								return false;
							}
						}.get(eval.getChild(0))) {
					ReversionGraph<P,L>.Vertex pass = eval.getHead().isPass()
							? eval : eval.getChild(1);
					pass.rewrite(mGraph.getVertex(
							FlowValue.<P,L>createPhi(),
							pass.getChild(0).getEval0(
									pass.getHead().getLoopDepth()),
							mGraph.getVertex(FlowValue.<P,L>createZero()),
							mGraph.getVertex(FlowValue.<P,L>createSuccessor(),
							mGraph.getVertex(pass.getLabel(),
							pass.getChild(0).getShift(
									pass.getHead().getLoopDepth())))));
					rewriteModifications();
					mGraph.trimInsignificant();
					continue redo;
				}
			return;
		}
	}
	
	protected void processHangers() {
		if (!mGraph.containsEvals())
			return;

		final Tag<IntMap<Boolean>> tag = new NamedTag("Reaches Self");
		Evaluator<P,L,Boolean> self = new Evaluator<P,L,Boolean>() {
			public Boolean get(ReversionGraph<P,L>.Vertex vertex) {
				if (!vertex.isVariant())
					return null;
				int loop = vertex.getMaxVariant();
				IntMap<Boolean> reach = vertex.hasTag(tag)
						? vertex.getTag(tag) : new ArrayIntMap(loop + 1);
				if (reach.containsKey(loop))
					return reach.get(loop);
				vertex.setTag(tag, reach);
				Boolean result = get(vertex, null, loop, new HashSet());
				reach.put(loop, result);
				return result;
			}
			public Boolean get(ReversionGraph<P,L>.Vertex vertex,
					ReversionGraph<P,L>.Vertex target, int loop,
					Set<ReversionGraph<P,L>.Vertex> reached) {
				if (!vertex.isVariant(loop))
					return null;
				if (target == null)
					target = vertex;
				else if (vertex.equals(target))
					return Boolean.TRUE;
				IntMap<Boolean> reach = vertex.hasTag(tag)
						? vertex.getTag(tag) : new ArrayIntMap(loop + 1);
				vertex.setTag(tag, reach);
				if (reach.containsKey(loop) && reach.get(loop) != Boolean.TRUE)
					return reach.get(loop);
				if (!reached.add(vertex))
					return null;
				boolean hanger = false;
				for (ReversionGraph<P,L>.Vertex child : vertex.getChildren()) {
					Boolean result = get(child, target, loop, reached);
					if (result == null)
						;
					else if (result) {
						reach.put(loop, Boolean.TRUE);
						return Boolean.TRUE;
					} else
						hanger = true;
				}
				
				if (!hanger && vertex.isTheta()
						&& vertex.getHead().getLoopDepth() == loop
						&& (target == vertex //TODO validate change
						|| get(vertex, null, loop, new HashSet()) == Boolean.FALSE))
					hanger = true;
				return hanger ? Boolean.FALSE : null;
			}
		};
		Evaluator<P,L,Void> redoSelf = new Evaluator<P,L,Void>() {
			public Void get(ReversionGraph<P,L>.Vertex vertex) {
				get(vertex, vertex.getMaxVariant(), new HashSet());
				return null;
			}
			public void get(ReversionGraph<P,L>.Vertex vertex, int loop,
					Set<ReversionGraph<P,L>.Vertex> reached) {
				if (!vertex.isVariant(loop))
					return;
				if (!vertex.hasTag(tag))
					return;
				IntMap<Boolean> reach = vertex.getTag(tag);
				if (reach.containsKey(loop) && reach.get(loop) == null)
					return;
				if (!reached.add(vertex))
					return;
				reach.remove(loop);
				for (ReversionGraph<P,L>.Vertex parent : vertex.getParents())
					get(parent, loop, reached);
			}
		};
		
		redo: while (true) {
			for (ReversionGraph<P,L>.Vertex vertex : mGraph.getVertices())
				if (self.get(vertex) == Boolean.TRUE) {
					boolean convert = false;
					for (ReversionGraph<P,L>.Vertex child
							: vertex.getChildren())
						if (child.getMaxVariant() == vertex.getMaxVariant()
								&& self.get(child) == Boolean.FALSE) {
							convert = true;
							break;
						}
					if (!convert)
						continue;
					vertex.rewrite(mGraph.getVertex(
							FlowValue.<P,L>createTheta(vertex.getMaxVariant()),
							vertex.getEval0(vertex.getMaxVariant()),
							vertex.getShift(vertex.getMaxVariant())));
					redoSelf.get(vertex.getRewriteSelf());
					rewriteModifications();
					mGraph.trimInsignificant();
					continue redo;
				}
			break;
		}

		redo: while (true) {
			for (ReversionGraph<P,L>.Vertex vertex : mGraph.getVertices()) {
				if (!vertex.isEval() && !(vertex.isPass()
						&& vertex.getChild(0).isVariant(
						vertex.getHead().getLoopDepth())))
					continue;
				Boolean result = self.get(vertex.getChild(0));
				ReversionGraph<P,L>.Vertex pass = vertex.isEval()
						? vertex.getChild(1) : vertex;
				int loop = vertex.getHead().getLoopDepth();
				if (result == Boolean.FALSE)
					pass.rewrite(mGraph.getVertex(
							FlowValue.<P,L>createPhi(),
							pass.getChild(0).getEval0(loop),
							mGraph.getVertex(FlowValue.<P,L>createZero()),
							mGraph.getVertex(
									FlowValue.<P,L>createSuccessor(),
									mGraph.getVertex(pass.getLabel(),
									pass.getChild(0).getShift(loop)))));
				else if (result == null && vertex.isEval()) {
					ReversionGraph<P,L>.Vertex child = vertex.getChild(0);
					if (child.getHead() != null && child.getHead().isPhi()
							&& child.getChild(0).isVariant(loop)
							&& (child.getChild(1).isVariant(loop)
							|| child.getChild(2).isVariant(loop)))
						continue;
					if (child.getHead() != null && child.getHead().isEval())
						continue; //TODO This should be doable with some rework
					if (child.getChildCount() == 1)
						vertex.rewrite(mGraph.getVertex(child.getLabel(),
								mGraph.getVertex(vertex.getLabel(),
										child.getChild(0), pass)));
					else {
						ReversionGraph<P,L>.Vertex[] children = new
								ReversionGraph.Vertex[child.getChildCount()];
						for (int i = child.getChildCount(); i-- != 0; )
							children[i] = child.getChild(i).isVariant(loop)
									? mGraph.getVertex(vertex.getLabel(),
									child.getChild(i), pass)
									: child.getChild(i);
						vertex.rewrite(mGraph.getVertex(child.getLabel(),
								children));
					}
				}/* else if (result == Boolean.TRUE && vertex.isEval()
						&& !vertex.getChild(0).isTheta()) {
					ReversionGraph<P,L>.Vertex child = vertex.getChild(0);
					child.rewrite(mGraph.getVertex(
							FlowValue.<P,L>createTheta(loop),
							child.getEval0(loop), child.getShift(loop)));
					TODO good idea, figure out how to make it work
				}*/ else
					continue;
				rewriteModifications();
				mGraph.trimInsignificant();
				continue redo;
			}
			break;
		}
	}
	
	protected void processPhis() {
		if (!mGraph.containsPhis())
			return;
		
		// First tag all the nodes that are always evaluated
		// Also, build a map from conditions to phis of that condition
		// that are always evaluated
		Tag<Map<ReversionGraph<P,L>.Vertex,Context>> evaluation
				= new NamedTag("Evaluation");
		final MultiMap<ReversionGraph<P,L>.Vertex,ReversionGraph<P,L>.Vertex>
				phis = new HashMultiMap();
		markAlways(evaluation, new HashSet(), phis, true, true);
		for (Iterator<ReversionGraph<P,L>.Vertex> conds
				= phis.keySet().iterator(); conds.hasNext(); )
			if (conds.next().isVariant())
				conds.remove();
		if (phis.isEmpty())
			return;
		final Set<ReversionGraph<P,L>.Vertex> phiSet = new HashSet();
		for (ReversionGraph<P,L>.Vertex phi : phis.values())
			phiSet.add(phi);
		Map<ReversionGraph<P,L>.Vertex,ReversionGraph<P,L>.Vertex> phiCond
				= new HashMap();
		for (ReversionGraph<P,L>.Vertex cond : phis.keySet())
			phiCond.put(cond, phis.get(cond).iterator().next());
		
		final Grouping<ReversionGraph<P,L>.Vertex> partition
				= new HashGrouping<ReversionGraph<P,L>.Vertex>(phiSet) {
			protected boolean swap(ReversionGraph<P,L>.Vertex leftRep,
					ReversionGraph<P,L>.Vertex rightRep) {
				return !phiSet.contains(leftRep) && phiSet.contains(rightRep);
			}
		};
		for (ReversionGraph<P,L>.Vertex vertex : mGraph.getVertices())
			if (vertex.isVariant()) {
				for (ReversionGraph<P,L>.Vertex child : vertex.getChildren())
					if (child.isVariant())
						partition.group(vertex, child);
			} else if (vertex.getHead() != null && vertex.getHead().isEval()) {
				if (vertex.getChild(0).isVariant())
					partition.group(vertex, vertex.getChild(0));
				partition.group(vertex, vertex.getChild(1));
			} else if (vertex.getHead() != null && vertex.getHead().isPass()) {
				if (vertex.getChild(0).isVariant())
					partition.group(vertex, vertex.getChild(0));
			} else if (vertex.getChildCount() == 1 && vertex.isFree()
					&& vertex.needsChild(0))
				partition.group(vertex, vertex.getChild(0));
			else if (vertex.isConstant() || vertex.isVariable()
					|| vertex.isParameter())
				vertex.setTag(evaluation, null);

		// Figure out condition commonalities
		final Grouping<ReversionGraph<P,L>.Vertex> subConds
				= new HashGrouping();
		for (final ReversionGraph<P,L>.Vertex cond : phis.keySet())
			new Evaluator<P,L,Void>() {
				public Void get(ReversionGraph<P,L>.Vertex vertex) {
					subConds.group(cond, vertex);
					if (!phis.containsKey(vertex)
							&& vertex.getHead() != null) {
						if (vertex.getHead().isNegate() && !vertex.isLeaf())
							get(vertex.getChild(0));
						else if (vertex.getHead().isPhi()) {
							get(vertex.getChild(0));
							get(vertex.getChild(1));
							get(vertex.getChild(2));
						}
					}
					return null;
				}
			}.get(cond);
		for (ReversionGraph<P,L>.Vertex phi : phis.values())
			for (ReversionGraph<P,L>.Vertex other : phis.values())
				if (subConds.isGrouped(phi.getChild(0), other.getChild(0)))
					partition.group(phi, other);

		final List<ReversionGraph<P,L>.Vertex> condBasis
				= new ArrayList(phis.keySet());
		final ArrayIntPartition condPartition
				= new ArrayIntPartition(0, condBasis.size() - 1);
		for (int i = condBasis.size(); i-- != 0; )
			for (int j = i; j-- != 0; )
				if (partition.isGrouped(condBasis.get(i), condBasis.get(j)))
					condPartition.group(j, i);
		final Map<ReversionGraph<P,L>.Vertex,Integer> condUnbasis
				= new HashMap<ReversionGraph<P,L>.Vertex,Integer>();
		for (int i = condBasis.size(); i-- != 0; )
			condUnbasis.put(condBasis.get(i),
					condPartition.getRepresentative(i));
		
		final Tag<Pair<BitIntSet,BitIntSet>> tag = new NamedTag("Claim");
		for (ReversionGraph<P,L>.Vertex vertex : mGraph.getVertices()) {
			if (vertex.getTag(evaluation) != null)
				for (ReversionGraph<P,L>.Vertex cond
						: vertex.getTag(evaluation).keySet())
					if (phiCond.containsKey(cond))
						partition.group(phiCond.get(cond), vertex);
			vertex.setTag(tag, new Pair(new BitIntSet(), new BitIntSet()));
			ReversionGraph<P,L>.Vertex rep
					= partition.getRepresentative(vertex);
			if (rep.getChildCount() != 3)
				;
			else if (condUnbasis.containsKey(rep.getChild(0))) {
				vertex.getTag(tag).getFirst().add(
						condUnbasis.get(rep.getChild(0)));
				vertex.getTag(tag).getSecond().add(
						condUnbasis.get(rep.getChild(0)));
			}
		}

		// Bridge self dependencies in groups
		final WorkList<ReversionGraph<P,L>.Vertex> work = new WorkList();
		work.addAll(mGraph.getVertices());
		Evaluator<P,L,Void> reachedByEval = new Evaluator<P,L,Void>() {
			public Void get(ReversionGraph<P,L>.Vertex vertex) {
				BitIntSet reachedBy = vertex.getTag(tag).getFirst();
				boolean changed = false;
				for (ReversionGraph<P,L>.Vertex parent : vertex.getParents())
					changed |= reachedBy.addAll(parent.getTag(tag).getFirst());
				if (changed)
					work.addAll(vertex.getChildren());
				return null;
			}
		};
		while (!work.isEmpty())
			reachedByEval.get(work.pop());

		work.addAll(mGraph.getVertices());
		Evaluator<P,L,Void> reachesEval = new Evaluator<P,L,Void>() {
			public Void get(ReversionGraph<P,L>.Vertex vertex) {
				BitIntSet reaches = vertex.getTag(tag).getSecond();
				boolean changed = false;
				for (ReversionGraph<P,L>.Vertex child : vertex.getChildren())
					changed |= reaches.addAll(
							child.getTag(tag).getSecond());
				if (changed)
					work.addAll(vertex.getParents());
				return null;
			}
		};
		while (!work.isEmpty())
			reachesEval.get(work.pop());
		
		for (boolean changed = true; changed; ) {
			changed = false;
			for (ReversionGraph<P,L>.Vertex vertex : mGraph.getVertices())
				for (IntIterator reachedBys
						= vertex.getTag(tag).getFirst().iterator();
						reachedBys.hasNext(); ) {
					ReversionGraph<P,L>.Vertex reachedBy
							= phiCond.get(condBasis.get(reachedBys.nextInt()));
					for (IntIterator reaches = vertex.getTag(tag)
							.getSecond().iterator(); reaches.hasNext(); )
						if (partition.isGrouped(reachedBy,
								phiCond.get(condBasis.get(reaches.nextInt()))))
							changed |= partition.group(reachedBy, vertex);
				}
		}
		
		for (int i = condBasis.size(); i-- != 0; )
			for (int j = i; j-- != 0; )
				if (partition.isGrouped(condBasis.get(i), condBasis.get(j)))
					condPartition.group(j, i);
		for (int i = condBasis.size(); i-- != 0; )
			condUnbasis.put(condBasis.get(i),
					condPartition.getRepresentative(i));
		
		// Build the branch blocks and figure out how the plug in
		final Map<ReversionGraph<P,L>.Vertex,Integer> claims = new HashMap();
		final IntMap<BranchBlock<P,L>> blocks = new ArrayIntMap();
		final IntMap<BlockInliner<P,L>> inliners = new ArrayIntMap();
		final IntMap<Set<ReversionGraph<P,L>.Vertex>> inputs
				= new ArrayIntMap();
		final IntMap<Set<ReversionGraph<P,L>.Vertex>> outputs
				= new ArrayIntMap();
		final Evaluator<P,L,Void> reacher = new Evaluator<P,L,Void>() {
			private final Tag<Void> mTag = new NamedTag("Reached");
			public Void get(ReversionGraph<P,L>.Vertex vertex) {
				if (vertex.hasTag(mTag))
					return null;
				Integer claim = claims.get(vertex);
				if (claim == null) {
					vertex.setTag(mTag);
					for (ReversionGraph<P,L>.Vertex child
							: vertex.getChildren())
						get(child);
				} else if (outputs.get(claim).add(vertex))
					blocks.get(claim).modify(vertex.getVariable(),
							inliners.get(claim).get(vertex));
				return null;
			}
		};
		for (ReversionGraph<P,L>.Vertex phi : partition.getRepresentatives()) {
			Integer key = condUnbasis.get(phi.getChild(0));
			if (key != null) {
				final Set<? extends ReversionGraph<P,L>.Vertex> group
						= partition.getGroup(phi);
				for (ReversionGraph<P,L>.Vertex vertex : group)
					claims.put(vertex, key);
				final Set<ReversionGraph<P,L>.Vertex> input = new HashSet();
				final Set<ReversionGraph<P,L>.Vertex> output = new HashSet();
				BranchBlock<P,L> block
						= new BranchBlock<P,L>(mReverter, getOpAmbassador());
				blocks.put(key, block);
				inliners.put(key, new BlockInliner<P,L>(block) {
					public boolean inline(ReversionGraph<P,L>.Vertex vertex) {
						return vertex.isVariable() || !group.contains(vertex);
					}
					public void inlined(ReversionGraph<P,L>.Vertex vertex) {
						if (input.add(vertex))
							reacher.get(vertex);
						output.remove(vertex);
					}
					public void reconsidered(ReversionGraph<P,L>.Vertex vertex)
							{
						input.remove(vertex);
					}
				});
				inputs.put(key, input);
				outputs.put(key, output);
			}
		}
		for (ReversionGraph<P,L>.Vertex vertex : mGraph.getSignificant())
			reacher.get(vertex);
		
		// Insert the branch blocks
		IntMap<ReversionGraph<P,L>.Vertex> nodes = new ArrayIntMap();
		for (IntIterator parts = blocks.keySet().iterator(); parts.hasNext(); )
				{
			int part = parts.nextInt();
			ReversionGraph<P,L>.Vertex chosen = partition.getRepresentative(
					phiCond.get(condBasis.get(part))).getChild(0);
			BranchBlock<P,L> block = blocks.get(part);
			block.setBranchCondition(inliners.get(part).get(chosen));
			List<ReversionGraph<P,L>.Vertex> children
					= new ArrayList(inputs.get(part));
			List<Variable> vars = new ArrayList(children.size());
			IncreasingIntSet volatil = new BitIntSet();
			for (ReversionGraph<P,L>.Vertex child : children) {
				vars.add(child.getVariable());
				ReversionGraph<P,L>.Vertex var
						= block.mGraph.getVertex(child.getVariable());
				parents: for (ReversionGraph<P,L>.Vertex parent
						: var.getParents())
					for (int i = parent.getChildCount(); i-- != 0; )
						if (parent.isVolatile(i)
								&& parent.getChild(i).equals(var)) {
							volatil.add(vars.size() - 1);
							break parents;
						}
			}
			Value<P,L> branch = Value.getBranch(block, vars, volatil);
			ReversionGraph<P,L>.Vertex node
					= mGraph.getVertex(branch, children);
			nodes.put(part, node);
		}

		// Rewrite branch outputs
		for (IntIterator parts = blocks.keySet().iterator(); parts.hasNext(); )
				{
			int part = parts.nextInt();
			ReversionGraph<P,L>.Vertex node = nodes.get(part);
			for (ReversionGraph<P,L>.Vertex output : outputs.get(part))
				output.rewrite(mGraph.getVertex(
						output.getVariable().<P,L>getProject(), node));
		}

		rewriteModifications();
		mGraph.trimInsignificant();
	}
	
	protected void processLoopBlocks() {
		if (!mGraph.containsBlocks())
			return;
		
		// group independent loops with same pass condition
		MultiMap<Variable,ReversionGraph<P,L>.Vertex> conds
				= new HashMultiMap<Variable,ReversionGraph<P,L>.Vertex>();
		for (ReversionGraph<P,L>.Vertex vertex : mGraph.getVertices())
			if (vertex.getLabel().isLoop())
				conds.addValue(vertex.getLabel().getPassVariable(), vertex);
		for (final Variable cond : conds.keySet()) {
			Set<ReversionGraph<P,L>.Vertex> loops = conds.get(cond);
			if (loops.size() < 2)
				continue;
			final UniqueLabeledGraph<ReversionGraph<P,L>.Vertex> dag
					= new UniqueLabeledGraph<ReversionGraph<P,L>.Vertex>();
			for (final ReversionGraph<P,L>.Vertex loop : loops) {
				dag.getVertex(loop);
				final Tag<Void> use = new NamedTag<Void>("Use");
				new Evaluator<P,L,Void>() {
					public Void get(ReversionGraph<P,L>.Vertex vertex) {
						if (vertex.hasTag(use))
							return null;
						vertex.setTag(use, null);
						if (vertex != loop && vertex.getLabel().isLoop()
								&& vertex.getLabel().getPassVariable()
								.equals(cond))
							dag.getVertex(loop).addChild(
									dag.getVertex(vertex));
						else
							for (ReversionGraph<P,L>.Vertex child
									: vertex.getChildren())
								get(child);
						return null;
					}
				}.get(loop);
			}
			while (!dag.getVertices().isEmpty()) {
				Collection<? extends ReversionGraph<P,L>.Vertex> leaves
						= dag.getLabels(dag.getLeaves());
				if (leaves.size() >= 2)
					processLoopBlocks(leaves);
				dag.removeVertices(dag.getLeaves());
			}
		}
		mGraph.trimInsignificant();
		rewriteModifications();
	}
	
	protected void processLoopBlocks(
			Collection<? extends ReversionGraph<P,L>.Vertex> loops) {
		Set<ReversionGraph<P,L>.Vertex> outputs
				= new HashSet<ReversionGraph<P,L>.Vertex>();
		List<ReversionGraph<P,L>.Vertex> children = new ArrayList();
		List<Variable> inputs = new ArrayList();
		IncreasingIntSet volatil = new BitIntSet();
		for (ReversionGraph<P,L>.Vertex loop : loops) {
			BlockValue<P,L> block = loop.getLabel().getBlockSelf();
			for (int i = loop.getChildCount(); i-- != 0; ) {
				if (!inputs.contains(block.getInput(i))) {
					inputs.add(block.getInput(i));
					children.add(loop.getChild(i));
				}
				if (loop.isVolatile(i))
					volatil.add(inputs.indexOf(block.getInput(i)));
			}
			outputs.addAll(loop.getParents());
		}
		FallBlock<P,L> init = new FallBlock<P,L>(mReverter, getOpAmbassador(),
				Collections.mapCollection(loops,
				new Function<ReversionGraph<P,L>.Vertex,FallBlock<P,L>>() {
			public FallBlock<P,L> get(ReversionGraph<P,L>.Vertex vertex) {
				return vertex.getLabel().getInitializer();
			}
		}));
		BranchBlock<P,L> body = new BranchBlock<P,L>(mReverter,
				getOpAmbassador(), Collections.mapCollection(loops,
				new Function<ReversionGraph<P,L>.Vertex,BranchBlock<P,L>>() {
			public BranchBlock<P,L> get(ReversionGraph<P,L>.Vertex vertex) {
				return vertex.getLabel().getBody();
			}
		}));
		Value<P,L> block = Value.getLoop(init, body, inputs, volatil);
		ReversionGraph<P,L>.Vertex loop = mGraph.getVertex(block, children);
		for (ReversionGraph<P,L>.Vertex output : outputs)
			output.rewrite(mGraph.getVertex(
					output.getVariable().<P,L>getProject(), loop));
		rewriteModifications();
		mGraph.trimInsignificant();
	}
	
	private void processVolatility() {
		tryagain: while (true) {
			for (ReversionGraph<P,L>.Vertex vertex : mGraph.getVertices())
				if (vertex.isAnyVolatile())
					for (int i = vertex.getChildCount(); i-- != 0; )
						if (vertex.isVolatile(i)
								&& vertex.getChild(i).getParentCount() > 1) {
							processVolatility(vertex.getChild(i));
							continue tryagain;
						}
			return;
		}
	}
	
	private void processVolatility(ReversionGraph<P,L>.Vertex vertex) {
		Variable variable = vertex.getVariable();
		ConstraintGraph constraints = new ConstraintGraph();
		ConstraintGraph needs = new ConstraintGraph();
		final List<ReversionGraph<P,L>.Vertex> basis = new ArrayList();
		List<ConstraintVertex> needMap = new ArrayList<ConstraintVertex>();
		for (ReversionGraph<P,L>.Vertex parent : vertex.getParents())
			for (int i = parent.getChildCount(); i-- != 0; )
				if (parent.getChild(i).equals(vertex)) {
					if (!parent.isVolatile(i))
						throw new IllegalArgumentException();
					constraints.addVertex(parent, i, basis.size());
					needMap.add(needs.addVertex(parent, i, basis.size()));
					basis.add(parent);
					break;
				}
		Evaluator<P,L,IntSet> depends = new Evaluator<P,L,IntSet>() {
			final Tag<IntSet> mTag = new NamedTag("Dependencies");
			public IntSet get(ReversionGraph<P,L>.Vertex vertex) {
				if (vertex.hasTag(mTag))
					return vertex.getTag(mTag);
				IntSet dependencies
						= IntCollections.createBoundedBitSet(basis.size() - 1);
				if (basis.contains(vertex))
					dependencies.add(basis.indexOf(vertex));
				for (ReversionGraph<P,L>.Vertex child : vertex.getChildren())
					dependencies.addAll(get(child));
				vertex.setTag(mTag, dependencies);
				return dependencies;
			}
		};
		for (ConstraintVertex use : constraints.getVertices())
			for (ConstraintVertex conflict : constraints.getVertices())
				if (!use.equals(conflict)
						&& depends.get(use.getVertex()).contains(
								conflict.getBasisIndex())) {
					needMap.get(use.getBasisIndex()).addChild(
							needMap.get(conflict.getBasisIndex()));
					use.addChild(conflict);
				}
		List<ConstraintVertex> weakSort = toposort(constraints, false);
		if (weakSort == null)
			throw new RuntimeException();
		for (ConstraintVertex use : constraints.getVertices())
			for (ConstraintVertex conflict : constraints.getVertices())
				if (!use.equals(conflict)
						&& !use.getVertex().isEquivalent(use.getVolatile(),
								conflict.getVertex(), conflict.getVolatile()))
					use.addChild(conflict);
		List<ConstraintVertex> sort = toposort(constraints, true);
		if (sort == null) {
			sort = weakSort;
			mReverter.setUnsound();
		}
		ReversionGraph<P,L>.Vertex last
				= sort.remove(sort.size() - 1).getVertex();
		ReversionGraph<P,L>.Vertex prior = vertex;
		for (ConstraintVertex use : sort) {
			ReversionGraph<P,L>.Vertex[] children =
					new ReversionGraph.Vertex[use.getVertex().getChildCount()];
			for (int i = children.length; i-- != 0; )
				children[i] = use.getVertex().getChild(i).equals(vertex)
						? prior : use.getVertex().getChild(i);
			ReversionGraph<P,L>.Vertex chained = mGraph.getVertex(
					use.getVertex().getChainVersion(use.getVolatile()),
					children);
			prior = use.getVertex().getChainProjectVolatile(use.getVolatile(),
					chained);
			use.getVertex().rewriteChainProjectValue(use.getVolatile(),
					chained);
			if (!prior.hasVariable())
				prior.setVariable(variable);
			if (chained.getVariable() == null)
				chained.setVariable(new Variable());
		}
		ReversionGraph<P,L>.Vertex[] children
				= new ReversionGraph.Vertex[last.getChildCount()];
		for (int i = children.length; i-- != 0; )
			children[i] = last.getChild(i).equals(vertex)
					? prior : last.getChild(i);
		ReversionGraph<P,L>.Vertex chained
				= mGraph.getVertex(last.getLabel(), children);
		if (!last.equals(chained))
			last.rewrite(chained);
		rewriteModifications();
		mGraph.trimInsignificant();
	}
	
	private List<ConstraintVertex> toposort(ConstraintGraph graph,
			boolean clear) {
		if (clear)
			for (ConstraintVertex vertex : graph.getVertices())
				vertex.mVisited = false;
		final List<ConstraintVertex> sort
				= new ArrayList(graph.getVertices().size());
		Pattern<ConstraintVertex> dfs = new AbstractPattern<ConstraintVertex>(){
			public boolean matches(ConstraintVertex vertex) {
				if (vertex.mVisited)
					return false;
				if (vertex.mStack)
					return true;
				vertex.mStack = true;
				boolean loops = false;
				for (ConstraintVertex child : vertex.getChildren())
					loops |= matches(child);
				vertex.mStack = false;
				vertex.mVisited = true;
				sort.add(vertex);
				return loops;
			}
		};
		for (ConstraintVertex vertex : graph.getVertices())
			if (dfs.matches(vertex))
				return null;
		return sort;
	}
	
	protected FallCFG<P,L> serialize() {
		mGraph.trimInsignificant(); //TODO Why is this necessary? May work now
		processRedundants();
		processUnlooping();
		processHangers();
		processPhis();
		processEvals();
		processLoopBlocks();
		processVolatility();

		if (!mGraph.containsBlocks())
			return new FallCFG<P,L>() {
				public RevertBlock<L,P> addToCFG(RevertCFG<L,P,?> cfg,
						RevertBlock<L,P> target) {
					return target;
				}
				public void chain(Variable input,
						Collection<? super PairInt<OpExpression<L>>> uses) {
				}
			};
		
		final Tag<Boolean> checked = new NamedTag("Checked");
		Evaluator<P,L,Boolean> check = new Evaluator<P,L,Boolean>() {
			public Boolean get(ReversionGraph<P,L>.Vertex vertex) {
				return get(vertex, true);
			}
			public boolean get(ReversionGraph<P,L>.Vertex vertex, boolean root)
					{
				if (vertex.hasTag(checked))
					return vertex.getTag(checked);
				if (vertex.isLeaf())
					return true;
				if (!root && (vertex.getLabel().isBranch()
						|| vertex.getLabel().isLoop()))
					return false;
				boolean result = true;
				for (ReversionGraph<P,L>.Vertex child : vertex.getChildren())
					if (!(result &= get(child, false)))
						break;
				vertex.setTag(checked, result);
				return result;
			}
		};
		for (ReversionGraph<P,L>.Vertex vertex : mGraph.getVertices())
			if ((vertex.getLabel().isBranch() || vertex.getLabel().isLoop())
					&& check.get(vertex))
				return serialize(vertex);
		throw new IllegalStateException();
	}
	
	protected FallCFG<P,L> serialize(ReversionGraph<P,L>.Vertex block) {
		BlockValue<P,L> label = block.getLabel().getBlockSelf();
		FallBlock<P,L> fall = new FallBlock(mReverter, getOpAmbassador());
		BlockInliner<P,L> convert = fall.getConverter();
		final Tag<ReversionGraph<P,L>.Vertex> converted = convert.mConverted;
		for (int i = block.getChildCount(); i-- != 0; )
			fall.modify(label.getInput(i), convert.get(block.getChild(i)));
		for (ReversionGraph<P,L>.Vertex output : block.getParents())
			output.rewrite(mGraph.getVertex(output.getVariable()));
		rewriteModifications();
		mGraph.trimInsignificant();
		Evaluator<P,L,Boolean> stick = new Evaluator<P,L,Boolean>() {
			public Boolean get(ReversionGraph<P,L>.Vertex vertex) {
				if (vertex.isConstant())
					return false;
				if (vertex.hasTag(converted))
					return true;
				if (vertex.needsAnyChild())
					for (int i = vertex.getChildCount(); i-- != 0; )
						if (vertex.needsChild(i) && get(vertex.getChild(i)))
							return true;
				return false;
			}
		};
		//Include vertices stuck to block inputs (assumes free/sticky only)
		for (ReversionGraph<P,L>.Vertex vertex : mGraph.getVertices())
			if (!vertex.hasTag(converted) && stick.get(vertex))
				fall.modify(vertex.getVariable(), convert.get(vertex));
		Collection<ReversionGraph<P,L>.Vertex> rewrite = new ArrayCollection();
		for (ReversionGraph<P,L>.Vertex vertex : mGraph.getVertices())
			if (vertex.hasTag(converted) && !vertex.isConstant()
					&& !vertex.isVariable()) {
				boolean used = vertex.isSignificant();
				for (ReversionGraph<P,L>.Vertex parent : vertex.getParents())
					if (used |= !parent.hasTag(converted))
						break;
				if (used)
					rewrite.add(vertex);
			}
		for (ReversionGraph<P,L>.Vertex vertex : rewrite) {
			fall.modify(vertex.getVariable(), vertex.getTag(converted));
			vertex.rewrite(mGraph.getVertex(vertex.getVariable()));
		}
		rewriteModifications();
		mGraph.trimInsignificant();
		final FallCFG<P,L> top = fall;
		final FallCFG<P,L> middle;
		if (label.isLoop()) {
			final FallCFG<P,L> init = label.getInitializer();
			final BranchCFG<P,L> body = label.getBody();
			middle = new FallCFG<P,L>() {
				public RevertBlock<L,P> addToCFG(RevertCFG<L,P,?> cfg,
						RevertBlock<L,P> target) {
					RevertBlock<L,P> empty = cfg.makeBlock();
					empty.setChild(body.addToCFG(cfg, target, empty));
					return init.addToCFG(cfg, empty);
				}
				public void chain(Variable input,
						Collection<? super PairInt<OpExpression<L>>> uses) {
					init.chain(input, uses);
					body.chain(input, uses);
				}
			};
		} else if (label.isBranch()) {
			final BranchCFG<P,L> branch = label.getBranch();
			middle = new FallCFG<P,L>() {
				public RevertBlock<L,P> addToCFG(RevertCFG<L,P,?> cfg,
						RevertBlock<L,P> target) {
					return branch.addToCFG(cfg, target, target);
				}
				public void chain(Variable input,
						Collection<? super PairInt<OpExpression<L>>> uses) {
					branch.chain(input, uses);
				}
			};
		} else
			throw new UnhandledCaseException();
		final FallCFG<P,L> bottom = serialize();
		return new FallCFG<P,L>() {
			public RevertBlock<L,P> addToCFG(RevertCFG<L,P,?> cfg,
					RevertBlock<L,P> target) {
				return top.addToCFG(cfg, middle.addToCFG(cfg,
						bottom.addToCFG(cfg, target)));
			}
			public void chain(Variable input,
					Collection<? super PairInt<OpExpression<L>>> uses) {
				top.chain(input, uses);
				middle.chain(input, uses);
				bottom.chain(input, uses);
			}
		};
	}

	protected void transcribe(RevertBlock<L,P> block,
			Tag<Vertex<RevertValue<L,P>>> convertTag) {
		for (Entry<Variable,ReversionGraph<P,L>.Vertex> entry
				: mModifications.entrySet())
			block.setModification(entry.getKey(),
					block.getConverter(convertTag).get(entry.getValue()));
	}
	
	protected void chain(Variable input,
			Collection<? super PairInt<OpExpression<L>>> uses) {
		if (!mGraph.containsVariable(input))
			return;
		ReversionGraph<P,L>.Vertex vertex = mGraph.getVertex(input);
		while (vertex.hasParents()) {
			ReversionGraph<P,L>.Vertex use
					= vertex.getParents().iterator().next();
			int child = -1;
			for (int i = use.getChildCount(); i-- != 0; )
				if (use.getChild(i).equals(vertex)) {
					child = i;
					use.getUses(i, uses);
				}
			Value<P,L> chain = use.getChainVersion(child);
			ReversionGraph<P,L>.Vertex chained;
			if (chain == null)
				chained = use;
			else
				chained = mGraph.getVertex(chain, use.getChildren());
			vertex = use.getChainProjectVolatile(child, chained);
			use.rewriteChainProjectValue(child, chained);
		}
		modify(input, vertex);
		rewriteModifications();
		mGraph.trimInsignificant();
	}
	
	protected void rewriteModifications() {
		for (Entry<Variable,ReversionGraph<P,L>.Vertex> entry
				: mModifications.entrySet())
			if (entry.getValue().isRewritten())
				entry.setValue(entry.getValue().getRewrite());
	}
	
	public String toString() {return mGraph.toString();}
	
	private class ConstraintGraph
			extends GenericGraph<ConstraintGraph,ConstraintVertex> {
		public ConstraintGraph getSelf() {return this;}
		
		public ConstraintVertex addVertex(ReversionGraph<P,L>.Vertex use,
				int child, int index) {
			ConstraintVertex vertex
					= new ConstraintVertex(this, use, child, index);
			super.addVertex(vertex);
			return vertex;
		}
	}
	
	private class ConstraintVertex
			extends LabeledGenericVertex<ConstraintGraph,ConstraintVertex,
			PairInt<ReversionGraph<P,L>.Vertex>> {
		public boolean mVisited = false;
		public boolean mStack = false;
		private final int mBasisIndex;
		
		public ConstraintVertex(ConstraintGraph graph,
				ReversionGraph<P,L>.Vertex vertex, int child, int index) {
			super(graph,
					new PairInt<ReversionGraph<P,L>.Vertex>(vertex, child));
			mBasisIndex = index;
		}

		public ConstraintVertex getSelf() {return this;}
		
		public ReversionGraph<P,L>.Vertex getVertex() {
			return getLabel().getFirst();
		}
		public int getVolatile() {return getLabel().getSecond();}
		
		public int getBasisIndex() {return mBasisIndex;}
	}
	
	protected static abstract class BlockInliner<P,L>
			extends ReversionInliner<P,L> {
		public BlockInliner(Block<P,L> block) {super(block.mGraph);}
		
		protected ReversionGraph<P,L>.Vertex inlineAs(
				ReversionGraph<P,L>.Vertex vertex) {
			if (vertex.isConstant())
				return mGraph.getVertex(vertex.getLabel());
			else {
				inlined(vertex);
				return mGraph.getVertex(vertex.getVariable());
			}
		}
		
		protected boolean fullInline(ReversionGraph<P,L>.Vertex vertex) {
			return inline(vertex);
		}
		
		protected ReversionGraph<P,L>.Vertex process(
				ReversionGraph<P,L>.Vertex vertex) {
			if (vertex.hasTag(mConverted))
				return super.process(vertex);
			if (vertex.isFree()) {
				boolean needed = false;
				for (int i = vertex.getChildCount(); i-- != 0; )
					if (needed |= !fullInline(vertex.getChild(i)))
						break;
				if (!needed)
					return inlineAs(vertex);
			}
			if (!vertex.needsAnyChild())
				return super.process(vertex);
			List<ReversionGraph<P,L>.Vertex> needs
					= new WrappingArrayList<ReversionGraph<P,L>.Vertex>();
			needs.add(vertex);
			while (!needs.isEmpty()) {
				ReversionGraph<P,L>.Vertex needed = needs.remove(0);
				if (needed.hasTag(mConverted))
					continue;
				for (int i = needed.getChildCount(); i-- != 0; )
					if (needed.needsChild(i))
						needs.add(needed.getChild(i));
				ReversionGraph<P,L>.Vertex copy = shallowCopy(needed);
				if (mGraph.containsVariable(needed.getVariable()))
					if (!mGraph.getVertex(needed.getVariable()).equals(copy)) {
						mGraph.getVertex(needed.getVariable()).rewrite(copy);
						reconsidered(needed);
					}
				needed.setTag(mConverted, copy);
			}
			return vertex.getTag(mConverted);
		}

		protected void inlined(ReversionGraph<P,L>.Vertex vertex) {}
		protected void reconsidered(ReversionGraph<P,L>.Vertex vertex) {}
	}
}
