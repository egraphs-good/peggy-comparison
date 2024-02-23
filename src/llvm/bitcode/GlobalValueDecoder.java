package llvm.bitcode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import llvm.instructions.*;
import llvm.types.*;
import llvm.values.*;

/**
 * This class reads and decodes the global values (functions/aliases/globals)
 * from an LLVM 2.3 bitcode file.
 */
public class GlobalValueDecoder {
	protected static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG) 
			System.err.println("GlobalValueDecoder: " + message);
	}
	
	private static Value getValue(int index, Type expectedType, List<Value> result, String recordName) {
		while (index >= result.size())
			result.add(new HolderValue());
		Value value = result.get(index);
		if (value.isHolder()) {
			value.getHolderSelf().setHolderType(expectedType);
		} else if (!value.getType().equals(expectedType)) {
			throw new RuntimeException(recordName + " got wrong type for child value");
		}
		return value;
	}
	
	private static Value decodeConstant(
			DataRecord constant, 
			Type currentType, 
			int index, 
			List<Value> result,
			Type[] typeTable) {
		
		switch (constant.getCode()) {
		case ConstantsBlock.CST_CODE_SETTYPE:
			throw new RuntimeException("This shouldn't happen");

		case ConstantsBlock.CST_CODE_NULL:
			return Value.getNullValue(currentType);
			
		case ConstantsBlock.CST_CODE_UNDEF:
			return new UndefValue(currentType);
		
		case ConstantsBlock.CST_CODE_INTEGER: {
			if (!currentType.isInteger())
				throw new RuntimeException("CST_CODE_INTEGER expects currentType to be an integer");
			int width = currentType.getIntegerSelf().getWidth();
			if (width > 64)
				throw new RuntimeException("CST_CODE_INTEGER cannot deal with widths of >63");
			long words[] = {decodeSignRotatedValue(constant.getOp(0).getNumericValue())};
			return IntegerValue.get(width, words);
		}
		
		case ConstantsBlock.CST_CODE_WIDE_INTEGER: {
			if (!currentType.isInteger())
				throw new RuntimeException("CST_CODE_WIDE_INTEGER expects currentType to be an integer");
			int width = currentType.getIntegerSelf().getWidth();
			long[] words = new long[constant.getNumOps()];
			for (int k = 0; k < constant.getNumOps(); k++)
				words[k] = decodeSignRotatedValue(constant.getOp(k).getNumericValue());
			return IntegerValue.get(width, words);
		}
		
		case ConstantsBlock.CST_CODE_FLOAT: {
			if (!currentType.isFloatingPoint())
				throw new RuntimeException("CST_CODE_FLOAT expects currentType to be floating point");
			FloatingPointType fpt = currentType.getFloatingPointSelf();
			if (fpt.getKind().equals(FloatingPointType.Kind.FLOAT) ||
				fpt.getKind().equals(FloatingPointType.Kind.DOUBLE)) {
				long[] words = {constant.getOp(0).getNumericValue()};
				return FloatingPointValue.get(fpt, words);
			} else {
				if (constant.getNumOps() < 2)
					throw new RuntimeException("CST_CODE_FLOAT needs 2 ops for non-floats");
				long[] words = {constant.getOp(0).getNumericValue(), 
								constant.getOp(1).getNumericValue()};
				return FloatingPointValue.get(fpt, words);
			}
		}
		
		case ConstantsBlock.CST_CODE_AGGREGATE: {
			if (!currentType.isComposite())
				throw new RuntimeException("CSE_CODE_AGGREGATE expects aggregate currentType");
			CompositeType ctype = currentType.getCompositeSelf();
			
			if (ctype.isStructure()) {
				StructureType struct = ctype.getStructureSelf();
				if (constant.getNumOps() < struct.getNumFields())
					throw new RuntimeException("CST_CODE_AGGREGATE doesn't have enough values for struct");
				List<Value> fieldValues = new ArrayList<Value>(struct.getNumFields());
				for (int k = 0; k < struct.getNumFields(); k++) {
					int valueIndex = (int)constant.getOp(k).getNumericValue();
					Value field = getValue(valueIndex, struct.getFieldType(k), result, "CST_CODE_AGGREGATE");
					fieldValues.add(field);
				}
				
				return new ConstantStructureValue(struct, fieldValues);
			} else if (ctype.isArray()) {
				ArrayType array = ctype.getArraySelf();
				if (array.getNumElements().gt(new UnsignedLong(constant.getNumOps())))
					throw new RuntimeException("Not enough values for CST_CODE_AGGREGATE array");
				
				List<Value> elements = new ArrayList<Value>((int)array.getNumElements().signedValue());
				for (int i = 0; i < array.getNumElements().signedValue(); i++) {
					int valueIndex = (int)constant.getOp(i).getNumericValue();
					Value elt = getValue(valueIndex, array.getElementType(), result, "CST_CODE_AGGREGATE");
					elements.add(elt);
				}
				
				return new ConstantExplicitArrayValue(array, elements);
			} else if (ctype.isVector()) {
				VectorType vector = ctype.getVectorSelf();
				
				if (vector.getNumElements().gt(new UnsignedLong(constant.getNumOps())))
					throw new RuntimeException("Not enough values for CST_CODE_AGGREGATE vector");
				
				List<Value> elements = new ArrayList<Value>((int)vector.getNumElements().signedValue());
				for (int i = 0; i < vector.getNumElements().signedValue(); i++) {
					int valueIndex = (int)constant.getOp(i).getNumericValue();
					Value elt = getValue(valueIndex, vector.getElementType(), result, "CST_CODE_AGGREGATE");
					elements.add(elt);
				}
				
				return new ConstantExplicitVectorValue(vector, elements);
			} else {
				throw new RuntimeException("This should never happen");
			}
		}
		
		case ConstantsBlock.CST_CODE_STRING: {
			// values are int bits, not value ids
			if (!currentType.isComposite() || !currentType.getCompositeSelf().isArray() || !currentType.getCompositeSelf().getArraySelf().getElementType().isInteger())
				throw new RuntimeException("CST_CODE_STRING expects array of integer currentType");
			ArrayType array = currentType.getCompositeSelf().getArraySelf();
			IntegerType elttype = array.getElementType().getIntegerSelf();
			if (array.getNumElements().gt(new UnsignedLong(constant.getNumOps())))
				throw new RuntimeException("Not enough values for CST_CODE_STRING");
			List<Value> elements = new ArrayList<Value>((int)array.getNumElements().signedValue());
			long[] words = {0L};
			for (int i = 0; i < array.getNumElements().signedValue(); i++) {
				words[0] = constant.getOp(i).getNumericValue();
				elements.add(IntegerValue.get(elttype.getWidth(), words));
			}
			return new ConstantExplicitArrayValue(array, elements);
		}
			
		case ConstantsBlock.CST_CODE_CSTRING: {
			// values are int bits, not value ids (add null terminator)
			if (!currentType.isComposite() || !currentType.getCompositeSelf().isArray() || !currentType.getCompositeSelf().getArraySelf().getElementType().isInteger())
				throw new RuntimeException("CST_CODE_CSTRING expects array of integer currentType");
			ArrayType array = currentType.getCompositeSelf().getArraySelf();
			IntegerType elttype = array.getElementType().getIntegerSelf();
			if (!array.getNumElements().equals(new UnsignedLong(constant.getNumOps()+1)))
				throw new RuntimeException("Not enough values for CST_CODE_CSTRING: " + array.getNumElements() + ", " + constant.getNumOps());
			List<Value> elements = new ArrayList<Value>((int)array.getNumElements().signedValue());
			long[] words = {0L};
			for (int i = 0; i < constant.getNumOps(); i++) {
				words[0] = constant.getOp(i).getNumericValue();
				elements.add(IntegerValue.get(elttype.getWidth(), words));
			}
			words[0] = 0L;
			elements.add(IntegerValue.get(elttype.getWidth(), words));
			
			return new ConstantExplicitArrayValue(array, elements);
		}
		
		case ConstantsBlock.CST_CODE_CE_BINOP: {
			Binop binop = Binop.decodeBinop((int)constant.getOp(0).getNumericValue(), currentType);
			int lhsindex = (int)constant.getOp(1).getNumericValue();
			int rhsindex = (int)constant.getOp(2).getNumericValue();
			
			Value lhs = getValue(lhsindex, currentType, result, "CST_CODE_CE_BINOP");
			Value rhs = getValue(rhsindex, currentType, result, "CST_CODE_CE_BINOP");

			return new ConstantExpr(new BinopInstruction(binop, lhs, rhs));
		}
		
		case ConstantsBlock.CST_CODE_CE_CAST: {
			// currentType is the destination type
			Cast cast = Cast.decodeCast((int)constant.getOp(0).getNumericValue());
			Type intype = typeTable[(int)constant.getOp(1).getNumericValue()];
			Type outtype = currentType;
			int casteeIndex = (int)constant.getOp(2).getNumericValue();
			Value castee = getValue(casteeIndex, intype, result, "CST_CODE_CE_CAST");

			return new ConstantExpr(new CastInstruction(cast, outtype, castee));
		}
			
		case ConstantsBlock.CST_CODE_CE_GEP: {
			// [n x (type,value)]
			Type baseType = typeTable[(int)constant.getOp(0).getNumericValue()];
			int baseValueIndex = (int)constant.getOp(1).getNumericValue();
			Value baseValue = getValue(baseValueIndex, baseType, result, "CST_CODE_CE_GEP");
			
			List<Value> indexes = new ArrayList<Value>(constant.getNumOps());
			for (int i = 2; i < constant.getNumOps(); i+=2) {
				Type indexType = typeTable[(int)constant.getOp(i).getNumericValue()];
				int indexIndex = (int)constant.getOp(i+1).getNumericValue();
				Value indexValue = getValue(indexIndex, indexType, result, "CST_CODE_CE_GEP");
				indexes.add(indexValue);
			}
			
			return new ConstantExpr(new GEPInstruction(baseValue, indexes));
		}
		
		case ConstantsBlock.CST_CODE_CE_SELECT: {
			// [opval#,opval#,opval#]
			// currentType is type of op2 and op3
			int condIndex = (int)constant.getOp(0).getNumericValue();
			int trueIndex = (int)constant.getOp(1).getNumericValue();
			int falseIndex = (int)constant.getOp(2).getNumericValue();
			
			Value condValue = getValue(condIndex, Type.getIntegerType(1), result, "CST_CODE_CE_SELECT");
			Value trueValue = getValue(trueIndex, currentType, result, "CST_CODE_CE_SELECT");
			Value falseValue = getValue(falseIndex, currentType, result, "CST_CODE_CE_SELECT");
			
			return new ConstantExpr(new SelectInstruction(condValue, trueValue, falseValue));
		}
			
		case ConstantsBlock.CST_CODE_CE_EXTRACTELT: {
			// [type,val,val]
			Type vectorType = typeTable[(int)constant.getOp(0).getNumericValue()];
			int vectorIndex = (int)constant.getOp(1).getNumericValue();
			Value vectorValue = getValue(vectorIndex, vectorType, result, "CST_CODE_CE_EXTRACTELT");
			if (!(vectorType.isComposite() && vectorType.getCompositeSelf().isVector()))
				throw new RuntimeException("CST_CODE_CE_EXTRACTELT expects vector type");
			int indexIndex = (int)constant.getOp(2).getNumericValue();
			Value indexValue = getValue(indexIndex, Type.getIntegerType(32), result, "CST_CODE_CE_EXTRACTELT");
			
			return new ConstantExpr(new ExtractEltInstruction(vectorValue, indexValue));
		}
		
		case ConstantsBlock.CST_CODE_CE_INSERTELT: {
			// [vectorval,elementval,indexval]
			// currentType = vector
			if (!(currentType.isComposite() && currentType.getCompositeSelf().isVector()))
				throw new RuntimeException("CST_CODE_CE_INSERTELT expects current type to be a vector");
			int vectorIndex = (int)constant.getOp(0).getNumericValue();
			Value vectorValue = getValue(vectorIndex, currentType, result, "CST_CODE_CE_INSERTELT");
			int elementIndex = (int)constant.getOp(1).getNumericValue();
			Value elementValue = getValue(elementIndex, currentType.getCompositeSelf().getVectorSelf().getElementType(), result, "CST_CODE_CE_INSERTELT");
			int indexIndex = (int)constant.getOp(2).getNumericValue();
			Value indexValue = getValue(indexIndex, Type.getIntegerType(32), result, "CST_CODE_CE_INSERTELT");
			
			return new ConstantExpr(new InsertEltInstruction(vectorValue, elementValue, indexValue));
		}
		
		case ConstantsBlock.CST_CODE_CE_SHUFFLEVEC: {
			// [vec1val,vec2val,shufflevecval]
			// currentType = vec{1,2}val type
			if (!(currentType.isComposite() && currentType.getCompositeSelf().isVector()))
				throw new RuntimeException("CST_CODE_CE_SHUFFLEVEC expects vector current type");
			int vec1Index = (int)constant.getOp(0).getNumericValue();
			Value vec1Value = getValue(vec1Index, currentType, result, "CST_CODE_CE_SHUFFLEVEC");
			int vec2Index = (int)constant.getOp(1).getNumericValue();
			Value vec2Value = getValue(vec2Index, currentType, result, "CST_CODE_CE_SHUFFLEVEC");
			
			Type shuffleType = new VectorType(Type.getIntegerType(32), currentType.getCompositeSelf().getVectorSelf().getNumElements());
			int shuffleIndex = (int)constant.getOp(2).getNumericValue();
			Value shuffleValue = getValue(shuffleIndex, shuffleType, result, "CST_CODE_CE_SHUFFLEVEC");
			
			return new ConstantExpr(new ShuffleVecInstruction(vec1Value, vec2Value, shuffleValue));
		}
			
		case ConstantsBlock.CST_CODE_CE_CMP: {
			// [optype,val1,val2,pred]
			Type compareType = typeTable[(int)constant.getOp(0).getNumericValue()];
			boolean isint;
			if (compareType.isInteger() || 
				(compareType.isComposite() && compareType.getCompositeSelf().isPointer()) ||
				(compareType.isComposite() && 
				 compareType.getCompositeSelf().isVector() && 
				 compareType.getCompositeSelf().getVectorSelf().getElementType().isInteger())) {
				isint = true;
			} else if (compareType.isFloatingPoint() ||
					   (compareType.isComposite() && 
						compareType.getCompositeSelf().isVector() && 
						compareType.getCompositeSelf().getVectorSelf().getElementType().isFloatingPoint())) {
				isint = false;
			} else {
				throw new RuntimeException("CST_CODE_CE_CMP expects comparable type");
			}
			
			int val1Index = (int)constant.getOp(1).getNumericValue();
			Value val1Value = getValue(val1Index, compareType, result, "CST_CODE_CE_CMP");
			int val2Index = (int)constant.getOp(2).getNumericValue();
			Value val2Value = getValue(val2Index, compareType, result, "CST_CODE_CE_CMP");
			
			ComparisonPredicate predicate;
			if (isint) {
				predicate = IntegerComparisonPredicate.getByValue((int)constant.getOp(3).getNumericValue());
			} else {
				predicate = FloatingPointComparisonPredicate.getByValue((int)constant.getOp(3).getNumericValue());
			}
			
			return new ConstantExpr(new CmpInstruction(predicate, val1Value, val2Value));
		}
		
		case ConstantsBlock.CST_CODE_INLINEASM: { 
			// [hasSideEffects,asmSize,asmSize x char,constSize,constSize x char]
			boolean hasSideEffects = (constant.getOp(0).getNumericValue() != 0L);
			int asmSize = (int)constant.getOp(1).getNumericValue();
			int constraintSize = (int)constant.getOp(2+asmSize).getNumericValue();
			
			String asmString = ModuleDecoder.recordToString(constant, 2, asmSize);
			String constraintString = ModuleDecoder.recordToString(constant, 3+asmSize, constraintSize);
			
			return new ConstantInlineASM(hasSideEffects, currentType.getCompositeSelf().getPointerSelf(), asmString, constraintString);
		}
			
		default:
			throw new RuntimeException("How did this happen?");
		}
	}
	
	
	
	/**
	 * Adds the constants from the given ConstantsBlock to the given value list.
	 * The input valueIndex is the starting valueIndex for use with these constants.
	 * Returns the new valueIndex resulting from adding these constants.
	 */
	protected static int addConstantsBlockValues(ConstantsBlock constants, int valueIndex, List<Value> result, Type[] typeTable) {
		Type currentType = Type.getIntegerType(32);
		for (int j = 0; j < constants.getNumBlockContents(); j++) {
			DataRecord constant = constants.getBlockContents(j).getDataRecordSelf();
			if (constant.getCode() == ConstantsBlock.CST_CODE_SETTYPE) {
				currentType = typeTable[(int)constant.getOp(0).getNumericValue()];
			} else {
				Value value = decodeConstant(constant, currentType, valueIndex, result, typeTable);
				
				debug("Decoding constant value: [" + valueIndex + "] = " + value);
				
				if (valueIndex < result.size()) {
					// set the holder contents
					Value holder = result.get(valueIndex);
					if (!holder.isHolder())
						throw new RuntimeException("Value should be a holder!");
					holder.getHolderSelf().setInnerValue(value);
				} else {
					result.add(value);
				}
				valueIndex++;
			}
		}

		return valueIndex;
	}
	

	/**
	 * Decodes and returns the global value list from the ModuleBlock's constants/globals/functions/aliases.
	 * This will also take into account any MODULE_CODE_PURGEVALS records encountered.
	 */
	protected static LinkedList<Value> decodeGlobalValueTable(
			ModuleBlock block, 
			Module module, 
			Type[] typeTable,
			List<ParameterAttributeMap> paramAttrList) {
		LinkedList<Value> result = new LinkedList<Value>();
		int valueIndex = 0;
		
		for (int i = 0; i < block.getNumBlockContents(); i++) {
			BlockContents bc = block.getBlockContents(i);
			if (bc.isBlock()) {
				Block subblock = bc.getBlockSelf();
				if (!subblock.isConstants())
					continue;
				ConstantsBlock constants = subblock.getConstantsSelf();
				
				valueIndex = addConstantsBlockValues(constants, valueIndex, result, typeTable);
			} else {
				DataRecord record = bc.getDataRecordSelf();
				if (record.getCode() == ModuleBlock.MODULE_CODE_GLOBALVAR) {
					Type type = typeTable[(int)record.getOp(0).getNumericValue()];
					boolean isconst = (record.getOp(1).getNumericValue() != 0L);
					Linkage linkage = Linkage.decodeLinkage((int)record.getOp(3).getNumericValue());
					long alignment = record.getOp(4).getNumericValue();
					long section = record.getOp(5).getNumericValue();
					if (section < 0 || section > module.getNumSections())
						throw new RuntimeException("Invalid section number: " + section);
					
					Visibility visibility = Visibility.DefaultVisibility;
					if (record.getNumOps() > 6)
						visibility = Visibility.decodeVisibility((int)record.getOp(6).getNumericValue());
					boolean isThreadLocal = false;
					if (record.getNumOps() > 7)
						isThreadLocal = (record.getOp(7).getNumericValue() != 0L);
					int initid = (int)record.getOp(2).getNumericValue();
					Value initValue = null;
					if (initid > 0) {
						initValue = getValue(initid-1, type.getCompositeSelf().getPointerSelf().getPointeeType(), result, "MODULE_CODE_GLOBALVAR");
					}
					
					GlobalVariable variable = new GlobalVariable(
							type.getCompositeSelf().getPointerSelf(),
							isconst,
							initValue,
							linkage, 
							(1<<(int)alignment) >> 1,
							(int)section,
							visibility,
							isThreadLocal);
					
					debug("Adding new global variable : " + variable + " : " + System.identityHashCode(variable));
					
					module.addGlobalVariable(variable);
					
					if (valueIndex < result.size()) {
						// set the holder contents
						Value holder = result.get(valueIndex);
						if (!holder.isHolder())
							throw new RuntimeException("Value should be a holder!");
						holder.getHolderSelf().setInnerValue(variable);
					} else {
						result.add(variable);
					}
					valueIndex++;
				} else if (record.getCode() == ModuleBlock.MODULE_CODE_ALIAS) {
					Type type = typeTable[(int)record.getOp(0).getNumericValue()];
					int initid = (int)record.getOp(1).getNumericValue();
					Linkage linkage = Linkage.decodeLinkage((int)record.getOp(2).getNumericValue());
					Visibility visibility = Visibility.DefaultVisibility;
					if (record.getNumOps() > 3)
						visibility = Visibility.decodeVisibility((int)record.getOp(3).getNumericValue());
					
					Value aliaseeValue = getValue(initid, type, result, "MODULE_CODE_ALIAS");
					AliasValue value = new AliasValue(type.getCompositeSelf().getPointerSelf(), aliaseeValue, linkage, visibility);

					module.addAlias(value);
					
					if (valueIndex < result.size()) {
						// set the holder contents
						Value holder = result.get(valueIndex);
						if (!holder.isHolder())
							throw new RuntimeException("Value should be a holder!");
						holder.getHolderSelf().setInnerValue(value);
					} else {
						result.add(value);
					}
					valueIndex++;
				} else if (record.getCode() == ModuleBlock.MODULE_CODE_FUNCTION) {
					// know record has 8 ops
					Type type = typeTable[(int)record.getOp(0).getNumericValue()];
					int cc = (int)record.getOp(1).getNumericValue();
					boolean isProto = (record.getOp(2).getNumericValue() != 0L);
					Linkage linkage = Linkage.decodeLinkage((int)record.getOp(3).getNumericValue());
					
					// paramattr index is 1-based! (0 implies paramAttr list with no attrs set)
					int paramAttrIndex = (int)record.getOp(4).getNumericValue();
					ParameterAttributeMap paramAttrs;
					if (paramAttrIndex != 0) {
						paramAttrs = paramAttrList.get(paramAttrIndex-1);
					} else {
						paramAttrs = new ParameterAttributeMap();
					}
					
					long alignment = record.getOp(5).getNumericValue();
					long section = record.getOp(6).getNumericValue(); // 0 means default section
					if (section < 0 || section > module.getNumSections())
						throw new RuntimeException("Invalid section number: " + section);
					
					Visibility visibility = Visibility.decodeVisibility((int)record.getOp(7).getNumericValue());
					long collector = 0L;
					if (record.getNumOps() > 8) {
						collector = record.getOp(8).getNumericValue();
						if (collector < 0 || collector > module.getNumCollectors())
							throw new RuntimeException("Invalid collector number: " + collector);
					}
					
					FunctionValue value = new FunctionValue(
							type.getCompositeSelf().getPointerSelf(),
							cc,
							isProto,
							linkage,
							paramAttrs,
							(1<<(int)alignment) >> 1,
							(int)section,
							visibility,
							(int)collector);
					
					module.addFunctionHeader(value);

					if (valueIndex < result.size()) {
						// set the holder contents
						Value holder = result.get(valueIndex);
						if (!holder.isHolder())
							throw new RuntimeException("Value should be a holder!");
						holder.getHolderSelf().setInnerValue(value);
					} else {
						result.add(value);
					}
					
					valueIndex++;
				} else if (record.getCode() == ModuleBlock.MODULE_CODE_PURGEVALS) {
					long newlength = record.getOp(0).getNumericValue();
					if (newlength > result.size())
						throw new RuntimeException("MODULE_CODE_PURGEVALS should not give a vlaue larger than the valuelist size");

					debug("Purging values to " + newlength + " from " + result.size());
					
					for (int k = 0; k < result.size(); k++) {
						if (result.get(k).isHolder())
							throw new RuntimeException("Unresolved values left in purged valuelist: " + k + ", " + newlength);
					}
					
					while (result.size() > newlength)
						result.removeLast();

					valueIndex = (int)newlength;
				}
			}

		}
		
		return result;
	}
	
	private static long decodeSignRotatedValue(long input) {
		if ((input & 1L) == 0L) {
			return input >>> 1;
		} else if (input != 1L) {
			return -(input >>> 1);
		} else {
			return 1L << 63;
		}
	}
}
