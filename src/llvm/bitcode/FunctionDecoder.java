package llvm.bitcode;

import java.util.*;

import llvm.instructions.*;
import llvm.instructions.BasicBlock.Handle;
import llvm.types.FunctionType;
import llvm.types.PointerType;
import llvm.types.Type;
import llvm.types.VectorType;
import llvm.values.*;
import util.pair.Pair;

/**
 * This class is responsible for creating the FunctionBody instances from an
 * LLVM 2.3 bitcode file.
 */
public class FunctionDecoder {
	protected static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("FunctionDecoder: " + message);
	}
	
	private static class Int {
		public int value;
		public Int(int _value) {this.value = _value;}
	}
	
	/**
	 * Must be called after decodeTypeTable and decodeModuleMisc and decodeGlobalValueTable.
	 */
	public static void decodeFunctions(
			ModuleBlock block, 
			Module module, 
			Type[] typeTable, 
			List<Value> valueTable,
			List<ParameterAttributeMap> paramAttrList) {

		int headerIndex = 0;
		for (int i = 0; i < block.getNumBlockContents(); i++) {
			BlockContents bc = block.getBlockContents(i);
			if (bc.isBlock()) {
				Block subblock = bc.getBlockSelf();

				if (subblock.isFunction()) {
					FunctionBlock funcblock = subblock.getFunctionSelf();
					FunctionValue header = module.getFunctionHeader(headerIndex++);
					while (header.isPrototype()) {
						header = module.getFunctionHeader(headerIndex++);
					}
					
					debug("Decoding function");
					
					FunctionBody body = decodeFunction(funcblock, header, typeTable, valueTable, paramAttrList);
					module.addFunctionBody(body);
				}
			}
		}
	}
	
	
	private static FunctionBody decodeFunction(
			FunctionBlock block, 
			FunctionValue header, 
			Type[] typeTable, 
			List<Value> valueTable, 
			List<ParameterAttributeMap> paramAttrList) {
		
		FunctionBody body = new FunctionBody(header);
		BasicBlock currentbb = null;
		int originalValueCount = valueTable.size();
		Iterator<BasicBlock> blockIter = null;
		int valueIndex = valueTable.size();
		
		for (int i = 0; i < header.getNumArguments(); i++) {
			debug("Adding function-local value:[" + valueIndex + "] = " + header.getArgument(i));
			valueTable.add(header.getArgument(i));
			valueIndex++;
		}
		
		for (int i = 0; i < block.getNumBlockContents(); i++) {
			BlockContents bc = block.getBlockContents(i);
			if (bc.isBlock()) {
				Block subblock = bc.getBlockSelf();
				if (subblock.isConstants()) {
					valueIndex = GlobalValueDecoder.addConstantsBlockValues(subblock.getConstantsSelf(), valueIndex, valueTable, typeTable);
				} else if (subblock.isValueSymtab()) {
					ModuleDecoder.setValueNames(valueTable, subblock.getValueSymtabSelf(), body);
					setBlockNames(body, subblock.getValueSymtabSelf());
				} else {
					throw new RuntimeException("This should never happen!");
				}
			} else {
				DataRecord record = bc.getDataRecordSelf();
				
				if (record.getCode() == FunctionBlock.FUNC_CODE_DECLAREBLOCKS) {
					int numblocks = (int)record.getOp(0).getNumericValue();
					for (int j = 0; j < numblocks; j++) {
						body.addBlock(new BasicBlock());
					}
					blockIter = body.blockIterator();
				} else {
					if (currentbb == null) {
						currentbb = blockIter.next();
					}
					Instruction inst = decodeInstruction(record, typeTable, valueTable, body, paramAttrList);
					Handle handle = currentbb.addInstruction(inst);
					
					if (!inst.getType().equals(Type.VOID_TYPE)) {
						// add to valueList
						VirtualRegister reg = VirtualRegister.getVirtualRegister(inst.getType());
						body.getRegisterAssignment().set(reg, handle);
				
						if (valueIndex < valueTable.size()) {
							Value value = valueTable.get(valueIndex);
							if (!value.isHolder())
								throw new RuntimeException("Value should be a holder");
							value.getHolderSelf().setInnerValue(reg);
						} else {
							valueTable.add(reg);
						}
						valueIndex++;
					} else {
						debug(inst.toString());
					}
					
					if (inst.isTerminator()) {
						currentbb = null;
					}
				}
			}
		}
		
		if (currentbb != null)
			throw new RuntimeException("Last instruction was not a terminator");
		
		while (valueTable.size() > originalValueCount)
			valueTable.remove(valueTable.size()-1);

		body.setStart(body.getBlock(0));
		return body;
	}
	
	
	private static Instruction decodeInstruction(
			DataRecord record, 
			Type[] typeTable, 
			List<Value> valueTable, 
			FunctionBody body, 
			List<ParameterAttributeMap> paramAttrList) {
		
		switch (record.getCode()) {
		case FunctionBlock.FUNC_CODE_INST_BINOP: {
			debug("Saw binop instruction");
			Int index = new Int(0);
			Value lhs = getValueTypePair(record, index, typeTable, valueTable);
			Value rhs = getValue(record, index, lhs.getType(), valueTable);
			int binopCode = (int)record.getOp(index.value).getNumericValue();
			Binop binop = Binop.decodeBinop(binopCode, lhs.getType());
			return new BinopInstruction(binop, lhs, rhs);
		}
		
		case FunctionBlock.FUNC_CODE_INST_CAST: {
			debug("Saw cast instruction");
			Int index = new Int(0);
			Value lhs = getValueTypePair(record, index, typeTable, valueTable);
			Type destType = typeTable[(int)record.getOp(index.value++).getNumericValue()];
			int castOpcode = (int)record.getOp(index.value++).getNumericValue();
			Cast cast = Cast.decodeCast(castOpcode);
			return new CastInstruction(cast, destType, lhs);
		}
		
		case FunctionBlock.FUNC_CODE_INST_GEP: {
			debug("Saw gep instruction");
			Int index = new Int(0);
			Value baseValue = getValueTypePair(record, index, typeTable, valueTable);
			List<Value> indexes = new ArrayList<Value>(record.getNumOps());

			while (index.value < record.getNumOps()) {
				Value indexValue = getValueTypePair(record, index, typeTable, valueTable);
				indexes.add(indexValue);
			}
			return new GEPInstruction(baseValue, indexes);
		}
		
		case FunctionBlock.FUNC_CODE_INST_SELECT: {
			debug("Saw select instruction");
			Int index = new Int(0);
			Value trueValue = getValueTypePair(record, index, typeTable, valueTable);
			Value falseValue = getValue(record, index, trueValue.getType(), valueTable);
			Value condition = getValue(record, index, Type.getIntegerType(1), valueTable);
			return new SelectInstruction(condition, trueValue, falseValue);
		}
		
		case FunctionBlock.FUNC_CODE_INST_EXTRACTELT: {
			debug("Saw extractelt instruction");
			Int index = new Int(0);
			Value vector = getValueTypePair(record, index, typeTable, valueTable);
			Value indexValue = getValue(record, index, Type.getIntegerType(32), valueTable);
			return new ExtractEltInstruction(vector, indexValue);
		}
		
		case FunctionBlock.FUNC_CODE_INST_INSERTELT: {
			debug("Saw insertelt instruction");
			Int index = new Int(0);
			Value vector = getValueTypePair(record, index, typeTable, valueTable);
			if (!(vector.getType().isComposite() && vector.getType().getCompositeSelf().isVector()))
				throw new RuntimeException("Vector doesn't have vector type");
			Type elementType = vector.getType().getCompositeSelf().getVectorSelf().getElementType();
			Value element = getValue(record, index, elementType, valueTable);
			Value indexValue = getValue(record, index, Type.getIntegerType(32), valueTable);
			
			return new InsertEltInstruction(vector, element, indexValue);
		}
		
		case FunctionBlock.FUNC_CODE_INST_SHUFFLEVEC: {
			debug("Saw shufflevec instruction");
			Int index = new Int(0);
			Value vector1 = getValueTypePair(record, index, typeTable, valueTable);
			UnsignedLong numelements;
			if (!(vector1.getType().isComposite() && vector1.getType().getCompositeSelf().isVector()))
				throw new RuntimeException("Vector has wrong type");
			else
				numelements = vector1.getType().getCompositeSelf().getVectorSelf().getNumElements();
			Value vector2 = getValue(record, index, vector1.getType(), valueTable);
			Type maskType = new VectorType(Type.getIntegerType(32), numelements);
			Value mask = getValue(record, index, maskType, valueTable);
			
			return new ShuffleVecInstruction(vector1, vector2, mask);
		}
		
		case FunctionBlock.FUNC_CODE_INST_CMP: {
			debug("Saw cmp instruction");
			Int index = new Int(0);
			Value lhs = getValueTypePair(record, index, typeTable, valueTable);
			Type lhstype = lhs.getType();
			Value rhs = getValue(record, index, lhstype, valueTable);
			int pred = (int)record.getOp(index.value++).getNumericValue();
			
			boolean isint = false;
			if (lhstype.isInteger() ||
				(lhstype.isComposite() && lhstype.getCompositeSelf().isPointer()) ||
				(lhstype.isComposite() && 
				 lhstype.getCompositeSelf().isVector() && 
				 lhstype.getCompositeSelf().getVectorSelf().getElementType().isInteger()))
				isint = true;
			
			ComparisonPredicate predicate = (isint ? IntegerComparisonPredicate.getByValue(pred) :
				 									 FloatingPointComparisonPredicate.getByValue(pred));
			return new CmpInstruction(predicate, lhs, rhs);
		}
		
		case FunctionBlock.FUNC_CODE_INST_RET: {
			debug("Saw ret instruction");
			if (record.getNumOps() == 0) 
				return new RetInstruction();
			
			Int index = new Int(0);
			List<Value> returnValues = new ArrayList<Value>(record.getNumOps());
			while (index.value < record.getNumOps()) {
				Value ret = getValueTypePair(record, index, typeTable, valueTable);
				returnValues.add(ret);
			}
			
			RetInstruction result = new RetInstruction(returnValues);
			
			FunctionType funcType = body.getHeader().getType().getPointeeType().getFunctionSelf();
			if (!funcType.getReturnType().equalsType(result.getReturnValueType()))
				throw new RuntimeException("Ret output type does not match function return type: " + funcType.getReturnType() + ", " + result.getReturnValueType());
			
			return result;
		}
		
		case FunctionBlock.FUNC_CODE_INST_BR: {
			debug("Saw br instruction");
			BasicBlock trueBlock = body.getBlock((int)record.getOp(0).getNumericValue());
			if (record.getNumOps() == 1)
				return new BrInstruction(trueBlock);
			BasicBlock falseBlock = body.getBlock((int)record.getOp(1).getNumericValue());
			Value condition = getValue(record, new Int(2), Type.BOOLEAN_TYPE, valueTable);
			return new BrInstruction(condition, trueBlock, falseBlock);
		}
		
		case FunctionBlock.FUNC_CODE_INST_SWITCH: {
			debug("Saw switch instruction");
			Type condType = typeTable[(int)record.getOp(0).getNumericValue()];
			if (!condType.isInteger())
				throw new RuntimeException("Condition type must be integer type");
			Value condvalue = getValue(record, new Int(1), condType, valueTable);
			BasicBlock defaultBlock = body.getBlock((int)record.getOp(2).getNumericValue());
			
			Map<IntegerValue,BasicBlock> caseMap = new HashMap<IntegerValue,BasicBlock>();
			Int index = new Int(0);
			for (int i = 3; i < record.getNumOps(); i+=2) {
				index.value = i;
				Value caseValue = getValue(record, index, condType, valueTable);
				if (!caseValue.isInteger())
					throw new RuntimeException("Case label must be constant integer");
				BasicBlock caseBlock = body.getBlock((int)record.getOp(i+1).getNumericValue());
				caseMap.put(caseValue.getIntegerSelf(), caseBlock);
			}
			return new SwitchInstruction(condvalue, defaultBlock, caseMap);
		}
		
		case FunctionBlock.FUNC_CODE_INST_INVOKE: {
			debug("Saw invoke instruction");
			int paramAttrIndex = (int)record.getOp(0).getNumericValue();
			ParameterAttributeMap paramAttrs;
			if (paramAttrIndex == 0) {
				paramAttrs = new ParameterAttributeMap();
			} else {
				paramAttrs = paramAttrList.get(paramAttrIndex-1);
			}
			
			int cc = (int)record.getOp(1).getNumericValue();
			BasicBlock returnBlock = body.getBlock((int)record.getOp(2).getNumericValue());
			BasicBlock unwindBlock = body.getBlock((int)record.getOp(3).getNumericValue());
			
			Int index = new Int(4);
			Value callee = getValueTypePair(record, index, typeTable, valueTable);
			Type calleeType = callee.getType();
			if (!(calleeType.isComposite() && calleeType.getCompositeSelf().isPointer() && calleeType.getCompositeSelf().getPointerSelf().getPointeeType().isFunction()))
				throw new RuntimeException("Callee value must be a pointer to a function");
			FunctionType funcType = calleeType.getCompositeSelf().getPointerSelf().getPointeeType().getFunctionSelf();
				
			List<Value> actuals = new ArrayList<Value>(funcType.getNumParams());
			for (int i = 0; i < funcType.getNumParams(); i++) {
				Value param = getValue(record, index, funcType.getParamType(i), valueTable);
				actuals.add(param);
			}
			
			if (!funcType.isVararg() && record.getNumOps() > index.value) {
				// not vararg, but more values
				throw new RuntimeException("Too many values for non-vararg function");
			} else {
				while (index.value < record.getNumOps()) {
					Value param = getValueTypePair(record, index, typeTable, valueTable);
					actuals.add(param);
				}
			}
			
			return new InvokeInstruction(cc, callee, paramAttrs, returnBlock, unwindBlock, actuals);
		}
		
		case FunctionBlock.FUNC_CODE_INST_UNWIND:
			debug("Saw unwind instruction");
			return UnwindInstruction.INSTANCE;
			
		case FunctionBlock.FUNC_CODE_INST_UNREACHABLE:
			debug("Saw unreachable instruction");
			return UnreachableInstruction.INSTANCE;
			
		case FunctionBlock.FUNC_CODE_INST_PHI: {
			debug("Saw phi instruction");
			// [typeindex, n x (value, bb)]
			Type type = typeTable[(int)record.getOp(0).getNumericValue()];
			if (!type.isFirstClass())
				throw new RuntimeException("Phi type must be firstclass");
			
			List<Pair<? extends Value,BasicBlock>> pairs = 
				new ArrayList<Pair<? extends Value,BasicBlock>>(record.getNumOps()/2);
			
			Int index = new Int(1);
			while (index.value < record.getNumOps()) {
				Value value = getValue(record, index, type, valueTable);
				BasicBlock bb = body.getBlock((int)record.getOp(index.value++).getNumericValue());
				pairs.add(new Pair<Value,BasicBlock>(value, bb));
			}

			return new PhiInstruction(type, pairs);
		}
			
		case FunctionBlock.FUNC_CODE_INST_MALLOC: {
			debug("Saw malloc instruction");
			Type type = typeTable[(int)record.getOp(0).getNumericValue()];
			if (!(type.isComposite() && type.getCompositeSelf().isPointer()))
				throw new RuntimeException("Type must be a pointer");
			if (!type.getCompositeSelf().getPointerSelf().getPointeeType().hasTypeSize())
				throw new RuntimeException("Pointee type must be sized");
			Value numElementsValue = getValue(record, new Int(1), Type.getIntegerType(32), valueTable);
			int alignment = (int)record.getOp(2).getNumericValue();
			return new MallocInstruction(type.getCompositeSelf().getPointerSelf().getPointeeType(), numElementsValue, (1<<alignment)>>1);
		}
		
		case FunctionBlock.FUNC_CODE_INST_FREE: {
			debug("Saw free instruction");
			Value value = getValueTypePair(record, new Int(0), typeTable, valueTable);
			return new FreeInstruction(value);
		}
		
		case FunctionBlock.FUNC_CODE_INST_ALLOCA: {
			debug("Saw alloca instruction");
			Type type = typeTable[(int)record.getOp(0).getNumericValue()];
			if (!(type.isComposite() && type.getCompositeSelf().isPointer()))
				throw new RuntimeException("Type must be a pointer");
			if (!type.getCompositeSelf().getPointerSelf().getPointeeType().hasTypeSize())
				throw new RuntimeException("Pointee type must be sized");
			Value numElementsValue = getValue(record, new Int(1), Type.getIntegerType(32), valueTable);
			int alignment = (int)record.getOp(2).getNumericValue();
			
			return new AllocaInstruction(type.getCompositeSelf().getPointerSelf().getPointeeType(), numElementsValue, (1<<alignment)>>1);
		}
		
		case FunctionBlock.FUNC_CODE_INST_LOAD: {
			debug("Saw load instruction");
			Int index = new Int(0);
			Value loadee = getValueTypePair(record, index, typeTable, valueTable);
			int alignment = (int)record.getOp(index.value++).getNumericValue();
			boolean isVolatile = (record.getOp(index.value++).getNumericValue() != 0L);

			return new LoadInstruction(loadee, (1<<alignment)>>1, isVolatile);
		}
		
		case FunctionBlock.FUNC_CODE_INST_STORE: {
			debug("Saw store instruction");
			Int index = new Int(0);
			Value value = getValueTypePair(record, index, typeTable, valueTable);
			PointerType ptrType = new PointerType(value.getType());
			Value address = getValue(record, index, ptrType, valueTable);
			int alignment = (int)record.getOp(index.value++).getNumericValue();
			boolean isVolatile = (record.getOp(index.value++).getNumericValue() != 0L);
			
			return new StoreInstruction(address, value, (1<<alignment)>>1, isVolatile);
		}
		
		case FunctionBlock.FUNC_CODE_INST_CALL: {
			debug("Saw call instruction");
			// [paramattr, cc, (fntype/fnvalue), arg0, arg1...]
			int paramAttrIndex = (int)record.getOp(0).getNumericValue();
			ParameterAttributeMap paramAttrs;
			if (paramAttrIndex == 0)
				paramAttrs = new ParameterAttributeMap();
			else
				paramAttrs = paramAttrList.get(paramAttrIndex-1);
			int ccinfo = (int)record.getOp(1).getNumericValue();
			Int index = new Int(2);
			
			int cc = ccinfo>>>1;
			boolean tailCall = ((ccinfo&1)!=0);
			
			Value function = getValueTypePair(record, index, typeTable, valueTable);
			if (!(function.getType().isComposite() && function.getType().getCompositeSelf().isPointer()))
				throw new RuntimeException("Function pointer is not a pointer");
			if (!function.getType().getCompositeSelf().getPointerSelf().getPointeeType().isFunction())
				throw new RuntimeException("Function pointer does not point to function");
			
			FunctionType funcType = function.getType().getCompositeSelf().getPointerSelf().getPointeeType().getFunctionSelf();
			List<Value> actuals = new ArrayList<Value>(funcType.getNumParams());
			for (int i = 0; i < funcType.getNumParams(); i++) {
				if (funcType.getParamType(i).isLabel()) {
					int bbindex = (int)record.getOp(index.value++).getNumericValue();
					actuals.add(new LabelValue(body.getBlock(bbindex)));
				} else {
					Value value = getValue(record, index, funcType.getParamType(i), valueTable);
					actuals.add(value);
				}
			}
			
			if (funcType.isVararg()) {
				while (index.value < record.getNumOps()) {
					Value value = getValueTypePair(record, index, typeTable, valueTable);
					actuals.add(value);
				}
			} else if (index.value < record.getNumOps()) {
				throw new RuntimeException("Too many arguments for non-varargs function");
			}
			
			return new CallInstruction(tailCall, cc, function, paramAttrs, actuals);
		}
		
		case FunctionBlock.FUNC_CODE_INST_VAARG: {
			debug("Saw vaarg instruction");
			Type listType = typeTable[(int)record.getOp(0).getNumericValue()];
			Value listValue = getValue(record, new Int(1), listType, valueTable);
			Type resultType = typeTable[(int)record.getOp(2).getNumericValue()];
			
			return new VaargInstruction(listValue, resultType);
		}

		case FunctionBlock.FUNC_CODE_INST_STORE2: {
			debug("Saw store2 instruction");
			Int index = new Int(0);
			Value address = getValueTypePair(record, index, typeTable, valueTable);
			Type ptrType = address.getType();
			if (!(ptrType.isComposite() && ptrType.getCompositeSelf().isPointer()))
				throw new RuntimeException("Address must have pointer type");
			Type elementType = ptrType.getCompositeSelf().getPointerSelf().getPointeeType();
			Value value = getValue(record, index, elementType, valueTable);
			int alignment = (int)record.getOp(index.value++).getNumericValue();
			boolean isVolatile = (record.getOp(index.value++).getNumericValue() != 0L);
			
			return new StoreInstruction(address, value, (1<<alignment)>>1, isVolatile);
		}
			
		case FunctionBlock.FUNC_CODE_INST_GETRESULT: {
			debug("Saw getresult instruction");
			Int index = new Int(0);
			Value value = getValueTypePair(record, index, typeTable, valueTable);
			int grIndex = (int)record.getOp(index.value++).getNumericValue();
			
			return new GetResultInstruction(value, grIndex);
		}
		
		default:
			throw new RuntimeException("Unknown instruction record: " + record);
		}
	}
	
	
	private static void setBlockNames(FunctionBody body, ValueSymtabBlock block) {
		for (int i = 0; i < block.getNumBlockContents(); i++) {
			DataRecord record = block.getBlockContents(i).getDataRecordSelf();
			if (record.getCode() == ValueSymtabBlock.VST_CODE_BBENTRY) {
				int bbIndex = (int)record.getOp(0).getNumericValue();
				if (bbIndex >= body.getNumBlocks())
					throw new RuntimeException("VST_CODE_BBENTRY has invalid index");

				String name = ModuleDecoder.recordToString(record, 1, record.getNumOps()-1);
				BasicBlock bb = body.getBlock(bbIndex);
				bb.setName(name);
			}
		}
	}
	
	private static Value getValue(DataRecord record, Int index, Type type, List<Value> valueList) {
		int valueIndex = (int)record.getOp(index.value++).getNumericValue();
		while (valueIndex >= valueList.size())
			valueList.add(new HolderValue());
		
		Value result = valueList.get(valueIndex);
		if (result.isHolder()) {
			result.getHolderSelf().setHolderType(type);
		}
		return result;
	}
	
	private static Value getValueTypePair(DataRecord record, Int index, Type[] typeTable, List<Value> valueList) {
		int valueIndex = (int)record.getOp(index.value++).getNumericValue();
		while (valueIndex >= valueList.size())
			valueList.add(new HolderValue());
		
		Value result = valueList.get(valueIndex);
		if (result.isHolder()) {
			Type type = typeTable[(int)record.getOp(index.value++).getNumericValue()];
			result.getHolderSelf().setHolderType(type);
		}
		return result;
	}
}
