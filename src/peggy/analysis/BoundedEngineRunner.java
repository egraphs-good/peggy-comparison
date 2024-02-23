package peggy.analysis;

import peggy.Logger;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This is an engine runner that runs the engine for a certain bound.
 * The bound can either be on time, memory, or iterations.
 */
public abstract class BoundedEngineRunner<L,P> implements EngineRunner<L,P> {
	protected Long memoryUpperBound;	// in bytes
	protected Long iterationUpperBound;	// in iterations
	protected Long timeUpperBound;		// in seconds
	protected Long timeUpdate;
	protected Long iterationUpdate;
	protected Logger logger;
	protected boolean halt = false;

	public BoundedEngineRunner() {}
	public BoundedEngineRunner(long _memory, long _iterations, long _seconds) {
		setMemoryUpperBound(_memory);
		setIterationUpperBound(_iterations);
		setTimeUpperBound(_seconds);
	}
	
	public Logger getLogger() {
		return this.logger;
	}
	public void setLogger(Logger _logger) {
		this.logger = _logger;
	}
	
	public void setTimeUpdate(long time) {
		if (time > 0L)
			timeUpdate = new Long(time);
		else
			timeUpdate = null;
	}
	public void setIterationUpdate(long iters) {
		if (iters > 0L)
			iterationUpdate = iters;
		else
			iterationUpdate = null;	
	}
	
	
	public void setMemoryUpperBound(long memory) {
		if (memory > 0L)
			this.memoryUpperBound = new Long(memory);
		else
			this.memoryUpperBound = null;
	}
	public void setIterationUpperBound(long iters) {
		if (iters > 0L)
			this.iterationUpperBound = new Long(iters);
		else
			this.iterationUpperBound = null;
	}
	public void setTimeUpperBound(long seconds) {
		if (seconds > 0L)
			this.timeUpperBound = new Long(seconds);
		else
			this.timeUpperBound = null;
	}
	
	public Long getMemoryUpperBound() {return this.memoryUpperBound;}
	public Long getIterationUpperBound() {return this.iterationUpperBound;}
	public Long getTimeUpperBound() {return this.timeUpperBound;}
	
	public boolean runEngine(CPeggyAxiomEngine<L,P> engine) {
		final long memBound = (this.memoryUpperBound == null ? -1L : this.memoryUpperBound.longValue());
		final long iterBound = (this.iterationUpperBound == null ? -1L : this.iterationUpperBound.longValue());
		final long timeBound = (this.timeUpperBound == null ? -1L : this.timeUpperBound.longValue());
		this.halt = false;
		
		long starttime = System.currentTimeMillis();
		int iterationsCompleted = 0;
		long lastTimeDiv = 0L;
		long lastIterDiv = 0L;
		
		while (!halt) {
			if (!engine.getEGraph().process()) {
				long now = System.currentTimeMillis();
				notifySaturated(iterationsCompleted, now-starttime);
				return true;
			}
			iterationsCompleted++;
			
			long currenttime = System.currentTimeMillis();
			if (timeBound > 0L) {
				if (currenttime-starttime >= timeBound) {
					notifyTimeBoundReached(iterationsCompleted, currenttime-starttime);
					return false;
				}
			}
			if (timeUpdate != null) { 
				long newTimeDiv = ((currenttime-starttime)/timeUpdate);
				if (lastTimeDiv < newTimeDiv)
					updateEngine(engine);
				lastTimeDiv = newTimeDiv;
			}
			
			if (iterBound > 0L) {
				if (iterationsCompleted >= iterBound) {
					notifyIterationBoundReached(iterationsCompleted, currenttime-starttime);
					return false;
				}
			}
			if (iterationUpdate != null) {
				long newIterDiv = iterationsCompleted/iterationUpdate;
				if (lastIterDiv < newIterDiv)
					updateEngine(engine);
				lastIterDiv = newIterDiv;
			}
			
			if (memBound > 0L) {
				if (getTotalFreeMemory() < memBound) {
					notifyMemoryBoundReached(iterationsCompleted, currenttime-starttime, memBound);
					return false;
				}
			}
		}

		// halted
		long currenttime = System.currentTimeMillis();
		notifyHalted(iterationsCompleted, currenttime-starttime, memBound);
		return false;
	}
	
	public void halt() {
		this.halt = true;
	}
	
	protected abstract void updateEngine(CPeggyAxiomEngine<L,P> engine);
	
	protected abstract void notifySaturated(long iters, long time);
	protected abstract void notifyTimeBoundReached(long iters, long time);
	protected abstract void notifyIterationBoundReached(long iters, long time);
	protected abstract void notifyMemoryBoundReached(long iters, long time, long mem);
	protected abstract void notifyHalted(long iters, long time, long mem);
	
	private static long getTotalFreeMemory() {
		Runtime runtime = Runtime.getRuntime();
		long free = runtime.freeMemory();
		long max = runtime.maxMemory();
		long total = runtime.totalMemory();
		return free + (max-total);
	}
}
