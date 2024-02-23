package peggy.represent.llvm;

import llvm.bitcode.ReferenceResolver;
import llvm.values.AliasValue;
import llvm.values.FunctionValue;
import llvm.values.GlobalVariable;
import llvm.values.FunctionValue.ArgumentValue;

/**
 * This is a ReferenceResolver that can give certain types of 
 * LLVMLabels in addition to the normal resolving.
 */
public interface LabelReferenceResolver extends ReferenceResolver {
	public FunctionLLVMLabel getFunctionLabel(FunctionValue header);
	public GlobalLLVMLabel getGlobalLabel(GlobalVariable global);
	public AliasLLVMLabel getAliasLabel(AliasValue alias);
	public ArgumentLLVMVariable getArgumentVariable(ArgumentValue arg);
}
