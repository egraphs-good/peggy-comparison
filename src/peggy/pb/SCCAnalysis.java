package peggy.pb;

import java.util.*;

public class SCCAnalysis {
	/** This method will compute the sets of strongly connected
	 *  components for the egraph given in the constructor.
	 *  The output will be a list of sets of nodes, each element of the
	 *  list is a single strongly connected component.
	 */
	public static <N> List<Set<N>> computeSCC(Digraph<N> graph) {
		List<N> worklist = new ArrayList<N>(graph.getNodeCount());
		for (N node : graph.getNodes())
			worklist.add(node);
		Map<N,Integer> forwardFinish = DFS(graph, worklist, null);

		Collections.sort(worklist, new FComparator<N>(forwardFinish));
		forwardFinish = null; // free up some memory
		List<Set<N>> components = new ArrayList<Set<N>>(20);
		DFS(graph.getReverseDigraph(), worklist, components);
		
		return components;
	}


	/** This is a comparator that takes a map from N's to Integers,
	 *  and will order the N's in increasing order of mapped value.
	 */
	private static class FComparator<N> implements Comparator<N> {
		private Map<N,Integer> F;

		public FComparator(Map<N,Integer> _F) {
			F = _F;
		}

		public int compare(N n1, N n2) {
			int f1 = F.get(n1);
			int f2 = F.get(n2);
			return -(f1-f2);
		}

		public boolean equals(Object o) {
			return super.equals(o);
		}
	}


	/** This method performs a DFS and returns the finish time map.
	 *  This same method is called twice from computeSCC. The first time,
	 *  it performs a normal DFS and returns the finish time map.
	 *  In the first call, both parameters will be null. 
	 *  The second time, both parameters will not be null, and the DFS should
	 *  run again, only this time it must sort the values in the worklist 
	 *  according to decreasing finish time from the first run (this is done
	 *  by providing a Comparator and calling Collections.sort). Also, during
	 *  the second DFS we want to collect the disjoint DFS trees, because they
	 *  will be our final SCC's. The trees will be put into 'components' if it is non-null.
	 */
	private static <N> Map<N,Integer> DFS(Digraph<N> graph, List<N> worklist, List<Set<N>> components) {
		HashMap<N,Integer> finish = new HashMap<N,Integer>();
		Set<N> seen = new HashSet<N>();

		int[] counter = {0};
		for (N next : worklist) {
			if (seen.contains(next))
				continue;

			Set<N> component = null;
			if (components!=null)
				component = new HashSet<N>();

			counter[0]++;
			DFS_helper(next, finish, seen, counter, graph, component);

			if (components!=null)
				components.add(component);
		}
		return finish;
	}
	private static <N> void DFS_helper(N root, Map<N,Integer> finish, Set<N> seen, int[] counter, 
									   Digraph<N> graph, Set<N> component) {
		// assume root has not been seen
		seen.add(root);
		if (component!=null)
			component.add(root);

		for (N child : graph.getSuccessors(root)) {
			if (seen.contains(child))
				continue;
			counter[0]++;
			DFS_helper(child, finish, seen, counter, graph, component);
		}

		finish.put(root, counter[0]);
		counter[0]++;
	}
}	
