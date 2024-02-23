package peggy.represent.java;

import soot.Value;

/**
 * This is a CFGInstruction that simply evaluates its argument.
 */
public class EvalCFGInstruction extends CFGInstruction {
	private final Value value;
	
	public EvalCFGInstruction(Value _value) {
		this.value = _value;
	}
	
	public boolean isEval() {return true;}
	public EvalCFGInstruction getEvalSelf() {return this;}
	
	public Value getValue() {return this.value;}
	
	public String toString() {
		return "eval " + value.toString();
	}
}
