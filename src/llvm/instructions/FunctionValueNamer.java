package llvm.instructions;

import java.util.HashSet;
import java.util.Set;

import llvm.values.FunctionValue;
import llvm.values.Value;
import llvm.values.VirtualRegister;
import llvm.values.FunctionValue.ArgumentValue;

/**
 * This class assigns names to the unnamed values within a function.
 */
public abstract class FunctionValueNamer {
	protected final FunctionBody body;
	
	protected FunctionValueNamer(FunctionBody _body) {
		this.body = _body;
	}
	
	public void assignNames() {
		RegisterAssignment assignment = this.body.getRegisterAssignment();
		FunctionValue header = this.body.getHeader();
		
		Set<Value> namedValues = new HashSet<Value>();
		Set<String> usedNames = new HashSet<String>();
		
		// gather up the used names and the named values
		for (String name : this.body.getValueNames()) {
			usedNames.add(name);
			namedValues.add(this.body.getValueByName(name));
		}
		for (int i = 0; i < this.body.getNumBlocks(); i++) {
			BasicBlock block = this.body.getBlock(i);
			if (block.getName() != null)
				usedNames.add(block.getName());
		}
		
		// name the registers
		for (VirtualRegister reg : assignment.getRegisters()) {
			if (!namedValues.contains(reg)) {
				// register needs a name
				String name = this.getValueName(reg, usedNames);
				this.body.addValueName(name, reg);
				namedValues.add(reg);
				usedNames.add(name);
			}
		}
		
		// name the arguments
		for (int i = 0; i < header.getNumArguments(); i++) {
			ArgumentValue arg = header.getArgument(i);
			if (!namedValues.contains(arg)) {
				// argument needs a name
				String name = this.getValueName(arg, usedNames);
				this.body.addValueName(name, arg);
				namedValues.add(arg);
				usedNames.add(name);
			}
		}
		
		// name the blocks
		for (int i = 0; i < this.body.getNumBlocks(); i++) {
			BasicBlock block = this.body.getBlock(i);
			if (block.getName() == null) {
				String name = this.getBlockName(block, usedNames);
				block.setName(name);
				usedNames.add(name);
			}
		}
	}
	
	protected abstract String getValueName(Value value, Set<? super String> usedNames);
	protected abstract String getBlockName(BasicBlock block, Set<? super String> usedNames);
}
