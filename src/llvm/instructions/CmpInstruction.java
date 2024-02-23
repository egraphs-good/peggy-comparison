package llvm.instructions;

import java.util.Iterator;
import java.util.Map;

import llvm.bitcode.UnsignedLong;
import llvm.types.Type;
import llvm.types.TypeIterator;
import llvm.types.VectorType;
import llvm.values.Value;
import llvm.values.ValueIterator;

/**
 * This represents the ICMP/FCMP instruction, which takes two values and
 * compares them according to a particular comparison predicate.
 */
public class CmpInstruction extends Instruction {
	protected final ComparisonPredicate predicate;
	protected final Value lhs, rhs;
	protected final Type resultType;
	
	public CmpInstruction(ComparisonPredicate _predicate, Value _lhs, Value _rhs) {
		if (!_predicate.isValid(_lhs.getType(), _rhs.getType()))
			throw new IllegalArgumentException("Invalid operands for predicate: " + _predicate.getLabel());
		if (_lhs.getType().isComposite() && _lhs.getType().getCompositeSelf().isVector()) {
			UnsignedLong length = _lhs.getType().getCompositeSelf().getVectorSelf().getNumElements();
			resultType = new VectorType(Type.BOOLEAN_TYPE, length);
		} else {
			resultType = Type.BOOLEAN_TYPE;
		}

		this.predicate = _predicate;
		this.lhs = _lhs;
		this.rhs = _rhs;
	}
	
	public ComparisonPredicate getPredicate() {return this.predicate;}
	public Value getLHS() {return this.lhs;}
	public Value getRHS() {return this.rhs;}

	public Type getType() {return resultType;}
	public Iterator<? extends Value> getValues() {
		return new ValueIterator(this.lhs, this.rhs);
	}
	public Iterator<? extends Type> getTypes() {
		return new TypeIterator(this.lhs.getType(), this.rhs.getType());
	}
	public boolean isCmp() {return true;}
	public CmpInstruction getCmpSelf() {return this;}
	
	public String toString() {
		if (this.lhs.getType().isFloatingPoint()) {
			return "fcmp " + this.predicate.getLabel() + " (" + this.lhs + ", " + this.rhs + " )";
		} else if (this.lhs.getType().isInteger() ||
				   (this.lhs.getType().isComposite() && this.lhs.getType().getCompositeSelf().isPointer())) {
			return "icmp " + this.predicate.getLabel() + " (" + this.lhs + ", " + this.rhs + " )";
		} else if (this.lhs.getType().getCompositeSelf().getVectorSelf().isInteger()) {
			return "vicmp " + this.predicate.getLabel() + " (" + this.lhs + ", " + this.rhs + " )";
		} else {
			return "vfcmp " + this.predicate.getLabel() + " (" + this.lhs + ", " + this.rhs + " )";
		}
	}
	public boolean equalsInstruction(Instruction o) {
		if (!o.isCmp())
			return false;
		CmpInstruction c = o.getCmpSelf();
		return this.predicate.equals(c.predicate) && this.lhs.equalsValue(c.lhs) && this.rhs.equalsValue(c.rhs);
	}
	public int hashCode() {
		return 
			this.predicate.hashCode()*491 +
			this.lhs.hashCode()*439 + 
			this.rhs.hashCode()*383;
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newlhs = this.lhs.rewrite(old2new);
		Value newrhs = this.rhs.rewrite(old2new);
		if (newlhs == this.lhs && newrhs == this.rhs)
			return this;
		else
			return new CmpInstruction(this.predicate, newlhs, newrhs);
	}
}
