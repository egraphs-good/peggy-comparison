package eqsat.meminfer.network.basic;

import util.NamedTag;
import util.Taggable;
import util.graph.OrderedVertex;

public abstract class MassSourceStructurizer
		<V extends Taggable & OrderedVertex<?,? extends V>>
		extends SourceStructurizer<V> {
	private final NamedTag<Void> mAdding = new NamedTag<Void>("Adding");
	
	protected abstract boolean isParameter(V vertex);
	
	public void addExpression(V root) {
		if (isAdded(root) || isParameter(root) || root.hasTag(mAdding))
			return;
		root.setTag(mAdding);
		addVertex(root);
		for (int i = 0; i < root.getChildCount(); i++)
			addExpression(root.getChild(i));
	}
}