package eqsat.meminfer.engine.generalize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.meminfer.engine.basic.TermChild;
import eqsat.meminfer.engine.basic.TermOrTermChild;
import eqsat.meminfer.engine.peg.PEGTerm;
import eqsat.meminfer.engine.peg.PEGValue;
import eqsat.meminfer.network.peg.PEGNetwork.PEGLoopOp;

public class GenPEG<O, T extends PEGTerm<O,?,T,V>, V extends PEGValue<T,V>> {
	public static class LoopDepth {
		private final int mAnchor;
		private LoopDepth mGroup;
		private List<LoopDepth> mDistinct = new ArrayList();
		
		private LoopDepth(int anchor) {mAnchor = anchor;}
		
		private LoopDepth getGroup() {
			if (mGroup == null)
				return this;
			else
				return mGroup = mGroup.getGroup();
		}
		
		public void unifyWith(LoopDepth that) {
			if (mGroup != null) {
				getGroup().unifyWith(that);
				return;
			}
			that = that.getGroup();
			if (this == that)
				return;
			if (mAnchor != that.mAnchor)
				throw new IllegalStateException();
			mDistinct.removeAll(that.mDistinct);
			that.mDistinct.addAll(mDistinct);
			mDistinct = null;
			mGroup = that;
		}
		
		public void distinctFrom(LoopDepth that) {
			if (mGroup != null) {
				getGroup().distinctFrom(that);
				return;
			}
			that = that.getGroup();
			if (mAnchor == that.mAnchor)
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
		private final GenPEG<O,?,?> mPEG;
		private final FlowValue<?,O> mAnchor;
		private GenOp<O> mGroup;
		private boolean mIsExtendedDomain = false;
		private O mDomainOp;
		private boolean mIsPhi = false;
		private PEGLoopOp mLoopOp;
		private LoopDepth mLoopDepth;
		private List<LoopDepth> mLifted = new ArrayList<LoopDepth>();
		
		private GenOp(GenPEG<O,?,?> peg, FlowValue<?,O> anchor) {
			mPEG = peg;
			mAnchor = anchor;
		}
		
		private GenOp<O> getGroup() {
			if (mGroup == null)
				return this;
			else
				return mGroup = mGroup.getGroup();
		}
		
		public void unifyWith(GenOp<O> that) {
			if (mGroup != null) {
				getGroup().unifyWith(that);
				return;
			}
			that = that.getGroup();
			if (this == that)
				return;
			if (!mAnchor.equals(that.mAnchor))
				throw new IllegalStateException();
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
				mLoopDepth.unifyWith(that.getLoopDepth());
				mLoopDepth = null;
			} else if (mLifted == null)
				that.setAllLoopLifted();
			else {
				for (LoopDepth depth : mLifted)
					that.setLoopLifted(depth);
				mLifted = null;
			}
			mGroup = that;
		}
		
		public void setExtendedDomain() {
			if (mGroup != null) {
				getGroup().setExtendedDomain();
				return;
			}
			if (!mAnchor.isExtendedDomain())
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
			if (!mAnchor.getDomain(mPEG.mAmbassador).equals(op))
				throw new IllegalStateException();
			mDomainOp = op;
		}
		
		public void setPhi() {
			if (mGroup != null) {
				getGroup().setPhi();
				return;
			}
			if (!mAnchor.isPhi())
				throw new IllegalStateException();
			mIsPhi = true;
			mLifted = null;
		}
		
		public void setLoopOp(PEGLoopOp op) {
			if (mGroup != null) {
				getGroup().setLoopOp(op);
				return;
			}
			if (!op.isLoopOp(mAnchor))
				throw new IllegalStateException();
			mLoopOp = op;
			if (mLoopDepth == null)
				mLoopDepth = new LoopDepth(mAnchor.getLoopDepth());
			if (mLifted != null) {
				for (LoopDepth depth : mLifted)
					mLoopDepth.distinctFrom(depth);
				mLifted = null;
			}
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
			if (!mAnchor.isLoopLiftedAll())
				throw new IllegalStateException();
			mLifted = null;
		}
		
		public void setLoopLifted(LoopDepth depth) {
			if (mGroup != null) {
				getGroup().setLoopLifted(depth);
				return;
			}
			if (!mAnchor.isLoopLiftedAll(depth.mAnchor))
				throw new IllegalArgumentException();
			if (mLoopOp != null)
				mLoopDepth.distinctFrom(depth);
			else if (mLifted != null && !mLifted.contains(depth))
				mLifted.add(depth);
		}
		
		public boolean isIdenticalTo(GenOp<O> that) {
			if (mGroup != null)
				return getGroup().isIdenticalTo(that);
			that = that.getGroup();
			if (this == that)
				return true;
			if (!mAnchor.equals(that.mAnchor))
				return false;
			if (mDomainOp != null) {
				if (that.mDomainOp == null)
					return false;
				return mDomainOp.equals(that.mDomainOp);
			} else if (mIsPhi)
				return that.mIsPhi;
			else if (mLoopOp != null) {
				if (that.mLoopOp == null)
					return false;
				if (!mLoopOp.equals(that.mLoopOp))
					return false;
				return mLoopDepth.equals(that.mLoopDepth);
			} else
				return false;
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
	
	public final static class Node
			<O, T extends PEGTerm<O,?,T,V>, V extends PEGValue<T,V>> {
		private final TermOrTermChild<T,V> mAnchor;
		private final GenPEG<O,T,V> mPEG;
		private Node<O,T,V> mGroup;
		private GenOp<O> mOp;
		private Node<O,T,V>[] mChildren;
		private List<LoopDepth> mInvariance = new ArrayList();
		private boolean mMarked = false;
		
		private Node(GenPEG<O,T,V> peg, TermOrTermChild<T,V> anchor) {
			mPEG = peg;
			peg.mNodes.add(this);
			mAnchor = anchor;
		}
		
		public TermOrTermChild<T,V> getAnchor() {return mAnchor;}
		
		private Node<O,T,V> getGroup() {
			return mGroup == null ? this : (mGroup = mGroup.getGroup()); 
		}
		
		public boolean hasArity() {return getGroup().mChildren != null;}
		
		public int getArity() {
			if (mGroup != null)
				return getGroup().getArity();
			if (mChildren == null)
				throw new IllegalStateException();
			else
				return mChildren.length;
		}
		public void setArity(int arity) {
			if (mGroup != null) {
				getGroup().setArity(arity);
				return;
			}
			if (mChildren == null) {
				if (!mAnchor.isTerm() || mAnchor.getTerm().getArity() != arity)
					throw new IllegalStateException();
				mChildren = new Node[arity];
				for (int i = 0; i < arity; i++)
					mChildren[i] = new Node<O,T,V>(mPEG,
							new TermChild<T,V>(mAnchor.getTerm(), i));
				mOp = new GenOp<O>(mPEG, mAnchor.getTerm().getOp());
			}
			else if (mChildren.length != arity)
				throw new IllegalStateException();
		}
		public Node<O,T,V> getChild(int index) {
			if (mGroup != null)
				return getGroup().getChild(index);
			if (mChildren == null)
				throw new IllegalStateException();
			else
				return mChildren[index];
		}
		
		public GenOp<O> getOp() {
			if (mGroup != null)
				return getGroup().getOp();
			if (mOp == null)
				throw new IllegalStateException();
			else
				return mOp;
		}
		
		public Collection<? extends LoopDepth> getInvariance() {
			if (mGroup != null)
				return getGroup().getInvariance();
			for (int i = 0; i < mInvariance.size(); i++)
				for (int j = i + 1; j < mInvariance.size(); )
					if (mInvariance.get(i).equals(mInvariance.get(j)))
						mInvariance.remove(j);
					else
						j++;
			return mInvariance;
		}
		public void setInvariant(LoopDepth depth) {
			if (mGroup != null) {
				getGroup().setInvariant(depth);
				return;
			}
			if (!mAnchor.getValue().isInvariant(depth.mAnchor))
				throw new IllegalStateException();
			if (!mInvariance.contains(depth))
				mInvariance.add(depth);
		}
		
		public void mark() {getGroup().mMarked = true;}
		
		public void unifyWith(Node<O,T,V> that) {
			if (mGroup != null) {
				getGroup().unifyWith(that);
				return;
			}
			that = that.getGroup();
			if (this == that)
				return;
			if ((mAnchor.isTerm() && that.mAnchor.isTermChild())
					|| (mChildren != null && that.mChildren == null)) {
				that.unifyWith(this);
				return;
			}
			if (!mAnchor.getValue().equals(that.mAnchor.getValue()))
				throw new IllegalStateException();
			mPEG.mNodes.remove(this);
			mGroup = that;
			if (mChildren != null) {
				that.setArity(mChildren.length);
				for (int i = 0; i < mChildren.length; i++)
					mChildren[i].unifyWith(that.mChildren[i]);
				mChildren = null;
			}
			if (mOp != null) {
				mOp.unifyWith(that.mOp);
				mOp = null;
			}
			for (LoopDepth depth : mInvariance)
				that.setInvariant(depth);
			mInvariance = null;
			if (mMarked) {
				that.mMarked = true;
				mMarked = false;
			}
		}
		
		private boolean isIdenticalTo(Node<O,T,V> that) {
			if (mGroup != null)
				return getGroup().isIdenticalTo(that);
			that = that.getGroup();
			if (this == that)
				return true;
			if (!mAnchor.getValue().equals(that.mAnchor.getValue()))
				return false;
			if (!mInvariance.containsAll(that.mInvariance))
				return false;
			if (!that.mInvariance.containsAll(mInvariance))
				return false;
			if (mChildren == null || that.mChildren == null)
				return false;
			if (mChildren.length != that.mChildren.length)
				return false;
			for (int i = 0; i < mChildren.length; i++)
				if (!mChildren[i].equals(that.mChildren[i]))
					return false;
			if (!mOp.isIdenticalTo(that.mOp))
				return false;
			return true;
		}
		
		public boolean equals(Object that) {return equals((Node)that);}
		public boolean equals(Node<O,T,V> that) {
			return getGroup() == that.getGroup();
		}
		public int hashCode() {
			if (mGroup == null)
				return super.hashCode();
			else
				return getGroup().hashCode();
		}
		
		public String toString() {return hashCode() + ".Anchor:" + mAnchor;}
	}
	
	private final OpAmbassador<O> mAmbassador;
	private final Set<Node<O,T,V>> mNodes = new HashSet();
	
	public GenPEG(OpAmbassador<O> ambassador) {mAmbassador = ambassador;}
	
	public Node<O,T,V> createNode(TermOrTermChild<T,V> anchor) {
		return new Node<O,T,V>(this, anchor);
	}
	
	public void simplify() {
		simplify: while (true) {
			for (Node<O,T,V> node : mNodes)
				for (Node<O,T,V> that : mNodes)
					if (!node.equals(that) && node.isIdenticalTo(that)) {
						node.unifyWith(that);
						continue simplify;
					}
			break;
		}
	}
	
	public String toString() {
		StringBuilder string = new StringBuilder("digraph {\nordering=out;\n");
		for (Node<O,T,V> node : mNodes) {
			string.append(node.hashCode());
			string.append(" [label=\"");
			if (node.mOp != null)
				string.append(node.getOp());
			else {
				string.append("Anchor:");
				string.append(node.mAnchor);
			}
			if (!node.getInvariance().isEmpty()) {
				string.append(" (Loop-Invariant: ");
				string.append(node.getInvariance());
				string.append(')');
			}
			string.append('"');
			if (node.mMarked)
				string.append(",shape=rectangle");
			string.append("];\n");
		}
		for (Node<O,T,V> node : mNodes)
			if (node.mChildren != null)
				for (int i = 0; i < node.getArity(); i++) {
					string.append(node.hashCode());
					string.append(" -> ");
					string.append(node.mChildren[i].hashCode());
					string.append(" [taillabel=\"");
					string.append(i);
					string.append("\"];\n");
				}
		string.append("}\n");
		return string.toString();
	}
}
