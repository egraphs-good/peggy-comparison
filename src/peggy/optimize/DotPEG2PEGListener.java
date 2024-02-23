package peggy.optimize;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Map;

import peggy.represent.PEGInfo;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This is a PEG2PEGListener that outputs DOT graphs for the various
 * graphs in the saturation process.
 */
public abstract class DotPEG2PEGListener<L,P,R,M> 
implements PEG2PEGListener<L,P,R,M> {
	private M function;
	private final boolean doEPEG;
	private final boolean doOptimalPEG;

	public DotPEG2PEGListener(
			boolean _epeg, 
			boolean _opt) { 
		this.doEPEG = _epeg;
		this.doOptimalPEG = _opt;
	}
	
	public void beginFunction(M _function) {
		this.function = _function;
	}
	
	public void notifyEngineSetup(
			CPeggyAxiomEngine<L,P> engine,
			Map<Vertex<FlowValue<P,L>>,CPEGTerm<L,P>> rootVertexMap) {}
	
	protected abstract String getEPEGFilename(M method);
	public void notifyEngineCompleted(CPeggyAxiomEngine<L,P> engine) {
		if (this.doEPEG) {
			String fileName = getEPEGFilename(function);
			try {
				PrintStream out = new PrintStream(new FileOutputStream(fileName));
				out.println(engine.getEGraph().toString());
				out.close();
			} catch (Throwable t) {}
		}
	}

	protected abstract String getOPTPEGFilename(M method);
	public void notifyRevertPEGBuilt(
			boolean original,
			PEGInfo<L,P,R> peginfo) {
		if (this.doOptimalPEG) {
			String fileName = getOPTPEGFilename(function);
			try {
				PrintStream out = new PrintStream(new FileOutputStream(fileName));
				out.println(peginfo.getGraph().toString());
				out.close();
			} catch (Throwable t) {}
		}
	}
	
	public void endFunction() {}
}
