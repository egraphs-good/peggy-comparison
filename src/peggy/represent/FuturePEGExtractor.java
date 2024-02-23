package peggy.represent;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import peggy.revert.ReversionHeuristic;
import util.Tag;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This is a PEGExtractor that uses Futures to extract the PEG in parallel.
 */
public class FuturePEGExtractor<L,P,R> extends AbstractPEGExtractor<L,P,R> {
	protected int timeoutMilliseconds;

	public FuturePEGExtractor(
			Tag<CPEGTerm<L,P>> _termTag,
			ReversionHeuristic<L,P,R,Integer> _heuristic) {
		super(_termTag, _heuristic);
		this.timeoutMilliseconds = 0;
	}
	
	public void setTimeout(int milliseconds) {
		this.timeoutMilliseconds = milliseconds;
	}

	protected Map<? extends CPEGValue<L,P>, ? extends CPEGTerm<L,P>> 
	getNodeMap(
			final CPeggyAxiomEngine<L,P> engine, 
			final PEGInfo<L,P,R> peginfo,
			final Map<Vertex<FlowValue<P,L>>, CPEGTerm<L,P>> rootVertexMap) {
		ExecutorService service = Executors.newSingleThreadExecutor();
		Future<Map<? extends CPEGValue<L,P>,? extends CPEGTerm<L,P>>> future = 
			service.submit(new Callable<Map<? extends CPEGValue<L,P>,? extends CPEGTerm<L,P>>>() {
				public Map<? extends CPEGValue<L,P>, ? extends CPEGTerm<L,P>> call() {
					Map<? extends CPEGValue<L,P>, ? extends CPEGTerm<L,P>> nodeMap =
						heuristic.chooseReversionNodes(engine, peginfo, rootVertexMap);
					return nodeMap;
				}
			});

		Map<? extends CPEGValue<L,P>, ? extends CPEGTerm<L,P>> nodeMap = null;
		if (this.timeoutMilliseconds > 0) {
			try {
				nodeMap = future.get(this.timeoutMilliseconds, TimeUnit.MILLISECONDS);
			} catch (Throwable t) {
				if (getLogger()!=null)
					getLogger().log("Reversion heuristic timed out after " + timeoutMilliseconds);
			}
		} else {
			try {
				nodeMap = future.get();
			} catch (Throwable t) {
				if (getLogger()!=null)
					getLogger().logException("Error in reversion heuristic", t);
			}
		}
		service.shutdown();
		
		return nodeMap;
	}
}
