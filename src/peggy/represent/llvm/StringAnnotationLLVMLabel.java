package peggy.represent.llvm;

/**
 * This is an annotation LLVMLabel that uses a simple string as its operator.
 */
public class StringAnnotationLLVMLabel extends AnnotationLLVMLabel {
	protected final String value;
	
	public StringAnnotationLLVMLabel(String _value) {
		this.value = _value;
	}
	public String getValue() {return this.value;}
	
	public boolean isString() {return true;}
	public StringAnnotationLLVMLabel getStringSelf() {return this;}
	
	public boolean equalsAnnotation(AnnotationLLVMLabel label) {
		if (!label.isString())
			return false;
		StringAnnotationLLVMLabel s = label.getStringSelf();
		return this.getValue().equals(s.getValue());
	}
	public int hashCode() {
		return this.getValue().hashCode() * 79;
	}
	public String toString() {
		return "StringAnnotation[" + this.getValue() + "]";
	}
}
