package peggy.revert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import peggy.revert.MiniPEG.Vertex;
import peggy.revert.java.JavaPEGCFGBlock;

/**
 * This is the block class for PEGCFGs.
 */
public abstract class PEGCFGBlock<L,P,R,T,X extends PEGCFG<L,P,R,T,X,Y>,Y extends PEGCFGBlock<L,P,R,T,X,Y>> {
	protected final Map<T,Vertex<Item<L,P,T>>> assignments = 
		new HashMap<T,Vertex<Item<L,P,T>>>();
	protected Vertex<Item<L,P,T>> branchCondition;
	protected final MiniPEG<Item<L,P,T>> minipeg = 
		new MiniPEG<Item<L,P,T>>();
	protected final List<Y> successors = new ArrayList<Y>();
	
	
	public Collection<T> getAssignedVars() {
		return Collections.unmodifiableCollection(this.assignments.keySet());
	}
	public Vertex<Item<L,P,T>> getAssignment(T var) {return this.assignments.get(var);}
	public void setAssignment(T var, Vertex<Item<L,P,T>> vertex) {
		if (vertex == null)
			throw new NullPointerException();
		this.assignments.put(var, vertex);
	}
	public void removeAssignment(T var) {this.assignments.remove(var);}
	
	public Vertex<Item<L,P,T>> getBranchCondition() {return this.branchCondition;}
	public void setBranchCondition(Vertex<Item<L,P,T>> vertex) {this.branchCondition = vertex;}
	
	public void addSucc(Y succ) {this.successors.add(succ);}
	public void insertSucc(int index, Y succ) {this.successors.add(index, succ);}
	public Y removeSucc(int index) {return this.successors.remove(index);}
	public void removeSucc(Y succ) {this.successors.remove(succ);}
	public int getNumSuccs() {return this.successors.size();}
	public Y getSucc(int index) {return this.successors.get(index);}
	public Iterable<Y> getSuccs() {return Collections.unmodifiableList(this.successors);}
	
	public MiniPEG<Item<L,P,T>> getMiniPEG() {return this.minipeg;}
	
	public abstract X getCFG();
	public abstract Y getSelf();
	
	public void pruneUnreachableVertices() {
		Set<Vertex<Item<L,P,T>>> reachable = 
			new HashSet<Vertex<Item<L,P,T>>>();
		for (T var : this.assignments.keySet()) {
			reachable.addAll(this.assignments.get(var).getDescendents());
		}
		if (this.branchCondition != null) {
			reachable.addAll(this.branchCondition.getDescendents());
		}
		Set<Vertex<Item<L,P,T>>> all = 
			new HashSet<Vertex<Item<L,P,T>>>(this.minipeg.getVertices());
		all.removeAll(reachable);
		this.minipeg.removeVertices(all);
	}
	
	/**
	 * Returns the set of all vars that are either set or used in this block.
	 */
	public Collection<? extends T> getReferencedVars() {
		Set<T> result = new HashSet<T>();
		result.addAll(this.assignments.keySet());
		for (BlockVerticesIterator<L,P,R,T,X,Y> iter=new BlockVerticesIterator<L,P,R,T,X,Y>(this.getSelf());iter.hasNext();) {
			Vertex<Item<L,P,T>> next = iter.next();
			if (next.getLabel().isVariable())
				result.add(next.getLabel().getVariable());
		}
		return result;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof JavaPEGCFGBlock))
			return false;
		return this.getSelf() == ((JavaPEGCFGBlock)o).getSelf();
	}
	
	/**
	 * Returns true iff this block has the same reachable minipeg as the given block.
	 */
	public boolean haveSameMiniPEG(Y block) {
		if (!this.getAssignedVars().equals(block.getAssignedVars()))
			return false;
		boolean mybc = (this.getBranchCondition() != null);
		boolean hisbc = (block.getBranchCondition() != null);
		if (mybc != hisbc)
			return false;
		
		for (T var : this.getAssignedVars()) {
			if (!exprsEqual(this.getAssignment(var), block.getAssignment(var)))
				return false;
		}
		if (mybc && !exprsEqual(this.getBranchCondition(), block.getBranchCondition()))
			return false;
		return true;
	}
	protected boolean exprsEqual(Vertex<Item<L,P,T>> left, Vertex<Item<L,P,T>> right) {
		if (!left.getLabel().equals(right.getLabel()))
			return false;
		if (left.getChildCount() != right.getChildCount())
			return false;
		for (int i = 0; i < left.getChildCount(); i++) {
			if (!exprsEqual(left.getChild(i), right.getChild(i)))
				return false;
		}
		return true;
	}
	
	public void replaceChild(Y oldBlock, Y newBlock) {
		for (int i = 0; i < this.successors.size(); i++) {
			if (this.successors.get(i).equals(oldBlock))
				this.successors.set(i, newBlock);
		}
	}
}
