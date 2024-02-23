package llvm.values;

import java.util.Iterator;
import java.util.Map;

import llvm.types.Type;
import llvm.values.FunctionValue.ArgumentValue;

/**
 * This is a value that implements the holder pattern.
 * It is a temporary placeholder that will eventually be replaced by another
 * value.
 */
public final class HolderValue extends Value {
	protected Value inner;
	protected Type holderType;
	protected boolean makeConstant;
	protected boolean makeMetadata;
	
	public HolderValue() {
		this.makeConstant = false;
		this.makeMetadata = false;
	}
	
	public Type getHolderType() {
		if (this.inner == null)
			return this.holderType;
		else
			throw new IllegalStateException();
	}
	public void setHolderType(Type type) {
		if (this.inner == null) {
			if (this.holderType == null)
				this.holderType = type;
			else if (!this.holderType.equalsType(type))
				throw new IllegalStateException("Holder type " + this.holderType + " does not equal new type " + type);
		} else
			throw new IllegalStateException();
	}

	public void setInnerValue(Value value) {
		if (this.inner == null) {
			if (this.holderType != null && !value.getType().equalsType(this.holderType))
				throw new IllegalStateException("type mismatch: " + value.getType() + ", " + this.holderType);
			if (this.makeConstant)
				value.ensureConstant();
			if (this.makeMetadata)
				value.ensureMetadata();
			this.inner = value;
		} else
			throw new IllegalStateException("inner value already set");
	}

	public void ensureMetadata() {
		if (this.inner == null)
			this.makeMetadata = true;
		else
			this.inner.ensureMetadata();
	}
	public void ensureConstant() {
		if (this.inner == null)
			this.makeConstant = true;
		else
			this.inner.ensureConstant();
	}
	public Type getType() {
		if (this.inner == null)
			return this.holderType;
		else
			return this.inner.getType();
	}
	
	public Iterator<? extends Value> getSubvalues() {
		if (this.inner == null)
			return new ValueIterator();
		else
			return this.inner.getSubvalues();
	}

	public boolean isInteger() {
		if (this.inner == null)
			return super.isInteger();
		else
			return this.inner.isInteger();
	}
	public IntegerValue getIntegerSelf() {
		if (this.inner == null)
			return super.getIntegerSelf();
		else
			return this.inner.getIntegerSelf();
	}
	
	public boolean isFloatingPoint() {
		if (this.inner == null)
			return super.isFloatingPoint();
		else
			return this.inner.isFloatingPoint();
	}
	public FloatingPointValue getFloatingPointSelf() {
		if (this.inner == null)
			return super.getFloatingPointSelf();
		else
			return this.inner.getFloatingPointSelf();
	}
	
	public boolean isUndef() {
		if (this.inner == null)
			return super.isUndef();
		else
			return this.inner.isUndef();
	}
	public UndefValue getUndefSelf() {
		if (this.inner == null)
			return super.getUndefSelf();
		else
			return this.inner.getUndefSelf();
	}
	
	public boolean isFunction() {
		if (this.inner == null)
			return super.isFunction();
		else
			return this.inner.isFunction();
	}
	public FunctionValue getFunctionSelf() {
		if (this.inner == null)
			return super.getFunctionSelf();
		else
			return this.inner.getFunctionSelf();
	}
	
	public boolean isConstantNullPointer() {
		if (this.inner == null)
			return super.isConstantNullPointer();
		else
			return this.inner.isConstantNullPointer();
	}
	public ConstantNullPointerValue getConstantNullPointerSelf() {
		if (this.inner == null)
			return super.getConstantNullPointerSelf();
		else
			return this.inner.getConstantNullPointerSelf();
	}
	
	public boolean isConstantStructure() {
		if (this.inner == null)
			return super.isConstantStructure();
		else
			return this.inner.isConstantStructure();
	}
	public ConstantStructureValue getConstantStructureSelf() {
		if (this.inner == null)
			return super.getConstantStructureSelf();
		else
			return this.inner.getConstantStructureSelf();
	}
	
	public boolean isConstantArray() {
		if (this.inner == null)
			return super.isConstantArray();
		else
			return this.inner.isConstantArray();
	}
	public ConstantArrayValue getConstantArraySelf() {
		if (this.inner == null)
			return super.getConstantArraySelf();
		else
			return this.inner.getConstantArraySelf();
	}

	public boolean isConstantVector() {
		if (this.inner == null)
			return super.isConstantVector();
		else
			return this.inner.isConstantVector();
	}
	public ConstantVectorValue getConstantVectorSelf() {
		if (this.inner == null)
			return super.getConstantVectorSelf();
		else
			return this.inner.getConstantVectorSelf();
	}
	
	public boolean isGlobalVariable() {
		if (this.inner == null)
			return super.isGlobalVariable();
		else
			return this.inner.isGlobalVariable();
	}
	public GlobalVariable getGlobalVariableSelf() {
		if (this.inner == null)
			return super.getGlobalVariableSelf();
		else
			return this.inner.getGlobalVariableSelf();
	}

	public boolean isAlias() {
		if (this.inner == null)
			return super.isAlias();
		else
			return this.inner.isAlias();
	}
	public AliasValue getAliasSelf() {
		if (this.inner == null)
			return super.getAliasSelf();
		else
			return this.inner.getAliasSelf();
	}
	
	public boolean isInlineASM() {
		if (this.inner == null)
			return super.isInlineASM();
		else
			return this.inner.isInlineASM();
	}
	public ConstantInlineASM getInlineASMSelf() {
		if (this.inner == null)
			return super.getInlineASMSelf();
		else
			return this.inner.getInlineASMSelf();
	}

	public boolean isArgument() {
		if (this.inner == null)
			return super.isArgument();
		else
			return this.inner.isArgument();
	}
	public ArgumentValue getArgumentSelf() {
		if (this.inner == null)
			return super.getArgumentSelf();
		else
			return this.inner.getArgumentSelf();
	}	
	
	public boolean isRegister() {
		if (this.inner == null)
			return super.isRegister();
		else
			return this.inner.isRegister();
	}
	public VirtualRegister getRegisterSelf() {
		if (this.inner == null)
			return super.getRegisterSelf();
		else
			return this.inner.getRegisterSelf();
	}
	
	public boolean isConstantExpr() {
		if (this.inner == null)
			return super.isConstantExpr();
		else
			return this.inner.isConstantExpr();
	}
	public ConstantExpr getConstantExprSelf() {
		if (this.inner == null)
			return super.getConstantExprSelf();
		else
			return this.inner.getConstantExprSelf();
	}

	public boolean isLabel() {
		if (this.inner == null)
			return super.isLabel();
		else
			return this.inner.isLabel();
	}
	public LabelValue getLabelSelf() {
		if (this.inner == null)
			return super.getLabelSelf();
		else
			return this.inner.getLabelSelf();
	}
	
	public boolean isMetadataNode() {
		if (this.inner == null)
			return super.isMetadataNode();
		else
			return this.inner.isMetadataNode();
	}
	public MetadataNodeValue getMetadataNodeSelf() {
		if (this.inner == null)
			return super.getMetadataNodeSelf();			
		else
			return this.inner.getMetadataNodeSelf();
	}
	
	public boolean isMetadataString() {
		if (this.inner == null)
			return super.isMetadataString();
		else
			return this.inner.isMetadataString();
	}
	public MetadataStringValue getMetadataStringSelf() {
		if (this.inner == null)
			return super.getMetadataStringSelf();			
		else
			return this.inner.getMetadataStringSelf();
	}
	
	public boolean isBlockAddress() {
		if (this.inner == null)
			return super.isBlockAddress();
		else
			return this.inner.isBlockAddress();
	}
	public BlockAddressValue getBlockAddressSelf() {
		if (this.inner == null)
			return super.getBlockAddressSelf();			
		else
			return this.inner.getBlockAddressSelf();
	}
	
	public boolean is2_8Value() {
		if (this.inner == null)
			return super.is2_8Value();
		else
			return this.inner.is2_8Value();
	}
	
	public boolean isHolder() {
		if (this.inner == null)
			return true;
		else
			return this.inner.isHolder();
	}
	public HolderValue getHolderSelf() {
		if (this.inner == null)
			return this;
		else
			return this.inner.getHolderSelf();
	}
	
	public String toString() {
		if (this.inner == null)
			return "<holder" + (this.holderType == null ? ">" : (":" + this.holderType + ">"));
		else
			return this.inner.toString();
	}
	public boolean equalsValue(Value o) {
		if (this.inner == null)
			return this == o;
		else
			return this.inner.equalsValue(o);
	}
	public int hashCode() {
		if (this.inner == null)
			return 47;
		else
			return this.inner.hashCode();
	}
	public boolean isFunctionLocal() {
		if (this.inner == null)
			return super.isFunctionLocal();
		else
			return this.inner.isFunctionLocal();
	}
	
	protected Value rewriteChildren(Map<Value,Value> old2new) {
		if (this.inner == null)
			throw new UnsupportedOperationException();
		else
			return this.inner.rewriteChildren(old2new);
	}
}
