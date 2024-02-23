package peggy.represent.llvm;

import llvm.instructions.ComparisonPredicate;

/**
 * This is an LLVMLabel for all of the icmp/fcmp instructions in LLVM.
 * signature: (v,v) -> v
 */
public class CmpLLVMLabel extends LLVMLabel {
	protected final ComparisonPredicate predicate;
	
	public CmpLLVMLabel(ComparisonPredicate _predicate) {
		this.predicate = _predicate;
	}
	
	public ComparisonPredicate getPredicate() {return this.predicate;}
	
	public boolean isCmp() {return true;}
	public CmpLLVMLabel getCmpSelf() {return this;}
	public boolean equalsLabel(LLVMLabel label) {
		if (!label.isCmp()) return false;
		CmpLLVMLabel c = label.getCmpSelf();
		return c.getPredicate().equals(this.getPredicate());
	}
	public int hashCode() {
		return this.getPredicate().hashCode()*11;
	}
	public String toString() {
		if (this.getPredicate().isInteger())
			return "ICmp[" + this.getPredicate().getLabel() + "]";
		else
			return "FCmp[" + this.getPredicate().getLabel() + "]";
	}
	public boolean isRevertible() {return true;}
}
