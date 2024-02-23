package peggy.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.event.ProofEvent;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.proof.ChildIsEquivalentTo;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyVertex;

/**
 * This analysis performs the same task as the built-in constant folder.
 * This was written at a time when it was believed that the built-in
 * constant folder was broken, but it is now mostly redundant.
 */
public abstract class ConstantFoldingAnalysis<L,P> extends Analysis<L,P> {
	private final ConstantFolder<L> folder;
	private boolean added = false;
	
	public ConstantFoldingAnalysis(
			ConstantFolder<L> _folder,
			Network network,
			CPeggyAxiomEngine<L,P> engine) {
		super(network, engine);
		this.folder = _folder;
	}
	
	protected abstract boolean isConstant(L label);
	protected abstract boolean isValidBinop(L label);
	protected abstract L makeAnnotation(String name);
	
	public void addAll() {
		if (!added) {
			addBinopCFAxioms();
			addConstantDetectAxioms();
			added = true;
		}
	}
	
	private void addConstantDetectAxioms() {
		final String name = "Detect constant values";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<L, Integer>(name, getNetwork(), getAmbassador()));
		PeggyVertex<L,Integer> cv = helper.get("cv", null);
		helper.mustExist(cv);

		final ProofEvent<CPEGTerm<L,P>,? extends Structure<CPEGTerm<L, P>>> 
		triggerEvent = helper.getTrigger();
		final StructureFunctions functions = helper.getStructureFunctions();

		ShapeListener listener = new ShapeListener() {
			protected ProofEvent<CPEGTerm<L,P>,? extends Structure<CPEGTerm<L, P>>> getProofEvent() {return triggerEvent;}
			protected StructureFunctions getFunctions() {return functions;}
			protected String getName() {return name;}
			protected String build(
					Bundle bundle,
					FutureExpressionGraph<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> futureGraph) {
				final CPEGTerm<L,P> cv = bundle.getTerm("cv");
				final Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs) addConstantProperties(proof, cv);

				final Node result = node(
						makeAnnotation("isConstant"),
						concOld(cv));
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);

				return cv.getOp().toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<L,P> cv = bundle.getTerm("cv");
				if (cv.getOp().isDomain() &&
					isConstant(cv.getOp().getDomain())) {
					return true;
				} else if (cv.getOp().isBasicOp() &&
						isConstant(getAmbassador().getBasicOp(cv.getOp().getBasicOp()))) {
					return true;
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	private void addBinopCFAxioms() {
		final String name = "Constant folder for binops";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<L, Integer>(name, getNetwork(), getAmbassador()));
		PeggyVertex<L,Integer> LHS = helper.getVariable("LHS");
		PeggyVertex<L,Integer> RHS = helper.getVariable("RHS");
		PeggyVertex<L,Integer> binop =
			helper.get("binop", null, LHS, RHS);
		helper.mustExist(binop);
		helper.mustBeTrue(helper.get(
				makeAnnotation("isConstant"),
				LHS));
		helper.mustBeTrue(helper.get(
				makeAnnotation("isConstant"),
				RHS));

		final ProofEvent<CPEGTerm<L,P>,? extends Structure<CPEGTerm<L, P>>> 
		triggerEvent = helper.getTrigger();
		final StructureFunctions functions = helper.getStructureFunctions();

		ShapeListener listener = new ShapeListener() {
			protected ProofEvent<CPEGTerm<L,P>,? extends Structure<CPEGTerm<L, P>>> getProofEvent() {return triggerEvent;}
			protected StructureFunctions getFunctions() {return functions;}
			protected String getName() {return name;}
			protected String build(
					Bundle bundle,
					FutureExpressionGraph<FlowValue<P,L>,CPEGTerm<L,P>,CPEGValue<L,P>> futureGraph) {
				final List<CPEGTerm<L,P>> children = getTerms(
						Arrays.asList(bundle.getRep("LHS").getValue(), bundle.getRep("RHS").getValue()));
				if (children == null)
					throw new RuntimeException("Cannot find all constants");
				
				final List<L> labels = new ArrayList<L>();
				for (CPEGTerm<L,P> term : children) {
					if (term.getOp().isDomain())
						labels.add(term.getOp().getDomain());
					else
						labels.add(getAmbassador().getBasicOp(term.getOp().getBasicOp()));
				}
				
				final CPEGTerm<L,P> binop = bundle.getTerm("binop");
				final L rootlabel = binop.getOp().getDomain();
				if (!folder.canFold(rootlabel, labels))
					throw new RuntimeException("Folder cannot do this fold");
				
				final L resultLabel = folder.fold(rootlabel, labels);
				final Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs) {
					addConstantProperties(proof, binop);
					for (int i = 0; i < children.size(); i++) {
						proof.addProperty(new ChildIsEquivalentTo<CPEGTerm<L,P>,CPEGValue<L,P>>(
								binop, i, children.get(i)));
					}
				}

				final Node result = node(resultLabel);
				result.finish(binop, proof, futureGraph);

				return rootlabel.toString() + "  " + labels.toString() + " = " + resultLabel;
			}
			private List<CPEGTerm<L,P>> getTerms(
					List<CPEGValue<L,P>> values) {
				final List<CPEGTerm<L,P>> result = 
					new ArrayList<CPEGTerm<L,P>>();
				toploop:
				for (int i = 0; i < values.size(); i++) {
					for (CPEGTerm<L,P> term : values.get(i).getTerms()) {
						if (term.getOp().isDomain() &&
							isConstant(term.getOp().getDomain())) {
							result.add(term);
							continue toploop;
						} else if (term.getOp().isBasicOp() &&
								isConstant(getAmbassador().getBasicOp(term.getOp().getBasicOp()))) {
							result.add(term);
							continue toploop;
						}
					}
					return null;
				}
				return result;
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<L,P> binop = bundle.getTerm("binop");
				if (binop.getOp().isDomain() && isValidBinop(binop.getOp().getDomain())) {
					final List<CPEGTerm<L,P>> children = getTerms(
							Arrays.asList(bundle.getRep("LHS").getValue(), bundle.getRep("RHS").getValue()));
					if (children == null)
						return false;
					
					final List<L> labels = new ArrayList<L>();
					for (CPEGTerm<L,P> term : children) {
						if (term.getOp().isDomain())
							labels.add(term.getOp().getDomain());
						else
							labels.add(getAmbassador().getBasicOp(term.getOp().getBasicOp()));
					}
					return folder.canFold(binop.getOp().getDomain(), labels);
				}
				return false;
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
}
