package llvm.instructions;

import java.util.Iterator;
import java.util.Map;

import llvm.bitcode.UnsignedLong;
import llvm.types.Type;
import llvm.types.TypeIterator;
import llvm.values.Value;
import llvm.values.ValueIterator;

/**
 * This class represents the VSELECT instruction.
 * It acts just like a select instruction, but is only available in LLVM 2.8.
 */
public class VSelectInstruction extends Instruction {
	protected final Value condition;
	protected final Value trueValue;
	protected final Value falseValue;
	
	public VSelectInstruction(Value _condition, Value _trueValue, Value _falseValue) {
		this.condition = _condition;
		this.trueValue = _trueValue;
		this.falseValue = _falseValue;
		if (!this.trueValue.getType().equalsType(this.falseValue.getType()))
			throw new IllegalArgumentException("True and false values must have same type");
		Type condtype = this.condition.getType();
		if (condtype.isComposite() && 
			condtype.getCompositeSelf().isVector() &&
			condtype.getCompositeSelf().getVectorSelf().getElementType().equalsType(Type.BOOLEAN_TYPE)) {
			final UnsignedLong length = condtype.getCompositeSelf().getVectorSelf().getNumElements();
			Type optype = this.trueValue.getType();
			if (!(optype.isComposite() &&
				  optype.getCompositeSelf().isVector() &&
				  optype.getCompositeSelf().getVectorSelf().getNumElements().equals(length)))
				throw new IllegalArgumentException("Operand type has wrong vector length");
		} else if (!condtype.equalsType(Type.BOOLEAN_TYPE)) 
			throw new IllegalArgumentException("Condition must have type i1 or <N x i1>");
	}
	
	public boolean is2_8Instruction() {return true;}

	public Value getCondition() {return this.condition;}
	public Value getTrueValue() {return this.trueValue;}
	public Value getFalseValue() {return this.falseValue;}
	
	public Type getType() {return this.trueValue.getType();}
	public Iterator<? extends Value> getValues() {
		return new ValueIterator(this.condition, this.trueValue, this.falseValue);
	}
	public Iterator<? extends Type> getTypes() {
		return new TypeIterator(this.condition.getType(), this.trueValue.getType());
	}
	public boolean isVSelect() {return true;}
	public VSelectInstruction getVSelectSelf() {return this;}
	
	public String toString() {
		return "vselect " + this.condition + " " + this.trueValue + " " + this.falseValue;
	}
	public boolean equalsInstruction(Instruction i) {
		if (!i.isVSelect())
			return false;
		VSelectInstruction e = i.getVSelectSelf();
		return this.condition.equalsValue(e.getCondition()) &&
			this.trueValue.equalsValue(e.getTrueValue()) &&
			this.falseValue.equalsValue(e.getFalseValue());
	}
	public int hashCode() {
		return 5*this.condition.hashCode() + 29*this.trueValue.hashCode() + 59*this.falseValue.hashCode();
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newcond = this.condition.rewrite(old2new);
		Value newtrue = this.trueValue.rewrite(old2new);
		Value newfalse = this.falseValue.rewrite(old2new);
		if (newcond == this.condition && 
			newtrue == this.trueValue &&
			newfalse == this.falseValue)
			return this;
		else
			return new VSelectInstruction(newcond, newtrue, newfalse);
	}
}
