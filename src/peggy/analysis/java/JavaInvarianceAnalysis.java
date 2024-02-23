package peggy.analysis.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import peggy.analysis.Analysis;
import peggy.represent.java.AnnotationJavaLabel;
import peggy.represent.java.JavaLabel;
import peggy.represent.java.JavaOperator;
import peggy.represent.java.JavaParameter;
import peggy.represent.java.MethodJavaLabel;
import peggy.represent.java.SimpleJavaLabel;
import eqsat.FlowValue;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.basic.Structure;
import eqsat.meminfer.engine.event.Event;
import eqsat.meminfer.engine.event.ProofEvent;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.proof.OpIs;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.network.peg.PEGNetwork.PEGNode;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;
import eqsat.meminfer.peggy.network.PeggyAxiomNetwork.AxiomNode;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyVertex;

/**
 * This analysis has axioms that deal with sigma invariance and reference
 * invariance.
 */
public abstract class JavaInvarianceAnalysis extends Analysis<JavaLabel,JavaParameter> {
	private static final JavaLabel SIGMA_INVARIANT = 
		new AnnotationJavaLabel("sigmaInvariant");
	
	protected final List<MethodJavaLabel> sigmaInvariantMethods = 
		new ArrayList<MethodJavaLabel>();
	private boolean addedAxioms = false;
	
	public JavaInvarianceAnalysis(
			Network _network,
			CPeggyAxiomEngine<JavaLabel,JavaParameter> _engine) {
		super(_network, _engine);
	}

	public void addSigmaInvariantMethods(
			Collection<? extends MethodJavaLabel> methods) {
		this.sigmaInvariantMethods.addAll(methods);
	}

	public void addAll() {
		if (!addedAxioms) {
			addSigmaInvariantMethodTagger();
			addSigmaInvariantCallAxioms();
			addedAxioms = true;
		}
	}
	
	private void addSigmaInvariantMethodTagger() {
		final String name = "M, where M is invariant => {sigmaInvariant(M)}";
		
		AxiomizerHelper helper = new AxiomizerHelper(
				new PeggyAxiomizer<JavaLabel, Integer>(name, getNetwork(), getAmbassador()));
		PeggyVertex<JavaLabel, Integer> M = helper.get("METHOD", null);
		helper.mustExist(M);
		
		final ProofEvent<CPEGTerm<JavaLabel,JavaParameter>,? extends Structure<CPEGTerm<JavaLabel, JavaParameter>>> 
		triggerEvent = helper.getTrigger();
		final StructureFunctions functions = helper.getStructureFunctions();
		
		ShapeListener listener = new ShapeListener() {
			protected ProofEvent<CPEGTerm<JavaLabel,JavaParameter>,? extends Structure<CPEGTerm<JavaLabel, JavaParameter>>> getProofEvent() {return triggerEvent;}
			protected StructureFunctions getFunctions() {return functions;}
			protected String getName() {return name;}
			protected String build(
					Bundle bundle,
					FutureExpressionGraph<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>,CPEGValue<JavaLabel,JavaParameter>> futureGraph) {
				CPEGTerm<JavaLabel,JavaParameter> method = bundle.getTerm("METHOD");
				MethodJavaLabel methodLabel = method.getOp().getDomain().getMethodSelf();
				Node result = node(
						SIGMA_INVARIANT,
						concOld(method));
				
				// got to say that the null label equalled the method label
				Proof proof = (enableProofs ? bundle.getTriggerProof() : null);
				if (enableProofs)
					proof.addProperty(
						new OpIs<FlowValue<JavaParameter,JavaLabel>,CPEGTerm<JavaLabel,JavaParameter>>(
								method, method.getOp()));
				
				result.finish(getEngine().getEGraph().getTrue(), proof, futureGraph);
				return methodLabel.toString();
			}
			protected boolean matches(Bundle bundle) {
				CPEGTerm<JavaLabel,JavaParameter> method = bundle.getTerm("METHOD");
				return method.getOp().isDomain() && 
					method.getOp().getDomain().isMethod() &&
					sigmaInvariantMethods.contains(method.getOp().getDomain().getMethodSelf());
			}
		};

		addStringListener(listener, name);
		triggerEvent.addListener(listener);
	}
	
	private void addSigmaInvariantCallAxioms() {
		SimpleJavaLabel[] labels = {
				SimpleJavaLabel.create(JavaOperator.INVOKESTATIC),
				SimpleJavaLabel.create(JavaOperator.INVOKESPECIAL),
				SimpleJavaLabel.create(JavaOperator.INVOKEVIRTUAL),
				SimpleJavaLabel.create(JavaOperator.INVOKEINTERFACE),
		};
		
		for (int i = 0; i < labels.length; i++) {
			final String name;
			if (i == 0) {
				name = "rho_sigma(" + labels[i].getOperator() + "(SIGMA,M,_)) = SIGMA [where M is sigmaInvariant]";  
			} else {
				name = "rho_sigma(" + labels[i].getOperator() + "(SIGMA,_,M,_)) = SIGMA [where M is sigmaInvariant]";  
			}
			PeggyAxiomizer<JavaLabel, Integer> axiomizer = 
					new PeggyAxiomizer<JavaLabel, Integer>(name, getNetwork(), getAmbassador());
			List<PeggyVertex<JavaLabel,Integer>> children = 
				new ArrayList<PeggyVertex<JavaLabel,Integer>> ();
			PeggyVertex<JavaLabel,Integer> method = axiomizer.getVariable(0);
			PeggyVertex<JavaLabel,Integer> SIGMA = axiomizer.getVariable(1);
			
			children.add(SIGMA); // SIGMA
			if (i != 0)
				children.add(axiomizer.getVariable(2)); // THIS
			children.add(method); // method
			children.add(axiomizer.getVariable(3)); // PARAMS
			
			PeggyVertex<JavaLabel,Integer> rho_sigma =  
				axiomizer.get(
						SimpleJavaLabel.create(JavaOperator.RHO_SIGMA),
						axiomizer.get(labels[i], children));
			PeggyVertex<JavaLabel,Integer> sigmaInvariant = 
				axiomizer.get(SIGMA_INVARIANT, method);
			
			axiomizer.mustExist(rho_sigma);
			axiomizer.mustBeTrue(sigmaInvariant);
			axiomizer.makeEqual(SIGMA, rho_sigma);

			AxiomNode<JavaLabel,? extends PEGNode<JavaLabel>> node =
				axiomizer.getAxiom();
			Event<? extends Proof> event = getEngine().addPEGAxiom(node);
			addProofListener(event, name);
		}
	}
}
