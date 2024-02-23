package peggy.represent.java;

import java.util.HashMap;
import java.util.Map;

import peggy.analysis.java.ClassAnalysis;
import peggy.represent.PEGInfo;
import peggy.represent.PEGProvider;
import soot.SootClass;
import soot.SootMethod;
import util.graph.CRecursiveExpressionGraph;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.APEG;
import eqsat.Flow;
import eqsat.FlowValue;

/**
 * This is a PEGProvider that loads Java PEGs based on SootMethods.
 */
public abstract class SootMethodPEGProvider 
implements PEGProvider<SootMethod,JavaLabel,JavaParameter,JavaReturn> {
	public boolean canProvidePEG(SootMethod method) {
		return resolveMethod(method) != null;
	}
	protected SootMethod resolveMethod(SootMethod method) {
		SootClass clazz = method.getDeclaringClass();
		SootMethod resolved = ClassAnalysis.lookupMethod(
				clazz, method.getSubSignature());
		if (resolved == null)
			return null;
		if (!resolved.isConcrete())
			return null;
		if (SootUtils.hasExceptions(resolved))
			return null;
		return resolved;
	}

	public abstract JavaLabelOpAmbassador getAmbassador();

	public PEGInfo<JavaLabel,JavaParameter,JavaReturn> 
	getPEG(SootMethod method) {
		method = resolveMethod(method);
		if (method == null)
			throw new IllegalArgumentException("Cannot provide method!");
		
		ExpressionCFG2 cfg = new ExpressionCFG2(method, getAmbassador());
		
		Flow<ExpressionCFG2,ExpressionCFGBlock2,JavaVariable> flow = 
			new Flow<ExpressionCFG2,ExpressionCFGBlock2,JavaVariable>(cfg);
		APEG<ExpressionCFG2,ExpressionCFGBlock2,JavaVariable,JavaLabel,JavaParameter> apeg
		= new APEG<ExpressionCFG2,ExpressionCFGBlock2,JavaVariable,JavaLabel,JavaParameter>(cfg);
		flow.getReturn(apeg);
		flow = null; // last usage of flow
		
		final Map<JavaReturn,Vertex<FlowValue<JavaParameter,JavaLabel>>> map = 
			new HashMap<JavaReturn,Vertex<FlowValue<JavaParameter,JavaLabel>>>();

		Vertex<FlowValue<JavaParameter,JavaLabel>> valueVertex = 
			apeg.getReturn().evaluate(cfg.getReturnVariable(JavaReturn.VALUE)); 
		valueVertex.makeSignificant();
		map.put(JavaReturn.VALUE, valueVertex);
		
		Vertex<FlowValue<JavaParameter,JavaLabel>> sigmaVertex = 
			apeg.getReturn().evaluate(cfg.getReturnVariable(JavaReturn.SIGMA)); 
		sigmaVertex.makeSignificant();
		map.put(JavaReturn.SIGMA, sigmaVertex);
		
		CRecursiveExpressionGraph<FlowValue<JavaParameter,JavaLabel>> graph = 
			apeg.getValues();
		JavaPEGInfo info = new JavaPEGInfo(graph, map);
		
		return info;
	}
}
