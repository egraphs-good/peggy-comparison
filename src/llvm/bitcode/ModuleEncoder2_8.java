package llvm.bitcode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import llvm.types.Type;
import llvm.values.AliasValue;
import llvm.values.FunctionValue;
import llvm.values.GlobalVariable;
import llvm.values.MetadataNodeList;
import llvm.values.Module;
import llvm.values.ParameterAttributeMap;
import llvm.values.Value;

/**
 * This class encodes and writes out an entire Module to an LLVM2.8 bitcode file.
 */
public class ModuleEncoder2_8 {
	protected static boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("ModuleEncoder: " + message);
	}

	protected final BitcodeWriter writer;
	protected final Module module;
	
	public ModuleEncoder2_8(BitcodeWriter _writer, Module _module) {
		this.writer = _writer;
		this.module = _module;
		if (!this.module.is2_8())
			throw new IllegalArgumentException("Expecting 2.8 module");
	}
	
	/**
	 * Fills the globalsAndConstants list with the globals/aliases/headers,
	 * along with all constants from the entire module.
	 * The globals/aliases/headers will be first in the resulting list, since they
	 * must be emitted first. No function-local values will be left in the result list. 
	 */
	private int gatherGlobalValues(
			HashList<Value> valueTable, 
			HashList<Value> metadataValueTable) { 
		LinkedList<Value> allvalues = new LinkedList<Value>();
		
		// get named metadata values
		for (String name : module.getNamedMetadataNames()) {
			MetadataNodeList list = module.getNamedMetadata(name);
			for (int i = 0; i < list.getNumNodes(); i++) {
				allvalues.add(list.getNode(i));
			}
		}
		
		// get named values
		for (String name : module.getValueNames()) {
			allvalues.add(module.getValueByName(name));
		}

		// get aliases/globals/funcs
		for (int i = 0; i < this.module.getNumAliases(); i++) {
			allvalues.add(this.module.getAlias(i));
		}
		for (int i = 0; i < this.module.getNumGlobalVariables(); i++) {
			allvalues.add(this.module.getGlobalVariable(i));
		}
		for (int i = 0; i < this.module.getNumFunctionHeaders(); i++) {
			allvalues.add(this.module.getFunctionHeader(i));
		}

		// fill in the global valueTable/metadataValueTable
		flattenValues(valueTable, metadataValueTable, allvalues);
		
		// count the nonconstant globals
		int numNonconstGlobals;
		for (numNonconstGlobals = 0; numNonconstGlobals < valueTable.size(); numNonconstGlobals++) {
			Value v = valueTable.getValue(numNonconstGlobals);
			if (v.isGlobalVariable() || v.isAlias() || v.isFunction())
				continue;
			else
				break;
		}
		return numNonconstGlobals;
	}


	
	/**
	 * Writes the following:
	 * 1) magic number 
	 * 2) module header
	 * 3) module records:
	 * 		- version
	 * 		- triple
	 * 		- datalayout
	 * 		- module asm
	 * 		- section names
	 * 		- dependent libraries
	 * 		- collector names
	 * 		
	 * (no globals, aliases, functions, or purgevals)
	 * 
	 * Returns the EnterSubblock blocklength bitindex, for patching
	 */
	private int writeModuleHeader() {
		final int innerAbbrevLength = 4;
		
		// magic number
		writer.Write('B', 8);
		writer.Write('C', 8);
		writer.Write(0x0, 4);
		writer.Write(0xC, 4);
		writer.Write(0xE, 4);
		writer.Write(0xD, 4);
		
		int patch = writer.writeEnterSubblock(2, new EnterSubblock(ModuleBlock.MODULE_BLOCK_ID, innerAbbrevLength, 0));
		
		List<Long> ops = new ArrayList<Long>();

		/*
		{// version
			ops.clear();
			ops.add(0L);
			writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ModuleBlock.MODULE_CODE_VERSION, ops));
		}
		*/
		
		if (module.getTargetTriple() != null) {
			// triple
			String triple = module.getTargetTriple();
			ops.clear();
			for (int i = 0; i < triple.length(); i++) {
				ops.add((long)triple.charAt(i));
			}
			writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ModuleBlock.MODULE_CODE_TRIPLE, ops));
		}
		
		if (module.getDataLayout() != null) {
			// datalayout
			String datalayout = module.getDataLayout();
			ops.clear();
			for (int i = 0; i < datalayout.length(); i++) {
				ops.add((long)datalayout.charAt(i));
			}
			writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ModuleBlock.MODULE_CODE_DATALAYOUT, ops));
		}
		
		if (module.getModuleInlineASM() != null) {
			// module asm
			String asm = module.getModuleInlineASM();
			ops.clear();
			for (int i = 0; i < asm.length(); i++) {
				ops.add((long)asm.charAt(i));
			}
			writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ModuleBlock.MODULE_CODE_ASM, ops));
		}
		
		// section names
		for (int i = 0; i < module.getNumSections(); i++) {
			String section = module.getSectionName(i);
			ops.clear();
			for (int j = 0; j < section.length(); j++) {
				ops.add((long)section.charAt(j));
			}
			writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ModuleBlock.MODULE_CODE_SECTIONNAME, ops));
		}
		
		// collector names
		for (int i = 0; i < module.getNumCollectors(); i++) {
			String collector = module.getCollectorName(i);
			ops.clear();
			for (int j = 0; j < collector.length(); j++) {
				ops.add((long)collector.charAt(j));
			}
			writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ModuleBlock.MODULE_CODE_COLLECTORNAME, ops));
		}
		
		// dep libs
		for (int i = 0; i < module.getNumLibraries(); i++) {
			String deplib = module.getLibraryName(i);
			ops.clear();
			for (int j = 0; j < deplib.length(); j++) {
				ops.add((long)deplib.charAt(j));
			}
			writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ModuleBlock.MODULE_CODE_DEPLIB, ops));
		}
		
		return patch;
	}

	
	
	/**
	 * Takes the list 'allvalues' and flattens it, into result and metadata.
	 * The 'globalsAndConstants' list will only contain globals/aliases/headers
	 * and constants. 
	 */
	private void flattenValues(
			HashList<Value> globalsAndConstants,
			HashList<Value> metadata,
			LinkedList<Value> allvalues) {
		
		HashList<Value> constants = new HashList<Value>();
	
		// BEGIN FILLING IN GLOBALSANDCONSTANTS:
		// must add all globals before we can emit the constants
		for (int i = 0; i < this.module.getNumGlobalVariables(); i++) {
			GlobalVariable global = this.module.getGlobalVariable(i);
			globalsAndConstants.add(global);
		}
		for (int i = 0; i < this.module.getNumAliases(); i++) {
			AliasValue alias = this.module.getAlias(i);
			globalsAndConstants.add(alias);
		}
		// only put in isProto function headers
		for (int i = 0; i < this.module.getNumFunctionHeaders(); i++) {
			FunctionValue func = this.module.getFunctionHeader(i);
			if (!func.isPrototype())
				continue;
			globalsAndConstants.add(func);
		}
		// now do the headers of functions that have bodies
		for (int i = 0; i < this.module.getNumFunctionBodies(); i++) {
			FunctionValue func = this.module.getFunctionBody(i).getHeader();
			globalsAndConstants.add(func);
		}

		// NOW PUT IN CONSTANTS!
		while (!allvalues.isEmpty()) {
			Value next = allvalues.removeFirst();

			if (next.isFunction() ||
				next.isArgument() ||
				next.isRegister() ||
				next.isLabel()) {
				// skip (no subvalues)
				continue;
			}
			else if (next.isGlobalVariable()) {
				GlobalVariable global = next.getGlobalVariableSelf();
				if (global.getInitialValue() != null)
					allvalues.addLast(global.getInitialValue());
			}
			else if (next.isAlias()) {
				AliasValue alias = next.getAliasSelf();
				allvalues.addLast(alias.getAliaseeValue());
			}
			else {
				// constant
				if (constants.hasValue(next))
					continue;
				constants.add(next);
				for (Iterator<? extends Value> iter = next.getSubvalues(); iter.hasNext(); ) {
					allvalues.add(iter.next());
				}
			}
		}

		debug("Original size = " + constants.size());
		orderConstants(constants, globalsAndConstants, metadata);
		debug("Ordered size = " + globalsAndConstants.size());
	}
	
	
	/**
	 * Reorders the constants topologically, so to speak
	 */
	private void orderConstants(
			HashList<Value> inputList, 
			HashList<Value> globalsAndConstants,
			HashList<Value> metadata) {
		boolean[] got = new boolean[inputList.size()];
		int leftover = inputList.size();
		for (boolean progress=true; progress && (leftover > 0); ) {
			progress = false;
			for (int index = 0; index < got.length; index++) {
				if (!got[index]) {
					Value v = inputList.getValue(index);
					boolean missing = false;
					for (Iterator<? extends Value> iter=v.getSubvalues(); iter.hasNext(); ) {
						Value sub = iter.next();
						if ((sub.isMetadataNode() || sub.isMetadataString()) && !metadata.hasValue(sub)) {
							missing = true;
							break;
						} else if (!globalsAndConstants.hasValue(sub)) {
							missing = true;
							break;
						}
					}
					if (missing) continue;

					if (v.isMetadataNode() || v.isMetadataString()) {
						metadata.add(v);
					} else {
						globalsAndConstants.add(v);
					}
					progress = true;
					leftover--;
					got[index] = true;
				}
			}
		}
		
		for (int index = 0; index < got.length; index++) {
			if (!got[index]) {
				got[index] = true;
				leftover--;
				Value v = inputList.getValue(index);
				if (v.isMetadataNode() || v.isMetadataString()) {
					metadata.add(v);
				} else {
					globalsAndConstants.add(v);
				}
			}
		}
	}
	
	public static interface TypeTable {
		public int getTypeIndex(Type type);
		public HashList<Type> getHashList();
	}
	
	public void writeModule() {
		final int innerAbbrevLength = 4;
		
		// output header and Module miscellany
		int patch = writeModuleHeader();
		
		// gather the types and values
		TypeTable typeTable = new TypeTable() {
			HashList<Type> types = new HashList<Type>();
			public int getTypeIndex(Type type) {
				if (types.hasValue(type))
					return types.getIndex(type);
				int result = types.add(type);
				for (Iterator<? extends Type> iter = type.getSubtypes(); iter.hasNext(); ) {
					getTypeIndex(iter.next());
				}
				return result;
			}
			public HashList<Type> getHashList() {return this.types;}
		};
		HashList<Value> metadataValues = new HashList<Value>();
		HashList<Value> valueTable = new HashList<Value>(); 
		debug("Gathering global values");
		int numNonconstGlobals = gatherGlobalValues(valueTable, metadataValues);

		// add named types to alltypes list
		for (String name : this.module.getTypeNames()) {
			typeTable.getTypeIndex(this.module.getTypeByName(name));
		}

		debug("Emitting paramattrs");
		List<ParameterAttributeMap> paramAttrs;
		{// build and emit the ParamAttrBlock
			ParamAttrEncoder encoder = new ParamAttrEncoder(writer, innerAbbrevLength, this.module);
			paramAttrs = encoder.writeParamAttrs();
			encoder = null;
		}

		BitcodeWriter fakeWriter = new BitcodeWriter();
		{// SHOULD EMIT TYPE TABLE NOW, so get a virtual BitcodeWriter
			// write out the globals, aliases, and function headers
			// IMPORTANT: GLOBALS/ALIASES/HEADERS MUST BE EMITTED BEFORE CONSTANTS!!!!
			debug("Emitting globals/aliases/funcs");
			writeNonconstGlobals(fakeWriter, valueTable, typeTable, innerAbbrevLength, paramAttrs);

			debug("Value table: ");
			debug(valueTable.toString());

			{// filled up the constant maps, now build the constant table
				debug("Emitting global constants");
				GlobalConstantEncoder2_8 encoder = new GlobalConstantEncoder2_8(fakeWriter, innerAbbrevLength, typeTable);
				encoder.writeConstantsBlock(valueTable, numNonconstGlobals);
				encoder = null;
			}
			
			debug("Emitting metadata");
			{
				MetadataEncoder encoder = new MetadataEncoder(fakeWriter, innerAbbrevLength, typeTable);
				encoder.writeMetadataBlock(module, metadataValues, valueTable);
				encoder = null;
			}

			// write out function bodies
			debug("Emitting function bodies");
			for (int i = 0; i < this.module.getNumFunctionBodies(); i++) {
				FunctionEncoder2_8 encoder = new FunctionEncoder2_8(fakeWriter, innerAbbrevLength, this.module, typeTable, this.module.getFunctionBody(i), paramAttrs);
				encoder.encodeFunction(metadataValues, valueTable);
				encoder = null;
			}
		}
		
		// build and output the type table, and type name table
		debug("Emitting types");
		{
			TypeEncoder2_8 typeEncoder = new TypeEncoder2_8(writer, innerAbbrevLength);
			typeEncoder.writeTypeBlock(typeTable.getHashList());
			typeEncoder.writeTypeSymtabBlock(this.module, typeTable.getHashList());
			typeEncoder = null;
		}
		
		// dump the fake writer into the real writer
		writer.WriteWriter(fakeWriter);

		debug("Emitting global valuesymtab");
		writeGlobalValueSymtab(innerAbbrevLength, valueTable);
		
		// write metadata kinds
		if (module.getMetadataKindNames().size() > 0) {
			debug("Emitting metadata kinds");
			MetadataEncoder encoder = new MetadataEncoder(writer, innerAbbrevLength, typeTable);
			encoder.writeMetadataKindsBlock(module);
			encoder = null;
		}
		
		writer.writeEndBlock(innerAbbrevLength);
		writer.patchEnterSubblockSize(patch);
		
		debug("Done emitting!");
	}
	
	
	
	/**
	 * Adds all of the globals, aliases, and function headers to the global
	 * value table. If any of them have names, their index->name mappings
	 * will be added to the valueSymtab.
	 */
	private void writeNonconstGlobals(
			BitcodeWriter fakeWriter,
			HashList<Value> valueTable, 
			TypeTable typeTable,
			int abbrevLength,
			List<ParameterAttributeMap> paramAttrs) {
		List<Long> ops = new ArrayList<Long>(10);

		/////////// Assigned indexes, now emit records ///////////////
		// two-pass approach might not be necessary, but it's more safe
		
		for (int i = 0; i < this.module.getNumGlobalVariables(); i++) {
			GlobalVariable global = this.module.getGlobalVariable(i);
			
			// emit
			ops.clear();
			ops.add((long)typeTable.getTypeIndex(global.getType()));
			ops.add(global.isConstant() ? 1L : 0L);
			if (global.getInitialValue() != null) {
				ops.add((long)valueTable.getIndex(global.getInitialValue())+1L);
			} else {
				ops.add(0L);
			}
			ops.add((long)global.getLinkage().getValue());
			ops.add((long)translateAlignment(global.getAlignment()));
			ops.add((long)global.getSectionIndex());
			if (global.getVisibility().getBits()!=0 || global.isThreadLocal()) {
				ops.add((long)global.getVisibility().getBits());
				if (global.isThreadLocal())
					ops.add(1L);
			}
			
			fakeWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(ModuleBlock.MODULE_CODE_GLOBALVAR, ops));
		}
		
		for (int i = 0; i < this.module.getNumAliases(); i++) {
			AliasValue alias = this.module.getAlias(i);
			
			// emit
			ops.clear();
			ops.add((long)typeTable.getTypeIndex(alias.getType()));
			ops.add((long)valueTable.getIndex(alias.getAliaseeValue()));
			ops.add((long)alias.getLinkage().getValue());
			ops.add((long)alias.getVisibility().getBits());
			
			fakeWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(ModuleBlock.MODULE_CODE_ALIAS, ops));
		}
		
		// only put in isProto function headers
		for (int i = 0; i < this.module.getNumFunctionHeaders(); i++) {
			FunctionValue func = this.module.getFunctionHeader(i);
			if (!func.isPrototype())
				continue;
			
			// emit
			ops.clear();
			ops.add((long)typeTable.getTypeIndex(func.getType()));
			ops.add((long)func.getCallingConvention());
			ops.add(1L);
			ops.add((long)func.getLinkage().getValue());
			{
				int paramAttrsIndex;
				if (func.getParameterAttributeMap().isEmpty()) {
					paramAttrsIndex = 0;
				} else {
					paramAttrsIndex = paramAttrs.indexOf(func.getParameterAttributeMap());
					if (paramAttrsIndex < 0)
						throw new RuntimeException("Parameter attribute map not found in table");
					paramAttrsIndex++;
				}
				ops.add((long)paramAttrsIndex);
			}
			ops.add((long)translateAlignment(func.getAlignment()));
			ops.add((long)func.getSectionIndex());
			ops.add((long)func.getVisibility().getBits());
			if (func.getCollectorIndex() != 0)
				ops.add((long)func.getCollectorIndex());
			
			fakeWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(ModuleBlock.MODULE_CODE_FUNCTION, ops));
		}
		
		// now do the headers of functions that have bodies
		for (int i = 0; i < this.module.getNumFunctionBodies(); i++) {
			FunctionValue func = this.module.getFunctionBody(i).getHeader();
			
			// emit
			ops.clear();
			ops.add((long)typeTable.getTypeIndex(func.getType()));
			ops.add((long)func.getCallingConvention());
			ops.add(0L);
			ops.add((long)func.getLinkage().getValue());
			{
				int paramAttrsIndex;
				if (func.getParameterAttributeMap().isEmpty()) {
					paramAttrsIndex = 0;
				} else {
					paramAttrsIndex = paramAttrs.indexOf(func.getParameterAttributeMap());
					if (paramAttrsIndex < 0)
						throw new RuntimeException("Parameter attribute map not found in table");
					paramAttrsIndex++;
				}
				ops.add((long)paramAttrsIndex);
			}
			
			int outputAlignment = translateAlignment(func.getAlignment());
			debug("func proto alignment = " + outputAlignment);
					
			ops.add((long)outputAlignment);
			ops.add((long)func.getSectionIndex());
			ops.add((long)func.getVisibility().getBits());
			if (func.getCollectorIndex() != 0)
				ops.add((long)func.getCollectorIndex());
			
			fakeWriter.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(ModuleBlock.MODULE_CODE_FUNCTION, ops));
		}
	}
	
	/**
	 * Write out the global value symbol table (no bb entries)
	 */
	private void writeGlobalValueSymtab(int abbrevLength, HashList<Value> valueTable) {
		Set<String> valueNames = this.module.getValueNames();
		if (valueNames.size() == 0)
			return;

		final int innerAbbrevLength = 2;
		int patch = writer.writeEnterSubblock(abbrevLength, new EnterSubblock(ValueSymtabBlock.VALUE_SYMTAB_BLOCK_ID, innerAbbrevLength, 0));
		
		List<Long> ops = new ArrayList<Long>(20);
		for (String name : valueNames) {
			int index = valueTable.getIndex(module.getValueByName(name));
			
			ops.clear();
			ops.add((long)index);
			for (int i = 0; i < name.length(); i++) {
				ops.add((long)name.charAt(i));
			}
			
			writer.writeUnabbrevRecord(innerAbbrevLength, new UnabbrevRecord(ValueSymtabBlock.VST_CODE_ENTRY, ops));
		}
		
		writer.writeEndBlock(innerAbbrevLength);
		writer.patchEnterSubblockSize(patch);
	}
	
	// inverse of (1<<alignment)>>1 is log2(2*alignment)
	public static int translateAlignment(int alignment) {
		alignment<<=1;
		int log2 = 1;
		while ((alignment >> log2) != 0)
			log2++;
		return log2-1;
	}
}
