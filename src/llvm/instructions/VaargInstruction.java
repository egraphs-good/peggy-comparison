package llvm.instructions;

import java.util.Iterator;
import java.util.Map;

import llvm.types.Type;
import llvm.types.TypeIterator;
import llvm.values.Value;
import llvm.values.ValueIterator;

/**
 * This represents the VAARG instruction.
 * It takes a valist pointer value and a result type.
 */
public class VaargInstruction extends Instruction {
	protected final Value valist;
	protected final Type resultType;
	
	public VaargInstruction(Value _valist, Type _resultType) {
		if (!(_valist.getType().isComposite() && _valist.getType().getCompositeSelf().isPointer()))
			throw new IllegalArgumentException("Valist must be a pointer");
		this.valist = _valist;
		this.resultType = _resultType;
	}
	
	public Value getVAList() {return this.valist;}
	public Type getResultType() {return this.resultType;}
	
	public Type getType() {return this.resultType;}
	public Iterator<? extends Value> getValues() {
		return new ValueIterator(this.valist);
	}
	public Iterator<? extends Type> getTypes() {
		return new TypeIterator(this.valist.getType(), this.resultType);
	}
	public boolean isVaarg() {return true;}
	public VaargInstruction getVaargSelf() {return this;}
	
	public String toString() {
		return "va_arg " + this.valist + ", " + this.resultType;
	}
	public boolean equalsInstruction(Instruction i) {
		if (!i.isVaarg())
			return false;
		VaargInstruction v = i.getVaargSelf();
		return this.valist.equalsValue(v.valist) && this.resultType.equalsType(v.resultType);
	}
	public int hashCode() {
		return this.valist.hashCode()*101 + this.resultType.hashCode()*79;
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newvalist = this.valist.rewrite(old2new);
		if (newvalist == this.valist)
			return this;
		else
			return new VaargInstruction(newvalist, this.resultType);
	}
}
