package peggy.represent.llvm;

/**
 * This is an LLVMLabel that represents LLVM numeral values (literal ints).
 */
public class NumeralLLVMLabel extends LLVMLabel {
	protected final int value;
	
	public NumeralLLVMLabel(int _value) {
		this.value = _value;
	}
	
	public int getValue() {return this.value;}
	
	public boolean isNumeral() {return true;}
	public NumeralLLVMLabel getNumeralSelf() {return this;}
	public boolean equalsLabel(LLVMLabel label) {
		if (!label.isNumeral()) return false;
		NumeralLLVMLabel n = label.getNumeralSelf();
		return n.getValue() == this.getValue();
	}
	public int hashCode() {
		return this.getValue()*79;
	}
	public String toString() {
		return "Numeral[" + this.getValue() + "]";
	}
	public boolean isRevertible() {return true;}
}
