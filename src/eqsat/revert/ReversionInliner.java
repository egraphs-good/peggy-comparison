package eqsat.revert;

public abstract class ReversionInliner<P,L>
		extends RecursiveInliner<Value<P,L>,ReversionGraph<P,L>.Vertex,
		ReversionGraph<P,L>,ReversionGraph<P,L>.Vertex> {
	public ReversionInliner(ReversionGraph<P,L> graph) {super(graph);}
	
	public ReversionGraph<P,L>.Vertex
			shallowCopy(ReversionGraph<P,L>.Vertex vertex) {
		ReversionGraph<P,L>.Vertex result = super.shallowCopy(vertex);
		if (!result.hasVariable())
			result.setVariable(vertex.getVariable());
		return result;
	}
}
