package peggy.analysis.llvm.types;

import llvm.types.Type;

/**
 * This is the domain-specific type of LLVM values in a PEG.
 * This class is often used as the parameter to the PEGType class.
 */
public abstract class LLVMType {
	public final boolean equals(Object o) {
		if (!(o instanceof LLVMType))
			return false;
		return equalsLLVMType((LLVMType)o);
	}
	public abstract boolean equalsLLVMType(LLVMType type);
	public abstract int hashCode();
	public abstract String toString();
	
	
	public boolean isSimple() {return false;}
	public Type getSimpleType() {throw new UnsupportedOperationException();}
	public static LLVMType makeSimple(final Type type) {
		return new LLVMType() {
			public boolean isSimple() {return true;}
			public Type getSimpleType() {return type;}
			public boolean equalsLLVMType(LLVMType him) {
				return him.isSimple() && him.getSimpleType().equalsType(type);
			}
			public int hashCode() {
				return type.hashCode()*17;
			}
			public String toString() {return type.toString();}
		};
	}
	
	// true for sigmas
	public boolean isSigma() {return false;}
	public static final LLVMType SIGMA = new LLVMType() {
		public boolean isSigma() {return true;}
		public boolean equalsLLVMType(LLVMType him) {
			return him.isSigma();
		}
		public int hashCode() {return 19;}
		public String toString() {return "SIGMA";}
	};
	
	// true for type descriptor nodes
	public boolean isType() {return false;}
	public static final LLVMType TYPE = new LLVMType() {
		public boolean isType() {return true;}
		public boolean equalsLLVMType(LLVMType him) {
			return him.isType();
		}
		public int hashCode() {return 29;}
		public String toString() {return "TYPE";}
	};
	
	// true for numerals
	public boolean isNumeral() {return false;}
	public static final LLVMType NUMERAL = new LLVMType() {
		public boolean isNumeral() {return true;}
		public boolean equalsLLVMType(LLVMType him) {
			return him.isNumeral();
		}
		public int hashCode() {return 31;}
		public String toString() {return "NUMERAL";}
	};
	
	public boolean isParamAttr() {return false;}
	public static final LLVMType PARAMATTR = new LLVMType() {
		public boolean isParamAttr() {return true;}
		public boolean equalsLLVMType(LLVMType him) {
			return him.isParamAttr();
		}
		public int hashCode() {return 37;}
		public String toString() {return "PARAMATTR";}
	};
	
	public boolean isParamAttrMap() {return false;}
	public static final LLVMType PARAMATTRMAP = new LLVMType() {
		public boolean isParamAttrMap() {return true;}
		public boolean equalsLLVMType(LLVMType him) {
			return him.isParamAttrMap();
		}
		public int hashCode() {return 41;}
		public String toString() {return "PARAMATTRMAP";}
	};
	
	public boolean isException() {return false;}
	public static final LLVMType EXCEPTION = new LLVMType() {
		public boolean isException() {return true;}
		public boolean equalsLLVMType(LLVMType him) {
			return him.isException();
		}
		public int hashCode() {return 43;}
		public String toString() {return "EXCEPTION";}
	};
}
