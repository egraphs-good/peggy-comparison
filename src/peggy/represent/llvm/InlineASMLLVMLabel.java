package peggy.represent.llvm;

import llvm.values.ConstantInlineASM;

/**
 * This is an LLVMLabel for ConstantInlineASM values.
 */
public class InlineASMLLVMLabel extends LLVMLabel {
	protected final ConstantInlineASM asm;
	
	public InlineASMLLVMLabel(ConstantInlineASM _asm) {
		this.asm = _asm;
	}
	
	public ConstantInlineASM getASM() {return this.asm;}
	
	public boolean isInlineASM() {return true;}
	public InlineASMLLVMLabel getInlineASMSelf() {return this;}
	public boolean equalsLabel(LLVMLabel label) {
		if (!label.isInlineASM()) return false;
		InlineASMLLVMLabel i = label.getInlineASMSelf();
		return this.getASM().equals(i.getASM());
	}
	public int hashCode() {
		return this.getASM().hashCode()*479;
	}
	public String toString() {
		return "Inline asm";
	}
	public boolean isRevertible() {return true;}
}
