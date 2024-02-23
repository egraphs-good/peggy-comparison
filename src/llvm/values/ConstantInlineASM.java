package llvm.values;

import java.util.Iterator;
import java.util.Map;

import llvm.types.PointerType;

/**
 * This represents some constant inline assembly code. This is encoded as a string
 * for the ASM, a string describing the constraints, a type which defines the
 * signature of the function, and a boolean telling whether or not this ASM
 * has side effects.
 */
public class ConstantInlineASM extends Value {
	protected final boolean hasSideEffects;
	protected final String asmString, constraintString;
	protected final PointerType type;
	
	public ConstantInlineASM(boolean _hasSideEffects, PointerType _type, String _asmString, String _constraintString) {
		if (!_type.getPointeeType().isFunction())
			throw new IllegalArgumentException("Type must be pointer to function");
		
		this.hasSideEffects = _hasSideEffects;
		this.type = _type;
		this.asmString = _asmString;
		this.constraintString = _constraintString;
	}
	
	public boolean hasSideEffects() {return this.hasSideEffects;}
	public String getASMString() {return this.asmString;}
	public String getConstraintString() {return this.constraintString;}

	public void ensureConstant() {}
	public PointerType getType() {return this.type;}
	public Iterator<? extends Value> getSubvalues() {
		return new ValueIterator();
	}
	public boolean isInlineASM() {return true;}
	public ConstantInlineASM getInlineASMSelf() {return this;}
	
	public String toString() {
		return "asm[" + this.type.getPointeeType() + " \"" + this.asmString + "\", \"" + this.constraintString + "\"]";
	}
	public boolean equalsValue(Value o) {
		if (!o.isInlineASM())
			return false;
		ConstantInlineASM c = o.getInlineASMSelf();
		return 
			this.hasSideEffects == c.hasSideEffects && 
			this.asmString.equals(c.asmString) &&
			this.constraintString.equals(c.constraintString) &&
			this.type.equalsType(c.type);
	}
	public int hashCode() {
		return 
			(this.hasSideEffects ? 487 : 433) + 
			this.asmString.hashCode()*379 + 
			this.constraintString.hashCode()*317 +
			this.type.hashCode()*269;
	}
	
	protected ConstantInlineASM rewriteChildren(Map<Value,Value> old2new) {
		return this;
	}
}
