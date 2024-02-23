package peggy.analysis.java.inlining;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import soot.SootClass;
import soot.SootMethod;

public class TargetDependentDispatchMap extends DispatchMap {
	protected Map<SootClass,SootMethod> map;
	
	public TargetDependentDispatchMap(String _signature, Map<SootClass,SootMethod> _map) {
		super(_signature);
		this.map = Collections.unmodifiableMap(_map);
	}
	
	public boolean isDependent() {return true;}
	public TargetDependentDispatchMap getDependentSelf() {return this;}
	
	public SootMethod getDispatchedMethod(SootClass clazz) {
		return this.map.get(clazz);
	}
	public Collection<SootClass> getClassKeys() {
		return this.map.keySet();
	}
	public Collection<SootMethod> getMethodValues() {
		return this.map.values();
	}
	public int size() {
		return this.map.size();
	}
}
