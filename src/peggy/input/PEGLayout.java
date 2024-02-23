package peggy.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import peggy.analysis.CREGVertexIterable;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;

/**
 * This class provides a Layout for PEGs.
 */
public class PEGLayout<L,P,R> extends Layout {
	private static final long serialVersionUID = 1L;
	private final Set<? extends Vertex<FlowValue<P,L>>> roots;
	private final boolean all;
	public PEGLayout(
			Set<? extends Vertex<FlowValue<P,L>>> _roots,
			boolean _all) {
		this.roots = _roots;
		this.all = _all;
		this.build();
	}
	protected Set<? extends Value> getValues() {
		CREGVertexIterable<L,P> iterable = new CREGVertexIterable<L, P>(roots);

		Map<Vertex<FlowValue<P,L>>,Value> term2value = 
			new HashMap<Vertex<FlowValue<P,L>>,Value>();
		int counter = 0;
		Set<Value> result = new HashSet<Value>();
		for (Vertex<FlowValue<P,L>> vertex : iterable) {
			Value v = new Value(counter++, roots.contains(vertex));
			term2value.put(vertex, v);
			if (all)
				result.add(v);
			else if (roots.contains(vertex))
				result.add(v);
		}
		
		for (Vertex<FlowValue<P,L>> vertex : iterable) {
			Value v = term2value.get(vertex);
			List<Value> children = new ArrayList<Value>();
			for (int i = 0; i < vertex.getChildCount(); i++)
				children.add(term2value.get(vertex.getChild(i)));
			v.addTerm(vertex.getLabel().toString(), children);
		}

		return result;
	}
}
