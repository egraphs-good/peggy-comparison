package eqsat;

import static eqsat.BasicOp.And;
import static eqsat.BasicOp.False;
import static eqsat.BasicOp.Negate;
import static eqsat.BasicOp.Or;
import static eqsat.BasicOp.True;
import static eqsat.BasicOp.Equals;
import util.integer.ArrayIntMap;
import util.integer.IntMap;
import util.integer.IntSet;

public abstract class FlowValue<P,L> {
//	private static final FlowValue mSplit = new FlowValue() {
//		public boolean isSplit() {return true;}
//		public boolean hasFixedArity() {return true;}
//		public int getFixedArity() {return 1;}
//		public String toString() {return "split";}
//	};
	private static final FlowValue mZero = new FlowValue() {
		public boolean isZero() {return true;}
		public boolean hasFixedArity() {return true;}
		public int getFixedArity() {return 0;}
		public boolean isRevertable() {return false;}
		public boolean canPreEvaluate(OpAmbassador ambassador) {return true;}
		public String toString() {return "0";}
	};
	private static final FlowValue mSuccessor = new FlowValue() {
		public boolean isSuccessor() {return true;}
		public boolean hasFixedArity() {return true;}
		public int getFixedArity() {return 1;}
		public boolean isRevertable() {return false;}
		public String toString() {return "+1";}
	};
	private static final FlowValue mTrue = new FlowValue() {
		public boolean isTrue() {return true;}
		public boolean hasFixedArity() {return true;}
		public int getFixedArity() {return 0;}
		public String toString() {return "True";}
		public boolean isBasicOp() {return true;}
		public BasicOp getBasicOp() {return True;}
	};
	private static final FlowValue mFalse = new FlowValue() {
		public boolean isFalse() {return true;}
		public boolean hasFixedArity() {return true;}
		public int getFixedArity() {return 0;}
		public String toString() {return "False";}
		public boolean isBasicOp() {return true;}
		public BasicOp getBasicOp() {return False;}
	};
	private static final FlowValue mPhi = new FlowValue() {
		public boolean isPhi() {return true;}
		public boolean hasFixedArity() {return true;}
		public int getFixedArity() {return 3;}
		public boolean isUndefinedLiftedAny() {return true;}
		public boolean isUndefinedLiftedAll() {return false;}
		public boolean isUndefinedLifted(int child) {return child == 0;}
		public boolean canPreEvaluate(OpAmbassador ambassador) {return true;}
		public String toString() {return "Phi";}
	};
	private static final FlowValue mShortCircuitAnd = new FlowValue() {
		public boolean isShortCircuitAnd() {return true;}
		public boolean hasFixedArity() {return true;}
		public int getFixedArity() {return 2;}
		public boolean canPreEvaluate(OpAmbassador ambassador) {return true;}
		public String toString() {return "&&";}
	};
	private static final FlowValue mShortCircuitOr = new FlowValue() {
		public boolean isShortCircuitOr() {return true;}
		public boolean hasFixedArity() {return true;}
		public int getFixedArity() {return 2;}
		public boolean canPreEvaluate(OpAmbassador ambassador) {return true;}
		public String toString() {return "||";}
	};
	private static final FlowValue mNegate = new FlowValue() {
		public boolean isNegate() {return true;}
		public boolean hasFixedArity() {return true;}
		public int getFixedArity() {return 1;}
		public boolean canPreEvaluate(OpAmbassador ambassador) {return true;}
		public String toString() {return "!";}
		public boolean isBasicOp() {return true;}
		public BasicOp getBasicOp() {return Negate;}
	};
	private static final FlowValue mAnd = new FlowValue() {
		public boolean isAnd() {return true;}
		public boolean hasFixedArity() {return true;}
		public int getFixedArity() {return 2;}
		public boolean canPreEvaluate(OpAmbassador ambassador) {return true;}
		public String toString() {return "&";}
		public boolean isBasicOp() {return true;}
		public BasicOp getBasicOp() {return And;}
	};
	private static final FlowValue mOr = new FlowValue() {
		public boolean isOr() {return true;}
		public boolean hasFixedArity() {return true;}
		public int getFixedArity() {return 2;}
		public boolean canPreEvaluate(OpAmbassador ambassador) {return true;}
		public String toString() {return "|";}
		public boolean isBasicOp() {return true;}
		public BasicOp getBasicOp() {return Or;}
	};
	private static final FlowValue mEquals = new FlowValue() {
		public boolean isEquals() {return true;}
		public boolean hasFixedArity() {return true;}
		public int getFixedArity() {return 2;}
		public boolean canPreEvaluate(OpAmbassador ambassador) {return true;}
		public String toString() {return "=";}
		public boolean isBasicOp() {return true;}
		public BasicOp getBasicOp() {return Equals;}
	};
	private static final IntMap<Theta> mThetas = new ArrayIntMap<Theta>();
	private static final IntMap<Shift> mShifts = new ArrayIntMap<Shift>();
	private static final IntMap<Eval> mEvals = new ArrayIntMap<Eval>();
	private static final IntMap<Pass> mPasses = new ArrayIntMap<Pass>();
	
	public static <V,L> FlowValue<V,L> createDomain(L value,
			OpAmbassador<? super L> ambassador) {
		BasicOp op = ambassador.getBasicOp(value);
		return op == null ? new DomainValue<V,L>(value)
				: op.<V,L>getFlowValue();
	}
	public static <V,L> FlowValue<V,L> createParameter(V parameter) {
		return new Parameter<V,L>(parameter);
	}
	
//	public static <V,L> FlowValue<V,L> createSplit() {return mSplit;}

	public static <V,L> FlowValue<V,L> createZero() {return mZero;}
	
	public static <V,L> FlowValue<V,L> createSuccessor() {return mSuccessor;}
	
	public static <V,L> FlowValue<V,L> createTrue() {return mTrue;}
	
	public static <V,L> FlowValue<V,L> createFalse() {return mFalse;}
	
	public static <V,L> FlowValue<V,L> createPhi() {return mPhi;}
	
	public static <V,L> FlowValue<V,L> createShortCircuitAnd() {
		return mShortCircuitAnd;
	}
	
	public static <V,L> FlowValue<V,L> createShortCircuitOr() {
		return mShortCircuitOr;
	}
	
	public static <V,L> FlowValue<V,L> createNegate() {return mNegate;}
	
	public static <V,L> FlowValue<V,L> createAnd() {return mAnd;}
	
	public static <V,L> FlowValue<V,L> createOr() {return mOr;}

	public static <V,L> FlowValue<V,L> createEquals() {return mEquals;}
	
	public static <V,L> FlowValue<V,L> createTheta(int depth) {
		Theta value = mThetas.get(depth - 1);
		if (value == null) {
			value = new Theta(depth);
			mThetas.put(depth - 1, value);
		}
		return value;
	}
	
	public static <V,L> FlowValue<V,L> createShift(int depth) {
		Shift value = mShifts.get(depth - 1);
		if (value == null) {
			value = new Shift(depth);
			mShifts.put(depth - 1, value);
		}
		return value;
	}
	
	public static <V,L> FlowValue<V,L> createEval(int depth) {
		Eval value = mEvals.get(depth - 1);
		if (value == null) {
			value = new Eval(depth);
			mEvals.put(depth - 1, value);
		}
		return value;
	}
	
	public static <V,L> FlowValue<V,L> createPass(int depth) {
		Pass value = mPasses.get(depth - 1);
		if (value == null) {
			value = new Pass(depth);
			mPasses.put(depth - 1, value);
		}
		return value;
	}
	
	public boolean isExtendedDomain() {
		return isDomain() || isBasicOp();
	}
	public boolean isRevertable() {return true;}
	
	public boolean canPreEvaluate(OpAmbassador<? super L> ambassador) {
		return isBasicOp() || isDomain()
				&& ambassador.canPreEvaluate(getDomain());
	}
	public boolean isBasicOp() {return false;}
	public BasicOp getBasicOp() {throw new UnsupportedOperationException();}
	
	public boolean hasFixedArity() {return false;}
	public int getFixedArity() {throw new UnsupportedOperationException();}

	public boolean isWellDefinedAny() {return true;}
	public boolean isWellDefinedAll() {return true;}
	public boolean isWellDefined(int child) {return true;}
	public boolean isUndefinedLiftedAny() {return isLoopLiftedAny();}
	public boolean isUndefinedLiftedAll() {return isLoopLiftedAll();}
	public boolean isUndefinedLifted(int child) {return isLoopLifted(child);}
	public boolean isLoopLiftedAny() {return true;}
	public boolean isLoopLiftedAny(int loop) {return true;}
	public boolean isLoopLiftedExceptAny(int loop) {return true;}
	public boolean isLoopLiftedAll() {return true;}
	public boolean isLoopLiftedExceptAll(int loop) {return true;}
	public boolean isLoopLiftedAll(int loop) {return true;}
	public boolean isLoopLiftedAll(IntSet loops) {return true;}
	public boolean isLoopLifted(int child) {return true;}
	public boolean isLoopLifted(int loop, int child) {return true;}
	public boolean isLoopLiftedExcept(int loop, int child) {return true;}
	
	public boolean isDomain() {return false;}
	public L getDomain() {throw new UnsupportedOperationException();}
	public L getDomain(OpAmbassador<L> ambassador) {
		if (isDomain())
			return getDomain();
		else if (isBasicOp())
			return ambassador.getBasicOp(getBasicOp());
		else
			throw new UnsupportedOperationException();
	}
	
	public boolean isParameter() {return false;}
	public P getParameter() {throw new UnsupportedOperationException();}

//	public boolean isSplit() {return false;}
	public boolean isZero() {return false;}
	public boolean isSuccessor() {return false;}
	
	public boolean isTrue() {return false;}
	public boolean isFalse() {return false;}
	
	public boolean isPhi() {return false;}
	public boolean isShortCircuitAnd() {return false;}
	public boolean isShortCircuitOr() {return false;}
	public boolean isNegate() {return false;}
	public boolean isAnd() {return false;}
	public boolean isOr() {return false;}
	public boolean isEquals() {return false;}
	
	public boolean isTheta() {return false;}
	public boolean isShift() {return false;}
	public boolean isEval() {return false;}
	public boolean isPass() {return false;}
	public boolean isLoopFunction() {return false;}
	public int getLoopDepth() {throw new UnsupportedOperationException();}

	public <V> FlowValue<V,L> getNonParameter() {return (FlowValue<V,L>)this;}
	
	public boolean equals(FlowValue<P,L> that) {return this == that;}
	public abstract String toString();
	
	private static final class DomainValue<V,L> extends FlowValue<V,L> {
		private final L mValue;
		
		public DomainValue(L value) {mValue = value;}
		
		public boolean isDomain() {return true;}
		public L getDomain() {return mValue;}
		
		public boolean equals(FlowValue<V,L> that) {
			return that.isDomain() && (mValue == null ? that.getDomain() == null
					: mValue.equals(that.getDomain()));
		}
		
		public boolean equals(Object that) {
			return that instanceof DomainValue
				&& (mValue == null ? ((DomainValue)that).mValue == null
						: mValue.equals(((DomainValue)that).mValue));
		}
		
		public int hashCode() {return mValue == null ? 0 : mValue.hashCode();}
		public String toString() {
			return mValue == null ? "<null>" : mValue.toString();
		}
	}
	
	private static final class Parameter<V,L> extends FlowValue<V,L> {
		private final V mParameter;
		
		public Parameter(V parameter) {mParameter = parameter;}
		
		public boolean isParameter() {return true;}
		public V getParameter() {return mParameter;}

		public boolean hasFixedArity() {return true;}
		public int getFixedArity() {return 0;}
		
		public <P> FlowValue<P,L> getNonParameter() {
			throw new UnsupportedOperationException();
		}
		
		public boolean equals(FlowValue<V,L> that) {
			return that.isParameter() && (mParameter == null ?
					that.getParameter() == null
					: mParameter.equals(that.getParameter()));
		}
		
		public boolean equals(Object that) {
			return that instanceof Parameter
				&& (mParameter == null ? ((Parameter)that).mParameter == null
						: mParameter.equals(((Parameter)that).mParameter));
		}
		
		public int hashCode() {
			return mParameter == null ? 0 : mParameter.hashCode();
		}
		public String toString() {
			return mParameter == null ? "<null>" : mParameter.toString();
		}
	}
	
	private static abstract class LoopFunction extends FlowValue {
		protected final int mDepth;
		
		public LoopFunction(int depth) {mDepth = depth;}
		
		public final boolean isLoopFunction() {return true;}
		public final int getLoopDepth() {return mDepth;}
		public boolean isLoopLiftedAny() {return false;}
		public boolean isLoopLiftedAny(int loop) {return mDepth != loop;}
		public boolean isLoopLiftedExceptAny(int loop) {return mDepth == loop;}
		public boolean isLoopLiftedAll() {return false;}
		public boolean isLoopLiftedAll(int loop) {return mDepth != loop;}
		public boolean isLoopLiftedExceptAll(int loop) {return mDepth == loop;}
		public boolean isLoopLiftedAll(IntSet loops) {
			return !loops.contains(mDepth);
		}
		public boolean isLoopLifted(int child) {return false;}
		public boolean isLoopLifted(int loop, int child) {
			return isLoopLiftedAll(loop) || isLoopLifted(child);
		}
		public boolean isLoopLiftedExcept(int loop, int child) {
			return isLoopLiftedExceptAll(loop) || isLoopLifted(child);
		}
	}
	
	private static final class Theta extends LoopFunction {
		public Theta(int depth) {super(depth);}
		
		public boolean isTheta() {return true;}
		public boolean hasFixedArity() {return true;}
		public int getFixedArity() {return 2;}
		public String toString() {return "Theta-" + mDepth;}
	}
	
	private static final class Shift extends LoopFunction {
		public Shift(int depth) {super(depth);}
		
		public boolean isShift() {return true;}
		public boolean hasFixedArity() {return true;}
		public int getFixedArity() {return 1;}
		public boolean isRevertable() {return false;}
		public boolean canPreEvaluate(OpAmbassador ambassador) {return true;}
		public String toString() {return "Shift-" + mDepth;}
	}
	
	private static final class Eval extends LoopFunction {
		public Eval(int depth) {super(depth);}
		
		public boolean isEval() {return true;}
		public boolean hasFixedArity() {return true;}
		public int getFixedArity() {return 2;}
		public boolean isLoopLiftedAny() {return true;}
		public boolean isLoopLiftedAny(int loop) {return true;}
		public boolean isLoopLiftedExceptAny(int loop) {return true;}
		public boolean isLoopLifted(int child) {return child == 1;}
		public boolean canPreEvaluate(OpAmbassador ambassador) {return true;}
		public String toString() {return "Eval-" + mDepth;}
	}
	
	private static final class Pass extends LoopFunction {
		public Pass(int depth) {super(depth);}
		
		public boolean isPass() {return true;}
		public boolean hasFixedArity() {return true;}
		public int getFixedArity() {return 1;}
		public String toString() {return "Pass-" + mDepth;}
	}
}
