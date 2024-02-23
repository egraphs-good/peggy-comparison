package peggy.pb;

import eqsat.meminfer.engine.peg.CPEGValue;

/**
 * This is the default NondomainInclusionPattern.
 */
public class DefaultNondomainInclusionPattern<O,P> extends NondomainInclusionPattern<O, P> {
	protected int getMaxVariance(CPEGValue<O, P> value) {
		return value.getMaxVariance();
	}
}
