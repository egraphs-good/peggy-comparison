package util.graph;

public abstract class AbstractMutableVertex
		<G extends Graph<?,? extends V>, V extends Vertex<?,? super V>>
		extends AbstractVertex<G,V> implements MutableVertex<G,V> {
	public void removeAllChildren() {
		while (!getChildren().isEmpty())
			removeChild(getChildren().iterator().next());
	}

	public void removeAllParents() {
		while (!getParents().isEmpty())
			removeParent(getParents().iterator().next());
	}
}
