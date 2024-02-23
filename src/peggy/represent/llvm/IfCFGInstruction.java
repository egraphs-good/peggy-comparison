package peggy.represent.llvm;

import llvm.values.Value;

/**
 * This is a CFGInstruction that represents conditional branches in a LLVMCFG.
 */
public class IfCFGInstruction extends CFGInstruction {
	protected Value condition;
	
	public IfCFGInstruction(Value _condition) {
		this.condition = _condition;
	}
	
	public Value getCondition() {return this.condition;}
	public boolean isIf() {return true;}
	public IfCFGInstruction getIfSelf() {return this;}
	
	public String toString() {
		return "if(" + this.condition + ")";
	}
}
