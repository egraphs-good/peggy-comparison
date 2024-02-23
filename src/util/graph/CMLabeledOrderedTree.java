package util.graph;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import util.ArrayCollection;
import util.MLabeled;

public final class CMLabeledOrderedTree<L> extends
		AbstractGraph<CMLabeledOrderedTree<L>,CMLabeledOrderedTree.Vertex<L>> {
	protected Collection<Vertex<L>> mVertices = new ArrayCollection();

	public CMLabeledOrderedTree<L> getSelf() {return this;}
	public Collection<? extends Vertex<L>> getVertices() {return mVertices;}
	
	public Vertex<L> makeVertex(L label) {
		return addVertex(new LeafVertex(label));
	}
	public Vertex<L> makeVertex(L label, Vertex<L> child) {
		return addVertex(new UniVertex(label, child));
	}
	public Vertex<L> makeVertex(L label, Vertex<L>... children) {
		return addVertex(new MultiVertex(label, children));
	}
	public Vertex makeVertex(L label, List<? extends Vertex<L>> children) {
		Vertex<L>[] array = new Vertex[children.size()];
		children.toArray(array);
		return makeVertex(label, children);
	}
	
	private Vertex<L> addVertex(Vertex<L> vertex) {
		mVertices.add(vertex);
		return vertex;
	}
	
	public interface Vertex<L> extends MLabeled<L>,
			MTreeVertex<CMLabeledOrderedTree<L>,Vertex<L>>,
			OrderedVertex<CMLabeledOrderedTree<L>,Vertex<L>> {}
	
	public abstract class CVertex
			extends AbstractVertex<CMLabeledOrderedTree<L>,Vertex<L>>
			implements Vertex<L> {
		protected L mLabel;
		protected Vertex<L> mParent = null;
		
		public CVertex(L label) {mLabel = label;}

		public CMLabeledOrderedTree<L> getGraph() {
			return CMLabeledOrderedTree.this;
		}
		public Vertex<L> getSelf() {return this;}
		
		public L getLabel() {return mLabel;}
		public void setLabel(L label) {mLabel = label;}
		public boolean hasLabel(L label) {
			return mLabel == null ? label == null : mLabel.equals(label);
		}
		
		public Vertex<L> getParent() {return mParent;}
		public void setParent(Vertex<L> parent) {
			if (mParent != null)
				throw new IllegalStateException();
			mParent = parent;
		}

		public abstract int getChildCount();
		public abstract List<? extends Vertex<L>> getChildren();
		
		public String toString() {
			return mLabel == null ? "<null>" : mLabel.toString();
		}
	}
	
	private final class LeafVertex extends CVertex {
		public LeafVertex(L label) {super(label);}

		public int getChildCount() {return 0;}
		
		public List<? extends Vertex<L>> getChildren() {
			return Collections.<Vertex<L>>emptyList();
		}

		public Vertex getChild(int child) {return getChildren().get(child);}

		public boolean hasChildren(Vertex... children) {
			return children.length == 0;
		}

		public boolean hasChildren(List<? extends Vertex<L>> children) {
			return children.isEmpty();
		}
	}
	
	private final class UniVertex extends CVertex {
		protected final Vertex<L> mChild;
		
		public UniVertex(L label, Vertex<L> child) {
			super(label);
			mChild = child;
			mChild.setParent(this);
		}

		public int getChildCount() {return 1;}
		
		public List<? extends Vertex<L>> getChildren() {
			return Collections.singletonList(mChild);
		}

		public Vertex getChild(int child) {
			if (child == 0)
				return mChild;
			else
				return getChildren().get(child);
		}

		public boolean hasChildren(Vertex<L>... children) {
			return children.length == 1 && mChild.equals(children[0]);
		}

		public boolean hasChildren(List<? extends Vertex<L>> children) {
			return children.size() == 1 && mChild.equals(children.get(0));
		}
	}
	
	private final class MultiVertex extends CVertex {
		protected final Vertex<L>[] mChildren;
		
		public MultiVertex(L label, Vertex<L>... children) {
			super(label);
			mChildren = children;
			for (Vertex<L> child : mChildren)
				child.setParent(this);
		}

		public int getChildCount() {return 1;}
		
		public List<? extends Vertex<L>> getChildren() {
			return Arrays.asList(mChildren);
		}

		public Vertex<L> getChild(int child) {
			return mChildren[child];
		}

		public boolean hasChildren(Vertex<L>... children) {
			return Arrays.equals(mChildren, children);
		}

		public boolean hasChildren(List<? extends Vertex<L>> children) {
			return getChildren().equals(children);
		}
	}
}
