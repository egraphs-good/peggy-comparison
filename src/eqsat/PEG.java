package eqsat;

import java.util.List;

import util.graph.RecursiveExpressionGraph;

public class PEG<O, P>
		extends RecursiveExpressionGraph<PEG<O,P>,PEG.PE<O,P>,FlowValue<P,O>> {
	public interface PE<O,P> extends RecursiveExpressionGraph
			.IVertex<PEG<O,P>,PE<O,P>,FlowValue<P,O>> {}
	
	protected class HolderVertex extends RecursiveExpressionGraph.HolderVertex
			<PEG<O,P>,PE<O,P>,FlowValue<P,O>> implements PE<O,P> {
		public HolderVertex(FlowValue<P,O> label) {super(label);}
		
		public PE<O,P> getSelf() {return this;}

		public PEG<O,P> getGraph() {return PEG.this;}
	}
	
	protected class NormalVertex extends RecursiveExpressionGraph.Vertex
			<PEG<O,P>,PE<O,P>,FlowValue<P,O>> implements PE<O,P> {
		public NormalVertex(FlowValue<P,O> label) {super(label);}
		public NormalVertex(FlowValue<P,O> label, PE<O,P> child) {
			super(label, child);
		}
		public NormalVertex(FlowValue<P,O> label, PE<O,P>... children) {
			super(label, children);
		}
		
		public PE<O,P> getSelf() {return this;}

		public PEG<O,P> getGraph() {return PEG.this;}
	}
	
	private final OpAmbassador<O> mOpAmbassador;
	
	public PEG(OpAmbassador<O> ambassador) {mOpAmbassador = ambassador;}
	
	public PEG<O,P> getSelf() {return this;}
	
	public OpAmbassador<O> getOpAmbassador() {return mOpAmbassador;}
	
	public PE<O,P> getParameter(P parameter) {
		return getVertex(FlowValue.<P,O>createParameter(parameter));
	}
	public PE<O,P> getDomain(O op) {
		return getVertex(FlowValue.<P,O>createDomain(op, mOpAmbassador));
	}
	public PE<O,P> getDomain(O op, PE<O,P> child) {
		return getVertex(FlowValue.<P,O>createDomain(op, mOpAmbassador), child);
	}
	public PE<O,P> getDomain(O op, PE<O,P>... children) {
		return getVertex(FlowValue.<P,O>createDomain(op, mOpAmbassador),
				children);
	}
	public PE<O,P> getDomain(O op, List<? extends PE<O,P>> children) {
		return getVertex(FlowValue.<P,O>createDomain(op, mOpAmbassador),
				children);
	}
	public PE<O,P> getTrue() {
		return getVertex(FlowValue.<P,O>createTrue());
	}
	public PE<O,P> getFalse() {
		return getVertex(FlowValue.<P,O>createFalse());
	}
	public PE<O,P> getNegate(PE<O,P> child) {
		return getVertex(FlowValue.<P,O>createNegate(), child);
	}
	public PE<O,P> getPhi(PE<O,P> condition,
			PE<O,P> trueCase, PE<O,P> falseCase) {
		return getVertex(FlowValue.<P,O>createPhi(), condition,
				trueCase, falseCase);
	}
	public PE<O,P> getTheta(int depth, PE<O,P> base, PE<O,P> shift) {
		return getVertex(FlowValue.<P,O>createTheta(depth), base, shift);
	}
	public PE<O,P> getShift(int depth, PE<O,P> child) {
		return getVertex(FlowValue.<P,O>createShift(depth), child);
	}
	public PE<O,P> getEval(int depth, PE<O,P> value, PE<O,P> index) {
		return getVertex(FlowValue.<P,O>createEval(depth), value, index);
	}
	public PE<O,P> getPass(int depth, PE<O,P> condition) {
		return getVertex(FlowValue.<P,O>createPass(depth), condition);
	}
	
	protected PE<O,P> makeHolderVertex() {return new HolderVertex(null);}
	protected PE<O,P> makeHolderVertex(FlowValue<P,O> label) {
		return new HolderVertex(label);
	}
	
	protected PE<O,P> makeVertex(FlowValue<P,O> label) {
		return new NormalVertex(label);
	}
	protected PE<O,P> makeVertex(FlowValue<P,O> label, PE<O,P> child) {
		return new NormalVertex(label, child);
	}
	protected PE<O,P> makeVertex(FlowValue<P,O> label, PE<O,P>... children) {
		return new NormalVertex(label, children);
	}
	protected PE<O,P> makeVertex(FlowValue<P,O> label,
			List<? extends PE<O,P>> children) {
		PE<O,P>[] array = new PE[children.size()];
		children.toArray(array);
		return new NormalVertex(label, array);
	}
}
