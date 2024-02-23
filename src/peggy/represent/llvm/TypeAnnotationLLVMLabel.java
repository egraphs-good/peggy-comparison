package peggy.represent.llvm;

import peggy.analysis.llvm.types.LLVMType;
import peggy.analysis.llvm.types.PEGType;

/**
 * This is an annotation LLVMLabel that is used to annotate a value with its
 * type.
 */
public class TypeAnnotationLLVMLabel extends AnnotationLLVMLabel {
	private final PEGType<LLVMType> type;
	public TypeAnnotationLLVMLabel(PEGType<LLVMType> _type) {
		this.type = _type;
	}
	public boolean isTypeAnnotation() {return true;}
	public TypeAnnotationLLVMLabel getTypeAnnotationSelf() {return this;}
	public PEGType<LLVMType> getType() {return type;}
	public boolean equalsAnnotation(AnnotationLLVMLabel label) {
		return label.isType() && label.getTypeSelf().getType().equals(type);
	}
	public int hashCode() {return type.hashCode();}
	public String toString() {
		return "TypeAnnotationLLVMLabel[" + this.type + "]";
	}
}
