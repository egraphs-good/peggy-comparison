package peggy.revert.java;

import java.util.Arrays;

import peggy.pb.DefaultNondomainInclusionPattern;
import peggy.pb.NondomainInclusionPattern;
import peggy.pb.NondomainStickyPredicate;
import peggy.pb.PseudoBooleanFormulation;
import peggy.represent.CombinedStickyPredicate;
import peggy.represent.FlowValueStickyPredicate;
import peggy.represent.StickyPredicate;
import peggy.represent.TermStickyPredicate;
import peggy.represent.java.JavaLabel;
import peggy.represent.java.JavaLabelStickyPredicate;
import peggy.represent.java.JavaParameter;
import peggy.represent.java.JavaReturn;
import peggy.revert.PseudoBooleanReversionHeuristic;
import util.AbstractPattern;
import util.Pattern;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;

/**
 * This is the default PB heuristic for the Java optimizer.
 */
public abstract class DefaultPBReversionHeuristic<PBF extends PseudoBooleanFormulation<CPEGTerm<JavaLabel,JavaParameter>>>
extends PseudoBooleanReversionHeuristic<JavaLabel,JavaParameter,JavaReturn,Integer,PBF> {
	final static NondomainInclusionPattern<JavaLabel,JavaParameter> nondomainPattern = 
		new DefaultNondomainInclusionPattern<JavaLabel, JavaParameter>();
	private final StickyPredicate<CPEGTerm<JavaLabel,JavaParameter>> predicate = 
		new TermStickyPredicate<JavaLabel,JavaParameter>(
				new CombinedStickyPredicate<FlowValue<JavaParameter,JavaLabel>>(
						Arrays.asList(
								new FlowValueStickyPredicate<JavaLabel,JavaParameter>(
										JavaLabelStickyPredicate.INSTANCE),
								new NondomainStickyPredicate<JavaLabel,JavaParameter>())));
	protected final StickyPredicate<CPEGTerm<JavaLabel,JavaParameter>> getStickyPredicate() {
		return predicate;
	}
	protected Pattern<? super CPEGTerm<JavaLabel,JavaParameter>> getNodeInclusionPattern() {
		return new AbstractPattern<CPEGTerm<JavaLabel,JavaParameter>>() {
			public boolean matches(CPEGTerm<JavaLabel,JavaParameter> nl) {
				FlowValue<JavaParameter,JavaLabel> flow = nl.getOp();
				if (flow.isDomain()) {
					return flow.getDomain().isRevertible();
				} else {
					return nondomainPattern.matches(nl);
				}
			}
		};
	}
}
