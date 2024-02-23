package eqsat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import util.graph.GenericVertex;
import util.integer.ArrayIntMap;
import util.integer.BitIntSet;
import util.integer.IntIterator;

public final class FlowVertex
		<B extends Block<? extends CFG<?,?,? extends V,?,?,?>,? extends B,V,?>,
		V>
		extends GenericVertex<Flow<?,B,V>,FlowVertex<B,V>>
		implements Comparable<FlowVertex<B,V>> {
	private FlowVertex<B,V> mLoop;
	private int mOrderIndex = -1;
	private final BitIntSet mAncestors = new BitIntSet();
	private final BitIntSet mDominators = new BitIntSet();
	private final BitIntSet mLoops = new BitIntSet();
	private final B mBlock;
	private Set<V> mLoopUnmodified = null;
	
	public FlowVertex(Flow<?,B,V> flow, B block) {
		super(flow, new ArrayList<FlowVertex<B,V>>(2),
				new HashSet<FlowVertex<B,V>>());
		mBlock = block;
		mLoop = null;
	}
	public FlowVertex(Flow<?,B,V> flow, FlowVertex<B,V> loop) {
		super(flow, new ArrayList<FlowVertex<B,V>>(2),
				new HashSet<FlowVertex<B,V>>());
		mBlock = null;
		mLoop = loop;
		loop.mLoop = this;
	}
	
	public FlowVertex<B,V> getSelf() {return this;}
	
	public boolean isLoopRoot() {return mLoop != null && mBlock != null;}
	public boolean isLoopBack() {return mLoop != null && mBlock == null;}
	public FlowVertex<B,V> getLoopBack() {return mLoop;}
	public boolean isLoopModified(V variable) {
		return !mLoopUnmodified.contains(variable);
	}
	
	public B getBlock() {return mBlock;}

	public Set<? extends FlowVertex<B,V>> getParents() {
		return (Set<? extends FlowVertex<B,V>>)mParents;
	}
	public List<? extends FlowVertex<B,V>> getChildren() {
		return (List<? extends FlowVertex<B,V>>)mChildren;
	}
	public FlowVertex<B,V> getChild(int index) {
		return getChildren().get(index);
	}
	
	public int doDominators(int orderIndex) {
		if (mOrderIndex != -1)
			return orderIndex;
		for (FlowVertex parent : getParents())
			orderIndex = parent.doDominators(orderIndex);
		if (!isRoot()) {
			mDominators.addRange(0, orderIndex);
			for (FlowVertex parent : getParents()) {
				mDominators.retainAll(parent.mDominators);
				mAncestors.addAll(parent.mAncestors);
			}
		}
		mOrderIndex = orderIndex++;
		mDominators.add(mOrderIndex);
		mAncestors.add(mOrderIndex);
		return orderIndex;
	}
	
	public void doLoops() {
		if (isLoopRoot()) {
			mLoopUnmodified = new HashSet<V>(mBlock.getGraph().getVariables());
			addLoop(this);
		}
	}
	
	private void addLoop(FlowVertex<B,V> loop) {
		if (!mLoops.contains(loop.mOrderIndex)) {
			if (mBlock != null)
				for (Iterator<V> variables = loop.mLoopUnmodified.iterator();
						variables.hasNext(); )
					if (mBlock.modifies(variables.next()))
						variables.remove();
			mLoops.add(loop.mOrderIndex);
			if (loop != this)
				for (FlowVertex<B,V> parent : getParents())
					parent.addLoop(loop);
			if (isLoopRoot())
				getLoopBack().addLoop(loop);
		}
	}
	
	public <G, L, P, A extends APEG<G,B,V,L,P>> A.Node getInput(A apeg,
			BitIntSet work) {
		BitIntSet loops = new BitIntSet(mLoops);
		if (isRoot())
			return apeg.getParameters();
		A.Node initial = decide(
				apeg, getGraph().getStart(), getParents(), null, loops, work);
		if (mLoop != null) {
			A.Node loop =
					decide(apeg, this, mLoop.getParents(), null, loops, work);
			A.Node theta = apeg.getTheta(mLoops.size(), initial, loop);
			return apeg.getCheckUnmodified(mLoopUnmodified, initial, theta);
		} else
			return initial;
	}
	
	private <G, L, P, A extends APEG<G,B,V,L,P>> A.Node decide(A apeg,
			FlowVertex<B,V> root,
			Set<? extends FlowVertex<B,V>> targets,
			FlowVertex<B,V> breaking, BitIntSet context, BitIntSet work) {
		if (breaking != null) {
			if (targets.size() == 1 && targets.contains(breaking))
				return apeg.getFalse();
			else if (!targets.contains(breaking))
				return apeg.getTrue();
		}
		work.addRange(0, getGraph().getVertices().size());
		for (FlowVertex<B,V> target : targets)
			work.retainAll(target.mDominators);
		FlowVertex<B,V> decider = getGraph().getVertex(work.lastInt());
		int rootIndex = root.mOrderIndex;
		if (!decider.mAncestors.contains(rootIndex)) {
			work.clear();
			for (FlowVertex<B,V> target : targets)
				work.addAll(target.mAncestors);
			ArrayIntMap<BitIntSet> dominatorMap = new ArrayIntMap<BitIntSet>();
			for (IntIterator indexes = work.iterator(); indexes.hasNext(); ) {
				FlowVertex<B,V> ancestor
						= getGraph().getVertex(indexes.next());
				if (ancestor == root) {
					BitIntSet dominators = new BitIntSet(rootIndex + 1);
					dominators.add(rootIndex);
					dominatorMap.put(rootIndex, dominators);
				} else if (ancestor.mAncestors.contains(rootIndex)) {
					BitIntSet dominators
							= new BitIntSet(getGraph().getVertices().size());
					dominators.addRange(0, getGraph().getVertices().size());
					for (FlowVertex<B,V> parent : ancestor.getParents())
						if (parent.mAncestors.contains(rootIndex))
							dominators.retainAll(
									dominatorMap.get(parent.mOrderIndex));
					dominators.add(ancestor.mOrderIndex);
					dominatorMap.put(ancestor.mOrderIndex, dominators);
				}
			}
			for (FlowVertex<B,V> target: targets)
				work.retainAll(dominatorMap.get(target.mOrderIndex));
			decider = getGraph().getVertex(work.lastInt());
		}
		boolean unique = targets.size() == 1 && breaking == null;
		if (!context.containsAll(decider.mLoops)) {
			work.set(decider.mLoops);
			work.removeAll(context);
			FlowVertex<B,V> loop = getGraph().getVertex(work.firstInt());
			context.add(loop.mOrderIndex);
			A.Node value
					= decide(apeg, decider, targets, breaking, context, work);
			A.Node breakCondition
					= getBreakCondition(apeg, loop, targets, context, work);
			A.Node pass = apeg.getPass(loop.mLoops.size(), breakCondition);
			A.Node eval = apeg.getEval(loop.mLoops.size(), value, pass);
			A.Node loopOutput = unique
					? apeg.getCheckUnmodified(loop.mLoopUnmodified, value, eval)
					: eval;
			context.remove(loop.mOrderIndex);
			return loopOutput;
		}
		if (unique)
			return apeg.getOutput(targets.iterator().next().mBlock);
		A.Node condition = apeg.getBranchCondition(decider.mBlock);
		int trueIndex = decider.getChild(0).mOrderIndex;
		int falseIndex = decider.getChild(1).mOrderIndex;
		Set<FlowVertex<B,V>> trueSide = new HashSet<FlowVertex<B,V>>();
		Set<FlowVertex<B,V>> falseSide = new HashSet<FlowVertex<B,V>>();
		for (FlowVertex<B,V> target : targets) {
			if (target.mAncestors.contains(trueIndex))
				trueSide.add(target);
			if (target.mAncestors.contains(falseIndex))
				falseSide.add(target);
		}
		A.Node trueValue, falseValue;
		if (trueSide.isEmpty())
			trueValue = decide(apeg, decider, Collections.singleton(decider),
					breaking, context, work);
		else
			trueValue = decide(apeg, decider.getChild(0), trueSide,
					breaking, context, work);
		if (falseSide.isEmpty())
			falseValue = decide(apeg, decider, Collections.singleton(decider),
					breaking, context, work);
		else
			falseValue = decide(apeg, decider.getChild(1), falseSide,
					breaking, context, work);
		return apeg.getPhiEquivalent(condition, trueValue, falseValue);
	}
	
	private <G, L, P, A extends APEG<G,B,V,L,P>> A.Node
			getBreakCondition(A apeg,
			FlowVertex<B,V> loop,
			Collection<? extends FlowVertex<B,V>> breaks,
			BitIntSet context, BitIntSet work) {
		Set<FlowVertex<B,V>> options;
		if (getGraph().usingSimpleBreaks()) {
			options = new HashSet<FlowVertex<B,V>>();
			for (FlowVertex<B,V> vertex : getGraph().getVertices())
				if (vertex.mLoops.contains(loop.mOrderIndex))
					for (FlowVertex<B,V> child : vertex.getChildren())
						if (!child.mLoops.contains(loop.mOrderIndex))
							options.add(child);
		} else
			options = new HashSet<FlowVertex<B,V>>(breaks);
		options.add(loop.mLoop);
		A.Node result
				= decide(apeg, loop, options, loop.mLoop, context, work);
		return result;
	}
	
	public String toString() {
		return (mBlock == null ? mLoop.mBlock.toString() + '\''
				: mBlock.toString()) + "@" + mOrderIndex + ": " + mLoops;
	}
	
	public int compareTo(FlowVertex<B,V> that) {
		return mOrderIndex - that.mOrderIndex;
	}
}
