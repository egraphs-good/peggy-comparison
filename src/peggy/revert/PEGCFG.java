package peggy.revert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import peggy.revert.MiniPEG.Vertex;
import util.AbstractVariaticFunction;
import util.Action;
import util.Function;
import eqsat.CFG;
import eqsat.CFGTranslator;

/**
 * A PEGCFG is a block-based PEG-like representation of a CFG.
 */
public abstract class PEGCFG<L,P,R,T,X extends PEGCFG<L,P,R,T,X,Y>,Y extends PEGCFGBlock<L,P,R,T,X,Y>> {
	protected final Map<R,T> returnMap = new HashMap<R,T>();
	protected Y startBlock, endBlock;
	
	protected <G extends CFG<G,B,V,L,P,R>,B extends eqsat.Block<G,B,V,L>,V> PEGCFG(
			CFG<G,B,V,L,P,R> graph) {
		this.<G,B,V>buildBlocks(graph);
	}
	
	private <G extends CFG<G,B,V,L,P,R>,B extends eqsat.Block<G,B,V,L>,V> void buildBlocks(
			CFG<G,B,V,L,P,R> graph) {
		Map<B,Y> blockmap = new HashMap<B,Y>();
		Map<V,T> varmap = new HashMap<V,T>();

		// map the variables
		for (V var : graph.getVariables()) {
			varmap.put(var, makeNewTemporary());
		}
		
		// make the blocks
		for (B oldblock : graph.getVertices()) {
			Y newblock = makeNewBlock();
			blockmap.put(oldblock, newblock);
			this.<G,B,V>convertBlock(graph, oldblock, newblock, varmap);
		}
		
		// fill in edges
		for (B oldblock : graph.getVertices()) {
			Y newblock = blockmap.get(oldblock);
			for (B succ : oldblock.getChildren()) {
				newblock.addSucc(blockmap.get(succ));
			}
		}
		
		this.startBlock = blockmap.get(graph.getStart());
		this.endBlock = blockmap.get(graph.getEnd());
		
		if (this.startBlock == null || this.endBlock == null)
			throw new NullPointerException();
		
		// fill in returnMap
		for (R ret : graph.getReturns()) {
			V retvar = graph.getReturnVariable(ret);
			T newretvar = varmap.get(retvar);
			this.returnMap.put(ret, newretvar);
		}
	}
	
	private <G extends CFG<G,B,V,L,P,R>,B extends eqsat.Block<G,B,V,L>,V> 
	void convertBlock(
			CFG<G,B,V,L,P,R> graph, B oldblock, Y newblock, Map<V,T> varmap) {
		MiniPEG<Item<L,P,T>> model
		= new MiniPEG<Item<L,P,T>>();
		CFGTranslator<B,V,Vertex<Item<L,P,T>>> translator = 
			graph.getTranslator(
					new ParamConverter(model), 
					new Converter(model), 
					new ArrayList<Vertex<Item<L,P,T>>>());

		Function<V,Vertex<Item<L,P,T>>> outputs = 
			translator.getOutputs(oldblock, new Inputs<V>(model, varmap));
		

		for (V var : graph.getVariables()) {
			if (oldblock.modifies(var)) {
				Vertex<Item<L,P,T>> vertex = outputs.get(var);
				T tvar = varmap.get(var);
				newblock.setAssignment(tvar, vertex);
			}
		}
		
		Vertex<Item<L,P,T>> branchcondition = null;
		try {
			branchcondition = outputs.get(null);
		} catch (UnsupportedOperationException uoe) {}
		if (branchcondition!=null)
			newblock.setBranchCondition(branchcondition);
	}
	
	public abstract X getSelf();
	protected abstract Y makeNewBlock();
	protected abstract T makeNewTemporary();

	public final T getReturnVariable(R r) {return this.returnMap.get(r);}
	public final Collection<T> getReturns() {return this.returnMap.values();}
	public final Y getStartBlock() {return this.startBlock;}
	public final void setStartBlock(Y block) {this.startBlock = block;}
	public final Y getEndBlock() {return this.endBlock;}
	public final void setEndBlock(Y block) {this.endBlock = block;}
	public final Collection<Y> getBlocks() {
		Set<Y> seen = new HashSet<Y>();
		LinkedList<Y> worklist = new LinkedList<Y>();
		worklist.add(this.startBlock);
		
		while (worklist.size() > 0) {
			Y next = worklist.removeFirst();
			if (seen.contains(next))
				continue;
			seen.add(next);
			for (int i = 0; i < next.getNumSuccs(); i++) {
				worklist.addLast(next.getSucc(i));
			}
		}
		return seen;
	}
	
	/**
	 * Returns the predecessors of the given block.
	 */
	public Set<Y> getPreds(Y block) {
		Set<Y> result = new HashSet<Y>();
		for (Y p : this.getBlocks()) {
			for (Y s : p.getSuccs()) {
				if (s.equals(block)) {
					result.add(p);
					break;
				}
			}
		}
		return result;
	}

	////////////////////////////////
	
	private class ParamConverter implements Function<P,Vertex<Item<L,P,T>>> {
		private MiniPEG<Item<L,P,T>> model;
		public ParamConverter(MiniPEG<Item<L,P,T>> _model) {
			this.model = _model;
		}
		public Vertex<Item<L,P,T>> get(P p) {
			return model.getVertex(Item.<L,P,T>getParameter(p));
		}
	}
	
	private class Converter extends AbstractVariaticFunction<L,
															 Vertex<Item<L,P,T>>,
															 Vertex<Item<L,P,T>>> {
		private MiniPEG<Item<L,P,T>> model;
		public Converter(MiniPEG<Item<L,P,T>> _model) {
			this.model = _model;
		}
		public Vertex<Item<L,P,T>> get(
				L label, 
				List<? extends Vertex<Item<L,P,T>>> children) {
			return model.getVertex(Item.<L,P,T>getLabel(label), children);
		}
	}
	
	private class Inputs<V> implements Function<V,Vertex<Item<L,P,T>>> {
		private final MiniPEG<Item<L,P,T>> model;
		private final Map<V,T> varmap;
		public Inputs(
				MiniPEG<Item<L,P,T>> _model,
				Map<V,T> _varmap) {
			this.model = _model;
			this.varmap = _varmap;
		}
		public Vertex<Item<L,P,T>> get(V v) {
			T t = this.varmap.get(v);
			if (t==null)
				throw new NullPointerException("no mapping for V " + v);
			return this.model.getVertex(Item.<L,P,T>getVariable(t));
		}
	}
	
	/////////////////////////
	
	public String toString() {
		final StringBuilder string = new StringBuilder(
				"digraph {\nordering=out;\n");
		for (final Y block : getBlocks()) {
			string.append("subgraph cluster");
			string.append(block.hashCode());
			string.append(" {\nstart");
			string.append(block.hashCode());
			string.append(" [rank=max,shape=point];\nend");
			string.append(block.hashCode());
			string.append(" [rank=min,shape=point];\n");
			Action<Vertex<Item<L,P,T>>> print
					= new Action<Vertex<Item<L,P,T>>>() {
				private Set<Vertex<Item<L,P,T>>> printed = 
					new HashSet<Vertex<Item<L,P,T>>>();
				
				public void execute(Vertex<Item<L,P,T>> vertex) {
					if (printed.contains(vertex))
						return;
					printed.add(vertex);
					if (vertex.getLabel().isVariable()) {
						String color = 
							(returnMap.values().contains(vertex.getLabel().getVariable()) ? 
						     "red" : 
						     "blue");
						
						string.append("get");
						string.append(vertex.getLabel());
						string.append("block");
						string.append(block.hashCode());
						string.append(" [rank=min,color=").append(color).append(",label=\"");
						string.append("Get ");
						string.append(vertex.getLabel());
						string.append("\"];\n");
						string.append("get");
						string.append(vertex.getLabel());
						string.append("block");
						string.append(block.hashCode());
						string.append(" -> end");
						string.append(block.hashCode());
						string.append(" [style=invis];\n");
						return;
					}
					string.append("node");
					string.append(vertex.hashCode());
					string.append("block");
					string.append(block.hashCode());
					string.append(" [label=\"");
					string.append(vertex.getLabel());
					string.append("\"];\n");
					for (Vertex<Item<L,P,T>> child
							: vertex.getChildren()) {
						execute(child);
						string.append("node");
						string.append(vertex.hashCode());
						string.append("block");
						string.append(block.hashCode());
						string.append(" -> ");
						if (child.getLabel().isVariable()) {
							string.append("get");
							string.append(child.getLabel());
							string.append("block");
							string.append(block.hashCode());
						} else {
							string.append("node");
							string.append(child.hashCode());
							string.append("block");
							string.append(block.hashCode());
						}
						string.append(";\n");
					}
					return;
				}
			};
			for (Entry<T,Vertex<Item<L,P,T>>> entry
					: block.assignments.entrySet()) {
				String color = 
					(this.returnMap.values().contains(entry.getKey()) ? 
				     "red" : 
				     "green");
				
				string.append("set");
				string.append(entry.getKey().hashCode());
				string.append("block");
				string.append(block.hashCode());
				string.append(" [rank=max,color=").append(color).append(",label=\"Set ");
				string.append(entry.getKey().hashCode());
				string.append("\"];\n");
				string.append("start");
				string.append(block.hashCode());
				string.append(" -> set");
				string.append(entry.getKey().hashCode());
				string.append("block");
				string.append(block.hashCode());
				string.append(" [style=invis];\n");
				string.append("set");
				string.append(entry.getKey().hashCode());
				string.append("block");
				string.append(block.hashCode());
				string.append(" -> ");
				if (entry.getValue().getLabel().isVariable()) {
					string.append("get").append(entry.getValue().getLabel()).append("block").append(block.hashCode());
				} else {
					string.append("node").append(entry.getValue().hashCode()).append("block").append(block.hashCode());
				}
				string.append(";\n");
				print.execute(entry.getValue());
			}
			if (block.getBranchCondition()!=null) {
				Vertex<Item<L,P,T>> branch = block.getBranchCondition();
				string.append("branch").append(block.hashCode()).append(" [rank=max,color=yellow,label=\"Branch\"];\n");
				string.append("start").append(block.hashCode()).append(" -> branch").append(block.hashCode()).append(" [style=invis];\n");
				string.append("branch").append(block.hashCode()).append(" -> ");
				if (branch.getLabel().isVariable()) {
					string.append("get").append(branch.getLabel()).append("block").append(block.hashCode());
				} else {
					string.append("node").append(branch.hashCode()).append("block").append(block.hashCode());
				}
				string.append(";\n");
				print.execute(branch);
			}
			string.append("}\n");
			for (int i = 0; i < block.getNumSuccs(); i++) {
				Y child = block.getSucc(i);
				string.append("end").append(block.hashCode()).append(" -> start").append(child.hashCode()).append(" [style=bold,taillabel=\"").append(i).append("\"];\n");
			}
		}
		string.append("}");
		return string.toString();
	}
}
