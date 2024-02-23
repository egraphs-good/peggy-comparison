package eqsat.revert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import eqsat.CFG;
import eqsat.CFGTranslator;
import eqsat.OpAmbassador;
import util.AbstractVariaticFunction;
import util.Action;
import util.Function;
import util.HashMultiMap;
import util.MultiMap;
import util.NamedTag;
import util.Tag;
import util.UnhandledCaseException;
import util.VariaticFunction;
import util.graph.AbstractGraph;
import util.graph.CExpressionGraph;
import util.graph.CExpressionGraph.Vertex;
import util.pair.Pair;
import util.pair.PairedList;
import util.pair.WrappingArrayPairedList;

public class RevertCFG<L, P, R>
		extends AbstractGraph<RevertCFG<L,P,R>,RevertBlock<L,P>>
		implements CFG<RevertCFG<L,P,R>,RevertBlock<L,P>,Variable,L,P,R> {
	protected final Set<Block> mBlocks = new HashSet<Block>();
	protected RevertBlock<L,P> mStart = null;
	protected final Block mEnd;
	protected final Set<Variable> mVariables = new HashSet<Variable>();
	protected final Set<? extends R> mReturns;
	protected final Function<? super R,? extends Variable> mReturnMap;
	protected final CExpressionGraph<RevertValue<L,P>> mModificationGraph
			= new CExpressionGraph<RevertValue<L,P>>();
	protected final OpAmbassador<L> mAmbassador;
	
	public RevertCFG(Set<? extends R> returns,
			Function<? super R,? extends Variable> returnMap,
			OpAmbassador<L> ambassador) {
		mReturns = returns;
		mReturnMap = returnMap;
		mAmbassador = ambassador;
		mBlocks.add(mEnd = new EndBlock());
	}
	
	public RevertCFG<L,P,R> getSelf() {return this;}

	public Collection<? extends RevertBlock<L,P>> getVertices() {
		return mBlocks;
	}

	/**
	 * @author stepp
	 */
	public void addVariable(Variable v) {this.mVariables.add(v);}
	public Set<? extends Variable> getVariables() {return mVariables;}
	public P getParameter(Variable variable) {
		throw new IllegalArgumentException();
	}
	public Set<? extends R> getReturns() {return mReturns;}
	public Variable getReturnVariable(R ret) {return mReturnMap.get(ret);}
	
	public RevertBlock<L,P> getStart() {return mStart;}
	public void setStart(RevertBlock<L,P> start) {mStart = start;}
	public RevertBlock<L,P> getEnd() {return mEnd;}
	
	public <E> CFGTranslator<RevertBlock<L,P>,Variable,E> getTranslator(
			final Function<P,E> parameterConverter,
			final VariaticFunction<L,E,E> converter,
			Collection<? super E> known) {
		return new CFGTranslator<RevertBlock<L,P>,Variable,E>() {
			public Function<Variable,E> getOutputs(final RevertBlock<L,P> block,
					final Function<Variable,E> inputs) {
				return new Function<Variable,E>() {
					final Function<Vertex<RevertValue<L,P>>,E> mConverter
							= new Function<Vertex<RevertValue<L,P>>,E>() {
						private final Tag<E> mTag = new NamedTag("Converted");
						public E get(Vertex<RevertValue<L,P>> vertex) {
							if (vertex.hasTag(mTag))
								return vertex.getTag(mTag);
							E result;
							if (vertex.isLeaf()) {
								if (vertex.getLabel().isParameter())
									result = parameterConverter.get(
											vertex.getLabel().getParameter());
								else if (vertex.getLabel().isVariable())
									result = inputs.get(
											vertex.getLabel().getVariable());
								else
									result = converter.get(
											vertex.getLabel().getDomain());
							} else if (vertex.getChildCount() == 1)
								result = converter.get(
										vertex.getLabel().getDomain(),
										get(vertex.getChild(0)));
							else {
								List<E> children = new ArrayList<E>(
										vertex.getChildCount());
								for (int i = 0; i < vertex.getChildCount(); i++)
									children.add(get(vertex.getChild(i)));
								result = converter.get(
										vertex.getLabel().getDomain(),
										children);
							}
							vertex.setTag(mTag, result);
							return result;
						}
					};
					
					public E get(Variable variable) {
						if (variable == null)
							return block.getBranchCondition(mConverter);
						else
							return block.getOutput(variable, inputs,
									mConverter);
					}
				};
			}
		};
	}

	public RevertBlock<L,P> makeBlock() {
		Block block = new FallBlock(null);
		mBlocks.add(block);
		return block;
	}
	public RevertBlock<L,P> makeBlock(RevertBlock<L,P> child) {
		Block block = new FallBlock(child);
		mBlocks.add(block);
		return block;
	}
	public RevertBlock<L,P> makeBlock(RevertBlock<L,P> trueChild,
			RevertBlock<L,P> falseChild) {
		Block block = new BranchBlock(trueChild, falseChild);
		mBlocks.add(block);
		return block;
	}

	protected Function<ReversionGraph<P,L>.Vertex,Vertex<RevertValue<L,P>>>
			getConverter(final Tag<Vertex<RevertValue<L,P>>> convertTag) {
		return new Function<ReversionGraph<P,L>.Vertex,
				Vertex<RevertValue<L,P>>>() {
			private final VariaticFunction<RevertValue<L,P>,
					ReversionGraph<P,L>.Vertex,Vertex<RevertValue<L,P>>>
					mConverter = new AbstractVariaticFunction<RevertValue<L,P>,
					ReversionGraph<P,L>.Vertex,Vertex<RevertValue<L,P>>>() {
				public Vertex<RevertValue<L,P>> get(RevertValue<L,P> label) {
					return mModificationGraph.getVertex(label);
				}
				public Vertex<RevertValue<L,P>> get(RevertValue<L,P> label,
						ReversionGraph<P,L>.Vertex child) {
					return mModificationGraph.getVertex(label, convert(child));
				}
				public Vertex<RevertValue<L,P>> get(RevertValue<L,P> label,
						List<? extends ReversionGraph<P,L>.Vertex> children) {
					List<Vertex<RevertValue<L,P>>> converted
							= new ArrayList(children.size());
					for (ReversionGraph<P,L>.Vertex child : children)
						converted.add(convert(child));
					return mModificationGraph.getVertex(label, converted);
				}
			};
			public Vertex<RevertValue<L,P>> get(
					ReversionGraph<P,L>.Vertex vertex) {
				if (vertex.hasTag(convertTag))
					return vertex.getTag(convertTag);
				if (vertex.isVariable())
					mVariables.add(vertex.getVariable());
				Vertex<RevertValue<L,P>> result
						= RevertValue.get(mConverter, vertex, mAmbassador);
				vertex.setTag(convertTag, result);
				return result;
			}
			private Vertex<RevertValue<L,P>> convert(
					ReversionGraph<P,L>.Vertex vertex) {
				return get(vertex);
			}
		};
	}
	
	protected abstract class Block extends RevertBlock<L,P> {
		public RevertCFG<L,P,R> getGraph() {return RevertCFG.this;}
	}
	
	protected class EndBlock extends Block {
		public boolean isLeaf() {return true;}
		public boolean hasChildren() {return false;}
		
		public List<? extends RevertBlock<L,P>> getChildren() {
			return Collections.<RevertBlock<L,P>>emptyList();
		}
		public int getChildCount() {return 0;}
		public RevertBlock<L,P> getChild(int child) {
			return getChildren().get(child);
		}

		protected void replaceChild(RevertBlock<L,P> child,
				RevertBlock<L,P> replacement) {
		}

		protected boolean deepEquals(RevertBlock<L,P> that) {
			return equals(that);
		}
	}
	
	protected class FallBlock extends Block {
		protected RevertBlock<L,P> mChild;
		
		public FallBlock(RevertBlock<L,P> child) {
			mChild = child;
			if (mChild != null)
				mChild.addParent(this);
		}
		
		public List<? extends RevertBlock<L,P>> getChildren() {
			return Collections.singletonList(mChild);
		}
		public int getChildCount() {return 1;}
		public RevertBlock<L,P> getChild(int child) {
			if (child == 0)
				return mChild;
			return getChildren().get(child);
		}
		public void setChild(RevertBlock<L,P> child) {
			if (mChild != null)
				throw new UnsupportedOperationException();
			mChild = child;
			mChild.addParent(this);
		}

		protected void replaceChild(RevertBlock<L,P> child,
				RevertBlock<L,P> replacement) {
			mChild = replacement;
			replacement.addParent(this);
		}

		protected boolean deepEquals(RevertBlock<L,P> that) {
			return equals(that) || that.getChildCount() == 1
					&& that.getChild(0) == mChild
					&& mModifications.equals(that.mModifications);
		}
	}
	
	protected class BranchBlock extends Block {
		protected RevertBlock<L,P> mTrueChild, mFalseChild;
		protected Vertex<RevertValue<L,P>> mCondition;
		
		public BranchBlock(RevertBlock<L,P> trueChild,
				RevertBlock<L,P> falseChild) {
			mTrueChild = trueChild;
			mFalseChild = falseChild;
			mTrueChild.addParent(this);
			mFalseChild.addParent(this);
		}
		
		public List<? extends RevertBlock<L,P>> getChildren() {
			return Arrays.asList(mTrueChild, mFalseChild);
		}
		public int getChildCount() {return 2;}
		public RevertBlock<L,P> getChild(int child) {
			if (child == 0)
				return mTrueChild;
			if (child == 1)
				return mFalseChild;
			return getChildren().get(child);
		}
		
		public void setBranchCondition(Vertex<RevertValue<L,P>> condition) {
			mCondition = condition;
		}
		public <E> E getBranchCondition(
				Function<Vertex<RevertValue<L,P>>,E> converter) {
			return converter.get(mCondition);
		}

		protected void replaceChild(RevertBlock<L,P> child,
				RevertBlock<L,P> replacement) {
			if (mTrueChild.equals(child))
				mTrueChild = replacement;
			if (mFalseChild.equals(child))
				mFalseChild = replacement;
			replacement.addParent(this);
		}

		protected boolean deepEquals(RevertBlock<L,P> that) {
			return equals(that) || that instanceof RevertCFG.BranchBlock
					&& that.getChild(0) == mTrueChild
					&& that.getChild(1) == mFalseChild
					&& mCondition.equals(((BranchBlock)that).mCondition)
					&& mModifications.equals(that.mModifications);
		}
	}

	public OpAmbassador<L> getOpAmbassador() {return mAmbassador;}
	
	public void simplify() {
		boolean changed;
		do {
			changed = false;
			changed |= removeDeadCode();
			changed |= removeEmptyFallBlocks();
			changed |= mergeIdenticalBlocks();
			changed |= removeCoincidentalBranches();
			changed |= mergeSequentialBlocks();
		} while (changed);
	}
	
	protected boolean removeDeadCode() {
		final PairedList<RevertBlock<L,P>,Variable> unprocessed
				= new WrappingArrayPairedList<RevertBlock<L,P>,Variable>();
		final MultiMap<RevertBlock<L,P>,Variable> used = new HashMultiMap();
		for (R ret : mReturns)
			unprocessed.add(mEnd, getReturnVariable(ret));
		for (Block block : mBlocks)
			if (block.getChildCount() > 1)
				unprocessed.add(block, null);
		Action<Pair<RevertBlock<L,P>,Variable>> process
				= new Action<Pair<RevertBlock<L,P>,Variable>>() {
			private final Map<RevertBlock<L,P>,Tag<Void>> mTags = new HashMap();
			
			public void execute(Pair<RevertBlock<L,P>,Variable> parameter) {
				RevertBlock<L,P> block = parameter.getFirst();
				Variable variable = parameter.getSecond();
				if (used.containsEntry(block, variable))
					return;
				used.addValue(block, variable);
				if (variable == null)
					process(block, ((BranchBlock)block).mCondition);
				else if (block.mModifications.containsKey(variable))
					process(block, block.mModifications.get(variable));
				else if (mStart.equals(block))
					throw new RuntimeException("Shouldn't happen: " + variable);
				else
					for (RevertBlock<L,P> parent : block.getParents())
						unprocessed.add(parent, variable);
			}
			
			private void process(final RevertBlock<L,P> block,
					Vertex<RevertValue<L,P>> vertex) {
				if (!mTags.containsKey(block))
					mTags.put(block,
							new NamedTag("Processed " + block.hashCode()));
				new Action<Vertex<RevertValue<L,P>>>() {
					private final Tag<Void> mTag = mTags.get(block);
					public void execute(Vertex<RevertValue<L,P>> vertex) {
						if (vertex.hasTag(mTag))
							return;
						vertex.setTag(mTag);
						for (Vertex<RevertValue<L,P>> child
								: vertex.getChildren())
							process(block, child);
						if (vertex.getLabel().isVariable()) {
							if (mStart.equals(block))
								throw new RuntimeException("Should not happen");
							Variable variable = vertex.getLabel().getVariable();
							for (RevertBlock<L,P> parent : block.getParents())
								unprocessed.add(parent, variable);
						}
					}
				}.execute(vertex);
			}
		};
		while (!unprocessed.isEmpty()) {
			Pair<RevertBlock<L,P>,Variable> pair
					= new Pair<RevertBlock<L,P>,Variable>(
					unprocessed.getFirst(0), unprocessed.getSecond(0));
			unprocessed.removeAt(0);
			process.execute(pair);
		}
		boolean changed = false;
		Set<Variable> variables = new HashSet<Variable>();
		for (Block block : mBlocks) {
			Set<Variable> blockUsed = used.get(block);
			variables.addAll(blockUsed);
			changed |= block.mModifications.keySet().retainAll(blockUsed);
		}
		if (changed) {
			mVariables.retainAll(variables);
			trimInsignificant();
		}
		return changed;
	}
	
	protected boolean removeEmptyFallBlocks() {
		boolean changed = false;
		for (Iterator<Block> blocks = mBlocks.iterator(); blocks.hasNext(); ) {
			Block block = blocks.next();
			if (block.mModifications.isEmpty() && block.getChildCount() == 1) {
				RevertBlock<L,P> child = block.getChild(0);
				if (block.equals(child))
					throw new RuntimeException("Bad CFG: Should never happen");
				for (RevertBlock<L,P> parent : block.getParents())
					parent.replaceChild(block, child);
				child.mParents.remove(block);
				if (block == mStart)
					mStart = child;
				blocks.remove();
				changed = true;
			}
		}
		return changed;
	}
	
	protected boolean mergeIdenticalBlocks() {
		boolean changed = false;
		for (Iterator<Block> blocks = mBlocks.iterator(); blocks.hasNext(); ) {
			Block block = blocks.next();
			if (block == mStart)
				continue;
			for (Block that : mBlocks)
				if (!block.equals(that) && block.deepEquals(that)) {
					for (RevertBlock<L,P> parent : block.getParents())
						parent.replaceChild(block, that);
					for (RevertBlock<L,P> child : block.getChildren())
						child.mParents.remove(block);
					blocks.remove();
					changed = true;
					break;
				}
		}
		return changed;
	}
	
	protected boolean removeCoincidentalBranches() {
		Collection<Block> replacements = new ArrayList<Block>();
		nextBlock: for (Iterator<Block> blocks = mBlocks.iterator();
				blocks.hasNext(); ) {
			Block block = blocks.next();
			if (block.getChildCount() < 2)
				continue;
			RevertBlock<L,P> child = block.getChild(0);
			for (RevertBlock<L,P> other : block.getChildren())
				if (!child.equals(other))
					continue nextBlock;
			if (block.equals(child))
				throw new RuntimeException("Bad CFG: Should never happen");
			Block replacement = new FallBlock(child);
			replacement.mModifications.putAll(block.mModifications);
			for (RevertBlock<L,P> parent : block.getParents())
				parent.replaceChild(block, replacement);
			child.mParents.remove(block);
			replacements.add(replacement);
			blocks.remove();
			
			/* 
			 * Needed to ensure that the start block stays valid. (steppm)
			 */
			if (block == mStart)
				mStart = replacement;
		}
		mBlocks.addAll(replacements);
		return !replacements.isEmpty();
	}

	protected boolean mergeSequentialBlocks() {
		boolean changed = false;
		for (Iterator<Block> blocks = mBlocks.iterator(); blocks.hasNext(); ) {
			final Block block = blocks.next();
			if (block.getChildCount() == 1
					&& block.getChild(0).getParentCount() == 1) {
				RevertBlock<L,P> child = block.getChild(0);
				if (block.equals(child))
					throw new RuntimeException("Bad CFG: Should never happen");
				Set<Variable> modified
						= new HashSet<Variable>(block.mModifications.keySet());
				modified.addAll(child.mModifications.keySet());
				Function<Vertex<RevertValue<L,P>>,Vertex<RevertValue<L,P>>>
						inline = new Function<Vertex<RevertValue<L,P>>,
						Vertex<RevertValue<L,P>>>() {
					private final Tag<Vertex<RevertValue<L,P>>> inlined
							= new NamedTag("Inlined");
					public Vertex<RevertValue<L,P>> get(
							Vertex<RevertValue<L,P>> vertex) {
						if (vertex.hasTag(inlined))
							return vertex.getTag(inlined);
						Vertex<RevertValue<L,P>> result;
						if (vertex.getLabel().isVariable() &&
								block.modifies(vertex.getLabel().getVariable()))
							result = block.mModifications.get(
									vertex.getLabel().getVariable());
						else if (vertex.isLeaf())
							result = vertex;
						else if (vertex.getChildCount() == 1)
							result = mModificationGraph.getVertex(
									vertex.getLabel(), get(vertex.getChild(0)));
						else {
							Vertex<RevertValue<L,P>>[] children
									= new Vertex[vertex.getChildCount()];
							for (int i = children.length; i-- != 0; )
								children[i] = get(vertex.getChild(i));
							result = mModificationGraph.getVertex(
									vertex.getLabel(), children);
						}
						vertex.setTag(inlined, result);
						return result;
					}
				};
				for (Entry<Variable,Vertex<RevertValue<L,P>>> entry
						: child.mModifications.entrySet())
					entry.setValue(inline.get(entry.getValue()));
				if (child.getChildCount() == 2)
					child.setBranchCondition(
							inline.get(((BranchBlock)child).mCondition));
				for (Entry<Variable,Vertex<RevertValue<L,P>>> entry
						: block.mModifications.entrySet())
					if (!child.mModifications.containsKey(entry.getKey()))
						child.mModifications.put(entry.getKey(),
								entry.getValue());
				child.removeNonModifications();
				for (RevertBlock<L,P> parent : block.getParents())
					parent.replaceChild(block, child);
				if (block == mStart)
					mStart = child;
				blocks.remove();
				changed = true;
			}
		}
		return changed;
	}
	
	protected void trimInsignificant() {
		mModificationGraph.clearSignificance();
		for (Block block : mBlocks) {
			for (Vertex<RevertValue<L,P>> modification
					: block.mModifications.values())
				modification.makeSignificant();
			if (block.getChildCount() > 1)
				((BranchBlock)block).mCondition.makeSignificant();
		}
		mModificationGraph.trimInsignificant();
	}
	
	public void rewrite(final ExpressionRewriter<L,P> rewriter) {
		Function<Vertex<RevertValue<L,P>>,Vertex<RevertValue<L,P>>>
				converter = new Function<Vertex<RevertValue<L,P>>,
				Vertex<RevertValue<L,P>>>() {
			private final Function<P,Vertex<RevertValue<L,P>>>
					mParameterConverter
					= new Function<P,Vertex<RevertValue<L,P>>>() {
				public Vertex<RevertValue<L,P>> get(P parameter) {
					return mModificationGraph.getVertex(
							RevertValue.<L,P>getParameter(parameter));
				}
			};

			private final VariaticFunction<L,Vertex<RevertValue<L,P>>,
					Vertex<RevertValue<L,P>>> mExpressionConverter
					= new AbstractVariaticFunction<L,Vertex<RevertValue<L,P>>,
					Vertex<RevertValue<L,P>>>() {
				public Vertex<RevertValue<L,P>> get(L first) {
					return mModificationGraph.getVertex(
							RevertValue.<L,P>getDomain(first));
				}
				public Vertex<RevertValue<L,P>> get(L first,
						Vertex<RevertValue<L,P>> child) {
					return mModificationGraph.getVertex(
							RevertValue.<L,P>getDomain(first), convert(child));
				}
				public Vertex<RevertValue<L,P>> get(L first,
						List<? extends Vertex<RevertValue<L,P>>> remaining) {
					Vertex<RevertValue<L,P>>[] children
							= new Vertex[remaining.size()];
					for (int i = 0; i < children.length; i++)
						children[i] = convert(remaining.get(i));
					return mModificationGraph.getVertex(
							RevertValue.<L,P>getDomain(first), children);
				}
			};
			
			private final Tag<Vertex<RevertValue<L,P>>> mTag
					= new NamedTag<Vertex<RevertValue<L,P>>>("Rewritten");

			private Vertex<RevertValue<L,P>> convert(
					Vertex<RevertValue<L,P>> parameter) {
				return get(parameter);
			}
			
			public Vertex<RevertValue<L,P>> get(
					Vertex<RevertValue<L,P>> parameter) {
				if (parameter.hasTag(mTag))
					return parameter.getTag(mTag);
				RevertValue<L,P> label = parameter.getLabel();
				Vertex<RevertValue<L,P>> rewrite;
				if (label.isVariable())
					rewrite = parameter;
				else if (label.isParameter())
					rewrite = rewriter.rewriteParameter(mExpressionConverter,
							mParameterConverter, label.getParameter());
				else if (label.isDomain())
					rewrite = rewriter.rewriteExpression(mExpressionConverter,
							mParameterConverter,
							label.getDomain(), parameter.getChildren());
				else
					throw new UnhandledCaseException();
				parameter.setTag(mTag, rewrite);
				return rewrite;
			}
		};
		for (Block block : mBlocks) {
			for (Entry<Variable,Vertex<RevertValue<L,P>>> entry
					: block.mModifications.entrySet())
				entry.setValue(converter.get(entry.getValue()));
			if (block.getChildCount() > 1)
				block.setBranchCondition(
						converter.get(((BranchBlock)block).mCondition));
			block.removeNonModifications();
		}
		trimInsignificant();
		simplify();
	}
	
	public String toString() {
		final StringBuilder string = new StringBuilder(
				"digraph {\nordering=out;\n");
		for (final RevertBlock<L,P> block : getVertices()) {
			string.append("subgraph cluster");
			string.append(block.hashCode());
			string.append(" {\nstart");
			string.append(block.hashCode());
			string.append(" [rank=max,shape=point];\nend");
			string.append(block.hashCode());
			string.append(" [rank=min,shape=point];\n");
			Action<Vertex<RevertValue<L,P>>> print
					= new Action<Vertex<RevertValue<L,P>>>() {
				private final Tag<Void> printed = new NamedTag<Void>("Printed");
				public void execute(Vertex<RevertValue<L,P>> vertex) {
					if (vertex.hasTag(printed))
						return;
					vertex.setTag(printed);
					if (vertex.getLabel().isVariable()) {
						string.append("get");
						string.append(vertex.getLabel());
						string.append("block");
						string.append(block.hashCode());
						string.append(" [rank=min,label=\"");
						string.append("Get ");
						string.append(vertex.getLabel());
						string.append("\"];\n");
						string.append("get");
						string.append(vertex.getLabel());
						string.append("block");
						string.append(block.hashCode());
						string.append(" -> end");
						string.append(block.hashCode());
						string.append(" [style=invis];\n");
						return;
					}
					string.append("node");
					string.append(vertex.hashCode());
					string.append("block");
					string.append(block.hashCode());
					string.append(" [label=\"");
					string.append(vertex.getLabel());
					string.append("\"];\n");
					for (Vertex<RevertValue<L,P>> child
							: vertex.getChildren()) {
						execute(child);
						string.append("node");
						string.append(vertex.hashCode());
						string.append("block");
						string.append(block.hashCode());
						string.append(" -> ");
						if (child.getLabel().isVariable()) {
							string.append("get");
							string.append(child.getLabel());
							string.append("block");
							string.append(block.hashCode());
						} else {
							string.append("node");
							string.append(child.hashCode());
							string.append("block");
							string.append(block.hashCode());
						}
						string.append(";\n");
					}
					return;
				}
			};
			for (Entry<Variable,Vertex<RevertValue<L,P>>> entry
					: block.mModifications.entrySet()) {
				string.append("set");
				string.append(entry.getKey());
				string.append("block");
				string.append(block.hashCode());
				string.append(" [rank=max,label=\"Set ");
				string.append(entry.getKey());
				string.append("\"];\n");
				string.append("start");
				string.append(block.hashCode());
				string.append(" -> set");
				string.append(entry.getKey());
				string.append("block");
				string.append(block.hashCode());
				string.append(" [style=invis];\n");
				string.append("set");
				string.append(entry.getKey());
				string.append("block");
				string.append(block.hashCode());
				string.append(" -> ");
				if (entry.getValue().getLabel().isVariable()) {
					string.append("get");
					string.append(entry.getValue().getLabel());
					string.append("block");
					string.append(block.hashCode());
				} else {
					string.append("node");
					string.append(entry.getValue().hashCode());
					string.append("block");
					string.append(block.hashCode());
				}
				string.append(";\n");
				print.execute(entry.getValue());
			}
			if (block instanceof RevertCFG.BranchBlock) {
				Vertex<RevertValue<L,P>> branch
						= ((BranchBlock)block).mCondition;
				string.append("branch");
				string.append(block.hashCode());
				string.append(" [rank=max,label=\"Branch\"];\n");
				string.append("start");
				string.append(block.hashCode());
				string.append(" -> branch");
				string.append(block.hashCode());
				string.append(" [style=invis];\n");
				string.append("branch");
				string.append(block.hashCode());
				string.append(" -> ");
				if (branch.getLabel().isVariable()) {
					string.append("get");
					string.append(branch.getLabel());
					string.append("block");
					string.append(block.hashCode());
				} else {
					string.append("node");
					string.append(branch.hashCode());
					string.append("block");
					string.append(block.hashCode());
				}
				string.append(";\n");
				print.execute(branch);
			}
			string.append("}\n");
			for (int i = 0; i < block.getChildCount(); i++) {
				RevertBlock<L,P> child = block.getChild(i);
				string.append("end");
				string.append(block.hashCode());
				string.append(" -> start");
				string.append(child.hashCode());
				string.append(" [style=bold,taillabel=\"");
				string.append(i);
				string.append("\"];\n");
			}
		}
		string.append("}");
		return string.toString();
	}
}
