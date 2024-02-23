package peggy.optimize;

import java.util.ArrayList;
import java.util.List;

import peggy.Loggable;
import peggy.Logger;
import peggy.represent.PEGProvider;
import eqsat.OpAmbassador;
import eqsat.revert.RevertCFG;

/**
 * This is an abstract parent of all Optimizers. 
 */
public abstract class Optimizer<CFG,M,L,P,R> implements Loggable {
	protected final List<OptimizerListener<L,P,R,CFG,M>> listeners =
		new ArrayList<OptimizerListener<L,P,R,CFG,M>>();
	
	protected Logger logger;
	
	public void addListener(OptimizerListener<L,P,R,CFG,M> list) {
		if (!this.listeners.contains(list))
			this.listeners.add(list);
	}
	public void removeListener(OptimizerListener<L,P,R,CFG,M> list) {
		this.listeners.remove(list);
	}

	public Logger getLogger() {
		return this.logger;
	}
	public void setLogger(Logger _logger) {
		this.logger = _logger;
	}
	protected abstract boolean canOptimize(M function);
	protected abstract OpAmbassador<L> getOpAmbassador();
	protected abstract PEGProvider<M,L,P,R> getPEGProvider();
	protected abstract CFG getOutputCFG(M function, RevertCFG<L,P,R> revert);
	protected abstract void encodeCFG(CFG cfg, M function);
	
	public abstract boolean optimize(M function) throws Throwable;
}

