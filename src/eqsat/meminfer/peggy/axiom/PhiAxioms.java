package eqsat.meminfer.peggy.axiom;

import eqsat.meminfer.engine.event.Event;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyVertex;

public final class PhiAxioms<O, P> extends PeggyAxioms<O,P> {
	public PhiAxioms(PeggyAxiomSetup<O,P> setup) {super(setup);}
	
	public void addAll() {
		addPhiTrueCondition();
		addPhiFalseCondition();
		//addNegatePhiCondition();
		addPhiNegateCondition();
		addJoinPhi();
		addPhiTrueFalse();
		addPhiFalseTrue();
	}
	
	public Event<? extends Proof> addPhiTrueCondition() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"True = c => phi(c, t, f) = t");
		PeggyVertex<O,Integer> c = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> t = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> f = axiomizer.getVariable(2);
		PeggyVertex<O,Integer> phi = axiomizer.getPhi(c, t, f);
		axiomizer.mustBeTrue(c);
		axiomizer.mustExist(phi);
		axiomizer.makeEqual(phi, t);
		
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addPhiFalseCondition() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"False = c => phi(c, t, f) = f");
		PeggyVertex<O,Integer> c = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> t = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> f = axiomizer.getVariable(2);
		PeggyVertex<O,Integer> phi = axiomizer.getPhi(c, t, f);
		axiomizer.mustBeFalse(c);
		axiomizer.mustExist(phi);
		axiomizer.makeEqual(phi, f);
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addPhiNegateCondition() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"phi(!c, t, f) = phi(c, f, t)");
		PeggyVertex<O,Integer> c = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> t = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> f = axiomizer.getVariable(2);
		PeggyVertex<O,Integer> phi
				= axiomizer.getPhi(axiomizer.getNegate(c), t, f);
		axiomizer.mustExist(phi);
		axiomizer.makeEqual(phi, axiomizer.getPhi(c, f, t));
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addJoinPhi() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"phi(c, x, x) = x");
		PeggyVertex<O,Integer> c = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> x = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> phi = axiomizer.getPhi(c, x, x);
		axiomizer.mustExist(phi);
		axiomizer.makeEqual(phi, x);
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addPhiTrueFalse() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"t = True ^ f = False => phi(c, t, f) = c");
		PeggyVertex<O,Integer> c = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> t = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> f = axiomizer.getVariable(2);
		PeggyVertex<O,Integer> phi = axiomizer.getPhi(c, t, f);
		axiomizer.mustBeTrue(t);
		axiomizer.mustBeFalse(f);
		axiomizer.mustExist(phi);
		axiomizer.makeEqual(phi, c);
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addPhiFalseTrue() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"t = False ^ f = True => phi(c, t, f) = !c");
		PeggyVertex<O,Integer> c = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> t = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> f = axiomizer.getVariable(2);
		PeggyVertex<O,Integer> phi = axiomizer.getPhi(c, t, f);
		axiomizer.mustBeFalse(t);
		axiomizer.mustBeTrue(f);
		axiomizer.mustExist(phi);
		axiomizer.makeEqual(phi, axiomizer.getNegate(c));
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
}
