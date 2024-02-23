package llvm.bitcode;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import llvm.types.Type;
import llvm.values.Module;
import llvm.values.NamedValueMap;
import llvm.values.ParameterAttributeMap;
import llvm.values.ParameterAttributes;
import llvm.values.Value;

/**
 * This class decodes a Module from a ModuleBlock.
 */
public abstract class ModuleDecoder {
	protected static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG) 
			System.err.println("ModuleDecoder: " + message);
	}
	
	public static Module decode(ModuleBlock block) {
		TypeBlock typeBlock = null;
		ParamAttrBlock paramAttrBlock = null;
		
		// find the TypeBlock and the ParamAttrBlock
		for (int i = 0; i < block.getNumBlockContents(); i++) {
			BlockContents bc = block.getBlockContents(i);
			if (bc.isBlock()) {
				Block subblock = bc.getBlockSelf();
				if (subblock.isType())
					typeBlock = subblock.getTypeSelf();
				else if (subblock.isParamAttr())
					paramAttrBlock = subblock.getParamAttrSelf();
			}
		}
		
		// build the type table
		Type[] typeTable = null;
		if (typeBlock != null) {
			typeTable = TypeTableDecoder.decodeTypeTable(typeBlock);
			if (DEBUG) {
				debug("\nType table:");
				for (int i = 0; i < typeTable.length; i++) {
					debug("type[" + i + "] = " + typeTable[i]);
				}
			}
		}

		Module module = new Module();

		debug("Setting type names");
		// assign the type names (from TypeSymtabBlock)
		setTypeNames(typeTable, block, module);
		
		debug("Decoding misc");
		// decode module-level properties
		decodeModuleMisc(module, block);
		
		debug("Decoding param attrs");
		List<ParameterAttributeMap> paramAttrList = decodeParamAttrs(paramAttrBlock);
		
		// decode the value table
		LinkedList<Value> valueTable = GlobalValueDecoder.decodeGlobalValueTable(block, module, typeTable, paramAttrList);
		
		debug("Setting value names");
		// decode the value symbol table
		for (int i = 0; i < block.getNumBlockContents(); i++) {
			BlockContents bc = block.getBlockContents(i);
			if (!bc.isBlock())
				continue;
			Block subblock = bc.getBlockSelf();
			if (!subblock.isValueSymtab())
				continue;
			setValueNames(valueTable, subblock.getValueSymtabSelf(), module);
		}

		
		debug("\nValue List:");
		if (DEBUG) {
			int counter = 0;
			for (Value v : valueTable) {
				debug("value " + counter + " = " + v);
				counter++;
			}
		}
		
		debug("Decoding functions");
		FunctionDecoder.decodeFunctions(block, module, typeTable, valueTable, paramAttrList);
		
		debug("Done decoding!");
		
		return module;
	}

	/**
	 * Takes every TypeSymtabBlock in the given ModuleBlock and assigns 
	 * the names to the appropriate type table entries.
	 */
	private static void setTypeNames(Type[] typeTable, ModuleBlock block, Module module) {
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
				if (paramIndex < 0)
					throw new RuntimeException("Negative parameter index: " + paramIndex);
				paramAttrs.put(paramIndex, new ParameterAttributes(paramAttrBits));
			}
			result.add(new ParameterAttributeMap(false, paramAttrs));
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
	private static void decodeModuleMisc(Module module, ModuleBlock block) {
		for (int i = 0; i < block.getNumBlockContents(); i++) {
			BlockContents bc = block.getBlockContents(i);
			if (bc.isBlock())
				continue;
			
			DataRecord record = bc.getDataRecordSelf();
			switch (record.getCode()) {
			case ModuleBlock.MODULE_CODE_COLLECTORNAME:
				module.addCollectorName(recordToString(record));
				break;
			case ModuleBlock.MODULE_CODE_DEPLIB:
				module.addLibraryName(recordToString(record));
				break;
			case ModuleBlock.MODULE_CODE_SECTIONNAME:
				module.addSectionName(recordToString(record));
				break;
				
			case ModuleBlock.MODULE_CODE_DATALAYOUT:
				module.setDataLayout(recordToString(record));
				break;
			case ModuleBlock.MODULE_CODE_TRIPLE:
				module.setTargetTriple(recordToString(record));
				break;
			case ModuleBlock.MODULE_CODE_ASM:
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
		BitcodeReader.DEBUG = true;
		FunctionDecoder.DEBUG = true;
		GlobalValueDecoder.DEBUG = true;
		DEBUG = true;
		BitcodeReader reader = new BitcodeReader(new FileInputStream(args[0]));
		ModuleBlock module = reader.readBitcode();
		debug("Parsed ModuleBlock");
		ModuleDecoder.decode(module);
		debug("Decoded Module");
	}
}
