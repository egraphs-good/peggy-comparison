package peggy.analysis.llvm;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import peggy.represent.llvm.FunctionLLVMLabel;

/**
 * This class specifies a function and the set of its pointer parameters
 * that it might modify.
 */
public class FunctionModifies {
	protected final FunctionLLVMLabel function;
	protected final int argumentCount;
	protected final Set<Integer> modifies;
	
	public FunctionModifies(
			FunctionLLVMLabel _function,
			int _nargs,
			Collection<Integer> _mods) {
		this.function = _function;
		this.argumentCount = _nargs;
		this.modifies = Collections.unmodifiableSet(new HashSet<Integer>(_mods));
	}
	public FunctionLLVMLabel getFunction() {return this.function;}
	public int getArgumentCount() {return this.argumentCount;}
	public Set<Integer> modifies() {return this.modifies;}
	public boolean modifies(int index) {return this.modifies.contains(index);}
	
	public boolean equals(Object o) {
		if (!(o instanceof FunctionModifies))
			return false;
		FunctionModifies fm = (FunctionModifies)o;
		return fm.function.equals(this.function) &&
			fm.argumentCount == this.argumentCount &&
			fm.modifies.equals(this.modifies);
	}
	public int hashCode() {
		return
			3*function.hashCode() +
			5*argumentCount + 
			7*modifies.hashCode();
	}
}
