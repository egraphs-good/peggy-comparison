package eqsat.meminfer.engine.basic;

import java.util.List;

import eqsat.meminfer.engine.op.OpTerm;
import util.UnhandledCaseException;
import util.graph.ExpressionGraph;

public class FutureExpressionGraph
		<O, T extends OpTerm<O,T,V>, V extends Value<T,V>>
		extends ExpressionGraph<FutureExpressionGraph<O,T,V>,
		FutureExpressionGraph.Vertex<O,T,V>,FutureExpressionGraph.Label<O,T,V>>{
	protected static abstract class Label<O,T,V> {
		public abstract boolean equals(Object that);
		public abstract int hashCode();
		
		public abstract String toString();
		
		public boolean isPlaceHolder() {return false;}
		public boolean isOp() {return false;}
		public O getOp() {throw new UnsupportedOperationException();}
		public boolean isRepresentative() {return false;}
		public Representative<V> getRepresentative() {
			throw new UnsupportedOperationException();
		}
	}
	
	protected static final class PlaceHolderLabel<O,T,V> extends Label<O,T,V> {
		public boolean equals(Object that) {return this == that;}
		public int hashCode() {return System.identityHashCode(this);}
		
		public String toString() {return "Place Holder";}

		public boolean isPlaceHolder() {return true;}
	}
	
	protected static final class OpLabel<O,T,V> extends Label<O,T,V> {
		private final O mOp;
		
		public OpLabel(O op) {mOp = op;}
		
		public boolean equals(Object that) {
			return that instanceof OpLabel && equals((OpLabel)that);
		}
		public boolean equals(OpLabel that) {
			return that != null && mOp.equals(that.mOp);
		}
		public int hashCode() {return mOp.hashCode();}
		
		public String toString() {
			return mOp == null ? "<null>" : mOp.toString();
		}
		
		public boolean isOp() {return true;}
		public O getOp() {return mOp;}
	}
	
	protected static final class RepresentativeLabel<O,T,V>
			extends Label<O,T,V> {
		private final Representative<V> mRepresentative;
		
		public RepresentativeLabel(Representative<V> representative) {
			mRepresentative = representative;
		}
		
		public boolean equals(Object that) {
			return that instanceof RepresentativeLabel
					&& equals((RepresentativeLabel)that);
		}
		public boolean equals(RepresentativeLabel that) {
			return that != null && mRepresentative.equals(that.mRepresentative);
		}
		public int hashCode() {return mRepresentative.hashCode();}
		
		public String toString() {return mRepresentative.getValue().toString();}
		
		public boolean isRepresentative() {return true;}
		public Representative<V> getRepresentative() {return mRepresentative;}
	}
	
	public interface Vertex<O,T extends OpTerm<O,T,V>,V extends Value<T,V>>
			extends ExpressionGraph.IVertex
			<FutureExpressionGraph<O,T,V>,Vertex<O,T,V>,Label<O,T,V>> {
		boolean isRepresentative();
		Representative<V> getRepresentative();
		boolean isFutureAmbassador();
		FutureAmbassador<O,T,V> getFutureAmbassador();
		boolean isFutureExpression();
		FutureExpression<O,T,V> getFutureExpression();
		
		Representative<V> getValue();
	}
	
	protected class RepresentativeVertex extends ExpressionGraph.Vertex
			<FutureExpressionGraph<O,T,V>,Vertex<O,T,V>,Label<O,T,V>>
			implements Vertex<O,T,V> {
		protected RepresentativeVertex(Label<O,T,V> label) {
			super(label);
			if (!label.isRepresentative())
				throw new IllegalArgumentException();
		}
		
		public FutureExpressionGraph<O,T,V> getGraph() {
			return FutureExpressionGraph.this;
		}
		public Vertex<O,T,V> getSelf() {return this;}
		
		public boolean isRepresentative() {return true;}
		public Representative<V> getRepresentative() {
			return getLabel().getRepresentative();
		}
		public boolean isFutureAmbassador() {return false;}
		public FutureAmbassador<O,T,V> getFutureAmbassador() {
			throw new UnsupportedOperationException();
		}
		public boolean isFutureExpression() {return false;}
		public FutureExpression<O,T,V> getFutureExpression() {
			throw new UnsupportedOperationException();
		}
		
		public Representative<V> getValue() {return getRepresentative();}
	}
	
	public FutureExpressionGraph<O,T,V> getSelf() {return this;}
	
	protected Vertex<O,T,V> makeVertex(Label<O,T,V> label) {
		if (label.isOp())
			return new FutureExpression<O,T,V>(this, label);
		else if (label.isPlaceHolder())
			return new FutureAmbassador<O,T,V>(this, label);
		else if (label.isRepresentative())
			return new RepresentativeVertex(label);
		else
			throw new UnhandledCaseException();
	}
	protected Vertex<O,T,V> makeVertex(Label<O,T,V> label, Vertex<O,T,V> child){
		return new FutureExpression<O,T,V>(this, label, child);
	}
	protected Vertex<O,T,V> makeVertex(Label<O,T,V> label,
			Vertex<O,T,V>... children) {
		return new FutureExpression<O,T,V>(this, label, children);
	}
	protected Vertex<O,T,V> makeVertex(Label<O,T,V> label,
			List<? extends Vertex<O,T,V>> children) {
		Vertex<O,T,V>[] array = new Vertex[children.size()];
		children.toArray(array);
		return new FutureExpression<O,T,V>(this, label, array);
	}
	
	public Vertex<O,T,V> getVertex(Representative<V> representative) {
		return getVertex(new RepresentativeLabel<O,T,V>(representative));
	}
	public FutureAmbassador<O,T,V> makePlaceHolder() {
		return (FutureAmbassador<O,T,V>)getVertex(
				new PlaceHolderLabel<O,T,V>());
	}
	public FutureExpression<O,T,V> getExpression(O op) {
		return (FutureExpression<O,T,V>)getVertex(new OpLabel<O,T,V>(op));
	}
	public FutureExpression<O,T,V> getExpression(O op, Vertex<O,T,V> child) {
		return (FutureExpression<O,T,V>)getVertex(new OpLabel<O,T,V>(op),
				child);
	}
	public FutureExpression<O,T,V> getExpression(O op,
			Vertex<O,T,V>... children) {
		return (FutureExpression<O,T,V>)getVertex(new OpLabel<O,T,V>(op),
				children);
	}
}
