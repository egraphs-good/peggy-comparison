package eqsat.meminfer.peggy.axiom;

import eqsat.meminfer.engine.event.Event;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyVertex;

public final class EqualityAxioms<O, P> extends PeggyAxioms<O,P> {
	public EqualityAxioms(PeggyAxiomSetup<O,P> setup) {super(setup);}
	
	public void addAll() {
		//addSymmetricEquals();
		addReflexiveEquals();
		addTrueEquals();
	}

	public Event<? extends Proof> addSymmetricEquals() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"(X == Y) = (Y == X)");
		PeggyVertex<O,Integer> x = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> y = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> equals = axiomizer.getEquals(x, y);
		axiomizer.mustExist(equals);
		axiomizer.makeEqual(equals, axiomizer.getEquals(y, x));
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}

	public Event<? extends Proof> addReflexiveEquals() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"(X == X) = True");
		PeggyVertex<O,Integer> x = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> equals = axiomizer.getEquals(x, x);
		axiomizer.mustExist(equals);
		axiomizer.makeTrue(equals);
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}

	public Event<? extends Proof> addTrueEquals() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"True = (X == Y)) => X = Y");
		PeggyVertex<O,Integer> x = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> y = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> equals = axiomizer.getEquals(x, y);
		axiomizer.mustBeTrue(equals);
		axiomizer.makeEqual(x, y);
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
}
