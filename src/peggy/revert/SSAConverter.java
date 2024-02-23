package peggy.revert;

import java.util.*;

import util.Function;

/**
 * Very general-purpose code to compute the SSA form of a given CFG.
 * This will only replace the variables V such that canReplace(V) is true.
 * canReplace should be implemented to return true for only the variables that
 * appear in the original CFG and have multiple definition points.
 * 
 * IMPORTANT: The SSA algorithm assumes that there is an assignment to EVERY variable
 * in the start block of the CFG. The algorithm relies on this, and is incorrect in
 * the absence of this fact. There is a neat way to hack around this though. See
 * LLVMSSAConverter for details. 
 * 
 * @author steppm
 *
 * @param <G> the graph type
 * @param <B> the node type
 * @param <V> the variable type
 */
public abstract class SSAConverter<G extends DominatorGraph<G,B>, B extends DominatorVertex<G,B>, V> {
   private static final boolean DEBUG = false;
   private static void debug(String message) {
      if (DEBUG)
         System.err.println("SSAConverter: " + message);
   }

	public abstract class Statement {
		public abstract boolean isPhi();
		public abstract Iterable<? extends V> getUsedVariables();
		public abstract Iterable<? extends V> getDefinedVariables();
		public abstract void replaceOperand(V oldvar, int index);
		public abstract void replacePhiOperand(B pred, Function<V,Integer> var2index);
		public abstract void replaceDefinition(V oldvar, int index);
	}
	
	protected final G graph;
	protected final Map<B,Set<B>> DF;
	
	public SSAConverter(G _graph) {
		this.graph = _graph;
		this.DF = new HashMap<B,Set<B>>();
		computeDominanceFrontiers();
	}

	/**
	 * Compute the dominance frontiers and stores them
	 * in the DF map. 
	 * A node w is in the dominance frontier of x if
	 * x dominates a predecessor of w, and x does not strictly dominate w.
	 */
	private void computeDominanceFrontiers() {
		for (B x : this.graph.getVertices()) {
			Set<B> frontier = new HashSet<B>();
			
			for (B w : this.graph.getVertices()) {
				// x cannot strictly dominate w
				if (w.getDominators().contains(x) && !x.equals(w))
					continue;
				
				// x must dominate some predecessor of w
				for (B predw : w.getParents()) {
					if (predw.getDominators().contains(x)) {
						frontier.add(w);
						break;
					}
				}
			}
			this.DF.put(x, frontier);
		}
	}
	
	/**
	 * Should insert an instruction 
	 * 	a = phi(a,a,...,a) 
	 * at the top of node y, where there are
	 * as many a's in the RHS of the phi as there are
	 * predecessors of y.
	 * @param a the variable
	 * @param y the block
	 */
	protected abstract void insertPhi(V a, B y);
	protected abstract Set<? extends V> getVariables();
	protected abstract Iterable<? extends Statement> getStatements(B node);
	protected abstract boolean canReplace(V v);

	protected G getGraph() {return this.graph;}

	/**
	 * Returns the set of all variables that have definition 
	 * points in the CFG.
	 */
	private Set<? extends V> getAssignedVariables(B node) {
		Set<V> defs = new HashSet<V>();
		for (Statement S : getStatements(node)) {
			for (V v : S.getDefinedVariables()) {
				defs.add(v);
			}
		}
		return defs;
	}

	/**
	 * Returns the dominance frontier of the given node.
	 */
	protected Set<B> getDominanceFrontier(B node) {
		return this.DF.get(node);
	}

	/**
	 * Stage 1 of the SSA algorithm:
	 * 		insert statements of the form
	 * 		a = phi [a,b1] [a,b2] ... [a,bN]
	 * 		in certain places
	 */
	protected final void placePhis() {
		Map<V,Set<B>> defsites = new HashMap<V,Set<B>>();
		for (B n : this.graph.getVertices()) {
			Set<? extends V> Aorig = getAssignedVariables(n);
			for (V a : Aorig) {
				Set<B> defsitesA = defsites.get(a);
				if (defsitesA == null) {
					defsitesA = new HashSet<B>();
					defsites.put(a, defsitesA);
				}
				defsitesA.add(n);
			}
		}
		
		Map<V,Set<B>> Aphi = new HashMap<V,Set<B>>();
		
		// for each variable a
		for (V a : getVariables()) {
			if (!this.canReplace(a))
				continue;
			
			if (!defsites.containsKey(a))
				continue;
			// W = defsites[a]
			LinkedList<B> W = new LinkedList<B>(defsites.get(a));
			// while W not empty
			while (!W.isEmpty()) {
				// remove random node n from W
				B n = W.removeFirst();
				Set<B> DFn = getDominanceFrontier(n);
				
				// for each y in DF[n]
				for (B y : DFn) {
					Set<B> Aphi_a = Aphi.get(a);
					
					// if y notin Aphi[a]
					if (Aphi_a == null || !Aphi_a.contains(y)) {
						// insert a = phi(a,a,...,a) into y
						insertPhi(a, y);
						// Aphi[a] = Aphi[a] union {y}
						if (Aphi_a == null) {
							Aphi_a = new HashSet<B>();
							Aphi.put(a, Aphi_a);
						}
						Aphi_a.add(y);

						// if a notin Aorig[y]
						if (!getAssignedVariables(y).contains(a)) {
							// W = W union {y}
							W.add(y);
						}
					}
				}
			}
		}
	}
	

	/**
	 * Stage 2 of the SSA algorithm: 
	 * 		(can only be executed after placePhis)
	 *  	for a given multiply-defined var a,
	 *  	introduce new variables a1, a2, ..., aN
	 *  	(where there are N definition points of a)
	 *  	and replace the defs of a with defs of aI
	 *  	and replace the uses of a with the appropriate
	 *  	uses of aI's.
	 */
	private void renameVariables(
			B n, 
			final Map<V,Integer> Count, 
			final Map<V,Stack<Integer>> Stack) {
		
		List<V> defined = new ArrayList<V>();
		
		for (Statement S : getStatements(n)) {
			if (!S.isPhi()) {
				for (V x : S.getUsedVariables()) {
					if (!this.canReplace(x))
						continue;
					
					int i = Stack(x, Stack).peek();
					// if i = 0, then error!
					
					S.replaceOperand(x, i);
				}
			}
				
			for (V a : S.getDefinedVariables()) {
				if (!this.canReplace(a))
					continue;

				defined.add(a);
				
				int CountA = Count(a, Count);
				CountA++;
				Count.put(a, CountA);
				int i = CountA;
				Stack(a, Stack).push(i);
				S.replaceDefinition(a, i);
			}
		}

		Function<V,Integer> var2index = new Function<V,Integer>() {
			public Integer get(V v) {
				if (!canReplace(v))
					return -1;
				else
					return Stack(v, Stack).peek();
			}
		};
		
		// update the phis in all of the CFG successors of n 
		for (B Y : n.getChildren()) {
			for (Statement S : getStatements(Y)) {
				if (S.isPhi()) {
					S.replacePhiOperand(n, var2index);
				}
			}
		}

		// rename all of n's immediately dominated nodes
		Collection<? extends B> nDominators = n.getDominators();
		for (B X : this.graph.getVertices()) {
			Collection<? extends B> doms = X.getDominators();
			if (doms.containsAll(nDominators) &&
				doms.size() == nDominators.size()+1)
				this.renameVariables(X, Count, Stack);
		}
		
		for (V a : defined) {
			Stack.get(a).pop();
		}
	}

	// convenience method
	private Integer Count(V v, Map<V,Integer> Count) {
		Integer result = Count.get(v);
		if (result == null) {
			result = new Integer(0);
			Count.put(v, result);
		}
		return result;
	}
	// convenience method
	private Stack<Integer> Stack(V v, Map<V,Stack<Integer>> Stack) {
		Stack<Integer> result = Stack.get(v);
		if (result == null) {
			result = new Stack<Integer>();
			result.push(0);
			Stack.put(v, result);
		}
		return result;
	}
	
	
	
	/**
	 * Performs the restructuring into SSA form.
	 */
	public void run() {
      debug("***starting run***");
      
		this.placePhis();

      debug("***placePhis***");
		
		Map<V,Integer> Count = new HashMap<V,Integer>(); 
		Map<V,Stack<Integer>> Stack = new HashMap<V,Stack<Integer>>();

		this.renameVariables(this.graph.getStart(), Count, Stack);

      debug("***renameVariables***");
	}
}
