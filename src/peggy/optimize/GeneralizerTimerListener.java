package peggy.optimize;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import peggy.represent.PEGInfo;
import util.Tag;
import eqsat.meminfer.engine.generalize.PostMultiGenEPEG;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;

/**
 * This is a GeneralizerListener that times the intervals between the callbacks.
 */
public class GeneralizerTimerListener<L,P,R,M> 
implements GeneralizerListener<L,P,R,M> {
	private long beginPEGTime;
	private long peg2pegBuiltTime;
	private long optimalPEGBuiltTime;
	private long expressionsTightenedTime;
	private final Map<R,Long> returnEPEGsBuiltMap = 
		new HashMap<R,Long>();
	private long endPEGTime;
	
	public void beginPEG(
			M function,
			PEGInfo<L,P,R> inputPEG, 
			Tag<? extends CPEGTerm<L,P>> termTag) {
		this.beginPEGTime = System.currentTimeMillis();
		this.returnEPEGsBuiltMap.clear();
	}
	public void notifyPEG2PEGBuilt(PEG2PEGOptimizer<L,P,R,M> peg2peg) {
		this.peg2pegBuiltTime = System.currentTimeMillis();
	}
	public void notifyOptimalPEGBuilt(PEGInfo<L,P,R> optimalPEG, boolean lastOriginal) {
		this.optimalPEGBuiltTime = System.currentTimeMillis();
	}
	public void notifyExpressionsTightened(Tag<? extends CPEGTerm<L,P>> newTag) {
		this.expressionsTightenedTime = System.currentTimeMillis();
	}
	public void notifyReturnEPEGsBuilt(
			R arr,
			Collection<? extends PostMultiGenEPEG<L,CPEGTerm<L,P>,CPEGValue<L,P>>> epegs) {
		this.returnEPEGsBuiltMap.put(arr, System.currentTimeMillis());
	}
	public void endPEG() {
		this.endPEGTime = System.currentTimeMillis();
	}
	
	////////////////////////
	
	public long getBeginPEGTime() {return this.beginPEGTime;}
	public long getPEG2PEGBuiltTime() {return this.peg2pegBuiltTime;}
	public long getOptimalPEGBuiltTime() {return this.optimalPEGBuiltTime;}
	public long getExpressionsTightenedTime() {return this.expressionsTightenedTime;}
	
	public Set<R> getGeneralizedReturns() {
		return Collections.unmodifiableSet(this.returnEPEGsBuiltMap.keySet());
	}
	public boolean hasGeneralizedReturn(R arr) {
		return this.returnEPEGsBuiltMap.containsKey(arr);
	}
	public long getReturnEPEGsBuiltTime(R arr) {
		return this.returnEPEGsBuiltMap.get(arr);
	}
	public long getEndPEGTime() {return this.endPEGTime;}
}
