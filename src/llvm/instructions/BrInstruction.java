package llvm.instructions;

import java.util.Iterator;
import java.util.Map;

import llvm.types.Type;
import llvm.types.TypeIterator;
import llvm.values.Value;
import llvm.values.ValueIterator;

/**
 * This represents the BR instruction. 
 * This can either be a condition or unconditional branch. 
 * A conditional branch will have a condition value (of type i1), a true target
 * basic block, and a false target basic block.
 * An unconditional branch will only have a true target basic block.
 */
public class BrInstruction extends TerminatorInstruction {
	protected final Value condition;
	protected final BasicBlock trueTarget, falseTarget;
	// if unconditional branch, 'condition' and 'falseTarget' will be null
	
	public BrInstruction(BasicBlock bb) {
		this.trueTarget = bb;
		this.condition = null;
		this.falseTarget = null;
	}
	public BrInstruction(Value _condition, BasicBlock _trueTarget, BasicBlock _falseTarget) {
		if (!_condition.getType().equals(Type.BOOLEAN_TYPE))
			throw new IllegalArgumentException("Condition value must have boolean type");
		this.condition = _condition;
		this.trueTarget = _trueTarget;
		this.falseTarget = _falseTarget;
	}
	
	public Value getCondition() {return this.condition;}
	public BasicBlock getTrueTarget() {return this.trueTarget;}
	public BasicBlock getFalseTarget() {return this.falseTarget;}
	public boolean isUnconditional() {return this.condition == null;}
	public BasicBlock getUnconditionalTarget() {return this.trueTarget;}

	public Type getType() {return Type.VOID_TYPE;}
	public Iterator<? extends Value> getValues() {
		if (this.condition != null)
			return new ValueIterator(this.condition);
		else
			return new ValueIterator();
	}
	public Iterator<? extends Type> getTypes() {
		if (this.condition != null)
			return new TypeIterator(this.condition.getType());
		else
			return new TypeIterator();
	}
	public boolean isBr() {return true;}
	public BrInstruction getBrSelf() {return this;}
	
	public int getNumTargets() {
		return this.condition==null ? 1 : 2;
	}
	/** Order of targets: 0 = trueTarget, 1 = falseTarget (if any)
	 */
	public BasicBlock getTarget(int i) {
		if (i == 0) return this.trueTarget;
		else if (i == 1 && this.condition != null) return this.falseTarget;
		else
			throw new IndexOutOfBoundsException(""+i);
	}
	
	public String toString() {
		if (this.condition == null) {
			return "br " + this.trueTarget;
		} else {
			return "br " + this.condition + ", " + this.trueTarget + ", " + this.falseTarget;
		}
	}
	public boolean equalsTerminator(TerminatorInstruction t) {
		if (!t.isBr())
			return false;
		BrInstruction b = t.getBrSelf();
		if (this.isUnconditional() && b.isUnconditional()) {
			return this.trueTarget.equals(b.trueTarget);
		} else if (!this.isUnconditional() && !b.isUnconditional()) {
			return this.condition.equalsValue(b.condition) && this.trueTarget.equals(b.trueTarget) && this.falseTarget.equals(b.falseTarget);
		} else {
			return false;
		}
	}
	public int hashCode() {
		if (this.isUnconditional()) {
			return this.trueTarget.hashCode() * 7;
		} else {
			return this.condition.hashCode()*3 + this.trueTarget.hashCode()*101 + this.falseTarget.hashCode()*31;
		}
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		if (this.condition == null)
			return this;
		
		Value newcond = this.condition.rewrite(old2new);
		if (newcond == this.condition)
			return this;
		else
			return new BrInstruction(newcond, this.trueTarget, this.falseTarget);
	}
	
}
