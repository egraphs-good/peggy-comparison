package peggy.represent.llvm;

import llvm.bitcode.LLVMUtils;
import llvm.bitcode.ReferenceResolver;
import llvm.instructions.FunctionBody;
import peggy.represent.PEGInfo;
import peggy.represent.PEGProvider;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.APEG;
import eqsat.Flow;
import eqsat.FlowValue;

/**
 * This is a PEGProvider that provides PEG based on FunctionBody instances.
 */
public class LLVMBodyPEGProvider implements PEGProvider<FunctionBody,LLVMLabel,LLVMParameter,LLVMReturn> {
	protected final ReferenceResolver resolver;
	protected final LLVMOpAmbassador ambassador;
	
	public LLVMBodyPEGProvider(
			ReferenceResolver _resolver,
			LLVMOpAmbassador _ambassador) {
		this.resolver = _resolver;
		this.ambassador = _ambassador;
	}
	
	public boolean canProvidePEG(FunctionBody body) {
		return !LLVMUtils.containsLabelParameters(body) && 
			!LLVMUtils.containsIndirectBranches(body);
	}

	public PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> getPEG(FunctionBody function) {
		if (!canProvidePEG(function))
			throw new IllegalArgumentException("Cannot convert function body to PEG");
		
		LLVMCFG cfg = new FunctionBodyLLVMCFG(
				this.ambassador, 
				function, 
				this.resolver);
		cfg.setGEPForcingPolicy(this.ambassador.getGEPForcingPolicy());
		Flow<LLVMCFG,LLVMBlock,LLVMVariable> flow = 
			new Flow<LLVMCFG,LLVMBlock,LLVMVariable>(cfg);
		APEG<LLVMCFG,LLVMBlock,LLVMVariable,LLVMLabel,LLVMParameter> apeg
		= new APEG<LLVMCFG,LLVMBlock,LLVMVariable,LLVMLabel,LLVMParameter>(cfg);
		flow.getReturn(apeg);
		
		flow = null; // last usage of flow
		
		Vertex<FlowValue<LLVMParameter,LLVMLabel>> sigma = 
			apeg.getReturn().evaluate(cfg.getReturnVariable(LLVMReturn.SIGMA));
		sigma.makeSignificant();
		Vertex<FlowValue<LLVMParameter,LLVMLabel>> value = 
			apeg.getReturn().evaluate(cfg.getReturnVariable(LLVMReturn.VALUE));
		value.makeSignificant();

		return new LLVMPEGInfo(apeg.getValues(),value,sigma);
	}
}
