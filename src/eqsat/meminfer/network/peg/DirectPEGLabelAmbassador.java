package eqsat.meminfer.network.peg;

import eqsat.FlowValue;
import eqsat.meminfer.network.peg.PEGNetwork.PEGLoopOp;

public abstract class DirectPEGLabelAmbassador<O,P>
		extends PEGLabelAmbassador<FlowValue<P,O>,Integer,O> {
	public boolean isExtendedDomain(FlowValue<P,O> label) {
		return label != null && label.isExtendedDomain();
	}

	public FlowValue<?,O> getExtendedDomain(FlowValue<P,O> label) {
		if (isExtendedDomain(label))
			return label;
		else
			throw new IllegalArgumentException();
	}

	public boolean isPhi(FlowValue<P,O> label) {
		return label != null && label.isPhi();
	}

	public boolean isZero(FlowValue<P,O> label) {
		return label != null && label.isZero();
	}

	public boolean isSuccessor(FlowValue<P,O> label) {
		return label != null && label.isSuccessor();
	}

	public boolean isLoopOp(FlowValue<P,O> label) {
		return label != null && label.isLoopFunction();
	}

	public PEGLoopOp getLoopOp(FlowValue<P,O> label) {
		return PEGLoopOp.getLoopOp(label);
	}

	public Integer getLoopDepth(FlowValue<P,O> label) {
		return label.getLoopDepth();
	}
	
	public abstract boolean mustBeDistinctLoops(Integer left, Integer right);
	public abstract boolean mustBeExtendedDomain(FlowValue<P,O> label);
	public abstract boolean mustBeLoopLifted(FlowValue<P,O> op, Integer depth);
}
