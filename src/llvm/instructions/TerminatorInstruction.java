package llvm.instructions;

/**
 * This is the parent class of all terminator instructions.
 * Terminator instructions are those that can end a basic block.
 */
public abstract class TerminatorInstruction extends Instruction {
	public final boolean isTerminator() {return true;}
	public TerminatorInstruction getTerminatorSelf() {return this;}
	
	public boolean isRet() {return false;}
	public RetInstruction getRetSelf() {throw new UnsupportedOperationException();}

	public boolean isBr() {return false;}
	public BrInstruction getBrSelf() {throw new UnsupportedOperationException();}

	public boolean isSwitch() {return false;}
	public SwitchInstruction getSwitchSelf() {throw new UnsupportedOperationException();}
	
	public boolean isInvoke() {return false;}
	public InvokeInstruction getInvokeSelf() {throw new UnsupportedOperationException();}
	
	public boolean isUnwind() {return false;}
	public UnwindInstruction getUnwindSelf() {throw new UnsupportedOperationException();}

	public boolean isUnreachable() {return false;}
	public UnreachableInstruction getUnreachableSelf() {throw new UnsupportedOperationException();}
	
	///////// 2.8 instructions ///////////
	
	public boolean isIndirectBR() {return false;}
	public IndirectBRInstruction getIndirectBRSelf() {throw new UnsupportedOperationException();}
	
	public abstract int getNumTargets();
	public abstract BasicBlock getTarget(int i);
	
	public final boolean equalsInstruction(Instruction i) {
		if (!i.isTerminator())
			return false;
		return this.equalsTerminator(i.getTerminatorSelf());
	}
	public abstract boolean equalsTerminator(TerminatorInstruction t);
}
