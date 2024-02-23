package peggy.analysis.llvm;

import llvm.bitcode.DataLayout;
import llvm.instructions.Binop;
import llvm.instructions.Cast;
import llvm.types.Type;
import peggy.analysis.Analysis;
import peggy.represent.llvm.BinopLLVMLabel;
import peggy.represent.llvm.CastLLVMLabel;
import peggy.represent.llvm.GEPForcingPolicy;
import peggy.represent.llvm.LLVMLabel;
import peggy.represent.llvm.LLVMParameter;
import peggy.represent.llvm.TypeLLVMLabel;
import eqsat.meminfer.network.Network;
import eqsat.meminfer.peggy.engine.CPeggyAxiomEngine;

/**
 * This analysis attempts to remove all GEP operators and replace them 
 * with explicit pointer arithmetic and casting.
 */
public abstract class GEPRemovalAnalysis extends Analysis<LLVMLabel,LLVMParameter> {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("GEPRemovalAnalysis: " + message);
	}

	private static final CastLLVMLabel PTRTOINT = new CastLLVMLabel(Cast.PtrToInt);
	private static final CastLLVMLabel INTTOPTR = new CastLLVMLabel(Cast.IntToPtr);
	private static final TypeLLVMLabel INT64 = new TypeLLVMLabel(Type.getIntegerType(64));
	private static final TypeLLVMLabel INT32 = new TypeLLVMLabel(Type.getIntegerType(32));
	private static final BinopLLVMLabel ADD = new BinopLLVMLabel(Binop.Add);
	private static final BinopLLVMLabel MUL = new BinopLLVMLabel(Binop.Mul);
	
	protected final GEPForcingPolicy forcingPolicy;
	
	public GEPRemovalAnalysis(
			GEPForcingPolicy policy,
			Network _network, 
			CPeggyAxiomEngine<LLVMLabel,LLVMParameter> _engine) {
		super(_network, _engine);
		if (policy==null || policy.equals(GEPForcingPolicy.NONE))
			throw new IllegalArgumentException("GEP remover must use a forcing policy");
		this.forcingPolicy = policy;
	}
	
	public void addAll(final DataLayout layout) {
//		final String name = "gep(BASE,BASETYPE,indexes(*))";
//		
//		PeggyAxiomizer<LLVMLabel,Integer> axiomizer = 
//			new PeggyAxiomizer<LLVMLabel,Integer>(name, getNetwork(), getAmbassador());
//
//		PeggyVertex<LLVMLabel,Integer> BASE = axiomizer.getVariable(1);
//		PeggyVertex<LLVMLabel,Integer> basetype = axiomizer.get(null); // will be a term but label ignored
//		PeggyVertex<LLVMLabel,Integer> indexes = axiomizer.getAnyArity(SimpleLLVMLabel.get(LLVMOperator.INDEXES));
//		
//		PeggyVertex<LLVMLabel,Integer> gep = 
//			axiomizer.get(SimpleLLVMLabel.get(LLVMOperator.GETELEMENTPTR), 
//						  BASE, basetype, indexes);
//		axiomizer.mustExist(gep);
//		
//		PEGNode<LLVMLabel> trigger = axiomizer.getTrigger();
//		
//		Event<? extends Structure<CPEGTerm<LLVMLabel,LLVMParameter>>> triggerEvent = 
//			getEngine().getEGraph().processPEGNode(trigger);
//
//		Structurizer<PeggyVertex<LLVMLabel,Integer>> structurizer = 
//			axiomizer.getStructurizer();
//		
//		final Function<? super Structure<CPEGTerm<LLVMLabel,LLVMParameter>>, ? extends CPEGTerm<LLVMLabel,LLVMParameter>> basetypeFunction = 
//			getEngine().getEGraph().processTermValueNode(structurizer.getTermValue(basetype));
//		final Function<? super Structure<CPEGTerm<LLVMLabel,LLVMParameter>>, ? extends CPEGTerm<LLVMLabel,LLVMParameter>> indexesFunction = 
//			getEngine().getEGraph().processTermValueNode(structurizer.getTermValue(indexes));
//		final Function<? super Structure<CPEGTerm<LLVMLabel,LLVMParameter>>, ? extends CPEGTerm<LLVMLabel,LLVMParameter>> gepFunction = 
//			getEngine().getEGraph().processTermValueNode(structurizer.getTermValue(gep));
//		
//		final Function<? super Structure<CPEGTerm<LLVMLabel,LLVMParameter>>,? extends Representative<CPEGValue<LLVMLabel,LLVMParameter>>> baseInputFunction =
//			getEngine().getEGraph().processValueNode(structurizer.getValue(BASE));
//
//		ChainEvent<Structure<CPEGTerm<LLVMLabel,LLVMParameter>>,Void> listener = 
//			new AbstractChainEvent<Structure<CPEGTerm<LLVMLabel,LLVMParameter>>,Void>() {
//			public boolean notify(Structure<CPEGTerm<LLVMLabel,LLVMParameter>> structure) {
//				debug("Called notify");
//
//				if (!this.canUse(structure))
//					return true;
//
//				debug("notify: canUse said yes!");
//
//				// good to go!
//				CPEGTerm<LLVMLabel,LLVMParameter> basetypeTerm = basetypeFunction.get(structure);
//				CPEGTerm<LLVMLabel,LLVMParameter> indexesTerm = indexesFunction.get(structure);
//				CPEGTerm<LLVMLabel,LLVMParameter> gepTerm = gepFunction.get(structure);
//				Representative<CPEGValue<LLVMLabel,LLVMParameter>> baseInputValue = 
//					baseInputFunction.get(structure);
//				if (indexesTerm.getArity() == 0) {
//					debug("notify: indexes has 0 children");
//					return true;
//				}
//
//				Type baseType = basetypeTerm.getOp().getDomain().getTypeSelf().getType();
//				if (!checkIndexes(baseType, 0, indexesTerm)) {
//					debug("notify: !checkIndexes(" + baseType + ", 0, ...)"); 
//					return true;
//				}
//
//				addEquivalence(gepTerm, baseInputValue, basetypeTerm, indexesTerm, layout);
//				
//				trigger(null);
//				return true;
//			}
//
//			public boolean canUse(Structure<CPEGTerm<LLVMLabel,LLVMParameter>> structure) {
//				// check basetype term
//				CPEGTerm<LLVMLabel,LLVMParameter> basetypeTerm = basetypeFunction.get(structure);
//				if (basetypeTerm != null) {
//					debug("canuse: has a basetype term");
//					FlowValue<LLVMParameter,LLVMLabel> basetypeFlow = basetypeTerm.getOp();
//					if (!basetypeFlow.isDomain()) {
//						debug("canuse: !basetypeFlow.isDomain(): " + basetypeFlow);
//						return false;
//					}
//					if (!(basetypeFlow.getDomain().isType())) {
//						debug("canuse: !(basetypeFlow.getDomain().isType()): " + basetypeFlow);
//						return false;
//					}
//				}
//
//				debug("canuse returned true!");
//				return true;
//			}
//		};
//		listener.addListener(new PrintListener(name));
//		triggerEvent.addListener(listener);
		
		// TODO
		// TODO
	}
	

//	/**
//	 * Checks that the given indexes are compatible with the given GEP type.
//	 * Mostly just checks that the structure type indexes are integer constants.
//	 * Returns true if the indexes are compatible.
//	 */
//	private boolean checkIndexes(
//			Type type,
//			int index,
//			CPEGTerm<LLVMLabel,LLVMParameter> indexesTerm) {
//		if (index >= indexesTerm.getArity())
//			return true;
//		if (!type.isComposite())
//			return false;
//		
//		CompositeType ctype = type.getCompositeSelf();
//		if (ctype.isPointer()) {
//			if (index != 0)
//				return false;
//			return checkIndexes(ctype.getPointerSelf().getPointeeType(), index+1, indexesTerm);
//		} else if (ctype.isArray()) {
//			return checkIndexes(ctype.getArraySelf().getElementType(), index+1, indexesTerm);
//		} else if (ctype.isStructure()) {
//			StructureType struct = ctype.getStructureSelf();
//			
//			Representative<CPEGValue<LLVMLabel,LLVMParameter>> child = 
//				indexesTerm.getChild(index);
//			if (child == null)
//				return false;
//			CPEGValue<LLVMLabel,LLVMParameter> value = child.getValue();
//			for (CPEGTerm<LLVMLabel,LLVMParameter> term : value.getTerms()) {
//				Integer intBits = getStructureConstantOffset(term);
//				if (intBits == null || !struct.isElementIndexValid(intBits.intValue()))
//					continue;
//				if (checkIndexes(struct.getElementType(intBits.intValue()), index+1, indexesTerm))
//					return true;
//			}
//			
//			return false;
//		} else {
//			return false;
//		}
//	}
//
//	
//	/**
//	 * If this term contains a ConstantValueLLVMLabel that holds an
//	 * IntegerValue of width 32, then this will
//	 * return the int bits of that constant. 
//	 * Otherwise, null is returned.
//	 */
//	private Integer getStructureConstantOffset(
//			CPEGTerm<LLVMLabel,LLVMParameter> term) {
//		if (term == null) return null;
//		if (!term.getOp().isDomain()) return null;
//		LLVMLabel label = term.getOp().getDomain();
//		if (!label.isConstantValue()) return null;
//		ConstantValueLLVMLabel constant = label.getConstantValueSelf();
//		Value constantValue = constant.getValue();
//		if (!constantValue.isInteger()) return null;
//		IntegerValue intValue = constantValue.getIntegerSelf();
//		if (intValue.getWidth() != 32)
//			return null;
//		int intBits = intValue.getIntBits();
//		return new Integer(intBits);
//	}
//	
//	
//	private class Helper {
//		private final FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>,CPEGValue<LLVMLabel,LLVMParameter>> futureGraph;
//		private final DataLayout layout;
//		
//		public Helper(
//				FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>,CPEGValue<LLVMLabel,LLVMParameter>> _futureGraph,
//				DataLayout _layout) {
//			this.futureGraph = _futureGraph;
//			this.layout = _layout;
//		}
//		public FutureExpression<FlowValue<LLVMParameter,LLVMLabel>,CPEGValue<LLVMLabel,LLVMParameter>> getExpression(
//				LLVMLabel label,
//				FutureExpressionGraph.Vertex<FlowValue<LLVMParameter,LLVMLabel>,CPEGValue<LLVMLabel,LLVMParameter>>... children) {
//			return this.futureGraph.getExpression(getDomain(label), children);
//		}
//		public FutureExpression<FlowValue<LLVMParameter,LLVMLabel>,CPEGValue<LLVMLabel,LLVMParameter>> getExpression(
//				FlowValue<LLVMParameter,LLVMLabel> flow,
//				FutureExpressionGraph.Vertex<FlowValue<LLVMParameter,LLVMLabel>,CPEGValue<LLVMLabel,LLVMParameter>>... children) {
//			return this.futureGraph.getExpression(flow, children);
//		}
//		public FutureExpressionGraph.Vertex<FlowValue<LLVMParameter,LLVMLabel>,CPEGValue<LLVMLabel,LLVMParameter>> getVertex(
//				Representative<CPEGValue<LLVMLabel,LLVMParameter>> rep) {
//			return this.futureGraph.getVertex(rep);
//		}
//		
//		public int getABITypeSize(Type type) {
//			return this.layout.getABITypeSize(type);
//		}
//		
//		public int getElementOffset(StructureType stype, int fieldIndex) {
//			DataLayout.StructLayout structlayout = this.layout.getStructLayout(stype);
//			return structlayout.getMemberOffset(fieldIndex);
//		}
//	}
//	
//	
//	private void addEquivalence(
//			CPEGTerm<LLVMLabel,LLVMParameter> gepTerm,
//			Representative<CPEGValue<LLVMLabel,LLVMParameter>> baseInputValue,
//			CPEGTerm<LLVMLabel,LLVMParameter> basetypeTerm, 
//			CPEGTerm<LLVMLabel,LLVMParameter> indexesTerm,
//			DataLayout layout) {
//
//		final FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>,CPEGValue<LLVMLabel,LLVMParameter>> futureGraph = 
//			new FutureExpressionGraph<FlowValue<LLVMParameter,LLVMLabel>,CPEGValue<LLVMLabel,LLVMParameter>>();
//
//		Helper helper = new Helper(futureGraph, layout);
//		
//		FutureExpression<FlowValue<LLVMParameter,LLVMLabel>,CPEGValue<LLVMLabel,LLVMParameter>> firstCast = 
//			helper.getExpression(
//					getDomain(PTRTOINT),
//					helper.getExpression((forcingPolicy.equals(GEPForcingPolicy.FORCE_32) ? INT32 : INT64)),
//					helper.getVertex(baseInputValue));
//		
//		Type baseType = basetypeTerm.getOp().getDomain().getTypeSelf().getType();
//		
//		// now add index offsets
//		Pair<PointerType,FutureExpression<FlowValue<LLVMParameter,LLVMLabel>,CPEGValue<LLVMLabel,LLVMParameter>>> result = 
//			addIndexOffsets(
//					helper,
//					firstCast,
//					baseType,
//					0,
//					indexesTerm);
//
//		PointerType resultType = result.getFirst();
//		
//		FutureExpression<FlowValue<LLVMParameter,LLVMLabel>,CPEGValue<LLVMLabel,LLVMParameter>> castBack =
//			helper.getExpression(
//					INTTOPTR,
//					helper.getExpression(new TypeLLVMLabel(resultType)),
//					result.getSecond());
//		
//		castBack.setValue(gepTerm);
//		
//		debug("added gep ptr arithmetic");
//		
//		getEngine().getEGraph().addExpressions(futureGraph);
//	}
//	
//	private Pair<PointerType,FutureExpression<FlowValue<LLVMParameter,LLVMLabel>,CPEGValue<LLVMLabel,LLVMParameter>>> 
//	addIndexOffsets(
//			Helper helper,
//			FutureExpression<FlowValue<LLVMParameter,LLVMLabel>,CPEGValue<LLVMLabel,LLVMParameter>> address,
//			Type type,
//			int index,
//			CPEGTerm<LLVMLabel,LLVMParameter> indexesTerm) {
//		if (index >= indexesTerm.getArity()) {
//			return new Pair<PointerType,FutureExpression<FlowValue<LLVMParameter,LLVMLabel>,CPEGValue<LLVMLabel,LLVMParameter>>>(
//					new PointerType(type),
//					address);
//		}
//		
//		if (!type.isComposite())
//			throw new RuntimeException("Not a composite type: " + type);
//		
//		CompositeType ctype = type.getCompositeSelf();
//		if (ctype.isArray() || ctype.isVector()) {
//			Type eltType = (ctype.isArray() ? 
//					ctype.getArraySelf().getElementType() : 
//						ctype.getVectorSelf().getElementType());
//			
//			int eltSize = helper.getABITypeSize(eltType);
//			
//			debug("elementSize for array = " + eltSize);
//			
//			Representative<CPEGValue<LLVMLabel,LLVMParameter>> indexRep = 
//				indexesTerm.getChild(index);
//
//			LLVMLabel eltSizeLabel = new ConstantValueLLVMLabel(IntegerValue.get(
//					(this.forcingPolicy.equals(GEPForcingPolicy.FORCE_32) ? 32 : 64), 
//					new long[]{eltSize}));
//			
//			FutureExpression<FlowValue<LLVMParameter,LLVMLabel>,CPEGValue<LLVMLabel,LLVMParameter>> newaddress = 
//				helper.getExpression(
//						ADD,
//						address, 
//						helper.getExpression(
//								MUL,
//								helper.getVertex(indexRep),
//								helper.getExpression(eltSizeLabel)));
//
//			return addIndexOffsets(
//					helper,
//					newaddress,
//					eltType,
//					index+1,
//					indexesTerm);
//		} 
//		else if (ctype.isPointer()) {
//			PointerType ptype = ctype.getPointerSelf();
//			if (index != 0)
//				throw new RuntimeException("Can't index into pointer unless it's first!");
//			int pointeeSize = helper.getABITypeSize(ptype.getPointeeType());
//
//			debug("pointeeSize for pointer = " + pointeeSize);
//			
//			Representative<CPEGValue<LLVMLabel,LLVMParameter>> indexRep = 
//				indexesTerm.getChild(index);
//			
//			LLVMLabel pointeeSizeLabel = new ConstantValueLLVMLabel(IntegerValue.get(
//					(this.forcingPolicy.equals(GEPForcingPolicy.FORCE_32) ? 32 : 64),
//					new long[]{pointeeSize}));
//			
//			FutureExpression<FlowValue<LLVMParameter,LLVMLabel>,CPEGValue<LLVMLabel,LLVMParameter>> newaddress =
//				helper.getExpression(
//						ADD,
//						address,
//						helper.getExpression(
//								MUL,
//								helper.getVertex(indexRep),
//								helper.getExpression(pointeeSizeLabel)));
//			
//			return addIndexOffsets(
//					helper,
//					newaddress,
//					ptype.getPointeeType(),
//					index+1,
//					indexesTerm);
//		} 
//		else if (ctype.isStructure()) {
//			StructureType stype = ctype.getStructureSelf();
//			
//			Representative<CPEGValue<LLVMLabel,LLVMParameter>> indexRep = 
//				indexesTerm.getChild(index);
//			CPEGValue<LLVMLabel,LLVMParameter> indexValue = indexRep.getValue();
//			Integer intBits = null;
//			for (CPEGTerm<LLVMLabel,LLVMParameter> term : indexValue.getTerms()) {
//				intBits = getStructureConstantOffset(term);
//				if (intBits != null)
//					break;
//			}
//			if (intBits == null)
//				throw new RuntimeException("No integer constant children!!");
//			
//			int offset = helper.getElementOffset(stype, intBits.intValue());
//			
//			debug("elementOffset for structure element " + intBits.intValue() + " is " + offset);
//			
//			LLVMLabel offsetLabel = new ConstantValueLLVMLabel(IntegerValue.get(
//					(this.forcingPolicy.equals(GEPForcingPolicy.FORCE_32) ? 32 : 64),
//					new long[]{offset}));
//			
//			FutureExpression<FlowValue<LLVMParameter,LLVMLabel>,CPEGValue<LLVMLabel,LLVMParameter>> newaddress =
//				helper.getExpression(
//						ADD,
//						address,
//						helper.getExpression(offsetLabel));
//			
//			return addIndexOffsets(
//					helper,
//					newaddress,
//					stype.getFieldType(intBits.intValue()),
//					index+1,
//					indexesTerm);
//		} else {
//			throw new RuntimeException("Can't be a vector type: " + ctype);
//		}
//	}
}
