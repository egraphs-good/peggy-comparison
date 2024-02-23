package peggy.represent.llvm;

import llvm.values.Value;

/**
 * This is an LLVMLabel that wraps around LLVM constant values.
 */
public class ConstantValueLLVMLabel extends LLVMLabel {
	protected final Value value;
	
	public ConstantValueLLVMLabel(Value _value) {
		this.value = _value;
	}
	
	public Value getValue() {return this.value;}
	
	public boolean isConstantValue() {return true;}
	public ConstantValueLLVMLabel getConstantValueSelf() {return this;}
	public boolean equalsLabel(LLVMLabel label) {
		if (!label.isConstantValue()) return false;
		ConstantValueLLVMLabel c = label.getConstantValueSelf();
		return this.getValue().equals(c.getValue());
	}
	public int hashCode() {
		return this.getValue().hashCode()*6361;
	}
	public String toString() {
		return this.getValue().toString();
	}
	public boolean isRevertible() {return true;}
}
