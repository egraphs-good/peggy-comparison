package peggy.represent.java;

import soot.Value;

/**
 * This is a CFGInstruction that encodes conditional branches.
 */
public class IfCFGInstruction extends CFGInstruction {
	private final Value condition;

	public IfCFGInstruction(Value _cond) {
		this.condition = _cond;
	}
	
	public Value getCondition() {return this.condition;}
	public boolean isIf() {return true;}
	public IfCFGInstruction getIfSelf() {return this;}
	
	public String toString() {
		return "if (" + condition + ")";
	}
}
