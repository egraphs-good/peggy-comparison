package peggy.optimize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import peggy.Loggable;
import peggy.Logger;
import peggy.analysis.EngineRunner;
import peggy.represent.PEGExtractor;
import peggy.represent.PEGInfo;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This is an optimizer that goes from input PEG to output PEG.
 * It starts with an input PEG and puts it into an EPEG, performs 
 * equality saturation, and extracts an optimal PEG from the EPEG.
 * 
 * This type of optimizer is generally used as each phase of a 
 * multi-phase optimizer.
 */
public abstract class PEG2PEGOptimizer<L,P,R,M> implements Loggable {
	private static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("PEG2PEGOptimizer: " + message);
	}
	
	protected final List<PEG2PEGListener<L,P,R,M>> listeners;
	protected Logger logger;

	protected PEG2PEGOptimizer() {
		this.listeners = new ArrayList<PEG2PEGListener<L,P,R,M>>();
	}
	
	public void addListener(PEG2PEGListener<L,P,R,M> list) {
		if (!this.listeners.contains(list))
			this.listeners.add(list);
	}
	public void removeListener(PEG2PEGListener<L,P,R,M> list) {
		this.listeners.remove(list);
	}

	public void setLogger(Logger _logger) {
		this.logger = _logger;
	}
	public Logger getLogger() {
		return this.logger;
	}
	
	protected abstract EngineRunner<L,P> getEngineRunner();
	protected abstract PEGExtractor<L,P,R> getExtractor();
	protected abstract OpAmbassador<L> getOpAmbassador();
	protected abstract void setupEngine(
			M method,
			CPeggyAxiomEngine<L,P> engine,
			PEGInfo<L,P,R> peginfo,
			Map<Vertex<FlowValue<P,L>>, CPEGTerm<L,P>> rootVertexMap);
	
	public PEGInfo<L,P,R> optimize(M method, PEGInfo<L,P,R> peginfo) {
		OpAmbassador<L> ambassador = this.getOpAmbassador();
		
		for (PEG2PEGListener<L,P,R,M> list : this.listeners) {
			list.beginFunction(method);
		}
		
		CPeggyAxiomEngine<L,P> engine = 
			new CPeggyAxiomEngine<L,P>(ambassador);
		
		Map<Vertex<FlowValue<P,L>>, CPEGTerm<L,P>> rootVertexMap =
			new HashMap<Vertex<FlowValue<P,L>>, CPEGTerm<L,P>>();
		
		this.setupEngine(method, engine, peginfo, rootVertexMap);
		
		for (PEG2PEGListener<L,P,R,M> list : this.listeners) {
			list.notifyEngineSetup(engine, rootVertexMap);
		}

		EngineRunner<L,P> engineRunner = this.getEngineRunner();
		if (this.getLogger()!=null)
			engineRunner.setLogger(this.getLogger());
		engineRunner.runEngine(engine);

		if (DEBUG) {
			for (R arr : peginfo.getReturns()) {
				debug("Cloud for return " + arr + ": " + 
						rootVertexMap.get(peginfo.getReturnVertex(arr)).getValue());
			}
		}

		for (PEG2PEGListener<L,P,R,M> list : this.listeners) {
			list.notifyEngineCompleted(engine);
		}

		PEGExtractor<L,P,R> extractor = this.getExtractor();
		if (this.getLogger() != null)
			extractor.setLogger(this.getLogger());
		PEGInfo<L,P,R> revertPEGinfo =
			extractor.extractPEG(engine, peginfo, rootVertexMap);

		boolean original = extractor.lastChoseOriginal();

		for (PEG2PEGListener<L,P,R,M> list : this.listeners) {
			list.notifyRevertPEGBuilt(original, revertPEGinfo);
		}
		
		for (PEG2PEGListener<L,P,R,M> list : this.listeners) {
			list.endFunction();
		}
		
		return revertPEGinfo;
	}
}
