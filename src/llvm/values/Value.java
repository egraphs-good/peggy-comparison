package llvm.values;

import java.util.*;

import llvm.types.*;
import llvm.values.FunctionValue.ArgumentValue;

/**
 * Ths abstract parent of all Values.
 * Every value has a type.
 * Values are hashable and equallable.
 */
public abstract class Value {
	/**
	 * Returns true iff the given value can be expressed as a null constant.
	 */
	public static boolean isNullConstant(Value value) {
		if (value.isConstantNullPointer())
			return true;
		else if (value.isInteger()) {
			IntegerValue inty = value.getIntegerSelf();
			for (int i = 0; i < inty.getWidth(); i++) {
				if (inty.getBit(i))
					return false;
			}
			return true;
		}
		else if (value.isFloatingPoint()) {
			FloatingPointValue fpv = value.getFloatingPointSelf();
			for (int i = 0; i < fpv.getType().getKind().getTypeSize(); i++) {
				if (fpv.getBit(i))
					return false;
			}
			return true;
		}
		else if (value.isConstantArray()) {
			ConstantArrayValue cav = value.getConstantArraySelf();
			if (cav instanceof ConstantNullArrayValue)
				return true;
			else if (cav instanceof ConstantExplicitArrayValue) {
				ConstantExplicitArrayValue caev = (ConstantExplicitArrayValue)cav;
				for (int i = 0; i < caev.getNumElements().signedValue(); i++) {
					if (!isNullConstant(caev.getElement(i)))
						return false;
				}
				return true;
			}
			else 
				return false;
		}
		else if (value.isConstantVector()) {
			ConstantVectorValue cvv = value.getConstantVectorSelf();
			if (cvv instanceof ConstantNullVectorValue)
				return true;
			else if (cvv instanceof ConstantExplicitVectorValue) {
				ConstantExplicitVectorValue cvev = (ConstantExplicitVectorValue)cvv;
				for (int i = 0; i < cvev.getNumElements().signedValue(); i++) {
					if (!isNullConstant(cvev.getElement(i)))
						return false;
				}
				return true;
			}
			else 
				return false;
		} 
		else if (value.isConstantStructure()) {
			ConstantStructureValue csv = value.getConstantStructureSelf();
			for (int i = 0; i < csv.getNumFields(); i++) {
				if (!isNullConstant(csv.getFieldValue(i)))
					return false;
			}
			return true;
		}
		else
			return false;
	}

	
	/**
	 * Returns a null version of the given type.
	 * For numeric values (integer,floating point) this will return the
	 * '0' equivalent for that type. For pointers, this will return the NULL
	 * pointer of the correct type. For aggregate values (array,vector,structure)
	 * this will return an aggregate value where all of the component values
	 * are built by a recursive call to this procedure given the component type.
	 * i.e. null({int,float,double}) = {null(int),null(float),null(double)} = {0, 0.0f, 0.0}
	 * 
	 * If the type is not nullable, an IllegalArgumentException is thrown.
	 */
	public static Value getNullValue(Type type) {
		long words[] = {0L,0L};
		if (type.isInteger())
			return IntegerValue.get(type.getIntegerSelf().getWidth(), words);
		else if (type.isFloatingPoint())
			return FloatingPointValue.get(type.getFloatingPointSelf(), words);
		else if (type.isComposite()) {
			CompositeType ctype = type.getCompositeSelf();
			if (ctype.isStructure()) {
				StructureType struct = ctype.getStructureSelf();
				List<Value> fieldValues = new ArrayList<Value>(struct.getNumFields());
				for (int i = 0; i < struct.getNumFields(); i++)
					fieldValues.add(getNullValue(struct.getFieldType(i)));
				return new ConstantStructureValue(struct, fieldValues);
			}
			else if (ctype.isArray()) {
				ArrayType array = ctype.getArraySelf();
				return new ConstantNullArrayValue(array);
			}
			else if (ctype.isVector()) {
				VectorType vector = ctype.getVectorSelf();
				return new ConstantNullVectorValue(vector);
			}
			else if (ctype.isPointer()) {
				return new ConstantNullPointerValue(ctype.getPointerSelf());
			}
			else 
				throw new RuntimeException("This should never happen");
		}
		else
			throw new IllegalArgumentException("Not a nullable type: " + type);
	}
	
	/**
	 * Returns true if the value of this Value can be determined statically.
	 */
	public void ensureConstant() {throw new IllegalStateException("Not a constant");}
	public void ensureMetadata() {throw new IllegalStateException("Not metadata: " + this);}
	public abstract Type getType();
	public abstract Iterator<? extends Value> getSubvalues();

	public final Value rewrite(Map<Value,Value> old2new) {
		Value result = this;
		for (boolean progress = true; progress; ) {
			progress = false;
			
			while (old2new.containsKey(result)) {
				Value next = old2new.get(result);
				if (next.equalsValue(result))
					break;
				result = next;
				progress = true;
			}
			Value children = result.rewriteChildren(old2new);
			if (children != result) {
				result = children;
				progress = true;
			}
		}
		return result;
	}
	protected abstract Value rewriteChildren(Map<Value,Value> old2new);
	
	public final Value rewrite(Value oldValue, Value newValue) {
		if (!oldValue.getType().equalsType(newValue.getType()))
			throw new IllegalArgumentException("Values do not share types");
		
		if (oldValue.equalsValue(this))
			return newValue;
		else
			return this.rewriteChildren(Collections.<Value,Value>singletonMap(oldValue, newValue));
	}
	protected final Value rewriteChildren(Value oldValue, Value newValue) { return null;}
	
	public boolean isInteger() {return false;}
	public IntegerValue getIntegerSelf() {throw new UnsupportedOperationException();}
	
	public boolean isFloatingPoint() {return false;}
	public FloatingPointValue getFloatingPointSelf() {throw new UnsupportedOperationException();}
	
	public boolean isUndef() {return false;}
	public UndefValue getUndefSelf() {throw new UnsupportedOperationException();}
	
	public boolean isFunction() {return false;}
	public FunctionValue getFunctionSelf() {throw new UnsupportedOperationException();}
	
	public boolean isConstantNullPointer() {return false;}
	public ConstantNullPointerValue getConstantNullPointerSelf() {throw new UnsupportedOperationException();}
	
	public boolean isConstantStructure() {return false;}
	public ConstantStructureValue getConstantStructureSelf() {throw new UnsupportedOperationException();}
	
	public boolean isConstantArray() {return false;}
	public ConstantArrayValue getConstantArraySelf() {throw new UnsupportedOperationException();}

	public boolean isConstantVector() {return false;}
	public ConstantVectorValue getConstantVectorSelf() {throw new UnsupportedOperationException();}
	
	public boolean isGlobalVariable() {return false;}
	public GlobalVariable getGlobalVariableSelf() {throw new UnsupportedOperationException();}
	
	public boolean isAlias() {return false;}
	public AliasValue getAliasSelf() {throw new UnsupportedOperationException();}

	public boolean isInlineASM() {return false;}
	public ConstantInlineASM getInlineASMSelf() {throw new UnsupportedOperationException();}
	
	public boolean isArgument() {return false;}
	public ArgumentValue getArgumentSelf() {throw new UnsupportedOperationException();}
	
	public boolean isRegister() {return false;}
	public VirtualRegister getRegisterSelf() {throw new UnsupportedOperationException();}

	public boolean isConstantExpr() {return false;}
	public ConstantExpr getConstantExprSelf() {throw new UnsupportedOperationException();}
	
	public boolean isLabel() {return false;}
	public LabelValue getLabelSelf() {throw new UnsupportedOperationException();}
	
	public boolean isHolder() {return false;}
	public HolderValue getHolderSelf() {throw new UnsupportedOperationException();}
	
	///// 2.8 values ///////////

	public boolean is2_8Value() {return false;}

	public boolean isMetadataString() {return false;}
	public MetadataStringValue getMetadataStringSelf() {throw new UnsupportedOperationException();}

	public boolean isMetadataNode() {return false;}
	public MetadataNodeValue getMetadataNodeSelf() {throw new UnsupportedOperationException();}
	
	public boolean isBlockAddress() {return false;}
	public BlockAddressValue getBlockAddressSelf() {throw new UnsupportedOperationException();}
	
	public boolean isFunctionLocal() {return false;}
	
	public abstract String toString();
	public final boolean equals(Object o) {
		if (o == null || !(o instanceof Value))
			return false;
		return this.equalsValue((Value)o);
	}
	public abstract boolean equalsValue(Value v);
	public abstract int hashCode();
}
