package peggy.represent.llvm;

import llvm.instructions.Binop;

/**
 * This is an LLVMLabel that represents a binary operator LLVM instruction.
 *
 * signature: (v,v) -> v
 */
public class BinopLLVMLabel extends LLVMLabel {
	protected final Binop binop;
	
	public BinopLLVMLabel(Binop _binop) {
		this.binop = _binop;
	}
	
	public Binop getOperator() {return this.binop;}
	
	public boolean isBinop() {return true;}
	public BinopLLVMLabel getBinopSelf() {return this;}
	public boolean equalsLabel(LLVMLabel label) {
		if (!label.isBinop()) return false;
		BinopLLVMLabel b = label.getBinopSelf();
		return this.getOperator().equals(b.getOperator());
	}
	public int hashCode() {
		return this.getOperator().hashCode()*7;
	}
	public String toString() {
		return "Binop[" + this.getOperator().name() + "]";
	}
	public boolean isRevertible() {return true;}
}
