package peggy.pb;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import llvm.bitcode.HashList;
import peggy.represent.PEGInfo;
import peggy.revert.AbstractReversionHeuristic;
import peggy.revert.ReversionHeuristic;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This algorithm can be used when the EPEG contains no loop operators
 * (theta/eval/pass/shift/etc) and hence the resulting PEG must be a DAG.
 */
public abstract class LooplessReversionHeuristic2<L,P,R>
extends AbstractReversionHeuristic<L,P,R,Integer> {
	
	private static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("LooplessReversionHeuristic: " + message);
	}
	
	/**
	 * Returns the reversion heuristic to be used if the given
	 * EPEG has loop operators in it (and hence this
	 * algorithm cannot be used)
	 */
	protected abstract ReversionHeuristic<L,P,R,Integer> getFallbackHeuristic();

	/**
	 * Returns true iff the given label is revertible.
	 */
	protected abstract boolean isRevertible(FlowValue<P,L> flow);

	/**
	 * Returns true iff the given EPEG has any loop operators in it
	 * (theta/eval/shift/pass)
	 */
	private boolean hasLoops(CPeggyAxiomEngine<L, P> engine) {
		for (CPEGValue<L,P> value : engine.getEGraph().getValueManager().getValues()) {
			for (CPEGTerm<L,P> term : value.getTerms()) {
				FlowValue<P,L> flow = term.getOp();
				if (flow.isTheta() || flow.isEval() ||
					flow.isShift() || flow.isPass()) 
					return true;
			}
		}
		return false;
	}

	public Map<? extends CPEGValue<L, P>, ? extends CPEGTerm<L, P>> chooseReversionNodes(
			CPeggyAxiomEngine<L, P> engine,
			PEGInfo<L, P, R> original,
			Map<? extends Vertex<FlowValue<P, L>>, ? extends CPEGTerm<L, P>> originalMap) {
		if (this.hasLoops(engine)) {
			debug("EPEG has loops! using fallback");
			return this.getFallbackHeuristic().chooseReversionNodes(engine, original, originalMap);
		}

		// else!
		Info info = new Info(this.getCostModel());

		// give each term an index
		for (CPEGValue<L,P> value : engine.getEGraph().getValueManager().getValues()) {
			for (CPEGTerm<L,P> term : value.getTerms())
				info.term2index.add(term);	
		}
			
		// do the 0-arity constants first
		for (CPEGValue<L,P> value : engine.getEGraph().getValueManager().getValues()) {
			for (CPEGTerm<L,P> term : value.getTerms()) {
				if (term.getArity() == 0 && this.isRevertible(term.getOp())) {
					// use!
					info.term2mincost.put(term, 0);
					BitSet set = new BitSet();
					set.set(info.term2index.getIndex(term));
					info.term2termset.put(term, set);
				}
			}
		}
		debug("Set initial constants");

		// compute each term's best subPEG
		List<List<CPEGTerm<L,P>>> rootchildren = 
			new ArrayList<List<CPEGTerm<L,P>>>();
		for (R arr : original.getReturns()) {
			CPEGValue<L,P> rootvalue = 
				originalMap.get(original.getReturnVertex(arr)).getValue();
			bottomUpHelper(info, rootvalue);
			rootchildren.add(new ArrayList<CPEGTerm<L,P>>(rootvalue.getTerms()));
		}
		
		// now do the topmost selection
		Candidate best = new Candidate();
		findBestChildren(
				info,
				best, 
				null, 
				rootchildren,
				new Stack<CPEGTerm<L,P>>(),
				new BitSet(),
				0);
		
		debug("All roots completed!");
		
		// now convert the result into a Value->Term map
		Map<CPEGValue<L,P>,CPEGTerm<L,P>> result = 
			new HashMap<CPEGValue<L,P>,CPEGTerm<L,P>>();
		for (int i = 0; i < best.bestbits.length(); i++) {
			if (best.bestbits.get(i)) {
				CPEGTerm<L,P> term = info.term2index.getValue(i);
				result.put(term.getValue(), term);
			}
		}
		return result;
	}
	
	/**
	 * Container for parameters that will be passed recursively
	 * in the bottomUpHelper method.
	 */
	class Info {
		// will change throughout the algorithm
		final Map<CPEGTerm<L,P>,Integer> term2mincost = 
			new HashMap<CPEGTerm<L,P>,Integer>();
		final Map<CPEGTerm<L,P>,BitSet> term2termset = 
			new HashMap<CPEGTerm<L,P>,BitSet>();
		final Set<CPEGValue<L,P>> finishedValues = 
			new HashSet<CPEGValue<L,P>>();
		final LinkedList<CPEGValue<L,P>> path = 
			new LinkedList<CPEGValue<L,P>>();

		// should not change once initialized
		final CostModel<CPEGTerm<L,P>,Integer> costmodel;
		final HashList<CPEGTerm<L,P>> term2index = 
			new HashList<CPEGTerm<L,P>>();
		
		Info(CostModel<CPEGTerm<L,P>,Integer> _costmodel) {
			this.costmodel = _costmodel;
		}
	}

	
	private class Candidate {
		int bestcost;
		List<CPEGTerm<L,P>> bestchildren;
		BitSet bestbits;
	}
	
	
	/**
	 * Uses a bottom-up approach to assign a cost to every term
	 * in the EPEG. Assumes that the EPEG has no loop operators,
	 * and hence the result PEG should be a DAG.
	 * 
	 * Each term/value should be processed exactly once.
	 */
	private void bottomUpHelper(
			Info info,
			CPEGValue<L,P> current) {
		if (info.path.contains(current)) return; // loop!
		if (info.finishedValues.contains(current)) return; // already done
		
		info.path.addLast(current);
		
		for (CPEGTerm<L,P> term : current.getTerms()) {
			if (info.term2mincost.containsKey(term)) continue; // done already
			if (!isRevertible(term.getOp())) continue; // don't worry about irrevertibles
		
			List<List<CPEGTerm<L,P>>> children = 
				new ArrayList<List<CPEGTerm<L,P>>>(term.getArity());
			for (int i = 0; i < term.getArity(); i++) {
				CPEGValue<L,P> childValue = term.getChild(i).getValue();
				bottomUpHelper(info, childValue); // recursively solve the child's value
				children.add(new ArrayList<CPEGTerm<L,P>>());
				children.get(i).addAll(childValue.getTerms());
			}
			
			Candidate best = new Candidate();
			BitSet bits = new BitSet();
			bits.set(info.term2index.getIndex(term));
			findBestChildren(
					info, best, term, children, new Stack<CPEGTerm<L,P>>(), bits, 0);

			if (best.bestchildren == null)
				continue; // cannot assign subPEG to this term
				
			// update the info
			info.term2mincost.put(term, best.bestcost);
			info.term2termset.put(term, best.bestbits);
		}
		
		info.finishedValues.add(current);
		info.path.removeLast();
	}
	
	// recursively backtrack trying every combination of children
	private void findBestChildren(
			Info info,
			Candidate best, 
			CPEGTerm<L,P> parent,
			List<List<CPEGTerm<L,P>>> children,
			Stack<CPEGTerm<L,P>> choice,
			BitSet bits,
			int index) {
		if (index >= children.size()) {
			// got a working child set
			// compute the cost
			int cost = 0;
			for (int i = 0; i < bits.length(); i++) {
				if (bits.get(i))
					cost += info.costmodel.cost(info.term2index.getValue(i));
			}
			
			// compare against best
			if (best.bestchildren == null || cost < best.bestcost) {
				// new one is better
				best.bestcost = cost;
				best.bestbits = bits;
				best.bestchildren = new ArrayList<CPEGTerm<L,P>>(choice);
			}
			return;
		}
		
		
		// try out each child from the index-th value
		List<CPEGTerm<L,P>> childNodes = children.get(index);
		for (int i = 0; i < childNodes.size(); i++) {
			CPEGTerm<L,P> child = childNodes.get(i);
			if (!isRevertible(child.getOp())) continue; // irrevertible, no good
			if (!info.term2mincost.containsKey(child)) continue; // has no info, no good
			
			choice.push(child);
			BitSet newbits = (BitSet)bits.clone();
			newbits.or(info.term2termset.get(child));
			findBestChildren(info, best, parent, children, choice, newbits, index+1);
			choice.pop();
		}
		
		return;
	}
}
