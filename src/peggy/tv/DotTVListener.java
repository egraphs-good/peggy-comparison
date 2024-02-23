package peggy.tv;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Map;

import peggy.represent.MergedPEGInfo;
import peggy.represent.PEGInfo;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.FlowValue;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This is a TVListener that outputs DOT graphs for the various graphs
 * that are built during TV.
 */
public abstract class DotTVListener<L,P,R> implements TVListener<L,P,R> {
	private final boolean outputOriginalPEGs;
	private final boolean outputEPEG;
	private final boolean outputMergedPEG;
	
	protected DotTVListener(
			boolean _originals,
			boolean _merged, 
			boolean _epeg) {
		this.outputOriginalPEGs = _originals;
		this.outputMergedPEG = _merged;
		this.outputEPEG = _epeg;
	}

	protected abstract String getOriginalPEG1Filename();
	protected abstract String getOriginalPEG2Filename();
	public void beginValidation(
			String method1,
			String method2,
			PEGInfo<L,P,R> peginfo1,
			PEGInfo<L,P,R> peginfo2) {
		if (this.outputOriginalPEGs) {
			String fileName1 = getOriginalPEG1Filename();
			String fileName2 = getOriginalPEG2Filename();
			try {
				PrintStream out = new PrintStream(new FileOutputStream(fileName1));
				out.println(peginfo1.getGraph());
				out.close();
			} catch (Throwable t) {}
			try {
				PrintStream out = new PrintStream(new FileOutputStream(fileName2));
				out.println(peginfo2.getGraph());
				out.close();
			} catch (Throwable t) {}
		}
	}
	
	protected abstract String getMergedPEGFilename();
	public void notifyMergedPEGBuilt(MergedPEGInfo<L,P,R> merged) {
		if (this.outputMergedPEG) {
			String fileName = getMergedPEGFilename();
			try {
				PrintStream out = new PrintStream(new FileOutputStream(fileName));
				out.println(merged.getGraph());
				out.close();
			} catch (Throwable t) {}
		}
	}
	
	public void notifyMergedPEGEqual(MergedPEGInfo<L,P,R> merged) {}
	
	protected abstract String getEPEGFilename();
	public void notifyEngineCompleted(CPeggyAxiomEngine<L,P> engine) {
		if (this.outputEPEG) {
			String fileName = this.getEPEGFilename();
			try {
				PrintStream out = new PrintStream(new FileOutputStream(fileName));
				out.println(engine.getEGraph().toString());
				out.close();
			} catch (Throwable t) {}
		}
	}
	
	public void notifyEngineSetup(
			CPeggyAxiomEngine<L,P> engine,
			Map<Vertex<FlowValue<P,L>>,CPEGTerm<L,P>> rootVertexMap) {}
	public void notifyReturnsEqual(R arr, CPEGTerm<L,P> root1, CPEGTerm<L,P> root2) {}
	public void endValidation() {}
}
