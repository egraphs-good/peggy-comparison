package llvm.bitcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import llvm.bitcode.ModuleEncoder2_8.TypeTable;
import llvm.instructions.AllocaInstruction;
import llvm.instructions.BasicBlock;
import llvm.instructions.Binop;
import llvm.instructions.BinopInstruction;
import llvm.instructions.BrInstruction;
import llvm.instructions.CallInstruction;
import llvm.instructions.CastInstruction;
import llvm.instructions.CmpInstruction;
import llvm.instructions.ExtractEltInstruction;
import llvm.instructions.ExtractValueInstruction;
import llvm.instructions.FreeInstruction;
import llvm.instructions.FunctionBody;
import llvm.instructions.GEPInstruction;
import llvm.instructions.GetResultInstruction;
import llvm.instructions.IndirectBRInstruction;
import llvm.instructions.InsertEltInstruction;
import llvm.instructions.InsertValueInstruction;
import llvm.instructions.Instruction;
import llvm.instructions.InvokeInstruction;
import llvm.instructions.LoadInstruction;
import llvm.instructions.MallocInstruction;
import llvm.instructions.PhiInstruction;
import llvm.instructions.RegisterAssignment;
import llvm.instructions.RetInstruction;
import llvm.instructions.SelectInstruction;
import llvm.instructions.ShuffleVec2_8Instruction;
import llvm.instructions.StoreInstruction;
import llvm.instructions.SwitchInstruction;
import llvm.instructions.TerminatorInstruction;
import llvm.instructions.VSelectInstruction;
import llvm.instructions.VaargInstruction;
import llvm.instructions.BasicBlock.Handle;
import llvm.types.FunctionType;
import llvm.types.Type;
import llvm.values.DebugLocation;
import llvm.values.FunctionValue;
import llvm.values.LabelValue;
import llvm.values.Module;
import llvm.values.ParameterAttributeMap;
import llvm.values.Value;
import util.Action;
import util.pair.Pair;

/**
 * This class is responsible for writing an LLVM 2.8 FunctionBlock based on the 
 * contents of a Module instance. This class will also emit the local symbol table
 * and the metadata attachment table.
 */
public class FunctionEncoder2_8 {
	protected static boolean DEBUG = false;
	private static void debug(String msg) {
		if (DEBUG)
			System.err.println("FunctionEncoder2_8: " + msg);
	}
	
	public static final long OBO_NO_UNSIGNED_WRAP = 0;
	public static final long OBO_NO_SIGNED_WRAP = 1;
	public static final long SDIV_EXACT = 0;
	
	protected final FunctionBody body;
	protected final Module module;
	protected final int abbrevLength;
	protected final int innerAbbrevLength = 5;
	protected final TypeTable typeTable;
	protected final BitcodeWriter writer;
	protected final List<ParameterAttributeMap> paramAttrs;
	
	public FunctionEncoder2_8(BitcodeWriter _writer, int _abbrevLength, Module _module, TypeTable _typeTable, FunctionBody _body, List<ParameterAttributeMap> _paramAttrs) {
		this.body = _body;
		this.abbrevLength = _abbrevLength;
		this.writer = _writer;
		this.typeTable = _typeTable;
		this.module = _module;
		this.paramAttrs = _paramAttrs;
	}
	
	private void addFunctionLocalValues(
			HashList<Value> valueTable,
			HashList<Value> metadataValueTable) {
		// add arguments to value table
		FunctionValue header = this.body.getHeader();
		for (int i = 0; i < header.getNumArguments(); i++) {
			Value arg = header.getArgument(i);
			debug("Adding local arg: " + arg);
			valueTable.add(arg);
		}

		// index of start of local constants (after args)
		final int startIndex = valueTable.size();
		final int metadataStartIndex = metadataValueTable.size();
		
		// now do local constants and such
		for (int i = 0; i < this.body.getNumBlocks(); i++) {
			final BasicBlock bb = this.body.getBlock(i);
			for (int j = 0; j < bb.getNumInstructions(); j++) {
				Instruction inst = bb.getInstruction(j);
				if (inst.getDebugLocation() != null) {
					DebugLocation debug = inst.getDebugLocation();
					if (debug.getScope() != null)
						addValue(debug.getScope(), valueTable, metadataValueTable);
					if (debug.getIA() != null)
						addValue(debug.getIA(), valueTable, metadataValueTable);
				}
				for (int kind : inst.getMetadataKinds()) {
					addValue(inst.getMetadata(kind), valueTable, metadataValueTable);
				}
				for (Iterator<? extends Value> iter = inst.getValues(); iter.hasNext(); ) {
					addValue(iter.next(), valueTable, metadataValueTable);
				}
			}
		}
		
		debug("Local constants:");
		for (int i = startIndex; i < valueTable.size(); i++) {
			debug("lc[" + (i-startIndex) + "] = " + valueTable.getValue(i));
		}
		debug("Local metadata:");
		for (int i = metadataStartIndex; i < metadataValueTable.size(); i++) {
			debug("lm[" + (i-metadataStartIndex) + "] = " + metadataValueTable.getValue(i));
		}
		
		// emit local constants block (if necessary)
		if (valueTable.size() > startIndex) {
			// emit local constants
			GlobalConstantEncoder2_8 constantEncoder = new GlobalConstantEncoder2_8(writer, innerAbbrevLength, typeTable);
			constantEncoder.writeConstantsBlock(valueTable, startIndex);
		}
		
		// add regs to the value list
		final RegisterAssignment regs = this.body.getRegisterAssignment();
		for (int b = 0; b < this.body.getNumBlocks(); b++) {
			BasicBlock bb = this.body.getBlock(b);
			for (int i = 0; i < bb.getNumInstructions(); i++) {
				Handle handle = bb.getHandle(i);
				if (regs.isAssigned(handle)) {
					valueTable.add(regs.getRegister(handle));
				}
			}
		}
	}
	// does not add function-local values (regs, labels, args)
	private void addValue(
			Value value, 
			HashList<Value> valueTable,
			HashList<Value> metadataValueTable) {
		if (value.isMetadataNode() || value.isMetadataString()) {
			if (metadataValueTable.hasValue(value))
				return;
			metadataValueTable.add(value);
			for (Iterator<? extends Value> iter = value.getSubvalues(); iter.hasNext(); ) {
				addValue(iter.next(), valueTable, metadataValueTable);
			}
		} else {
			if (valueTable.hasValue(value) || value.isFunctionLocal())
				return;
			for (Iterator<? extends Value> iter = value.getSubvalues(); iter.hasNext(); ) {
				addValue(iter.next(), valueTable, metadataValueTable);
			}
			valueTable.add(value);
		}
	}

	/**
	 * Writes the function block.
	 * This may write out inner blocks, including metadata, constants,
	 * metadataAttachment, etc.
	 */
	public void encodeFunction(
			final HashList<Value> metadataTable,
			final HashList<Value> valueTable) {
		// make the local value table
		final HashList<Value> newValueTable = new WrapperHashList<Value>(valueTable);
		final HashList<Value> newMetadataValueTable = new WrapperHashList<Value>(metadataTable);

		// emit FunctionBlock header
		final int patch = writer.writeEnterSubblock(abbrevLength, new EnterSubblock(FunctionBlock2_8.FUNCTION_BLOCK_ID, innerAbbrevLength, 0));

		// put all the local constants and metadata in the value tables (not instructions)
		// possibly emit local constants block
		addFunctionLocalValues(newValueTable, newMetadataValueTable);
		
		if (newMetadataValueTable.size() > metadataTable.size()) {
			// emit local metadata!
			MetadataEncoder metaEncoder = new MetadataEncoder(writer, innerAbbrevLength, typeTable);
			metaEncoder.writeMetadataBlock(module, metadataTable.size(), newMetadataValueTable, newValueTable);
		}
		
		// emit FUNC_CODE_DECLAREBLOCKS record
		writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_DECLAREBLOCKS, this.body.getNumBlocks()));

		// order the instructions and blocks
		final HashList<Handle> allhandles = new HashList<Handle>();
		final Map<BasicBlock,Integer> bbMap = new HashMap<BasicBlock,Integer>();
		BasicBlock start = this.body.getStart();
		bbMap.put(start, bbMap.size());
		for (int j = 0; j < start.getNumInstructions(); j++) {
			Handle inst = start.getHandle(j);
			allhandles.add(inst);
		}
		for (int i = 0; i < this.body.getNumBlocks(); i++) {
			BasicBlock bb = this.body.getBlock(i);
			if (bb == start) continue;
			bbMap.put(bb, bbMap.size());
			for (int j = 0; j < bb.getNumInstructions(); j++) {
				Handle inst = bb.getHandle(j);
				allhandles.add(inst);
			}
		}
		
		final ValueEncoder encoder = new ValueEncoder() {
			public void encodeValueTypePair(int valueTableSize, Value value, List<Long> ops) {
				int valueIndex = newValueTable.getIndex(value); 
				ops.add(new Long(valueIndex));
				if (valueIndex >= valueTableSize)
					ops.add(new Long(typeTable.getTypeIndex(value.getType())));
			}
			public void encodeValue(Value value, List<Long> ops) {
				int valueIndex = newValueTable.getIndex(value);
				ops.add(new Long(valueIndex));
			}
			public void encodeMetadataValue(Value metadata, List<Long> ops) {
				int metadataValueIndex = newMetadataValueTable.getIndex(metadata);
				ops.add(new Long(metadataValueIndex));
			}
			public void encodeType(Type type, List<Long> ops) {
				ops.add(new Long(typeTable.getTypeIndex(type)));
			}
			public void encodeBB(BasicBlock bb, List<Long> ops) {
				if (!bbMap.containsKey(bb))
					throw new IllegalArgumentException("BB has no index: " + bb);
				ops.add(new Long(bbMap.get(bb)));
			}
			public void encodeInstruction(Handle handle, List<Long> ops) {
				int index = allhandles.getIndex(handle);
				ops.add((long)index);
			}
		};

		Action<Instruction> debugAction = new Action<Instruction>() {
			DebugLocation lastloc = null;
			List<Long> ops = new ArrayList<Long>();
			public void execute(Instruction inst) {
				if (inst.getDebugLocation() != null) {
					DebugLocation debug = inst.getDebugLocation();
					if (lastloc != null && debug.equals(lastloc)) {
						// FUNC_CODE_DEBUG_LOC_AGAIN
						ops.clear();
						writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_DEBUG_LOC_AGAIN, ops));
					} else {
						// FUNC_CODE_DEBUG_LOC2
						ops.clear();
						ops.add((long)debug.getLine());
						ops.add((long)debug.getColumn());
						if (debug.getScope() == null)
							ops.add(0L);
						else
							ops.add((long)newMetadataValueTable.getIndex(debug.getScope())+1L);
						if (debug.getIA() == null)
							ops.add(0L);
						else
							ops.add((long)newMetadataValueTable.getIndex(debug.getIA())+1L);
						writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_DEBUG_LOC2, ops));
					}
					lastloc = debug;
				}
			}
		};
		
		// emit the instructions
		int valueTableSize = newValueTable.size();
		while (newValueTable.getValue(valueTableSize-1).isRegister())
			valueTableSize--;

		for (int j = 0; j < start.getNumInstructions(); j++) {
			Instruction inst = start.getInstruction(j);
			encodeInstruction(writer, valueTableSize, inst, encoder, innerAbbrevLength);
			if (!inst.getType().isVoid())
				valueTableSize++;
			debugAction.execute(inst);
		}

		for (int i = 0; i < this.body.getNumBlocks(); i++) {
			BasicBlock bb = this.body.getBlock(i);
			if (bb == start) continue;
			
			for (int j = 0; j < bb.getNumInstructions(); j++) {
				Instruction inst = bb.getInstruction(j);
				encodeInstruction(writer, valueTableSize, inst, encoder, innerAbbrevLength);
				if (!inst.getType().isVoid())
					valueTableSize++;
				debugAction.execute(inst);
			}
		}

		// emit local symbol table (for bbs)
		writeBBValueSymbolTable(innerAbbrevLength, encoder);
		// one block for all instructions (in this function)
		emitMetadataAttachmentBlock(innerAbbrevLength, encoder, allhandles);

		// close up the block
		writer.writeEndBlock(innerAbbrevLength);
		writer.patchEnterSubblockSize(patch);
	}
	
	private void emitMetadataAttachmentBlock(int abbrevLength, ValueEncoder encoder, HashList<Handle> handles) {
		boolean gotone = false;
		for (Handle handle : handles) {
			if (handle.getInstruction().getMetadataKinds().size() > 0) {
				gotone = true;
				break;
			}
		}

		if (!gotone) return;
		
		final int innerAbbrevLength = 4;
		int patch = writer.writeEnterSubblock(abbrevLength, new EnterSubblock(MetadataAttachmentBlock2_8.METADATA_ATTACHMENT_BLOCK_ID, innerAbbrevLength, 0));
		
		for (Handle handle : handles) {
			Instruction inst = handle.getInstruction();
			Set<Integer> kinds = inst.getMetadataKinds();
			if (kinds.size() > 0) {
				List<Long> ops = new ArrayList<Long>();
				ops.clear();
				encoder.encodeInstruction(handle, ops);
				for (int kind : kinds) {
					ops.add((long)kind);
					encoder.encodeMetadataValue(inst.getMetadata(kind), ops);
				}
				writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(MetadataAttachmentBlock2_8.METADATA_ATTACHMENT2, ops));
			}
		}
		
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
			BitcodeWriter localWriter,
			int valueTableSize, Instruction inst, 
			ValueEncoder encoder, int abbrevLength) {

		List<Long> ops = new ArrayList<Long>();

		if (inst.isBinop()) {
			BinopInstruction binop = inst.getBinopSelf();
			encoder.encodeValueTypePair(valueTableSize, binop.getLHS(), ops);
			encoder.encodeValue(binop.getRHS(), ops);
			switch (binop.getBinop()) {
			case AddNsw:
				ops.add((long)Binop.Add.getValue());
				ops.add(1L<<OBO_NO_SIGNED_WRAP);
				break;
			case AddNuw:
				ops.add((long)Binop.Add.getValue());
				ops.add(1L<<OBO_NO_UNSIGNED_WRAP);
				break;
			case AddNswNuw:
				ops.add((long)Binop.Add.getValue());
				ops.add((1L<<OBO_NO_UNSIGNED_WRAP) | (1L<<OBO_NO_SIGNED_WRAP));
				break;
				
			case SubNsw:
				ops.add((long)Binop.Sub.getValue());
				ops.add(1L<<OBO_NO_SIGNED_WRAP);
				break;
			case SubNuw:
				ops.add((long)Binop.Sub.getValue());
				ops.add(1L<<OBO_NO_UNSIGNED_WRAP);
				break;
			case SubNswNuw:
				ops.add((long)Binop.Sub.getValue());
				ops.add((1L<<OBO_NO_UNSIGNED_WRAP) | (1L<<OBO_NO_SIGNED_WRAP));
				break;
				
			case MulNsw:
				ops.add((long)Binop.Mul.getValue());
				ops.add(1L<<OBO_NO_SIGNED_WRAP);
				break;
			case MulNuw:
				ops.add((long)Binop.Mul.getValue());
				ops.add(1L<<OBO_NO_UNSIGNED_WRAP);
				break;
			case MulNswNuw:
				ops.add((long)Binop.Mul.getValue());
				ops.add((1L<<OBO_NO_UNSIGNED_WRAP) | (1L<<OBO_NO_SIGNED_WRAP));
				break;
				
			case SDivExact:
				ops.add((long)Binop.SDiv.getValue());
				ops.add(1L<<SDIV_EXACT);
				break;
				
			default:
				ops.add((long)binop.getBinop().getValue());
				break;
			}
			
			localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_BINOP, ops));
		} else if (inst.isCast()) {
			CastInstruction cast = inst.getCastSelf();
			encoder.encodeValueTypePair(valueTableSize, cast.getCastee(), ops);
			encoder.encodeType(cast.getDestinationType(), ops);
			ops.add(new Long(cast.getCast().getValue()));
			localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_CAST, ops));
		} else if (inst.isShuffleVec2_8()) {
			// 2.8 does not use ShuffleVecInstruction
			ShuffleVec2_8Instruction shuffle = inst.getShuffleVec2_8Self();
			encoder.encodeValueTypePair(valueTableSize, shuffle.getOperand1(), ops);
			encoder.encodeValue(shuffle.getOperand2(), ops);
			encoder.encodeValueTypePair(valueTableSize, shuffle.getMask(), ops);
			localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_SHUFFLEVEC, ops));
		} else if (inst.isInsertElt()) {
			InsertEltInstruction insert = inst.getInsertEltSelf();
			encoder.encodeValueTypePair(valueTableSize, insert.getVector(), ops);
			encoder.encodeValue(insert.getElement(), ops);
			encoder.encodeValue(insert.getIndex(), ops);
			localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_INSERTELT, ops));
		} else if (inst.isGEP()) {
			GEPInstruction gep = inst.getGEPSelf();
			if (gep.isInbounds()) {
				encoder.encodeValueTypePair(valueTableSize, gep.getBaseValue(), ops);
				for (int i = 0; i < gep.getNumIndexes(); i++) 
					encoder.encodeValueTypePair(valueTableSize, gep.getIndex(i), ops);
				localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_INBOUNDS_GEP, ops));
			} else {
				encoder.encodeValueTypePair(valueTableSize, gep.getBaseValue(), ops);
				for (int i = 0; i < gep.getNumIndexes(); i++) 
					encoder.encodeValueTypePair(valueTableSize, gep.getIndex(i), ops);
				localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_GEP, ops));
			}
		} else if (inst.isSelect()) {
			SelectInstruction sel = inst.getSelectSelf();
			encoder.encodeValueTypePair(valueTableSize, sel.getTrueValue(), ops);
			encoder.encodeValue(sel.getFalseValue(), ops);
			encoder.encodeValue(sel.getCondition(), ops);
			localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_SELECT, ops));			
		} else if (inst.isExtractElt()) { 
			ExtractEltInstruction extract = inst.getExtractEltSelf();
			encoder.encodeValueTypePair(valueTableSize, extract.getVector(), ops);
			encoder.encodeValue(extract.getIndex(), ops);
			localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_EXTRACTELT, ops));			
		} else if (inst.isCmp()) {
			CmpInstruction cmp = inst.getCmpSelf();
			encoder.encodeValueTypePair(valueTableSize, cmp.getLHS(), ops);
			encoder.encodeValue(cmp.getRHS(), ops);
			ops.add(new Long(cmp.getPredicate().getValue()));
			localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_CMP, ops));			
		} else if (inst.isPhi()) {
			PhiInstruction phi = inst.getPhiSelf();
			encoder.encodeType(phi.getType(), ops);
			for (int i = 0; i < phi.getNumPairs(); i++) {
				Pair<? extends Value, BasicBlock> pair = phi.getPair(i);
				encoder.encodeValue(pair.getFirst(), ops);
				encoder.encodeBB(pair.getSecond(), ops);
			}
			localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_PHI, ops));			
		} else if (inst.isGetResult()) {
			GetResultInstruction get = inst.getGetResultSelf();
			encoder.encodeValueTypePair(valueTableSize, get.getBase(), ops);
			ops.add(new Long(get.getIndex()));
			localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_GETRESULT, ops));
		} else if (inst.isMalloc()) {
			MallocInstruction malloc = inst.getMallocSelf();
			encoder.encodeType(malloc.getType(), ops);
			encoder.encodeValue(malloc.getNumElementsValue(), ops);
			ops.add(new Long(ModuleEncoder.translateAlignment(malloc.getAlignment())));
			localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_MALLOC, ops));			
		} else if (inst.isFree()) {
			FreeInstruction free = inst.getFreeSelf();
			encoder.encodeValueTypePair(valueTableSize, free.getFreedValue(), ops);
			localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_FREE, ops));			
		} else if (inst.isAlloca()) {
			AllocaInstruction alloc = inst.getAllocaSelf();
			encoder.encodeType(alloc.getType(), ops);
			if (!alloc.getNumElementsValue().getType().equalsType(Type.getIntegerType(32))) {
				encoder.encodeType(alloc.getNumElementsValue().getType(), ops);
				encoder.encodeValue(alloc.getNumElementsValue(), ops);
			} else {
				encoder.encodeValue(alloc.getNumElementsValue(), ops);
			}
			ops.add(new Long(ModuleEncoder2_8.translateAlignment(alloc.getAlignment())));
			localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_ALLOCA, ops));
		} else if (inst.isLoad()) {
			LoadInstruction load = inst.getLoadSelf();
			encoder.encodeValueTypePair(valueTableSize, load.getLoadee(), ops);
			ops.add(new Long(ModuleEncoder.translateAlignment(load.getAlignment())));
			ops.add(load.isVolatile() ? 1L : 0L);
			localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_LOAD, ops));
		} else if (inst.isStore()) {
			StoreInstruction store = inst.getStoreSelf();
			encoder.encodeValueTypePair(valueTableSize, store.getAddress(), ops);
			encoder.encodeValue(store.getValue(), ops);
			ops.add(new Long(ModuleEncoder.translateAlignment(store.getAlignment())));
			ops.add(store.isVolatile() ? 1L : 0L);
			localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_STORE2, ops));
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
				} else if (type.getParamType(i).isMetadata()) {
					encoder.encodeMetadataValue(call.getActual(i), ops);
				} else {
					encoder.encodeValue(call.getActual(i), ops);
				}
			}
			
			if (type.isVararg()) {
				for (int i = type.getNumParams(); i < call.getNumActuals(); i++) {
					encoder.encodeValueTypePair(valueTableSize, call.getActual(i), ops);
				}
			}

			localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_CALL2, ops));
		} else if (inst.isVaarg()) {
			VaargInstruction vaarg = inst.getVaargSelf();
			encoder.encodeType(vaarg.getVAList().getType(), ops);
			encoder.encodeValue(vaarg.getVAList(), ops);
			encoder.encodeType(vaarg.getResultType(), ops);
			localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_VAARG, ops));
		} else if (inst.isInsertValue()) {
			InsertValueInstruction insert = inst.getInsertValueSelf();
			encoder.encodeValueTypePair(valueTableSize, insert.getAggregate(), ops);
			encoder.encodeValueTypePair(valueTableSize, insert.getElement(), ops);
			for (int index : insert.getIndexes()) {
				ops.add((long)index);
			}
			localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_INSERTVAL, ops));
		} else if (inst.isExtractValue()) {
			ExtractValueInstruction extract = inst.getExtractValueSelf();
			encoder.encodeValueTypePair(valueTableSize, extract.getAggregate(), ops);
			for (int index : extract.getIndexes()) {
				ops.add((long)index);
			}
			localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_EXTRACTVAL, ops));
		} else if (inst.isVSelect()) {
			VSelectInstruction select = inst.getVSelectSelf();
			encoder.encodeValueTypePair(valueTableSize, select.getTrueValue(), ops);
			encoder.encodeValue(select.getFalseValue(), ops);
			encoder.encodeValueTypePair(valueTableSize, select.getCondition(), ops);
			localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_VSELECT, ops));
		} else if (inst.isTerminator()) {
			TerminatorInstruction term = inst.getTerminatorSelf();
			if (term.isRet()) {
				RetInstruction ret = term.getRetSelf();
				if (ret.getNumReturnValues() > 0) {
					for (int i = 0; i < ret.getNumReturnValues(); i++) {
						encoder.encodeValueTypePair(valueTableSize, ret.getReturnValue(i), ops);
					}
				}
				localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_RET, ops));
			} else if (term.isIndirectBR()) {
				IndirectBRInstruction indirect = term.getIndirectBRSelf();
				encoder.encodeType(indirect.getAddress().getType(), ops);
				encoder.encodeValue(indirect.getAddress(), ops);
				for (BasicBlock bb : indirect.getDestinations()) {
					encoder.encodeBB(bb, ops);
				}
				localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_INDIRECTBR, ops));
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
				
				localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_BR, ops));
			} else if (term.isSwitch()) {
				SwitchInstruction sw = term.getSwitchSelf();
				
				encoder.encodeType(sw.getInputValue().getType(), ops);
				encoder.encodeValue(sw.getInputValue(), ops);
				encoder.encodeBB(sw.getDefaultTarget(), ops);
				for (int i = 0; i < sw.getNumCaseLabels(); i++) {
					encoder.encodeValue(sw.getCaseLabel(i), ops);
					encoder.encodeBB(sw.getCaseTarget(i), ops);
				}
				localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_SWITCH, ops));
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
					Value actual = invoke.getActual(i);
					if (actual.getType().isMetadata()) {
						encoder.encodeMetadataValue(actual, ops);
					} else {
						encoder.encodeValue(actual, ops);
					}
				}
				if (type.isVararg()) {
					for (int i = type.getNumParams(); i < invoke.getNumActuals(); i++) {
						encoder.encodeValueTypePair(valueTableSize, invoke.getActual(i), ops);
					}
				}
				
				localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_INVOKE, ops));
			} else if (term.isUnwind()) {
				localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_UNWIND, ops));
			} else if (term.isUnreachable()) {
				localWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(FunctionBlock2_8.FUNC_CODE_INST_UNREACHABLE, ops));
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
		public void encodeInstruction(Handle handle, List<Long> ops);
		public void encodeMetadataValue(Value metadata, List<Long> ops);
	}
}
