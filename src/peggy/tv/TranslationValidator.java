package peggy.tv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import peggy.Logger;
import peggy.analysis.EngineRunner;
import peggy.represent.MergedPEGInfo;
import peggy.represent.PEGInfo;
import util.graph.CRecursiveExpressionGraph.Vertex;
import util.pair.Pair;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This is the abstract translation validation engine. It takes two PEGs 
 * and puts them both into an equality saturation engine, then checks if the
 * corresponding roots are equal once saturation is complete.
 */
public abstract class TranslationValidator<L,P,R> {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("TranslationValidator: " + message);
	}
	
	protected final List<TVListener<L,P,R>> listeners =
		new ArrayList<TVListener<L,P,R>>();
	protected Logger logger;

	public void addListener(TVListener<L,P,R> list) {
		if (!this.listeners.contains(list))
			this.listeners.add(list);
	}
	public void removeListener(TVListener<L,P,R> list) {
		this.listeners.remove(list);
	}
	
	public void setLogger(Logger _logger) {this.logger = _logger;}
	public Logger getLogger() {return this.logger;}

	public abstract EngineRunner<L,P> getEngineRunner();
	protected abstract CPeggyAxiomEngine<L,P> createEngine(
			MergedPEGInfo<L,P,R> mergedpeginfo,
			Map<Vertex<FlowValue<P,L>>, CPEGTerm<L,P>> rootVertexMap);
	protected abstract MergedPEGInfo<L,P,R> mergePEGs(
			PEGInfo<L,P,R> peginfo1,
			PEGInfo<L,P,R> peginfo2);
	protected abstract void enginePostPass(CPeggyAxiomEngine<L,P> engine);
	
	public void validate(
			String methodName1, String methodName2,  
			PEGInfo<L,P,R> peginfo1, PEGInfo<L,P,R> peginfo2) { 
		for (TVListener<L,P,R> list : this.listeners) {
			list.beginValidation(methodName1, methodName2, peginfo1, peginfo2);
		}
		
		MergedPEGInfo<L,P,R> mergedinfo = mergePEGs(peginfo1, peginfo2);

		for (TVListener<L,P,R> list : this.listeners) {
			list.notifyMergedPEGBuilt(mergedinfo);
		}
		
		// check to see if roots are already equal
		boolean allEqual = true;
		for (R arr : mergedinfo.getReturns()) {
			if (!mergedinfo.getReturnVertex1(arr).equals(
					mergedinfo.getReturnVertex2(arr))) {
				allEqual = false;
				break;
			}
		}
		if (allEqual) {
			// no need to run engine
			for (TVListener<L,P,R> list : this.listeners) {
				list.notifyMergedPEGEqual(mergedinfo);
			}
			for (TVListener<L,P,R> list : this.listeners) {
				list.endValidation();
			}
			return;
		}

		Map<Vertex<FlowValue<P,L>>, CPEGTerm<L,P>> rootVertexMap =
			new HashMap<Vertex<FlowValue<P,L>>, CPEGTerm<L,P>>();
		CPeggyAxiomEngine<L,P> engine = 
			this.createEngine(mergedinfo, rootVertexMap);

		for (TVListener<L,P,R> list : this.listeners) {
			list.notifyEngineSetup(engine, rootVertexMap);
		}

		for (final R arr : mergedinfo.getReturns()) {
			eqsat.meminfer.engine.event.EventListener<Pair<CPEGTerm<L,P>,CPEGTerm<L,P>>> listener = 
				new eqsat.meminfer.engine.event.EventListener<Pair<CPEGTerm<L,P>,CPEGTerm<L,P>>>() {
				public boolean notify(Pair<CPEGTerm<L,P>,CPEGTerm<L,P>> pair) {
					for (TVListener<L,P,R> list : listeners) {
						list.notifyReturnsEqual(arr, pair.getFirst(), pair.getSecond());
					}
					return true;
				}
				public boolean canUse(Pair<CPEGTerm<L,P>,CPEGTerm<L,P>> v) {return true;}
			};

			Pair<CPEGTerm<L,P>,CPEGTerm<L,P>> pair = 
				new Pair<CPEGTerm<L,P>,CPEGTerm<L,P>>(
						(CPEGTerm<L,P>)rootVertexMap.get(mergedinfo.getReturnVertex1(arr)),
						(CPEGTerm<L,P>)rootVertexMap.get(mergedinfo.getReturnVertex2(arr)));

			if (engine.getEGraph().watchEquality(
					rootVertexMap.get(mergedinfo.getReturnVertex1(arr)),
					rootVertexMap.get(mergedinfo.getReturnVertex2(arr)),
					listener,
					pair)) {
				// roots already equal, notify
				for (TVListener<L,P,R> list : this.listeners) {
					list.notifyReturnsEqual(arr, pair.getFirst(), pair.getSecond());
				}
			}
		}
		
		EngineRunner<L,P> runner = this.getEngineRunner();
		runner.setLogger(getLogger());
		runner.runEngine(engine);

		this.enginePostPass(engine);
		
		for (TVListener<L,P,R> list : this.listeners) {
			list.notifyEngineCompleted(engine);
		}
		
		if (DEBUG) {
			for (final R arr : mergedinfo.getReturns()) {
				CPEGTerm<L,P> left = 
					(CPEGTerm<L,P>)rootVertexMap.get(mergedinfo.getReturnVertex1(arr));
				CPEGTerm<L,P> right = 
					(CPEGTerm<L,P>)rootVertexMap.get(mergedinfo.getReturnVertex2(arr));
				debug("left return " + arr + " = " + left.getValue());
				debug("right return " + arr + " = " + right.getValue());
			}
		}

		for (TVListener<L,P,R> list : this.listeners) {
			list.endValidation();
		}
	}
}
