package llvm.instructions;

import llvm.types.Type;

/**
 * This enum defines the various binary operator types that can be used
 * in a BinopInstruction.
 */
public enum Binop {
	Add(0) {
		public boolean validTypes(Type LHS, Type RHS) {return a_va(LHS, RHS);}
	},
	Sub(1) {
		public boolean validTypes(Type LHS, Type RHS) {return a_va(LHS, RHS);}
	},
	Mul(2) {
		public boolean validTypes(Type LHS, Type RHS) {return a_va(LHS, RHS);}
	},
	UDiv(3) {
		public boolean validTypes(Type LHS, Type RHS) {return i_vi(LHS, RHS);}
	},
	SDiv(4) {
		public boolean validTypes(Type LHS, Type RHS) {return i_vi(LHS, RHS);}
	},
	FDiv(4) {
		public boolean validTypes(Type LHS, Type RHS) {return f_vf(LHS, RHS);}
	},
	URem(5) {
		public boolean validTypes(Type LHS, Type RHS) {return i_vi(LHS, RHS);}
	},
	SRem(6) {
		public boolean validTypes(Type LHS, Type RHS) {return i_vi(LHS, RHS);}
	},
	FRem(6) {
		public boolean validTypes(Type LHS, Type RHS) {return f_vf(LHS, RHS);}
	},
	Shl(7) {
		public boolean validTypes(Type LHS, Type RHS) {return i_vi(LHS, RHS);}
	},
	LShr(8) {
		public boolean validTypes(Type LHS, Type RHS) {return i_vi(LHS, RHS);}
	},
	AShr(9) {
		public boolean validTypes(Type LHS, Type RHS) {return i_vi(LHS, RHS);}
	},
	And(10) {
		public boolean validTypes(Type LHS, Type RHS) {return i_vi(LHS, RHS);}
	},
	Or(11) {
		public boolean validTypes(Type LHS, Type RHS) {return i_vi(LHS, RHS);}
	},
	Xor(12) {
		public boolean validTypes(Type LHS, Type RHS) {return i_vi(LHS, RHS);}
	},
	AddNsw(true,0|16) {
		public boolean validTypes(Type LHS, Type RHS) {return a_va(LHS, RHS);}
	},
	AddNuw(true,0|32) {
		public boolean validTypes(Type LHS, Type RHS) {return a_va(LHS, RHS);}
	},
	AddNswNuw(true,0|16|32) {
		public boolean validTypes(Type LHS, Type RHS) {return a_va(LHS, RHS);}
	},
	SubNsw(true,1|16) {
		public boolean validTypes(Type LHS, Type RHS) {return a_va(LHS, RHS);}
	},
	SubNuw(true,1|32) {
		public boolean validTypes(Type LHS, Type RHS) {return a_va(LHS, RHS);}
	},
	SubNswNuw(true,1|16|32) {
		public boolean validTypes(Type LHS, Type RHS) {return a_va(LHS, RHS);}
	},
	MulNsw(true,2|16) {
		public boolean validTypes(Type LHS, Type RHS) {return a_va(LHS, RHS);}
	},
	MulNuw(true,2|32) {
		public boolean validTypes(Type LHS, Type RHS) {return a_va(LHS, RHS);}
	},
	MulNswNuw(true,2|16|32) {
		public boolean validTypes(Type LHS, Type RHS) {return a_va(LHS, RHS);}
	},
	SDivExact(true,4|64) {
		public boolean validTypes(Type LHS, Type RHS) {return i_vi(LHS, RHS);}
	};
	
	private final int value;
	private final boolean is2_8;
	private Binop(int _value) {
		this(false, _value);
	}
	private Binop(boolean _is2_8, int _value) {
		this.is2_8 = _is2_8;
		this.value = _value;
	}
	
	public boolean is2_8() {return this.is2_8;}
	public int getValue() {return this.value;}
	public String getLabel() {return this.name().toLowerCase();}

	private static boolean isFPorFPVector(Type t) {
		return t.isFloatingPoint() || (t.isComposite() && t.getCompositeSelf().isVector() && t.getCompositeSelf().getVectorSelf().getElementType().isFloatingPoint());
	}
	
	public static Binop decodeBinop(int value, Type type) {
		switch (value) {
		case 0: return Add;
		case 1: return Sub;
		case 2: return Mul;
		case 3: return UDiv;
		case 4: 
			if (isFPorFPVector(type)) 
				return FDiv;
			else
				return SDiv;
		case 5: return URem;
		case 6: 
			if (isFPorFPVector(type)) 
				return FRem;
			else 
				return SRem;
		case 7: return Shl;
		case 8: return LShr;
		case 9: return AShr;
		case 10: return And;
		case 11: return Or;
		case 12: return Xor;
		
		case (0|16): return AddNsw;
		case (0|32): return AddNuw;
		case (0|16|32): return AddNswNuw;
		case (1|16): return SubNsw;
		case (1|32): return SubNuw;
		case (1|16|32): return SubNswNuw;
		case (2|16): return MulNsw;
		case (2|32): return MulNuw;
		case (2|16|32): return MulNswNuw;
		case (4|64): return SDivExact;

		default:
			throw new IllegalArgumentException("Unknown binop value: " + value);
		}
	}
	
	protected boolean a_va(Type LHS, Type RHS) {
		return (LHS.equals(RHS)) && (LHS.isInteger() || LHS.isFloatingPoint() || (LHS.isComposite() && LHS.getCompositeSelf().isVector()));
	}
	protected boolean i_vi(Type LHS, Type RHS) {
		return LHS.equals(RHS) && (LHS.isInteger() || (LHS.isComposite() && LHS.getCompositeSelf().isVector() && LHS.getCompositeSelf().getVectorSelf().getElementType().isInteger()));
	}
	protected boolean f_vf(Type LHS, Type RHS) {
		return LHS.equals(RHS) && (LHS.isFloatingPoint() || (LHS.isComposite() && LHS.getCompositeSelf().isVector() && LHS.getCompositeSelf().getVectorSelf().getElementType().isFloatingPoint()));
	}
	
	public abstract boolean validTypes(Type LHS, Type RHS);
}
