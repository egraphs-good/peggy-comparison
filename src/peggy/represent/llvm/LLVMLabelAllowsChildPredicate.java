package peggy.represent.llvm;

import static peggy.represent.llvm.LLVMOperator.*;

/**
 * This class encodes the 'allowsChild' information for the LLVM sticky
 * predicate.
 */
public class LLVMLabelAllowsChildPredicate {
	private LLVMLabelAllowsChildPredicate() {}

	private static boolean isOp(LLVMLabel parent, LLVMOperator operator) {
		return 
		parent.isSimple() &&
		parent.getSimpleSelf().getOperator().equals(operator);
	}
	
	/** Assume that the childindex will always be in range for the parent.
	 */
	public static boolean allowsChild(LLVMLabel parent, int childIndex, LLVMLabel child) {
		if (parent.isSimple()) {
			LLVMOperator operator = parent.getSimpleSelf().getOperator();
			switch (operator) {
			case NONSTACK:
			case INJR:
			case INJL:
			case RHO_VALUE:
			case RHO_SIGMA:
			case RHO_EXCEPTION:
			case IS_EXCEPTION:
				// projections are not sticky
				return true;

			case GETELEMENTPTR:
				if (childIndex == 1)
					return child.isType();
				else if (childIndex == 2)
					return isOp(child, INDEXES);
				else
					return true;
				// indexes
//			case PARAMATTRMAP:
//				return child.isParamAttr(); 
				// sticks to all paramattr children
			case TAILCALL:
			case CALL:
			case INVOKE:
				if (childIndex==2)
					return child.isNumeral();
				else if (childIndex==3)
					return isOp(child, PARAMS);
//				else if (childIndex==4)
//					return isOp(child, PARAMATTRMAP);
				else
					return true;
				// CC, params, paramAttrs
			case MALLOC:
				if (childIndex==1)
					return child.isType();
				else if (childIndex==3)
					return child.isNumeral();
				else
					return true;
				// type and alignment
			case ALLOCA:
				if (childIndex==1)
					return child.isType();
				else if (childIndex==3)
					return child.isNumeral();
				else
					return true;
				// type and alignment
			case VOLATILE_LOAD:
				if (childIndex==2)
					return child.isNumeral();
				else
					return true;
				// alignment
			case LOAD:
				if (childIndex==2)
					return child.isNumeral();
				else
					return true;
				// alignment
			case VOLATILE_STORE:
				if (childIndex==3)
					return child.isNumeral();
				else
					return true;
				// alignment
			case STORE:
				if (childIndex==3)
					return child.isNumeral();
				else
					return true;
				// alignment
			case GETRESULT:
				if (childIndex==1)
					return child.isNumeral();
				else if (childIndex==0)
					return isOp(child, RETURNSTRUCTURE);
				else
					return true;
				// index and returnstructure
			case VAARG:
				if (childIndex==2)
					return child.isType();
				else
					return true;
				// type
			default:
				return true;
			}
		}
		else if (parent.isCast()) {
			if (childIndex==0)
				return child.isType();
			else
				return true;
		}
		else if (parent.isType() ||
				 parent.isBinop() ||
				 parent.isCmp() ||
				 parent.isNumeral() ||
				 parent.isParamAttr() ||
				 parent.isFunction() ||
				 parent.isGlobal() ||
				 parent.isInlineASM() ||
				 parent.isConstantValue() ||
				 parent.isBasicOp()) {
			return true;
		} else {
			throw new RuntimeException("Mike forgot to implement: " + parent.getClass());
		}
	}
}
