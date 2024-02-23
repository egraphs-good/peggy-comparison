package llvm.values;

/**
 * Interface for all objects that have linkage and visibility.
 * This includes functions and globals.
 */
public interface LinkedAndVisible {
	public Linkage getLinkage();
	public Visibility getVisibility();
}
