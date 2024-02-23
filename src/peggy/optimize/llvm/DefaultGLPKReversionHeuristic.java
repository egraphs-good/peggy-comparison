package peggy.optimize.llvm;

import java.util.Arrays;

import peggy.ilp.GLPKReversionHeuristic;
import peggy.pb.DefaultNondomainInclusionPattern;
import peggy.pb.NondomainInclusionPattern;
import peggy.pb.NondomainStickyPredicate;
import peggy.represent.CombinedStickyPredicate;
import peggy.represent.FlowValueStickyPredicate;
import peggy.represent.StickyPredicate;
import peggy.represent.TermStickyPredicate;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMLabelStickyPredicate;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.LLVMReturn;
import util.AbstractPattern;
import util.Pattern;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;

/**
 * This is the GLP reversion heuristic that is used by default in the LLVM optimizer.
 */
public abstract class DefaultGLPKReversionHeuristic extends GLPKReversionHeuristic<LLVMLabel,LLVMParameter,LLVMReturn> {
	final static NondomainInclusionPattern<LLVMLabel,LLVMParameter> nondomainPattern = 
		new DefaultNondomainInclusionPattern<LLVMLabel, LLVMParameter>();
	private final StickyPredicate<CPEGTerm<LLVMLabel,LLVMParameter>> predicate = 
		new TermStickyPredicate<LLVMLabel,LLVMParameter>(
				new CombinedStickyPredicate<FlowValue<LLVMParameter,LLVMLabel>>(
						Arrays.asList(
								new FlowValueStickyPredicate<LLVMLabel,LLVMParameter>(
										LLVMLabelStickyPredicate.INSTANCE),
								new NondomainStickyPredicate<LLVMLabel,LLVMParameter>())));
	protected final StickyPredicate<CPEGTerm<LLVMLabel,LLVMParameter>> getStickyPredicate() {
		return predicate;
	}
	protected Pattern<? super CPEGTerm<LLVMLabel,LLVMParameter>> getNodeInclusionPattern() {
		return new AbstractPattern<CPEGTerm<LLVMLabel,LLVMParameter>>() {
			public boolean matches(CPEGTerm<LLVMLabel,LLVMParameter> nl) {
				FlowValue<LLVMParameter,LLVMLabel> flow = nl.getOp();
				if (flow.isDomain()) {
					return flow.getDomain().isRevertible();
				} else {
					return nondomainPattern.matches(nl);
				}
			}
		};
	}
}
