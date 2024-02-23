package peggy.optimize;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import peggy.analysis.StackMap;
import peggy.pb.CostModel;
import peggy.represent.PEGInfo;
import peggy.represent.StickyPredicate;
import peggy.revert.ReversionHeuristic;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This class implements a greedy PEG reversion heuristic.
 * At each point where there is a choice to be made for the
 * term within a value, the terms are sorted by cost 
 * (according to the given cost model) and we attempt to use
 * each term in ordering of increasing cost. 
 * By 'attempt' we mean that the term is used unless there is
 * no possible way to use it and still form a valid PEG.
 * 
 * Essentially, this class just restricts the PEG to be valid,
 * while trying to choose the cheapest term from each value at
 * each step.
 *  
 * This class is designed for speed rather than PEG cost optimality.
 */
public abstract class GreedyReversionHeuristic<O,P,R,N extends Number>
implements ReversionHeuristic<O,P,R,N> {
	public Map<? extends CPEGValue<O,P>, ? extends CPEGTerm<O,P>> 
	chooseReversionNodes(
			CPeggyAxiomEngine<O,P> engine, 
			PEGInfo<O,P,R> original, 
			Map<? extends Vertex<FlowValue<P,O>>, ? extends CPEGTerm<O,P>> originalMap) {
		
		List<CPEGValue<O,P>> topLevelChildValues = 
			new ArrayList<CPEGValue<O,P>>();
		for (R arr : original.getReturns()) {
			topLevelChildValues.add(originalMap.get(
					original.getReturnVertex(arr)).getValue());
		}
		
		StackMap<CPEGValue<O,P>,CPEGTerm<O,P>> value2term = 
			new StackMap<CPEGValue<O,P>,CPEGTerm<O,P>>();
		Stack<CPEGValue<O,P>> ancestors = new Stack<CPEGValue<O,P>>();
		Stack<Integer> childIndexes = new Stack<Integer>();
		
		if (greedyChildren(
				ancestors, childIndexes, 
				value2term, null, 
				topLevelChildValues, 0)) {
			
			Map<CPEGValue<O,P>,CPEGTerm<O,P>> result = 
				new HashMap<CPEGValue<O,P>,CPEGTerm<O,P>>();
			for (CPEGValue<O,P> value : value2term.keySet()) {
				result.put(value, value2term.get(value));
			}
			return result;
		} else {
			return null;
		}
	}
	

	private void toDot(
			PrintStream out, 
			StackMap<CPEGValue<O,P>,CPEGTerm<O,P>> value2term,
			PEGInfo<O,P,R> original,
			Map<? extends Vertex<FlowValue<P,O>>, ? extends CPEGTerm<O,P>> originalMap) {

		out.println("digraph OPTPEG {");
		for (CPEGValue<O,P> value : value2term.keySet()) {
			CPEGTerm<O,P> term = value2term.get(value);
			String termStr = "term" + term.hashCode();
			out.println("  value" + value.hashCode() + " -> " + termStr + ";");
			out.println("  " + termStr + " [label=\"" + term.getOp().toString() + "\"];");
			for (int j = 0; j < term.getArity(); j++) {
				out.println("  " + termStr + " -> value" + term.getChild(j).getValue().hashCode() + ";");
			}
		}
		for (R arr : original.getReturns()) {
			out.println("  " + arr + " -> value" + originalMap.get(original.getReturnVertex(arr)).getValue().hashCode() + ";");
		}
		
		out.println("}");
	}

	
	
	/**
	 * Constraints:
	 * 	- every node must have all its children
	 * 	- no value may use more than one term from that value
	 * 	- every loop must go through a theta on child 1
	 * 	- theta_i must be invariant beyond i (maxvariance <= i)
	 * 	- eval_i/pass_i must have 0th child be invariant beyond i
	 * 	- eval_i must have pass_i as 1st child
	 */
	/** 
	 * Ancestor and childIndexes stacks do NOT include 
	 * the parentTerm and index.
	 */
	private boolean greedyChildren(
			Stack<CPEGValue<O,P>> ancestors,
			Stack<Integer> childIndexes,
			StackMap<CPEGValue<O,P>,CPEGTerm<O,P>> value2term,
			CPEGTerm<O,P> parentTerm, // may be null!
			List<CPEGValue<O,P>> childValues,
			int index) {
		if (index >= childValues.size()) {
			// base case, return true
			return true;
		}
		
		CPEGValue<O,P> childValue = childValues.get(index);
		
		if (parentTerm != null && 
		    parentTerm.getValue().equals(childValue)) {
			// parent and child should never have same value!
			return false;
		} else if (ancestors.contains(childValue)) {
			// check for loops, and verify parent-child requirements
			ancestors.push(parentTerm.getValue()); // temporary push
			childIndexes.push(index);
			boolean result = checkLoops(
					ancestors, childIndexes, 
					value2term, childValue);
			ancestors.pop();
			childIndexes.pop();
			
			if (result && checkChildRequirements(
					parentTerm, index, 
					value2term.get(childValue))) {
				// recurse on next child!
				return greedyChildren(
						ancestors, childIndexes, 
						value2term, parentTerm, 
						childValues, index+1);
			} else {
				return false;
			}
		} else if (value2term.containsKey(childValue)) {
			// seen, but not along this path
			CPEGTerm<O,P> childTerm = value2term.get(childValue);
			// check parent-child restrictions
			if (parentTerm == null ||
				checkChildRequirements(parentTerm, index, childTerm)) {
				// recurse!
				return greedyChildren(
						ancestors, childIndexes,
						value2term, parentTerm,
						childValues, index+1);
			}
		}
		// value not yet seen!
		
		// sort the terms in order of cost
		List<CPEGTerm<O,P>> sortedTerms = 
			new ArrayList<CPEGTerm<O,P>>(childValue.getTerms());
		Collections.sort(sortedTerms, getCostComparator());
		
		// in order from least to most cost, try to use each term
		final int ancestorHeight = ancestors.size();
		final int mapHeight = value2term.getHeight();
		for (int i = 0; i < sortedTerms.size(); i++) {
			// reset the stacks
			popToHeight(ancestors, ancestorHeight);
			popToHeight(childIndexes, ancestorHeight);
			value2term.popToHeight(mapHeight);

			CPEGTerm<O,P> potentialChildTerm = sortedTerms.get(i);
			
			if (!checkNodeRequirements(potentialChildTerm))
				continue;
			
			// check the child requirements of the immediate parent
			if (parentTerm != null &&
				!checkChildRequirements(
						parentTerm,
						index,
						potentialChildTerm))
				continue;

			// recurse!
			if (parentTerm != null) {
				ancestors.push(parentTerm.getValue());
				childIndexes.push(index);
			}
			value2term.push(childValue, potentialChildTerm);
			
			List<CPEGValue<O,P>> grandChildValues = 
				new ArrayList<CPEGValue<O,P>>();
			for (int j = 0; j < potentialChildTerm.getArity(); j++) {
				grandChildValues.add(potentialChildTerm.getChild(j).getValue());
			}
			// TODO cache these (and compute them lazily)

			// recurse on the grandchild values
			if (greedyChildren(
					ancestors, 
					childIndexes,
					value2term,
					potentialChildTerm,
					grandChildValues,
					0)) {
				popToHeight(ancestors, ancestorHeight);
				popToHeight(childIndexes, ancestorHeight);
				// if successful, recurse on the other children of parentTerm
				if (greedyChildren(
						ancestors, 
						childIndexes, 
						value2term, 
						parentTerm,
						childValues,
						index+1)) {
					// success! mappings should already be in value2term
					return true;
				}
			}
						
			// if we made it here, didn't work!
			continue;
		}

		// might not be necessary
		popToHeight(ancestors, ancestorHeight);
		popToHeight(childIndexes, ancestorHeight);
		value2term.popToHeight(mapHeight);
		
		return false;
	}

	/**
	 * Used for sorting the terms by cost 
	 */
	private Comparator<CPEGTerm<O,P>> getCostComparator() {
		final CostModel<CPEGTerm<O,P>,N> costmodel = getCostModel();
		return new Comparator<CPEGTerm<O,P>>() {
			public int compare(CPEGTerm<O,P> left, CPEGTerm<O,P> right) {
				int costL = costmodel.cost(left).intValue();
				int costR = costmodel.cost(right).intValue();
				if (costL == costR) {
					return left.getArity() - right.getArity();
				} else {
					return (int)(costL-costR);
				}
			}
		};
	}

	/**
	 * Returns boolean stating whether or not the
	 * given parent node can have the given child node
	 * along the given child index.
	 */
	private boolean checkChildRequirements(
			CPEGTerm<O,P> parent,
			int childIndex,
			CPEGTerm<O,P> child) {
		StickyPredicate<CPEGTerm<O,?>> sticky = getStickyPredicate();
		if (parent.getOp().isEval()) {
			int i = parent.getOp().getLoopDepth();
			if (childIndex == 0) {
				// must be invariant beyond i (maxvariance <= i)
				return (child.getValue().getMaxVariance() <= i);
			} else {
				// must be pass_i
				return child.getOp().isPass() && 
					child.getOp().getLoopDepth() == i;
			}
		} else if (parent.getOp().isPass()) {
			// must be invariant beyond i
			int i = parent.getOp().getLoopDepth();
			return (child.getValue().getMaxVariance() <= i);
		} else if (sticky.isSticky(parent, childIndex)) {
			return sticky.allowsChild(parent, childIndex, child);
		} else 
			return true;
	}
	
	/** 
	 * All loops must go through the 1st child of a theta node.
	 */
	private boolean checkLoops(
			Stack<CPEGValue<O,P>> ancestors,
			Stack<Integer> childIndexes,
			StackMap<CPEGValue<O,P>,CPEGTerm<O,P>> value2term,
			CPEGValue<O,P> newvalue) {
		CPEGTerm<O,P> loopTerm = value2term.get(newvalue);
		for (int i = ancestors.size()-1; i >= 0; i--) {
			CPEGTerm<O,P> ancestorTerm = value2term.get(ancestors.get(i));
			if (ancestorTerm.equals(loopTerm))
				return false;
			if (ancestorTerm.getOp().isTheta() && childIndexes.get(i) == 1)
				return true;
		}
		return false;
	}
	
	private boolean checkNodeRequirements(CPEGTerm<O,P> node) {
		if (node.getOp().isTheta()) {
			int i = node.getOp().getLoopDepth();
			return (node.getValue().getMaxVariance() <= i);
		} else if (node.getOp().isDomain()) {
			return isRevertible(node.getOp().getDomain());
		} else {
			return true;
		}
	}
	
	private static void popToHeight(Stack<?> stack, int height) {
		if (stack.size() < height)
			throw new IllegalArgumentException(
					"Stack height is less than parameter: " + height);
		while (stack.size() > height)
			stack.pop();
	}

	protected abstract StickyPredicate<CPEGTerm<O,?>> getStickyPredicate();
	public abstract CostModel<CPEGTerm<O,P>,N> getCostModel();
	protected abstract boolean isRevertible(O domain);
}
