package peggy.represent.llvm;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import peggy.represent.PEGInfo;
import util.graph.CRecursiveExpressionGraph;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;

/**
 * This is a PEGInfo that is specialized for LLVM PEGs.
 */
public class LLVMPEGInfo extends PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> {
	private static Map<LLVMReturn,Vertex<FlowValue<LLVMParameter,LLVMLabel>>> makeMap(
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> _valueReturnVertex,
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> _sigmaReturnVertex) {
		Map<LLVMReturn,Vertex<FlowValue<LLVMParameter,LLVMLabel>>> result = 
			new HashMap<LLVMReturn,Vertex<FlowValue<LLVMParameter,LLVMLabel>>>();
		result.put(LLVMReturn.VALUE, _valueReturnVertex);
		result.put(LLVMReturn.SIGMA, _sigmaReturnVertex);
		return result;
	}
	private static final Set<LLVMReturn> returns; 
	static {
		Set<LLVMReturn> temp = 
			new HashSet<LLVMReturn>();		
		temp.add(LLVMReturn.VALUE);
		temp.add(LLVMReturn.SIGMA);
		returns = Collections.unmodifiableSet(temp);
	}
	public LLVMPEGInfo(
			CRecursiveExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>> _graph,
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> _valueReturnVertex,
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> _sigmaReturnVertex) {
		super(_graph, makeMap(_valueReturnVertex, _sigmaReturnVertex));
	}
	public Collection<? extends LLVMReturn> getReturns() {
		return returns;
	}
}
