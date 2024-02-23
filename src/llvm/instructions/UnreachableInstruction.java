package llvm.instructions;

import java.util.Iterator;
import java.util.Map;

import llvm.types.Type;
import llvm.types.TypeIterator;
import llvm.values.Value;
import llvm.values.ValueIterator;

/**
 * This represents the UNREACHABLE instruction.
 * It has no parameters, hence there is a single global instance of this 
 * instruction.
 */
public class UnreachableInstruction extends TerminatorInstruction {
	public static final UnreachableInstruction INSTANCE = new UnreachableInstruction();
	
	private UnreachableInstruction() {}
	
	public void setName(String _name) {throw new UnsupportedOperationException();}
	public Type getType() {return Type.VOID_TYPE;}
	public Iterator<? extends Value> getValues() {
		return new ValueIterator();
	}
	public Iterator<? extends Type> getTypes() {
		return new TypeIterator();
	}
	public boolean isUnreachable() {return true;}
	public UnreachableInstruction getUnreachableSelf() {return this;}
	
	public int getNumTargets() {return 0;}
	public BasicBlock getTarget(int i) {throw new IndexOutOfBoundsException(""+i);}
	
	public String toString() {
		return "unreachable";
	}
	public boolean equalsTerminator(TerminatorInstruction t) {
		return t.isUnreachable();
	}
	public int hashCode() {
		return 43269087;
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		return this;
	}
}
