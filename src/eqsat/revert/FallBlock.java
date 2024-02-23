package eqsat.revert;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import eqsat.OpAmbassador;
import eqsat.OpExpression;
import util.NamedTag;
import util.integer.PairInt;

public class FallBlock<P, L> extends Block<P,L> implements FallCFG<P,L> {
	protected FallCFG<P,L> mCFG = null;
	
	public FallBlock(CFGReverter<P,L,?> reverter, OpAmbassador<L> ambassador) {
		super(reverter, ambassador);
	}
	public FallBlock(CFGReverter<P,L,?> reverter, ReversionGraph<P,L> graph) {
		super(reverter, graph);
	}
	public FallBlock(CFGReverter<P,L,?> reverter, OpAmbassador<L> ambassador,
			Collection<? extends FallBlock<P,L>> blocks) {
		super(reverter, ambassador, blocks);
	}
	
	public RevertBlock<L,P> addToCFG(RevertCFG<L,P,?> cfg,
			RevertBlock<L,P> target) {
		if (mCFG == null)
			mCFG = process();
		return mCFG.addToCFG(cfg, target);
	}
	
	private FallCFG<P,L> process() {
		final FallCFG<P,L> head = serialize();
		return new FallCFG<P,L>() {
			public RevertBlock<L,P> addToCFG(RevertCFG<L,P,?> cfg,
					RevertBlock<L,P> target) {
				RevertBlock<L,P> block = cfg.makeBlock(target);
				transcribe(block, new NamedTag("Converted"));
				return head.addToCFG(cfg, block);
			}
			public void chain(Variable input,
					Collection<? super PairInt<OpExpression<L>>> uses) {
				head.chain(input, uses);
				FallBlock.super.chain(input, uses);
			}
		};
	}
	
	public void chain(Variable input,
			Collection<? super PairInt<OpExpression<L>>> uses) {
		if (mCFG == null)
			mCFG = process();
		mCFG.chain(input, uses);
	}
	
	public FallBlock<P,L> inline(
			final Map<? extends Variable, ? extends Value<P,L>> toInline) {
		boolean changes = false;
		for (Variable variable : toInline.keySet())
			if (changes |= mGraph.containsVariable(variable))
				break;
		if (!changes)
			return this;
		FallBlock<P,L> inlined
				= new FallBlock<P,L>(mReverter, getOpAmbassador());
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
		for (Entry<Variable,ReversionGraph<P,L>.Vertex> entry
				: mModifications.entrySet())
			inlined.modify(entry.getKey(), inliner.get(entry.getValue()));
		inlined.mGraph.trimInsignificant();
		return inlined;
	}
}
