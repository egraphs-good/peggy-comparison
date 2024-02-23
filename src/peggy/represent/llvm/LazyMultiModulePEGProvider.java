package peggy.represent.llvm;

import llvm.values.Module;
import peggy.represent.PEGInfo;
import peggy.represent.PEGProvider;

/**
 * This is a PEGProvider that can load PEGs from multiple Modules.
 * It will only load the Modules as needed (hence lazily).
 */
public abstract class LazyMultiModulePEGProvider<F,L,P,R> implements PEGProvider<F,L,P,R> {
	protected abstract ModuleProvider getModuleProvider();
	protected abstract boolean hasFunction(Module module, F function);
	protected abstract PEGInfo<L,P,R> getPEG(
			Module module, F function);

	public boolean canProvidePEG(F function) {
		for (Module module : this.getModuleProvider().getAllModulesLazily()) {
			if (this.hasFunction(module, function))
				return true;
		}
		return false;
	}
	
	public final PEGInfo<L,P,R> getPEG(F function) {
		for (Module module : this.getModuleProvider().getAllModulesLazily()) {
			if (this.hasFunction(module, function))
				return getPEG(module, function);
		}
		throw new IllegalArgumentException("Cannot provide function " + function);
	}
}
