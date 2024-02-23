package eqsat.meminfer.engine.generalize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eqsat.meminfer.engine.basic.TermChild;
import eqsat.meminfer.engine.basic.TermOrTermChild;
import eqsat.meminfer.engine.peg.PEGTerm;
import eqsat.meminfer.engine.peg.PEGValue;
import eqsat.meminfer.network.peg.PEGNetwork.PEGLoopOp;
import util.UnhandledCaseException;
import util.pair.Couple;

public class GenEPEG<O, T extends PEGTerm<O,?,T,V>, V extends PEGValue<T,V>> {
	public static class LoopDepth {
		private LoopDepth mGroup;
		private List<LoopDepth> mDistinct = new ArrayList();
		
		private LoopDepth() {}
		
		private LoopDepth getGroup() {
			if (mGroup == null)
				return this;
			else
				return mGroup = mGroup.getGroup();
		}
		
		public void unifyWith(LoopDepth that) {
			if (mGroup != null) {
				mGroup.unifyWith(that);
				return;
			} else if (that.mGroup != null)
				that = that.getGroup();
			else if (this == that)
				return;
			if (mDistinct.contains(that))
				throw new IllegalStateException();
			mDistinct.removeAll(that.mDistinct);
			that.mDistinct.addAll(mDistinct);
			mDistinct = null;
			mGroup = that;
		}
		
		public void distinctFrom(LoopDepth that) {
			if (mGroup != null) {
				mGroup.distinctFrom(that);
				return;
			} else if (that.mGroup != null)
				that = that.getGroup();
			if (this == that)
				throw new IllegalStateException();
			if (!mDistinct.contains(that))
				mDistinct.add(that);
			if (!that.mDistinct.contains(this))
				that.mDistinct.add(this);
		}
		
		public boolean equals(Object that) {
			return that instanceof LoopDepth && equals((LoopDepth)that);
		}
		public boolean equals(LoopDepth that) {
			return getGroup() == that.getGroup();
		}
		public int hashCode() {
			if (mGroup == null)
				return super.hashCode();
			else
				return getGroup().hashCode();
		}
		
		public String toString() {return Integer.toString(hashCode());}
	}
	
	public static class GenOp<O> {
		private GenOp<O> mGroup;
		private boolean mIsExtendedDomain = false;
		private O mDomainOp;
		private boolean mIsPhi = false;
		private PEGLoopOp mLoopOp;
		private LoopDepth mLoopDepth;
		private List<LoopDepth> mLifted = new ArrayList<LoopDepth>();
		
		private GenOp() {}
		
		private GenOp<O> getGroup() {
			if (mGroup == null)
				return this;
			else
				return mGroup = mGroup.getGroup();
		}
		
		public void unifyWith(GenOp<O> that) {
			if (mGroup != null)
				getGroup().unifyWith(that);
			else if (that.mGroup != null)
				that = that.getGroup();
			else if (this == that)
				return;
			if (mIsExtendedDomain) {
				that.setExtendedDomain();
				mIsExtendedDomain = false;
				if (mDomainOp != null) {
					that.setExtendedDomainOp(mDomainOp);
					mDomainOp = null;
				}
			} else if (mIsPhi) {
				that.setPhi();
				mIsPhi = false;
			} else if (mLoopOp != null) {
				that.setLoopOp(mLoopOp);
				that.getLoopDepth().unifyWith(mLoopDepth);
			} else if (mLifted == null)
				that.setAllLoopLifted();
			else for (LoopDepth depth : mLifted)
				that.setLoopLifted(depth);
			mGroup = that;
		}
		
		public void setExtendedDomain() {
			if (mGroup != null) {
				getGroup().setExtendedDomain();
				return;
			}
			if (mIsPhi || mLoopOp != null)
				throw new IllegalStateException();
			mIsExtendedDomain = true;
			mLifted = null;
		}
		
		public void setExtendedDomainOp(O op) {
			if (mGroup != null) {
				getGroup().setExtendedDomainOp(op);
				return;
			}
			setExtendedDomain();
			if (mDomainOp != null && !op.equals(mDomainOp))
				throw new IllegalStateException();
			mDomainOp = op;
		}
		
		public void setPhi() {
			if (mGroup != null) {
				getGroup().setPhi();
				return;
			}
			if (mIsExtendedDomain || mLoopOp != null)
				throw new IllegalStateException();
			mIsPhi = true;
			mLifted = null;
		}
		
		public void setLoopOp(PEGLoopOp op) {
			if (mGroup != null) {
				getGroup().setLoopOp(op);
				return;
			}
			if (mLifted == null)
				throw new IllegalStateException();
			if (mLoopOp != null) {
				if (!mLoopOp.equals(op))
					throw new IllegalStateException();
				else
					return;
			}
			mLoopOp = op;
			mLoopDepth = new LoopDepth();
			for (LoopDepth depth : mLifted)
				mLoopDepth.distinctFrom(depth);
			mLifted = Collections.<LoopDepth>emptyList();
		}
		
		public LoopDepth getLoopDepth() {
			if (mGroup != null)
				return getGroup().getLoopDepth();
			else if (mLoopDepth != null)
				return mLoopDepth;
			else
				throw new IllegalStateException();
		}
		
		public void setAllLoopLifted() {
			if (mGroup != null) {
				getGroup().setAllLoopLifted();
				return;
			}
			if (mLoopOp != null)
				throw new IllegalStateException();
			mLifted = null;
		}
		
		public void setLoopLifted(LoopDepth depth) {
			if (mGroup != null) {
				getGroup().setLoopLifted(depth);
				return;
			}
			if (mLoopOp != null)
				mLoopDepth.distinctFrom(depth);
			else if (mLifted != null && !mLifted.contains(depth))
				mLifted.add(depth);
		}
		
		public boolean equals(Object that) {return equals((GenOp)that);}
		public boolean equals(GenOp<O> that) {
			return getGroup() == that.getGroup();
		}
		public int hashCode() {
			if (mGroup == null)
				return super.hashCode();
			else
				return getGroup().hashCode();
		}
		
		public String toString() {
			if (mGroup != null)
				return getGroup().toString();
			if (mIsExtendedDomain) {
				if (mDomainOp != null)
					return mDomainOp.toString();
				else
					return hashCode() + " (ExtDom)";
			} else if (mIsPhi)
				return "Phi";
			else if (mLoopOp != null)
				return mLoopOp + "-" + mLoopDepth;
			else if (mLifted == null)
				return hashCode() + " (Loop-Lifted: All)";
			else if (mLifted.isEmpty())
				return Integer.toString(hashCode());
			else
				return hashCode() + " (Loop-Lifted: " + mLifted + ")";
		}
	}
	
	public abstract static class NodeOrNodeChild
			<O, T extends PEGTerm<O,?,T,V>, V extends PEGValue<T,V>> {
		private List<LoopDepth> mInvariance = new ArrayList();
		
		private NodeOrNodeChild() {}
		
		public abstract boolean isNode();
		public abstract Node<O,T,V> getNode();
		
		public abstract boolean isNodeChild();
		public abstract NodeChild<O,T,V> getNodeChild();
		
		public abstract TermOrTermChild<T,V> getTermOrTermChild();
		
		public Collection<? extends LoopDepth> getInvariance() {
			return mInvariance;
		}
		public void setInvariant(LoopDepth depth) {
			if (!mInvariance.contains(depth))
				mInvariance.add(depth);
		}
	}
	
	public final static class NodeChild
			<O, T extends PEGTerm<O,?,T,V>, V extends PEGValue<T,V>>
			extends NodeOrNodeChild<O,T,V> {
		private final TermChild<T,V> mTermChild;
		private final Node<O,T,V> mParent;
		private final int mIndex;
		
		private NodeChild(Node<O,T,V> parent, int index) {
			mParent = parent;
			mIndex = index;
			mTermChild = new TermChild(parent.getTerm(), index);
		}
		
		public boolean isNode() {return false;}
		public Node<O,T,V> getNode() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isNodeChild() {return true;}
		public NodeChild<O,T,V> getNodeChild() {return this;}
		
		public TermOrTermChild<T,V> getTermOrTermChild() {return mTermChild;}
		public TermChild<T,V> getTermChild() {return mTermChild;}
		
		public Node<O,T,V> getParent() {return mParent;}
		public int getIndex() {return mIndex;}
	}
	
	public final static class Node
			<O, T extends PEGTerm<O,?,T,V>, V extends PEGValue<T,V>>
			extends NodeOrNodeChild<O,T,V> {
		private final T mTerm;
		private NodeChild<O,T,V>[] mChildren;
		private GenOp<O> mOp;
		
		private Node(GenEPEG<O,T,V> epeg, T term) {
			epeg.mNodes.add(this);
			mTerm = term;
		}
		
		public boolean isNode() {return true;}
		public Node<O,T,V> getNode() {return this;}
		
		public boolean isNodeChild() {return false;}
		public NodeChild<O,T,V> getNodeChild() {
			throw new UnsupportedOperationException();
		}
		
		public TermOrTermChild<T,V> getTermOrTermChild() {return mTerm;}
		public T getTerm() {return mTerm;}
		
		public boolean hasArity() {return mChildren != null;}
		
		public int getArity() {
			if (mChildren == null)
				throw new IllegalStateException();
			else
				return mChildren.length;
		}
		public void setArity(int arity) {
			if (mChildren == null) {
				mChildren = new NodeChild[arity];
				for (int i = 0; i < arity; i++)
					mChildren[i] = new NodeChild<O,T,V>(this, i);
				mOp = new GenOp<O>();
			}
			else if (mChildren.length != arity)
				throw new IllegalStateException();
		}
		public NodeChild<O,T,V> getChild(int index) {
			if (mChildren == null)
				throw new IllegalStateException();
			else
				return mChildren[index];
		}
		
		public GenOp<O> getOp() {
			if (mOp == null)
				throw new IllegalStateException();
			else
				return mOp;
		}
	}
	
	private final Set<Node<O,T,V>> mNodes = new HashSet();
	private final Set<Couple<NodeOrNodeChild<O,T,V>>> mEqualities
			= new HashSet();
	
	public Node<O,T,V> createNode(T term) {return new Node<O,T,V>(this, term);}
	public void addEquality(NodeOrNodeChild<O,T,V> left,
			NodeOrNodeChild<O,T,V> right) {
		mEqualities.add(new Couple<NodeOrNodeChild<O,T,V>>(left, right));
	}
	
	public String toString() {
		StringBuilder string = new StringBuilder("digraph {\nordering=out;\n");
		for (Node<O,T,V> node : mNodes) {
			string.append(node.hashCode());
			if (node.hasArity()) {
				string.append(" [label=\"");
				string.append(node.getOp());
				if (!node.getInvariance().isEmpty()) {
					string.append(" (Loop-Invariant: ");
					string.append(node.getInvariance());
					string.append(')');
				}
				string.append("\"];\n");
				for (int i = 0; i < node.getArity(); i++) {
					string.append(node.hashCode());
					string.append(i);
					string.append(" [label=\"(");
					string.append(i);
					string.append(")\"];\n");
				}
			} else
				string.append(" [label=\"\"];\n");
		}
		for (Node<O,T,V> node : mNodes)
			if (node.hasArity())
				for (int i = 0; i < node.getArity(); i++) {
					string.append(node.hashCode());
					string.append(" -> ");
					string.append(node.hashCode());
					string.append(i);
					string.append(";\n");
				}
		for (Couple<NodeOrNodeChild<O,T,V>> equality : mEqualities) {
			NodeOrNodeChild<O,T,V> left, right;
			if (equality.getLeft().isNode()
					&& equality.getRight().isNodeChild()) {
				left = equality.getRight();
				right = equality.getLeft();
			} else {
				left = equality.getLeft();
				right = equality.getRight();
			}
			if (left.isNode())
				string.append(left.getNode().hashCode());
			else if (left.isNodeChild()) {
				string.append(left.getNodeChild().getParent().hashCode());
				string.append(left.getNodeChild().getIndex());
			} else
				throw new UnhandledCaseException();
			string.append(" -> ");
			if (right.isNode())
				string.append(right.getNode().hashCode());
			else if (right.isNodeChild()) {
				string.append(right.getNodeChild().getParent().hashCode());
				string.append(right.getNodeChild().getIndex());
			} else
				throw new UnhandledCaseException();
			string.append(" [arrowhead=none,style=\"dashed\"];\n");
		}
		string.append("}\n");
		return string.toString();
	}
}
