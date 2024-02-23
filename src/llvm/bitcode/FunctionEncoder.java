package llvm.bitcode;

import java.util.*;

import llvm.instructions.*;
import llvm.instructions.BasicBlock.Handle;
import llvm.types.FunctionType;
import llvm.types.Type;
import llvm.values.*;
import util.pair.Pair;

/**
 * This class is responsible for writing out an LLVM 2.3 FunctionBlock based
 * on the contents of a Module instance. This class will also write out the
 * local symbol table.
 */
public class FunctionEncoder {
	protected FunctionBody body;
	protected Module module;
	protected BitcodeWriter writer;
	protected List<ParameterAttributeMap> paramAttrs;
	
	public FunctionEncoder(BitcodeWriter _writer, Module _module, FunctionBody _body, List<ParameterAttributeMap> _paramAttrs) {
		this.body = _body;
		this.writer = _writer;
		this.module = _module;
		this.paramAttrs = _paramAttrs;
	}
	
	public void encodeFunction(int abbrevLength, final HashList<Type> typeTable, HashList<Value> valueTable) {
		// make the local value table
		final HashList<Value> newValueTable = new WrapperHashList<Value>(valueTable);
		FunctionValue header = body.getHeader();
		
		final String funcname = module.lookupValueName(header);
		
		// add arguments to value table
		for (int i = 0; i < header.getNumArguments(); i++) {
			newValueTable.add(header.getArgument(i));
		}
		
		// emit FunctionBlock header
		final int innerAbbrevLength = 5;
		int patch = writer.writeEnterSubblock(abbrevLength, new EnterSubblock(FunctionBlock.FUNCTION_BLOCK_ID, innerAbbrevLength, 0));

		// emit FUNC_CODE_DECLAREBLOCKS record
		writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_DECLAREBLOCKS, this.body.getNumBlocks()));

		// order the instructions and blocks
		final Map<Handle,Integer> instructionValueMap = 
			new IdentityHashMap<Handle,Integer>();
		final Map<BasicBlock,Integer> bbMap = 
			new HashMap<BasicBlock,Integer>();
		BasicBlock start = this.body.getStart();
		bbMap.put(start, bbMap.size());
		for (int j = 0; j < start.getNumInstructions(); j++) {
			Handle inst = start.getHandle(j);
			if (!inst.getInstruction().getType().isVoid())
				instructionValueMap.put(inst, instructionValueMap.size());
		}
		for (int i = 0; i < this.body.getNumBlocks(); i++) {
			BasicBlock bb = this.body.getBlock(i);
			if (bb == start) continue;
			bbMap.put(bb, bbMap.size());
			for (int j = 0; j < bb.getNumInstructions(); j++) {
				Handle inst = bb.getHandle(j);
				if (!inst.getInstruction().getType().isVoid())
					instructionValueMap.put(inst, instructionValueMap.size());
			}
		}
		
		ValueEncoder encoder = new ValueEncoder() {
			public void encodeValueTypePair(int valueTableSize, Value value, List<Long> ops) {
				int valueIndex = getValueIndex(value);
				ops.add(new Long(valueIndex));
				
				if (valueIndex >= valueTableSize)
					ops.add(new Long(typeTable.getIndex(value.getType())));
			}
			public void encodeValue(Value value, List<Long> ops) {
				int valueIndex = getValueIndex(value);
				ops.add(new Long(valueIndex));
			}
			public void encodeType(Type type, List<Long> ops) {
				if (!typeTable.hasValue(type))
					throw new IllegalArgumentException("Type not found: "  +type);
				ops.add(new Long(typeTable.getIndex(type)));
			}
			private int getValueIndex(Value value) {
				int valueIndex;
				if (value.isRegister()) {
					Handle assigned = body.getRegisterAssignment().getHandle(value.getRegisterSelf());
					if (!instructionValueMap.containsKey(assigned))
						throw new IllegalArgumentException("Virtual register not assigned: " + value);
					valueIndex = newValueTable.size() + instructionValueMap.get(assigned);
				} else if (newValueTable.hasValue(value)) {
					valueIndex = newValueTable.getIndex(value);
				} else {
					throw new IllegalArgumentException("Value has no index: " + value + " in function " + funcname);
				}
				return valueIndex;
			}
			public void encodeBB(BasicBlock bb, List<Long> ops) {
				if (!bbMap.containsKey(bb))
					throw new IllegalArgumentException("BB has no index: " + bb);
				ops.add(new Long(bbMap.get(bb)));
			}
		};
		
		// emit the instructions
		int valueTableSize = newValueTable.size();
		for (int j = 0; j < start.getNumInstructions(); j++) {
			Instruction inst = start.getInstruction(j);
			encodeInstruction(valueTableSize, inst, encoder, innerAbbrevLength);
			if (!inst.getType().isVoid())
				valueTableSize++;
		}
		for (int i = 0; i < this.body.getNumBlocks(); i++) {
			BasicBlock bb = this.body.getBlock(i);
			if (bb == start) continue;
			for (int j = 0; j < bb.getNumInstructions(); j++) {
				Instruction inst = bb.getInstruction(j);
				encodeInstruction(valueTableSize, inst, encoder, innerAbbrevLength);
				if (!inst.getType().isVoid())
					valueTableSize++;
			}
		}

		// emit local symbol table (for bbs)
		writeBBValueSymbolTable(innerAbbrevLength, encoder);
		
		// close up the block
		writer.writeEndBlock(innerAbbrevLength);
		writer.patchEnterSubblockSize(patch);
	}
	
	
	/**
	 * Writes out the ValueSymtab block
	 */
	private void writeBBValueSymbolTable(int abbrevLength, ValueEncoder encoder) {
		boolean any = false;
		for (int i = 0; i < this.body.getNumBlocks(); i++) {
			if (this.body.getBlock(i).getName() != null) {
				any = true;
				break;
			}
		}

		Map<String,Value> namedValues = new HashMap<String,Value>();
		for (String name : this.body.getValueNames()) {
			Value v = this.body.getValueByName(name);
			if (v.isRegister() || v.isArgument()) {
				namedValues.put(name, v);
				any = true;
			}
		}
		
		if (!any) return;
		
		
		// write block header
		final int innerAbbrevLength = 3;
		int patch = writer.writeEnterSubblock(abbrevLength, new EnterSubblock(ValueSymtabBlock.VALUE_SYMTAB_BLOCK_ID, innerAbbrevLength, 0));
		
		// emit BBs
		List<Long> ops = new ArrayList<Long>();
		if (this.body.getStart().getName() != null) {
			String name = this.body.getStart().getName();
			ops.clear();
			ops.add(0L);
			for (int i = 0; i < name.length(); i++) {
				ops.add(new Long(name.charAt(i)&0xFFL));
			}
			writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ValueSymtabBlock.VST_CODE_BBENTRY, ops));
		}
		int blockIndex = 1;
		for (int j = 0; j < this.body.getNumBlocks(); j++) {
			BasicBlock bb = this.body.getBlock(j);
			if (bb == this.body.getStart()) continue;

			if (bb.getName() != null) {
				String name = bb.getName();
				ops.clear();
				ops.add(new Long(blockIndex));
				for (int i = 0; i < name.length(); i++) {
					ops.add(new Long(name.charAt(i)&0xFFL));
				}
				writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ValueSymtabBlock.VST_CODE_BBENTRY, ops));
			}
			
			blockIndex++;
		}
		
		// emit values
		for (String name : namedValues.keySet()) {
			ops.clear();
			encoder.encodeValue(namedValues.get(name), ops);
			for (int i = 0; i < name.length(); i++) {
				ops.add(new Long(name.charAt(i) & 0xFFL));
			}
			writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ValueSymtabBlock.VST_CODE_ENTRY, ops));
		}
		
		// close up the block
		writer.writeEndBlock(innerAbbrevLength);
		writer.patchEnterSubblockSize(patch);
	}

	/**
	 * Writes out the record version of the given instruction.
	 */
	private void encodeInstruction(
			int valueTableSize, Instruction inst, 
			ValueEncoder encoder, int abbrevLength) {

		List<Long> ops = new ArrayList<Long>();

		if (inst.isBinop()) {
			BinopInstruction binop = inst.getBinopSelf();
			encoder.encodeValueTypePair(valueTableSize, binop.getLHS(), ops);
			encoder.encodeValue(binop.getRHS(), ops);
			ops.add(new Long(binop.getBinop().getValue()));
			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_BINOP, ops));
		} else if (inst.isCast()) {
			CastInstruction cast = inst.getCastSelf();
			encoder.encodeValueTypePair(valueTableSize, cast.getCastee(), ops);
			encoder.encodeType(cast.getDestinationType(), ops);
			ops.add(new Long(cast.getCast().getValue()));
			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_CAST, ops));
		} else if (inst.isShuffleVec()) {
			ShuffleVecInstruction shuffle = inst.getShuffleVecSelf();
			encoder.encodeValueTypePair(valueTableSize, shuffle.getVector1(), ops);
			encoder.encodeValue(shuffle.getVector2(), ops);
			encoder.encodeValue(shuffle.getShuffleVector(), ops);
			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_SHUFFLEVEC, ops));
		} else if (inst.isInsertElt()) {
			InsertEltInstruction insert = inst.getInsertEltSelf();
			encoder.encodeValueTypePair(valueTableSize, insert.getVector(), ops);
			encoder.encodeValue(insert.getElement(), ops);
			encoder.encodeValue(insert.getIndex(), ops);
			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_INSERTELT, ops));
		} else if (inst.isGEP()) {
			GEPInstruction gep = inst.getGEPSelf();
			encoder.encodeValueTypePair(valueTableSize, gep.getBaseValue(), ops);
			for (int i = 0; i < gep.getNumIndexes(); i++) 
				encoder.encodeValueTypePair(valueTableSize, gep.getIndex(i), ops);
			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_GEP, ops));
		} else if (inst.isSelect()) {
			SelectInstruction sel = inst.getSelectSelf();
			encoder.encodeValueTypePair(valueTableSize, sel.getTrueValue(), ops);
			encoder.encodeValue(sel.getFalseValue(), ops);
			encoder.encodeValue(sel.getCondition(), ops);
			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_SELECT, ops));			
		} else if (inst.isExtractElt()) { 
			ExtractEltInstruction extract = inst.getExtractEltSelf();
			encoder.encodeValueTypePair(valueTableSize, extract.getVector(), ops);
			encoder.encodeValue(extract.getIndex(), ops);
			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_EXTRACTELT, ops));			
		} else if (inst.isCmp()) {
			CmpInstruction cmp = inst.getCmpSelf();
			encoder.encodeValueTypePair(valueTableSize, cmp.getLHS(), ops);
			encoder.encodeValue(cmp.getRHS(), ops);
			ops.add(new Long(cmp.getPredicate().getValue()));
			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_CMP, ops));			
		} else if (inst.isPhi()) {
			PhiInstruction phi = inst.getPhiSelf();
			encoder.encodeType(phi.getType(), ops);
			for (int i = 0; i < phi.getNumPairs(); i++) {
				Pair<? extends Value, BasicBlock> pair = phi.getPair(i);
				encoder.encodeValue(pair.getFirst(), ops);
				encoder.encodeBB(pair.getSecond(), ops);
			}
			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_PHI, ops));			
		} else if (inst.isGetResult()) {
			GetResultInstruction get = inst.getGetResultSelf();
			encoder.encodeValueTypePair(valueTableSize, get.getBase(), ops);
			ops.add(new Long(get.getIndex()));
			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_GETRESULT, ops));
		} else if (inst.isMalloc()) {
			MallocInstruction malloc = inst.getMallocSelf();
			encoder.encodeType(malloc.getType(), ops);
			encoder.encodeValue(malloc.getNumElementsValue(), ops);
			ops.add(new Long(ModuleEncoder.translateAlignment(malloc.getAlignment())));
			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_MALLOC, ops));			
		} else if (inst.isFree()) {
			FreeInstruction free = inst.getFreeSelf();
			encoder.encodeValueTypePair(valueTableSize, free.getFreedValue(), ops);
			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_FREE, ops));			
		} else if (inst.isAlloca()) {
			AllocaInstruction alloc = inst.getAllocaSelf();
			encoder.encodeType(alloc.getType(), ops);
			encoder.encodeValue(alloc.getNumElementsValue(), ops);
			ops.add(new Long(ModuleEncoder.translateAlignment(alloc.getAlignment())));
			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_ALLOCA, ops));
		} else if (inst.isLoad()) {
			LoadInstruction load = inst.getLoadSelf();
			encoder.encodeValueTypePair(valueTableSize, load.getLoadee(), ops);
			ops.add(new Long(ModuleEncoder.translateAlignment(load.getAlignment())));
			ops.add(load.isVolatile() ? 1L : 0L);
			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_LOAD, ops));
		} else if (inst.isStore()) {
			StoreInstruction store = inst.getStoreSelf();
			encoder.encodeValueTypePair(valueTableSize, store.getAddress(), ops);
			encoder.encodeValue(store.getValue(), ops);
			ops.add(new Long(ModuleEncoder.translateAlignment(store.getAlignment())));
			ops.add(store.isVolatile() ? 1L : 0L);
			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_STORE2, ops));
		} else if (inst.isCall()) {
			CallInstruction call = inst.getCallSelf();
			
			if (call.getParameterAttributeMap().isEmpty()) 
				ops.add(0L);
			else {
				int paramIndex = this.paramAttrs.indexOf(call.getParameterAttributeMap());
				if (paramIndex < 0) 
					throw new IllegalArgumentException("Unregistered parameter attributes");
				ops.add(new Long(paramIndex+1));
				// param attributes are numbered from 1 (0 = no attributes)
			}
			
			ops.add(new Long(call.getCallingConvention()));
			encoder.encodeValueTypePair(valueTableSize, call.getFunctionPointer(), ops);
			
			FunctionType type = call.getFunctionPointer().getType().getCompositeSelf().getPointerSelf().getPointeeType().getFunctionSelf();
			
			for (int i = 0; i < type.getNumParams(); i++) {
				if (type.getParamType(i).isLabel()) {
					LabelValue label = call.getActual(i).getLabelSelf();
					encoder.encodeBB(label.getBlock(), ops);
				} else {
					encoder.encodeValue(call.getActual(i), ops);
				}
			}
			
			if (type.isVararg()) {
				for (int i = type.getNumParams(); i < call.getNumActuals(); i++) {
					encoder.encodeValueTypePair(valueTableSize, call.getActual(i), ops);
				}
			}

			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_CALL, ops));
		} else if (inst.isVaarg()) {
			VaargInstruction vaarg = inst.getVaargSelf();
			encoder.encodeType(vaarg.getVAList().getType(), ops);
			encoder.encodeValue(vaarg.getVAList(), ops);
			encoder.encodeType(vaarg.getResultType(), ops);
			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_VAARG, ops));
		} else if (inst.isTerminator()) {
			TerminatorInstruction term = inst.getTerminatorSelf();
			if (term.isRet()) {
				RetInstruction ret = term.getRetSelf();
				if (ret.getNumReturnValues() > 0) {
					for (int i = 0; i < ret.getNumReturnValues(); i++) {
						encoder.encodeValueTypePair(valueTableSize, ret.getReturnValue(i), ops);
					}
				}
				writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_RET, ops));
			} else if (term.isBr()) {
				BrInstruction br = term.getBrSelf();
				
				if (br.getCondition() == null) {
					// just trueTarget
					encoder.encodeBB(br.getTrueTarget(), ops);
				} else {
					// trueTarget, falseTarget, conditionValue
					encoder.encodeBB(br.getTrueTarget(), ops);
					encoder.encodeBB(br.getFalseTarget(), ops);
					encoder.encodeValue(br.getCondition(), ops);
				}
				
				writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_BR, ops));
			} else if (term.isSwitch()) {
				SwitchInstruction sw = term.getSwitchSelf();
				
				encoder.encodeType(sw.getInputValue().getType(), ops);
				encoder.encodeValue(sw.getInputValue(), ops);
				encoder.encodeBB(sw.getDefaultTarget(), ops);
				for (int i = 0; i < sw.getNumCaseLabels(); i++) {
					encoder.encodeValue(sw.getCaseLabel(i), ops);
					encoder.encodeBB(sw.getCaseTarget(i), ops);
				}
				writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_SWITCH, ops));
			} else if (term.isInvoke()) {
				InvokeInstruction invoke = term.getInvokeSelf();
				
				if (invoke.getParameterAttributeMap().isEmpty()) 
					ops.add(0L);
				else {
					int paramIndex = this.paramAttrs.indexOf(invoke.getParameterAttributeMap());
					if (paramIndex < 0) 
						throw new IllegalArgumentException("Unregistered parameter attributes");
					ops.add(new Long(paramIndex+1));
					// param attributes are numbered from 1 (0 = no attributes)
				}
				
				ops.add(new Long(invoke.getCallingConvention()));
				encoder.encodeBB(invoke.getReturnBlock(), ops);
				encoder.encodeBB(invoke.getUnwindBlock(), ops);
				encoder.encodeValueTypePair(valueTableSize, invoke.getFunctionPointer(), ops);
				FunctionType type = invoke.getFunctionPointer().getType().getCompositeSelf().getPointerSelf().getPointeeType().getFunctionSelf();
					
				// encode actuals (not the varargs yet)
				for (int i = 0; i < type.getNumParams(); i++) {
					encoder.encodeValue(invoke.getActual(i), ops);
				}
				if (type.isVararg()) {
					for (int i = type.getNumParams(); i < invoke.getNumActuals(); i++) {
						encoder.encodeValueTypePair(valueTableSize, invoke.getActual(i), ops);
					}
				}
				
				writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_INVOKE, ops));
			} else if (term.isUnwind()) {
				writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_UNWIND, ops));
			} else if (term.isUnreachable()) {
				writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock.FUNC_CODE_INST_UNREACHABLE, ops));
			} else {
				throw new RuntimeException("Mike forgot to support " + term);
			}
		} else {
			throw new RuntimeException("Mike forgot to support: " + inst);
		}
	}
	
	private static interface ValueEncoder {
		public void encodeValueTypePair(int myindex, Value value, List<Long> ops);
		public void encodeValue(Value value, List<Long> ops);
		public void encodeType(Type type, List<Long> ops);
		public void encodeBB(BasicBlock bb, List<Long> ops);
	}
}
