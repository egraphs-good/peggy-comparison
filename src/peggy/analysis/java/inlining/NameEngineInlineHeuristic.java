package peggy.analysis.java.inlining;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This is an inlining heuristic that only inlines explicitly named methods.
 */
public abstract class NameEngineInlineHeuristic<M,N extends Number> implements EngineInlineHeuristic<M,N> {
	private final Map<String,N> name2cost;
	
	public NameEngineInlineHeuristic() {
		this.name2cost = new HashMap<String,N>();
	}
	
	public void addFunction(String name, N newcost) {
		this.name2cost.put(name, newcost);
	}
	public void removeFunction(String name) {
		this.name2cost.remove(name);
	}
	public Iterable<String> getNames() {
		return Collections.unmodifiableSet(this.name2cost.keySet());
	}
	
	protected abstract String getName(M method);
	
	public boolean shouldInline(M method) {
		String name = this.getName(method);
		return this.name2cost.containsKey(name);
	}
	public N getInlinedCallCost(M method) {
		String name = this.getName(method);
		if (!this.name2cost.containsKey(name))
			throw new IllegalArgumentException("Method " + name + " is unknown");
		return this.name2cost.get(name);
	}
}
