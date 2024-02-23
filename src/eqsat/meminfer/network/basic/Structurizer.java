package eqsat.meminfer.network.basic;

import java.util.Collection;
import java.util.Iterator;

import eqsat.meminfer.network.basic.StructureNetwork.RepresentativeNode;
import eqsat.meminfer.network.basic.StructureNetwork.StructureNode;
import eqsat.meminfer.network.basic.StructureNetwork.TermNode;
import eqsat.meminfer.network.basic.TermValueNetwork.TermValueNode;
import eqsat.meminfer.network.basic.ValueNetwork.ValueNode;
import util.ArrayCollection;
import util.NamedTag;
import util.Tag;
import util.Taggable;
import util.graph.CMLabeledOrderedTree;
import util.graph.OrderedVertex;
import util.graph.CMLabeledOrderedTree.Vertex;
import util.integer.BitIntSet;
import util.integer.PairInt;

public abstract class Structurizer
		<V extends Taggable & OrderedVertex<?,? extends V>> {
	private final CMLabeledOrderedTree<StructureNode> mJoins
			= new CMLabeledOrderedTree<StructureNode>();
	private final Collection<V> mVertices = new ArrayCollection<V>();
	private final Tag<Vertex<StructureNode>> mExpressionTag
			= new NamedTag<Vertex<StructureNode>>("Expression");
	private final Tag<PairInt<V>> mChildTag = new NamedTag("Child");
	private final Tag<Void> mAnyArity = new NamedTag("Any Arity");
	
	protected TermNode getSource(V vertex) {return getNetwork().general();}
	
	protected boolean checkArity(V vertex) {
		return !vertex.hasTag(mAnyArity);
	}
	public void allowAnyArity(V vertex) {
		if (!vertex.isLeaf())
			throw new IllegalArgumentException();
		vertex.setTag(mAnyArity);
	}

	protected final TermNode getTerm(V vertex) {
		return checkArity(vertex) ?  getNetwork().checkArity(
				vertex.getChildCount(), getSource(vertex))
				: getSource(vertex);
	}
	
	public abstract StructureNetwork getNetwork();
	public Collection<? extends V> getVertices() {return mVertices;}
	
	public StructureNode addVertex(V vertex) {
		if (vertex.hasTag(mExpressionTag))
			return getStructure(vertex);
		mVertices.add(vertex);
		TermNode term = getTerm(vertex);
		BitIntSet skip = new BitIntSet();
		for (int i = 0; i < vertex.getChildCount(); i++)
			for (int j = i; ++j < vertex.getChildCount(); )
				if (vertex.getChild(i).equals(vertex.getChild(j))) {
					term = getNetwork().checkChildEquality(i, j, term);
					skip.add(i);
					break;
				}
		Vertex<StructureNode> node = mJoins.makeVertex(term);
		Vertex<StructureNode> root = node;
		StructureNode expression;
		if (vertex.hasTag(mChildTag)) {
			expression = getNetwork().join(getRepresentative(vertex),
					getNetwork().adaptTerm(term));
			root = mJoins.makeVertex(expression, getRoot(
					vertex.getTag(mChildTag).getFirst()), root);
			vertex.removeTag(mChildTag);
		} else
			expression = term;
		vertex.setTag(mExpressionTag, node);
		for (int i = 0; i < vertex.getChildCount(); i++) {
			if (skip.contains(i))
				continue;
			V child = vertex.getChild(i);
			if (child.hasTag(mExpressionTag)) {
				Vertex<StructureNode> that = getRoot(child);
				if (that.equals(root)) {
					expression = getNetwork().checkEquality(getValue(child),
							getNetwork().childValue(i, getTermValue(vertex)),
							expression);
					root.setLabel(expression);
				} else {
					expression = getNetwork().join(getRepresentative(child),
							getNetwork().represent(
							getNetwork().childValue(i, getTermValue(vertex)),
							expression));
					root = mJoins.makeVertex(expression, that, root);
				}
			} else if (child.hasTag(mChildTag)) {
				Vertex<StructureNode> that
						= getRoot(child.getTag(mChildTag).getFirst());
				if (that.equals(root)) {
					expression = getNetwork().checkEquality(
							getValue(child),
							getNetwork()
							.childValue(i, getTermValue(vertex)), expression);
					root.setLabel(expression);
				} else {
					expression = getNetwork().join(getRepresentative(child),
							getNetwork().represent(
							getNetwork().childValue(i, getTermValue(vertex)),
							expression));
					root = mJoins.makeVertex(expression, that, root);
				}
				child.setTag(mChildTag, new PairInt(vertex, i));
			} else
				child.setTag(mChildTag, new PairInt(vertex, i));
		}
		return expression;
	}

	public boolean isAdded(V vertex) {return vertex.hasTag(mExpressionTag);}
	public boolean isConnected(V vertex) {
		return isAdded(vertex) || vertex.hasTag(mChildTag);
	}
	
	public TermValueNode getTermValue(V vertex) {
		return getNetwork().componentValue(getComponentIndex(vertex));
	}
	
	private int getComponentIndex(V vertex) {
		if (!vertex.hasTag(mExpressionTag))
			throw new IllegalArgumentException();
		Vertex<StructureNode> node = vertex.getTag(mExpressionTag);
		int index = 0;
		while (!node.isRoot()) {
			Vertex<StructureNode> parent = node.getParent();
			if (parent.getChild(0).equals(node))
				;
			else if (parent.getChild(1).equals(node))
				index += getLeafCount(parent.getChild(0));
			else
				throw new RuntimeException();
			node = parent;
		}
		return index;
	}
	
	public ValueNode getValue(V vertex) {
		if (vertex.hasTag(mExpressionTag))
			return getNetwork().adaptTermValue(getTermValue(vertex));
		else if (vertex.hasTag(mChildTag))
			return getNetwork().childValue(
					vertex.getTag(mChildTag).getSecond(),
					getTermValue(vertex.getTag(mChildTag).getFirst()));
		else
			throw new IllegalArgumentException();
	}
	
	private RepresentativeNode getRepresentative(V vertex) {
		return getNetwork().represent(getValue(vertex), getStructure(vertex));
	}
	
	private StructureNode getStructure(V vertex) {
		return getRoot(vertex).getLabel();
	}
	
	private Vertex<StructureNode> getRoot(V vertex) {
		Vertex<StructureNode> node;
		if (vertex.hasTag(mExpressionTag))
			node = vertex.getTag(mExpressionTag);
		else if (vertex.hasTag(mChildTag))
			node = vertex.getTag(mChildTag).getFirst().getTag(mExpressionTag);
		else
			throw new IllegalArgumentException();
		while (!node.isRoot())
			node = node.getParents().iterator().next();
		return node;
	}
	
	public StructureNode getStructure() {
		Collection<? extends Vertex<StructureNode>> roots = mJoins.getRoots();
		if (roots.size() == 2) {
			Iterator<? extends Vertex<StructureNode>> itr = roots.iterator();
			Vertex<StructureNode> left = itr.next();
			Vertex<StructureNode> right = itr.next();
			return mJoins.makeVertex(
					getNetwork().productJoin(left.getLabel(), right.getLabel()),
					left, right).getLabel();
		} else if (roots.size() != 1)
			throw new IllegalStateException();
		return roots.iterator().next().getLabel();
	}
	
	protected final int getLeafCount(Vertex<?> vertex) {
		if (vertex.isLeaf())
			return 1;
		else {
			int count = 0;
			for (Vertex<?> child : vertex.getChildren())
				count += getLeafCount(child);
			return count;
		}
	}
}