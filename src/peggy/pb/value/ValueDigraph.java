package peggy.pb.value;

import java.util.HashSet;
import java.util.Set;

import peggy.pb.Digraph;
import peggy.pb.ExpressionDigraph;

/**
 * This is a digraph that connects values instead of terms.
 */
public class ValueDigraph<V,N> implements Digraph<V> {
	protected final ExpressionDigraph<V,N> nodeGraph;
	protected final Set<V> values;
	
	public ValueDigraph(ExpressionDigraph<V,N> _nodeGraph) {
		this.nodeGraph = _nodeGraph;
		this.values = new HashSet<V>();
		for (V v : this.nodeGraph.getValues()) {
			this.values.add(v);
		}
	}
	
	public int getNodeCount() {return this.values.size();}
	public Iterable<? extends V> getNodes() {return this.values;}
	public Iterable<? extends V> getSuccessors(V value) {
		Set<V> result = new HashSet<V>();
		for (N node : this.nodeGraph.getValueElements(value)) {
			for (N succ : this.nodeGraph.getSuccessors(node)) {
				result.add(this.nodeGraph.getElementValue(succ));
			}
		}
		return result;
	}
	public Digraph<V> getReverseDigraph() {
		final Digraph<N> reverseNodeGraph = this.nodeGraph.getReverseDigraph();
		return new Digraph<V>() {
			public int getNodeCount() {return ValueDigraph.this.getNodeCount();}
			public Iterable<? extends V> getNodes() {return ValueDigraph.this.getNodes();}
			public Iterable<? extends V> getSuccessors(V value) {
				Set<V> result = new HashSet<V>();
				for (N node : ValueDigraph.this.nodeGraph.getValueElements(value)) {
					for (N succ : reverseNodeGraph.getSuccessors(node)) {
						result.add(ValueDigraph.this.nodeGraph.getElementValue(succ));
					}
				}
				return result;
			}
			public Digraph<V> getReverseDigraph() {return ValueDigraph.this;}
		};
	}
}
