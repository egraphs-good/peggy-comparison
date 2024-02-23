package eqsat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import util.Function;
import util.VariaticFunction;
import util.WrappingArrayList;
import util.graph.AbstractGraph;
import util.graph.AbstractVertex;
import util.mapped.MappedCollection;
import util.mapped.MappedList;

public final class SimplifyCFG
		<G extends CFG<?,B,V,L,P,R>, B extends Block<?,? extends B,V,L>,
		V, L, P, R>
		extends
		AbstractGraph<SimplifyCFG<G,B,V,L,P,R>,SimplifyCFG<G,B,V,L,P,R>.Block>
		implements
		CFG<SimplifyCFG<G,B,V,L,P,R>,SimplifyCFG<G,B,V,L,P,R>.Block,V,L,P,R> {
	public final class Block
			extends AbstractVertex<SimplifyCFG<G,B,V,L,P,R>,Block>
			implements eqsat.Block<SimplifyCFG<G,B,V,L,P,R>,Block,V,L> {
		protected final List<? extends B> mBlocks;
		
		protected Block(List<? extends B> blocks) {mBlocks = blocks;}

		public SimplifyCFG<G, B, V, L, P, R> getGraph() {
			return SimplifyCFG.this;
		}
		public Block getSelf() {return this;}

		public Collection<? extends Block> getParents() {
			return new MappedCollection<B,Block>() {
				protected Collection<? extends B> getWrapped() {
					return mBlocks.get(0).getParents();
				}
				protected Block map(B block) {return mBlockMap.get(block);}
			};
		}
		public List<? extends Block> getChildren() {
			return new MappedList<B,Block>() {
				protected List<? extends B> getWrapped() {
					return mBlocks.get(mBlocks.size()-1).getChildren();
				}
				protected Block map(B block) {return mBlockMap.get(block);}
			};
		}
		public Block getChild(int child) {
			return mBlockMap.get(mBlocks.get(mBlocks.size()-1).getChild(child));
		}

		public boolean isStart() {return mBlocks.get(0).isStart();}
		public boolean isEnd() {return mBlocks.get(mBlocks.size()-1).isEnd();}

		public boolean modifies(V variable) {
			for (B block : mBlocks)
				if (block.modifies(variable))
					return true;
			return false;
		}
		
		protected <E> Function<V,E> getOutputs(CFGTranslator<B,V,E> translator,
				Function<V,E> inputs) {
			for (B block : mBlocks)
				inputs = translator.getOutputs(block, inputs);
			return inputs;
		}
		
		public String toString() {
			String string = "";
			for (Iterator<? extends B> blocks = mBlocks.iterator();
					blocks.hasNext(); ) {
				string += blocks.next();
				if (blocks.hasNext())
					string += "; ";
			}
			return string;
		}
	}
	
	protected final G mCFG;
	protected final Map<B,Block> mBlockMap = new HashMap<B,Block>();
	protected final Collection<Block> mBlocks = new ArrayList<Block>();
	
	public SimplifyCFG(G cfg) {
		mCFG = cfg;
		List<B> working = new WrappingArrayList<B>();
		working.add(cfg.getStart());
		while (!working.isEmpty()) {
			B block = working.remove(0);
			if (mBlockMap.containsKey(block))
				continue;
			List<B> blocks = new ArrayList<B>();
			blocks.add(block);
			while (block.getChildCount() == 1
					&& block.getChild(0).getParentCount() == 1)
				blocks.add(block = block.getChild(0));
			working.addAll(block.getChildren());
			Block grouped = new Block(blocks);
			for (B sub : blocks)
				mBlockMap.put(sub, grouped);
			mBlocks.add(grouped);
		}
	}

	public SimplifyCFG<G,B,V,L,P,R> getSelf() {return this;}

	public Collection<? extends Block> getVertices() {return mBlocks;}

	public Block getStart() {return mBlockMap.get(mCFG.getStart());}
	public Block getEnd() {return mBlockMap.get(mCFG.getEnd());}

	public OpAmbassador<L> getOpAmbassador() {return mCFG.getOpAmbassador();}
	public Collection<? extends V> getVariables() {return mCFG.getVariables();}
	public P getParameter(V variable) throws IllegalArgumentException {
		return mCFG.getParameter(variable);
	}
	public Collection<? extends R> getReturns() {return mCFG.getReturns();}
	public V getReturnVariable(R ret) {return mCFG.getReturnVariable(ret);}

	public <E> CFGTranslator<Block,V,E> getTranslator(
			Function<P,E> parameterConverter, VariaticFunction<L,E,E> converter,
			Collection<? super E> known) {
		final CFGTranslator<B,V,E> translator
				= mCFG.getTranslator(parameterConverter, converter, known);
		return new CFGTranslator<Block,V,E>() {
			public Function<V,E> getOutputs(Block block, Function<V,E> inputs) {
				return block.getOutputs(translator, inputs);
			}
		};
	}
}
