package peggy.analysis.llvm.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import llvm.bitcode.UnsignedLong;
import llvm.types.CompositeType;
import llvm.types.FunctionType;
import llvm.types.IntegerType;
import llvm.types.PointerType;
import llvm.types.StructureType;
import llvm.types.Type;
import llvm.types.VectorType;
import llvm.values.IntegerValue;
import peggy.represent.PEGInfo;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMOpAmbassador;
import peggy.represent.llvm.LLVMOperator;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.LLVMReturn;
import util.Tag;
import util.graph.CRecursiveExpressionGraph.Vertex;
import eqsat.BasicOp;
import eqsat.FlowValue;

/**
 * This class annotates the nodes of a PEG with their PEGTypes.
 */
public class LLVMPEGTypeAnnotater 
implements PEGTypeAnnotater<LLVMType,LLVMLabel,LLVMParameter,LLVMReturn> {
	public static final PEGType<LLVMType> SIGMA = PEGType.<LLVMType>makeDomain(LLVMType.SIGMA);
	public static final PEGType<LLVMType> TYPE = PEGType.<LLVMType>makeDomain(LLVMType.TYPE);
	public static final PEGType<LLVMType> NUMERAL = PEGType.<LLVMType>makeDomain(LLVMType.NUMERAL);
	public static final PEGType<LLVMType> PARAMATTR = PEGType.<LLVMType>makeDomain(LLVMType.PARAMATTR);
	public static final PEGType<LLVMType> PARAMATTRMAP = PEGType.<LLVMType>makeDomain(LLVMType.PARAMATTRMAP);
	public static final PEGType<LLVMType> EXCEPTION = PEGType.<LLVMType>makeDomain(LLVMType.EXCEPTION);
	public static final PEGType<LLVMType> BOOLEAN = 
		PEGType.<LLVMType>makeDomain(LLVMType.makeSimple(Type.BOOLEAN_TYPE));
	
	protected final LLVMOpAmbassador ambassador;
	
	public LLVMPEGTypeAnnotater(LLVMOpAmbassador _ambassador) {
		this.ambassador = _ambassador;
	}
	
	public void computeTypes(
			LLVMType returnType,
			PEGInfo<LLVMLabel,LLVMParameter,LLVMReturn> peg, 
			Tag<PEGType<LLVMType>> typetag) {
		// worklist algorithm
		LinkedList<Vertex<FlowValue<LLVMParameter,LLVMLabel>>> worklist = 
			new LinkedList<Vertex<FlowValue<LLVMParameter,LLVMLabel>>>();
		worklist.addAll(peg.getGraph().getVertices());
		
		while (!worklist.isEmpty()) {
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> vertex = worklist.removeFirst();
			if (vertex.hasTag(typetag))
				continue;
			tryAssignType(vertex, typetag, returnType);
			if (!vertex.hasTag(typetag)) {
				worklist.addLast(vertex);
			}
		}
	}
	
	protected void assignAndAssert(
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> vertex,
			PEGType<LLVMType> type,
			Tag<PEGType<LLVMType>> typetag) {
		if (vertex.hasTag(typetag)) {
			if (!vertex.getTag(typetag).equalsPEGType(type))
				throw new RuntimeException("Type mismatch: " + type + " != " + vertex.getTag(typetag));
		} else {
			vertex.setTag(typetag, type);
		}
	}
	
	protected void tryAssignType(
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> vertex,
			Tag<PEGType<LLVMType>> typetag,
			LLVMType returnType) {
		// giant switch on type of node
		FlowValue<LLVMParameter,LLVMLabel> flow = vertex.getLabel();
		if (flow.isAnd() || flow.isOr() || flow.isNegate() ||
			flow.isShortCircuitAnd() || flow.isShortCircuitOr()) {
			// boolean, with children boolean
			assignAndAssert(vertex, BOOLEAN, typetag);
			for (int i = 0; i < vertex.getChildCount(); i++) {
				assignAndAssert(vertex.getChild(i), BOOLEAN, typetag);
			}
		} else if (flow.isDomain()) {
			// big switch
			tryAssignDomainType(vertex, typetag, returnType);
		} else if (flow.isEquals()) {
			// boolean
			assignAndAssert(vertex, BOOLEAN, typetag);
		} else if (flow.isEval()) {
			// same as child(0),
			// and child(1) is iteration value
			assignAndAssert(vertex.getChild(1), PEGType.<LLVMType>makeIterationValue(), typetag);
			if (vertex.getChild(0).hasTag(typetag)) {
				PEGType<LLVMType> type = vertex.getChild(0).getTag(typetag);
				assignAndAssert(vertex, type, typetag);
			} 			
		} else if (flow.isFalse() || flow.isTrue()) {
			// boolean
			assignAndAssert(vertex, BOOLEAN, typetag);
		} else if (flow.isParameter()) {
			LLVMParameter param = flow.getParameter();
			if (param.isSigma()) {
				assignAndAssert(vertex, SIGMA, typetag);
			} else {
				// argument
				PEGType<LLVMType> type = simple(param.getArgumentSelf().getType());
				assignAndAssert(vertex, type, typetag);
			}
		} else if (flow.isPass() || flow.isSuccessor() || flow.isZero()) {
			// iteration value
			assignAndAssert(vertex, PEGType.<LLVMType>makeIterationValue(), typetag);
			if (flow.isSuccessor())
				assignAndAssert(vertex.getChild(0), PEGType.<LLVMType>makeIterationValue(), typetag);
			if (flow.isPass())
				assignAndAssert(vertex.getChild(0), BOOLEAN, typetag);
		} else if (flow.isPhi()) {
			// same as child(1) or child(2),
			// and child(0) is boolean
			assignAndAssert(vertex.getChild(0), BOOLEAN, typetag);
			if (vertex.getChild(1).hasTag(typetag)) {
				PEGType<LLVMType> type = vertex.getChild(1).getTag(typetag);
				assignAndAssert(vertex.getChild(2), type, typetag);
				assignAndAssert(vertex, type, typetag);
			} else if (vertex.getChild(2).hasTag(typetag)) {
				PEGType<LLVMType> type = vertex.getChild(2).getTag(typetag);
				assignAndAssert(vertex.getChild(1), type, typetag);
				assignAndAssert(vertex, type, typetag);
			}
		} else if (flow.isShift()) {
			// same as child(0)
			if (vertex.getChild(0).hasTag(typetag)) {
				PEGType<LLVMType> type = vertex.getChild(0).getTag(typetag);
				assignAndAssert(vertex, type, typetag);
			} 			
		} else if (flow.isTheta()) {
			// same as child(0) or child(1)
			if (vertex.getChild(0).hasTag(typetag)) {
				PEGType<LLVMType> type = vertex.getChild(0).getTag(typetag);
				assignAndAssert(vertex, type, typetag);
				assignAndAssert(vertex.getChild(1), type, typetag);
			} else if (vertex.getChild(1).hasTag(typetag)) {
				PEGType<LLVMType> type = vertex.getChild(1).getTag(typetag);
				assignAndAssert(vertex, type, typetag);
				assignAndAssert(vertex.getChild(0), type, typetag);
			}
		} else {
			throw new RuntimeException("Mike forgot to handle: " + flow);
		}
	}
	
	protected void tryAssignDomainType(
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> vertex,
			Tag<PEGType<LLVMType>> typetag,
			LLVMType returnType) {
		LLVMLabel label = vertex.getLabel().getDomain();
		if (label.isAlias()) {
			assignAndAssert(
					vertex,
					simple(new PointerType(label.getAliasSelf().getType())),
					typetag);
		} else if (label.isBasicOp()) {
			BasicOp op = label.getBasicOpSelf().getOperator();
			switch (op) {
			case True:
			case False:
				assignAndAssert(vertex, BOOLEAN, typetag);
				break;
			case Negate:
			case And:
			case Or:
				assignAndAssert(vertex, BOOLEAN, typetag);
				for (int i = 0; i < vertex.getChildCount(); i++) {
					assignAndAssert(vertex.getChild(i), BOOLEAN, typetag);
				}
				break;
			case Equals:
				assignAndAssert(vertex, BOOLEAN, typetag);
				break;
			default:
				throw new RuntimeException("Mike forgot case: " + op);
			}
		} else if (label.isBinop()) {
			// ops same as result
			if (vertex.getChild(0).hasTag(typetag)) {
				PEGType<LLVMType> type = vertex.getChild(0).getTag(typetag);
				assignAndAssert(vertex.getChild(1), type, typetag);
				assignAndAssert(vertex, type, typetag);
			} else if (vertex.getChild(1).hasTag(typetag)) {
				PEGType<LLVMType> type = vertex.getChild(1).getTag(typetag);
				assignAndAssert(vertex.getChild(0), type, typetag);
				assignAndAssert(vertex, type, typetag);
			}
		} else if (label.isCast()) {
			if (!(vertex.getChild(0).getLabel().isDomain() &&
				  vertex.getChild(0).getLabel().getDomain().isType()))
				throw new RuntimeException("Cast expects type as first child");
			Type casttype = vertex.getChild(0).getLabel().getDomain().getTypeSelf().getType();
			
			assignAndAssert(vertex, simple(casttype), typetag);
			assignAndAssert(vertex.getChild(0), TYPE, typetag);
		} else if (label.isCmp()) {
			assignAndAssert(vertex, BOOLEAN, typetag);
			if (vertex.getChild(0).hasTag(typetag)) {
				assignAndAssert(vertex.getChild(1), vertex.getChild(0).getTag(typetag), typetag);
			} else if (vertex.getChild(1).hasTag(typetag)) {
				assignAndAssert(vertex.getChild(0), vertex.getChild(1).getTag(typetag), typetag);
			}
		} else if (label.isConstantValue()) {
			assignAndAssert(
					vertex,
					simple(label.getConstantValueSelf().getValue().getType()),
					typetag);
		} else if (label.isFunction()) {
			assignAndAssert(
					vertex,
					simple(new PointerType(label.getFunctionSelf().getType())),
					typetag);
		} else if (label.isGlobal()) {
			assignAndAssert(
					vertex,
					simple(new PointerType(label.getGlobalSelf().getType())),
					typetag);
		} else if (label.isInlineASM()) {
			assignAndAssert(
					vertex,
					simple(label.getInlineASMSelf().getASM().getType()),
					typetag);
		} else if (label.isNumeral()) {
			assignAndAssert(vertex, NUMERAL, typetag);
		} else if (label.isParamAttr()) {
			assignAndAssert(vertex, PARAMATTR, typetag);
		} else if (label.isSimple()) {
			tryAssignSimpleDomainType(vertex, typetag, returnType);
		} else if (label.isType()) {
			assignAndAssert(vertex, TYPE, typetag);
		} else {
			throw new RuntimeException("Mike forgot to handle: " + label);
		}
	}
	

	
	
	
	protected void tryAssignSimpleDomainType(
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> vertex,
			Tag<PEGType<LLVMType>> typetag,
			LLVMType returnType) {
		LLVMOperator op = vertex.getLabel().getDomain().getSimpleSelf().getOperator();
		switch (op) {
		case INJR:
			// disjoint union (e|v)
			if (vertex.getChild(0).hasTag(typetag)) {
				PEGType<LLVMType> mytype = PEGType.<LLVMType>makeDisjointUnion(
						EXCEPTION,
						vertex.getChild(0).getTag(typetag));
				assignAndAssert(vertex, mytype, typetag);
			}
			assignAndAssert(
					vertex,
					PEGType.<LLVMType>makeDisjointUnion(
						EXCEPTION,
						PEGType.<LLVMType>makeDomain(returnType)),
					typetag);
			break;
			
		case INJL:
			// disjoint union (e|v)
			assignAndAssert(
					vertex,
					PEGType.<LLVMType>makeDisjointUnion(
						EXCEPTION,
						PEGType.<LLVMType>makeDomain(returnType)),
					typetag);
			assignAndAssert(
					vertex.getChild(0),
					EXCEPTION,
					typetag);
			break;
			
		case CALL:
		case TAILCALL:
		case INVOKE:
			// (sigma,func,n,params) -> (sigma,e|v)
			assignAndAssert(vertex.getChild(0), SIGMA, typetag);
			assignAndAssert(vertex.getChild(2), NUMERAL, typetag);
//			assignAndAssert(vertex.getChild(4), PARAMATTRMAP, typetag);
			
			if (vertex.getChild(1).hasTag(typetag)) {
				FunctionType type = 
					vertex.getChild(1).getTag(typetag).getDomain().getSimpleType().getCompositeSelf().getPointerSelf().getPointeeType().getFunctionSelf();
				List<PEGType<LLVMType>> paramTypes = 
					new ArrayList<PEGType<LLVMType>> (type.getNumParams());
				for (int i = 0; i < type.getNumParams(); i++) {
					paramTypes.add(simple(type.getParamType(i)));
				}
				assignAndAssert(
						vertex.getChild(3),
						PEGType.<LLVMType>makeTupleType(paramTypes),
						typetag);
				assignAndAssert(
						vertex, 
						PEGType.<LLVMType>makeTupleType(
								SIGMA, 
								PEGType.<LLVMType>makeDisjointUnion(
										EXCEPTION,
										simple(type.getReturnType()))),
						typetag);
			}
			break;

		case RHO_VALUE:
			// (sigma,e|v) -> v
			// (sigma,v) -> v
			if (vertex.getChild(0).hasTag(typetag)) {
				PEGType<LLVMType> childtype = vertex.getChild(0).getTag(typetag);
				if (!(childtype.isTuple() && childtype.getNumTupleElements() == 2))
					throw new RuntimeException("Expecting tuple for RHO_VALUE, " + vertex.getChild(0).getLabel() + ":" + childtype);
				PEGType<LLVMType> right = childtype.getTupleElement(1);
				if (right.isDisjointUnion())
					right = right.getDisjointUnionRight();
				assignAndAssert(vertex, right, typetag);
			}
			break;
			
		case RHO_SIGMA:
			assignAndAssert(vertex, SIGMA, typetag);
			break;
			
		case RHO_EXCEPTION:
			assignAndAssert(vertex, EXCEPTION, typetag);
			break;
			
		case SHUFFLEVECTOR:
			if (vertex.getChild(0).hasTag(typetag)) {
				PEGType<LLVMType> type = vertex.getChild(0).getTag(typetag);
				assignAndAssert(vertex, type, typetag);
				assignAndAssert(vertex.getChild(1), type, typetag);
				UnsignedLong length = type.getDomain().getSimpleType().getCompositeSelf().getVectorSelf().getNumElements();
				PEGType<LLVMType> shuffleType = 
					simple(new VectorType(Type.getIntegerType(32), length));
				assignAndAssert(vertex.getChild(2), shuffleType, typetag);
			} else if (vertex.getChild(1).hasTag(typetag)) {
				PEGType<LLVMType> type = vertex.getChild(1).getTag(typetag);
				assignAndAssert(vertex, type, typetag);
				assignAndAssert(vertex.getChild(0), type, typetag);
				UnsignedLong length = type.getDomain().getSimpleType().getCompositeSelf().getVectorSelf().getNumElements();
				PEGType<LLVMType> shuffleType = 
					simple(new VectorType(Type.getIntegerType(32), length));
				assignAndAssert(vertex.getChild(2), shuffleType, typetag);
			}
			break;
			
		case INSERTELEMENT:
			assignAndAssert(
					vertex.getChild(2),
					simple(Type.getIntegerType(32)),
					typetag);
			if (vertex.getChild(0).hasTag(typetag)) {
				PEGType<LLVMType> vectype = vertex.getChild(0).getTag(typetag);
				PEGType<LLVMType> elttype = 
					simple(vectype.getDomain().getSimpleType().getCompositeSelf().getVectorSelf().getElementType());
				assignAndAssert(vertex, vectype, typetag);
				assignAndAssert(vertex.getChild(1), elttype, typetag);
			}
			break;
			
		case GETELEMENTPTR: {
			assignAndAssert(vertex.getChild(1), TYPE, typetag);
			if (!(vertex.getChild(1).getLabel().isDomain() &&
				  vertex.getChild(1).getLabel().getDomain().isType()))
				throw new RuntimeException("GEP expects TYPE child");
			Type inputType = vertex.getChild(1).getLabel().getDomain().getTypeSelf().getType();
			assignAndAssert(
					vertex.getChild(0),
					simple(inputType),
					typetag);
			if (vertex.getChild(2).hasTag(typetag)) {
				// verify indexes type
				PEGType<LLVMType> indexesType = vertex.getChild(2).getTag(typetag);
				if (!indexesType.isTuple())
					throw new RuntimeException("Expecting tuple type for INDEXES");
				for (int i = 0; i < indexesType.getNumTupleElements(); i++) {
					PEGType<LLVMType> subtype = indexesType.getTupleElement(i);
					if (!(subtype.isDomain() && 
						  subtype.getDomain().isSimple() &&
						  subtype.getDomain().getSimpleType().isInteger()))
						throw new RuntimeException("Expecting integer type");
					IntegerType inttype = subtype.getDomain().getSimpleType().getIntegerSelf();
					if (!(inttype.getWidth() == 32 || inttype.getWidth() == 64))
						throw new RuntimeException("Expecting i32 or i64");
				}
			}
			
			Type geptype = computeGEPType(inputType, vertex.getChild(2));
			assignAndAssert(vertex, simple(geptype), typetag);
			break;
		}
			
		case INDEXES: {
			List<PEGType<LLVMType>> childtypes = 
				new ArrayList<PEGType<LLVMType>>(vertex.getChildCount());
			for (int i = 0; i < vertex.getChildCount(); i++) {
				if (!vertex.getChild(i).hasTag(typetag))
					return;
				childtypes.add(vertex.getChild(i).getTag(typetag));
			}
			assignAndAssert(
					vertex,
					PEGType.<LLVMType>makeTupleType(childtypes),
					typetag);
			break;
		}
			
		case SELECT:
			assignAndAssert(vertex.getChild(0), BOOLEAN, typetag);
			if (vertex.getChild(1).hasTag(typetag)) {
				PEGType<LLVMType> type = vertex.getChild(1).getTag(typetag);
				assignAndAssert(vertex, type, typetag);
				assignAndAssert(vertex.getChild(2), type, typetag);
			} else if (vertex.getChild(2).hasTag(typetag)) {
				PEGType<LLVMType> type = vertex.getChild(2).getTag(typetag);
				assignAndAssert(vertex, type, typetag);
				assignAndAssert(vertex.getChild(1), type, typetag);
			}
			break;
			
		case EXTRACTELEMENT:
			assignAndAssert(
					vertex.getChild(1), 
					simple(Type.getIntegerType(32)),
					typetag);
			if (vertex.getChild(0).hasTag(typetag)) {
				PEGType<LLVMType> childtype = vertex.getChild(0).getTag(typetag);
				if (!(childtype.isDomain() && childtype.getDomain().isSimple()))
					throw new RuntimeException("Expecting simple vector type");
				Type simpleType = childtype.getDomain().getSimpleType();
				if (!(simpleType.isComposite() && simpleType.getCompositeSelf().isVector()))
					throw new RuntimeException("Expecting vector type");
				
				assignAndAssert(
						vertex,
						simple(simpleType.getCompositeSelf().getVectorSelf().getElementType()),
						typetag);
			}
			break;
			
		case GETRESULT:
			assignAndAssert(
					vertex.getChild(1),
					NUMERAL,
					typetag);
			if (vertex.getChild(0).hasTag(typetag)) {
				PEGType<LLVMType> childtype = vertex.getChild(0).getTag(typetag);
				if (!(childtype.isDomain() && childtype.getDomain().isSimple()))
					throw new RuntimeException("Expecting simple domain type");
				Type simpleType = childtype.getDomain().getSimpleType();
				if (!(simpleType.isComposite() && simpleType.getCompositeSelf().isStructure()))
					throw new RuntimeException("Expecting structure type");
				
				if (!(vertex.getChild(1).getLabel().isDomain() &&
					  vertex.getChild(1).getLabel().getDomain().isNumeral()))
					throw new RuntimeException("Expecting numeral");
				int number = vertex.getChild(1).getLabel().getDomain().getNumeralSelf().getValue();
				Type fieldType = simpleType.getCompositeSelf().getElementType(number);
				
				assignAndAssert(vertex, simple(fieldType), typetag);
			}
			break;
			
		case MALLOC:
		case ALLOCA: {
			assignAndAssert(vertex.getChild(0), SIGMA, typetag);
			assignAndAssert(vertex.getChild(1), TYPE, typetag);
			assignAndAssert(vertex.getChild(3), NUMERAL, typetag);
			assignAndAssert(
					vertex.getChild(2),
					simple(Type.getIntegerType(32)),
					typetag);
			if (!(vertex.getChild(1).getLabel().isDomain() &&
				  vertex.getChild(1).getLabel().getDomain().isType()))
				throw new RuntimeException("Expecting type node");
			Type elttype = vertex.getChild(1).getLabel().getDomain().getTypeSelf().getType();
			
			assignAndAssert(
					vertex, 
					PEGType.<LLVMType>makeTupleType(
							SIGMA,
							simple(new PointerType(elttype))), 
					typetag);
			break;
		}
			
		case FREE:
			assignAndAssert(vertex.getChild(0), SIGMA, typetag);
			assignAndAssert(vertex, SIGMA, typetag);
			break;
			
		case VOLATILE_LOAD:
		case LOAD:
			assignAndAssert(vertex.getChild(0), SIGMA, typetag);
			assignAndAssert(vertex.getChild(2), NUMERAL, typetag);
			
			if (vertex.getChild(1).hasTag(typetag)) {
				PEGType<LLVMType> ptrtype = vertex.getChild(1).getTag(typetag);
				if (!(ptrtype.isDomain() && ptrtype.getDomain().isSimple()))
					throw new RuntimeException("Expecting simple domain type");
				Type simpleType = ptrtype.getDomain().getSimpleType();
				if (!(simpleType.isComposite() && simpleType.getCompositeSelf().isPointer()))
					throw new RuntimeException("Expecting pointer type: " + vertex.getChild(1).getLabel() + ":" + simpleType);
				
				// (sigma,v)
				PEGType<LLVMType> resulttype = 
					PEGType.<LLVMType>makeTupleType(
							Arrays.asList(
									SIGMA,
									simple(simpleType.getCompositeSelf().getPointerSelf().getPointeeType())));
				assignAndAssert(vertex, resulttype, typetag);
			}
			break;
			
		case VOLATILE_STORE:
		case STORE:
			assignAndAssert(vertex.getChild(0), SIGMA, typetag);
			assignAndAssert(vertex.getChild(3), NUMERAL, typetag);
			if (vertex.getChild(1).hasTag(typetag)) {
				// ptr
				PEGType<LLVMType> childtype = vertex.getChild(1).getTag(typetag);
				if (!(childtype.isDomain() && childtype.getDomain().isSimple() &&
					  childtype.getDomain().getSimpleType().isComposite() &&
					  childtype.getDomain().getSimpleType().getCompositeSelf().isPointer()))
					throw new RuntimeException("Expecting pointer type");
				Type ptrtype = childtype.getDomain().getSimpleType().getCompositeSelf().getPointerSelf();
				assignAndAssert(
						vertex.getChild(2), 
						simple(ptrtype.getCompositeSelf().getPointerSelf().getPointeeType()), 
						typetag);
			}
			assignAndAssert(vertex, SIGMA, typetag);
			break;
			
		case PARAMS: {
			List<PEGType<LLVMType>> children = 
				new ArrayList<PEGType<LLVMType>>(vertex.getChildCount());
			for (int i = 0; i < vertex.getChildCount(); i++) {
				if (!vertex.getChild(i).hasTag(typetag))
					return;
				children.add(vertex.getChild(i).getTag(typetag));
			}
			assignAndAssert(vertex, PEGType.<LLVMType>makeTupleType(children), typetag);
			break;
		}
			
//		case PARAMATTRMAP:
//			assignAndAssert(vertex, PARAMATTRMAP, typetag);
//			break;
			
		case UNWIND:
			assignAndAssert(vertex.getChild(0), SIGMA, typetag);
			assignAndAssert(
					vertex,
					PEGType.<LLVMType>makeTupleType(
							Arrays.asList(
									SIGMA,
									PEGType.<LLVMType>makeDisjointUnion(
											EXCEPTION,
											PEGType.<LLVMType>makeTupleType()))),
					typetag);
			break;
			
		case VOID:
			assignAndAssert(vertex, simple(Type.VOID_TYPE), typetag);
			break;
			
		case RETURNSTRUCTURE: {
			List<Type> fieldTypes = new ArrayList<Type>(vertex.getChildCount());
			for (int i = 0; i < vertex.getChildCount(); i++) {
				if (!vertex.getChild(i).hasTag(typetag))
					return;
				PEGType<LLVMType> childtype = vertex.getChild(i).getTag(typetag);
				if (!(childtype.isDomain() && childtype.getDomain().isSimple()))
					throw new RuntimeException("Expecting simple type");
				fieldTypes.add(childtype.getDomain().getSimpleType());
			}
			assignAndAssert(
					vertex,
					simple(new StructureType(false, fieldTypes)),
					typetag);
			break;
		}
			
		case VAARG: {
			assignAndAssert(vertex.getChild(0), SIGMA, typetag);
			assignAndAssert(vertex.getChild(2), TYPE, typetag);
			
			if (!(vertex.getChild(2).getLabel().isDomain() &&
				  vertex.getChild(2).getLabel().getDomain().isType()))
				throw new RuntimeException("Expecting type node");
			Type resulttype = vertex.getChild(2).getLabel().getDomain().getTypeSelf().getType();
			assignAndAssert(
					vertex,
					PEGType.<LLVMType>makeTupleType(
							Arrays.asList(SIGMA, simple(resulttype))),
					typetag);
			break;
		}
			
		case IS_EXCEPTION:
			assignAndAssert(vertex, BOOLEAN, typetag);
			break;
			
		case NONSTACK:
			assignAndAssert(vertex, SIGMA, typetag);
			assignAndAssert(vertex.getChild(0), SIGMA, typetag);
			break;
			
		default:
			throw new RuntimeException("Forgot to handle: " + op); 
		}
	}
	
	
	protected Type computeGEPType(
			Type inputType, 
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> indexes) {
		if (!(inputType.isComposite() && inputType.getCompositeSelf().isPointer()))
			throw new RuntimeException("Expecting pointer type");
		int addrspace = inputType.getCompositeSelf().getPointerSelf().getAddressSpace();
		Type result = computeGEPType_helper(inputType, indexes.getChildren().iterator());
		return new PointerType(result, addrspace);
	}
	protected Type computeGEPType_helper(
			Type base,
			Iterator<? extends Vertex<FlowValue<LLVMParameter,LLVMLabel>>> indexes) {
		if (indexes.hasNext()) {
			Vertex<FlowValue<LLVMParameter,LLVMLabel>> index = indexes.next();
			if (!base.isComposite())
				throw new RuntimeException("Too many indexes");
			CompositeType ctype = base.getCompositeSelf();
			if (ctype.isArray() || ctype.isPointer() || ctype.isVector()) {
				// any index
				return computeGEPType_helper(ctype.getElementType(0), indexes);
			} else if (ctype.isStructure()) {
				// i32 index
				if (!(index.getLabel().isDomain() &&
					  index.getLabel().getDomain().isConstantValue() &&
					  index.getLabel().getDomain().getConstantValueSelf().getValue().isInteger()))
					throw new RuntimeException("Expecting constant integer");
				IntegerValue intvalue = index.getLabel().getDomain().getConstantValueSelf().getValue().getIntegerSelf();
				if (intvalue.getWidth() != 32)
					throw new RuntimeException("Expecting i32 constant integer");
				return computeGEPType_helper(ctype.getElementType(intvalue.getIntBits()), indexes);
			} else {
				throw new RuntimeException("Mike forgot: " + ctype);
			}
		} else {
			return base;
		}
	}
	
	
	protected PEGType<LLVMType> simple(Type type) {
		return PEGType.<LLVMType>makeDomain(LLVMType.makeSimple(type));
	}
}
