package eqsat.revert;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.OpExpression;
import util.NamedTag;
import util.Tag;
import util.graph.CExpressionGraph.Vertex;
import util.integer.PairInt;

public class BranchBlock<P, L> extends Block<P,L> implements BranchCFG<P,L> {
	protected Map<Variable,ReversionGraph<P,L>.Vertex> mTrueModifications
			= new HashMap<Variable,ReversionGraph<P,L>.Vertex>();
	protected Map<Variable,ReversionGraph<P,L>.Vertex> mFalseModifications
			= new HashMap<Variable,ReversionGraph<P,L>.Vertex>();
	protected ReversionGraph<P,L>.Vertex mBranchCondition = null;
	protected BranchCFG<P,L> mCFG = null;
	
	public BranchBlock(CFGReverter<P,L,?> reverter, OpAmbassador<L> ambassador){
		super(reverter, ambassador);
	}
	public BranchBlock(CFGReverter<P,L,?> reverter, OpAmbassador<L> ambassador,
			Collection<? extends BranchBlock<P,L>> blocks) {
		this(reverter, ambassador, blocks, new NamedTag("Converted"));
	}
	protected BranchBlock(CFGReverter<P,L,?> reverter,
			OpAmbassador<L> ambassador,
			Collection<? extends BranchBlock<P,L>> blocks,
			final Tag<ReversionGraph<P,L>.Vertex> convertTag) {
		super(reverter, ambassador, blocks, convertTag);
		if (blocks.isEmpty())
			throw new IllegalArgumentException();
		BlockInliner<P,L> convert = getConverter();
		setBranchCondition(
				convert.get(blocks.iterator().next().mBranchCondition));
		for (BranchBlock<P,L> block : blocks) {
			for (Entry<Variable,ReversionGraph<P,L>.Vertex> entry
					: block.mTrueModifications.entrySet())
				if (!mTrueModifications.containsKey(entry.getKey()))
					modifyIfTrue(entry.getKey(),
							convert.get(entry.getValue()));
			for (Entry<Variable,ReversionGraph<P,L>.Vertex> entry
					: block.mFalseModifications.entrySet())
				if (!mFalseModifications.containsKey(entry.getKey()))
					modifyIfFalse(entry.getKey(),
							convert.get(entry.getValue()));
		}
	}
	
	protected void setBranchCondition(ReversionGraph<P,L>.Vertex condition) {
		mBranchCondition = condition;
		mBranchCondition.makeSignificant();
	}
	
	public boolean modifies(Variable variable) {
		return super.modifies(variable)
				|| mTrueModifications.containsKey(variable)
				|| mFalseModifications.containsKey(variable);
	}
	
	protected void modifyIfTrue(Variable variable,
			ReversionGraph<P,L>.Vertex value) {
		if (value.getLabel().isVariable()
				&& value.getLabel().getVariable().equals(variable))
			mTrueModifications.remove(variable);
		else {
			mTrueModifications.put(variable, value);
			value.makeSignificant();
		}
	}
	
	protected void modifyIfFalse(Variable variable,
			ReversionGraph<P,L>.Vertex value) {
		if (value.getLabel() != null && value.getLabel().isVariable()
				&& value.getLabel().getVariable().equals(variable))
			mFalseModifications.remove(variable);
		else {
			mFalseModifications.put(variable, value);
			value.makeSignificant();
		}
	}
	
	public Variable getBranchVariable() {return mBranchCondition.getVariable();}
	
	public RevertBlock<L,P> addToCFG(RevertCFG<L,P,?> cfg,
			RevertBlock<L,P> trueTarget, RevertBlock<L,P> falseTarget) {
		if (mCFG == null)
			mCFG = process();
		return mCFG.addToCFG(cfg, trueTarget, falseTarget);
	}
	
	public void chain(Variable input,
			Collection<? super PairInt<OpExpression<L>>> uses) {
		if (mCFG == null)
			mCFG = process();
		mCFG.chain(input, uses);
	}
	
	private BranchCFG<P,L> process() {
		if (mBranchCondition.getHead() != null
				&& mBranchCondition.getHead().isNegate()) {
			mBranchCondition = mBranchCondition.getTail();
			Map<Variable,ReversionGraph<P,L>.Vertex> temp
					= mTrueModifications;
			mTrueModifications = mFalseModifications;
			mFalseModifications = temp;
			final BranchCFG<P,L> swapped = process();
			return new BranchCFG<P,L>() {
				public RevertBlock<L,P> addToCFG(RevertCFG<L,P,?> cfg,
						RevertBlock<L,P> trueTarget,
						RevertBlock<L,P> falseTarget) {
					return swapped.addToCFG(cfg, falseTarget, trueTarget);
				}
				public void chain(Variable input,
						Collection<? super PairInt<OpExpression<L>>> uses) {
					swapped.chain(input, uses);
				}
			};
		}
		if (mBranchCondition.isConstant()
				&& (mBranchCondition.getHead().isTrue()
				|| mBranchCondition.getHead().isFalse())) {
			mBranchCondition.unmakeSignificant();
			final boolean cond = mBranchCondition.getHead().isTrue();
			final FallBlock<P,L> block = new FallBlock<P,L>(mReverter, mGraph);
			mGraph.clearSignificance();
			for (Entry<Variable,ReversionGraph<P,L>.Vertex> entry
					: mModifications.entrySet())
				block.modify(entry.getKey(), entry.getValue());
			for (Entry<Variable,ReversionGraph<P,L>.Vertex> entry : (cond
					? mTrueModifications : mFalseModifications).entrySet())
				block.modify(entry.getKey(), entry.getValue());
			mGraph.trimInsignificant();
			return new BranchCFG<P,L>() {
				public RevertBlock<L,P> addToCFG(RevertCFG<L,P,?> cfg,
						RevertBlock<L,P> trueTarget,
						RevertBlock<L,P> falseTarget) {
					return block.addToCFG(cfg, cond ? trueTarget : falseTarget);
				}
				public void chain(Variable input,
						Collection<? super PairInt<OpExpression<L>>> uses) {
					block.chain(input, uses);
				}
			};
		}
		boolean split = !mTrueModifications.isEmpty()
				|| !mFalseModifications.isEmpty()
				|| mBranchCondition.getHead() != null
				&& mBranchCondition.getHead().isPhi();
		if (!split) {
			Evaluator<P,L,Boolean> check = new Evaluator<P,L,Boolean>() {
				public Boolean get(ReversionGraph<P,L>.Vertex vertex) {
					if (vertex.equals(mBranchCondition))
						return true;
					if (vertex.getLabel().getHead() != null
							&& vertex.getHead().isNegate())
						return get(vertex.getTail());
					if (vertex.getLabel().getHead() != null
							&& vertex.getHead().isPhi())
						return get(vertex.getChild(0))
								|| get(vertex.getChild(1))
								|| get(vertex.getChild(2));
					return false;
				}
			};
			for (ReversionGraph<P,L>.Vertex vertex : mGraph.getVertices())
				if (vertex.getHead() != null && vertex.getHead().isPhi())
					if (split |= check.get(vertex.getChild(0)))
						break;
		}
		return split ? processSplit() : processNoSplit();
	}
	
	protected BranchCFG<P,L> processNoSplit() {
		final FallCFG<P,L> head = serialize();
		return new BranchCFG<P,L>() {
			public RevertBlock<L,P> addToCFG(RevertCFG<L,P,?> cfg,
					RevertBlock<L,P> trueTarget,
					RevertBlock<L,P> falseTarget) {
				RevertBlock<L,P> branch
						= cfg.makeBlock(trueTarget, falseTarget);
				transcribe(branch, new NamedTag("Converted"));
				return head.addToCFG(cfg, branch);
			}
			public void chain(Variable input,
					Collection<? super PairInt<OpExpression<L>>> uses) {
				head.chain(input, uses);
				BranchBlock.super.chain(input, uses);
			}
		};
	}
	
	protected BranchCFG<P,L> processSplit() {
		for (Entry<Variable,ReversionGraph<P,L>.Vertex> entry
				: mTrueModifications.entrySet()) {
			ReversionGraph<P,L>.Vertex falseValue
					= mFalseModifications.remove(entry.getKey());
			if (falseValue == null)
				falseValue = mGraph.getVertex(entry.getKey());
			ReversionGraph<P,L>.Vertex value = mGraph.getVertex(
					FlowValue.<P,L>createPhi(),
					mBranchCondition, entry.getValue(), falseValue);
			value.setVariable(entry.getKey());
			mModifications.put(entry.getKey(), value);
		}
		for (Entry<Variable,ReversionGraph<P,L>.Vertex> entry
				: mFalseModifications.entrySet()) {
			ReversionGraph<P,L>.Vertex value = mGraph.getVertex(
					FlowValue.<P,L>createPhi(), mBranchCondition,
					mGraph.getVertex(entry.getKey()), entry.getValue());
			value.setVariable(entry.getKey());
			mModifications.put(entry.getKey(), value);
		}
		mTrueModifications.clear();
		mFalseModifications.clear();
		ReversionGraph<P,L>.Vertex phi = mBranchCondition;
		while (mBranchCondition.getHead() != null
				&& mBranchCondition.getHead().isPhi())
			mBranchCondition = mBranchCondition.getChild(0);
		mGraph.clearSignificance();
		mBranchCondition.makeSignificant();
		for (ReversionGraph<P,L>.Vertex vertex : mModifications.values())
			vertex.makeSignificant();
		final Tag<Void> used = new NamedTag("Used");
		new Evaluator<P,L,Void>() {
			public Void get(ReversionGraph<P,L>.Vertex vertex) {
				if (vertex.hasTag(used))
					return null;
				vertex.setTag(used);
				for (ReversionGraph<P,L>.Vertex child : vertex.getChildren())
					get(child);
				if (vertex.isPass())
					for (ReversionGraph<P,L>.Vertex parent
							: vertex.getParents())
						get(parent);
				return null;
			}
		}.get(mBranchCondition);
		final Block<P,L> trueBlock, falseBlock;
		if (phi == mBranchCondition) {
			trueBlock = new FallBlock<P,L>(mReverter, getOpAmbassador());
			falseBlock = new FallBlock<P,L>(mReverter, getOpAmbassador());
		} else {
			trueBlock = new BranchBlock<P,L>(mReverter, getOpAmbassador());
			falseBlock = new BranchBlock<P,L>(mReverter, getOpAmbassador());
		}
		ReversionInliner<P,L> convertTrue = new BlockInliner<P,L>(trueBlock) {
			protected boolean inline(ReversionGraph<P,L>.Vertex vertex) {
				return vertex.hasTag(used) && !vertex.isVariant();
			}
			protected ReversionGraph<P,L>.Vertex inlineAs(
					ReversionGraph<P,L>.Vertex vertex) {
				return mBranchCondition.equals(vertex)
						? mGraph.getVertex(FlowValue.<P,L>createTrue())
						: super.inlineAs(vertex);
			}
			protected void inlined(ReversionGraph<P,L>.Vertex vertex) {
				modify(vertex.getVariable(), vertex);
			}
		};
		ReversionInliner<P,L> convertFalse = new BlockInliner<P,L>(falseBlock) {
			protected boolean inline(ReversionGraph<P,L>.Vertex vertex) {
				return vertex.hasTag(used) && !vertex.isVariant();
			}
			protected ReversionGraph<P,L>.Vertex inlineAs(
					ReversionGraph<P,L>.Vertex vertex) {
				return mBranchCondition.equals(vertex)
						? mGraph.getVertex(FlowValue.<P,L>createFalse())
						: super.inlineAs(vertex);
			}
			protected void inlined(ReversionGraph<P,L>.Vertex vertex) {
				modify(vertex.getVariable(), vertex);
			}
		};
		Map<Variable,ReversionGraph<P,L>.Vertex> modifications
				= new HashMap(mModifications);
		mModifications.clear();
		mGraph.clearSignificance();
		mBranchCondition.makeSignificant();
		for (Entry<Variable,ReversionGraph<P,L>.Vertex> entry
				: modifications.entrySet()) {
			if (!entry.getValue().hasTag(used)) {
				trueBlock.modify(entry.getKey(),
						convertTrue.get(entry.getValue()));
				falseBlock.modify(entry.getKey(),
						convertFalse.get(entry.getValue()));
			} else
				modify(entry.getKey(), entry.getValue());
		}
		if (phi != mBranchCondition) {
			((BranchBlock<P,L>)trueBlock).setBranchCondition(
					convertTrue.get(phi));
			((BranchBlock<P,L>)falseBlock).setBranchCondition(
					convertFalse.get(phi));
		}
		trueBlock.mGraph.trimInsignificant();
		falseBlock.mGraph.trimInsignificant();
		mGraph.trimInsignificant();
		final BranchCFG<P,L> head = processNoSplit();
		if (trueBlock instanceof FallBlock)
			return new BranchCFG<P,L>() {
				public RevertBlock<L,P> addToCFG(RevertCFG<L,P,?> cfg,
						RevertBlock<L,P> trueTarget,
						RevertBlock<L,P> falseTarget) {
					return head.addToCFG(cfg, ((FallBlock<P,L>)trueBlock)
							.addToCFG(cfg, trueTarget),
							((FallBlock<P,L>)falseBlock)
							.addToCFG(cfg, falseTarget));
				}
				public void chain(Variable input,
						Collection<? super PairInt<OpExpression<L>>> uses) {
					head.chain(input, uses);
					trueBlock.chain(input, uses);
					falseBlock.chain(input, uses);
				}
			};
		else
			return new BranchCFG<P,L>() {
				public RevertBlock<L,P> addToCFG(RevertCFG<L,P,?> cfg,
						RevertBlock<L,P> trueTarget,
						RevertBlock<L,P> falseTarget) {
					return head.addToCFG(cfg, ((BranchBlock<P,L>)trueBlock)
							.addToCFG(cfg, trueTarget, falseTarget),
							((BranchBlock<P,L>)falseBlock)
							.addToCFG(cfg, trueTarget, falseTarget));
				}
				public void chain(Variable input,
						Collection<? super PairInt<OpExpression<L>>> uses) {
					head.chain(input, uses);
					trueBlock.chain(input, uses);
					falseBlock.chain(input, uses);
				}
			};
	}
	
	protected void transcribe(RevertBlock<L,P> branch,
			Tag<Vertex<RevertValue<L,P>>> convertTag) {
		super.transcribe(branch, convertTag);
		branch.setBranchCondition(
				branch.getConverter(convertTag).get(mBranchCondition));
	}
	
	protected void rewriteModifications() {
		super.rewriteModifications();
		if (mBranchCondition.isRewritten())
			mBranchCondition = mBranchCondition.getRewrite();
		for (Entry<Variable,ReversionGraph<P,L>.Vertex> entry
				: mTrueModifications.entrySet())
			if (entry.getValue().isRewritten())
				entry.setValue(entry.getValue().getRewrite());
		for (Entry<Variable,ReversionGraph<P,L>.Vertex> entry
				: mFalseModifications.entrySet())
			if (entry.getValue().isRewritten())
				entry.setValue(entry.getValue().getRewrite());
	}
	
	public BranchBlock<P,L> inline(
			final Map<? extends Variable, ? extends Value<P,L>> toInline) {
		boolean changes = false;
		for (Variable variable : toInline.keySet())
			if (changes |= mGraph.containsVariable(variable))
				break;
		if (!changes)
			return this;
		BranchBlock<P,L> inlined
				= new BranchBlock<P,L>(mReverter, getOpAmbassador());
		ReversionInliner<P,L> inliner
				= new ReversionInliner<P,L>(inlined.mGraph) {
			protected boolean inline(ReversionGraph<P,L>.Vertex vertex) {
				return vertex.getVariable() != null
						&& toInline.containsKey(vertex.getVariable());
			}
			protected ReversionGraph<P,L>.Vertex inlineAs(
					ReversionGraph<P,L>.Vertex vertex) {
				return mGraph.getVertex(toInline.get(vertex.getVariable()));
			}
		};
		inlined.setBranchCondition(inliner.get(mBranchCondition));
		for (Entry<Variable,ReversionGraph<P,L>.Vertex> entry
				: mModifications.entrySet())
			inlined.modify(entry.getKey(), inliner.get(entry.getValue()));
		for (Entry<Variable,ReversionGraph<P,L>.Vertex> entry
				: mTrueModifications.entrySet())
			inlined.modifyIfTrue(entry.getKey(), inliner.get(entry.getValue()));
		for (Entry<Variable,ReversionGraph<P,L>.Vertex> entry
				: mFalseModifications.entrySet())
			inlined.modifyIfFalse(entry.getKey(),
					inliner.get(entry.getValue()));
		inlined.mGraph.trimInsignificant();
		return inlined;
	}
}
