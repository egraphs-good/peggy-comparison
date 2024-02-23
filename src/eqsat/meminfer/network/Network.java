package eqsat.meminfer.network;

import static eqsat.meminfer.network.Network.ListLabel.Empty;
import static eqsat.meminfer.network.Network.ListLabel.Postpend;
import util.NamedTag;
import util.Tag;
import util.Taggable;
import util.graph.CExpressionGraph;
import util.graph.CExpressionGraph.Vertex;
import util.integer.ArrayIntMap;
import util.integer.IntMap;

public class Network {
	protected interface NetworkLabel {}
	
	protected enum ListLabel implements NetworkLabel {Empty, Postpend;}
	
	protected static final class IntLabel implements NetworkLabel {
		private static final IntMap<IntLabel> mInstances
				= new ArrayIntMap<IntLabel>(); //TODO make weak-valued
		
		private final int mInt;
		
		private IntLabel(int index) {mInt = index;}
		
		public static IntLabel get(int index) {
			IntLabel instance = mInstances.get(index);
			if (instance == null) {
				instance = new IntLabel(index);
				mInstances.put(index, instance);
			}
			return instance;
		}
		
		public String toString() {return Integer.toString(mInt);}
	}
	
	protected static final class StringLabel implements NetworkLabel {
		private final String mString;
		
		public StringLabel(String string) {mString = string;}
		
		public boolean equals(Object that) {
			return that instanceof StringLabel && equals((StringLabel)that);
		}
		public boolean equals(StringLabel that) {
			return mString == null ? that.mString == null
					: that.mString != null && mString.equals(that.mString);
		}
		public int hashCode() {return mString == null ? 0 : mString.hashCode();}
		
		public String toString() {return mString;}
	}
	
	protected static final class OpLabel<O> implements NetworkLabel {
		private final O mOp;
		
		public OpLabel(O op) {mOp = op;}
		
		public boolean equals(Object that) {
			return that instanceof OpLabel && equals((OpLabel)that);
		}
		public boolean equals(OpLabel that) {
			return that.mOp.equals(mOp);
		}
		public int hashCode() {return mOp == null ? 0 : mOp.hashCode();}
		
		public String toString() {
			return mOp == null ? "<null>" : mOp.toString();
		}
	}
	
	public static abstract class Node implements Taggable {
		private final Vertex<NetworkLabel> mVertex;
		
		protected Node(Vertex<NetworkLabel> vertex) {mVertex = vertex;}
		
		public final Vertex<NetworkLabel> getVertex() {return mVertex;}

		public final <T> T getTag(Tag<T> label) {return mVertex.getTag(label);}
		public final boolean hasTag(Tag label) {return mVertex.hasTag(label);}
		public final <T> T removeTag(Tag<T> label) {
			return mVertex.removeTag(label);
		}
		public final void setTag(Tag<Void> label) {mVertex.setTag(label);}
		public final <T> T setTag(Tag<T> label, T tag) {
			return mVertex.setTag(label, tag);
		}
		public final String tagsToString() {return mVertex.tagsToString();}
	}
	
	public static abstract class ListNode<N> extends Node {
		protected ListNode(Vertex<NetworkLabel> vertex) {super(vertex);}
		
		public boolean isEmpty() {return false;}
		public EmptyNode getEmpty() {
			throw new UnsupportedOperationException();
		}
		
		public boolean isPostpend() {return false;}
		public PostpendNode<N> getPostpend() {
			throw new UnsupportedOperationException();
		}
	}
	
	public static final class EmptyNode extends ListNode {
		protected static final Tag<EmptyNode> mTag = new NamedTag("Empty");
		
		protected EmptyNode(Vertex<NetworkLabel> vertex) {
			super(vertex);
			vertex.setTag(mTag, this);
		}
		
		public boolean isEmpty() {return true;}
		public EmptyNode getEmpty() {return this;}
	}
	
	public static final class PostpendNode<N> extends ListNode<N> {
		protected static final Tag<PostpendNode> mTag
				= new NamedTag("Postpend");
		
		private final ListNode<? extends N> mHead;
		private final N mTail;
		
		protected PostpendNode(Vertex<NetworkLabel> vertex,
				ListNode<? extends N> head, N tail) {
			super(vertex);
			vertex.setTag(mTag, this);
			mHead = head;
			mTail = tail;
		}
		
		public boolean isPostpend() {return true;}
		public PostpendNode<N> getPostpend() {return this;}
		
		public ListNode<? extends N> getHead() {return mHead;}
		public N getTail() {return mTail;}
	}

	private final CExpressionGraph<NetworkLabel> mGraph;
	
	public Network() {mGraph = new CExpressionGraph<NetworkLabel>();}
	protected Network(Network network) {mGraph = network.mGraph;}
	
	protected final CExpressionGraph<NetworkLabel> getGraph() {return mGraph;}
	
	public EmptyNode empty() {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(Empty);
		EmptyNode node = vertex.getTag(EmptyNode.mTag);
		return node == null ? new EmptyNode(vertex) : node;
	}
	
	public <N extends Node> PostpendNode<? extends N> postpend(
			ListNode<? extends N> head, N tail) {
		Vertex<NetworkLabel> vertex = getGraph().getVertex(Postpend,
				head.getVertex(), tail.getVertex());
		PostpendNode<? extends N> node = vertex.getTag(PostpendNode.mTag);
		return node == null ? new PostpendNode<N>(vertex, head, tail) : node;
	}
	
	public String toString() {return mGraph.toString();}
}
