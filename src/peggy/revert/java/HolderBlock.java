package peggy.revert.java;

import java.util.Collection;

import peggy.represent.java.JavaLabel;
import peggy.represent.java.JavaParameter;
import peggy.revert.Item;
import peggy.revert.MiniPEG;
import peggy.revert.MiniPEG.Vertex;

/**
 * This is a JavaPEGCFGBlock that implements the holder pattern,
 * that will be replaced by another JavaPEGCFGBlock at some point.
 */
public class HolderBlock extends JavaPEGCFGBlock {
	private JavaPEGCFGBlock inner;
	public HolderBlock(JavaPEGCFG cfg) {super(cfg);}

	public boolean isHolder() {
		if (this.inner == null)
			return true;
		else
			return this.inner.isHolder();
	}
	public void setInner(JavaPEGCFGBlock _inner) {
		if (this.inner == null)
			this.inner = _inner;
		else
			throw new UnsupportedOperationException();
	}
	public JavaPEGCFGBlock getSelf() {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.getSelf();
	}
	public Collection<Object> getAssignedVars() {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.getAssignedVars();
	}
	public Vertex<Item<JavaLabel,JavaParameter,Object>> getAssignment(Object var) {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.getAssignment(var);
	}
	public void setAssignment(Object var, 
			Vertex<Item<JavaLabel,JavaParameter,Object>> vertex) {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			this.inner.setAssignment(var, vertex);
	}
	public void removeAssignment(Object var) {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			this.inner.removeAssignment(var);
	}
	public Vertex<Item<JavaLabel,JavaParameter,Object>> getBranchCondition() {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.getBranchCondition();
	}
	public void setBranchCondition(Vertex<Item<JavaLabel,JavaParameter,Object>> vertex) {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			this.inner.setBranchCondition(vertex);
	}
	public void addSucc(JavaPEGCFGBlock succ) {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			this.inner.addSucc(succ);
	}
	public void insertSucc(int index, JavaPEGCFGBlock succ) {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			this.inner.insertSucc(index, succ);
	}
	public JavaPEGCFGBlock removeSucc(int index) {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.removeSucc(index);
	}
	public void removeSucc(JavaPEGCFGBlock succ) {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			this.inner.removeSucc(succ);
	}
	public int getNumSuccs() {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.getNumSuccs();
	}
	public JavaPEGCFGBlock getSucc(int index) {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.getSucc(index);
	}
	public Iterable<JavaPEGCFGBlock> getSuccs() {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.getSuccs();
	}
	public MiniPEG<Item<JavaLabel,JavaParameter,Object>> getMiniPEG() {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.getMiniPEG();
	}
	public JavaPEGCFG getCFG() {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.getCFG();
	}
	public void pruneUnreachableVertices() {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			this.inner.pruneUnreachableVertices();
	}
	public Collection<? extends Object> getReferencedVars() {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.getReferencedVars();
	}
	public int hashCode() {
		if (this.inner == null)
			return super.hashCode();
		else
			return this.inner.hashCode();
	}
	public boolean haveSameMiniPEG(JavaPEGCFGBlock block) {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.haveSameMiniPEG(block);
	}
	public void replaceChild(JavaPEGCFGBlock oldBlock, JavaPEGCFGBlock newBlock) {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			this.inner.replaceChild(oldBlock, newBlock);
	}
}
