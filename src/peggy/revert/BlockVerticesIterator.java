package peggy.revert;

import java.util.Iterator;
import java.util.NoSuchElementException;

import peggy.revert.MiniPEG.Vertex;

/**
 * This is an iterator for all the minipeg vertices in a given PEGCFGBlock.
 */
public class BlockVerticesIterator<L,P,R,T,X extends PEGCFG<L,P,R,T,X,Y>,Y extends PEGCFGBlock<L,P,R,T,X,Y>>
implements Iterator<Vertex<Item<L,P,T>>> {
	private Y block;
	private Iterator<T> varIterator;
	private Iterator<? extends Vertex<Item<L,P,T>>> currentIterator = null;
	private Vertex<Item<L,P,T>> nextVertex = null;

	public BlockVerticesIterator(Y _block) {
		this.block = _block;
		this.varIterator = this.block.getAssignedVars().iterator();
		if (this.block.getBranchCondition() != null)
			this.currentIterator = this.block.getBranchCondition().getDescendents().iterator();
	}

	public boolean hasNext() {
		if (this.nextVertex != null)
			return true;

		while (this.currentIterator == null || !this.currentIterator.hasNext()) {
			if (this.varIterator.hasNext()) {
				this.currentIterator = this.block.getAssignment(this.varIterator.next()).getDescendents().iterator();
			} else {
				return false;
			}
		}

		this.nextVertex = this.currentIterator.next();
		return true;
	}

	public Vertex<Item<L,P,T>> next() {
		if (!this.hasNext())
			throw new NoSuchElementException();
		Vertex<Item<L,P,T>> result = this.nextVertex;
		this.nextVertex = null;
		return result;
	}
	
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
