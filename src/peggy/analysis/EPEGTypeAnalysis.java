package peggy.analysis;

import peggy.analysis.llvm.types.PEGType;
import util.MultiMap;
import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.EGraphManager;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;

/**
 * This represents a type analysis that can run occasionally over the egraph
 * to deduce types for each of the values in the EPEG.
 */
public abstract class EPEGTypeAnalysis<L,P,D> {
	private static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("EPEGTypeAnalysis: " + message);
	}
	
	protected MultiMap<CPEGValue<L,P>,PEGType<D>> value2type;
	protected final EGraphManager<CPEGTerm<L,P>, CPEGValue<L,P>> egraph;
	
	public EPEGTypeAnalysis(EGraphManager<CPEGTerm<L,P>, CPEGValue<L,P>> _egraph) {
		this.egraph = _egraph;
		this.value2type = this.egraph.getValueManager().<PEGType<D>>createValueMultiMap();
	}
	
	public PEGType<D> getType(CPEGValue<L,P> value) {
		if (this.value2type.containsKey(value)) {
			return this.value2type.get(value).iterator().next();
		}
		return null;
	}
	
	protected PEGType<D> computeType(CPEGValue<L,P> value) {
		for (CPEGTerm<L,P> term : value.getTerms()) {
			final FlowValue<P,L> flow = term.getOp();
			if (flow.isDomain()) {
				return this.computeDomainType(term);
			} else if (flow.isTrue() ||
					   flow.isAnd() ||
					   flow.isEquals() ||
					   flow.isFalse() ||
					   flow.isNegate() ||
					   flow.isOr() ||
					   flow.isShortCircuitAnd() ||
					   flow.isShortCircuitOr()) {
				return PEGType.<D>makeBoolean();
			} else if (flow.isEval()) {
				return this.getType(term.getChild(0).getValue());
			} else if (flow.isParameter()) {
				return this.computeParameterType(flow.getParameter());
			} else if (flow.isPass() ||
					   flow.isZero() ||
					   flow.isSuccessor()) {
				return PEGType.<D>makeIterationValue();
			} else if (flow.isPhi()) {
				PEGType<D> first = this.getType(term.getChild(1).getValue());
				if (first!=null)
					return first;
				return this.getType(term.getChild(2).getValue());
			} else if (flow.isShift()) {
				return this.getType(term.getChild(0).getValue());
			} else if (flow.isTheta()) {
				PEGType<D> first = this.getType(term.getChild(0).getValue());
				if (first!=null)
					return first;
				return this.getType(term.getChild(1).getValue());
			} else {
				throw new RuntimeException("Didn't handle: " + flow);
			}
		}
		return null;
	}
	
	protected abstract PEGType<D> computeDomainType(CPEGTerm<L,P> term);
	protected abstract PEGType<D> computeParameterType(P param);
	
	/**
	 * Returns whether or not any progress was made.
	 */
	public boolean run() {
		boolean changed = false;
		for (boolean progress = true; progress; ) {
			progress = false;
			for (CPEGValue<L,P> value : this.egraph.getValueManager().getValues()) {
				if (!this.value2type.containsKey(value)) {
					PEGType<D> type = this.computeType(value);
					if (type != null) {
						this.value2type.putValue(value, type);
						changed = progress = true;
					}
				}
			}
		}
		return changed;
	}
	
	public PEGType<D> getOrComputeType(CPEGValue<L,P> value) {
		PEGType<D> type = this.getType(value);
		if (type == null) {
			type = this.computeType(value);
			if (type == null) {
				this.run();
				type = this.getType(value);
			} else {
				this.value2type.putValue(value, type);
			}
		}
		return type;
	}
}
