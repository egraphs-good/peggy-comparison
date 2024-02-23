package eqsat.meminfer.network.peg;

import eqsat.FlowValue;
import eqsat.meminfer.network.op.LabelAmbassador;
import eqsat.meminfer.network.peg.PEGNetwork.PEGLoopOp;

public abstract class PEGLabelAmbassador<L, D, O>
		implements LabelAmbassador<L,FlowValue<?,O>> {
	public abstract boolean isExtendedDomain(L label);
	public abstract FlowValue<?,O> getExtendedDomain(L label);
	public abstract boolean isPhi(L label);
	public abstract boolean isZero(L label);
	public abstract boolean isSuccessor(L label);
	public abstract boolean isLoopOp(L label);
	public abstract PEGLoopOp getLoopOp(L label);
	public abstract D getLoopDepth(L label);
	
	public abstract boolean mustBeExtendedDomain(L label);
	public abstract boolean mustBeLoopLifted(L op, D depth);
	
	public abstract boolean mustBeDistinctLoops(D left, D right);
	
	public final boolean isConcrete(L label) {
		return isExtendedDomain(label) || isPhi(label)
				|| isZero(label) || isSuccessor(label);
	}
	public final FlowValue<?,O> getConcrete(L label) {
		if (isExtendedDomain(label))
			return getExtendedDomain(label);
		else if (isPhi(label))
			return FlowValue.createPhi();
		else if (isZero(label))
			return FlowValue.createZero();
		else if (isSuccessor(label))
			return FlowValue.createSuccessor();
		else
			throw new IllegalArgumentException();
	}
}
