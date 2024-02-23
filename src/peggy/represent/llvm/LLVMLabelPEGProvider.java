package peggy.represent.llvm;

import llvm.bitcode.DefaultReferenceResolver;
import llvm.instructions.FunctionBody;
import llvm.values.FunctionValue;
import llvm.values.Module;
import llvm.values.Value;
import peggy.represent.PEGInfo;
import peggy.represent.PEGProvider;

/**
 * This is a PEGProvider that provides PEGs based on FunctionLLVMLabels.
 */
public class LLVMLabelPEGProvider implements PEGProvider<FunctionLLVMLabel,LLVMLabel,LLVMParameter,LLVMReturn> {
	protected final PEGProvider<FunctionBody,LLVMLabel,LLVMParameter,LLVMReturn> bodyProvider;
	protected final Module module;
	
	public LLVMLabelPEGProvider(
			Module _module,
			LLVMOpAmbassador _ambassador) {
		this(_module, new LLVMBodyPEGProvider(new DefaultReferenceResolver(_module), _ambassador));
	}
	public LLVMLabelPEGProvider(
			Module _module, 
			PEGProvider<FunctionBody,LLVMLabel,LLVMParameter,LLVMReturn> _bodyProvider) {
		this.module = _module;
		this.bodyProvider = _bodyProvider;
	}
	
	private FunctionBody getBody(FunctionLLVMLabel label) {
		for (int i = 0; i < this.module.getNumFunctionBodies(); i++) {
			FunctionBody body = this.module.getFunctionBody(i);
			FunctionValue header = body.getHeader();
			if (!header.getType().getPointeeType().equals(label.getType()))
				continue;
			// has same type!
			
			for (String name : this.module.getValueNames()) {
				Value value = this.module.getValueByName(name);
				if (value.equals(header) && name.equals(label.getFunctionName()))
					return body;
			}
		}
		return null;
	}
	
	public boolean canProvidePEG(FunctionLLVMLabel label) {
		FunctionBody body = getBody(label);
		return (body != null) && this.bodyProvider.canProvidePEG(body);
	}

	public PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> getPEG(FunctionLLVMLabel function) {
		FunctionBody body = getBody(function);
		if (body == null)
			throw new IllegalArgumentException("Can't find function body for: " + function.getFunctionName());
		return this.bodyProvider.getPEG(body);
	}
}
