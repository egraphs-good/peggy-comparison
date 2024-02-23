package peggy.represent.llvm;

/**
 * This enumerates the various GEP forcing policies.
 * One can either for all GEP indexes to be 32 bit, 64 bit, or no forcing.
 */
public enum GEPForcingPolicy {
	NONE,
	FORCE_32,
	FORCE_64;
}
