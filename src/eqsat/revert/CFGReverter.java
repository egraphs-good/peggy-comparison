package eqsat.revert;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import eqsat.OpAmbassador;
import util.Function;

public final class CFGReverter<P, L, R> {
	private final RevertCFG<L,P,R> mCFG;
	private boolean mSound = true;
	
	public CFGReverter(ReversionGraph<P,L> graph,
			Map<R,ReversionGraph<P,L>.Vertex> returns,
			OpAmbassador<L> ambassador) {
		rewrite(graph);
		validate(graph);
		for (ReversionGraph<P,L>.Vertex vertex : graph.getVertices())
			vertex.setVariable(new Variable());
		final Map<R,Variable> returnMap = new HashMap<R,Variable>();
		for (Entry<R,ReversionGraph<P,L>.Vertex> entry : returns.entrySet())
			returnMap.put(entry.getKey(), entry.getValue().isRewritten() ?
					entry.getValue().getRewrite().getVariable()
					: entry.getValue().getVariable());
		mCFG = new RevertCFG<L,P,R>(returnMap.keySet(),
				new Function<R,Variable>() {
			public Variable get(R parameter) {return returnMap.get(parameter);}
		}, ambassador);
		FallBlock<P,L> body = new FallBlock<P,L>(this, graph);
		for (ReversionGraph<P,L>.Vertex vertex : returns.values())
			if (vertex.isRewritten())
				body.modify(vertex.getRewrite().getVariable(),
						vertex.getRewrite());
			else
				body.modify(vertex.getVariable(), vertex);
		mCFG.setStart(body.addToCFG(mCFG, mCFG.getEnd()));
		mCFG.simplify();
	}
	
	public RevertCFG<L,P,R> getCFG() {return mCFG;}
	public boolean isSound() {return mSound;}
	public void setUnsound() {mSound = false;}
	
	public static <P,L> void rewrite(ReversionGraph<P,L> graph) {
		graph.trimInsignificant();
	}
	
	public static <P,L> void validate(ReversionGraph<P,L> graph) {
		for (ReversionGraph<P,L>.Vertex vertex : graph.getSignificant())
			if (vertex.isVariant())
				throw new IllegalArgumentException(
						"Vertex is variant: " + vertex);
		for (ReversionGraph<P,L>.Vertex vertex : graph.getVertices()) {
			if (vertex.getHead().isEval()
					&& !vertex.getChild(1).getHead().isPass())
				throw new IllegalArgumentException();
			else if ((vertex.getHead().isTheta() || vertex.getHead().isPass())
					&& vertex.getMaxVariant() > vertex.getHead().getLoopDepth())
				throw new IllegalArgumentException();
			else if (vertex.getHead().isPass()) {
				int loop = vertex.getHead().getLoopDepth();
				for (ReversionGraph<P,L>.Vertex parent : vertex.getParents())
					if (!parent.getHead().isEval()
							|| parent.getHead().getLoopDepth() != loop)
						throw new IllegalArgumentException();
			}
		}
	}
}
