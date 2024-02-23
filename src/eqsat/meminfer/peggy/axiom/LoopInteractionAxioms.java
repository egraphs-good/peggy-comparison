package eqsat.meminfer.peggy.axiom;

import eqsat.meminfer.engine.event.Event;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyVertex;

public class LoopInteractionAxioms<O, P> extends PeggyAxioms<O,P> {
	public LoopInteractionAxioms(PeggyAxiomSetup<O,P> setup) {super(setup);}
	
	public void addAll() {
		//addDistributeLoopOpsThroughEval();
		//addUndistributeThroughEval();
		addDistributeThetaThroughEval1();
		addDistributeThetaThroughEval2();
		addDistributeShiftThroughEval();
		addDistributePassThroughEval();
		addDistributeEvalThroughEval();
	}
	
	public Event<? extends Proof> addDistributeThetaThroughEval1() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"invariant_1(i) ^ invariant_2(u) => "
				+ "theta_1(eval_2(b, i), u) = eval_2(theta_1(b, u), i)");
		PeggyVertex<O,Integer> b = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> i = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> u = axiomizer.getVariable(2);
		PeggyVertex<O,Integer> theta = axiomizer.getTheta(1,
				axiomizer.getEval(2, b, i), u);
		axiomizer.mustBeDistinctLoops(1, 2);
		axiomizer.mustBeInvariant(1, i);
		axiomizer.mustBeInvariant(2, u);
		axiomizer.mustExist(theta);
		axiomizer.makeEqual(theta,
				axiomizer.getEval(2, axiomizer.getTheta(1, b, u), i));
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addDistributeThetaThroughEval2() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"invariant_1(i) ^ invariant_2(b) => "
				+ "theta_1(b, eval_2(u, i)) = eval_2(theta_1(b, u), i)");
		PeggyVertex<O,Integer> b = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> i = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> u = axiomizer.getVariable(2);
		PeggyVertex<O,Integer> theta = axiomizer.getTheta(1,
				b, axiomizer.getEval(2, u, i));
		axiomizer.mustBeDistinctLoops(1, 2);
		axiomizer.mustBeInvariant(1, i);
		axiomizer.mustBeInvariant(2, b);
		axiomizer.mustExist(theta);
		axiomizer.makeEqual(theta,
				axiomizer.getEval(2, axiomizer.getTheta(1, b, u), i));
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addDistributeShiftThroughEval() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"shift_1(eval_2(x, i)) = eval_2(shift_1(x), shift_1(i))");
		PeggyVertex<O,Integer> x = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> i = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> shift = axiomizer.getShift(1,
				axiomizer.getEval(2, x, i));
		axiomizer.mustBeDistinctLoops(1, 2);
		axiomizer.mustExist(shift);
		axiomizer.makeEqual(shift, axiomizer.getEval(2,
				axiomizer.getShift(1, x), axiomizer.getShift(1, i)));
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addDistributePassThroughEval() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"invariant_1(i) => "
				+ "pass_1(eval_2(c, i)) = eval_2(pass_1(c), i)");
		PeggyVertex<O,Integer> c = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> i = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> pass = axiomizer.getPass(1,
				axiomizer.getEval(2, c, i));
		axiomizer.mustBeDistinctLoops(1, 2);
		axiomizer.mustBeInvariant(1, i);
		axiomizer.mustExist(pass);
		axiomizer.makeEqual(pass,
				axiomizer.getEval(2, axiomizer.getPass(1, c), i));
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addDistributeEvalThroughEval() {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"invariant_1(i2) ^ invariant_2(i1) => "
				+ "eval_1(eval_2(x, i2), i1) = eval_2(eval_1(x, i1), i2)");
		PeggyVertex<O,Integer> x = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> i2 = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> i1 = axiomizer.getVariable(2);
		PeggyVertex<O,Integer> eval = axiomizer.getEval(1,
				axiomizer.getEval(2, x, i2), i1);
		axiomizer.mustBeDistinctLoops(1, 2);
		axiomizer.mustBeInvariant(1, i2);
		axiomizer.mustBeInvariant(2, i1);
		axiomizer.mustExist(eval);
		axiomizer.makeEqual(eval,
				axiomizer.getEval(2, axiomizer.getEval(1, x, i1), i2));
		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
}
