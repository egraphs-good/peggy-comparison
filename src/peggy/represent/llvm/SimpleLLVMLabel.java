package peggy.represent.llvm;

import java.util.EnumMap;
import java.util.Map;

/**
 * This is an LLVMLabel that acts as a wrapper around an LLVMOperator.
 */
public class SimpleLLVMLabel extends LLVMLabel {
	private static final Map<LLVMOperator,SimpleLLVMLabel> cache = 
		new EnumMap<LLVMOperator,SimpleLLVMLabel>(LLVMOperator.class);
	public static SimpleLLVMLabel get(LLVMOperator operator) {
		SimpleLLVMLabel label;
		if (!cache.containsKey(operator)) {
			 label = new SimpleLLVMLabel(operator);
			 cache.put(operator, label);
		} else {
			label = cache.get(operator);
		}
		return label;
	}
	
	///////////////////////////////////////
	
	private final LLVMOperator operator;
	private SimpleLLVMLabel(LLVMOperator _operator) {this.operator = _operator;}
	
	public LLVMOperator getOperator() {return this.operator;}
	
	public boolean isSimple() {return true;}
	public SimpleLLVMLabel getSimpleSelf() {return this;}

	public boolean equalsLabel(LLVMLabel label) {
		if (!label.isSimple())
			return false;
		SimpleLLVMLabel s = label.getSimpleSelf();
		return s.getOperator().equals(this.getOperator());
	}
	public int hashCode() {
		return this.getOperator().hashCode()*11;
	}
	public String toString() {
		return this.getOperator().toString();
	}
	public boolean isRevertible() {return true;}
}
