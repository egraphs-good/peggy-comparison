package llvm.instructions;

import java.util.Iterator;
import java.util.Map;

import llvm.types.Type;
import llvm.types.TypeIterator;
import llvm.values.Value;
import llvm.values.ValueIterator;

/**
 * This represents the GETRESULT instruction.
 * It takes a struct value and a literal index value and returns the Nth
 * field in the struct. This can only be applied to the result of a multi-return
 * function call.
 */
public class GetResultInstruction extends Instruction {
	protected final Value base;
	protected final int index;
	
	public GetResultInstruction(Value _base, int _index) {
		if (!(_base.getType().isComposite() && _base.getType().getCompositeSelf().isStructure()))
			throw new IllegalArgumentException("Base value must have structure type");
		if (!_base.getType().getCompositeSelf().isElementIndexValid(_index))
			throw new IllegalArgumentException("Index is not valid for the base structure type");
		this.base = _base;
		this.index = _index;
	}
	
	public Value getBase() {return this.base;}
	public int getIndex() {return this.index;}

	public Type getType() {return this.base.getType().getCompositeSelf().getElementType(this.index);}
	public Iterator<? extends Value> getValues() {
		return new ValueIterator(this.base);
	}
	public Iterator<? extends Type> getTypes() {
		return new TypeIterator(this.base.getType());
	}
	public boolean isGetResult() {return true;}
	public GetResultInstruction getGetResultSelf() {return this;}

	public String toString() {
		return "getresult ( " + this.base + ", " + this.index + " )";
	}
	public boolean equalsInstruction(Instruction i) {
		if (!i.isGetResult())
			return false;
		GetResultInstruction g = i.getGetResultSelf();
		return this.base.equalsValue(g.base) && this.index == g.index;
	}
	public int hashCode() {
		return this.base.hashCode()*79 + this.index*17;
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newbase = this.base.rewrite(old2new);
		if (newbase == this.base)
			return this;
		else
			return new GetResultInstruction(newbase, this.index);
	}
}
