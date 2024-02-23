package peggy.ilp;

/**
 * This class describes the linearity of a given <L,P,R> system.
 */
public interface LinearityAmbassador<L,P,R> {
//	int getNumInputs(L t);
	int getNumOutputs(L t);
	
	R getLinearReturn();
	boolean isParameterLinear(P param);
	
	// these are only meant to be called on non-projections
	boolean isOutputLinear(L t, int index);
	boolean isInputLinear(L t, int index);
}
