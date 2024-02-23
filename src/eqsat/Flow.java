package eqsat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import util.graph.GenericGraph;
import util.integer.BitIntSet;

public final class Flow<G,
		B extends Block<? extends CFG<?,?,? extends V,?,?,?>,? extends B,V,?>,
		V>
		extends GenericGraph<Flow<G,B,V>,FlowVertex<B,V>> {
	private final boolean mSimpleBreaks;
	
	public Flow(CFG<?,B,? extends V,?,?,?> cfg, boolean simpleBreaks) {
		super(new ArrayList<FlowVertex<B,V>>());
		mSimpleBreaks = simpleBreaks;
		Map<B,FlowVertex<B,V>> flowMap = new HashMap<B,FlowVertex<B,V>>();
		for (B block : cfg.getVertices()) {
			FlowVertex<B,V> vertex = new FlowVertex<B,V>(this, block);
			flowMap.put(block, vertex);
			addVertex(vertex);
		}
		Stack<FlowVertex<B,V>> stack = new Stack<FlowVertex<B,V>>();
		build(flowMap, flowMap.get(cfg.getStart()), stack);
		doDominators(flowMap.get(cfg.getEnd()));
		doLoops();
	}
	public Flow(CFG<?,B,? extends V,?,?,?> cfg) {this(cfg, true);}
	
	public Flow<G,B,V> getSelf() {return this;}
	
	public boolean usingSimpleBreaks() {return mSimpleBreaks;}
	
	private FlowVertex<B,V> build(Map<B,FlowVertex<B,V>> flowMap,
			FlowVertex<B,V> vertex, Stack<FlowVertex<B,V>> stack) {
		if (stack.contains(vertex)) {
			if (vertex.isLoopRoot())
				vertex = vertex.getLoopBack();
			else
				addVertex(vertex = new FlowVertex<B,V>(this, vertex));
			return vertex;
		}
		if (vertex.getChildCount() != 0)
			return vertex;
		stack.push(vertex);
		for (B child : vertex.getBlock().getChildren())
			vertex.addChild(build(flowMap, flowMap.get(child), stack));
		stack.pop();
		return vertex;
	}
	
	private List<FlowVertex<B,V>> getVertexList() {
		return (List<FlowVertex<B,V>>)mVertices;
	}
	public FlowVertex<B,V> getVertex(int orderIndex) {
		return getVertexList().get(orderIndex);
	}
	public FlowVertex<B,V> getStart() {return getVertexList().get(0);}
	
	private void doDominators(FlowVertex<B,V> end) {
		int orderIndex = end.doDominators(0);
		for (FlowVertex<B,V> vertex : mVertices)
			if (vertex.isLoopBack())
				orderIndex = vertex.doDominators(orderIndex);
		Collections.sort(getVertexList());
	}
	
	private void doLoops() {
		for (FlowVertex<B,V> vertex : getVertices())
			vertex.doLoops();
	}
	
	public <H, L, P, A extends APEG<H,B,V,L,P>> A.Node getReturn(A apeg) {
		BitIntSet work = new BitIntSet();
		for (FlowVertex<B,V> vertex : getVertices())
			if (!vertex.isLoopBack())
				apeg.getBlock(vertex.getBlock()).setChild(
						vertex.getInput(apeg, work));
		return apeg.getReturn();
	}
}
