package peggy.pb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import peggy.represent.StickyPredicate;
import util.Function;
import util.pair.Pair;

/**
 * This is the default formulation builder that describes the constraints
 * that must be used to produce a valid PEG from an EPEG.
 */
public abstract class PeggyFormulationBuilder<V,N, NUMBER extends Number, D extends ExpressionDigraph<V,N>> 
implements FormulationBuilder<V,N,NUMBER,D> {
	public static final int THRESHOLD = (1<<25);
	
	public abstract String toString(N n);
	protected abstract int getMaxCost();
	
	public boolean buildFormulation(final PseudoBooleanFormulation<N> pbf) {
		D graph = this.getGraph();

		final Map<Pair<N,N>,Variable<N>> transitiveCache = 
			new HashMap<Pair<N,N>,Variable<N>>();
		Function<Pair<N,N>,Variable<N>> getTransVar = 
			new Function<Pair<N,N>,Variable<N>>() {
				public Variable<N> get(Pair<N,N> pair) {
					Variable<N> result = transitiveCache.get(pair);
					if (result == null) {
						result = pbf.getFreshVariable(null);
						transitiveCache.put(pair, result);
					}
					return result;
				}
			};
		
		// check for one invalid case
		for (V P : graph.getValues()) {
			if (!graph.isRoot(P))
				continue;
			if (!graph.getValueElements(P).iterator().hasNext()) {
				// root cloud has no elements
				throw new IllegalArgumentException("Root cloud has no elements");
			}
		}
		
		CostModel<N, NUMBER> costModel = this.getCostModel();
		
		List<Set<N>> scc = SCCAnalysis.<N>computeSCC(graph);
		StickyPredicate<N> stickyP = graph.getStickyPredicate();

		List<Variable<N>> thresholdViolators = 
			new ArrayList<Variable<N>>();
		
		ObjectiveFunction<N> objFunction;
		Map<N,Variable<N>> node2variable = new HashMap<N,Variable<N>>();
		{// create variables for all nodes, and assign weights to them all
			objFunction = new ObjectiveFunction<N>(true);
			WeightMap<N> objectiveMap = objFunction.getWeightMap();
			for (N node : graph.getNodes()){
				Variable<N> v = pbf.getFreshVariable(node);
				node2variable.put(node, v);
				
				int nodeCost = costModel.cost(node).intValue();
				if (nodeCost > THRESHOLD) {
					objectiveMap.assignWeight(v, 0);
					thresholdViolators.add(v);
				} else {
					objectiveMap.assignWeight(v, nodeCost);
				}
			}
			
			pbf.setObjectiveFunction(objFunction);
		}
		
		if (getMaxCost() > 0) {
			pbf.addConstraint(
					new Constraint<N>(
							objFunction.getWeightMap(), 
							Constraint.Relation.LESS_EQUAL, 
							getMaxCost()), 
					"Maximum cost");
		}
		
		// add the threshold constraints
		if (thresholdViolators.size() > 0){
			WeightMap<N> thresholdMap = new WeightMap<N>();
			for (Variable<N> v : thresholdViolators) {
				thresholdMap.assignWeight(v, 1);
			}
			pbf.addConstraint(
					new Constraint<N>(thresholdMap, Constraint.Relation.EQUAL, 0), 
					"Nodes exceed threshold constraint");
		}
		
		
		////////// print out the constraints ///////////
		for (Set<N> C : scc) {
			// foreach node n in C
			for (N n : C){
				Variable<N> Nn_Nn = getTransVar.get(new Pair<N,N>(n,n));
				
				if (!graph.isRecursive(n)) {
					// add rule "Nn_Nn = 0"
					WeightMap<N> wm = new WeightMap<N>();
					wm.assignWeight(Nn_Nn, 1);
					pbf.addConstraint(
							new Constraint<N>(wm, Constraint.Relation.EQUAL, 0), 
							"Not a theta node, cannot self-reference: " + toString(n));
				}
			}


			// foreach distinct pair of nodes (n,m) in C
			for (N n : C) {
				for (N m : C) {
					if (n.equals(m)) continue;
					for (int index = 0; index < graph.getArity(m); index++) {
						for (N a : graph.getValueElements(graph.getChildValue(m, index))) {
							if (!C.contains(a)) continue;
							if (a.equals(m)) continue;

							Variable<N> Nn_Nm = getTransVar.get(new Pair<N,N>(n,m)); 
							Variable<N> Nm_Na = getTransVar.get(new Pair<N,N>(m,a)); 
							Variable<N> Nn_Na = getTransVar.get(new Pair<N,N>(n,a)); 
							
							// add rule "Nn_Nm + Nm_Na - Nn_Na <= 1"
							WeightMap<N> wm = new WeightMap<N>();
							wm.assignWeight(Nn_Nm, 1);
							wm.assignWeight(Nm_Na, 1);
							wm.assignWeight(Nn_Na, -1);
							
							pbf.addConstraint(
									new Constraint<N>(wm, Constraint.Relation.LESS_EQUAL, 1), 
									"Transitive rule");
						}
						
						if (graph.isRecursive(m))
							break;
					}
				}
			}
		}



		// foreach cloud P
		for (V P : graph.getValues()) {
			WeightMap<N> wm = new WeightMap<N>();
			
			boolean gotroot = graph.isRoot(P);
			boolean gotany = false;
			for (N v : graph.getValueElements(P)){
				gotany = true;
				wm.assignWeight(node2variable.get(v), 1);
			}
			if (!gotany)
				continue;
			
			if (gotroot) {
				// add rule "sum_{v in P}{Nv} = 1"
				pbf.addConstraint(
						new Constraint<N>(wm, Constraint.Relation.EQUAL, 1), 
						"This is a root cloud, exactly 1 must be used");
			} else {
				// add rule "sum_{v in P}{Nv} <= 1"
				pbf.addConstraint(
						new Constraint<N>(wm, Constraint.Relation.LESS_EQUAL, 1), 
						"This is not a root cloud, at most 1 used");
			}
		}

		// foreach node a in V
		Aloop:
		for (N a : graph.getNodes()) {
			Variable<N> Na = node2variable.get(a);
			// foreach child cloud P of a
			for (int index=0;index<graph.getArity(a);index++) {
				boolean sticky = stickyP.isSticky(a, index);
				
				Iterable<? extends N> P = graph.getValueElements(graph.getChildValue(a, index));

				WeightMap<N> ORMAP = new WeightMap<N>();
				boolean anyAllowable = false;
				for (N n : P) {
					if (n.equals(a)) {
						// A is unusable
						WeightMap<N> singleton = new WeightMap<N>();
						singleton.assignWeight(Na, 1);
						pbf.addConstraint(
								new Constraint<N>(singleton, Constraint.Relation.EQUAL, 0), 
								"Node is its own child and not theta[1]: " + Na);
						continue Aloop;
					}
					
					boolean addNode;
					if (sticky) {
						// if sticky, only add this node if its allowable
						addNode = stickyP.allowsChild(a, index, n);
					} else {
						// if nonsticky, always add the node
						addNode = true;
					}

					if (addNode) {
						anyAllowable = true;
						Variable<N> Nn = node2variable.get(n);
						ORMAP.assignWeight(Nn, 1);
					}
				}

				if (anyAllowable) {
					// parent points to allowable children
					ORMAP.assignWeight(Na, -1);
					pbf.addConstraint(
							new Constraint<N>(ORMAP, Constraint.Relation.MORE_EQUAL, 0), 
							"Parent has these allowable children along index " + index);
				} else {
					// parent has no allowable children along index i
					WeightMap<N> singleton = new WeightMap<N>();
					singleton.assignWeight(Na, 1);
					pbf.addConstraint(
							new Constraint<N>(singleton, Constraint.Relation.EQUAL, 0), 
							"Don't use this node, no allowable children along index " + index + ": " + Na);
					continue Aloop;
				}
			}
		}

		
		// add the transitivity axioms for parent-child relationships
		for (Set<N> C : scc) {
			for (N a : C) {
				Variable<N> Na = node2variable.get(a);

				for (int index=0;index<graph.getArity(a);index++) {
					Iterable<? extends N> P = graph.getValueElements(graph.getChildValue(a, index));
					for (N n : P) {
						Variable<N> Nn = node2variable.get(n);

						// if there is some C in SCC such that A in C and N in C
						if (C.contains(n) && !(graph.isRecursive(a) && index==1)) {
							// add rule "Na ^ Nn => Na_Nn" === "Na + Nn - N_{a->n} <= 1"
							// but only if !(a=theta && index!=0)
							WeightMap<N> wm = new WeightMap<N>();
							if (n.equals(a)) {
								wm.assignWeight(Na, 2);
							} else {
								wm.assignWeight(Na, 1);
								wm.assignWeight(Nn, 1);
							}
							Variable<N> Na_Nn = getTransVar.get(new Pair<N,N>(a,n));
							wm.assignWeight(Na_Nn, -1);

							pbf.addConstraint(
									new Constraint<N>(wm, Constraint.Relation.LESS_EQUAL, 1), 
									"Na and Nn implies Na_Nn: " + Na.toString() + " " + Nn.toString());
						}
					}
				}
			}
		}
		
		return true;
	}
}
