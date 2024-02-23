package peggy.analysis.java.inlining;

/**
 * A dispatch map refers to a method signature and maps classes to the 
 * implementations of that method which they use.
 */
public abstract class DispatchMap {
	protected String signature;
	
	public DispatchMap(String _signature) {
		this.signature = _signature;
	}
	
	public String getNamedSignature() {return this.signature;}
	
	public boolean isIndependent() {return false;}
	public TargetIndependentDispatchMap getIndependentSelf() {throw new UnsupportedOperationException();}
	
	public boolean isDependent() {return false;}
	public TargetDependentDispatchMap getDependentSelf() {throw new UnsupportedOperationException();}
}
