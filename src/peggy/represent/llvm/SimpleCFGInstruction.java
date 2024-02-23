package peggy.represent.llvm;

import llvm.instructions.Instruction;
import llvm.instructions.TerminatorInstruction;

/**
 * This is a CFGInstruction that wraps around the LLVM instructions that 
 * don't need special attention.
 * Doesn't allow BR or SWITCH.
 */
public class SimpleCFGInstruction extends CFGInstruction {
	protected final Instruction instruction;

	public SimpleCFGInstruction(Instruction _inst) {
		if (_inst.isTerminator()) {
			TerminatorInstruction term = _inst.getTerminatorSelf();
			if (term.isBr() || term.isSwitch())
				throw new IllegalArgumentException("Br and Switch instructions are not simple cfg instructions");
		}
		this.instruction = _inst;
	}

	public boolean isSimple() {return true;}
	public SimpleCFGInstruction getSimpleSelf() {return this;}

	public Instruction getInstruction() {return this.instruction;}
	
	public String toString() {
		return this.instruction.toString();
	}
}
