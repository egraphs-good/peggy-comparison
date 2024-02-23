package llvm.bitcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import llvm.instructions.AllocaInstruction;
import llvm.instructions.BasicBlock;
import llvm.instructions.Binop;
import llvm.instructions.BinopInstruction;
import llvm.instructions.BrInstruction;
import llvm.instructions.CallInstruction;
import llvm.instructions.Cast;
import llvm.instructions.CastInstruction;
import llvm.instructions.CmpInstruction;
import llvm.instructions.ComparisonPredicate;
import llvm.instructions.ExtractEltInstruction;
import llvm.instructions.ExtractValueInstruction;
import llvm.instructions.FloatingPointComparisonPredicate;
import llvm.instructions.FreeInstruction;
import llvm.instructions.FunctionBody;
import llvm.instructions.GEPInstruction;
import llvm.instructions.GetResultInstruction;
import llvm.instructions.InboundsGEPInstruction;
import llvm.instructions.IndirectBRInstruction;
import llvm.instructions.InsertEltInstruction;
import llvm.instructions.InsertValueInstruction;
import llvm.instructions.Instruction;
import llvm.instructions.IntegerComparisonPredicate;
import llvm.instructions.InvokeInstruction;
import llvm.instructions.LoadInstruction;
import llvm.instructions.MallocInstruction;
import llvm.instructions.PhiInstruction;
import llvm.instructions.RetInstruction;
import llvm.instructions.SelectInstruction;
import llvm.instructions.ShuffleVec2_8Instruction;
import llvm.instructions.StoreInstruction;
import llvm.instructions.SwitchInstruction;
import llvm.instructions.UnreachableInstruction;
import llvm.instructions.UnwindInstruction;
import llvm.instructions.VSelectInstruction;
import llvm.instructions.VaargInstruction;
import llvm.instructions.BasicBlock.Handle;
import llvm.types.FunctionType;
import llvm.types.PointerType;
import llvm.types.Type;
import llvm.values.BlockAddressValue;
import llvm.values.DebugLocation;
import llvm.values.FunctionValue;
import llvm.values.HolderValue;
import llvm.values.IntegerValue;
import llvm.values.LabelValue;
import llvm.values.Module;
import llvm.values.ParameterAttributeMap;
import llvm.values.UndefValue;
import llvm.values.Value;
import llvm.values.VirtualRegister;
import util.pair.Pair;

/**
 * This class is responsible for creating the FunctionBody instances from an 
 * LLVM 2.8 bitcode file.
 */
public class FunctionDecoder2_8 {
	public static final long OBO_NO_UNSIGNED_WRAP = 0;
	public static final long OBO_NO_SIGNED_WRAP = 1;
	public static final long SDIV_EXACT = 0;
	
	protected static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("FunctionDecoder2_8: " + message);
	}
	
	public static class Int {
		public int value;
		public Int(int _value) {this.value = _value;}
		public String toString() {
			return "" + this.value;
		}
	}
	
	/**
	 * Must be called after decodeTypeTable and decodeModuleMisc and decodeGlobalValueTable.
	 */
	public static void decodeFunctions(
			boolean LLVM2_7MetadataDetected,
			ModuleBlock2_8 block, 
			Module module, 
			Type[] typeTable, 
			List<Value> valueTable,
			List<Value> metadataValueTable,
			List<ParameterAttributeMap> paramAttrList,
			Map<FunctionValue,Set<BlockAddressValue>> func2blockaddresses,
			Map<Integer,Integer> mdkindmap) {

		int headerIndex = 0;
		for (int i = 0; i < block.getNumBlockContents(); i++) {
			BlockContents bc = block.getBlockContents(i);
			if (bc.isBlock()) {
				Block subblock = bc.getBlockSelf();
				if (subblock.isFunction2_8()) {
					FunctionBlock2_8 funcblock = subblock.getFunction2_8Self();
					FunctionValue header = module.getFunctionHeader(headerIndex++);
					while (header.isPrototype()) {
						header = module.getFunctionHeader(headerIndex++);
					}
					
					debug("Decoding function");
					
					FunctionBody body = decodeFunction(
							LLVM2_7MetadataDetected,
							module, 
							funcblock, 
							header, 
							typeTable, 
							valueTable, 
							metadataValueTable, 
							paramAttrList,
							func2blockaddresses,
							mdkindmap);
					module.addFunctionBody(body);
					
					// now do the blockaddresses
					if (func2blockaddresses.containsKey(header)) {
						for (BlockAddressValue address : func2blockaddresses.get(header)) {
							address.resolve(body.getBlock(address.getBlockNumber()));
						}
					}
				}
			}
		}
	}
	
	private abstract static class State {
		BasicBlock currentbb = null;
		DebugLocation lastLocation = null;
		int valueIndex = 0;
		int metadataValueIndex = 0;
		State(int _valueIndex, int _metadataValueIndex) {
			this.valueIndex = _valueIndex;
			this.metadataValueIndex = _metadataValueIndex;
		}
		public abstract Value addInstruction(Instruction inst);
	}
	
	private static FunctionBody decodeFunction(
			boolean LLVM2_7MetadataDetected,
			Module module,
			FunctionBlock2_8 block, 
			FunctionValue header, 
			Type[] typeTable, 
			final List<Value> valueTable,
			final List<Value> metadataValueTable,
			List<ParameterAttributeMap> paramAttrList,
			Map<FunctionValue,Set<BlockAddressValue>> func2blockaddresses,
			Map<Integer,Integer> mdkindmap) {

		final FunctionBody body = new FunctionBody(header);
		final int originalValueCount = valueTable.size();
		final int originalMetadataValueCount = metadataValueTable.size();
		Iterator<BasicBlock> blockIter = null;
		final List<Instruction> allinsts = new ArrayList<Instruction>();
		
		final State state = new State(valueTable.size(), metadataValueTable.size()) {
			public Value addInstruction(Instruction inst) {
				allinsts.add(inst);
				Handle handle = currentbb.addInstruction(inst);
				Value result;
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
					result = reg;
				} else {
					debug(inst.toString());
					result = null;
				}
				if (inst.isTerminator()) {
					currentbb = null;
				}
				return result;
			}
		};
		
		for (int i = 0; i < header.getNumArguments(); i++) {
			debug("Adding function-local value:[" + state.valueIndex + "] = " + header.getArgument(i));
			valueTable.add(header.getArgument(i));
			state.valueIndex++;
		}
		
		for (int i = 0; i < block.getNumBlockContents(); i++) {
			BlockContents bc = block.getBlockContents(i);
			if (bc.isBlock()) {
				Block subblock = bc.getBlockSelf();
				if (subblock.isConstants2_8()) {
					state.valueIndex = GlobalValueDecoder2_8.addConstantsBlockValues(
							subblock.getConstants2_8Self(), 
							state.valueIndex, 
							valueTable, 
							func2blockaddresses, 
							typeTable);
				} else if (subblock.isValueSymtab()) {
					ModuleDecoder2_8.setValueNames(valueTable, subblock.getValueSymtabSelf(), body);
					setBlockNames(body, subblock.getValueSymtabSelf());
				} else if (subblock.isMetadataAttachment2_8()) {
					MetadataAttachmentDecoder.decodeMetadataAttachment(subblock.getMetadataAttachment2_8Self(), module, mdkindmap, metadataValueTable, allinsts);
				} else if (subblock.isMetadata2_8()) {
					state.metadataValueIndex = MetadataDecoder.decodeMetadataBlock(
							LLVM2_7MetadataDetected,
							state.metadataValueIndex,
							subblock.getMetadata2_8Self(), 
							module, 
							typeTable, 
							mdkindmap, 
							valueTable, 
							metadataValueTable);
				} else {
					throw new RuntimeException("This should never happen!");
				}
			} else {
				DataRecord record = bc.getDataRecordSelf();
				if (record.getCode() == FunctionBlock2_8.FUNC_CODE_DECLAREBLOCKS) {
					int numblocks = (int)record.getOp(0).getNumericValue();
					for (int j = 0; j < numblocks; j++) {
						body.addBlock(new BasicBlock());
					}
					blockIter = body.blockIterator();
				} else if (record.getCode() == FunctionBlock2_8.FUNC_CODE_DEBUG_LOC ||
						   record.getCode() == FunctionBlock2_8.FUNC_CODE_DEBUG_LOC2) {
					state.lastLocation = decodeDebugLocation(record, metadataValueTable);
					Instruction inst = allinsts.get(allinsts.size()-1);
					inst.setDebugLocation(state.lastLocation);
				} else if (record.getCode() == FunctionBlock2_8.FUNC_CODE_DEBUG_LOC_AGAIN) {
					Instruction inst = allinsts.get(allinsts.size()-1);
					inst.setDebugLocation(state.lastLocation);
				} else {
					if (state.currentbb == null) {
						state.currentbb = blockIter.next();
					}
					decodeInstruction(record, typeTable, valueTable, metadataValueTable, body, paramAttrList, state.currentbb, state);
				}
			}
		}
		
		if (state.currentbb != null)
			throw new RuntimeException("Last instruction was not a terminator");
		
		while (valueTable.size() > originalValueCount)
			valueTable.remove(valueTable.size()-1);
		if (!LLVM2_7MetadataDetected) {
			while (metadataValueTable.size() > originalMetadataValueCount)
				metadataValueTable.remove(metadataValueTable.size()-1);
		}

		body.setStart(body.getBlock(0));
		return body;
	}
	
	
	private static void decodeInstruction(
			DataRecord record, 
			Type[] typeTable, 
			List<Value> valueTable,
			List<Value> metadataValueTable,
			FunctionBody body, 
			List<ParameterAttributeMap> paramAttrList,
			BasicBlock currentbb,
			State state) {
		
		final Type returnType = body.getHeader().getType().getPointeeType().getFunctionSelf().getReturnType();
		
		switch (record.getCode()) {
		case FunctionBlock2_8.FUNC_CODE_INST_BINOP: {
			debug("Saw binop instruction");
			Int index = new Int(0);
			Value lhs = getValueTypePair(record, index, typeTable, valueTable);
			Value rhs = getValue(record, index, lhs.getType(), valueTable);
			int binopCode = (int)record.getOp(index.value++).getNumericValue();
			Binop binop = Binop.decodeBinop(binopCode, lhs.getType());
			
			if (index.value < record.getNumOps()) {
				long flags = record.getOp(index.value++).getNumericValue();
				switch (binop) {
				case Add:
					if ((flags & (1L<<OBO_NO_UNSIGNED_WRAP)) != 0L) {
						if ((flags & (1L<<OBO_NO_SIGNED_WRAP)) != 0L) {
							binop = Binop.AddNswNuw;
						} else {
							binop = Binop.AddNuw;
						}
					} else if ((flags & (1L<<OBO_NO_SIGNED_WRAP)) != 0L) {
						binop = Binop.AddNsw;
					}
					break;
					
				case Sub:
					if ((flags & (1L<<OBO_NO_UNSIGNED_WRAP)) != 0L) {
						if ((flags & (1L<<OBO_NO_SIGNED_WRAP)) != 0L) {
							binop = Binop.SubNswNuw;
						} else {
							binop = Binop.SubNuw;
						}
					} else if ((flags & (1L<<OBO_NO_SIGNED_WRAP)) != 0L) {
						binop = Binop.SubNsw;
					}
					break;
				
				case Mul:
					if ((flags & (1L<<OBO_NO_UNSIGNED_WRAP)) != 0L) {
						if ((flags & (1L<<OBO_NO_SIGNED_WRAP)) != 0L) {
							binop = Binop.MulNswNuw;
						} else {
							binop = Binop.MulNuw;
						}
					} else if ((flags & (1L<<OBO_NO_SIGNED_WRAP)) != 0L) {
						binop = Binop.MulNsw;
					}
					break;
					
				case SDiv:
					if ((flags & (1L<<SDIV_EXACT)) != 0L) {
						binop = Binop.SDivExact;
					}
					break;
					
				default:
					throw new RuntimeException("Cannot have flags for other binops");
				}
			}
			
			state.addInstruction(new BinopInstruction(binop, lhs, rhs));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_CAST: {
			debug("Saw cast instruction");
			Int index = new Int(0);
			Value lhs = getValueTypePair(record, index, typeTable, valueTable);
			Type destType = typeTable[(int)record.getOp(index.value++).getNumericValue()];
			int castOpcode = (int)record.getOp(index.value++).getNumericValue();
			Cast cast = Cast.decodeCast(castOpcode);

			state.addInstruction(new CastInstruction(cast, destType, lhs));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_GEP: {
			debug("Saw gep instruction");
			Int index = new Int(0);
			Value baseValue = getValueTypePair(record, index, typeTable, valueTable);
			List<Value> indexes = new ArrayList<Value>(record.getNumOps());

			while (index.value < record.getNumOps()) {
				Value indexValue = getValueTypePair(record, index, typeTable, valueTable);
				indexes.add(indexValue);
			}
			
			state.addInstruction(new GEPInstruction(baseValue, indexes));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_SELECT: {
			debug("Saw select instruction");
			Int index = new Int(0);
			Value trueValue = getValueTypePair(record, index, typeTable, valueTable);
			Value falseValue = getValue(record, index, trueValue.getType(), valueTable);
			Value condition = getValue(record, index, Type.getIntegerType(1), valueTable);
			
			state.addInstruction(new SelectInstruction(condition, trueValue, falseValue));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_EXTRACTELT: {
			debug("Saw extractelt instruction");
			Int index = new Int(0);
			Value vector = getValueTypePair(record, index, typeTable, valueTable);
			Value indexValue = getValue(record, index, Type.getIntegerType(32), valueTable);
			
			state.addInstruction(new ExtractEltInstruction(vector, indexValue));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_INSERTELT: {
			debug("Saw insertelt instruction");
			Int index = new Int(0);
			Value vector = getValueTypePair(record, index, typeTable, valueTable);
			if (!(vector.getType().isComposite() && vector.getType().getCompositeSelf().isVector()))
				throw new RuntimeException("Vector doesn't have vector type");
			Type elementType = vector.getType().getCompositeSelf().getVectorSelf().getElementType();
			Value element = getValue(record, index, elementType, valueTable);
			Value indexValue = getValue(record, index, Type.getIntegerType(32), valueTable);
			
			state.addInstruction(new InsertEltInstruction(vector, element, indexValue));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_SHUFFLEVEC: {
			debug("Saw shufflevec instruction");
			Int index = new Int(0);
			Value vector1 = getValueTypePair(record, index, typeTable, valueTable);
			if (!(vector1.getType().isComposite() && vector1.getType().getCompositeSelf().isVector()))
				throw new RuntimeException("Vector has wrong type");
			Value vector2 = getValue(record, index, vector1.getType(), valueTable);
			Value mask = getValueTypePair(record, index, typeTable, valueTable);
			
			state.addInstruction(new ShuffleVec2_8Instruction(vector1, vector2, mask));
			return;
		}

		case FunctionBlock2_8.FUNC_CODE_INST_CMP2:
		case FunctionBlock2_8.FUNC_CODE_INST_CMP: {
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
			
			state.addInstruction(new CmpInstruction(predicate, lhs, rhs));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_RET: {
			debug("Saw ret instruction");
			if (record.getNumOps() == 0) { 
				state.addInstruction(new RetInstruction());
				return;
			}
			
			Int index = new Int(0);
			List<Value> returnValues = new ArrayList<Value>(record.getNumOps());
			while (index.value < record.getNumOps()) {
				Value ret = getValueTypePair(record, index, typeTable, valueTable);
				returnValues.add(ret);
			}

			if (returnValues.size() > 1) {
				Value RV = new UndefValue(returnType);
				for (int i = 0; i < returnValues.size(); i++) {
					RV = state.addInstruction(new InsertValueInstruction(RV, returnValues.get(i), Arrays.asList(i)));
				}
				RetInstruction result = new RetInstruction(Arrays.asList(RV));
				if (!returnType.equalsType(result.getReturnValueType()))
					throw new RuntimeException("Ret output type does not match function return type: " + returnType + ", " + result.getReturnValueType());
				state.addInstruction(result);
				return;
			} else {
				state.addInstruction(new RetInstruction(Arrays.asList(returnValues.get(0))));
				return;
			}
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_BR: {
			debug("Saw br instruction");
			BasicBlock trueBlock = body.getBlock((int)record.getOp(0).getNumericValue());
			if (record.getNumOps() == 1) {
				state.addInstruction(new BrInstruction(trueBlock));
				return;
			}
			BasicBlock falseBlock = body.getBlock((int)record.getOp(1).getNumericValue());
			Value condition = getValue(record, new Int(2), Type.BOOLEAN_TYPE, valueTable);
			
			state.addInstruction(new BrInstruction(condition, trueBlock, falseBlock));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_SWITCH: {
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
			
			state.addInstruction(new SwitchInstruction(condvalue, defaultBlock, caseMap));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_INVOKE: {
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
				Type paramType = funcType.getParamType(i);
				if (paramType.isMetadata()) {
					Value param = getValue(record, index, paramType, metadataValueTable);
					actuals.add(param);
				} else {
					Value param = getValue(record, index, paramType, valueTable);
					actuals.add(param);
				}
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
			
			state.addInstruction(new InvokeInstruction(cc, callee, paramAttrs, returnBlock, unwindBlock, actuals));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_UNWIND: {
			debug("Saw unwind instruction");
			state.addInstruction(UnwindInstruction.INSTANCE);
			return;
		}
			
		case FunctionBlock2_8.FUNC_CODE_INST_UNREACHABLE: {
			debug("Saw unreachable instruction");
			state.addInstruction(UnreachableInstruction.INSTANCE);
			return;
		}
			
		case FunctionBlock2_8.FUNC_CODE_INST_PHI: {
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

			state.addInstruction(new PhiInstruction(type, pairs));
			return;
		}
			
		case FunctionBlock2_8.FUNC_CODE_INST_MALLOC: {
			debug("Saw malloc instruction");
			Type type = typeTable[(int)record.getOp(0).getNumericValue()];
			if (!(type.isComposite() && type.getCompositeSelf().isPointer()))
				throw new RuntimeException("Type must be a pointer");
			if (!type.getCompositeSelf().getPointerSelf().getPointeeType().hasTypeSize())
				throw new RuntimeException("Pointee type must be sized");
			Value numElementsValue = getValue(record, new Int(1), Type.getIntegerType(32), valueTable);
			int alignment = (int)record.getOp(2).getNumericValue();
			
			state.addInstruction(new MallocInstruction(type.getCompositeSelf().getPointerSelf().getPointeeType(), numElementsValue, (1<<alignment)>>1));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_FREE: {
			debug("Saw free instruction");
			Value value = getValueTypePair(record, new Int(0), typeTable, valueTable);
			
			state.addInstruction(new FreeInstruction(value));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_ALLOCA: {
			debug("Saw alloca instruction");
			Int index = new Int(0);
			Type type = typeTable[(int)record.getOp(index.value++).getNumericValue()];
			if (!(type.isComposite() && type.getCompositeSelf().isPointer()))
				throw new RuntimeException("Type must be a pointer");
			if (!type.getCompositeSelf().getPointerSelf().getPointeeType().hasTypeSize())
				throw new RuntimeException("Pointee type must be sized");
			Type optype = (record.getNumOps() == 4) ? 
					typeTable[(int)record.getOp(index.value++).getNumericValue()] :
					Type.getIntegerType(32);
			
			Value numElementsValue = getValue(record, index, optype, valueTable);
			int alignment = (int)record.getOp(index.value++).getNumericValue();
			
			state.addInstruction(new AllocaInstruction(type.getCompositeSelf().getPointerSelf().getPointeeType(), numElementsValue, (1<<alignment)>>1));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_LOAD: {
			debug("Saw load instruction");
			Int index = new Int(0);
			Value loadee = getValueTypePair(record, index, typeTable, valueTable);
			int alignment = (int)record.getOp(index.value++).getNumericValue();
			boolean isVolatile = (record.getOp(index.value++).getNumericValue() != 0L);

			state.addInstruction(new LoadInstruction(loadee, (1<<alignment)>>1, isVolatile));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_STORE: {
			debug("Saw store instruction");
			Int index = new Int(0);
			Value value = getValueTypePair(record, index, typeTable, valueTable);
			PointerType ptrType = new PointerType(value.getType());
			Value address = getValue(record, index, ptrType, valueTable);
			int alignment = (int)record.getOp(index.value++).getNumericValue();
			boolean isVolatile = (record.getOp(index.value++).getNumericValue() != 0L);
			
			state.addInstruction(new StoreInstruction(address, value, (1<<alignment)>>1, isVolatile));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_CALL2:
		case FunctionBlock2_8.FUNC_CODE_INST_CALL: {
			debug("Saw call instruction");
			// [paramattr, cc, (fntype/fnvalue), arg0, arg1...]
			Int index = new Int(0);
			int paramAttrIndex = (int)record.getOp(index.value++).getNumericValue();
			ParameterAttributeMap paramAttrs;
			if (paramAttrIndex == 0)
				paramAttrs = new ParameterAttributeMap();
			else
				paramAttrs = paramAttrList.get(paramAttrIndex-1);
			int ccinfo = (int)record.getOp(index.value++).getNumericValue();
			
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
				Type paramType = funcType.getParamType(i);
				debug("Actual type = " + paramType);
				if (paramType.isLabel()) {
					debug("Block param");
					int bbindex = (int)record.getOp(index.value++).getNumericValue();
					actuals.add(new LabelValue(body.getBlock(bbindex)));
				} else if (paramType.isMetadata()) {
					Value value = getValue(record, index, paramType, metadataValueTable);
					actuals.add(value);
					debug("Metadata actual param = " + value);
				} else {
					Value value = getValue(record, index, paramType, valueTable);
					actuals.add(value);
					debug("Actual param = " + value);
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
			
			state.addInstruction(new CallInstruction(tailCall, cc, function, paramAttrs, actuals));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_VAARG: {
			debug("Saw vaarg instruction");
			Type listType = typeTable[(int)record.getOp(0).getNumericValue()];
			Value listValue = getValue(record, new Int(1), listType, valueTable);
			Type resultType = typeTable[(int)record.getOp(2).getNumericValue()];
			
			state.addInstruction(new VaargInstruction(listValue, resultType));
			return;
		}

		case FunctionBlock2_8.FUNC_CODE_INST_STORE2: {
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
			
			state.addInstruction(new StoreInstruction(address, value, (1<<alignment)>>1, isVolatile));
			return;
		}
			
		case FunctionBlock2_8.FUNC_CODE_INST_GETRESULT: {
			debug("Saw getresult instruction");
			Int index = new Int(0);
			Value value = getValueTypePair(record, index, typeTable, valueTable);
			int grIndex = (int)record.getOp(index.value++).getNumericValue();
			
			state.addInstruction(new GetResultInstruction(value, grIndex));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_EXTRACTVAL: {
			debug("Saw extractvalue instruction");
			Int index = new Int(0);
			Value agg = getValueTypePair(record, index, typeTable, valueTable);
			List<Integer> indexes = new ArrayList<Integer>();
			while (index.value < record.getNumOps()) {
				indexes.add((int)record.getOp(index.value++).getNumericValue());
			}
			
			state.addInstruction(new ExtractValueInstruction(agg, indexes));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_INSERTVAL: {
			debug("Saw insertvalue instruction");
			Int index = new Int(0);
			Value agg = getValueTypePair(record, index, typeTable, valueTable);
			Value elt = getValueTypePair(record, index, typeTable, valueTable);
			List<Integer> indexes = new ArrayList<Integer>();
			while (index.value < record.getNumOps()) {
				indexes.add((int)record.getOp(index.value++).getNumericValue());
			}
			
			state.addInstruction(new InsertValueInstruction(agg, elt, indexes));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_VSELECT: {
			debug("Saw vselect instruction");
			Int index = new Int(0);
			Value trueValue = getValueTypePair(record, index, typeTable, valueTable);
			Value falseValue = getValue(record, index, trueValue.getType(), valueTable);
			Value cond = getValueTypePair(record, index, typeTable, valueTable);
			
			state.addInstruction(new VSelectInstruction(cond, trueValue, falseValue));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_INBOUNDS_GEP: {
			debug("Saw inbounds gep instruction");
			Int index = new Int(0);
			Value baseValue = getValueTypePair(record, index, typeTable, valueTable);
			List<Value> indexes = new ArrayList<Value>(record.getNumOps());
			while (index.value < record.getNumOps()) {
				Value indexValue = getValueTypePair(record, index, typeTable, valueTable);
				indexes.add(indexValue);
			}
			
			state.addInstruction(new InboundsGEPInstruction(baseValue, indexes));
			return;
		}
		
		case FunctionBlock2_8.FUNC_CODE_INST_INDIRECTBR: {
			debug("Saw indirectbr instruction");
			Int index = new Int(0);
			Type addressType = typeTable[(int)record.getOp(index.value++).getNumericValue()];
			Value address = getValue(record, index, addressType, valueTable);
			List<BasicBlock> dests = new ArrayList<BasicBlock>();
			while (index.value < record.getNumOps()) {
				BasicBlock dest = body.getBlock((int)record.getOp(index.value++).getNumericValue());
				dests.add(dest);
			}
			
			state.addInstruction(new IndirectBRInstruction(address, dests));
			return;
		}
		
		default:
			throw new RuntimeException("Unknown instruction record: " + record);
		}
	}
	
	private static DebugLocation decodeDebugLocation(
			DataRecord record,
			List<Value> metadataValueTable) {
		switch (record.getCode()) {
		case FunctionBlock2_8.FUNC_CODE_DEBUG_LOC2:
		case FunctionBlock2_8.FUNC_CODE_DEBUG_LOC: {
			debug("Saw debugloc");
			Int index = new Int(0);
			long line = record.getOp(index.value++).getNumericValue();
			long column = record.getOp(index.value++).getNumericValue();
			
			// scope index may be 0, implies no scope
			Value scope;
			if (record.getOp(index.value).getNumericValue() == 0L) {
				scope = null;
			} else {
				scope = getValue(record.getOp(index.value).getNumericValue()-1L, Type.METADATA_TYPE, metadataValueTable);
				scope.ensureMetadata();
			}
			index.value++;
			
			// IA index may be 0, implies no IA
			Value IA;
			if (record.getOp(index.value).getNumericValue() == 0L) {
				IA = null;
			} else {
				IA = getValue(record.getOp(index.value).getNumericValue()-1L, Type.METADATA_TYPE, metadataValueTable);
				IA.ensureMetadata();
			}
			index.value++;
			
			debug("Scope = " + scope);
			debug("IA = " + IA);
			return new DebugLocation((int)line, (int)column, scope, IA);
		}
		default:
			throw new IllegalArgumentException("Expecting FUNC_CODE_DEBUG_LOC{2}");
		}
	}
	
	private static void setBlockNames(FunctionBody body, ValueSymtabBlock block) {
		for (int i = 0; i < block.getNumBlockContents(); i++) {
			DataRecord record = block.getBlockContents(i).getDataRecordSelf();
			if (record.getCode() == ValueSymtabBlock.VST_CODE_BBENTRY) {
				int bbIndex = (int)record.getOp(0).getNumericValue();
				if (bbIndex >= body.getNumBlocks())
					throw new RuntimeException("VST_CODE_BBENTRY has invalid index");

				String name = ModuleDecoder2_8.recordToString(record, 1, record.getNumOps()-1);
				BasicBlock bb = body.getBlock(bbIndex);
				bb.setName(name);
			}
		}
	}

	public static Value getValue(DataRecord record, Int index, Type type, List<Value> valueList) {
		int valueIndex = (int)record.getOp(index.value++).getNumericValue();
		return getValue(valueIndex, type, valueList);
	}
	public static Value getValue(long index, Type type, List<Value> valueList) {
		int valueIndex = (int)index;
		while (valueIndex >= valueList.size())
			valueList.add(new HolderValue());
		
		Value result = valueList.get(valueIndex);
		if (result.isHolder()) {
			result.getHolderSelf().setHolderType(type);
		} else if (!result.getType().equalsType(type)) {
			throw new IllegalArgumentException("Value has wrong type");
		}
		
		return result;
	}
	
	public static Value getValueTypePair(DataRecord record, Int index, Type[] typeTable, List<Value> valueList) {
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
