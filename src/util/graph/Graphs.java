package util.graph;

import java.util.ArrayList;
import java.util.List;

import util.Function;
import util.NamedTag;
import util.Tag;
import util.Taggable;

public final class Graphs {
	public static <V extends Taggable & Vertex<?,V>> List<V> reverseToposort(
			Graph<?,V> graph) {
		final Tag<Boolean> added = new NamedTag<Boolean>("Added");
		final List<V> sorting = new ArrayList<V>(graph.getVertices().size());
		Function<V,Void> sort = new Function<V,Void>() {
			public Void get(V parameter) {
				if (parameter.hasTag(added)) {
					if (parameter.getTag(added))
						return null;
					else
						throw new IllegalArgumentException("Graph has cycles.");
				}
				parameter.setTag(added, false);
				for (V child : parameter.getChildren())
					get(child);
				sorting.add(parameter);
				parameter.setTag(added, true);
				return null;
			}
		};
		for (V vertex : graph.getVertices())
			sort.get(vertex);
		return sorting;
	}
}
