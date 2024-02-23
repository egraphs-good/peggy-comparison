package peggy.analysis.java.inlining;

import java.util.Collection;

import soot.SootMethod;
import soot.Unit;

/**
 * This interface defines heuristics for when a method should be inlined.
 */
public interface InlinerHeuristic {
	public boolean shouldInline(SootMethod inliner, Unit where, SootMethod inlinee);
	public boolean shouldInlineAll(SootMethod inliner, Unit where, Collection<SootMethod> inlinees);
	public void update(SootMethod inliner);
}