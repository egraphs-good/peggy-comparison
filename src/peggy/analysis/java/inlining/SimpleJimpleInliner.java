package peggy.analysis.java.inlining;

import java.util.HashMap;
import java.util.Map;

import peggy.analysis.java.ClassMapLattice;
import soot.SootMethod;

/**
 * This is the default implementation of the Jimple inliner.
 */
public class SimpleJimpleInliner extends JimpleInliner {
	protected final Map<SootMethod,JimpleTypeAnalysis> cacheAnalyses;
	
	protected SimpleJimpleInliner() {
		this.cacheAnalyses = new HashMap<SootMethod,JimpleTypeAnalysis>();
	}
	
	protected JimpleTypeAnalysis getTypeAnalysis(SootMethod inliner) {
		JimpleTypeAnalysis result = this.cacheAnalyses.get(inliner);
		if (result == null) {
			result = new SimpleJimpleTypeAnalysis(inliner, new ClassMapLattice<JimpleWrapper>());
			this.cacheAnalyses.put(inliner, result);
		}
		return result;
	}
}
