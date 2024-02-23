package peggy.represent.llvm;

import eqsat.BasicOp;

/**
 * Only used for non-(TRUE/FALSE) basicops.
 */
public class BasicOpLLVMLabel extends LLVMLabel {
	protected final BasicOp operator;
	
	public BasicOpLLVMLabel(BasicOp _operator) {
		this.operator = _operator;
	}
	
	public BasicOp getOperator() {return this.operator;}
	
	public boolean isBasicOp() {return true;}
	public BasicOpLLVMLabel getBasicOpSelf() {return this;}
	public boolean equalsLabel(LLVMLabel label) {
		if (!label.isBasicOp()) return false;
		BasicOpLLVMLabel b = label.getBasicOpSelf();
		return this.getOperator().equals(b.getOperator());
	}
	public int hashCode() {
		return this.getOperator().hashCode()*607;
	}
	public String toString() {
		return this.getOperator().name();
	}
	public boolean isRevertible() {return true;}
}
