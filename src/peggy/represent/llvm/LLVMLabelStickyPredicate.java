package peggy.represent.llvm;

import peggy.represent.StickyPredicate;

/**
 * This is a sticky predicate for LLVMLabels.
 */
public class LLVMLabelStickyPredicate implements StickyPredicate<LLVMLabel> {
	public static final LLVMLabelStickyPredicate INSTANCE = 
		new LLVMLabelStickyPredicate();
	
	private LLVMLabelStickyPredicate() {}
	
	public boolean isSticky(LLVMLabel label, int childIndex) {
		return needsChild(label, childIndex);
	}
	public boolean allowsChild(
			LLVMLabel parent, 
			int childIndex, 
			LLVMLabel child) {
		return LLVMLabelAllowsChildPredicate.allowsChild(
				parent, childIndex, child);
	}
	
	public static boolean needsAnyChild(LLVMLabel op) {
		if (op.isSimple()) {
			LLVMOperator operator = op.getSimpleSelf().getOperator();
			switch (operator) {
			case NONSTACK:
			case PARAMS:
			case VOID:
			case RETURNSTRUCTURE:
			case INDEXES:
			case FREE:
			case UNWIND:
			case SHUFFLEVECTOR:
			case INSERTELEMENT:
			case SELECT:
			case EXTRACTELEMENT:
				return false;

			case INJR:
			case INJL:
			case IS_EXCEPTION:
			case RHO_EXCEPTION:
				return false;
				
			case RHO_VALUE:
			case RHO_SIGMA:
				return false;
				
			case GETELEMENTPTR:
//			case PARAMATTRMAP:
			case TAILCALL:
			case CALL:
			case INVOKE:
			case MALLOC:
			case ALLOCA:
			case VOLATILE_LOAD:
			case LOAD:
			case VOLATILE_STORE:
			case STORE:
			case GETRESULT:
			case VAARG:
				return true;
			default:
				throw new IllegalArgumentException("Mike forgot to handle: " + operator.name());
			}
		} 
		else if (op.isBinop() ||
				 op.isCast() ||
				 op.isType() ||
				 op.isNumeral() ||
				 op.isParamAttr() ||
				 op.isFunction() ||
				 op.isGlobal() ||
				 op.isInlineASM() ||
				 op.isConstantValue() ||
				 op.isBasicOp() ||
				 op.isCmp() ||
				 op.isAnnotation()) {
			return false;
		}
		else
			throw new IllegalArgumentException("Mike forgot to handle: " + op.getClass());
	}
	public static boolean needsChild(LLVMLabel op, int childIndex) {
		if (op.isSimple()) {
			LLVMOperator operator = op.getSimpleSelf().getOperator();
			switch (operator) {
			case NONSTACK:
			case PARAMS:
			case VOID:
			case RETURNSTRUCTURE:
			case INDEXES:
			case FREE:
			case UNWIND:
			case SHUFFLEVECTOR:
			case INSERTELEMENT:
			case SELECT:
			case EXTRACTELEMENT:
				return false;

			case INJR:
			case INJL:
			case IS_EXCEPTION:
			case RHO_EXCEPTION:
				return false;
				
			case RHO_VALUE:
			case RHO_SIGMA:
				return false;

			case GETELEMENTPTR:
				return (childIndex==1) || (childIndex == 2);
				// indexes
//			case PARAMATTRMAP:
//				return true; 
				// sticks to all paramattr children
			case TAILCALL:
			case CALL:
				return (childIndex==2) || (childIndex==3) || (childIndex==4);
				// CC, params, paramAttrs
			case INVOKE:
				return (childIndex==2) || (childIndex==3) || (childIndex==4);
				// CC, params, paramAttrs
			case MALLOC:
				return (childIndex==1) || (childIndex==3);
				// type and alignment
			case ALLOCA:
				return (childIndex==1) || (childIndex==3);
				// type and alignment
			case VOLATILE_LOAD:
				return (childIndex==2);
				// alignment
			case LOAD:
				return (childIndex==2);
				// alignment
			case VOLATILE_STORE:
				return (childIndex==3);
				// alignment
			case STORE:
				return (childIndex==3);
				// alignment
			case GETRESULT:
				return (childIndex==1);
				// index
			case VAARG:
				return (childIndex==2);
				// type
			default:
				throw new IllegalArgumentException("Mike forgot to handle: " + operator.name());
			}
		} 
		else if (op.isBinop() ||
				 op.isCast() ||
				 op.isType() ||
				 op.isNumeral() ||
				 op.isParamAttr() ||
				 op.isFunction() ||
				 op.isGlobal() ||
				 op.isInlineASM() ||
				 op.isConstantValue() ||
				 op.isBasicOp() ||
				 op.isCmp() ||
				 op.isAnnotation()) {
			return false;
		}
		else
			throw new IllegalArgumentException("Mike forgot to handle: " + op.getClass());
	}
}
