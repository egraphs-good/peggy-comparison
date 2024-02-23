package peggy.revert;

import java.util.Iterator;
import java.util.NoSuchElementException;

import peggy.revert.MiniPEG.Vertex;

/**
 * This is an iterator for all the vertices in all the PEGCFG's block's minipegs.
 */
public class CFGVerticesIterator<L,P,R,T,X extends PEGCFG<L,P,R,T,X,Y>,Y extends PEGCFGBlock<L,P,R,T,X,Y>>
implements Iterator<Vertex<Item<L,P,T>>> {
	private Iterator<Y> blockIterator;
	private Iterator<Vertex<Item<L,P,T>>> currentIterator;
	private Vertex<Item<L,P,T>> nextVertex;
	
	public CFGVerticesIterator(X cfg) {
		this.blockIterator = cfg.getBlocks().iterator();
	}
	
	public boolean hasNext() {
		if (this.nextVertex != null)
			return true;
		
		while (this.currentIterator == null || !this.currentIterator.hasNext()) {
			if (this.blockIterator.hasNext()) {
				this.currentIterator = new BlockVerticesIterator<L,P,R,T,X,Y>(this.blockIterator.next());
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
