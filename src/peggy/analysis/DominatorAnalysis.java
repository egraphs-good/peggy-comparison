package peggy.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class performs a dominator analysis over a rooted directed graph.
 * Real implementations will probably be closures around a graph class.
 * The B block class must be hashable.
 * Every block dominates itself by definition.
 */
public abstract class DominatorAnalysis<B> {
	protected abstract B getStartBlock();
	protected abstract Iterable<? extends B> getPredecessors(B block);
	protected abstract Iterable<? extends B> getBlocks();
	
	private final Map<B,Set<B>> dominators = 
		new HashMap<B,Set<B>>();
	
	DominatorAnalysis() {
		this.buildDominators();
	}
	
	public Iterable<? extends B> getDominators(B block) {
		return this.dominators.get(block);
	}
	
	private final void buildDominators() {
		// collect all nodes
		Set<B> allBlocks = new HashSet<B>();
		for (B block : this.getBlocks())
			allBlocks.add(block);
			
		// set all nodes' domsets to allBlocks
		for (B block : this.getBlocks())
			this.dominators.put(block, new HashSet<B>(allBlocks));
		
		{// Dom(start) = {start}
			B start = this.getStartBlock();
			this.dominators.get(start).clear();
			this.dominators.get(start).add(start);
		}

		// now iterate Dom(n) = {n} union  U_{p in preds(n)} Dom(p)
		for (boolean progress = true; progress; ) {
			progress = false;
			for (B block : this.getBlocks()) {
				Set<B> olddoms = this.dominators.get(block);
				Set<B> newdoms = new HashSet<B>();
				boolean gotone = false;
				for (B pred : this.getPredecessors(block)) {
					if (!gotone)
						newdoms.addAll(this.dominators.get(pred));
					else
						newdoms.retainAll(this.dominators.get(pred));
					gotone = true;
				}
				newdoms.add(block);
				if (!newdoms.equals(olddoms)) {
					progress = true;
					newdoms.clear();
					newdoms.addAll(olddoms);
				}
			}
		}
	}
}
