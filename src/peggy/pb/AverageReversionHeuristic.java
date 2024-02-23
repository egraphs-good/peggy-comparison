package peggy.pb;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import llvm.bitcode.HashList;
import peggy.analysis.StackMap;
import peggy.represent.PEGInfo;
import peggy.represent.StickyPredicate;
import peggy.revert.AbstractReversionHeuristic;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This reversion heuristic chooses nodes by picking the average-cost term
 * in each value. 
 */
public abstract class AverageReversionHeuristic<L,P,R> 
extends AbstractReversionHeuristic<L,P,R,Integer> {
	private static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("AverageReversionHeuristic: " + message);
	}

	/**
	 * Returns true iff the given label is revertible.
	 */
	protected abstract boolean isRevertible(FlowValue<P,L> flow);

	/** 
	 * Returns the sticky predicate for this label type. 
	 */
	protected abstract StickyPredicate<FlowValue<P,L>> getStickyPredicate();

	public Map<? extends CPEGValue<L, P>, ? extends CPEGTerm<L, P>> chooseReversionNodes(
			CPeggyAxiomEngine<L, P> engine,
			PEGInfo<L, P, R> original,
			Map<? extends Vertex<FlowValue<P, L>>, ? extends CPEGTerm<L, P>> originalMap) {

		// assign each term an index
		HashList<CPEGTerm<L,P>> term2index = new HashList<CPEGTerm<L,P>>();
		for (CPEGValue<L,P> value : engine.getEGraph().getValueManager().getValues()) {
			for (CPEGTerm<L,P> term : value.getTerms()) {
				term2index.add(term);
			}
		}

		// give each value its set of reachable terms
		Map<CPEGValue<L,P>,BitSet> value2termset = 
			new HashMap<CPEGValue<L,P>,BitSet>();
		buildTermSets(engine, term2index, value2termset);
		
		// map each term to its set of terms
		Map<CPEGTerm<L,P>,BitSet> term2termset = 
			new HashMap<CPEGTerm<L,P>,BitSet>();
		for (CPEGValue<L,P> value : engine.getEGraph().getValueManager().getValues()) {
			for (CPEGTerm<L,P> term : value.getTerms()) {
				BitSet mybits = new BitSet();
				for (int i = 0; i < term.getArity(); i++) {
					mybits.or(value2termset.get(term.getChild(i).getValue()));
				}
				mybits.set(term2index.getIndex(term));
				term2termset.put(term, mybits);
			}
		}

		// now choose terms based on average costs
		Info info = new Info();
		info.costmodel = this.getCostModel();
		info.currentChosen = new BitSet();
		//info.value2termset = value2termset;
		info.term2termset = term2termset;
		info.term2index = term2index;
		info.result = new StackMap<CPEGValue<L,P>,CPEGTerm<L,P>>();
		info.path = new LinkedList<TermIndex>();

		Continuation finalCont = new Continuation() {
			public boolean continuate(Info info) {return true;}
		};
		
		// compute the roots
		for (R arr : original.getReturns()) {
			CPEGValue<L,P> rootvalue = 
				originalMap.get(original.getReturnVertex(arr)).getValue();
			finalCont = new RootChildContinuation(rootvalue, finalCont);
		}
		
		if (!finalCont.continuate(info)) {
			throw new RuntimeException("Cannot find root values");
		}
		
		// reformat into a map
		Map<CPEGValue<L,P>,CPEGTerm<L,P>> result = 
			new HashMap<CPEGValue<L,P>,CPEGTerm<L,P>> ();
		for (CPEGValue<L,P> key : info.result.keySet()) {
			result.put(key, info.result.get(key));
		}
		return result;
	}
	
	class Info {
		CostModel<CPEGTerm<L,P>,Integer> costmodel;
		HashList<CPEGTerm<L,P>> term2index;
		//Map<CPEGValue<L,P>,BitSet> value2termset;
		Map<CPEGTerm<L,P>,BitSet> term2termset;

		BitSet currentChosen;
		StackMap<CPEGValue<L,P>,CPEGTerm<L,P>> result;
		LinkedList<TermIndex> path;
	}
	
	class TermIndex {
		CPEGTerm<L,P> term;
		int index;
		TermIndex(CPEGTerm<L,P> _term, int _index) {
			this.term = _term;
			this.index = _index;
		}
	}
	class TermChoice implements Comparable<TermChoice> {
		CPEGTerm<L,P> term;
		int cost;
		BitSet bits;
		public int compareTo(TermChoice arg0) {
			return this.cost - arg0.cost;
		}
	}
	private abstract class Continuation {
		public abstract boolean continuate(Info info);
	}
	private class ChildContinuation extends Continuation {
		protected final Continuation after;
		protected final CPEGTerm<L,P> parent;
		protected final int childIndex;
		public ChildContinuation(
				Continuation _after,
				CPEGTerm<L,P> _parent,
				int _childIndex) {
			this.after = _after;
			this.childIndex = _childIndex;
			this.parent = _parent;
		}
		public boolean continuate(Info info) {
			final int oldPathLength = info.path.size();
			info.path.addLast(new TermIndex(this.parent, this.childIndex));
			if (chooseTerm2(
					this.parent.getChild(this.childIndex).getValue(), 
					info, 
					this.after)) {
				while (info.path.size()>oldPathLength)
					info.path.removeLast();
				return true;
			} else {
				return false;
			}
		}
	}
	private class RootChildContinuation extends Continuation {
		protected final CPEGValue<L,P> rootValue;
		protected final Continuation after;
		public RootChildContinuation(
				CPEGValue<L,P> _rootValue,
				Continuation _after) {
			this.after = _after;
			this.rootValue = _rootValue;
		}
		public boolean continuate(Info info) {
			return chooseTerm2(this.rootValue, info, this.after);
		}
	}
	
	private Boolean checkBaseCases(CPEGValue<L,P> value, Info info) {
		if (info.result.containsKey(value)) {
			// already done, check for validity
			CPEGTerm<L,P> myterm = info.result.get(value);
			
			// check if this term is in the current path
			int pathindex = info.path.size()-1;
			for (Iterator<TermIndex> iter = info.path.descendingIterator(); iter.hasNext(); ) {
				TermIndex next = iter.next();
				if (next.term.equals(myterm))
					break;
				pathindex--;
			}

			// if in current path...
			if (pathindex >= 0) {
				// only allow if theta second child
				TermIndex first = info.path.get(pathindex);
				if (first.term.getOp().isTheta() && first.index==1) {
					// OK!
					return true;
				} else {
					// NOT OK!
					return false;
				}
			} else {
				// not in current path, return cached result
				return true;
			}
		}
		
		return null;
	}
	
	private boolean chooseTerm2(
			CPEGValue<L,P> value,
			Info info,
			Continuation cont) {
		Boolean bc = this.checkBaseCases(value, info);
		if (bc!=null) {
			if (bc) return cont.continuate(info);
			else return false;
		}
		
		// try my choices
		final int oldHeight = info.result.getHeight();
		final int oldPathLength = info.path.size();
		BitSet oldChosen = info.currentChosen;
		for (Iterator<TermChoice> choiceIter = getTermChoices(info, value); choiceIter.hasNext(); ) {
			TermChoice choice = choiceIter.next();
			
			info.result.push(value, choice.term);
			info.currentChosen = new BitSet();
			info.currentChosen.or(oldChosen);
			info.currentChosen.set(info.term2index.getIndex(choice.term));
			
			final int arity = choice.term.getArity();
		
			Continuation mycont = cont;
			for (int i = arity-1; i>=0; i--)
				mycont = new ChildContinuation(mycont, choice.term, i);
			// degenerates to cont if arity=0
			
			if (mycont.continuate(info)) {
				return true;
			} else {
				info.result.popToHeight(oldHeight);
				while (info.path.size()>oldPathLength)
					info.path.removeLast();
				continue;
			}
		}

		return false;
	}
	
	
	/**
	 * Returns an Iterator of all the TermChoices for the given CPEGValue,
	 * in order by term cost.
	 */
	private Iterator<TermChoice> getTermChoices(Info info, CPEGValue<L,P> value) {
		List<TermChoice> choices = 
			new ArrayList<TermChoice>(value.getTerms().size());

		final StickyPredicate<FlowValue<P,L>> sticky = this.getStickyPredicate();
		FlowValue<P,L> parentOp = null;
		int childIndex = 0;
		boolean isSticky = false;
		if (info.path.size()>0) {
			parentOp = info.path.getLast().term.getOp();
			childIndex = info.path.getLast().index;
			isSticky = sticky.isSticky(parentOp, childIndex); 
		}
		
		for (CPEGTerm<L,P> term : value.getTerms()) {
			if (!isRevertible(term.getOp())) continue;
//			if (isSticky && 
//				!sticky.allowsChild(parentOp, childIndex, term.getOp())) 
//				continue;
			
			BitSet mybits = new BitSet();
			mybits.or(info.term2termset.get(term));
			mybits.or(info.currentChosen);
			
			int mycost = 0;
			for (int i = 0; i < mybits.length(); i++) {
				if (mybits.get(i))
					mycost += info.costmodel.cost(info.term2index.getValue(i));
			}
			
			TermChoice mychoice = new TermChoice();
			mychoice.term = term;
			mychoice.cost = mycost;
			mychoice.bits = mybits;
			choices.add(mychoice);
		}
		Collections.sort(choices);
		return choices.iterator();
	}
	

	/**
	 * Associates a bitset with each value, in value2termset. 
	 * The mapping (v:b) means that b is the set of all terms that are 
	 * descendants of some term in v. 
	 */
	private void buildTermSets(
			CPeggyAxiomEngine<L,P> engine,
			HashList<CPEGTerm<L,P>> indexes,
			Map<CPEGValue<L,P>,BitSet> value2termset) {
		
		Map<CPEGValue<L,P>,BitSet> initmap = new HashMap<CPEGValue<L,P>,BitSet>();
		for (CPEGValue<L,P> value : engine.getEGraph().getValueManager().getValues()) {
			BitSet mybits = new BitSet();
			for (CPEGTerm<L,P> term : value.getTerms()) {
				mybits.set(indexes.getIndex(term));
			}
			initmap.put(value, mybits);
		}
		
		for (CPEGValue<L,P> value : engine.getEGraph().getValueManager().getValues()) {
			Set<CPEGValue<L,P>> seen = new HashSet<CPEGValue<L,P>>();
			LinkedList<CPEGValue<L,P>> worklist = 
				new LinkedList<CPEGValue<L,P>>();
			worklist.addLast(value);
			
			while (worklist.size() > 0) {
				CPEGValue<L,P> next = worklist.removeFirst();
				if (seen.contains(next))
					continue;
				seen.add(next);
				for (CPEGTerm<L,P> term : next.getTerms()) {
					for (int i = 0; i < term.getArity(); i++) {
						worklist.addLast(term.getChild(i).getValue());
					}
				}
			}
			
			BitSet mybits = initmap.get(value);
			for (CPEGValue<L,P> v : seen) {
				mybits.or(initmap.get(v));
			}
			value2termset.put(value, mybits);
		}
	}
}