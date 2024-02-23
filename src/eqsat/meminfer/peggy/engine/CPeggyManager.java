package eqsat.meminfer.peggy.engine;

import eqsat.FlowValue;
import eqsat.OpAmbassador;
import eqsat.meminfer.engine.basic.FutureExpression;
import eqsat.meminfer.engine.basic.FutureExpressionGraph;
import eqsat.meminfer.engine.basic.ValueManager;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;
import eqsat.meminfer.engine.peg.CPEGValueManager;
import eqsat.meminfer.engine.peg.EPEGManager;
import eqsat.meminfer.engine.proof.FirstProofManager;
import eqsat.meminfer.engine.proof.ProofManager;

public class CPeggyManager<O, P>
		extends EPEGManager<O,P,CPEGTerm<O,P>,CPEGValue<O,P>> {
	protected final OpAmbassador<O> mOpAmbassador;
	protected final ValueManager<CPEGValue<O,P>> mValueManager;
	protected final ProofManager<CPEGTerm<O,P>,CPEGValue<O,P>> mProofManager;
	protected final CPEGTerm<O,P> mTrue, mFalse;
	
	public CPeggyManager(OpAmbassador<O> ambassador) {
		this(ambassador, new FirstProofManager<CPEGTerm<O,P>,CPEGValue<O,P>>());
	}
	public CPeggyManager(OpAmbassador<O> ambassador,
			ProofManager<CPEGTerm<O,P>,CPEGValue<O,P>> proofManager) {
		this(ambassador, new CPEGValueManager<O,P>(), proofManager);
	}
	protected CPeggyManager(OpAmbassador<O> ambassador,
			ValueManager<CPEGValue<O,P>> valueManager,
			ProofManager<CPEGTerm<O,P>,CPEGValue<O,P>> proofManager) {
		super(valueManager);
		mValueManager = valueManager;
		mProofManager = proofManager;
		mOpAmbassador = ambassador;
		FutureExpressionGraph<FlowValue<P,O>,CPEGTerm<O,P>,CPEGValue<O,P>> graph
				= new FutureExpressionGraph();
		FutureExpression<FlowValue<P,O>,CPEGTerm<O,P>,CPEGValue<O,P>> t
				= graph.getExpression(FlowValue.<P,O>createTrue());
		FutureExpression<FlowValue<P,O>,CPEGTerm<O,P>,CPEGValue<O,P>> f
				= graph.getExpression(FlowValue.<P,O>createFalse());
		addExpressions(graph);
		mTrue = t.getTerm();
		mFalse = f.getTerm();
	}
	
	protected CPEGTerm<O,P> createTerm(
			FutureExpression<FlowValue<P,O>,CPEGTerm<O,P>,CPEGValue<O,P>>
			expression) {
		return new CPEGTerm<O,P>(getPEGTermConstructor(expression));
	}
	
	public final ValueManager<CPEGValue<O,P>> getValueManager() {
		return mValueManager;
	}
	public final ProofManager<CPEGTerm<O,P>,CPEGValue<O,P>> getProofManager() {
		return mProofManager;
	}
	
	public final OpAmbassador<O> getOpAmbassador() {return mOpAmbassador;}

	public final CPEGTerm<O,P> getTrue() {return mTrue;}
	public final CPEGTerm<O,P> getFalse() {return mFalse;}
}
