package peggy.analysis;

import java.util.ArrayList;
import java.util.List;

import eqsat.meminfer.engine.event.Event;
import eqsat.meminfer.engine.proof.Proof;
import eqsat.meminfer.peggy.axiom.PeggyAxiomSetup;
import eqsat.meminfer.peggy.axiom.PeggyAxioms;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;
import eqsat.meminfer.peggy.network.PeggyVertex;

/**
 * This class defines some additional axioms about the phi operator.
 */
public final class TemporaryPhiAxioms<O, P> extends PeggyAxioms<O,P> {
	protected final int maxArgs;
	
	public TemporaryPhiAxioms(PeggyAxiomSetup<O,P> setup, int _maxArgs) {
		super(setup);
		this.maxArgs = _maxArgs;
	}
	
	public void addAll() {
		/*
		for (int args = 1; args <= maxArgs; args++) {
			for (int index = 0; index < args; index++) {
				addDistributeFactorPhi(args, index, true);
				//addDistributeFactorPhi(args, index, false);
			}
		}
		*/
		
		addPhiOverPhiLeftAxiom();
		addPhiOverPhiRightAxiom();
		
		addPhi2Deep(true,true);
		addPhi2Deep(true,false);
		addPhi2Deep(false,true);
		addPhi2Deep(false,false);
	}
	
	public Event<? extends Proof> addPhi2Deep(boolean first, boolean second) {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer(
				"phi over phi 2 deep (" + first + "," + second + ")");
		/**
		 * phi(c1,
		 *     phi(c2,
		 *         phi(c1,t3,f3),
		 *         f2),
		 *     f1) 
		 * =
		 * phi(c1,
		 *     phi(c2,
		 *         t3,
		 *         f2),
		 *     f1)
		 * etc.
		 */
		
		PeggyVertex<O,Integer> c1 = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> c2 = axiomizer.getVariable(1);

		PeggyVertex<O,Integer> t1 = axiomizer.getVariable(2);
		PeggyVertex<O,Integer> f1 = axiomizer.getVariable(3);
		PeggyVertex<O,Integer> t2 = axiomizer.getVariable(4);
		PeggyVertex<O,Integer> f2 = axiomizer.getVariable(5);
		PeggyVertex<O,Integer> t3 = axiomizer.getVariable(6);
		PeggyVertex<O,Integer> f3 = axiomizer.getVariable(7);

		PeggyVertex<O,Integer> phi3 = axiomizer.getPhi(c1, t3, f3);
		PeggyVertex<O,Integer> phi2 = (second ?
				axiomizer.getPhi(c2, phi3, f2) :
				axiomizer.getPhi(c2, t2, phi3));
		PeggyVertex<O,Integer> phi1 = (first ?
				axiomizer.getPhi(c1, phi2, f1) :
				axiomizer.getPhi(c1, t1, phi2));

		
		PeggyVertex<O,Integer> afterphi3 = (first ? t3 : f3);
		PeggyVertex<O,Integer> afterphi2 = (second ?
				axiomizer.getPhi(c2, afterphi3, f2) :
				axiomizer.getPhi(c2, t2, afterphi3));
		PeggyVertex<O,Integer> afterphi1 = (first ?
				axiomizer.getPhi(c1, afterphi2, f1) :
				axiomizer.getPhi(c1, t1, afterphi2));
		
		axiomizer.mustExist(phi1);
		axiomizer.makeEqual(phi1, afterphi1);

		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}

	public Event<? extends Proof> addDistributeThroughPhi(int args, int index) {
		return addDistributeFactorPhi(args, index, true);
	}
	public Event<? extends Proof> addFactorOutPhi(int args, int index) {
		return addDistributeFactorPhi(args, index, false);
	}
	
	private Event<? extends Proof> addDistributeFactorPhi(
			int args, int index, boolean distribute) {
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer("distribute through phi");

		PeggyVertex<O,Integer> c = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> t = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> f = axiomizer.getVariable(2);
		PeggyVertex<O,Integer> phi = axiomizer.getPhi(c, t, f);
		
		List<PeggyVertex<O,Integer>> myParams = 
			new ArrayList<PeggyVertex<O,Integer>>(args);
		List<PeggyVertex<O,Integer>> leftParams = 
			new ArrayList<PeggyVertex<O,Integer>>(args);
		List<PeggyVertex<O,Integer>> rightParams = 
			new ArrayList<PeggyVertex<O,Integer>>(args);
		for (int i = 1; i <= args; i++) {
			if (index+1 == i) {
				myParams.add(phi);
				leftParams.add(t);
				rightParams.add(f);
			} else {
				PeggyVertex<O,Integer> newvar = axiomizer.getVariable(3+i);
				myParams.add(newvar);
				leftParams.add(newvar);
				rightParams.add(newvar);
			}
		}

		PeggyVertex<O,Integer> before = axiomizer.get(null, myParams);

		PeggyVertex<O,Integer> after = 
			axiomizer.getPhi(
					c, 
					axiomizer.get(null, leftParams),
					axiomizer.get(null, rightParams));

		if (true || distribute) {
			axiomizer.mustExist(before);
			axiomizer.makeEqual(before, after);
		} else {
			axiomizer.mustExist(after);
			axiomizer.makeEqual(after, before);
		}
		
		//System.out.println(axiomizer.mGraph);

		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addPhiOverPhiLeftAxiom() {
		// phi(c,phi(c,t1,f1),f2) = phi(c,t1,f2)
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer("phi over phi (true)");

		PeggyVertex<O,Integer> c = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> t1 = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> f1 = axiomizer.getVariable(2);
		PeggyVertex<O,Integer> f2 = axiomizer.getVariable(3);
		PeggyVertex<O,Integer> phi1 = axiomizer.getPhi(c, t1, f1);
		PeggyVertex<O,Integer> before = axiomizer.getPhi(c, phi1, f2);

		PeggyVertex<O,Integer> after = axiomizer.getPhi(c, t1, f2);

		axiomizer.mustExist(before);
		axiomizer.makeEqual(before, after);

		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
	
	public Event<? extends Proof> addPhiOverPhiRightAxiom() {
		// phi(c,t2,phi(c,t1,f1)) = phi(c,t2,f1)
		PeggyAxiomizer<O,Integer> axiomizer = this.<Integer>createAxiomizer("phi over phi (false)");

		PeggyVertex<O,Integer> c = axiomizer.getVariable(0);
		PeggyVertex<O,Integer> t1 = axiomizer.getVariable(1);
		PeggyVertex<O,Integer> t2 = axiomizer.getVariable(2);
		PeggyVertex<O,Integer> f1 = axiomizer.getVariable(3);
		PeggyVertex<O,Integer> phi1 = axiomizer.getPhi(c, t1, f1);
		PeggyVertex<O,Integer> before = axiomizer.getPhi(c, t2, phi1);

		PeggyVertex<O,Integer> after = axiomizer.getPhi(c, t2, f1);

		axiomizer.mustExist(before);
		axiomizer.makeEqual(before, after);

		return getEngine().addPEGAxiom(axiomizer.getAxiom());
	}
}
