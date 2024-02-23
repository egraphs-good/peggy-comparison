package peggy.analysis.llvm;

import java.util.LinkedList;
import java.util.Map;

import peggy.analysis.PEGInvarianceTagger;
import peggy.analysis.PhiCollapserAnalysis;
import peggy.represent.MutablePEG;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMOperator;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.LLVMReturn;
import eqsat.FlowValue;

/**
 * This class represents a static phi-collapsing analysis that runs over the
 * initial PEG.
 */
public class LLVMPhiCollapserAnalysis 
extends PhiCollapserAnalysis<LLVMLabel,LLVMParameter,LLVMReturn> {
	public LLVMPhiCollapserAnalysis(int _threshold) {
		super(_threshold);
	}
	
	protected boolean isDomainPhi(
			MutablePEG<FlowValue<LLVMParameter,LLVMLabel>,LLVMReturn>.MutableVertex condition,
			MutablePEG<FlowValue<LLVMParameter,LLVMLabel>,LLVMReturn>.MutableVertex current) {
		if (current.getLabel().isDomain() && current.getLabel().getDomain().isSimple()) {
			LLVMOperator op = current.getLabel().getDomain().getSimpleSelf().getOperator();
			switch (op) {
			case SELECT:
			case VSELECT:
				return current.getChild(0).equals(condition);
			}
		}
		return false;
	}
	
	protected MutablePEG<FlowValue<LLVMParameter,LLVMLabel>,LLVMReturn>.MutableVertex
	handleDomainPhi(
			MutablePEG<FlowValue<LLVMParameter,LLVMLabel>,LLVMReturn>.MutableVertex condition, 
			boolean which, 
			int invariance, 
			MutablePEG<FlowValue<LLVMParameter,LLVMLabel>,LLVMReturn>.MutableVertex current, 
			LinkedList<MutablePEG<FlowValue<LLVMParameter,LLVMLabel>,LLVMReturn>.MutableVertex> childList,
			Map<MutablePEG<FlowValue<LLVMParameter,LLVMLabel>,LLVMReturn>.MutableVertex,MutablePEG<FlowValue<LLVMParameter,LLVMLabel>,LLVMReturn>.MutableVertex> cache,
			LinkedList<MutablePEG<FlowValue<LLVMParameter,LLVMLabel>,LLVMReturn>.MutableVertex> phis,			
			PEGInvarianceTagger<LLVMLabel,LLVMParameter,LLVMReturn> tagger) {
		// found one! replace!
		MutablePEG<FlowValue<LLVMParameter,LLVMLabel>,LLVMReturn>.MutableVertex result = 
			which ? current.getChild(1) : current.getChild(2);
			
		result = replacePhis(
				0,
				condition, 
				which, 
				invariance, 
				result, 
				childList, 
				cache, 
				phis,
				tagger);

		if (!cache.containsKey(current))
			cache.put(current, result);
		return result;
	}
}
