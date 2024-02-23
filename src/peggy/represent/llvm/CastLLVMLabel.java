package peggy.represent.llvm;

import llvm.instructions.Cast;

/**
 * This is an LLVMLabel that represents a cast LLVM instruction.
 * signature: (type,v) -> v
 */
public class CastLLVMLabel extends LLVMLabel {
	protected final Cast cast;
	
	public CastLLVMLabel(Cast _cast) {
		this.cast = _cast;
	}
	
	public Cast getOperator() {return this.cast;}
	
	public boolean isCast() {return true;}
	public CastLLVMLabel getCastSelf() {return this;}
	public boolean equalsLabel(LLVMLabel label) {
		if (!label.isCast()) return false;
		CastLLVMLabel c = label.getCastSelf();
		return this.getOperator().equals(c.getOperator());
	}
	public int hashCode() {
		return this.getOperator().hashCode()*5;
	}
	public String toString() {
		return "Cast[" + this.getOperator().name() + "]";
	}
	public boolean isRevertible() {return true;}
}
