package llvm.instructions;

import java.util.Iterator;
import java.util.Map;

import llvm.types.Type;
import llvm.types.TypeIterator;
import llvm.values.Value;
import llvm.values.ValueIterator;

/**
 * This represents a LOAD/VOLATILELOAD instruction.
 * It takes a pointer 'loadee' value, and a literal pointer alignment.
 */
public class LoadInstruction extends Instruction {
	protected final Value loadee;
	protected final int alignment;
	protected final boolean isVolatile;
	protected final Type loadType;
	
	public LoadInstruction(Value _loadee, int _alignment, boolean _volatile) {
		if (!(_loadee.getType().isComposite() && _loadee.getType().getCompositeSelf().isPointer()))
			throw new IllegalArgumentException("Loadee value must be a pointer");
		if (!_loadee.getType().getCompositeSelf().getPointerSelf().getPointeeType().isFirstClass())
			throw new IllegalArgumentException("Pointee must be a first class type");
		this.loadee = _loadee;
		this.alignment = _alignment;
		this.isVolatile = _volatile;
		this.loadType = _loadee.getType().getCompositeSelf().getPointerSelf().getPointeeType();
	}
	
	public Value getLoadee() {return this.loadee;} 
	public int getAlignment() {return this.alignment;}
	public boolean isVolatile() {return this.isVolatile;}
	
	public Type getType() {return this.loadType;}
	public Iterator<? extends Value> getValues() {
		return new ValueIterator(this.loadee);
	}
	public Iterator<? extends Type> getTypes() {
		return new TypeIterator(this.loadee.getType(), this.loadType);
	}
	public boolean isLoad() {return true;}
	public LoadInstruction getLoadSelf() {return this;}

	public String toString() {
		return "load [" + this.loadee + "]" + (this.alignment!=0 ? ", alignment=" + this.alignment : "") + (this.isVolatile ? ", volatile" : "");
	}
	public boolean equalsInstruction(Instruction i) {
		if (!i.isLoad())
			return false;
		LoadInstruction l = i.getLoadSelf();
		return this.loadee.equalsValue(l.loadee) && this.alignment==l.alignment && this.isVolatile==l.isVolatile;
	}
	public int hashCode() {
		return this.loadee.hashCode()*17 + this.alignment*67 + (this.isVolatile ? 5 : 7);
	}
	
	protected Instruction rewriteChildren(Map<Value,Value> old2new) {
		Value newloadee = this.loadee.rewrite(old2new);
		if (newloadee == this.loadee)
			return this;
		else
			return new LoadInstruction(newloadee, this.alignment, this.isVolatile);
	}
}
