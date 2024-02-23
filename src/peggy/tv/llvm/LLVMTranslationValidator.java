package peggy.tv.llvm;

import llvm.instructions.FunctionBody;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.LLVMReturn;
import peggy.tv.TranslationValidator;

/**
 * This is the default abstract implementation of the TV engine for LLVM.
 */
abstract class LLVMTranslationValidator 
extends TranslationValidator<LLVMLabel,LLVMParameter,LLVMReturn> {
	private FunctionBody currentMethod;
	protected void setCurrentMethod(FunctionBody method) {
		this.currentMethod = method;
	}
	public FunctionBody getCurrentMethod() {return this.currentMethod;}
}
