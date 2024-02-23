package peggy.represent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import util.graph.CRecursiveExpressionGraph;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.OpAmbassador;

/**
 * Implementation of XML2PEG that uses a CREG as its PEG representation.
 */
public abstract class CREGXML2PEG<L,P,R>
extends XML2PEG<CRecursiveExpressionGraph<FlowValue<P,L>>,Vertex<FlowValue<P,L>>,R> {
	protected abstract PEGInfo<L,P,R> getPEGInfo(
			CRecursiveExpressionGraph<FlowValue<P,L>> graph,
			Map<R,Vertex<FlowValue<P,L>>> outputs);
	
	public PEGInfo<L,P,R> parsePEGInfo(Element docroot) {
		Map<R,Vertex<FlowValue<P,L>>> outputs = 
			new HashMap<R,Vertex<FlowValue<P,L>>>();
		CRecursiveExpressionGraph<FlowValue<P,L>> graph = 
			this.parsePEG(docroot, outputs);
		return this.getPEGInfo(graph, outputs);
	}
	
	protected Vertex<FlowValue<P,L>> getPlaceHolder(
			CRecursiveExpressionGraph<FlowValue<P,L>> graph) {
		return graph.createPlaceHolder();
	}
	protected CRecursiveExpressionGraph<FlowValue<P,L>> getFreshGraph() {
		return new CRecursiveExpressionGraph<FlowValue<P,L>>();
	}
	protected void replaceWith(
			Vertex<FlowValue<P,L>> holder,
			Vertex<FlowValue<P,L>> replacement) {
		holder.replaceWith(replacement);
	}

//	protected Vertex<FlowValue<P, L>> getSplitNode(
//			CRecursiveExpressionGraph<FlowValue<P, L>> graph,
//			List<Vertex<FlowValue<P, L>>> children) {
//		return graph.getVertex(FlowValue.<P,L>createSplit(), children);
//	}
	
	protected Vertex<FlowValue<P, L>> getAndNode(
			CRecursiveExpressionGraph<FlowValue<P, L>> graph,
			List<Vertex<FlowValue<P, L>>> children) {
		return graph.getVertex(FlowValue.<P,L>createAnd(), children);
	}

	protected Vertex<FlowValue<P, L>> getEqualsNode(
			CRecursiveExpressionGraph<FlowValue<P, L>> graph,
			List<Vertex<FlowValue<P, L>>> children) {
		return graph.getVertex(FlowValue.<P,L>createEquals(), children);
	}

	protected Vertex<FlowValue<P, L>> getEvalNode(
			CRecursiveExpressionGraph<FlowValue<P, L>> graph, int index,
			List<Vertex<FlowValue<P, L>>> children) {
		return graph.getVertex(FlowValue.<P,L>createEval(index), children);
	}

	protected Vertex<FlowValue<P, L>> getNegateNode(
			CRecursiveExpressionGraph<FlowValue<P, L>> graph,
			List<Vertex<FlowValue<P, L>>> children) {
		return graph.getVertex(FlowValue.<P,L>createNegate(), children);
	}

	protected Vertex<FlowValue<P, L>> getOrNode(
			CRecursiveExpressionGraph<FlowValue<P, L>> graph,
			List<Vertex<FlowValue<P, L>>> children) {
		return graph.getVertex(FlowValue.<P,L>createOr(), children);
	}

	protected Vertex<FlowValue<P,L>> getLabelNode(
			CRecursiveExpressionGraph<FlowValue<P, L>> graph, 
			L label) {
		return graph.getVertex(FlowValue.<P,L>createDomain(label, this.getAmbassador()));
	}
	protected Vertex<FlowValue<P,L>> getLabelNode(
			CRecursiveExpressionGraph<FlowValue<P, L>> graph, 
			L label,
			List<Vertex<FlowValue<P,L>>> children) {
		return graph.getVertex(
				FlowValue.<P,L>createDomain(label, this.getAmbassador()), 
				children);
	}
			
	protected abstract OpAmbassador<L> getAmbassador();
	protected abstract P getParam(String paramname);

	protected Vertex<FlowValue<P, L>> getParamNode(
			CRecursiveExpressionGraph<FlowValue<P, L>> graph, 
			String paramname) {
		P param = this.getParam(paramname);
		return graph.getVertex(FlowValue.<P,L>createParameter(param));
	}

	protected Vertex<FlowValue<P, L>> getPassNode(
			CRecursiveExpressionGraph<FlowValue<P, L>> graph, int index,
			List<Vertex<FlowValue<P, L>>> children) {
		return graph.getVertex(FlowValue.<P,L>createPass(index), children);
	}

	protected Vertex<FlowValue<P, L>> getPhiNode(
			CRecursiveExpressionGraph<FlowValue<P, L>> graph,
			List<Vertex<FlowValue<P, L>>> children) {
		return graph.getVertex(FlowValue.<P,L>createPhi(), children);
	}

	protected Vertex<FlowValue<P, L>> getShiftNode(
			CRecursiveExpressionGraph<FlowValue<P, L>> graph, int index,
			List<Vertex<FlowValue<P, L>>> children) {
		return graph.getVertex(FlowValue.<P,L>createShift(index), children);
	}

	protected Vertex<FlowValue<P, L>> getSuccessorNode(
			CRecursiveExpressionGraph<FlowValue<P, L>> graph,
			List<Vertex<FlowValue<P, L>>> children) {
		return graph.getVertex(FlowValue.<P,L>createSuccessor(), children);
	}

	protected Vertex<FlowValue<P, L>> getThetaNode(
			CRecursiveExpressionGraph<FlowValue<P, L>> graph, int index,
			List<Vertex<FlowValue<P, L>>> children) {
		return graph.getVertex(FlowValue.<P,L>createTheta(index), children);
	}

	protected Vertex<FlowValue<P, L>> getZeroNode(
			CRecursiveExpressionGraph<FlowValue<P, L>> graph) {
		return graph.getVertex(FlowValue.<P,L>createZero());
	}
}
