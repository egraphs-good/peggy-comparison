package llvm.instructions;

import java.util.Iterator;
import java.util.Map;

import llvm.types.Type;
import llvm.types.TypeIterator;
import llvm.values.Value;
import llvm.values.ValueIterator;

/**
 * This class represents all the various binary operations in LLVM.
 * Each of these take two values and perform some computation with them.
 * The type of binop this is is encoded in the Binop instance.
 */
public class BinopInstruction extends Instruction {
	protected final Value LHS, RHS;
	protected final Binop binop;
	
	public BinopInstruction(Binop _binop, Value _LHS, Value _RHS) {
		if (!_binop.validTypes(_LHS.getType(), _RHS.getType()))
			throw new IllegalArgumentException("Wrong types for binop: " + _binop.getLabel() + " " + _LHS.getType() + ", " + _RHS.getType());
		this.binop = _binop;
		this.LHS = _LHS;
		this.RHS = _RHS;
	}
	
	public Binop getBinop() {return this.binop;}
	public Value getLHS() {return this.LHS;}
	public Value getRHS() {return this.RHS;}

	public Type getType() {return this.LHS.getType();}
	public Iterator<? extends Value> getValues() {
		return new ValueIterator(this.LHS, this.RHS);
	}
	public Iterator<? extends Type> getTypes() {
		return new TypeIterator(this.LHS.getType(), this.RHS.getType());
	}
	public boolean isBinop() {return true;}
	public BinopInstruction getBinopSelf() {return this;}

	public String toString() {
		return this.binop.getLabel() + " " + this.LHS + ", " + this.RHS;
	}
	public boolean equalsInstruction(Instruction inst) {
		if (!inst.isBinop())
			return false;
		BinopInstruction b = inst.getBinopSelf();
		return this.binop.equals(b.binop) && this.LHS.equalsValue(b.LHS) && this.RHS.equalsValue(b.RHS);
	}
	public int hashCode() {
		return this.LHS.hashCode()*37 + this.binop.hashCode()*5 + this.RHS.hashCode()*101;
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newlhs = this.LHS.rewrite(old2new);
		Value newrhs = this.RHS.rewrite(old2new);
		if (newlhs == this.LHS && newrhs == this.RHS)
			return this;
		else
			return new BinopInstruction(this.binop, newlhs, newrhs);
	}
}
