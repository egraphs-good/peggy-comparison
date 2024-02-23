package eqsat.meminfer.engine.generalize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.meminfer.engine.basic.TermChild;
import eqsat.meminfer.engine.basic.TermOrTermChild;
import eqsat.meminfer.engine.peg.PEGTerm;
import eqsat.meminfer.engine.peg.PEGValue;
import eqsat.meminfer.network.peg.PEGLabelAmbassador;
import eqsat.meminfer.network.peg.PEGNetwork.PEGLoopOp;
import util.Labeled;
import util.Tag;
import util.Taggable;
import util.graph.AbstractGraph;
import util.graph.AbstractVertex;
import util.graph.OrderedVertex;
import util.pair.Couple;

public class MultiGenEPEG
		<O, T extends PEGTerm<O,?,T,V>, V extends PEGValue<T,V>>
		extends AbstractGraph<MultiGenEPEG<O,T,V>,MultiGenEPEG.Node<O,T,V>> {
	public static class LoopDepth {
		private final MultiGenEPEG<?,?,?> mEPEG;
		private final int mAnchor;
		private LoopDepth mGroup;
		private List<LoopDepth> mDistinct = new ArrayList();
		
		private LoopDepth(MultiGenEPEG<?,?,?> epeg, int anchor) {
			mEPEG = epeg;
			mAnchor = anchor;
		}
		
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
			mEPEG.simplify();
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
		private final MultiGenEPEG<O,?,?> mEPEG;
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
		
		private GenOp(MultiGenEPEG<O,?,?> epeg, FlowValue<?,O> anchor) {
			mEPEG = epeg;
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
			mEPEG.simplify();
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
			if (!mAnchor.getDomain(mEPEG.mAmbassador).equals(op))
				throw new IllegalStateException();
			if (mDomainOp != null)
				return;
			mDomainOp = op;
			mEPEG.simplify();
		}
		
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
			mEPEG.simplify();
		}
		
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
			mEPEG.simplify();
		}
		
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
			mEPEG.simplify();
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
				mLoopDepth = new LoopDepth(mEPEG, mAnchor.getLoopDepth());
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
			<O, T extends PEGTerm<O,?,T,V>, V extends PEGValue<T,V>>
			extends AbstractVertex<MultiGenEPEG<O,T,V>,Node<O,T,V>>
			implements OrderedVertex<MultiGenEPEG<O,T,V>,Node<O,T,V>>,
			Taggable, Labeled<GenOp<O>> {
		private final TermOrTermChild<T,V> mAnchor;
		private final MultiGenEPEG<O,T,V> mEPEG;
		private Node<O,T,V> mGroup;
		private GenOp<O> mOp;
		private Node<O,T,V>[] mChildren;
		private List<LoopDepth> mInvariance = new ArrayList();
		private boolean mMarked = false;
		private boolean mReachable = false;
		private Map<Tag,Object> mTags = null;
		
		private Node(MultiGenEPEG<O,T,V> epeg, TermOrTermChild<T,V> anchor) {
			mEPEG = epeg;
			mEPEG.mNodes.add(this);
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
				for (int i = 0; i < arity; i++) {
					mChildren[i] = new Node<O,T,V>(mEPEG,
							new TermChild<T,V>(mAnchor.getTerm(), i));
					if (mReachable)
						mChildren[i].setReachable();
				}
				mOp = new GenOp<O>(mEPEG, mAnchor.getTerm().getOp());
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
		
		public void mark() {getGroup().mMarked = true; setReachable();}
		
		public boolean isReachable() {return getGroup().mReachable;}
		private void setReachable() {
			if (mGroup != null) {
				getGroup().setReachable();
				return;
			}
			if (mReachable)
				return;
			mReachable = true;
			if (mChildren != null)
				for (Node<O,T,V> child : mChildren)
					child.setReachable();
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
			mEPEG.mNodes.remove(this);
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
			if (mReachable) {
				that.setReachable();
				mReachable = false;
				if (mMarked) {
					that.mark();
					mMarked = false;
				}
			}
			mEPEG.simplify();
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

		public MultiGenEPEG<O,T,V> getGraph() {return mEPEG;}
		public Node<O,T,V> getSelf() {return getGroup();}
		
		public List<Node<O,T,V>> getChildren() {
			if (mGroup != null)
				return getGroup().getChildren();
			if (mChildren == null)
				return Collections.<Node<O,T,V>>emptyList();
			else
				return Arrays.asList(mChildren);
		}

		public boolean hasChildren(Node<O,T,V>... children) {
			if (mGroup != null)
				return getGroup().hasChildren(children);
			if (mChildren == null)
				return children.length == 0;
			if (mChildren.length != children.length)
				return false;
			for (int i = 0; i < children.length; i++)
				if (!mChildren[i].equals(children[i]))
					return false;
			return true;
		}

		public boolean hasChildren(List<? extends Node<O,T,V>> children) {
			if (mGroup != null)
				return getGroup().hasChildren(children);
			if (mChildren == null || mChildren.length != children.size())
				return false;
			for (int i = 0; i < mChildren.length; i++)
				if (!mChildren[i].equals(children.get(i)))
					return false;
			return true;
		}

		public GenOp<O> getLabel() {return getGroup().mOp;}
		public boolean hasLabel(GenOp<O> label) {
			return label == null ? mOp == null
					: mOp != null && mOp.equals(label);
		}

		public boolean hasTag(Tag label) {
			if (mGroup != null)
				return getGroup().hasTag(label);
			return mTags != null && mTags.containsKey(label);
		}
		
		public <L> L getTag(Tag<L> label) {
			if (mGroup != null)
				return getGroup().getTag(label);
			return mTags == null ? null : (L)mTags.get(label);
		}
		
		public void setTag(Tag<Void> label) {setTag(label, null);}

		public <L> L setTag(Tag<L> label, L tag) {
			if (mGroup != null)
				return getGroup().setTag(label, tag);
			if (mTags == null)
				mTags = new WeakHashMap<Tag,Object>();
			return (L)mTags.put(label, tag);
		}

		public <L> L removeTag(Tag<L> label) {
			if (mGroup != null)
				return getGroup().removeTag(label);
			return mTags == null ? null : (L)mTags.remove(label);
		}
		
		public String tagsToString() {
			if (mGroup != null)
				return getGroup().tagsToString();
			return mTags == null ? "[]" : mTags.toString();
		}
	}
	
	private final OpAmbassador<O> mAmbassador;
	private final GenOpAmbassador mLabelAmbassador = new GenOpAmbassador();
	private final Set<Node<O,T,V>> mNodes = new HashSet();
	private final Set<Couple<Node<O,T,V>>> mEqualities = new HashSet();
	
	public MultiGenEPEG(OpAmbassador<O> ambassador) {mAmbassador = ambassador;}
	
	public Node<O,T,V> createNode(TermOrTermChild<T,V> anchor) {
		return new Node<O,T,V>(this, anchor);
	}
	
	private void simplify() {
		for (Node<O,T,V> node : mNodes)
			for (Node<O,T,V> that : mNodes)
				if (!node.equals(that) && node.isIdenticalTo(that)) {
					node.unifyWith(that);
					return;
				}
	}
	
	public void addEquality(Node<O,T,V> left, Node<O,T,V> right) {
		mEqualities.add(new Couple<Node<O,T,V>>(left, right));
	}
	
	public String toString() {return toString(false);}
	
	public String toString(boolean reachableOnly) {
		StringBuilder string = new StringBuilder("digraph {\nordering=out;\n");
		for (Node<O,T,V> node : mNodes)
			if (!reachableOnly || node.mReachable) {
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
			if ((!reachableOnly || node.mReachable) && node.mChildren != null)
				for (int i = 0; i < node.getArity(); i++) {
					string.append(node.hashCode());
					string.append(" -> ");
					string.append(node.mChildren[i].hashCode());
					string.append(" [taillabel=\"");
					string.append(i);
					string.append("\"];\n");
				}
		for (Couple<Node<O,T,V>> equality : mEqualities) {
			string.append(equality.getLeft().hashCode());
			string.append(" -> ");
			string.append(equality.getRight().hashCode());
			string.append(" [arrowhead=none,style=\"dashed\"];\n");
		}
		string.append("}\n");
		return string.toString();
	}
	
	private class GenOpAmbassador
			extends PEGLabelAmbassador<GenOp<O>,LoopDepth,O> {
		public boolean isExtendedDomain(GenOp<O> label) {
			return label.mDomainOp != null;
		}
		public FlowValue<?,O> getExtendedDomain(GenOp<O> label) {
			return FlowValue.createDomain(label.mDomainOp, mAmbassador);
		}
		
		public boolean isPhi(GenOp<O> label) {return label.mIsPhi;}
		public boolean isZero(GenOp<O> label) {return label.mIsZero;}
		public boolean isSuccessor(GenOp<O> label) {return label.mIsSuccessor;}
		
		public boolean isLoopOp(GenOp<O> label) {return label.mLoopOp != null;}
		public PEGLoopOp getLoopOp(GenOp<O> label) {return label.mLoopOp;}
		public LoopDepth getLoopDepth(GenOp<O> label) {return label.mLoopDepth;}

		public boolean mustBeExtendedDomain(GenOp<O> label) {
			return label.mIsExtendedDomain && label.mDomainOp == null;
		}
		
		public boolean mustBeLoopLifted(GenOp<O> op, LoopDepth depth) {
			return op.mLifted == null || op.mLifted.contains(depth);
		}

		public boolean mustBeDistinctLoops(LoopDepth left, LoopDepth right) {
			return left.mDistinct.contains(right);
		}
	}
	
	public PEGLabelAmbassador<GenOp<O>,LoopDepth,O> getLabelAmbassador() {
		return mLabelAmbassador;
	}

	public MultiGenEPEG<O,T,V> getSelf() {return this;}

	public Collection<? extends Node<O,T,V>> getVertices() {return mNodes;}
}
