package peggy.analysis.llvm;

import java.util.ArrayList;
import java.util.List;

import llvm.bitcode.UnsignedLong;
import llvm.instructions.FunctionBody;
import llvm.types.CompositeType;
import llvm.types.PointerType;
import llvm.types.Type;
import llvm.types.VectorType;
import llvm.values.IntegerValue;
import peggy.analysis.EPEGTypeAnalysis;
import peggy.analysis.llvm.types.LLVMType;
import peggy.analysis.llvm.types.PEGType;
import peggy.represent.llvm.AliasLLVMLabel;
import peggy.represent.llvm.ArgumentLLVMParameter;
import peggy.represent.llvm.ConstantValueLLVMLabel;
import peggy.represent.llvm.FunctionLLVMLabel;
import peggy.represent.llvm.GlobalLLVMLabel;
import peggy.represent.llvm.InlineASMLLVMLabel;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMOperator;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.NumeralLLVMLabel;
import peggy.represent.llvm.TypeLLVMLabel;
import eqsat.meminfer.engine.basic.EGraphManager;
import eqsat.meminfer.engine.peg.CPEGTerm;
import eqsat.meminfer.engine.peg.CPEGValue;

/**
 * This class runs over the EPEG occasionally and annotates every
 * Value with its type.
 */
public class LLVMEPEGTypeAnalysis 
extends EPEGTypeAnalysis<LLVMLabel,LLVMParameter,LLVMType> {
	private static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("LLVMEPEGTypeAnalysis: " + message);
	}
	
	protected FunctionBody body;
	
	public LLVMEPEGTypeAnalysis(
			EGraphManager<CPEGTerm<LLVMLabel, LLVMParameter>, CPEGValue<LLVMLabel, LLVMParameter>> _egraph) {
		super(_egraph);
	}
	
	public void setCurrentMethod(FunctionBody _body) {
		this.body = _body;
	}
	
	private PEGType<LLVMType> md(LLVMType t) {
		return PEGType.<LLVMType>makeDomain(t);
	}
	private PEGType<LLVMType> dj(PEGType<LLVMType> lhs, PEGType<LLVMType> rhs) {
		return PEGType.<LLVMType>makeDisjointUnion(lhs, rhs);
	}
	private PEGType<LLVMType> tuple(PEGType<LLVMType>... elts) {
		return PEGType.<LLVMType>makeTupleType(elts);
	}

	protected PEGType<LLVMType> computeDomainType(
			CPEGTerm<LLVMLabel,LLVMParameter> term) {
		LLVMLabel label = term.getOp().getDomain();
		if (label.isSimple()) {
			return this.computeSimpleType(term);
		} 
		else if (label.isType()) {
			return md(LLVMType.TYPE);
		}
		else if (label.isBinop()) {
			PEGType<LLVMType> lhs = this.getType(term.getChild(0).getValue());
			if (lhs!=null)
				return lhs;
			return this.getType(term.getChild(1).getValue());
		}
		else if (label.isCast()) {
			// find the type node in the first child value
			for (CPEGTerm<LLVMLabel,LLVMParameter> child : term.getChild(0).getValue().getTerms()) {
				if (child.getOp().isDomain() &&
					child.getOp().getDomain().isType()) {
					// found a type node
					TypeLLVMLabel typenode = child.getOp().getDomain().getTypeSelf();
					return md(LLVMType.makeSimple(typenode.getType()));
				}
			}
		}
		else if (label.isCmp()) {
			return md(LLVMType.makeSimple(Type.BOOLEAN_TYPE));
		}
		else if (label.isNumeral()) {
			return md(LLVMType.NUMERAL);
		}
		else if (label.isFunction()) {
			FunctionLLVMLabel func = label.getFunctionSelf();
			return md(LLVMType.makeSimple(new PointerType(func.getType())));
		}
		else if (label.isGlobal()) {
			GlobalLLVMLabel global = label.getGlobalSelf();
			return md(LLVMType.makeSimple(new PointerType(global.getType())));
		}
		else if (label.isAlias()) {
			AliasLLVMLabel alias = label.getAliasSelf();
			return md(LLVMType.makeSimple(new PointerType(alias.getType())));
		}
		else if (label.isInlineASM()) {
			InlineASMLLVMLabel inline = label.getInlineASMSelf();
			return md(LLVMType.makeSimple(inline.getASM().getType()));
		} 
		else if (label.isConstantValue()) {
			ConstantValueLLVMLabel value = label.getConstantValueSelf();
			return md(LLVMType.makeSimple(value.getValue().getType()));
		}
		else if (label.isBasicOp()) {
			return PEGType.<LLVMType>makeBoolean();
		}
		else if (label.isAnnotation()) {
			return null;
		}
		else
			throw new RuntimeException("Forgot to handle: " + label);
		
		debug("Cannot find domain type for: " + term.getOp());
		return null;
	}
	
	protected Type getIndexedType(Type base, CPEGTerm<LLVMLabel,LLVMParameter> indexes) {
		for (int i = 0; i < indexes.getArity(); i++) {
			if (base.isComposite()) {
				CompositeType cbase = base.getCompositeSelf();
				if (cbase.isArray() || cbase.isVector() || cbase.isPointer()) {
					base = cbase.getElementType(0);
				} else if (cbase.isStructure()) {
					// find a literal int in the Ith child value
					IntegerValue value = null;
					for (CPEGTerm<LLVMLabel,LLVMParameter> indexI : indexes.getChild(i).getValue().getTerms()) {
						if (indexI.getOp().isDomain() && 
							indexI.getOp().getDomain().isConstantValue() &&
							indexI.getOp().getDomain().getConstantValueSelf().getValue().isInteger()) {
							value = indexI.getOp().getDomain().getConstantValueSelf().getValue().getIntegerSelf();
							break;
						}
					}
					if (value == null)
						return null;
					base = cbase.getElementType(value.getIntBits());
				} else {
					throw new RuntimeException("Forgot to handle: " + cbase);
				}
			} else {
				throw new RuntimeException("Not a composite type: " + base);
			}
		}
		return base;
	}
	
	protected Type getIndexedNumeralType(Type base, CPEGTerm<LLVMLabel,LLVMParameter> offsets) {
		for (int i = 0; i < offsets.getArity(); i++) {
			if (base.isComposite()) {
				CompositeType cbase = base.getCompositeSelf();
				if (cbase.isArray() || cbase.isVector() || cbase.isPointer()) {
					base = cbase.getElementType(0);
				} else if (cbase.isStructure()) {
					// find a numeral in the Ith child value
					NumeralLLVMLabel value = null;
					for (CPEGTerm<LLVMLabel,LLVMParameter> indexI : offsets.getChild(i).getValue().getTerms()) {
						if (indexI.getOp().isDomain() && 
							indexI.getOp().getDomain().isNumeral()) {
							value = indexI.getOp().getDomain().getNumeralSelf();
							break;
						}
					}
					if (value == null)
						return null;
					base = cbase.getElementType(value.getValue());
				} else {
					throw new RuntimeException("Forgot to handle: " + cbase);
				}
			} else {
				throw new RuntimeException("Not a composite type");
			}
		}
		return base;
	}

	
	protected PEGType<LLVMType> computeSimpleType(
			CPEGTerm<LLVMLabel,LLVMParameter> term) {
		final LLVMOperator operator = term.getOp().getDomain().getSimpleSelf().getOperator();
		
		switch (operator) {
		case MALLOC:
		case ALLOCA: {// (sigma,()|ptr)
			// find type node in child1
			for (CPEGTerm<LLVMLabel,LLVMParameter> child1 : term.getChild(1).getValue().getTerms()) {
				if (child1.getOp().isDomain() && child1.getOp().getDomain().isType()) {
					Type type = child1.getOp().getDomain().getTypeSelf().getType();
					return tuple(
							md(LLVMType.SIGMA),
							dj(tuple(), md(LLVMType.makeSimple(new PointerType(type)))));					
				}
			}
			break;
		}
			
		case TAILCALL:
		case INVOKE:
		case CALL: { // (sigma,e|v)
			// find return value from function type in child1
			PEGType<LLVMType> functype = this.getType(term.getChild(1).getValue());
			if (functype != null && functype.isDomain() && functype.getDomain().isSimple()) {
				// will be pointer to function type
				Type funcType = functype.getDomain().getSimpleType().getCompositeSelf().getPointerSelf().getPointeeType();
				if (funcType.isFunction()) {
					Type returnType = funcType.getFunctionSelf().getReturnType();
					return tuple(
							md(LLVMType.SIGMA),
							dj(md(LLVMType.EXCEPTION), md(LLVMType.makeSimple(returnType))));
				}
			}
			break;
		}
		
		case EXTRACTELEMENT: {// element type of child 0
			PEGType<LLVMType> child0 = this.getType(term.getChild(0).getValue());
			if (child0 != null && 
				child0.isDomain() && 
				child0.getDomain().isSimple()) {
				Type type = child0.getDomain().getSimpleType();
				if (type.isComposite() && type.getCompositeSelf().isVector())
					return md(LLVMType.makeSimple(type.getCompositeSelf().getVectorSelf().getElementType()));
			}
			break;
		}
			
		case EXTRACTVALUE: { // (agg, numerals) -> v
			PEGType<LLVMType> child0 = this.getType(term.getChild(0).getValue());
			if (child0!=null && child0.isDomain() && child0.getDomain().isSimple()) {
				// find offsets in value of child1
				for (CPEGTerm<LLVMLabel,LLVMParameter> offsets : term.getChild(1).getValue().getTerms()) {
					if (offsets.getOp().isDomain() &&
						offsets.getOp().getDomain().isSimple() &&
						offsets.getOp().getDomain().getSimpleSelf().getOperator().equals(LLVMOperator.OFFSETS)) {
						Type result = getIndexedNumeralType(child0.getDomain().getSimpleType(), offsets);
						if (result != null)
							return md(LLVMType.makeSimple(result));
					}
				}
			}
			break;
		}
			
		case RHO_SIGMA:
		case NONSTACK:
		case FREE: // sigma
			return md(LLVMType.SIGMA);
			
		case GETELEMENTPTR:
		case INBOUNDSGETELEMENTPTR: { // (base,indexes) -> v
			PEGType<LLVMType> base = this.getType(term.getChild(0).getValue());
			if (base!=null && base.isDomain() && base.getDomain().isSimple()) {
				// find indexes term
				for (CPEGTerm<LLVMLabel,LLVMParameter> indexes : term.getChild(2).getValue().getTerms()) {
					if (indexes.getOp().isDomain() && 
						indexes.getOp().getDomain().isSimple() &&
						indexes.getOp().getDomain().getSimpleSelf().getOperator().equals(LLVMOperator.INDEXES)) {

						Type result = this.getIndexedType(base.getDomain().getSimpleType(), indexes);
						if (result!=null)
							return md(LLVMType.makeSimple(new PointerType(result)));
					}
				}
			}
			break;
		}
			
		case GETRESULT: {// ((r0,...rN),M:numeral) -> rM
			PEGType<LLVMType> child0 = this.getType(term.getChild(0).getValue());
			if (child0 != null && child0.isTuple()) {
				for (CPEGTerm<LLVMLabel,LLVMParameter> child1 : term.getChild(1).getValue().getTerms()) {
					if (child1.getOp().isDomain() && child1.getOp().getDomain().isNumeral()) {
						int value = child1.getOp().getDomain().getNumeralSelf().getValue();
						return child0.getTupleElement(value);
					}
				}
			}
			break;
		}
			
		case INJR:
		case INJL: // e|returnvalue
			return dj(md(LLVMType.EXCEPTION),
					  md(LLVMType.makeSimple(this.body.getHeader().getFunctionType().getReturnType())));
			
		case INSERTELEMENT: // same as child0
			return this.getType(term.getChild(0).getValue());
			
		case INSERTVALUE: // same as child0
			return this.getType(term.getChild(0).getValue());
			
		case IS_EXCEPTION: // bool
			return PEGType.<LLVMType>makeBoolean();

		case VOLATILE_LOAD:
		case LOAD: {// (sigma,()|v), v* is type of child1
			PEGType<LLVMType> child1 = this.getType(term.getChild(1).getValue());
			if (child1!=null && child1.isDomain() && child1.getDomain().isSigma()) {
				Type type1 = child1.getDomain().getSimpleType();
				if (type1.isComposite() && type1.getCompositeSelf().isPointer()) {
					Type eltType = type1.getCompositeSelf().getPointerSelf().getPointeeType();
					return tuple(md(LLVMType.SIGMA),
								 dj(tuple(),
								    md(LLVMType.makeSimple(eltType))));
				}
			}
			break;
		}
		
		case INDEXES:
		case OFFSETS:
		case PARAMS: // who cares, randomly give numeral type
			return md(LLVMType.NUMERAL);
			
		case RETURNSTRUCTURE: {// tuple of all children types
			List<PEGType<LLVMType>> childtypes = 
				new ArrayList<PEGType<LLVMType>>(term.getArity());
			for (int i = 0; i < term.getArity(); i++) {
				PEGType<LLVMType> childI = this.getType(term.getChild(i).getValue());
				if (childI==null)
					return null;
				childtypes.add(childI);
			}
			return PEGType.<LLVMType>makeTupleType(childtypes);
		}
			
		case RHO_EXCEPTION: // exception
			return md(LLVMType.EXCEPTION);
			
		case RHO_VALUE: { // (A,B|C) -> C
			PEGType<LLVMType> child = this.getType(term.getChild(0).getValue());
			if (child != null &&
				child.isTuple() && 
				child.getNumTupleElements()==2 &&
				child.getTupleElement(1).isDisjointUnion()) {
				return child.getTupleElement(1).getDisjointUnionRight();
			}
			break;
		}
		
		case VSELECT:
		case SELECT: // same as first child
			return this.getType(term.getChild(1).getValue());

		case SHUFFLEVECTOR: { // (<n x TY>, <n x TY>, <m x i32>) -> <m x TY>
			PEGType<LLVMType> maskType = this.getType(term.getChild(2).getValue());
			if (maskType!=null && maskType.isDomain() && maskType.getDomain().isSimple()) {
				Type mType = maskType.getDomain().getSimpleType();
				if (mType.isComposite() && mType.getCompositeSelf().isVector()) {
					UnsignedLong M = mType.getCompositeSelf().getVectorSelf().getNumElements();
					PEGType<LLVMType> vecType = this.getType(term.getChild(0).getValue());
					if (vecType==null)
						vecType = this.getType(term.getChild(1).getValue());
					if (vecType!=null && vecType.isDomain() && vecType.getDomain().isSimple()) {
						Type vType = vecType.getDomain().getSimpleType();
						if (vType.isComposite() && vType.getCompositeSelf().isVector()) {
							Type eltType = vType.getCompositeSelf().getVectorSelf().getElementType();
							return md(LLVMType.makeSimple(new VectorType(eltType, M)));
						}
					}
				}
			}
			break;
		}

		case VOLATILE_STORE:
		case STORE: // sigma
			return md(LLVMType.SIGMA);
		
		case UNWIND: // (sigma,e|())
			return tuple(md(LLVMType.SIGMA),
					     dj(md(LLVMType.EXCEPTION),
					    	tuple()));
			
		case VAARG: {// type of type node in child2
			for (CPEGTerm<LLVMLabel,LLVMParameter> child2 : term.getChild(2).getValue().getTerms()) {
				if (child2.getOp().isDomain() && child2.getOp().getDomain().isType()) {
					Type type = child2.getOp().getDomain().getTypeSelf().getType();
					return md(LLVMType.makeSimple(type));
				}
			}
			break;
		}

		case VOID: // void
			return md(LLVMType.makeSimple(Type.VOID_TYPE));
		
		default:
			throw new RuntimeException("Forgot to handle: " + operator);
		}
		
		debug("Cannot find simple type for: " + term.getOp());
		return null;
	}
	
	protected PEGType<LLVMType> computeParameterType(LLVMParameter param) {
		if (param.isSigma()) {
			return md(LLVMType.SIGMA);
		} else if (param.isArgument()) {
			ArgumentLLVMParameter arg = param.getArgumentSelf();
			return md(LLVMType.makeSimple(arg.getType()));
		} else {
			debug("Cannot find parameter type for: " + param);
			return null;
		}
	}
}
