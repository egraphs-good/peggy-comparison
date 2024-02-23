package eqsat.meminfer.engine.peg;

import eqsat.meminfer.engine.basic.Term;
import eqsat.meminfer.engine.basic.Value;

public abstract class PEGValue<T extends Term<T,V>, V extends PEGValue<T,V>>
		extends Value<T,V> {
	public final boolean isInvariant(int depth) {
		if (0 < depth && depth < 32)
			return ((1 << depth) & getInvariance()) != 0;
		else
			throw new IndexOutOfBoundsException();
	}
	public final int getMaxVariance() {
		for (int depth = 32; --depth != 0; )
			if ((getInvariance() & (1 << depth)) == 0)
				return depth;
		return 0;
	}
	
	protected abstract int getInvariance();
	protected abstract void setInvariance(int invariance);
	
	protected final void makeInvariant(int depth) {
		if (0 < depth && depth < 32)
			makeInvariants(1 << depth);
		else
			throw new IndexOutOfBoundsException();
	}
	protected final void makeInvariants(int invariants) {
		setInvariance(getInvariance() | invariants);
	}
}
