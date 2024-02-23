package peggy.pb.value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import peggy.pb.Constraint;
import peggy.pb.CostModel;
import peggy.pb.ExpressionDigraph;
import peggy.pb.FormulationBuilder;
import peggy.pb.ObjectiveFunction;
import peggy.pb.PseudoBooleanFormulation;
import peggy.pb.SCCAnalysis;
import peggy.pb.Variable;
import peggy.pb.WeightMap;
import peggy.represent.StickyPredicate;
import util.Function;
import util.pair.Pair;

/**
 * This is a formulation builder that describes how to choose a PEG from an
 * EPEG, while using only values to constrain the theta-loops.
 */
public abstract class PeggyValueFormulationBuilder<V,N, NUMBER extends Number, D extends ExpressionDigraph<V,N>> 
implements FormulationBuilder<V,N,NUMBER,D> {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("PeggyValueFormulationBuilder: " + message);
	}
	
	
	public static final int THRESHOLD = (1<<25);

	public boolean buildFormulation(final PseudoBooleanFormulation<N> pbf) {
		D graph = this.getGraph();
		
		final Map<Pair<V,V>,Variable<N>> transitiveCache = 
			new HashMap<Pair<V,V>,Variable<N>>();
		Function<Pair<V,V>,Variable<N>> getTransVar = 
			new Function<Pair<V,V>,Variable<N>>() {
				public Variable<N> get(Pair<V,V> pair) {
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

		CostModel<N,NUMBER> costModel = this.getCostModel();
		ValueDigraph<V,N> valueGraph = new ValueDigraph<V,N>(graph);
		List<Set<V>> scc = SCCAnalysis.<V>computeSCC(valueGraph);
		StickyPredicate<N> stickyP = graph.getStickyPredicate();
		List<Variable<N>> thresholdViolators = new ArrayList<Variable<N>>();
		
		Map<N,Variable<N>> node2variable = new HashMap<N,Variable<N>>();
		Map<V,Variable<N>> value2variable = new HashMap<V,Variable<N>>();
		{// create variables for all nodes, and assign weights to them all
			ObjectiveFunction<N> objFunction = new ObjectiveFunction<N>(true);
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
			
			for (V value : graph.getValues()) {
				Variable<N> v = pbf.getFreshVariable(null);
				value2variable.put(value, v);
			}
			
			pbf.setObjectiveFunction(objFunction);
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
		
		// separate pass just for sticky nodes
		// for each node n
		for (N n : graph.getNodes()) {
			// for each child index
			for (int index = 0; index < graph.getArity(n); index++) {
				// if that child index is sticky for n
				boolean sticky = stickyP.isSticky(n, index);
				if (!sticky) continue;

				// gather all the allowable nodes in the child cloud
				WeightMap<N> wm = new WeightMap<N>();
				for (N m : graph.getValueElements(graph.getChildValue(n, index))) {
					boolean allows = stickyP.allowsChild(n, index, m);
					if (allows)
						wm.assignWeight(node2variable.get(m), 1);
				}
				wm.assignWeight(node2variable.get(n), -1);
				// add rule  n => m1 \/ m2 \/ ... \/ mK
				// (m1 + m2 + ... + mK - n >= 0)
				
				Constraint<N> cons = new Constraint<N>(
						wm,
						Constraint.Relation.MORE_EQUAL,
						0);
				pbf.addConstraint(cons, "Node can only use sticky children");
			}
		}
		
		////////// print out the constraints ///////////
		for (Set<V> C : scc) {
			// foreach node V in C
			for (V v : C) {
				Variable<N> Nv_Nv = getTransVar.get(new Pair<V,V>(v,v));
				
				/* make rule:
				 * Nv_Nv => theta1 \/ theta2 \/ ... \/ thetaK
				 *   where thetaI are the thetas inside value V
				 *   or if there are none, then
				 * Nv_Nv = 0  (can't be a self-loop)
				 */
				WeightMap<N> thetas = new WeightMap<N>();
				int thetaCount = 0;
				for (N n : graph.getValueElements(v)) {
					if (graph.isRecursive(n)) {
						thetas.assignWeight(node2variable.get(n), 1);
						thetaCount++;
					}
				}

				if (thetaCount > 0) {
					// Nv_Nv => theta1 \/ theta2 \/ ... \/ thetaK
					thetas.assignWeight(Nv_Nv, -1);
					Constraint<N> cons = new Constraint<N>(
							thetas, 
							Constraint.Relation.MORE_EQUAL,
							0);
					pbf.addConstraint(cons, "Cloud can only have self loops if it uses a theta node");
				} else {
					// Nv_Nv = 0
					thetas.assignWeight(Nv_Nv, 1);
					Constraint<N> cons = new Constraint<N>(
							thetas, 
							Constraint.Relation.EQUAL,
							0);
					pbf.addConstraint(cons, "Cloud can only have self loops if it uses a theta node");
				}
			}


			// foreach distinct pair of nodes (n,m) in C
			for (V n : C) {
				for (V m : C) {
					if (n.equals(m)) continue;

					// for each successor a of m in the same SCC (a!=m)
					for (V a : valueGraph.getSuccessors(m)) {
						if (!C.contains(a)) continue;
						if (a.equals(m)) continue;

						Variable<N> Vn_Vm = getTransVar.get(new Pair<V,V>(n,m));
						Variable<N> Vm_Va = getTransVar.get(new Pair<V,V>(m,a));
						Variable<N> Vn_Va = getTransVar.get(new Pair<V,V>(n,a));

						// add rule  Vn_Vm /\ Vm_Va => Vn_Va 
						// add rule "Vn_Vm + Vm_Va - Vn_Va <= 1"
						WeightMap<N> wm = new WeightMap<N>();
						wm.assignWeight(Vn_Vm, 1);
						wm.assignWeight(Vm_Va, 1);
						wm.assignWeight(Vn_Va, -1);

						pbf.addConstraint(
								new Constraint<N>(wm, Constraint.Relation.LESS_EQUAL, 1), 
								"Transitive rule");
					}
				}
			}
		}

		
		// foreach cloud P
		for (V P : graph.getValues()){
			WeightMap<N> wm = new WeightMap<N>();
			for (N v : graph.getValueElements(P)){
				Variable<N> Nv = node2variable.get(v);
				wm.assignWeight(Nv, 1);
			}
			
			// add rule "sum_{v in P}(Nv) = VP"
			Variable<N> VP = value2variable.get(P);
			wm.assignWeight(VP, -1);
			pbf.addConstraint(
					new Constraint<N>(wm, Constraint.Relation.EQUAL, 0), 
					"Cloud equals exactly one of its nodes");

			// if this is a root cloud
			if (graph.isRoot(P)) {
				// add rule "VP = 1"
				WeightMap<N> wm2 = new WeightMap<N>();
				wm2.assignWeight(VP, 1);
				pbf.addConstraint(
						new Constraint<N>(wm2, Constraint.Relation.EQUAL, 1), 
						"Root cloud must be used");
			}
		}
		

		// foreach node a in V
		for (N a : graph.getNodes()) {
			Variable<N> Na = node2variable.get(a);
			Set<V> childvalues = new HashSet<V>();
			for (int index = 0; index < graph.getArity(a); index++) {
				childvalues.add(graph.getChildValue(a, index));
			}
			// foreach (unique) child cloud P of a
			for (V P : childvalues) {
				// add rule  a => P   (P-a >= 0)
				Variable<N> VP = value2variable.get(P);
				WeightMap<N> wm = new WeightMap<N>();
				wm.assignWeight(Na, -1);
				wm.assignWeight(VP, 1);
				Constraint<N> cons = new Constraint<N>(
						wm, Constraint.Relation.MORE_EQUAL, 0);
				pbf.addConstraint(cons, "Parent must use child cloud");
			}
		}

		// add the transitivity axioms for parent-child relationships
		// for each node n
		for (N n : graph.getNodes()) {
			Variable<N> Nn = node2variable.get(n);
			V Vn = graph.getElementValue(n);
			
			Set<V> children = new HashSet<V>();
			for (int index = 0; index < graph.getArity(n); index++) {
				// don't do this for right child of theta!
				if (graph.isRecursive(n) && index==1)
					continue;
				children.add(graph.getChildValue(n, index));
			}
			// for each (unique) child cloud Vm
			for (V Vm : children) {
				Variable<N> Vn_Vm = getTransVar.get(new Pair<V,V>(Vn,Vm));
				// add rule n => Vn_Vm  (Vn_Vm - n >= 0)
				WeightMap<N> wm = new WeightMap<N>();
				wm.assignWeight(Nn, -1);
				wm.assignWeight(Vn_Vm, 1);
				Constraint<N> cons = new Constraint<N>(
						wm,
						Constraint.Relation.MORE_EQUAL,
						0);
				pbf.addConstraint(cons, "Node implies transitive node to child");
			}
		}
		
		return true;
	}
}
