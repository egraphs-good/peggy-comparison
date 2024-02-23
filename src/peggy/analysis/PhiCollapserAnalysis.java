package peggy.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import peggy.represent.MutablePEG;
import peggy.represent.PEG;
import peggy.represent.PEGInfo;
import util.NamedTag;
import eqsat.FlowValue;

/**
 * This analysis is run periodically on the EPEG to collapse phi nodes
 * that are under others that have the same condition.
 */
public abstract class PhiCollapserAnalysis<L,P,R> {
	private static final NamedTag<Integer> invarianceTag = 
		new NamedTag<Integer>("Invariance");
	
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("PhiCollapserAnalysis: " + message);
	}
	
	protected int threshold;
	
	public PhiCollapserAnalysis(int _threshold) {
		this.threshold = _threshold;
	}
	
	/**
	 * This method collapses phis on a PEG.
	 */
	public PEGInfo<L,P,R> collapsePhis(PEGInfo<L,P,R> peg) {
		long start = System.currentTimeMillis();
		
		final MutablePEG<FlowValue<P,L>,R> mutable = MutablePEG.<L,P,R>fromPEGInfo(peg);
		PEGInvarianceTagger<L,P,R> tagger = new PEGInvarianceTagger<L, P, R>();
		tagger.tagInvariance(invarianceTag, mutable);
		
		// find all phis beforehand
		LinkedList<MutablePEG<FlowValue<P,L>,R>.MutableVertex> phis = 
			new LinkedList<MutablePEG<FlowValue<P,L>,R>.MutableVertex>();
		for (MutablePEG<FlowValue<P,L>,R>.MutableVertex vertex : mutable.getVertices()) {
			if (vertex.getLabel().isPhi())
				phis.add(vertex);
		}
		
		while (phis.size() > 0) {
			final MutablePEG<FlowValue<P,L>,R>.MutableVertex phi = phis.removeFirst();
			final MutablePEG<FlowValue<P,L>,R>.MutableVertex oldTrue = phi.getChild(1);			
			final MutablePEG<FlowValue<P,L>,R>.MutableVertex oldFalse = phi.getChild(2);
			
			if (phi.getParents().size() == 0)
				continue;
			
			LinkedList<MutablePEG<FlowValue<P,L>,R>.MutableVertex> childList = 
				new LinkedList<MutablePEG<FlowValue<P,L>,R>.MutableVertex>();
			childList.addLast(phi);

			MutablePEG<FlowValue<P,L>,R>.MutableVertex newTrue = 
				replacePhis(
						0,
						phi.getChild(0), 
						true,
						phi.getTag(invarianceTag),
						oldTrue,
						childList,
						new HashMap<MutablePEG<FlowValue<P,L>,R>.MutableVertex,MutablePEG<FlowValue<P,L>,R>.MutableVertex>(),
						phis,
						tagger);

			childList.clear();
			childList.addLast(phi);

			MutablePEG<FlowValue<P,L>,R>.MutableVertex newFalse = 
				replacePhis(
						0,
						phi.getChild(0), 
						false,
						phi.getTag(invarianceTag),
						oldFalse,
						childList,
						new HashMap<MutablePEG<FlowValue<P,L>,R>.MutableVertex,MutablePEG<FlowValue<P,L>,R>.MutableVertex>(),
						phis,
						tagger);

			boolean changed = (newTrue != oldTrue) || (newFalse != oldFalse);
			if (changed) {
				phi.removeChild(2);
				phi.removeChild(1);
				phi.addChild(newTrue);
				phi.addChild(newFalse);
			}
		}
		
		PEGInfo<L,P,R> result = 
			PEG.<L,P,R,MutablePEG<FlowValue<P,L>,R>,MutablePEG<FlowValue<P,L>,R>.MutableVertex>toPEGInfo(mutable);
		
		long end = System.currentTimeMillis();
		debug("collapse time = " + (end-start));
		
		return result;
	}
	
	/**
	 * Returns true if invariance1 "varies less than" invariance2.
	 * i.e. if the greatest variance of 1 is less than that of 2. 
	 */
	public static boolean variesLess(int invariance1, int invariance2) {
		int max1 = 0, max2 = 0;
		for (int i = 0; i < 32; i++) {
			if ((invariance1&(1<<i)) == 0)
				max1 = i;
			if ((invariance2&(1<<i)) == 0)
				max2 = i;
		}
		return max1 < max2;
	}
	
	
	/**
	 * The toplevel call to this method starts when we have found the top phi and are looking for inner phis
	 */
	protected MutablePEG<FlowValue<P,L>,R>.MutableVertex replacePhis(
		int depth,
		MutablePEG<FlowValue<P,L>,R>.MutableVertex condition,
		boolean which,
		int invariance,
		MutablePEG<FlowValue<P,L>,R>.MutableVertex current,
		LinkedList<MutablePEG<FlowValue<P,L>,R>.MutableVertex> childList,
		Map<MutablePEG<FlowValue<P,L>,R>.MutableVertex,MutablePEG<FlowValue<P,L>,R>.MutableVertex> cache,
		LinkedList<MutablePEG<FlowValue<P,L>,R>.MutableVertex> phis,			
		PEGInvarianceTagger<L,P,R> tagger) {
		
		if (depth >= threshold)
			return current;
		if (childList.contains(current))
			return current;
		if (variesLess(current.getTag(invarianceTag), invariance))
			return current;
		if (current.getLabel().isTheta() &&
			(invariance&(1<<current.getLabel().getLoopDepth())) == 0) {
			// can't pass through theta in which I vary
			return current;
		}
		if (cache.containsKey(current))
			return cache.get(current);
		
		if (current.getLabel().isPhi() && current.getChild(0).equals(condition)) {
			// found one! replace!
			MutablePEG<FlowValue<P,L>,R>.MutableVertex result = 
				which ? current.getChild(1) : current.getChild(2);
				
			debug("Collapsed a phi: " + which);
			
			result = replacePhis(
					depth,
					condition, 
					which, 
					invariance, 
					result, 
					childList, 
					cache, 
					phis,
					tagger);

			if (!cache.containsKey(current))
				cache.put(current, result);
			return result;
		} else if (isDomainPhi(condition, current)) {
			return handleDomainPhi(condition, which, invariance, current, childList, cache, phis, tagger);
		} else {
			List<MutablePEG<FlowValue<P,L>,R>.MutableVertex> newchildren = 
				new ArrayList<MutablePEG<FlowValue<P,L>,R>.MutableVertex>();
			boolean anynew = false;
			
			childList.addLast(current);
			for (int i = 0; i < current.getChildCount(); i++) {
				MutablePEG<FlowValue<P,L>,R>.MutableVertex newchild = 
					replacePhis(
							depth+1,
							condition,
							which,
							invariance,
							current.getChild(i),
							childList,
							cache,
							phis,
							tagger);
				
				if (newchild != current.getChild(i))
					anynew = true;
				newchildren.add(newchild);
			}
			childList.removeLast();
			
			if (anynew) {
				// must return new nodes
				MutablePEG<FlowValue<P,L>,R>.MutableVertex result = 
					current.getPEG().new MutableVertex(current.getLabel());
				for (MutablePEG<FlowValue<P,L>,R>.MutableVertex child : newchildren)
					result.addChild(child);
				
				tagger.updateInvariance(invarianceTag, result);
				
				if (current.getLabel().isPhi())
					phis.addLast(result);
				
				cache.put(current, result);
				return result;
			} else {
				cache.put(current, current);
				return current;
			}
		}
	}
	
	protected abstract boolean isDomainPhi(
			MutablePEG<FlowValue<P,L>,R>.MutableVertex condition,
			MutablePEG<FlowValue<P,L>,R>.MutableVertex current);
	protected abstract MutablePEG<FlowValue<P,L>,R>.MutableVertex 
	handleDomainPhi(
			MutablePEG<FlowValue<P,L>,R>.MutableVertex condition, 
			boolean which, 
			int invariance, 
			MutablePEG<FlowValue<P,L>,R>.MutableVertex current, 
			LinkedList<MutablePEG<FlowValue<P,L>,R>.MutableVertex> childList,
			Map<MutablePEG<FlowValue<P,L>,R>.MutableVertex,MutablePEG<FlowValue<P,L>,R>.MutableVertex> cache,
			LinkedList<MutablePEG<FlowValue<P,L>,R>.MutableVertex> phis,			
			PEGInvarianceTagger<L,P,R> tagger);
}
