package peggy.represent.llvm;

/**
 * This class is used as the L parameter to LLVM CFGs.
 * Many of the standard LLVM instructions are encoded as SimpleLLVMLabels
 * using LLVMOperators.
 */
public abstract class LLVMLabel {
	public boolean isSimple() {return false;}
	public SimpleLLVMLabel getSimpleSelf() {throw new UnsupportedOperationException();}
	
	public boolean isType() {return false;}
	public TypeLLVMLabel getTypeSelf() {throw new UnsupportedOperationException();}
	
	public boolean isBinop() {return false;}
	public BinopLLVMLabel getBinopSelf() {throw new UnsupportedOperationException();}
	
	public boolean isCast() {return false;}
	public CastLLVMLabel getCastSelf() {throw new UnsupportedOperationException();}
	
	public boolean isCmp() {return false;}
	public CmpLLVMLabel getCmpSelf() {throw new UnsupportedOperationException();}
	
	public boolean isNumeral() {return false;}
	public NumeralLLVMLabel getNumeralSelf() {throw new UnsupportedOperationException();}
	
	/* deprecated */
	public boolean isParamAttr() {return false;}
	public ParamAttrLLVMLabel getParamAttrSelf() {throw new UnsupportedOperationException();}
	
	public boolean isFunction() {return false;}
	public FunctionLLVMLabel getFunctionSelf() {throw new UnsupportedOperationException();}

	public boolean isGlobal() {return false;}
	public GlobalLLVMLabel getGlobalSelf() {throw new UnsupportedOperationException();}
	
	public boolean isAlias() {return false;}
	public AliasLLVMLabel getAliasSelf() {throw new UnsupportedOperationException();}

	public boolean isInlineASM() {return false;}
	public InlineASMLLVMLabel getInlineASMSelf() {throw new UnsupportedOperationException();}
	
	public boolean isConstantValue() {return false;}
	public ConstantValueLLVMLabel getConstantValueSelf() {throw new UnsupportedOperationException();}
	
	public boolean isBasicOp() {return false;}
	public BasicOpLLVMLabel getBasicOpSelf() {throw new UnsupportedOperationException();}
	
	public boolean isAnnotation() {return false;}
	public AnnotationLLVMLabel getAnnotationSelf() {throw new UnsupportedOperationException();}
	
	public final boolean equals(Object o) {
		if (o == null || !(o instanceof LLVMLabel))
			return false;
		LLVMLabel label = (LLVMLabel)o;
		return this.equalsLabel(label);
	}
	public abstract boolean equalsLabel(LLVMLabel label);
	public abstract int hashCode();
	public abstract String toString();
	public abstract boolean isRevertible();
}
