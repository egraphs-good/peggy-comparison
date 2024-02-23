package peggy.pb;

import util.AbstractPattern;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;

public abstract class NondomainInclusionPattern<O,P> extends AbstractPattern<CPEGTerm<O,P>> {
	protected abstract int getMaxVariance(CPEGValue<O,P> value);
	public boolean matches(CPEGTerm<O,P> term) {
		FlowValue<P,O> flow = term.getOp();
		if (flow.isTheta()) {
			int loopDepth = flow.getLoopDepth();
			if (getMaxVariance(term.getValue()) > loopDepth)
				return false;
		} else if (flow.isEval() || flow.isPass()) {
			int loopDepth = flow.getLoopDepth();
			if (getMaxVariance(term.getChild(0).getValue()) > loopDepth)
				return false;
		}
		return flow.isRevertable();
	}
}
