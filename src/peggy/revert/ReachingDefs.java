package peggy.revert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import peggy.revert.MiniPEG.Vertex;
import static peggy.revert.ReachingDefs.Def;
import static peggy.revert.ReachingDefs.Use;

/**
 * This class implements the reaching definitions analysis.
 */
public abstract class ReachingDefs<L,P,R,T,X extends PEGCFG<L,P,R,T,X,Y>, Y extends PEGCFGBlock<L,P,R,T,X,Y>,RD extends ReachingDefs<L,P,R,T,X,Y,RD,USE,DEF>,USE extends Use<T,Y,USE>,DEF extends Def<T,Y,DEF>> {
	public static abstract class Use<T,Y,USE extends Use<T,Y,USE>> {
		public abstract Y getBlock();
		public abstract T getVariable();
		public abstract USE getSelf();
		public abstract boolean equals(Object o);
		public abstract int hashCode();
		public abstract String toString();
	}
	public static abstract class Def<T,Y,DEF extends Def<T,Y,DEF>> {
		public abstract Y getBlock();
		public abstract T getVariable();
		public abstract DEF getSelf();
		public abstract boolean equals(Object o);
		public abstract int hashCode();
		public abstract String toString();
	}
	
	//////////////////////////////
	
	private final X cfg;
	private final Map<USE,List<DEF>> use2defs =
		new HashMap<USE,List<DEF>>();
	
	protected ReachingDefs(X _cfg) {
		this.cfg = _cfg;
	}
	
	public abstract USE getUse(Y block, T var);
	public abstract DEF getDef(Y block, T var);

	public final List<DEF> getReachingDefs(Y block, T var) {
		return getReachingDefs(getUse(block, var));
	}
	public final List<DEF> getReachingDefs(USE use) {
		List<DEF> defs = this.use2defs.get(use);
		if (defs == null) {
			Set<DEF> defset = new HashSet<DEF>();
			Set<Y> seenblocks = new HashSet<Y>();
			LinkedList<Y> worklist = new LinkedList<Y>(this.cfg.getPreds(use.getBlock()));
			while (!worklist.isEmpty()) {
				Y next = worklist.removeFirst();
				if (seenblocks.contains(next))
					continue;
				seenblocks.add(next);
				
				if (next.getAssignedVars().contains(use.getVariable())) {
					// found a def! do not add parent blocks
					defset.add(getDef(next, use.getVariable()));
					continue;
				}
				
				worklist.addAll(this.cfg.getPreds(next));
			}
			defs = new ArrayList<DEF>(defset);
			this.use2defs.put(use, defs);
		}
		return Collections.unmodifiableList(defs);
	}
	

	
	/**
	 * Returns all the terms that transitively flow into the given start var usage.
	 * This will flow through any rhs terms that are just items/variables.
	 */
	public final List<DEF> getTransitiveDefs(USE use) {
		return getTransitiveDefs(use.getBlock(), use.getVariable());
	}
	public final List<DEF> getTransitiveDefs(Y block, T startVar) {
		LinkedList<USE> uses = new LinkedList<USE>();
		uses.add(getUse(block, startVar));
		Set<USE> seen = new HashSet<USE>();
		List<DEF> result = new ArrayList<DEF>();
		
		while (!uses.isEmpty()) {
			USE next = uses.removeFirst();
			if (seen.contains(next))
				continue;
			seen.add(next);
			
			List<DEF> defs = this.getReachingDefs(next);
			if (defs == null)
				throw new RuntimeException("Use has no defs: " + next);
			
			for (DEF def : defs) {
				Vertex<Item<L,P,T>> assignment = 
					def.getBlock().getAssignment(def.getVariable());
				
				if (assignment.getLabel().isVariable()) {
					// copy!
					USE rhsuse = getUse(def.getBlock(), assignment.getLabel().getVariable());
					if (rhsuse == null)
						throw new RuntimeException("Cannot create use");
					uses.add(rhsuse);
					continue;
				} else {
					// concrete item
					result.add(def);
					continue;
				}
			}
		}
		
		return result;
	}
}
