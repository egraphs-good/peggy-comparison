package llvm.instructions;

import java.util.Iterator;
import java.util.Map;

import llvm.types.Type;
import llvm.types.TypeIterator;
import llvm.values.Value;
import llvm.values.ValueIterator;

/**
 * This represents the UNWIND instruction.
 * It has no parameters, and hence there is a single global instance of
 * this instruction.
 */
public class UnwindInstruction extends TerminatorInstruction {
	public static final UnwindInstruction INSTANCE = new UnwindInstruction();
	
	private UnwindInstruction() {}
	
	public void setName(String _name) {throw new UnsupportedOperationException();}
	public Type getType() {return Type.VOID_TYPE;}
	public Iterator<? extends Value> getValues() {
		return new ValueIterator();
	}
	public Iterator<? extends Type> getTypes() {
		return new TypeIterator();
	}
	public boolean isUnwind() {return true;}
	public UnwindInstruction getUnwindSelf() {return this;}
	
	public int getNumTargets() {return 0;}
	public BasicBlock getTarget(int i) {throw new IndexOutOfBoundsException("" + i);}
	
	public String toString() {
		return "unwind";
	}
	public boolean equalsTerminator(TerminatorInstruction t) {
		return t.isUnwind();		
	}
	public int hashCode() {
		return 26235;
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		return this;
	}
}
