package peggy.analysis.java.inlining;

import soot.SootMethod;

/**
 * This class represents a dispatch map that does not depend on its
 * target for its values.
 */
public class TargetIndependentDispatchMap extends DispatchMap {
	protected SootMethod method;
	
	public TargetIndependentDispatchMap(String _signature, SootMethod _method) {
		super(_signature);
		this.method = _method;
	}
	
	public boolean isIndependent() {return true;}
	public TargetIndependentDispatchMap getIndependentSelf() {return this;}
	
	public SootMethod getDispatchedMethod() {return this.method;}
}
