package peggy.represent.llvm;

import java.util.List;

import llvm.values.IntegerValue;
import llvm.values.Value;
import peggy.analysis.ConstantFolder;
import util.AbstractVariaticFunction;
import eqsat.BasicOp;
import eqsat.OpAmbassador;
import eqsat.OpExpression;

/**
 * This is the OpAmbassador for LLVM.
 */
public class LLVMOpAmbassador extends AbstractVariaticFunction<LLVMLabel,LLVMLabel,LLVMLabel> 
implements OpAmbassador<LLVMLabel> {
	protected final ConstantFolder<LLVMLabel> folder;
	protected final boolean exceptions;
	protected final boolean linearLoads;
	protected final GEPForcingPolicy forcingPolicy;
	
	public LLVMOpAmbassador(
			ConstantFolder<LLVMLabel> _folder,
			GEPForcingPolicy _forcingPolicy,
			boolean _exceptions,
			boolean _linearLoads) {
		this.folder = _folder;
		this.forcingPolicy = _forcingPolicy;
		this.exceptions = _exceptions;
		this.linearLoads = _linearLoads;
	}
	
	/**
	 * We only allow linear loads during TV!
	 */
	public boolean hasLinearLoads() {return this.linearLoads;}
	public boolean hasExceptions() {return this.exceptions;}
	public GEPForcingPolicy getGEPForcingPolicy() {return this.forcingPolicy;}
	
	public LLVMLabel getBasicOp(BasicOp op) {
		switch (op) {
		case True:
			return new ConstantValueLLVMLabel(IntegerValue.TRUE);
		case False:
			return new ConstantValueLLVMLabel(IntegerValue.FALSE);
		default:
			return new BasicOpLLVMLabel(op);
		}
	}
	public boolean canPreEvaluate(LLVMLabel op) {
		if (op.isSimple()) {
			LLVMOperator operator = op.getSimpleSelf().getOperator();
			switch (operator) {
			case NONSTACK:
			case INJR:
			case INJL:
			case CALL:
			case TAILCALL:
			case INVOKE:
			case RHO_VALUE:
			case RHO_SIGMA:
			case RHO_EXCEPTION:
			case INDEXES:
			case MALLOC:
			case FREE:
			case ALLOCA:
			case VOLATILE_LOAD:
			case LOAD:
			case VOLATILE_STORE:
			case STORE:
			case PARAMS:
			case UNWIND:
			case VOID:
			case RETURNSTRUCTURE:
			case VAARG:
			case IS_EXCEPTION:
			case GETELEMENTPTR:
				return false;
				
			case SHUFFLEVECTOR:
			case INSERTELEMENT:
			case SELECT:
			case VSELECT:
			case EXTRACTELEMENT:
			case INSERTVALUE:
			case EXTRACTVALUE:
			case GETRESULT:
				return true;
			default:
				throw new IllegalArgumentException("Mike forgot to handle: " + operator.name());
			}
		} 
		else if (op.isType() ||
				 op.isBinop() ||
				 op.isCast() ||
				 op.isCmp() ||
				 op.isNumeral() ||
				 op.isParamAttr() ||
				 op.isFunction() ||
				 op.isGlobal() ||
				 op.isAlias() ||
				 op.isInlineASM() ||
				 op.isConstantValue() ||
				 op.isBasicOp() ||
				 op.isAnnotation()) {
			return true;
		}
		else
			throw new IllegalArgumentException("Mike forgot to handle: " + op.getClass());
	}
	public boolean isFree(LLVMLabel op) {
		if (op.isSimple()) {
			LLVMOperator operator = op.getSimpleSelf().getOperator();
			switch (operator) {
			case NONSTACK:
			case INJR:
			case INJL:
			case RHO_VALUE:
			case RHO_SIGMA:
			case RHO_EXCEPTION:
			case PARAMS:
			case VOID:
			case RETURNSTRUCTURE:
			case IS_EXCEPTION:
				return true;
				
			case TAILCALL:
			case CALL:
			case INVOKE:
			case INDEXES:
			case MALLOC:
			case FREE:
			case ALLOCA:
			case VOLATILE_LOAD:
			case LOAD:
			case VOLATILE_STORE:
			case STORE:
			case UNWIND:
			case SHUFFLEVECTOR:
			case INSERTELEMENT:
			case GETELEMENTPTR:
			case SELECT:
			case EXTRACTELEMENT:
			case GETRESULT:
			case VAARG:
				return false;
			default:
				throw new IllegalArgumentException("Mike forgot to handle: " + operator.name());
			}
		} 
		else if (op.isBinop() ||
				 op.isCast() ||
				 op.isCmp()) {
			return false;
		}
		else if (op.isType() ||
				 op.isNumeral() ||
				 op.isParamAttr() ||
				 op.isFunction() ||
				 op.isGlobal() ||
				 op.isAlias() ||
				 op.isInlineASM() ||
				 op.isConstantValue() ||
				 op.isBasicOp() ||
				 op.isAnnotation()) {
			return true;
		}
		else
			throw new IllegalArgumentException("Mike forgot to handle: " + op.getClass());
	}
	public boolean needsAnyChild(LLVMLabel op) {
		return LLVMLabelStickyPredicate.needsAnyChild(op);
	}
	public boolean needsChild(LLVMLabel op, int childIndex) {
		return LLVMLabelStickyPredicate.needsChild(op, childIndex);
	}
	public BasicOp getBasicOp(LLVMLabel op) {
		if (op.isConstantValue()) {
			Value value = op.getConstantValueSelf().getValue();
			if (value.equals(IntegerValue.TRUE))
				return BasicOp.True;
			else if (value.equals(IntegerValue.FALSE))
				return BasicOp.False;
			else
				return null;
		} else if (op.isBasicOp()) {
			return op.getBasicOpSelf().getOperator();
		} else {
			return null;
		}
	}
	public boolean isAnyVolatile(LLVMLabel op) {
		return isVolatile(op, 0); // only volatile values are in the 0th position
	}
	public boolean isVolatile(LLVMLabel op, int childIndex) {
		if (op.isSimple()) {
			LLVMOperator operator = op.getSimpleSelf().getOperator();
			switch (operator) {
			case INJR:
			case INJL:
			case RHO_VALUE:
			case RHO_SIGMA:
			case RHO_EXCEPTION:
			case PARAMS:
			case VOID:
			case RETURNSTRUCTURE:
			case INDEXES:
			case SHUFFLEVECTOR:
			case INSERTELEMENT:
			case GETELEMENTPTR:
			case SELECT:
			case EXTRACTELEMENT:
//			case PARAMATTRMAP:
			case GETRESULT:
			case IS_EXCEPTION:
				return false;
				
			case NONSTACK:
			case FREE:
			case UNWIND:
			case TAILCALL:
			case CALL:
			case INVOKE:
			case MALLOC:
			case ALLOCA:
			case VOLATILE_LOAD:
			case LOAD:
			case VOLATILE_STORE:
			case STORE:
			case VAARG:
				return (childIndex==0);
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
				 op.isAlias() ||
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
	public LLVMLabel getChainVersion(LLVMLabel op, int childIndex) {
		if (!isVolatile(op, childIndex))
			throw new IllegalArgumentException("Child " + childIndex + " is not volatile for " + op);
		return op;
	}
	public LLVMLabel getChainProjectValue(LLVMLabel op, int childIndex) {
		if (!isVolatile(op, childIndex))
			throw new IllegalArgumentException("Child " + childIndex + " is not volatile for " + op);
		return null;
	}
	public LLVMLabel getChainProjectVolatile(LLVMLabel op, int childIndex) {
		if (!isVolatile(op, childIndex))
			throw new IllegalArgumentException("Child " + childIndex + " is not volatile for " + op);
		
		if (op.isSimple()) {
			LLVMOperator operator = op.getSimpleSelf().getOperator();
			switch (operator) {
			case NONSTACK:
			case INJR:
			case INJL:
			case RHO_VALUE:
			case RHO_SIGMA:
			case RHO_EXCEPTION:
			case PARAMS:
			case VOID:
			case RETURNSTRUCTURE:
			case INDEXES:
			case SHUFFLEVECTOR:
			case INSERTELEMENT:
			case GETELEMENTPTR:
			case SELECT:
			case EXTRACTELEMENT:
//			case PARAMATTRMAP:
			case GETRESULT:
			case IS_EXCEPTION:
				return null;

			case TAILCALL:
			case CALL:
			case INVOKE:
				// (sigma,func,cc,params,attrs) -> (sigma,()|v)
			case UNWIND:
				// sigma -> (sigma,e|())
			case MALLOC:
			case ALLOCA:
				// (sigma,type,numElts,align) -> (sigma,()|v)
			case VOLATILE_LOAD:
			case LOAD:
				// (sigma,v:ptr,n:align) -> (sigma,()|v)
			case VAARG:
				// (sigma,v,type) -> (sigma,()|v)
				return SimpleLLVMLabel.get(LLVMOperator.RHO_SIGMA);
				
			case FREE:
				// (sigma,ptr) -> sigma
			case VOLATILE_STORE:
			case STORE:
				// (sigma,v:ptr,v:val,n:align) -> sigma
				return null;

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
				 op.isAlias() ||
				 op.isInlineASM() ||
				 op.isConstantValue() ||
				 op.isBasicOp() ||
				 op.isCmp() ||
				 op.isAnnotation()) {
			return null;
		}
		else
			throw new IllegalArgumentException("Mike forgot to handle: " + op.getClass());
	}
	public boolean isEquivalent(OpExpression<LLVMLabel> first, int firstChild,
			OpExpression<LLVMLabel> second, int secondChild) {
		LLVMLabel label = first.getOperation();
		
		if (label.isSimple()) {
			LLVMOperator op = label.getSimpleSelf().getOperator();
			if ((op.equals(LLVMOperator.CALL) && firstChild==0) ||
				(op.equals(LLVMOperator.TAILCALL) && firstChild==0) ||
				(op.equals(LLVMOperator.INVOKE) && firstChild==0) ||
				(op.equals(LLVMOperator.MALLOC) && firstChild==0) ||
				(op.equals(LLVMOperator.FREE) && firstChild==0) ||
				(op.equals(LLVMOperator.ALLOCA) && firstChild==0) ||
				(op.equals(LLVMOperator.VOLATILE_STORE) && firstChild==0) ||
				(op.equals(LLVMOperator.STORE) && firstChild==0) ||
				(op.equals(LLVMOperator.UNWIND) && firstChild==0) ||
				(op.equals(LLVMOperator.VAARG) && firstChild==0)) {
				return false;
			} else if ((op.equals(LLVMOperator.VOLATILE_LOAD) && firstChild==0) ||
					   (op.equals(LLVMOperator.LOAD) && firstChild==0)) {
				return false;
			}
		}
		return true;
	}
	
	public LLVMLabel get(LLVMLabel root, List<? extends LLVMLabel> children) {
//		if (true)
//			return null;
		return this.folder.fold(root, children);
	}
}
