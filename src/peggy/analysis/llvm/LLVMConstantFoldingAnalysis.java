package peggy.analysis.llvm;

import llvm.instructions.ComparisonPredicate;
import peggy.analysis.ConstantFolder;
import peggy.analysis.ConstantFoldingAnalysis;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.StringAnnotationLLVMLabel;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This class was used to replace the built-in constant folder but is now
 * mostly redundant.
 */
public abstract class LLVMConstantFoldingAnalysis 
extends ConstantFoldingAnalysis<LLVMLabel,LLVMParameter> {
	public LLVMConstantFoldingAnalysis(
			ConstantFolder<LLVMLabel> folder,
			Network network, 
			CPeggyAxiomEngine<LLVMLabel, LLVMParameter> engine) {
		super(folder, network, engine);
	}
	protected boolean isConstant(LLVMLabel label) {
		return label.isConstantValue() || 
			label.isType() ||
			label.isNumeral() ||
			label.isInlineASM() ||
			label.isParamAttr();
	}
	protected boolean isValidBinop(LLVMLabel label) {
		if (label.isBinop()) {
			return true;
		} else if (label.isCmp()) {
			ComparisonPredicate pred = label.getCmpSelf().getPredicate();
			if (pred.isInteger()) {
				return true;
			} else {
				// TODO
				return false;
			}
		} else if (label.isCast()) {
			switch (label.getCastSelf().getOperator()) {
			case Bitcast:
			case ZExt:
			case SExt:
			case PtrToInt:
			case IntToPtr:
			case Trunc:
			case SIToFP:
			case UIToFP:
				return true;
			default:
				return false;	
			}
		} else
			return false;
	}
	protected LLVMLabel makeAnnotation(String name) {
		return new StringAnnotationLLVMLabel(name);
	}
}
