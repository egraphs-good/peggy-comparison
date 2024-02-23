package eqsat.meminfer.peggy.axiom;

import eqsat.meminfer.network.peg.PEGNetwork;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;


public class PeggyAxioms<O, P> {
	private final PeggyAxiomSetup<O,P> mSetup;
	
	protected PeggyAxioms(PeggyAxiomSetup<O,P> setup) {mSetup = setup;}

	public final PEGNetwork<O> getPEGNetwork() {return mSetup.getPEGNetwork();}
	public final <T> PeggyAxiomizer<O,T> createAxiomizer(String name) {
		return mSetup.<T>createAxiomizer(name);
	}
	public final CPeggyAxiomEngine<O,P> getEngine() {return mSetup.getEngine();}
}
