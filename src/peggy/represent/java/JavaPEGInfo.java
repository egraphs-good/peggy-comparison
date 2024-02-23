package peggy.represent.java;

import java.util.Collection;
import java.util.Map;

import peggy.represent.PEGInfo;
import util.graph.CRecursiveExpressionGraph;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;

/**
 * This is a PEGInfo specified for Java PEGs.
 */
public class JavaPEGInfo extends PEGInfo<JavaLabel,JavaParameter,JavaReturn> {
	public JavaPEGInfo(
			CRecursiveExpressionGraph<FlowValue<JavaParameter,JavaLabel>> _graph,
			Map<JavaReturn,Vertex<FlowValue<JavaParameter,JavaLabel>>> map) {
		super(_graph, map);
	}
	public Collection<? extends JavaReturn> getReturns() {
		return this.return2vertex.keySet();
	}
}
