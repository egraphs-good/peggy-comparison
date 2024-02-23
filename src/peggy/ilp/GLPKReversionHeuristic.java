package peggy.ilp;

import java.io.File;
import java.io.IOException;
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
import peggy.represent.PEGInfo;
import peggy.represent.StickyPredicate;
import peggy.revert.AbstractReversionHeuristic;
import peggy.revert.NewEngineExpressionDigraph;
import util.Pattern;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This is a reversion heuristic that relies on a GLPK formulation and runs 
 * GLPK on that formulation to choose a PEG from the EPEG.
 */
public abstract class GLPKReversionHeuristic<O,P,R> 
extends AbstractReversionHeuristic<O,P,R,Integer> {
	private static boolean DEBUG = false;
	private static final void debug(String message) {
		if (DEBUG)
			System.err.println("GLPKReversionHeuristic: " + message);
	}
	
	protected abstract File getFreshBackingFile();
	protected abstract StickyPredicate<CPEGTerm<O,P>> getStickyPredicate();
	protected abstract Pattern<? super CPEGTerm<O,P>> getNodeInclusionPattern();
	protected abstract GLPKRunner<O,P> getRunner();
	protected abstract int getFormulationTimeout();
	protected abstract int getMaxCost();
	protected abstract int getMaxILPFileSize();
	
	public Map<? extends CPEGValue<O,P>, ? extends CPEGTerm<O,P>> chooseReversionNodes(
			final CPeggyAxiomEngine<O,P> engine, 
			PEGInfo<O,P,R> original, 
			Map<? extends Vertex<FlowValue<P,O>>, ? extends CPEGTerm<O,P>> rootVertexMap) {

		final peggy.Logger logger = getLogger();
		final peggy.Logger sublogger = logger.getSubLogger();

		if (logger!=null) logger.log("Begin GLPK solving");
		
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
			
			
		final File backingFile = this.getFreshBackingFile();
		final GLPKFormulation<O,P> form = 
			new GLPKFormulation<O,P>(backingFile, getMaxILPFileSize(), digraph) {
				public CostModel<CPEGTerm<O,P>,Integer> getCostModel() {
					return GLPKReversionHeuristic.this.getCostModel();
				}
				private void oldWriteFormulation() {
					try {
						super.writeFormulation();
					} catch (Throwable t) {
						debug("super.writeFormulation had an error");
						if (sublogger!=null) sublogger.logException("Cannot write GLPK formulation", t);
						if (DEBUG) t.printStackTrace();
						throw new RuntimeException(t);
					}
				}
				public void writeFormulation() throws IOException {
					ExecutorService service = Executors.newSingleThreadExecutor();
					Future<Void> future = 
						service.submit(new Callable<Void>() {
							public Void call() {
								oldWriteFormulation();
								return null;
							}
						});

					if (getFormulationTimeout() > 0) {
						try {
							future.get(getFormulationTimeout(), TimeUnit.MILLISECONDS);
						} catch (Throwable t) {
							debug("*** cancelling future ***");
							if (sublogger!=null) sublogger.logException("Error running formulation builder", t);
							future.cancel(true);
							throw new IOException("Future had a problem", t);
						}
					} else {
						try {
							future.get();
						} catch (Throwable t) {
							if (sublogger!=null) sublogger.logException("Error running formulation builder", t);
							debug("future.get() threw exception");
							throw new IOException("Future had a problem", t);
						}
					}
					service.shutdown();
				}
			};

		Set<CPEGTerm<O,P>> used = null;
		try {
			if (sublogger!=null) sublogger.log("Writing formulation");
			form.writeFormulation();
			if (sublogger!=null) sublogger.log("Running solver");
			GLPKRunner<O,P> pbrunner = this.getRunner();
			used = pbrunner.run(form);
		} catch (Throwable t) {
			debug("Runner had an exception: " + t);
			if (sublogger!=null) sublogger.logException("Error running GLPK", t);
			return null;
		}
		
		if (used == null) {
			debug("Used set is null");
			if (sublogger!=null) sublogger.log("No result from ILP solver");
			return null;
		}
			
		Map<CPEGValue<O,P>, CPEGTerm<O,P>> nodeMap = 
			new HashMap<CPEGValue<O,P>, CPEGTerm<O,P>>();
		for (CPEGTerm<O,P> var : used) {
			nodeMap.put(var.getValue(), var);
		}
		
		if (sublogger!=null) sublogger.log("GLPK solver returned nonempty result");
		return nodeMap;
	}
}
