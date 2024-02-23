package eqsat.meminfer.engine.generalize;

import java.util.ArrayList;
import java.util.Arrays;
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
import util.Grouping;
import util.HashGrouping;

public class PostMultiGenPEG
		<O, T extends PEGTerm<O,?,T,V>, V extends PEGValue<T,V>> {
	public static class LoopDepth {
		private final int mAnchor;
		private LoopDepth mGroup;
		private List<LoopDepth> mDistinct = new ArrayList();
		
		private LoopDepth(PostMultiGenPEG<?,?,?> peg, int anchor) {
			mAnchor = anchor;
		}
		
		private LoopDepth getGroup() {
			if (mGroup == null)
				return this;
			else
				return mGroup = mGroup.getGroup();
		}
		
		public int getAnchor() {return mAnchor;}
		
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
		
		public boolean isDistinctFrom(LoopDepth that) {
			return getGroup().mDistinct.contains(that);
		}
		public void setDistinctFrom(LoopDepth that) {
			if (mGroup != null) {
				getGroup().setDistinctFrom(that);
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
		private final PostMultiGenPEG<O,?,?> mPEG;
		private final FlowValue<?,O> mAnchor;
		private GenOp<O> mGroup;
		private boolean mIsExtendedDomain = false;
		private O mDomainOp;
		private boolean mIsPhi = false;
		private boolean mIsZero = false;
		private boolean mIsSuccessor = false;
		private PEGLoopOp mLoopOp;
		private LoopDepth mLoopDepth;
		private List<LoopDepth> mLifted = new ArrayList<LoopDepth>();
		
		private GenOp(PostMultiGenPEG<O,?,?> peg, FlowValue<?,O> anchor) {
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
			} else if (mIsZero) {
				that.setZero();
				mIsZero = false;
			} else if (mIsSuccessor) {
				that.setSuccessor();
				mIsZero = false;
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
		
		public boolean isExtendedDomain() {return getGroup().mIsExtendedDomain;}
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
		
		public O getExtendedDomain() {
			if (getGroup().mDomainOp == null)
				throw new IllegalStateException();
			return getGroup().mDomainOp;
		}
		public void setExtendedDomainOp(O op) {
			if (mGroup != null) {
				getGroup().setExtendedDomainOp(op);
				return;
			}
			setExtendedDomain();
			if (!mAnchor.getDomain(mPEG.mAmbassador).equals(op))
				throw new IllegalStateException();
			if (mDomainOp != null)
				return;
			mDomainOp = op;
		}
		
		public boolean isPhi() {return getGroup().mIsPhi;}
		public void setPhi() {
			if (mGroup != null) {
				getGroup().setPhi();
				return;
			}
			if (!mAnchor.isPhi())
				throw new IllegalStateException();
			if (mIsPhi)
				return;
			mIsPhi = true;
			mLifted = null;
		}
		
		public boolean isZero() {return getGroup().mIsZero;}
		public void setZero() {
			if (mGroup != null) {
				getGroup().setZero();
				return;
			}
			if (!mAnchor.isZero())
				throw new IllegalStateException();
			if (mIsZero)
				return;
			mIsZero = true;
			mLifted = null;
		}
		
		public boolean isSuccessor() {return getGroup().mIsSuccessor;}
		public void setSuccessor() {
			if (mGroup != null) {
				getGroup().setSuccessor();
				return;
			}
			if (!mAnchor.isSuccessor())
				throw new IllegalStateException();
			if (mIsSuccessor)
				return;
			mIsSuccessor = true;
			mLifted = null;
		}
		
		public boolean isLoopOp() {return getGroup().mLoopOp != null;}
		public PEGLoopOp getLoopOp() {
			if (getGroup().mLoopOp == null)
				throw new IllegalStateException();
			return getGroup().mLoopOp;
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
				mLoopDepth = new LoopDepth(mPEG, mAnchor.getLoopDepth());
			if (mLifted != null) {
				for (LoopDepth depth : mLifted)
					mLoopDepth.setDistinctFrom(depth);
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
		
		public boolean isAllLoopLifted() {
			return mAnchor.isLoopLiftedAll() && getGroup().mLifted == null;
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
		
		public boolean isLoopLifted(LoopDepth depth) {
			if (mLoopOp != null)
				return mLoopDepth.isDistinctFrom(depth);
			else if (mLifted != null)
				return mLifted.contains(depth);
			else
				return mAnchor.isLoopLiftedAll();
		}
		public void setLoopLifted(LoopDepth depth) {
			if (mGroup != null) {
				getGroup().setLoopLifted(depth);
				return;
			}
			if (!mAnchor.isLoopLiftedAll(depth.mAnchor))
				throw new IllegalArgumentException();
			if (mLoopOp != null)
				mLoopDepth.setDistinctFrom(depth);
			else if (mLifted != null && !mLifted.contains(depth))
				mLifted.add(depth);
		}
		
		private boolean isIdenticalTo(GenOp<O> that) {
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
			else if (mIsZero)
				return that.mIsZero;
			else if (mIsSuccessor)
				return that.mIsSuccessor;
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
			else if (mIsZero)
				return "0";
			else if (mIsSuccessor)
				return "+1";
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
		private TermOrTermChild<T,V> mAnchor;
		private final PostMultiGenPEG<O,T,V> mPEG;
		private Node<O,T,V> mGroup;
		private GenOp<O> mOp;
		private Node<O,T,V>[] mChildren;
		private List<LoopDepth> mInvariance = new ArrayList();
		private boolean mMarked;
		private boolean mTrigger;
		private boolean mResult;
		
		private Node(PostMultiGenPEG<O,T,V> peg, TermOrTermChild<T,V> anchor,
				boolean trigger, boolean result, boolean marked) {
			mPEG = peg;
			peg.mNodes.add(this);
			mAnchor = anchor;
			mMarked = marked;
			mTrigger = trigger;
			mResult = result;
		}
		
		public TermOrTermChild<T,V> getAnchor() {return mAnchor;}
		public T getTermAnchor() {
			if (getGroup().mAnchor.isTerm())
				return getGroup().mAnchor.getTerm();
			else
				throw new IllegalStateException();
		}
		
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
					throw new IllegalArgumentException();
				mChildren = new Node[arity];
				for (int i = 0; i < arity; i++)
					mChildren[i] = new Node<O,T,V>(mPEG,
							new TermChild<T,V>(mAnchor.getTerm(), i),
							mTrigger, mResult, false);
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
		
		public boolean isInvariant(LoopDepth depth) {
			return getGroup().mInvariance.contains(depth);
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
		
		public boolean isTrigger() {return getGroup().mTrigger;}
		private void setTrigger() {
			if (mGroup != null) {
				getGroup().setTrigger();
				return;
			}
			if (mTrigger)
				return;
			mTrigger = true;
			if (mChildren != null)
				for (Node<O,T,V> child : mChildren)
					child.setTrigger();
		}
		public boolean isResult() {return getGroup().mResult;}
		private void setResult() {
			if (mGroup != null) {
				getGroup().setResult();
				return;
			}
			if (mResult)
				return;
			mResult = true;
			if (mChildren != null)
				for (Node<O,T,V> child : mChildren)
					child.setResult();
		}
		public boolean isReachable() {
			return getGroup().mTrigger || getGroup().mResult;
		}
		
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
			for (LoopDepth depth : mInvariance)
				that.setInvariant(depth);
			mInvariance = null;
			if (mMarked) {
				that.mMarked = true;
				mMarked = false;
			}
			if (mTrigger) {
				that.setTrigger();
				mTrigger = false;
			}
			if (mResult) {
				that.setResult();
				mResult = false;
			}
			if (mChildren != null) {
				that.setArity(mChildren.length);
				mOp.unifyWith(that.mOp);
				Node<O,T,V>[] children = mChildren;
				Node<O,T,V>[] thatChildren = that.mChildren;
				mChildren = null;
				mOp = null;
				for (int i = 0; i < children.length; i++)
					children[i].unifyWith(thatChildren[i]);
			}
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
	
	public PostMultiGenPEG(OpAmbassador<O> ambassador) {
		mAmbassador = ambassador;
	}
	
	public Node<O,T,V> createNode(TermOrTermChild<T,V> anchor) {
		return new Node<O,T,V>(this, anchor, false, false, false);
	}
	public Node<O,T,V> createMarkedNode(TermOrTermChild<T,V> anchor,
			boolean trigger, boolean result) {
		return new Node<O,T,V>(this, anchor, trigger, result, true);
	}
	
	public void simplify() {
		for (Node<O,T,V> node : mNodes)
			if (node.hasArity())
				for (Node<O,T,V> that : mNodes)
					if (that.hasArity()
							&& node.getOp().isIdenticalTo(that.getOp()))
						node.getOp().unifyWith(that.getOp());
		Node<O,T,V>[] nodes = mNodes.toArray(new Node[mNodes.size()]);
		for (int i = 0; i < nodes.length; i++) {
			Node<O,T,V> node = nodes[i];
			if (node.mGroup != null || !node.hasArity())
				continue;
			for (int j = 0; j < i; j++)
				if (nodes[j].mGroup == null && nodes[j].hasArity()
						&& node.getArity() == nodes[j].getArity()
						&& node.getOp().equals(nodes[j].getOp())
						&& canSimplify(node, nodes[j])) {
					node.unifyWith(nodes[j]);
					break;
				}
		}
	}
	
	private boolean canSimplify(Node<O,T,V> left, Node<O,T,V> right) {
		return canSimplify(left, right, new HashGrouping<Node<O,T,V>>());
	}
	private boolean canSimplify(Node<O,T,V> left, Node<O,T,V> right,
			Grouping<Node<O,T,V>> unified) {
		if (!unified.group(left, right))
			return true;
		if (!left.hasArity() || !right.hasArity())
			return false;
		if (left.getArity() != right.getArity())
			return false;
		if (!left.getOp().equals(right.getOp()))
			return false;
		for (int i = 0; i < left.getArity(); i++)
			if (!canSimplify(left.getChild(i), right.getChild(i), unified))
				return false;
		return true;
	}

	public void clearAnchors() {
		for (Node<O,T,V> node : mNodes) {
			node.mAnchor = null;
			if (node.hasArity()) {
				for (int i = 0; i < node.getArity(); i++)
					node.mChildren[i] = node.mChildren[i].getGroup();
				if (node.mOp.mLifted != null) {
					Set<LoopDepth> lifted
							= new HashSet<LoopDepth>(node.mOp.mLifted);
					LoopDepth[] liftedArray
							= lifted.toArray(new LoopDepth[lifted.size()]);
					for (int i = 0; i < liftedArray.length; i++)
						liftedArray[i] = liftedArray[i].getGroup();
					node.mOp.mLifted = new ArrayList<LoopDepth>(
							Arrays.asList(liftedArray));
				}
				if (node.mOp.mLoopDepth != null) {
					node.mOp.mLoopDepth = node.mOp.mLoopDepth.getGroup();
					Set<LoopDepth> distinct = new HashSet<LoopDepth>(
							node.mOp.mLoopDepth.mDistinct);
					LoopDepth[] distinctArray
							= distinct.toArray(new LoopDepth[distinct.size()]);
					for (int i = 0; i < distinctArray.length; i++)
						distinctArray[i] = distinctArray[i].getGroup();
					node.mOp.mLoopDepth.mDistinct = new ArrayList<LoopDepth>(
							Arrays.asList(distinctArray));
				}
			}
		}
	}
	
	public String toString() {return toString(false);}
	public String toString(boolean reachableOnly) {
		StringBuilder string = new StringBuilder("digraph {\nordering=out;\n");
		for (Node<O,T,V> node : mNodes)
			if (!reachableOnly || node.isReachable()) {
				string.append(node.hashCode());
				string.append(" [label=\"");
				if (node.mOp != null)
					string.append(node.getOp());
				else if (node.mAnchor != null) {
					string.append("Anchor:");
					string.append(node.mAnchor);
				}
				if (!node.getInvariance().isEmpty()) {
					string.append(" (Loop-Invariant: ");
					string.append(node.getInvariance());
					string.append(')');
				}
				string.append("\",shape=");
				string.append(node.isTrigger()
						? node.mMarked ? "octagon,style=bold" : "octagon"
						: node.isResult()
						? node.mMarked ? "box,style=bold" : "box"
						: "ellipse,style=dashed");
				string.append("];\n");
			}
		for (Node<O,T,V> node : mNodes)
			if ((!reachableOnly || node.isReachable())
					&& node.mChildren != null)
				for (int i = 0; i < node.getArity(); i++) {
					string.append(node.hashCode());
					string.append(" -> ");
					string.append(node.mChildren[i].hashCode());
					string.append(" [taillabel=\"");
					string.append(i);
					string.append("\",style=");
					string.append(node.isReachable() ? "solid" : "dashed");
					string.append("];\n");
				}
		string.append("}\n");
		return string.toString();
	}
}
