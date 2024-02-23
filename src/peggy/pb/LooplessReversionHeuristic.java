package peggy.pb;

import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
public abstract class LooplessReversionHeuristic<L,P,R>
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
		
		valueloop:
		for (CPEGValue<L,P> value : engine.getEGraph().getValueManager().getValues()) {
			info.value2index.add(value);
			
			for (CPEGTerm<L,P> term : value.getTerms()) {
				if (term.getArity() == 0 && this.isRevertible(term.getOp())) {
					// use!
					info.result.put(value, term);
					info.value2mincost.put(value, 0);
					BitSet set = new BitSet();
					set.set(info.value2index.getIndex(value));
					info.value2termset.put(value, set);
					continue valueloop;
				}
			}
		}
		debug("Set initial constants");

		LinkedList<CPEGValue<L,P>> path = new LinkedList<CPEGValue<L,P>>();
		for (R arr : original.getReturns()) {
			CPEGValue<L,P> rootvalue = 
				originalMap.get(original.getReturnVertex(arr)).getValue();
			bottomUpHelper(info, path, rootvalue);
			debug("Computed root " + arr);
			if (!info.result.containsKey(rootvalue)) {
				debug("Root " + arr + " not completed!");
				throw new RuntimeException("Root value not computed: " + arr);
			}
		}
		debug("All roots completed!");
		
		return info.result;
	}
	
	/**
	 * Container for parameters that will be passed recursively
	 * in the bottomUpHelper method.
	 */
	class Info {
		// will change throughout the algorithm
		final Map<CPEGValue<L,P>,CPEGTerm<L,P>> result = 
			new HashMap<CPEGValue<L,P>,CPEGTerm<L,P>>();
		final Map<CPEGValue<L,P>,Integer> value2mincost = 
			new HashMap<CPEGValue<L,P>,Integer>();
		final Map<CPEGValue<L,P>,BitSet> value2termset = 
			new HashMap<CPEGValue<L,P>,BitSet>();

		// should not change once initialized
		final CostModel<CPEGTerm<L,P>,Integer> costmodel;
		final HashList<CPEGValue<L,P>> value2index = 
			new HashList<CPEGValue<L,P>>();
		
		Info(CostModel<CPEGTerm<L,P>,Integer> _costmodel) {
			this.costmodel = _costmodel;
		}
	}

	/**
	 * Uses a bottom-up approach to assign a cost to every value
	 * in the EPEG. Assumes that the EPEG has no loop operators,
	 * and hence the result PEG should be a DAG.
	 * 
	 * Each value should be processed exactly once.
	 */
	private void bottomUpHelper(
			Info info,
			LinkedList<CPEGValue<L,P>> path,
			CPEGValue<L,P> current) {
		if (path.contains(current)) return;
		if (info.result.containsKey(current)) return;
		
		int bestcost = 0;
		CPEGTerm<L,P> bestterm = null;
		BitSet bestset = null;
		path.addLast(current);
		termloop:
		for (CPEGTerm<L,P> term : current.getTerms()) {
			if (!this.isRevertible(term.getOp()))
				continue;

			BitSet set = new BitSet();
			for (int i = 0; i < term.getArity(); i++) {
				CPEGValue<L,P> childvalue = term.getChild(i).getValue();
				bottomUpHelper(info, path, childvalue);
				if (info.result.containsKey(childvalue)) {
					set.or(info.value2termset.get(childvalue));
				} else {
					continue termloop;
				}
			}
			
			int cost = info.costmodel.cost(term);
			for (int i = 0; i < set.size(); i++) {
				if (set.get(i))
					cost += info.costmodel.cost(info.result.get(info.value2index.getValue(i)));
			}
			
			if (bestterm == null || cost < bestcost) {
				bestterm = term;
				bestcost = cost;
				set.set(info.value2index.getIndex(current));
				bestset = set;
			}
		}
		path.removeLast();
		
		if (bestterm != null) {
			info.result.put(current, bestterm);
			info.value2mincost.put(current, bestcost);
			info.value2termset.put(current, bestset);
		}
	}
}
