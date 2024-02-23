package eqsat.meminfer.peggy.axiom;

import eqsat.OpAmbassador;
import eqsat.PEG;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.network.peg.PEGNetwork;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;
import eqsat.meminfer.peggy.network.PeggyAxiomizer;

public final class PeggyAxiomSetup<O, P> {
	private final Network mNetwork;
	private final OpAmbassador<O> mAmbassador;
	private final CPeggyAxiomEngine<O,P> mEngine;
	
	public PeggyAxiomSetup(Network network,
			OpAmbassador<O> ambassador, CPeggyAxiomEngine<O,P> engine) {
		mNetwork = network;
		mAmbassador = ambassador;
		mEngine = engine;
	}
	
	public final PEGNetwork<O> getPEGNetwork() {
		return new PEGNetwork<O>(mNetwork);
	}
	public final OpAmbassador<O> getOpAmbassador() {return mAmbassador;}
	public final <T> PEG<O,T> createPEG() {return new PEG<O,T>(mAmbassador);}
	public final <T> PeggyAxiomizer<O,T> createAxiomizer(String name) {
		return new PeggyAxiomizer<O,T>(name, mNetwork, mAmbassador);
	}
	public final CPeggyAxiomEngine<O,P> getEngine() {return mEngine;}
}
