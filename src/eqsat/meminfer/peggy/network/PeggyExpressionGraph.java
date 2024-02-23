package eqsat.meminfer.peggy.network;

import java.util.List;

import eqsat.FlowValue;
import util.graph.RecursiveExpressionGraph;

public class PeggyExpressionGraph<O,P>
		extends RecursiveExpressionGraph
		<PeggyExpressionGraph<O,P>,PeggyVertex<O,P>,FlowValue<P,O>> {
	protected class HolderVertex extends RecursiveExpressionGraph.HolderVertex
			<PeggyExpressionGraph<O,P>,PeggyVertex<O,P>,FlowValue<P,O>>
			implements PeggyVertex<O,P> {
		public HolderVertex(FlowValue<P,O> label) {super(label);}
		
		public PeggyVertex<O,P> getSelf() {return this;}

		public PeggyExpressionGraph<O,P> getGraph() {
			return PeggyExpressionGraph.this;
		}
	}
	
	protected class NormalVertex extends RecursiveExpressionGraph.Vertex
			<PeggyExpressionGraph<O,P>,PeggyVertex<O,P>,FlowValue<P,O>>
			implements PeggyVertex<O,P> {
		public NormalVertex(FlowValue<P,O> label) {super(label);}
		public NormalVertex(FlowValue<P,O> label, PeggyVertex<O,P> child) {
			super(label, child);
		}
		public NormalVertex(FlowValue<P,O> label,
				PeggyVertex<O,P>... children) {
			super(label, children);
		}
		
		public PeggyVertex<O,P> getSelf() {return this;}

		public PeggyExpressionGraph<O,P> getGraph() {
			return PeggyExpressionGraph.this;
		}
	}
	
	public PeggyExpressionGraph<O,P> getSelf() {return this;}
	
	protected PeggyVertex<O,P> makeHolderVertex() {
		return new HolderVertex(null);
	}
	protected PeggyVertex<O,P> makeHolderVertex(FlowValue<P,O> label) {
		return new HolderVertex(label);
	}
	
	protected PeggyVertex<O,P> makeVertex(FlowValue<P,O> label) {
		return new NormalVertex(label);
	}
	protected PeggyVertex<O,P> makeVertex(FlowValue<P,O> label,
			PeggyVertex<O,P> child) {
		return new NormalVertex(label, child);
	}
	protected PeggyVertex<O,P> makeVertex(FlowValue<P,O> label,
			PeggyVertex<O,P>... children) {
		return new NormalVertex(label, children);
	}
	protected PeggyVertex<O,P> makeVertex(FlowValue<P,O> label,
			List<? extends PeggyVertex<O,P>> children) {
		PeggyVertex<O,P>[] array = new PeggyVertex[children.size()];
		children.toArray(array);
		return new NormalVertex(label, array);
	}
}
