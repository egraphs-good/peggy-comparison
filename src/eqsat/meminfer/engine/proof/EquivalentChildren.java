package eqsat.meminfer.engine.proof;

import eqsat.meminfer.engine.basic.Term;
import eqsat.meminfer.engine.basic.TermChild;
import eqsat.meminfer.engine.basic.Value;
import util.pair.Couple;

public class EquivalentChildren<T extends Term<T,V>, V extends Value<T,V>>
		implements Property {
	private Couple<TermChild<T,V>> mEquivalent;
	
	public EquivalentChildren(T left, int leftChild, T right, int rightChild) {
		if (!left.getChild(leftChild).getValue().equals(
				right.getChild(rightChild).getValue()))
			throw new IllegalArgumentException();
		mEquivalent = new Couple<TermChild<T,V>>(
				new TermChild<T,V>(left, leftChild),
				new TermChild<T,V>(right, rightChild));
	}
	
	public TermChild<T,V> getLeft() {return mEquivalent.getLeft();}
	public T getLeftTerm() {return mEquivalent.getLeft().getParentTerm();}
	public int getLeftChild() {return mEquivalent.getLeft().getChildIndex();}
	public TermChild<T,V> getRight() {return mEquivalent.getRight();}
	public T getRightTerm() {return mEquivalent.getRight().getParentTerm();}
	public int getRightChild() {return mEquivalent.getRight().getChildIndex();}
	public Couple<TermChild<T,V>> getTermChildren() {return mEquivalent;}
	
	public boolean equals(Object that) {
		return that instanceof EquivalentChildren
				&& equals((EquivalentChildren)that);
	}
	public boolean equals(EquivalentChildren that) {
		return mEquivalent.equals(that.mEquivalent);
	}
	public int hashCode() {return mEquivalent.hashCode() + 7;}
	
	public String toString() {
		return "EquivalentChildren("
				+ mEquivalent.getLeft().getParentTerm().hashCode() + ","
				+ mEquivalent.getLeft().getChildIndex() + ","
				+ mEquivalent.getRight().getParentTerm().hashCode() + ","
				+ mEquivalent.getRight().getChildIndex() + ")";
	}
}
