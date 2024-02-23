package llvm.instructions;

import llvm.bitcode.UnsignedLong;
import llvm.types.Type;

/**
 * This represents the various methods whereby a value can be cast by 
 * a CastInstruction.
 */
public enum Cast {
	Trunc(0) {
		public boolean isValid(Type src, Type dest) {
			return extendInt(dest, src) || extendVectorInt(dest,src);
		}
	},
	ZExt(1) {
		public boolean isValid(Type src, Type dest) {
			return extendInt(src, dest) || extendVectorInt(src,dest);
		}
	},
	SExt(2) {
		public boolean isValid(Type src, Type dest) {
			return extendInt(src, dest) || extendVectorInt(src,dest);
		}
	},
	FPToUI(3) {
		public boolean isValid(Type src, Type dest) {
			return fptoi(src, dest);
		}
	},
	FPToSI(4) {
		public boolean isValid(Type src, Type dest) {
			return fptoi(src, dest);
		}
	},
	UIToFP(5) {
		public boolean isValid(Type src, Type dest) {
			return itofp(src, dest);
		}
	},
	SIToFP(6) {
		public boolean isValid(Type src, Type dest) {
			return itofp(src, dest);
		}
	},
	FPTrunc(7) {
		public boolean isValid(Type src, Type dest) {
			return extendFloat(dest, src) || extendVectorFloat(dest,src);
		}
	},
	FPExt(8) {
		public boolean isValid(Type src, Type dest) {
			return extendFloat(src,dest) || extendVectorFloat(src,dest);
		}
	},
	PtrToInt(9) {
		public boolean isValid(Type src, Type dest) {
			return src.isComposite() && src.getCompositeSelf().isPointer() && dest.isInteger();
		}
	},
	IntToPtr(10) {
		public boolean isValid(Type src, Type dest) {
			return dest.isComposite() && dest.getCompositeSelf().isPointer() && src.isInteger();
		}
	},
	Bitcast(11) {
		public boolean isValid(Type src, Type dest) {
			if (src.isComposite() && src.getCompositeSelf().isPointer()) {
				return dest.isComposite() && dest.getCompositeSelf().isPointer();
			} else if (dest.isComposite() && dest.getCompositeSelf().isPointer()) {
				return src.isComposite() && src.getCompositeSelf().isPointer();
			} else if (src.hasTypeSize() && dest.hasTypeSize() && src.isFirstClass() && dest.isFirstClass()) {
				return src.getTypeSize() == dest.getTypeSize();
			} else {
				return false;
			}
		}
	};
	
	private int value;
	private Cast(int _value) {
		this.value = _value;
	}
	public int getValue() {
		return this.value;
	}

	protected boolean fptoi(Type src, Type dest) {
		if (src.isFloatingPoint()) {
			return dest.isInteger();
		} else if (src.isVectorOfFloatingPoint()) {
			UnsignedLong size = src.getCompositeSelf().getVectorSelf().getNumElements();
			return 
				dest.isVectorOfInteger() &&
				dest.getCompositeSelf().getVectorSelf().getNumElements().equals(size);
		} else {
			return false;
		}
	}
	protected boolean itofp(Type src, Type dest) {
		if (src.isInteger()) {
			return dest.isFloatingPoint();
		} else if (src.isVectorOfInteger()) { 
			UnsignedLong size = src.getCompositeSelf().getVectorSelf().getNumElements();
			return 
				dest.isVectorOfFloatingPoint() && 
				dest.getCompositeSelf().getVectorSelf().getNumElements().equals(size);
		} else {
			return false;
		}
	}
	protected boolean extendVectorInt(Type src, Type dest) {
		if (src.isVectorOfInteger() && dest.isVectorOfInteger()) {
			return extendInt(src.getCompositeSelf().getVectorSelf().getElementType(),
							 dest.getCompositeSelf().getVectorSelf().getElementType());
		}
		return false;
	}
	protected boolean extendInt(Type src, Type dest) {
		if (src.isInteger() && dest.isInteger()) {
			return src.getIntegerSelf().getWidth() < dest.getIntegerSelf().getWidth();
		}
		return false;
	}
	protected boolean extendFloat(Type src, Type dest) {
		if (src.isFloatingPoint() && dest.isFloatingPoint()) {
			return src.getFloatingPointSelf().getTypeSize() < dest.getFloatingPointSelf().getTypeSize();
		}
		return false;
	}
	protected boolean extendVectorFloat(Type src, Type dest) {
		if (src.isVectorOfFloatingPoint() && dest.isVectorOfFloatingPoint()) {
			return extendFloat(src.getCompositeSelf().getVectorSelf().getElementType(),
							   dest.getCompositeSelf().getVectorSelf().getElementType());
		}
		return false;
	}
	
	public abstract boolean isValid(Type src, Type dest);
	
	public static Cast decodeCast(int value) {
		switch (value) {
		case 0: return Trunc;
		case 1: return ZExt;
		case 2: return SExt;
		case 3: return FPToUI;
		case 4: return FPToSI;
		case 5: return UIToFP;
		case 6: return SIToFP;
		case 7: return FPTrunc;
		case 8: return FPExt;
		case 9: return PtrToInt;
		case 10: return IntToPtr;
		case 11: return Bitcast;
		default:
			throw new IllegalArgumentException("Unknown cast value: " + value);
		}
	}
}
