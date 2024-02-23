package eqsat.meminfer.engine.generalize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;

import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.meminfer.engine.generalize.PostMultiGenPEG.GenOp;
import eqsat.meminfer.engine.generalize.PostMultiGenPEG.LoopDepth;
import eqsat.meminfer.engine.generalize.PostMultiGenPEG.Node;
import eqsat.meminfer.engine.peg.PEGTerm;
import eqsat.meminfer.engine.peg.PEGValue;
import eqsat.meminfer.network.peg.PEGLabelAmbassador;
import eqsat.meminfer.network.peg.PEGNetwork.PEGLoopOp;
import util.AbstractPattern;
import util.Grouping;
import util.HashGrouping;
import util.Labeled;
import util.Tag;
import util.Taggable;
import util.UnhandledCaseException;
import util.graph.AbstractGraph;
import util.graph.AbstractVertex;
import util.graph.OrderedVertex;
import util.pair.Couple;

public class PostMultiGenEPEG
		<O, T extends PEGTerm<O,?,T,V>, V extends PEGValue<T,V>> extends
		AbstractGraph<PostMultiGenEPEG<O,T,V>,PostMultiGenEPEG.ENode<O,T,V>> {
	public static class ELoopDepth {
		private final LoopDepth mAnchor;
		private ELoopDepth mGroup;
		private List<ELoopDepth> mDistinct = new ArrayList();
		
		private ELoopDepth(PostMultiGenEPEG<?,?,?> epeg, LoopDepth anchor) {
			mAnchor = anchor;
		}
		
		private ELoopDepth getGroup() {
			if (mGroup == null)
				return this;
			else
				return mGroup = mGroup.getGroup();
		}
		
		public void unifyWith(ELoopDepth that) {
			if (mGroup != null) {
				getGroup().unifyWith(that);
				return;
			}
			that = that.getGroup();
			if (this == that)
				return;
			if (!mAnchor.equals(that.mAnchor))
				throw new IllegalStateException();
			mDistinct.removeAll(that.mDistinct);
			that.mDistinct.addAll(mDistinct);
			mDistinct = null;
			mGroup = that;
		}

		public boolean isDistinctFrom(ELoopDepth that) {
			return getGroup().mDistinct.contains(that);
		}
		public void setDistinctFrom(ELoopDepth that) {
			if (mGroup != null) {
				getGroup().setDistinctFrom(that);
				return;
			}
			that = that.getGroup();
			if (mAnchor.equals(that.mAnchor))
				throw new IllegalStateException();
			if (!mDistinct.contains(that))
				mDistinct.add(that);
			if (!that.mDistinct.contains(this))
				that.mDistinct.add(this);
		}
		
		private boolean mapTo(Mapping<?,?,?,?,?> mapping, ELoopDepth that) {
			if (mapping.containsKey(this))
				return mapping.get(this).equals(that);
			mapping.put(this, that);
			return true;
		}
		
		public boolean equals(Object that) {
			return that instanceof ELoopDepth && equals((ELoopDepth)that);
		}
		public boolean equals(ELoopDepth that) {
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
	
	public static class EGenOp<O> {
		private final PostMultiGenEPEG<O,?,?> mEPEG;
		private final GenOp<O> mAnchor;
		private EGenOp<O> mGroup;
		private boolean mIsExtendedDomain = false;
		private O mDomainOp;
		private boolean mIsPhi = false;
		private boolean mIsZero = false;
		private boolean mIsSuccessor = false;
		private PEGLoopOp mLoopOp;
		private ELoopDepth mLoopDepth;
		private List<ELoopDepth> mLifted = new ArrayList<ELoopDepth>();
		
		private EGenOp(PostMultiGenEPEG<O,?,?> epeg, GenOp<O> anchor) {
			mEPEG = epeg;
			mAnchor = anchor;
		}
		
		private EGenOp<O> getGroup() {
			if (mGroup == null)
				return this;
			else
				return mGroup = mGroup.getGroup();
		}
		
		public void unifyWith(EGenOp<O> that) {
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
				for (ELoopDepth depth : mLifted)
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
			if (!mAnchor.getExtendedDomain().equals(op))
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
			if (!op.equals(mAnchor.getLoopOp()))
				throw new IllegalStateException();
			mLoopOp = op;
			if (mLoopDepth == null)
				mLoopDepth = new ELoopDepth(mEPEG, mAnchor.getLoopDepth());
			if (mLifted != null) {
				for (ELoopDepth depth : mLifted)
					mLoopDepth.setDistinctFrom(depth);
				mLifted = null;
			}
		}
		
		public ELoopDepth getLoopDepth() {
			if (mGroup != null)
				return getGroup().getLoopDepth();
			else if (mLoopDepth != null)
				return mLoopDepth;
			else
				throw new IllegalStateException();
		}

		public boolean isAllLoopLifted() {
			return mAnchor.isAllLoopLifted() && getGroup().mLifted == null;
		}
		public void setAllLoopLifted() {
			if (mGroup != null) {
				getGroup().setAllLoopLifted();
				return;
			}
			if (!mAnchor.isAllLoopLifted())
				throw new IllegalStateException();
			mLifted = null;
		}

		public boolean isLoopLifted(ELoopDepth depth) {
			if (mLoopOp != null)
				return mLoopDepth.isDistinctFrom(depth);
			else if (mLifted != null)
				return mLifted.contains(depth);
			else
				return mAnchor.isAllLoopLifted();
		}
		public void setLoopLifted(ELoopDepth depth) {
			if (mGroup != null) {
				getGroup().setLoopLifted(depth);
				return;
			}
			if (!mAnchor.isLoopLifted(depth.mAnchor))
				throw new IllegalArgumentException();
			if (mLoopOp != null)
				mLoopDepth.setDistinctFrom(depth);
			else if (mLifted != null && !mLifted.contains(depth))
				mLifted.add(depth);
		}
		
		private boolean isIdenticalTo(EGenOp<O> that) {
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
		
		private boolean mapTo(Mapping<O,?,?,?,?> mapping, EGenOp<O> that) {
			if (mGroup != null)
				return getGroup().mapTo(mapping, that);
			that = that.getGroup();
			if (mapping.containsKey(this))
				return mapping.get(this).equals(that);
			mapping.put(this, that);
			if (mIsExtendedDomain) {
				if (!that.mIsExtendedDomain)
					return false;
				if (mDomainOp != null)
					return that.mDomainOp != null
							&& mDomainOp.equals(that.mDomainOp);
				return true;
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
				return mLoopDepth.mapTo(mapping, that.mLoopDepth);
			} else if (mLifted != null)
				return true;
			else
				return that.mLoopOp == null && that.mLifted == null;
		}
		
		public boolean equals(Object that) {return equals((EGenOp)that);}
		public boolean equals(EGenOp<O> that) {
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
	
	public final static class ENode
			<O, T extends PEGTerm<O,?,T,V>, V extends PEGValue<T,V>>
			extends AbstractVertex<PostMultiGenEPEG<O,T,V>,ENode<O,T,V>>
			implements OrderedVertex<PostMultiGenEPEG<O,T,V>,ENode<O,T,V>>,
			Taggable, Labeled<EGenOp<O>> {
		private Node<O,T,V> mAnchor;
		private final PostMultiGenEPEG<O,T,V> mEPEG;
		private ENode<O,T,V> mGroup;
		private EGenOp<O> mOp;
		private ENode<O,T,V>[] mChildren;
		private List<ELoopDepth> mInvariance = new ArrayList();
		private boolean mMarked;
		private boolean mTrigger;
		private boolean mResult;
		private Map<Tag,Object> mTags = null;
		
		private ENode(PostMultiGenEPEG<O,T,V> epeg, Node<O,T,V> anchor,
				boolean trigger, boolean result, boolean marked) {
			mEPEG = epeg;
			epeg.mNodes.add(this);
			mAnchor = anchor;
			mTrigger = trigger;
			mResult = result;
			mMarked = marked;
			if (mMarked && mTrigger) {
				if (mEPEG.mTrigger == null)
					mEPEG.mTrigger = this;
				else if (mEPEG.mResult == null)
					mEPEG.mResult = this;
				else
					throw new RuntimeException();
			} else if (mMarked && mResult) {
				if (mEPEG.mResult == null)
					mEPEG.mResult = this;
				else
					throw new RuntimeException();
			}
		}
		
		public Node<O,T,V> getAnchor() {return mAnchor;}
		
		private ENode<O,T,V> getGroup() {
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
				if (!mAnchor.hasArity() || arity != mAnchor.getArity())
					throw new IllegalArgumentException();
				mChildren = new ENode[arity];
				for (int i = 0; i < arity; i++)
					mChildren[i] = new ENode<O,T,V>(mEPEG, mAnchor.getChild(i),
							mTrigger, mResult, false);
				mOp = new EGenOp<O>(mEPEG, mAnchor.getOp());
			}
			else if (mChildren.length != arity)
				throw new IllegalStateException();
		}
		public ENode<O,T,V> getChild(int index) {
			if (mGroup != null)
				return getGroup().getChild(index);
			if (mChildren == null)
				throw new IllegalStateException();
			else
				return mChildren[index];
		}
		
		public EGenOp<O> getOp() {
			if (mGroup != null)
				return getGroup().getOp();
			if (mOp == null)
				throw new IllegalStateException();
			else
				return mOp;
		}

		public boolean isInvariant(ELoopDepth depth) {
			return getGroup().mInvariance.contains(depth);
		}
		public Collection<? extends ELoopDepth> getInvariance() {
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
		public void setInvariant(ELoopDepth depth) {
			if (mGroup != null) {
				getGroup().setInvariant(depth);
				return;
			}
			if (!mAnchor.getAnchor().getValue().isInvariant(
					depth.mAnchor.getAnchor()))
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
				for (ENode<O,T,V> child : mChildren)
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
				for (ENode<O,T,V> child : mChildren)
					child.setResult();
		}
		public boolean isReachable() {
			return getGroup().mTrigger || getGroup().mResult;
		}
		
		public void unifyWith(ENode<O,T,V> that) {
			if (mGroup != null) {
				getGroup().unifyWith(that);
				return;
			}
			that = that.getGroup();
			if (this == that)
				return;
			if (!mAnchor.getAnchor().getValue().equals(
					that.mAnchor.getAnchor().getValue()))
				throw new IllegalStateException();
			if (mTags != null || that.mTags != null)
				throw new IllegalStateException();
			if (mChildren != null && that.mChildren == null) {
				that.unifyWith(this);
				return;
			}
			mEPEG.mNodes.remove(this);
			mGroup = that;
			for (ELoopDepth depth : mInvariance)
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
				ENode<O,T,V>[] children = mChildren;
				ENode<O,T,V>[] thatChildren = that.mChildren;
				mChildren = null;
				mOp = null;
				for (int i = 0; i < children.length; i++)
					children[i].unifyWith(thatChildren[i]);
			}
		}
		
		public <U extends PEGTerm<O,?,U,W>, W extends PEGValue<U,W>> boolean
				mapsTo(ENode<O,U,W> that) {
			return mapTo(new Mapping<O,T,V,U,W>(), that);
		}
		
		private <U extends PEGTerm<O,?,U,W>, W extends PEGValue<U,W>> boolean
				mapTo(Mapping<O,T,V,U,W> mapping, ENode<O,U,W> that) {
			if (mGroup != null)
				return getGroup().mapTo(mapping, that);
			that = that.getGroup();
			if (mapping.containsKey(this))
				return mapping.get(this).equals(that);
			mapping.put(this, that);
			if (mChildren != null) {
				if (that.mChildren == null)
					return false;
				if (mChildren.length != that.mChildren.length)
					return false;
				if (!mOp.mapTo(mapping, that.mOp))
					return false;
				for (int i = 0; i < mChildren.length; i++)
					if (!mChildren[i].mapTo(mapping, that.mChildren[i]))
						return false;
			}
			return true;
		}
		
		public boolean equals(Object that) {return equals((ENode)that);}
		public boolean equals(ENode<O,T,V> that) {
			return getGroup() == that.getGroup();
		}
		public int hashCode() {
			if (mGroup == null)
				return super.hashCode();
			else
				return getGroup().hashCode();
		}
		
		public String toString() {return hashCode() + ".Anchor:" + mAnchor;}

		public PostMultiGenEPEG<O,T,V> getGraph() {return mEPEG;}
		public ENode<O,T,V> getSelf() {return getGroup();}
		
		public List<ENode<O,T,V>> getChildren() {
			if (mGroup != null)
				return getGroup().getChildren();
			if (mChildren == null)
				return Collections.<ENode<O,T,V>>emptyList();
			else
				return Arrays.asList(mChildren);
		}

		public boolean hasChildren(ENode<O,T,V>... children) {
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

		public boolean hasChildren(List<? extends ENode<O,T,V>> children) {
			if (mGroup != null)
				return getGroup().hasChildren(children);
			if (mChildren == null || mChildren.length != children.size())
				return false;
			for (int i = 0; i < mChildren.length; i++)
				if (!mChildren[i].equals(children.get(i)))
					return false;
			return true;
		}

		public EGenOp<O> getLabel() {return getGroup().mOp;}
		public boolean hasLabel(EGenOp<O> label) {
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
	private static final class Mapping<O,
			T extends PEGTerm<O,?,T,V>, V extends PEGValue<T,V>,
			U extends PEGTerm<O,?,U,W>, W extends PEGValue<U,W>> {
		private final Map<ENode<O,T,V>,ENode<O,U,W>> mNodeMap = new HashMap();
		private final Map<EGenOp<O>,EGenOp<O>> mOpMap = new HashMap();
		private final Map<ELoopDepth,ELoopDepth> mDepthMap = new HashMap();
		
		protected boolean containsKey(ENode<O,T,V> node) {
			return mNodeMap.containsKey(node);
		}
		protected boolean containsKey(EGenOp<O> op) {
			return mOpMap.containsKey(op);
		}
		protected boolean containsKey(ELoopDepth depth) {
			return mDepthMap.containsKey(depth);
		}
		
		protected ENode<O,U,W> get(ENode<O,T,V> node) {
			return mNodeMap.get(node);
		}
		protected EGenOp<O> get(EGenOp<O> op) {return mOpMap.get(op);}
		protected ELoopDepth get(ELoopDepth depth) {
			return mDepthMap.get(depth);
		}
		
		protected void put(ENode<O,T,V> key, ENode<O,U,W> value) {
			ENode<O,U,W> old = mNodeMap.put(key.getGroup(), value.getGroup());
			if (old != null && !old.equals(value))
				throw new IllegalStateException();
		}
		protected void put(EGenOp<O> key, EGenOp<O> value) {
			EGenOp<O> old = mOpMap.put(key.getGroup(), value.getGroup());
			if (old != null && !old.equals(value))
				throw new IllegalStateException();
		}
		protected void put(ELoopDepth key, ELoopDepth value) {
			ELoopDepth old = mDepthMap.put(key.getGroup(), value.getGroup());
			if (old != null && !old.equals(value))
				throw new IllegalStateException();
		}
		
		protected boolean isValid() {
			for (Entry<ENode<O,T,V>,ENode<O,U,W>> entry : mNodeMap.entrySet())
				for (ELoopDepth depth : entry.getKey().mInvariance)
					if (!entry.getValue().mInvariance.contains(get(depth)))
						return false;
			for (Entry<EGenOp<O>,EGenOp<O>> entry : mOpMap.entrySet())
				if (entry.getKey().mLifted != null)
					for (ELoopDepth depth : entry.getKey().mLifted)
						if (!(entry.getValue().mLoopOp == null
								? entry.getValue().mLifted == null
										|| entry.getValue().mLifted.contains(
												get(depth))
								: entry.getValue().mLoopDepth.mDistinct
										.contains(get(depth))))
							return false;
			for (Entry<ELoopDepth,ELoopDepth> entry : mDepthMap.entrySet())
				for (ELoopDepth depth : entry.getKey().mDistinct)
					if (!entry.getValue().mDistinct.contains(get(depth)))
						return false;
			return true;
		}
		
		protected void clear() {
			mNodeMap.clear();
			mOpMap.clear();
			mDepthMap.clear();
		}
	}

	private final OpAmbassador<O> mAmbassador;
	private final PEGLabelAmbassador<EGenOp<O>,ELoopDepth,O> mEGenOpAmbassador
			= new PEGLabelAmbassador<EGenOp<O>,ELoopDepth,O>() {
		public boolean isExtendedDomain(EGenOp<O> label) {
			return label.getGroup().mDomainOp != null;
		}
		public FlowValue<?,O> getExtendedDomain(EGenOp<O> label) {
			return FlowValue.createDomain(label.getExtendedDomain(),
					mAmbassador);
		}
		
		public boolean isPhi(EGenOp<O> label) {return label.isPhi();}
		public boolean isZero(EGenOp<O> label) {return label.isZero();}
		public boolean isSuccessor(EGenOp<O> label){return label.isSuccessor();}
		
		public boolean isLoopOp(EGenOp<O> label) {
			return label.getGroup().mLoopOp != null;
		}
		public PEGLoopOp getLoopOp(EGenOp<O> label) {return label.getLoopOp();}
		public ELoopDepth getLoopDepth(EGenOp<O> label) {
			return label.getLoopDepth();
		}

		public boolean mustBeExtendedDomain(EGenOp<O> label) {
			return label.isExtendedDomain()
					&& label.getGroup().mDomainOp == null;
		}
		
		public boolean mustBeLoopLifted(EGenOp<O> label, ELoopDepth depth) {
			return label.getGroup().mLifted == null
					|| label.getGroup().mLifted.contains(depth);
		}

		public boolean mustBeDistinctLoops(ELoopDepth left, ELoopDepth right) {
			return left.isDistinctFrom(right);
		}
	};
	private final Set<ENode<O,T,V>> mNodes = new HashSet();
	private final Set<Couple<ENode<O,T,V>>> mEqualities = new HashSet();
	private ENode<O,T,V> mTrigger, mResult;
	
	public PostMultiGenEPEG(OpAmbassador<O> ambassador) {
		mAmbassador = ambassador;
	}
	
	public ENode<O,T,V> createNode(Node<O,T,V> anchor) {
		return new ENode<O,T,V>(this, anchor, false, false, false);
	}
	public ENode<O,T,V> createMarkedNode(Node<O,T,V> anchor,
			boolean trigger, boolean result) {
		return new ENode<O,T,V>(this, anchor, trigger, result, true);
	}
	
	public void simplify() {
		for (ENode<O,T,V> node : mNodes)
			if (node.hasArity())
				for (ENode<O,T,V> that : mNodes)
					if (that.hasArity()
							&& node.getOp().isIdenticalTo(that.getOp()))
						node.getOp().unifyWith(that.getOp());
		ENode<O,T,V>[] nodes = mNodes.toArray(new ENode[mNodes.size()]);
		for (int i = 0; i < nodes.length; i++) {
			ENode<O,T,V> node = nodes[i];
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
	
	private boolean canSimplify(ENode<O,T,V> left, ENode<O,T,V> right) {
		return canSimplify(left, right, new HashGrouping<ENode<O,T,V>>());
	}
	private boolean canSimplify(ENode<O,T,V> left, ENode<O,T,V> right,
			Grouping<ENode<O,T,V>> unified) {
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
	
	public boolean improveInvariance() {
		for (ENode<O,T,V> node : mNodes)
			if (!node.isTrigger())
				for (final ELoopDepth depth : node.getInvariance()) {
					final Set<ENode<O,T,V>> remove = new HashSet();
					final Set<ENode<O,T,V>> add = new HashSet();
					final Set<ELoopDepth> distinct = new HashSet();
					final Set<EGenOp<O>> lift = new HashSet();
					if (new AbstractPattern<ENode<O,T,V>>() {
								private final Set<ENode<O,T,V>> mProcessed
										= new HashSet();
								public boolean matches(ENode<O,T,V> parameter) {
									if (parameter.isTrigger()) {
										add.add(parameter);
										return true;
									} else if (!parameter.hasArity())
										return false;
									else if (!mProcessed.add(parameter))
										return true;
									if (parameter.isInvariant(depth))
										remove.add(parameter);
									EGenOp<O> op = parameter.getOp();
									if (op.isExtendedDomain()
											|| op.isAllLoopLifted()
											|| op.isPhi()
											|| op.isZero()
											|| op.isSuccessor()) {
										for (ENode<O,T,V> child
												: parameter.getChildren())
											if (!matches(child))
												return false;
										return true;
									} else if (op.isLoopOp()) {
										switch (op.getLoopOp()) {
										case Theta:
											if (depth.equals(op.getLoopDepth()))
												return false;
											else {
												distinct.add(op.getLoopDepth());
												return true;
											}
										case Shift:
											if (parameter.getArity() != 1)
												throw new RuntimeException();
											return matches(
													parameter.getChild(0));
										case Pass:
											return true;
										case Eval:
											if (parameter.getArity() != 2)
												throw new RuntimeException();
											if (depth.equals(op.getLoopDepth()))
												return matches(
														parameter.getChild(1));
											else {
												distinct.add(op.getLoopDepth());
												return matches(
														parameter.getChild(0))
														&& matches(
														parameter.getChild(1));
											}
										default:
											throw new UnhandledCaseException();
										}
									} else {
										lift.add(op);
										for (ENode<O,T,V> child
												: parameter.getChildren())
											if (!matches(child))
												return false;
										return true;
									}
								}
							}.matches(node)) {
						for (ENode<O,T,V> that : remove)
							that.getGroup().mInvariance.remove(depth);
						for (ENode<O,T,V> that : add)
							that.setInvariant(depth);
						for (ELoopDepth that : distinct)
							depth.setDistinctFrom(that);
						for (EGenOp<O> op : lift)
							op.setLoopLifted(depth);
					} else
						return false;
				}
		return true;
	}
	
	public void addEquality(ENode<O,T,V> left, ENode<O,T,V> right) {
		mEqualities.add(new Couple<ENode<O,T,V>>(left, right));
	}
	
	public Collection<? extends Couple<ENode<O,T,V>>> getEqualities() {
		return mEqualities;
	}
	
	public boolean canAxiomizeSafely() {
		for (ENode<O,T,V> node : mNodes)
			if (node.isResult() && !node.isTrigger() && !node.hasArity())
				return false;
		for (ENode<O,T,V> node : mNodes)
			if (!node.isTrigger() && !node.getInvariance().isEmpty())
				return false;
		for (Couple<ENode<O,T,V>> equality : mEqualities)
			if (!equality.getLeft().isTrigger()
					|| !equality.getRight().isTrigger())
				return false;
		Set<EGenOp<O>> ops = new HashSet();
		Set<ELoopDepth> depths = new HashSet();
		for (ENode<O,T,V> node : mNodes)
			if (node.isTrigger() && node.hasArity()) {
				ops.add(node.getOp());
				if (node.getOp().isLoopOp())
					depths.add(node.getOp().getLoopDepth());
			}
		for (ENode<O,T,V> node : mNodes)
			if (node.isResult() && !node.isTrigger() && node.hasArity()
					&& !ops.contains(node.getOp())
					&& node.getOp().getGroup().mDomainOp == null
					&& !node.getOp().isPhi()
					&& !node.getOp().isZero() && !node.getOp().isSuccessor()
					&& !(node.getOp().isLoopOp()
							&& depths.contains(node.getOp().getLoopDepth())))
				return false;
		for (ENode<O,T,V> node : mNodes)
			if (!depths.containsAll(node.getInvariance()))
				return false;
		return true;
	}
	
	public <U extends PEGTerm<O,?,U,W>, W extends PEGValue<U,W>> boolean
			subsumes(PostMultiGenEPEG<O,U,W> that){
		if (mResult.isTrigger() && !that.mResult.isTrigger())
			return false;
		Mapping<O,T,V,U,W> mapping = new Mapping<O,T,V,U,W>();
		if (!mTrigger.mapTo(mapping, that.mTrigger)
				|| !mResult.mapTo(mapping, that.mResult)
				|| !mapping.isValid()) {
			if (!that.mResult.isTrigger())
				return false;
			mapping.clear();
			if (!mTrigger.mapTo(mapping, that.mResult)
					|| !mResult.mapTo(mapping, that.mTrigger)
					|| !mapping.isValid())
				return false;
		}
		for (Couple<ENode<O,T,V>> equality : mEqualities)
			if (!mapping.get(equality.getLeft()).equals(
							mapping.get(equality.getRight()))
					&& !that.mEqualities.contains(new Couple<ENode<O,U,W>>(
							mapping.get(equality.getLeft()),
							mapping.get(equality.getRight()))))
				return false;
		return true;
	}
	
	public ENode<O,T,V> getTrigger() {return mTrigger;}
	public ENode<O,T,V> getResult() {return mResult;}

	public void clearAnchors() {
		mTrigger = mTrigger.getGroup();
		mResult = mResult.getGroup();
		for (ENode<O,T,V> node : mNodes) {
			node.mAnchor = null;
			if (node.hasArity()) {
				for (int i = 0; i < node.getArity(); i++)
					node.mChildren[i] = node.mChildren[i].getGroup();
				node.mOp = node.mOp.getGroup();
				if (node.mOp.mLifted != null) {
					Set<ELoopDepth> lifted
							= new HashSet<ELoopDepth>(node.mOp.mLifted);
					ELoopDepth[] liftedArray
							= lifted.toArray(new ELoopDepth[lifted.size()]);
					for (int i = 0; i < liftedArray.length; i++)
						liftedArray[i] = liftedArray[i].getGroup();
					node.mOp.mLifted = new ArrayList<ELoopDepth>(
							Arrays.asList(liftedArray));
				}
				if (node.mOp.mLoopDepth != null) {
					node.mOp.mLoopDepth = node.mOp.mLoopDepth.getGroup();
					Set<ELoopDepth> distinct = new HashSet<ELoopDepth>(
							node.mOp.mLoopDepth.mDistinct);
					ELoopDepth[] distinctArray
							= distinct.toArray(new ELoopDepth[distinct.size()]);
					for (int i = 0; i < distinctArray.length; i++)
						distinctArray[i] = distinctArray[i].getGroup();
					node.mOp.mLoopDepth.mDistinct = new ArrayList<ELoopDepth>(
							Arrays.asList(distinctArray));
				}
			}
		}
		Set<Couple<ENode<O,T,V>>> equalities
				= new HashSet<Couple<ENode<O,T,V>>>(mEqualities);
		mEqualities.clear();
		for (Couple<ENode<O,T,V>> equality : equalities)
			mEqualities.add(new Couple<ENode<O,T,V>>(
					equality.getLeft().getGroup(),
					equality.getRight().getGroup()));
	}
	
	public String toString() {return toString(false);}
	public String toString(boolean reachableOnly) {
		StringBuilder string = new StringBuilder("digraph {\nordering=out;\n");
		for (ENode<O,T,V> node : mNodes)
			if (!reachableOnly || node.isReachable()) {
				string.append(node.hashCode());
				string.append(" [label=\"");
				if (node.mOp != null)
					string.append(node.getOp());
				else if (node.mAnchor != null ) {
					string.append("Anchor:");
					string.append(node.mAnchor.getAnchor());
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
		for (ENode<O,T,V> node : mNodes)
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
		for (Couple<ENode<O,T,V>> equality : mEqualities) {
			string.append(equality.getLeft().hashCode());
			string.append(" -> ");
			string.append(equality.getRight().hashCode());
			string.append(" [arrowhead=none,style=\"dotted\"];\n");
		}
		string.append("}\n");
		return string.toString();
	}
	
	public PEGLabelAmbassador<EGenOp<O>,ELoopDepth,O> getEGenOpAmbassador() {
		return mEGenOpAmbassador;
	}

	public PostMultiGenEPEG<O,T,V> getSelf() {return this;}

	public Collection<? extends ENode<O,T,V>> getVertices() {return mNodes;}
}
