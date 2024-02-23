package llvm.instructions;

import java.util.Set;

import llvm.values.Value;
import llvm.values.FunctionValue.ArgumentValue;

/**
 * This class assigns arbitrary names to the unnamed functions and values
 * within a module.
 */
public class DefaultFunctionValueNamer extends FunctionValueNamer {
	public DefaultFunctionValueNamer(FunctionBody _body) {
		super(_body);
	}
	
	protected String getValueName(Value value, Set<? super String> usedNames) {
		if (value.isArgument()) {
			int counter = 0;
			ArgumentValue arg = value.getArgumentSelf();
			String name = "arg" + arg.getIndex();
			if (!usedNames.contains(name)) {
				return name;
			}
			
			String prefix = "arg" + arg.getIndex() + "_";
			while (usedNames.contains(name)) {
				name = prefix + (counter++);
			}
			return name;
		} else if (value.isRegister()) {
			int counter = 0;
			String name = "reg" + (counter++);
			while (usedNames.contains(name)) 
				name = "reg" + (counter++);
			return name;
		} else {
			int counter = 0;
			String name = "v" + (counter++);
			while (usedNames.contains(name))
				name = "v" + (counter++);
			return name;
		}
	}
	public String getBlockName(BasicBlock block, Set<? super String> usedNames) {
		int counter = 0;
		String name = "block" + (counter++);
		while (usedNames.contains(name))
			name = "block" + (counter++);
		return name;
	}
}
