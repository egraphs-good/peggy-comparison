package peggy.revert;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import peggy.pb.CostModel;
import peggy.pb.EngineExpressionDigraph;
import peggy.pb.PBRunner;
import peggy.pb.PseudoBooleanFormulation;
import peggy.pb.value.PeggyValueFormulationBuilder;
import peggy.represent.PEGInfo;
import peggy.represent.StickyPredicate;
import util.Pattern;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This class is a reversion heuristic that uses a pseudo-boolean solver to 
 * find the best PEG in an EPEG.
 */
public abstract class PseudoBooleanReversionHeuristic<O,P,R,NUMBER extends Number, PBF extends PseudoBooleanFormulation<CPEGTerm<O,P>>> 
extends AbstractReversionHeuristic<O,P,R,NUMBER> {
	private static boolean DEBUG = false;
	private static final void debug(String message) {
		if (DEBUG)
			System.err.println("PseudoBooleanReversionHeuristic: " + message);
	}
	
	protected abstract StickyPredicate<CPEGTerm<O,P>> getStickyPredicate();
	protected abstract Pattern<? super CPEGTerm<O,P>> getNodeInclusionPattern();
	protected abstract PBF getFreshFormulation();
	protected abstract PBRunner<CPEGTerm<O,P>,PBF> getRunner();
	protected abstract int getFormulationTimeout();
	protected abstract int getMaxCost();
	
	public Map<? extends CPEGValue<O,P>, ? extends CPEGTerm<O,P>> chooseReversionNodes(
			final CPeggyAxiomEngine<O,P> engine, 
			PEGInfo<O,P,R> original, 
			Map<? extends Vertex<FlowValue<P,O>>, ? extends CPEGTerm<O,P>> rootVertexMap) {
		
		final peggy.Logger logger = getLogger();
		final peggy.Logger sublogger = logger.getSubLogger();
		
		if (logger!=null) logger.log("Begin PB solving");		
		
		final Map<Vertex<FlowValue<P,O>>, CPEGValue<O,P>> rootvaluemap = 
			new HashMap<Vertex<FlowValue<P,O>>, CPEGValue<O,P>>();
		for (Vertex<FlowValue<P,O>> vertex : original.getGraph().getSignificant()) {
			rootvaluemap.put(vertex, rootVertexMap.get(vertex).getValue());
		}
		
		final Pattern<? super CPEGTerm<O,P>> newpattern = getNodeInclusionPattern();

		final EngineExpressionDigraph<CPEGValue<O,P>,CPEGTerm<O,P>> digraph = 
			new NewEngineExpressionDigraph<O,P>(this.getStickyPredicate()) {
				public Iterable<? extends CPEGValue<O,P>> getValues() {
					return engine.getEGraph().getValueManager().getValues();
				}
				public boolean isRoot(CPEGValue<O,P> value) {
					return rootvaluemap.containsValue(value);
				}
				public Iterable<? extends CPEGValue<O,P>> getRootValues() {
					return rootvaluemap.values();
				}
				public Pattern<? super CPEGTerm<O,P>> getNodeInclusionPattern() {
					return newpattern;
				}
				public boolean isRecursive(CPEGTerm<O, P> node) {
					return node.getOp().isTheta();
				}
				protected boolean haveSameLabel(CPEGTerm<O, P> n1,
						CPEGTerm<O, P> n2) {
					return n1.getOp().equals(n2.getOp());
				}
			};
			
		PeggyValueFormulationBuilder<CPEGValue<O,P>,CPEGTerm<O,P>,NUMBER,EngineExpressionDigraph<CPEGValue<O,P>,CPEGTerm<O,P>>> builder = 
			new PeggyValueFormulationBuilder<CPEGValue<O,P>,CPEGTerm<O,P>,NUMBER,EngineExpressionDigraph<CPEGValue<O,P>,CPEGTerm<O,P>>> () {
				public CostModel<CPEGTerm<O,P>,NUMBER> getCostModel() {
					return PseudoBooleanReversionHeuristic.this.getCostModel();
				}
				public EngineExpressionDigraph<CPEGValue<O,P>,CPEGTerm<O,P>> getGraph() {
					return digraph;
				}
				// this is a hack
				private boolean oldBuildFormulation(PseudoBooleanFormulation<CPEGTerm<O,P>> pbf) {
					return super.buildFormulation(pbf);
				}
				
				public boolean buildFormulation(final PseudoBooleanFormulation<CPEGTerm<O,P>> pbf) {
					ExecutorService service = Executors.newSingleThreadExecutor();
					Future<Boolean> future = 
						service.submit(new Callable<Boolean>() {
							public Boolean call() {
								return oldBuildFormulation(pbf);
							}
						});

					boolean result;
					if (getFormulationTimeout() > 0) {
						try {
							result = future.get(getFormulationTimeout(), TimeUnit.MILLISECONDS);
						} catch (Throwable t) {
							if (sublogger!=null) sublogger.logException("Error running solver", t);
							debug("*** cancelling future ***");
							future.cancel(true);
							result = false;
						}
					} else {
						try {
							result = future.get();
						} catch (Throwable t) {
							if (sublogger!=null) sublogger.logException("Error running solver", t);
							result = false;
							debug("future.get() threw exception");
						}
					}

					service.shutdown();
					return result;
				}
			};

		Set<peggy.pb.Variable<CPEGTerm<O,P>>> used = null;
		try {
			if (sublogger!=null) sublogger.log("Building formulation");
			PBF pbf = this.getFreshFormulation();
			if (builder.buildFormulation(pbf)) {
				pbf.close();
				if (sublogger!=null) sublogger.log("Running PB solver");
				PBRunner<CPEGTerm<O,P>,PBF> pbrunner = this.getRunner();
				used = pbrunner.run(pbf);
			} else {
				debug("buildFormulation returned false");
			}
		} catch (Throwable t) {
			debug("Runner had an exception: " + t);
			if (sublogger!=null) sublogger.logException("Error running PB solver", t);
			return null;
		}
		
		if (used == null) {
			debug("Used set is null");
			if (sublogger!=null) sublogger.log("No result from PB solver");
			return null;
		}
			
		Map<CPEGValue<O,P>, CPEGTerm<O,P>> nodeMap = 
			new HashMap<CPEGValue<O,P>, CPEGTerm<O,P>>();
		for (peggy.pb.Variable<CPEGTerm<O,P>> var : used) {
			if (var.node != null)
				nodeMap.put(var.node.getValue(), var.node);
		}
		
		if (sublogger!=null) sublogger.log("PB solver return nonempty result");
		return nodeMap;
	}
}
