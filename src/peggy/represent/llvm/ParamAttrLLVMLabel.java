package peggy.represent.llvm;

import llvm.values.ParameterAttributes;

/**
 * This is an LLVMLabel that represents LLVM parameter attributes.
 */
public class ParamAttrLLVMLabel extends LLVMLabel {
	protected final ParameterAttributes attributes;
	
	public ParamAttrLLVMLabel(ParameterAttributes _attributes) {
		this.attributes = _attributes;
	}
	
	public ParameterAttributes getAttributes() {return this.attributes;}
	
	public boolean isParamAttr() {return true;}
	public ParamAttrLLVMLabel getParamAttrSelf() {return this;}
	public boolean equalsLabel(LLVMLabel label) {
		if (!label.isParamAttr()) return false;
		ParamAttrLLVMLabel p = label.getParamAttrSelf();
		return p.getAttributes().equals(this.getAttributes());
	}
	public int hashCode() {
		return this.getAttributes().hashCode()*31;
	}
	public String toString() {
		return "ParamAttr[0x" + Integer.toHexString(this.getAttributes().getBits()) + "]";
	}
	public boolean isRevertible() {return true;}
}
