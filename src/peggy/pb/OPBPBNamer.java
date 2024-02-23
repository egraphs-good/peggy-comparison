package peggy.pb;

import java.util.HashMap;
import java.util.Map;

import util.pair.Pair;

/**
 * This namer names everything like "x0", "x1", etc
 */
public class OPBPBNamer<N> implements PBNamer<N> {
	private final Map<N,Integer> node2name = 
		new HashMap<N,Integer>();
	private final Map<Pair<N,N>,Integer> pair2name = 
		new HashMap<Pair<N,N>,Integer>();
	private int nameCounter = 0;
	
	public String getName(N node) {
		if (node2name.containsKey(node))
			return ("x" + node2name.get(node)).intern();
		int id = nameCounter++;
		node2name.put(node, id);
		return ("x" + id).intern();
	}
	public String getTransitiveName(N a, N b) {
		Pair<N,N> pair = new Pair<N,N>(a,b);
		if (pair2name.containsKey(pair))
			return ("x" + pair2name.get(pair)).intern();
		int id = nameCounter++;
		pair2name.put(pair, id);
		return ("x" + id).intern();
	}
}
