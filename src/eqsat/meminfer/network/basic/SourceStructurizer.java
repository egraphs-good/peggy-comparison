package eqsat.meminfer.network.basic;

import eqsat.meminfer.network.basic.StructureNetwork.StructureNode;
import eqsat.meminfer.network.basic.StructureNetwork.TermNode;
import eqsat.meminfer.network.basic.TermValueNetwork.TermValueNode;
import util.NamedTag;
import util.Tag;
import util.Taggable;
import util.graph.OrderedVertex;

public abstract class SourceStructurizer
		<V extends Taggable & OrderedVertex<?,? extends V>>
		extends EqualityStructurizer<V> {
	protected enum Source {
		Known {
			public TermNode getSource(StructureNetwork network) {
				return network.known();
			}
			public StructureNode constrainChild(StructureNetwork network,
					StructureNode structure, TermValueNode term, int child) {
				return network.checkChildIsKnown(term, child, structure);
			}
		}, False {
			public TermNode getSource(StructureNetwork network) {
				return network.fals();
			}
			public StructureNode constrainChild(StructureNetwork network,
					StructureNode structure, TermValueNode term, int child) {
				return network.checkChildIsFalse(term, child, structure);
			}
		};
		
		public abstract TermNode getSource(StructureNetwork network);
		public abstract StructureNode constrainChild(StructureNetwork network,
				StructureNode structure, TermValueNode term, int child);
	}
	
	private final Tag<Source> mSourceTag = new NamedTag<Source>("Source");
	
	protected TermNode getSource(V vertex) {
		return vertex.hasTag(mSourceTag)
				? vertex.getTag(mSourceTag).getSource(getNetwork())
				: super.getSource(vertex);
	}
	
	public void makeKnown(V vertex) {vertex.setTag(mSourceTag, Source.Known);}
	public void makeFalse(V vertex) {vertex.setTag(mSourceTag, Source.False);}

	public StructureNode getStructure() {
		StructureNode structure = super.getStructure();
		Tag<Void> processed = new NamedTag<Void>("Processed");
		for (V vertex : getVertices())
			for (int i = 0; i < vertex.getChildCount(); i++) {
				V child = vertex.getChild(i);
				if (child.hasTag(mSourceTag) && !child.hasTag(processed)
						&& !isAdded(child)) {
					structure = child.getTag(mSourceTag).constrainChild(
							getNetwork(), structure, getTermValue(vertex), i);
					child.setTag(processed);
				}
			}
		return structure;
	}
}
