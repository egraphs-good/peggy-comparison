package peggy.optimize;

import java.io.FileOutputStream;
import java.io.PrintStream;

import peggy.represent.PEGInfo;
import eqsat.revert.CFGReverter;
import eqsat.revert.ReversionGraph;

/**
 * This is an OptimizerListener that outputs DOT graphs for the various
 * graphs that are built during the saturation process.
 */
public abstract class DotOptimizerListener<L,P,R,CFG,M> 
implements OptimizerListener<L,P,R,CFG,M> {
	private M function;
	private final boolean doOriginalPEG;
	private final boolean doOptimalPEG;
	private final boolean doRevertGraph;
	private final boolean doRevertCFG;
	private final boolean doOutputCFG;
	
	public DotOptimizerListener(
			boolean _original, 
			boolean _opt, 
			boolean _revert, 
			boolean _revertCFG,
			boolean _outputCFG) {
		this.doOriginalPEG = _original;
		this.doOptimalPEG = _opt;
		this.doRevertGraph = _revert;
		this.doRevertCFG = _revertCFG;
		this.doOutputCFG = _outputCFG;
	}
	
	public void beginFunction(M _function) {
		this.function = _function;
	}
	
	protected abstract String getPEGFilename(M method);
	public void notifyOriginalPEGBuilt(PEGInfo<L,P,R> peginfo) {
		if (this.doOriginalPEG) {
			String fileName = getPEGFilename(function);
			try {
				PrintStream out = new PrintStream(new FileOutputStream(fileName));
				out.println(peginfo.getGraph().toString());
				out.close();
			} catch (Throwable t) {}
		}
	}
	
	protected abstract String getOPTPEGFilename(M method);
	public void notifyOptimalPEGBuilt(PEGInfo<L,P,R> peginfo) {
		if (this.doOptimalPEG) {
			String fileName = getOPTPEGFilename(function);
			try {
				PrintStream out = new PrintStream(new FileOutputStream(fileName));
				out.println(peginfo.getGraph().toString());
				out.close();
			} catch (Throwable t) {}
		}
	}
	
	protected abstract String getRevertFilename(M method);
	public void notifyReversionGraphBuilt(ReversionGraph<P,L> result) {
		if (this.doRevertGraph) {
			String fileName = getRevertFilename(function);
			try {
				PrintStream out = new PrintStream(new FileOutputStream(fileName));
				out.println(result.toString());
				out.close();
			} catch (Throwable t) {}
		}
	}

	protected abstract String getRevertCFGFilename(M method);
	public void notifyCFGReverterBuilt(CFGReverter<P,L,R> result) {
		if (this.doRevertCFG) {
			String fileName = getRevertCFGFilename(function);
			try {
				PrintStream out = new PrintStream(new FileOutputStream(fileName));
				out.println(result.getCFG().toString());
				out.close();
			} catch (Throwable t) {}
		}
	}
	
	protected abstract String getOutputCFGFilename(M method);
	public void notifyOutputCFGBuilt(CFG cfg) {
		if (this.doOutputCFG) {
			String filename = getOutputCFGFilename(function);
			try {
				PrintStream out = new PrintStream(new FileOutputStream(filename));
				out.println(cfg.toString());
				out.close();
			} catch (Throwable t) {}
		}
	}
	
	public void endFunction() {}
}
