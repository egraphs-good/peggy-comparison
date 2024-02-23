package eqsat.revert;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import util.Function;
import util.Tag;
import util.graph.AbstractVertex;
import util.graph.CExpressionGraph.Vertex;

public abstract class RevertBlock<L, P>
		extends AbstractVertex<RevertCFG<L,P,?>,RevertBlock<L,P>>
		implements eqsat.Block<RevertCFG<L,P,?>,RevertBlock<L,P>,Variable,L> {
	protected final Set<RevertBlock<L,P>> mParents
			= new HashSet<RevertBlock<L,P>>();
	protected final Map<Variable,Vertex<RevertValue<L,P>>> mModifications
			= new HashMap<Variable,Vertex<RevertValue<L,P>>>();
	
	public RevertBlock<L,P> getSelf() {return this;}
	
	public abstract List<? extends RevertBlock<L,P>> getChildren();
	public abstract RevertBlock<L,P> getChild(int child);
	public boolean isStart() {return isRoot();}
	public boolean isEnd() {return isLeaf();}
	
	/** 
	 * @author stepp
	 */
	public Collection<Variable> getModifiedVariables() {
		return mModifications.keySet();
	}
	public boolean modifies(Variable variable) {
		return mModifications.containsKey(variable);
	}
	public void setModification(Variable variable,
			Vertex<RevertValue<L,P>> mod) {
		getGraph().mVariables.add(variable);
		if (mod.getLabel().isVariable()
				&& mod.getLabel().getVariable().equals(variable))
			mModifications.remove(variable);
		else
			mModifications.put(variable, mod);
	}
	protected void removeNonModifications() {
		for (Iterator<Entry<Variable,Vertex<RevertValue<L,P>>>> entries
				= mModifications.entrySet().iterator(); entries.hasNext(); ) {
			Entry<Variable,Vertex<RevertValue<L,P>>> entry = entries.next();
			if (entry.getValue().getLabel().isVariable()
					&& entry.getValue().getLabel().getVariable().equals(
							entry.getKey()))
				entries.remove();
		}
	}
	
	public <E> E getOutput(Variable variable, Function<Variable,E> inputs,
			Function<Vertex<RevertValue<L,P>>,E> converter) {
		if (!modifies(variable))
			return inputs.get(variable);
		else
			return converter.get(mModifications.get(variable));
	}
	
	public void setBranchCondition(Vertex<RevertValue<L,P>> condition) {
		throw new UnsupportedOperationException();
	}
	public <E> E getBranchCondition(
			Function<Vertex<RevertValue<L,P>>,E> converter) {
		throw new UnsupportedOperationException();
	}
	
	public Function<ReversionGraph<P,L>.Vertex,Vertex<RevertValue<L,P>>>
			getConverter(final Tag<Vertex<RevertValue<L,P>>> convertTag) {
		return getGraph().getConverter(convertTag);
	}
	
	public void setChild(RevertBlock<L,P> child) {
		throw new UnsupportedOperationException();
	}
	
	protected void addParent(RevertBlock<L,P> parent) {mParents.add(parent);}
	
	protected abstract void replaceChild(RevertBlock<L,P> child,
			RevertBlock<L,P> replacement);
	
	protected abstract boolean deepEquals(RevertBlock<L,P> that);
}
