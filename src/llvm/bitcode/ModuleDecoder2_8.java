package llvm.bitcode;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import llvm.types.Type;
import llvm.values.BlockAddressValue;
import llvm.values.FunctionValue;
import llvm.values.Module;
import llvm.values.NamedValueMap;
import llvm.values.ParameterAttributeMap;
import llvm.values.ParameterAttributes;
import llvm.values.Value;

/**
 * This class decodes a Module from a ModuleBlock2_8.
 */
public abstract class ModuleDecoder2_8 {
	protected static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG) 
			System.err.println("ModuleDecoder2_8: " + message);
	}
	
	public static Module decode(ModuleBlock2_8 block, boolean LLVM2_7MetadataDetected) {
		TypeBlock2_8 typeBlock = null;
		ParamAttrBlock paramAttrBlock = null;
		Map<Integer,Integer> mdkindmap = new HashMap<Integer,Integer>();
		Module module = new Module(true);
		Map<FunctionValue,Set<BlockAddressValue>> func2blockaddresses = 
			new HashMap<FunctionValue, Set<BlockAddressValue>>();
		
		// find the TypeBlock and the ParamAttrBlock
		for (int i = 0; i < block.getNumBlockContents(); i++) {
			BlockContents bc = block.getBlockContents(i);
			if (bc.isBlock()) {
				Block subblock = bc.getBlockSelf();
				if (subblock.isType2_8())
					typeBlock = subblock.getType2_8Self();
				else if (subblock.isParamAttr())
					paramAttrBlock = subblock.getParamAttrSelf();
			}
		}
		
		// build the type table
		Type[] typeTable = null;
		if (typeBlock != null) {
			typeTable = TypeTableDecoder2_8.decodeTypeTable(typeBlock);
			if (DEBUG) {
				debug("\nType table:");
				for (int i = 0; i < typeTable.length; i++) {
					debug("type[" + i + "] = " + typeTable[i]);
				}
			}
		}

		debug("Setting type names");
		// assign the type names (from TypeSymtabBlock)
		setTypeNames(typeTable, block, module);
		
		debug("Decoding misc");
		// decode module-level properties
		decodeModuleMisc(module, block);
		
		debug("Decoding param attrs");
		List<ParameterAttributeMap> paramAttrList = decodeParamAttrs(paramAttrBlock);
		
		// decode the value table
		LinkedList<Value> valueTable = GlobalValueDecoder2_8.decodeGlobalValueTable(
				block, 
				module, 
				typeTable, 
				func2blockaddresses, 
				paramAttrList);

		debug("Value List:");
		if (DEBUG) {
			int counter = 0;
			for (Value v : valueTable) {
				debug("value " + counter + " = " + v);
				counter++;
			}
		}
		
		List<Value> metadataValueTable = MetadataDecoder.decodeMetadata(
				LLVM2_7MetadataDetected,
				block,
				module, 
				typeTable, 
				mdkindmap, 
				valueTable); 
		
		debug("Setting value names");
		// decode the value symbol table
		for (int i = 0; i < block.getNumBlockContents(); i++) {
			BlockContents bc = block.getBlockContents(i);
			if (!bc.isBlock())
				continue;
			Block subblock = bc.getBlockSelf();
			if (subblock.isValueSymtab())
				setValueNames(valueTable, subblock.getValueSymtabSelf(), module);
		}
		
		if (DEBUG) {
			debug("Metadata Value list:");
			int counter = 0;
			for (Value v : metadataValueTable) {
				debug("metadata " + (counter++) + " = " + v);
			}
		}
		
		if (DEBUG) {
			debug("Value List:");
			int counter = 0;
			for (Value v : valueTable) {
				debug("value " + counter + " = " + v);
				counter++;
			}
		}
		
		debug("Decoding functions");
		FunctionDecoder2_8.decodeFunctions(
				LLVM2_7MetadataDetected,
				block, 
				module, 
				typeTable, 
				valueTable, 
				metadataValueTable, 
				paramAttrList, 
				func2blockaddresses, 
				mdkindmap);
		
		debug("Done decoding!");
		
		return module;
	}

	/**
	 * Takes every TypeSymtabBlock in the given ModuleBlock and assigns 
	 * the names to the appropriate type table entries.
	 */
	private static void setTypeNames(Type[] typeTable, ModuleBlock2_8 block, Module module) {
		for (int i = 0; i < block.getNumBlockContents(); i++) {
			BlockContents bc = block.getBlockContents(i);
			if (!bc.isBlock())
				continue;
			Block subblock = bc.getBlockSelf();
			if (!subblock.isTypeSymtab())
				continue;
			TypeSymtabBlock symtab = subblock.getTypeSymtabSelf();
			for (int j = 0; j < symtab.getNumBlockContents(); j++) {
				DataRecord record = symtab.getBlockContents(j).getDataRecordSelf();
				long typeIndex = record.getOp(0).getNumericValue();
				if (typeIndex < 0 || typeIndex >= typeTable.length)
					throw new RuntimeException("TYPESYMTAB has invalid type index: " + typeIndex);
				String name = recordToString(record, 1, record.getNumOps()-1);
				
				debug("Setting type name " + name + " on " + typeTable[(int)typeIndex]);
				
				module.addTypeName(name, typeTable[(int)typeIndex]);
			}
		}
	}
	
	
	private static List<ParameterAttributeMap> decodeParamAttrs(ParamAttrBlock block) {
		List<ParameterAttributeMap> result = new ArrayList<ParameterAttributeMap>();
		if (block == null)
			return result;

		for (int i = 0; i < block.getNumBlockContents(); i++) {
			Map<Integer,ParameterAttributes> paramAttrs = new HashMap<Integer,ParameterAttributes>();
			DataRecord paramAttrRecord = block.getBlockContents(i).getDataRecordSelf();
			for (int p = 0; p < paramAttrRecord.getNumOps(); p+=2) {
				int paramIndex = (int)paramAttrRecord.getOp(p).getNumericValue();
				int paramAttrBits = (int)paramAttrRecord.getOp(p+1).getNumericValue();
				// 0 = ret attribute, -1 = function attribute
				if (paramIndex < -1)
					throw new RuntimeException("Negative parameter index: " + paramIndex);
				paramAttrs.put(paramIndex, new ParameterAttributes(true, paramAttrBits));
			}
			result.add(new ParameterAttributeMap(true, paramAttrs));
		}
		
		return result;
	}
	
	
	/**
	 * Assign the following:
	 *	- deplibs
	 *	- section names
	 *	- collector names
	 *  - target triple
	 *  - data layout
	 *  - module inline ASM 
	 */
	private static void decodeModuleMisc(Module module, ModuleBlock2_8 block) {
		for (int i = 0; i < block.getNumBlockContents(); i++) {
			BlockContents bc = block.getBlockContents(i);
			if (bc.isBlock())
				continue;
			
			DataRecord record = bc.getDataRecordSelf();
			switch (record.getCode()) {
			case ModuleBlock2_8.MODULE_CODE_COLLECTORNAME:
				module.addCollectorName(recordToString(record));
				break;
			case ModuleBlock2_8.MODULE_CODE_DEPLIB:
				module.addLibraryName(recordToString(record));
				break;
			case ModuleBlock2_8.MODULE_CODE_SECTIONNAME:
				module.addSectionName(recordToString(record));
				break;
				
			case ModuleBlock2_8.MODULE_CODE_DATALAYOUT:
				module.setDataLayout(recordToString(record));
				break;
			case ModuleBlock2_8.MODULE_CODE_TRIPLE:
				module.setTargetTriple(recordToString(record));
				break;
			case ModuleBlock2_8.MODULE_CODE_ASM:
				module.setModuleInlineASM(recordToString(record));
				break;
			
			default:
				continue;
			}
		}
	}

	/**
	 * Uses the given ValueSymtabBlock to assign names to the values in the 
	 * given value list.
	 * THESE NAMES DO NOT APPLY TO METADATA VALUES! 
	 */
	public static void setValueNames(List<Value> valueList, ValueSymtabBlock block, NamedValueMap map) {
		for (int i = 0; i < block.getNumBlockContents(); i++) {
			DataRecord record = block.getBlockContents(i).getDataRecordSelf();
			if (record.getCode() == ValueSymtabBlock.VST_CODE_ENTRY) {
				int valueIndex = (int)record.getOp(0).getNumericValue();
				if (valueIndex >= valueList.size())
					throw new RuntimeException("VST_CODE_ENTRY has invalid index");
				
				String name = recordToString(record, 1, record.getNumOps()-1);
				Value value = valueList.get(valueIndex);
				map.addValueName(name, value);
				
				debug("Setting value name: " + name + " on " + value);
			}
		}
	}
	
	

	/**
	 * Takes a data record and turns each op into a char,
	 * either by taking its ASCII value or by decoding a Char6.
	 */
	public static String recordToString(DataRecord record) {
		return recordToString(record, 0, record.getNumOps());
	}
	public static String recordToString(DataRecord record, int start, int length) {
		StringBuffer buffer = new StringBuffer(record.getNumOps());
		for (int i = start; i < start+length; i++) {
			BasicOperandValue ov = record.getOp(i).getBasicSelf();
			buffer.append((char)(ov.getNumericValue() & 0xFF));
		}
		return buffer.toString();
	}
	
	/////////////////////////
	
	public static void main(String args[]) throws Throwable {
		BitcodeReader2_8.DEBUG = true;
		FunctionDecoder2_8.DEBUG = true;
		GlobalValueDecoder2_8.DEBUG = true;
		TypeTableDecoder2_8.DEBUG = true;
		
		DEBUG = true;
		BitcodeReader2_8 reader = new BitcodeReader2_8(new FileInputStream(args[0]));
		ModuleBlock2_8 module = reader.readBitcode();
		debug("Parsed ModuleBlock2_8");
		ModuleDecoder2_8.decode(module, reader.LLVM2_7MetadataDetected());
		debug("Decoded Module");
	}
}
