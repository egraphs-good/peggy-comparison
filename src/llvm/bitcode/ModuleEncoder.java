package llvm.bitcode;

import java.util.*;

import llvm.instructions.BasicBlock;
import llvm.instructions.FunctionBody;
import llvm.types.Type;
import llvm.values.*;

/**
 * This class encodes and writes out an entire Module to an lLVM 2.3 bitcode file.
 */
public class ModuleEncoder {
	private static final boolean DEBUG = false;
	private static void debug(String message) {
		if (DEBUG)
			System.err.println("ModuleEncoder: " + message);
	}


	protected final BitcodeWriter writer;
	protected final Module module;
	
	public ModuleEncoder(BitcodeWriter _writer, Module _module) {
		this.writer = _writer;
		this.module = _module;
	}
	
	private HashList<Value> gatherTypesAndValues(Set<? super Type> alltypes) {
		LinkedList<Value> allvalues = new LinkedList<Value>();
		
		// collect all the instructions' values
		for (int f = 0; f < this.module.getNumFunctionBodies(); f++) {
			FunctionBody body = this.module.getFunctionBody(f);
			
			Set<Value> values = new HashSet<Value>();
			for (int b = 0; b < body.getNumBlocks(); b++) {
				BasicBlock block = body.getBlock(b);
				for (int j = 0; j < block.getNumInstructions(); j++) {
					for (Iterator<? extends Value> iter = block.getInstruction(j).getValues(); iter.hasNext(); ) {
						values.add(iter.next());
					}
					for (Iterator<? extends Type> iter = block.getInstruction(j).getTypes(); iter.hasNext(); ) {
						alltypes.add(iter.next());
					}
				}
			}
			allvalues.addAll(values);
			alltypes.add(body.getHeader().getType());
		}
		
		// complete the types list
		for (int i = 0; i < this.module.getNumAliases(); i++) {
			alltypes.add(this.module.getAlias(i).getType());
			allvalues.add(this.module.getAlias(i));
		}
		for (int i = 0; i < this.module.getNumGlobalVariables(); i++) {
			GlobalVariable glob = this.module.getGlobalVariable(i);
			alltypes.add(glob.getType());
			allvalues.add(glob);
		}
		for (int i = 0; i < this.module.getNumFunctionHeaders(); i++) {
			FunctionValue func = this.module.getFunctionHeader(i);
			alltypes.add(func.getType());
			if (func.isPrototype()) {
				allvalues.add(func);
			}
		}
		
		HashList<Value> constants = flattenValues(allvalues, alltypes);
		return constants;
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
	 * Takes the list of all values and flattens it.
	 * Builds and returns the constants table.
	 */
	private HashList<Value> flattenValues(
			LinkedList<Value> allvalues,
			Set<? super Type> alltypes) {
		
		HashList<Value> constants = new HashList<Value>();
		HashList<Value> result = new HashList<Value>(); 
		
		// must add all globals before we can emit the constants
		for (int i = 0; i < this.module.getNumGlobalVariables(); i++) {
			GlobalVariable global = this.module.getGlobalVariable(i);
			result.add(global);
		}
		for (int i = 0; i < this.module.getNumAliases(); i++) {
			AliasValue alias = this.module.getAlias(i);
			result.add(alias);
		}
		// only put in isProto function headers
		for (int i = 0; i < this.module.getNumFunctionHeaders(); i++) {
			FunctionValue func = this.module.getFunctionHeader(i);
			if (!func.isPrototype())
				continue;
			result.add(func);
		}
		// now do the headers of functions that have bodies
		for (int i = 0; i < this.module.getNumFunctionBodies(); i++) {
			FunctionValue func = this.module.getFunctionBody(i).getHeader();
			result.add(func);
		}

		while (!allvalues.isEmpty()) {
			Value next = allvalues.removeFirst();
			alltypes.add(next.getType());

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
		orderConstants(constants, result);
		debug("Ordered size = " + result.size());
		return result;
	}
	
	
	/**
	 * Reorders the constants topologically, so to speak
	 */
	private void orderConstants(HashList<Value> constants, HashList<Value> result) {
		boolean[] got = new boolean[constants.size()];
		
		int leftover = constants.size();
		for (boolean progress=true; progress && (leftover > 0); ) {
			progress = false;
			for (int index = 0; index < got.length; index++) {
				if (!got[index]) {
					Value v = constants.getValue(index);
					boolean missing = false;
					for (Iterator<? extends Value> iter=v.getSubvalues(); iter.hasNext(); ) {
						Value sub = iter.next();
						if (!result.hasValue(sub)) {
							missing = true;
							break;
						}
					}
					if (missing) continue;
					
					result.add(v);
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
				result.add(constants.getValue(index));
			}
		}
	}
	
	
	public void writeModule() {
		final int innerAbbrevLength = 4;
		
		// output header and Module miscellany
		int patch = writeModuleHeader();
		
		// gather the types and values
		debug("Gathering types and values...");
		Set<Type> alltypes = new HashSet<Type>();
		HashList<Value> constants = gatherTypesAndValues(alltypes);

		// count the nonconstants
		int numNonconstGlobals;
		for (numNonconstGlobals = 0; numNonconstGlobals < constants.size(); numNonconstGlobals++) {
			Value v = constants.getValue(numNonconstGlobals);
			if (v.isGlobalVariable() || v.isAlias() || v.isFunction())
				continue;
			else
				break;
		}
		
		// add named types to alltypes list
		for (String name : this.module.getTypeNames()) {
			alltypes.add(this.module.getTypeByName(name));
		}

		// build and output the type table, and type name table
		debug("Emitting types");
		debug("Alltypes = " + alltypes);
		HashList typeTable;
		{
			TypeEncoder typeEncoder = new TypeEncoder(writer, innerAbbrevLength);
			typeEncoder.writeTypeBlock(alltypes);
			typeEncoder.writeTypeSymtabBlock(this.module);
			typeTable = typeEncoder.getTypeTable();
			typeEncoder = null; // save memory
			alltypes = null;   // save memory
		}

		
		debug("Emitting paramattrs");
		List<ParameterAttributeMap> paramAttrs;
		{// build and emit the ParamAttrBlock
			ParamAttrEncoder encoder = new ParamAttrEncoder(this.writer, innerAbbrevLength, this.module);
			paramAttrs = encoder.writeParamAttrs();
			encoder = null;
		}
		
		// write out the globals, aliases, and function headers
		// IMPORTANT: GLOBALS/ALIASES/HEADERS MUST BE EMITTED BEFORE CONSTANTS!!!!
		debug("Emitting nonconst globals");
		writeNonconstGlobals(constants, typeTable, innerAbbrevLength, paramAttrs);

		
		debug("Value table: ");
		debug(constants.toString());
		
		
		{// filled up the constant maps, now build the constant table
			debug("Emitting global constants");
			GlobalConstantEncoder encoder = new GlobalConstantEncoder(this.writer, innerAbbrevLength, typeTable);
			encoder.writeGlobalConstants(constants, numNonconstGlobals);
			encoder = null;
		}

		
		debug("Emitting global valuesymtab");
		writeGlobalValueSymtab(innerAbbrevLength, constants);

		
		// write out function bodies
		debug("Emitting function bodies");
		for (int i = 0; i < this.module.getNumFunctionBodies(); i++) {
			FunctionEncoder encoder = new FunctionEncoder(this.writer, this.module, this.module.getFunctionBody(i), paramAttrs);
			encoder.encodeFunction(innerAbbrevLength, typeTable, constants);
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
			HashList<Value> valueTable, 
			HashList<Type> typeTable,
			int abbrevLength,
			List<ParameterAttributeMap> paramAttrs) {
		List<Long> ops = new ArrayList<Long>(10);

		/////////// Assigned indexes, now emit records ///////////////
		// two-pass approach might not be necessary, but it's more safe
		
		for (int i = 0; i < this.module.getNumGlobalVariables(); i++) {
			GlobalVariable global = this.module.getGlobalVariable(i);
			
			// emit
			ops.clear();
			ops.add((long)typeTable.getIndex(global.getType()));
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
			
			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(ModuleBlock.MODULE_CODE_GLOBALVAR, ops));
		}
		
		for (int i = 0; i < this.module.getNumAliases(); i++) {
			AliasValue alias = this.module.getAlias(i);
			
			// emit
			ops.clear();
			ops.add((long)typeTable.getIndex(alias.getType()));
			ops.add((long)valueTable.getIndex(alias.getAliaseeValue()));
			ops.add((long)alias.getLinkage().getValue());
			ops.add((long)alias.getVisibility().getBits());
			
			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(ModuleBlock.MODULE_CODE_ALIAS, ops));
		}
		
		// only put in isProto function headers
		for (int i = 0; i < this.module.getNumFunctionHeaders(); i++) {
			FunctionValue func = this.module.getFunctionHeader(i);
			if (!func.isPrototype())
				continue;
			
			// emit
			ops.clear();
			ops.add((long)typeTable.getIndex(func.getType()));
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
			
			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(ModuleBlock.MODULE_CODE_FUNCTION, ops));
		}
		
		// now do the headers of functions that have bodies
		for (int i = 0; i < this.module.getNumFunctionBodies(); i++) {
			FunctionValue func = this.module.getFunctionBody(i).getHeader();
			
			// emit
			ops.clear();
			ops.add((long)typeTable.getIndex(func.getType()));
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
			ops.add((long)translateAlignment(func.getAlignment()));
			ops.add((long)func.getSectionIndex());
			ops.add((long)func.getVisibility().getBits());
			if (func.getCollectorIndex() != 0)
				ops.add((long)func.getCollectorIndex());
			
			writer.writeUnabbrevRecord(abbrevLength, new UnabbrevRecord(ModuleBlock.MODULE_CODE_FUNCTION, ops));
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
