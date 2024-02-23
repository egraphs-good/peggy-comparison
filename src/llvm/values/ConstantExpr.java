package llvm.values;

import java.util.Iterator;
import java.util.Map;

import llvm.instructions.Instruction;
import llvm.types.Type;


/**
 * This represents a constant instruction expression, such as GEP of a global.
 * This class is just a wrapper around an instruction, which must have all
 * constant values. (reminder: function and global references count as constants).
 */
public class ConstantExpr extends Value {
	protected final Instruction instruction;
	
	public ConstantExpr(Instruction _inst) {
		if (!(_inst.isBinop() ||
			  _inst.isCast() ||
			  _inst.isGEP() ||
			  _inst.isSelect() ||
			  _inst.isExtractElt() ||
			  _inst.isInsertElt() ||
			  _inst.isShuffleVec() ||
			  _inst.isShuffleVec2_8() ||
			  _inst.isCmp()))
			throw new IllegalArgumentException("Invalid instruction type");
		this.instruction = _inst;
	}
	
	public Instruction getInstruction() {return this.instruction;}
	
	public void ensureConstant() {
		for (Iterator<? extends Value> iter = this.instruction.getValues(); iter.hasNext(); ) {
			iter.next().ensureConstant();
		}
	}
	public Type getType() {return this.instruction.getType();}
	public Iterator<? extends Value> getSubvalues() {
		return this.instruction.getValues();
	}
	public boolean isConstantExpr() {return true;}
	public ConstantExpr getConstantExprSelf() {return this;}
	
	public String toString() {
		return "constant " + this.instruction;
	}
	public boolean equalsValue(Value v) {
		if (!v.isConstantExpr())
			return false;
		ConstantExpr e = v.getConstantExprSelf();
		return this.instruction.equalsInstruction(e.instruction);
	}
	public int hashCode() {
		return this.instruction.hashCode()*31;
	}
	
	protected ConstantExpr rewriteChildren(Map<Value,Value> old2new) {
		Instruction newinst = this.instruction.rewrite(old2new);
		if (newinst != this.instruction)
			return new ConstantExpr(newinst);
		else
			return this;
	}
}
