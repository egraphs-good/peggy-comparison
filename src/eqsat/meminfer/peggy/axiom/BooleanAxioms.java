package eqsat.meminfer.peggy.axiom;

import eqsat.meminfer.engine.event.Event;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyVertex;

public final class BooleanAxioms<O, P> extends PeggyAxioms<O,P> {
	public BooleanAxioms(PeggyAxiomSetup<O,P> setup) {super(setup);}
	
	public void addAll() {
		addNegateTrueIsFalse();
		addNegateFalseIsTrue();
		addNegateNegate();
		addOrSymmetric();
		addAndSymmetric();
		addOrTrueIsTrue();
		addOrFalseIsOther();
		addAndTrueIsOther();
		addAndFalseIsFalse();
	}

	public Event<? extends Proof> addAndFalseIsFalse() {
		PeggyAxiomizer<O,Integer> axiomizer = 
			this.<Integer>createAxiomizer("a&F = F");
		PeggyVertex<O,Integer> aandb = axiomizer.getAnd(
				axiomizer.getVariable(0),
				axiomizer.getVariable(1));
		axiomizer.mustExist(aandb);
		axiomizer.mustBeFalse(axiomizer.getVariable(1));
		axiomizer.makeFalse(aandb);
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addAndTrueIsOther() {
		PeggyAxiomizer<O,Integer> axiomizer = 
			this.<Integer>createAxiomizer("a&T = a");
		PeggyVertex<O,Integer> aandb = axiomizer.getAnd(
				axiomizer.getVariable(0),
				axiomizer.getVariable(1));
		axiomizer.mustExist(aandb);
		axiomizer.mustBeTrue(axiomizer.getVariable(1));
		axiomizer.makeEqual(aandb, axiomizer.getVariable(0));
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addOrFalseIsOther() {
		PeggyAxiomizer<O,Integer> axiomizer = 
			this.<Integer>createAxiomizer("a|F = a");
		PeggyVertex<O,Integer> aorb = axiomizer.getOr(
				axiomizer.getVariable(0),
				axiomizer.getVariable(1));
		axiomizer.mustBeFalse(axiomizer.getVariable(1));
		axiomizer.mustExist(aorb);
		axiomizer.makeEqual(aorb, axiomizer.getVariable(0));
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addOrTrueIsTrue() {
		PeggyAxiomizer<O,Integer> axiomizer = 
			this.<Integer>createAxiomizer("a|T = T");
		PeggyVertex<O,Integer> aorb = axiomizer.getOr(
				axiomizer.getVariable(0),
				axiomizer.getVariable(1));
		axiomizer.mustExist(aorb);
		axiomizer.mustBeTrue(axiomizer.getVariable(1));
		axiomizer.makeTrue(aorb);
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}

	public Event<? extends Proof> addAndSymmetric() {
		PeggyAxiomizer<O,Integer> axiomizer = 
			this.<Integer>createAxiomizer("a&b = b&a");
		PeggyVertex<O,Integer> aandb = axiomizer.getAnd(
				axiomizer.getVariable(0),
				axiomizer.getVariable(1));
		PeggyVertex<O,Integer> banda = axiomizer.getAnd(
				axiomizer.getVariable(1),
				axiomizer.getVariable(0));
		axiomizer.mustExist(aandb);
		axiomizer.makeEqual(aandb, banda);
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addOrSymmetric() {
		PeggyAxiomizer<O,Integer> axiomizer = 
			this.<Integer>createAxiomizer("a|b = b|a");
		PeggyVertex<O,Integer> aorb = axiomizer.getOr(
				axiomizer.getVariable(0),
				axiomizer.getVariable(1));
		PeggyVertex<O,Integer> bora = axiomizer.getOr(
				axiomizer.getVariable(1),
				axiomizer.getVariable(0));
		axiomizer.mustExist(aorb);
		axiomizer.makeEqual(aorb, bora);
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addNegateTrueIsFalse() {
		PeggyAxiomizer<O,Integer> axiomizer = 
			this.<Integer>createAxiomizer("!True = False");
		axiomizer.mustBeTrue(axiomizer.getVariable(0));
		axiomizer.mustExist(axiomizer.getNegate(axiomizer.getVariable(0)));
		axiomizer.makeFalse(axiomizer.getNegate(axiomizer.getVariable(0)));
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addNegateFalseIsTrue() {
		PeggyAxiomizer<O,Integer> axiomizer = 
			this.<Integer>createAxiomizer("!False = True");
		axiomizer.mustBeFalse(axiomizer.getVariable(0));
		axiomizer.mustExist(axiomizer.getNegate(axiomizer.getVariable(0)));
		axiomizer.makeTrue(axiomizer.getNegate(axiomizer.getVariable(0)));
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addNegateNegate() {
		PeggyAxiomizer<O,Integer> axiomizer = 
			this.<Integer>createAxiomizer("!!X = X");
		PeggyVertex<O,Integer> c = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> expression
				= axiomizer.getNegate(axiomizer.getNegate(c));
		axiomizer.mustExist(expression);
		axiomizer.makeEqual(expression, c);
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
}
