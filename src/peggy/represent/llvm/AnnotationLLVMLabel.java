package peggy.represent.llvm;

/**
 * This is an LLVMLabel for annotation nodes.
 */
public abstract class AnnotationLLVMLabel extends LLVMLabel {
	public final boolean isAnnotation() {return true;}
	public final AnnotationLLVMLabel getAnnotationSelf() {return this;}
	public final boolean isRevertible() {return false;}
	
	public boolean isString() {return false;}
	public StringAnnotationLLVMLabel getStringSelf() {throw new UnsupportedOperationException();}
	
	public boolean isTypeAnnotation() {return false;}
	public TypeAnnotationLLVMLabel getTypeAnnotationSelf() {throw new UnsupportedOperationException();}
	
	public final boolean equalsLabel(LLVMLabel label) {
		if (!label.isAnnotation())
			return false;
		return this.equalsAnnotation(label.getAnnotationSelf());
	}
	public abstract boolean equalsAnnotation(AnnotationLLVMLabel label);
}
