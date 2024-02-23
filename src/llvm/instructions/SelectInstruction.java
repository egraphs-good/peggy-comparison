package llvm.instructions;

import java.util.Iterator;
import java.util.Map;

import llvm.types.Type;
import llvm.types.TypeIterator;
import llvm.values.Value;
import llvm.values.ValueIterator;

/**
 * This represents a SELECT instruction.
 * It takes a condition value (either i1 or vector of i1), a true value and a false value.
 * The true and false value must be the same type.
 * If the condition is a vector of i1, then the true and false values must also be
 * vectors of the same length, and their elements will be chosen piecewise by
 * the condition mask.
 */
public class SelectInstruction extends Instruction {
	protected final Value condition;
	protected final Value trueValue;
	protected final Value falseValue;
	
	public SelectInstruction(Value _condition, Value _trueValue, Value _falseValue) {
		if (!_condition.getType().equals(Type.getIntegerType(1)))
			throw new IllegalArgumentException("Condition must have i1 type");
		if (!_trueValue.getType().equals(_falseValue.getType()))
			throw new IllegalArgumentException("true and false values must have same type");
		if (!_trueValue.getType().isFirstClass())
			throw new IllegalArgumentException("values must have first class type");
		
		this.condition = _condition;
		this.trueValue = _trueValue;
		this.falseValue = _falseValue;
	}
	
	public Value getCondition() {return this.condition;}
	public Value getTrueValue() {return this.trueValue;}
	public Value getFalseValue() {return this.falseValue;}

	public Type getType() {return this.trueValue.getType();}
	public Iterator<? extends Value> getValues() {
		return new ValueIterator(this.condition, this.trueValue, this.falseValue);
	}
	public Iterator<? extends Type> getTypes() {
		return new TypeIterator(this.condition.getType(), this.trueValue.getType(), this.falseValue.getType());
	}
	public boolean isSelect() {return true;}
	public SelectInstruction getSelectSelf() {return this;}
	
	public String toString() {
		return "select ( " + this.condition.toString() + ", " + this.trueValue.toString() + ", " + this.falseValue.toString() + " )";
	}
	public boolean equalsInstruction(Instruction o) {
		if (!o.isSelect())
			return false;
		SelectInstruction c = o.getSelectSelf();
		return 
			this.condition.equalsValue(c.condition) &&
			this.trueValue.equalsValue(c.trueValue) &&
			this.falseValue.equalsValue(c.falseValue);
	}
	public int hashCode() {
		return 
			this.condition.hashCode()*193 +
			this.trueValue.hashCode()*197 + 
			this.falseValue.hashCode()*199;
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newcond = this.condition.rewrite(old2new);
		Value newtrue = this.trueValue.rewrite(old2new);
		Value newfalse = this.falseValue.rewrite(old2new);
		if (newcond == this.condition && newtrue == this.trueValue && newfalse == this.falseValue)
			return this;
		else
			return new SelectInstruction(newcond, newtrue, newfalse);
	}
}
