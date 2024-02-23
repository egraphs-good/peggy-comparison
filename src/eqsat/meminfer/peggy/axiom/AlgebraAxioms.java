package eqsat.meminfer.peggy.axiom;

import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyVertex;

public class AlgebraAxioms<O, P> extends PeggyAxioms<O,P> {
	public AlgebraAxioms(PeggyAxiomSetup<O,P> setup) {super(setup);}
	
	public void addRightZero(O op, O zero) {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"Right Zero: " + op + " " + zero);
		PeggyVertex<O,Integer> x = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> z = axiomizer.get(zero);
		PeggyVertex<O,Integer> expression = axiomizer.get(op, x, z);
		axiomizer.mustExist(expression);
		axiomizer.makeEqual(expression, z);
		getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public void addLeftZero(O op, O zero) {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"Left Zero: " + op + " " + zero);
		PeggyVertex<O,Integer> x = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> z = axiomizer.get(zero);
		PeggyVertex<O,Integer> expression = axiomizer.get(op, z, x);
		axiomizer.mustExist(expression);
		axiomizer.makeEqual(expression, z);
		getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public void addZero(O op, O zero) {
		addRightZero(op, zero);
		addLeftZero(op, zero);
	}
	
	public void addRightIdentity(final O op, final O id) {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"Right Identity: " + op + " " + id);
		PeggyVertex<O,Integer> x = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> identity = axiomizer.get(id);
		PeggyVertex<O,Integer> expression = axiomizer.get(op, x, identity);
		axiomizer.mustExist(expression);
		axiomizer.makeEqual(expression, x);
		getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public void addLeftIdentity(O op, O id) {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"Left Identity: " + op + " " + id);
		PeggyVertex<O,Integer> x = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> identity = axiomizer.get(id);
		PeggyVertex<O,Integer> expression = axiomizer.get(op, identity, x);
		axiomizer.mustExist(expression);
		axiomizer.makeEqual(expression, x);
		getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public void addIdentity(O op, O id) {
		addRightIdentity(op, id);
		addLeftIdentity(op, id);
	}
	
	public void addLeftDistributivity(O times, O plus) {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"Left Distributivity: " + times + " " + plus);
		PeggyVertex<O,Integer> x = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> y = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> z = axiomizer.getVariable(2);
		PeggyVertex<O,Integer> expression
				= axiomizer.get(times, x, axiomizer.get(plus, y, z));
		axiomizer.mustExist(expression);
		axiomizer.makeEqual(expression, axiomizer.get(plus,
				axiomizer.get(times, x, y), axiomizer.get(times, x, z)));
		getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public void addRightDistributivity(O times, O plus) {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"Right Distributivity: " + times + " " + plus);
		PeggyVertex<O,Integer> x = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> y = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> z = axiomizer.getVariable(2);
		PeggyVertex<O,Integer> expression
				= axiomizer.get(times, axiomizer.get(plus, x, y), z);
		axiomizer.mustExist(expression);
		axiomizer.makeEqual(expression, axiomizer.get(plus,
				axiomizer.get(times, x, z), axiomizer.get(times, y, z)));
		getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public void addDistributivity(O times, O plus) {
		addLeftDistributivity(times, plus);
		addRightDistributivity(times, plus);
	}
	
	public void addLeftAssociativity(O op) {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"Left Associativity: " + op);
		PeggyVertex<O,Integer> x = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> y = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> z = axiomizer.getVariable(2);
		PeggyVertex<O,Integer> expression
				= axiomizer.get(op, x, axiomizer.get(op, y, z));
		axiomizer.mustExist(expression);
		axiomizer.makeEqual(expression,
				axiomizer.get(op, axiomizer.get(op, x, y), z));
		getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public void addRightAssociativity(O op) {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"Right Associativity: " + op);
		PeggyVertex<O,Integer> x = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> y = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> z = axiomizer.getVariable(2);
		PeggyVertex<O,Integer> expression
				= axiomizer.get(op, axiomizer.get(op, x, y), z);
		axiomizer.mustExist(expression);
		axiomizer.makeEqual(expression,
				axiomizer.get(op, x, axiomizer.get(op, y, z)));
		getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public void addAssociativity(O op) {
		addLeftAssociativity(op);
		addRightAssociativity(op);
	}
	
	public void addCommutativity(O op) {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"Commutativity: " + op);
		PeggyVertex<O,Integer> x = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> y = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> expression = axiomizer.get(op, x, y);
		axiomizer.mustExist(expression);
		axiomizer.makeEqual(expression, axiomizer.get(op, y, x));
		getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
}
