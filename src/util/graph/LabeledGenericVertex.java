package util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import util.Labeled;

public abstract class LabeledGenericVertex
		<G extends Graph<?,? extends V>, V extends GenericVertex<?,V>, L>
		extends GenericVertex<G,V> implements Labeled<L> {
	private L mLabel;

	public LabeledGenericVertex(G graph, L label) {
		this(graph, label, new ArrayList<V>(), new HashSet<V>());
	}
	protected LabeledGenericVertex(G graph, L label, List<V> children,
			Collection<V> parents) {
		super(graph, children, parents);
		mLabel = label;
	}
	
	public L getLabel() {return mLabel;}
	public boolean hasLabel(L label) {
		return mLabel == null ? label == null : mLabel.equals(label);
	}
	
	public List<? extends V> getChildren() {
		return (List<? extends V>)mChildren;
	}
	public V getChild(int index) {
		return getChildren().get(index);
	}
	
	public String toString() {return mLabel.toString();}
}
