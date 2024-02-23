package peggy.pb;

import java.util.HashMap;
import java.util.Map;

/**
 * This namer gives nodes names like "N0", "N1", etc
 * and transitive nodes names like "N0_N1", "N4_N5", etc
 */
public class DefaultPBNamer<N> implements PBNamer<N> {
	private final Map<N,String> node2name =
		new HashMap<N,String>();
	private int nodeCounter = 0;
	
	public String getName(N node) {
		if (node2name.containsKey(node))
			return node2name.get(node);
		String name = "N" + (nodeCounter++);
		node2name.put(node, name);
		return name;
	}
	public String getTransitiveName(N a, N b) {
		String Na = getName(a);
		String Nb = getName(b);
		return Na + "_" + Nb;
	}
}
