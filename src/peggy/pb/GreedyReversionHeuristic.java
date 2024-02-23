package peggy.pb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import peggy.analysis.StackMap;
import peggy.represent.PEGInfo;
import peggy.revert.AbstractReversionHeuristic;
import util.graph.CRecursiveExpressionGraph.Vertex;
import util.pair.Pair;
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
public abstract class GreedyReversionHeuristic<O,P,R>
extends AbstractReversionHeuristic<O,P,R,Integer> {
	private static final boolean DEBUG = true;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("GreedyReversionHeuristic: " + message);
	}
	
	public Map<? extends CPEGValue<O,P>, ? extends CPEGTerm<O,P>> 
	chooseReversionNodes(
			CPeggyAxiomEngine<O,P> engine, 
			final PEGInfo<O,P,R> original, 
			final Map<? extends Vertex<FlowValue<P,O>>, ? extends CPEGTerm<O,P>> originalMap) {
		ValueMap<CPEGValue<O,P>,CPEGTerm<O,P>> inputMap = 
			new EPEGValueMap<O,P>(engine.getEGraph().getValueManager());

		// filter out unusables
		Set<CPEGTerm<O,P>> initialRemove = new HashSet<CPEGTerm<O,P>>();
		for (CPEGValue<O,P> value : engine.getEGraph().getValueManager().getValues()) {
			for (CPEGTerm<O,P> term : value.getTerms()) {
				if (!isUsable(term))
					initialRemove.add(term);
			}
		}
		
		final ValueMap<CPEGValue<O,P>,CPEGTerm<O,P>> filteredMap;
		if (initialRemove.size() > 0) {
			filteredMap = new RemoveValueMap<CPEGValue<O,P>,CPEGTerm<O,P>>(
					inputMap,
					initialRemove);
		} else {
			filteredMap = inputMap;
		}
		
		final List<R> orderedReturns = new ArrayList<R>(original.getReturns());
		
		// add the null value and null node to the graph
		ValueMap<CPEGValue<O,P>,CPEGTerm<O,P>> nullroot = 
			new ValueMap<CPEGValue<O,P>,CPEGTerm<O,P>>() {
				public boolean containsNode(CPEGTerm<O,P> node) {
					return (node == null) || filteredMap.containsNode(node);
				}
	
				public CPEGValue<O,P> getValue(CPEGTerm<O,P> node) {
					if (node == null) return null;
					else return filteredMap.getValue(node);
				}
				public int getArity(CPEGTerm<O,P> node) {
					if (node == null) return orderedReturns.size();
					else return filteredMap.getArity(node);
						
				}
				public CPEGValue<O,P> getChildValue(CPEGTerm<O,P> node, int index) {
					if (node == null) {
						return originalMap.get(
								original.getReturnVertex(
										orderedReturns.get(index))).getValue();
					} else {
						return filteredMap.getChildValue(node, index);
					}
				}
				
				public Iterable<? extends CPEGTerm<O,P>> getParentNodes(
						CPEGValue<O,P> value) {
					if (value == null) {
						return Collections.EMPTY_LIST;
					} else {
						return filteredMap.getParentNodes(value);
					}
				}
				public Iterable<? extends CPEGTerm<O,P>> getNodes(
						CPEGValue<O,P> value) {
					if (value == null) {
						return Collections.singleton(null);
					} else {
						return filteredMap.getNodes(value);
					}
				}
			};
		
		StackMap<CPEGValue<O,P>,CPEGTerm<O,P>> choice = 
			new StackMap<CPEGValue<O,P>,CPEGTerm<O,P>>();
		LinkedList<Pair<CPEGTerm<O,P>,Integer>> path = 
			new LinkedList<Pair<CPEGTerm<O,P>,Integer>>();
		
		
		Result ret = chooseChildren(
				choice,
				nullroot,
				path,
				null,
				0);
		if (ret == null) {
			this.getLogger().log("Root value is null");
			debug("Root returns null");
			return null;
		} else {
			// build a Map version of the results
			Map<CPEGValue<O,P>,CPEGTerm<O,P>> result = 
				new HashMap<CPEGValue<O,P>,CPEGTerm<O,P>>();
			for (CPEGValue<O,P> key : ret.choice.keySet())
				result.put(key, ret.choice.get(key));
			return result;
		}
	}
	
	private static void popToHeight(LinkedList<?> list, int height) {
		if (list.size() < height)
			throw new IllegalArgumentException(
					"Stack height is less than parameter: " + height);
		while (list.size() > height)
			list.removeLast();
	}

	class Result {
		StackMap<CPEGValue<O,P>,CPEGTerm<O,P>> choice;
		ValueMap<CPEGValue<O,P>,CPEGTerm<O,P>> graph;
		Result(
				StackMap<CPEGValue<O,P>,CPEGTerm<O,P>> _choice,
				ValueMap<CPEGValue<O,P>,CPEGTerm<O,P>> _graph) {
			this.choice = _choice;
			this.graph = _graph;
		}
	}
	
	protected Result choose(
			StackMap<CPEGValue<O,P>,CPEGTerm<O,P>> choice,
			ValueMap<CPEGValue<O,P>,CPEGTerm<O,P>> graph,
			LinkedList<Pair<CPEGTerm<O,P>,Integer>> path,
			CPEGValue<O,P> currentv,
			CPEGTerm<O,P> currentn) {
		if (choice.containsKey(currentv)) {
			// already chosen, verify
			if (!currentn.equals(choice.get(currentv)))
				return null;
			
			if (currentn.getOp().isTheta()) {
				// theta node, check whether it is in the path with child index 1
				for (Iterator<Pair<CPEGTerm<O,P>,Integer>> iter = path.descendingIterator(); iter.hasNext(); ) {
					Pair<CPEGTerm<O,P>,Integer> pair = iter.next();
					if (pair.getFirst() != null && pair.getFirst().equals(currentn)) {
						if (pair.getSecond() == 1)
							return new Result(choice, graph);
						else {
							debug("Found a bad theta cycle");
							return null;
						}
					}
				}
				// not seen in path, okay
				return new Result(choice, graph);
			} else {
				// not a theta node, check if it is in the path with a theta[1] in between
				for (Iterator<Pair<CPEGTerm<O,P>,Integer>> iter = path.descendingIterator(); iter.hasNext(); ) {
					Pair<CPEGTerm<O,P>,Integer> pair = iter.next();
					if (pair.getFirst() != null && pair.getFirst().equals(currentn)) {
						debug("Found a bad non-theta cycle");
						return null;
					}
					else if (pair.getFirst().getOp().isTheta() && 
							 pair.getSecond() == 1)
						return new Result(choice, graph);
				}
				// not seen at all, okay
				return new Result(choice, graph);
			}
		}
		
		// not already chosen

		final int choiceHeight = choice.getHeight();
		choice.push(currentv, currentn);
		
		final int pathHeight = path.size();

		Set<CPEGTerm<O,P>> toremove = 
			new HashSet<CPEGTerm<O,P>>();
		for (CPEGTerm<O,P> M : graph.getNodes(currentv))
			toremove.add(M);
		toremove.remove(currentn);
		
		ValueMap<CPEGValue<O,P>,CPEGTerm<O,P>> newgraph =
			removeAndPropagate(graph, toremove);

		// continue if N or any node from PATH is not in NEWGRAPH
		if (!newgraph.containsNode(currentn)) {
			debug("Current node has been removed");
			return null;
		}
		for (Pair<CPEGTerm<O,P>,Integer> pair : path) {
			if (!newgraph.containsNode(pair.getFirst())) { 
				debug("Element of path has been removed");
				return null;
			}
		}
		for (CPEGTerm<O,P> chosen : choice.values()) {
			if (!newgraph.containsNode(chosen)) {
				debug("Chosen node has been removed");
				return null;
			}
		}
		
		Result ret = chooseChildren(
				choice,
				newgraph,
				path,
				currentn,
				0);
		
		if (ret == null) {
			// reset changed values
			choice.popToHeight(choiceHeight);
			popToHeight(path, pathHeight);
		}

		return ret;
	}
	
	
	private Result chooseChildren(
			StackMap<CPEGValue<O,P>,CPEGTerm<O,P>> choice,
			ValueMap<CPEGValue<O,P>,CPEGTerm<O,P>> graph,
			LinkedList<Pair<CPEGTerm<O,P>,Integer>> path,
			CPEGTerm<O,P> N,
			int index) {
		if (index >= graph.getArity(N))
			return new Result(choice, graph);

		CPEGValue<O,P> CV = graph.getChildValue(N, index);
		
		// sort the nodes of CV in ascending order of cost
		List<CPEGTerm<O,P>> sorted = new ArrayList<CPEGTerm<O,P>>();
		for (CPEGTerm<O,P> term : graph.getNodes(CV))
			sorted.add(term);
		Collections.sort(
				sorted, 
				new Comparator<CPEGTerm<O,P>>() {
					final CostModel<CPEGTerm<O,P>,Integer> costModel = 
						getCostModel();
					public int compare(CPEGTerm<O,P> left, CPEGTerm<O,P> right) {
						return costModel.cost(left) - costModel.cost(right);
					}
				});

		final int pathHeight = path.size();
		final int choiceHeight = choice.getHeight();
		for (CPEGTerm<O,P> CN : sorted) {
			popToHeight(path, pathHeight);
			choice.popToHeight(choiceHeight);
			
			Result ret = choose(choice, graph, path, CV, CN);
			if (ret == null) {
				continue;
			} else {
				popToHeight(path, pathHeight);
				Result ret2 = chooseChildren(
						ret.choice,
						ret.graph,
						path,
						N,
						index+1);
				if (ret2 != null)
					return ret2;
			}
		}

		debug("No valid set of children");
		return null;
	}
	
	
	/**
	 * This call will modify and reuse toremove.
	 */
	protected ValueMap<CPEGValue<O,P>,CPEGTerm<O,P>> removeAndPropagate(
			ValueMap<CPEGValue<O,P>,CPEGTerm<O,P>> graph,
			Set<CPEGTerm<O,P>> toremove) {
		LinkedList<CPEGValue<O,P>> worklist = new LinkedList<CPEGValue<O,P>>();
		for (CPEGTerm<O,P> term : toremove) {
			if (!worklist.contains(term.getValue()))
				worklist.add(term.getValue());
		}
		
		while (!worklist.isEmpty()) {
			CPEGValue<O,P> V = worklist.removeFirst();

			ValueMap<CPEGValue<O,P>,CPEGTerm<O,P>> newgraph = 
				new RemoveValueMap<CPEGValue<O,P>,CPEGTerm<O,P>>(
						graph,
						toremove);
			
			Set<CPEGTerm<O,P>> newremoved = new HashSet<CPEGTerm<O,P>>();
			Set<CPEGValue<O,P>> updated = new HashSet<CPEGValue<O,P>>();
			for (CPEGTerm<O,P> parent : newgraph.getParentNodes(V)) {
				for (int i = 0; i < newgraph.getArity(parent); i++) {
					int allowable = 0;
					CPEGValue<O,P> childValue = newgraph.getChildValue(parent, i);
					for (CPEGTerm<O,P> child : newgraph.getNodes(childValue)) {
						if (allowsChild(parent, i, child)) {
							allowable++;
							break;
						}
					}
					if (allowable == 0) {
						newremoved.add(parent);
						updated.add(newgraph.getValue(parent));
						break;
					}
				}
			}
			
			toremove.addAll(newremoved);
			worklist.addAll(updated);
		}
		
		return new RemoveValueMap<CPEGValue<O,P>,CPEGTerm<O,P>>(
				graph, 
				toremove);
	}

	protected abstract boolean isUsable(CPEGTerm<O,P> term);
	
	protected abstract boolean allowsChild(
			CPEGTerm<O,P> parent,
			int childIndex,
			CPEGTerm<O,P> child);
}
