package peggy.analysis;

import peggy.Logger;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * Represents any object that takes control of the running of the saturation 
 * engine. "Running" the engine generally implies calling the "process()" method
 * of the CPeggyAxiomEngine over and over, while possibly tracking its
 * execution or performing some actions in between iterations.
 */
public interface EngineRunner<L,P> {
	public Logger getLogger();
	public void setLogger(Logger logger);
	public boolean runEngine(CPeggyAxiomEngine<L,P> engine);
	public void halt();
}
