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
 * This instruction completely replaces ShuffleVecInstruction in 2.8.
 */
public class ShuffleVec2_8Instruction extends Instruction {
	protected final Value operand1, operand2, mask;
	protected final VectorType resultType;

	public ShuffleVec2_8Instruction(Value _operand1, Value _operand2, Value _mask) {
		this.operand1 = _operand1;
		this.operand2 = _operand2;
		this.mask = _mask;
		
		Type type1 = this.operand1.getType();
		if (!(type1.isComposite() && type1.getCompositeSelf().isVector()))
			throw new IllegalArgumentException("Expecting vector type");
		if (!type1.equalsType(this.operand2.getType()))
			throw new IllegalArgumentException("Operands must have same type");
		Type eltType = type1.getCompositeSelf().getVectorSelf().getElementType();
		
		Type masktype = this.mask.getType();
		if (!(masktype.isComposite() && 
			  masktype.getCompositeSelf().isVector() &&
			  masktype.getCompositeSelf().getVectorSelf().getElementType().equalsType(Type.getIntegerType(32))))
			throw new IllegalArgumentException("Mask type must be <N x i32>");
		UnsignedLong length = masktype.getCompositeSelf().getVectorSelf().getNumElements();
		this.resultType = new VectorType(eltType, length);
	}
	
	public boolean is2_8Instruction() {return true;}

	public Value getOperand1() {return this.operand1;}
	public Value getOperand2() {return this.operand2;}
	public Value getMask() {return this.mask;}
	
	public VectorType getType() {return this.resultType;}
	public Iterator<? extends Value> getValues() {
		return new ValueIterator(this.operand1, this.operand2, this.mask);
	}
	public Iterator<? extends Type> getTypes() {
		return new TypeIterator(this.operand1.getType(), this.mask.getType(), this.resultType);
	}
	public boolean isShuffleVec2_8() {return true;}
	public ShuffleVec2_8Instruction getShuffleVec2_8Self() {return this;}
	
	public String toString() {
		return "shufflevec " + this.operand1 + ", " + this.operand2 + ", " + this.mask;
	}
	public boolean equalsInstruction(Instruction i) {
		if (!i.isShuffleVec2_8())
			return false;
		ShuffleVec2_8Instruction e = i.getShuffleVec2_8Self();
		return this.getOperand1().equalsValue(e.getOperand1()) &&
			this.getOperand2().equalsValue(e.getOperand2()) &&
			this.getMask().equalsValue(e.getMask());
	}
	public int hashCode() {
		return 101*this.getOperand1().hashCode() + 61*this.getOperand2().hashCode() + 3*this.getMask().hashCode();
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newop1 = this.operand1.rewrite(old2new);
		Value newop2 = this.operand2.rewrite(old2new);
		Value newmask = this.mask.rewrite(old2new);
		if (newop1 == this.operand1 && 
			newop2 == this.operand2 &&
			newmask == this.mask)
			return this;
		else
			return new ShuffleVec2_8Instruction(newop1, newop2, newmask);
	}
}
