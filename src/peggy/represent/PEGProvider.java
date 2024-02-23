package peggy.represent;

/**
 * This is a basic interface for objects that can provide PEGs given function
 * descriptions. The getPEG function only needs to return valid PEGs for those
 * that canProvidePEG returns true.
 */
public interface PEGProvider<F,L,P,R> {
	public boolean canProvidePEG(F function);
	public PEGInfo<L,P,R> getPEG(F function);
}
