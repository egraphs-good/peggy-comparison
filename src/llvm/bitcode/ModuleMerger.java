package llvm.bitcode;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import llvm.instructions.*;
import llvm.instructions.BasicBlock.Handle;
import llvm.values.*;
import util.pair.Pair;

/**
 * This class will "merge" several related modules. It is assumed that all
 * the modules given have the same set of defined functions/globals/aliases.
 * This class will pick certain function body implementations from the modules
 * and splice them together to form a new module with those chosen implementations.
 * This class is often used in concert with FunctionExtractor.
 */
public class ModuleMerger {
	private static class ModuleMap {
		public final Map<GlobalVariable,GlobalVariable> globalMap =
			new HashMap<GlobalVariable,GlobalVariable>();
		public final Map<AliasValue,AliasValue> aliasMap =
			new HashMap<AliasValue,AliasValue>();
		public final Map<FunctionValue,FunctionValue> headerMap =
			new HashMap<FunctionValue,FunctionValue>();
	}
	
	/**
	 * Will destroy all the modules given, along with the map
	 */
	public static Module merge(Iterator<Pair<String,Module>> modulePairs) {
		Pair<String,Module> pair = modulePairs.next(); 
		String firstFunction = pair.getFirst();
		Module firstModule = pair.getSecond();
		pair = null;
		
		Set<String> donefuncs = new HashSet<String>();
		donefuncs.add(firstFunction);
		
		if (!modulePairs.hasNext()) 
			throw new NoSuchElementException("Must have at least 2 modules");
		
		while (modulePairs.hasNext()) {
			pair = modulePairs.next();
			String funcname = pair.getFirst();
			Module otherModule = pair.getSecond();
			pair = null;
			
			if (donefuncs.contains(funcname))
				throw new RuntimeException("Duplicate copies of function: " + funcname);
			
			System.out.println("Adding function " + funcname);
			
			int otherbodyindex = getFunctionBodyIndex(otherModule, funcname);
			int firstbodyindex = getFunctionBodyIndex(firstModule, funcname);
			
			ModuleMap map = buildModuleMap(firstModule, otherModule);
			firstModule.removeFunctionBody(firstbodyindex);
			FunctionBody body = otherModule.removeFunctionBody(otherbodyindex);
			body = updateFunctionBody(body, map);
			firstModule.addFunctionBody(body);
			
			System.gc();
		}
		
		return firstModule;
	}
	
	
	private static int getFunctionBodyIndex(Module module, String funcname) {
		Value v = module.getValueByName(funcname);
		if (v == null || !v.isFunction() || v.getFunctionSelf().isPrototype())
			throw new RuntimeException("Function body " + funcname + " is not in module");
		int index = -1;
		for (int i = 0; i < module.getNumFunctionBodies(); i++) {
			if (module.getFunctionBody(i).getHeader().equalsValue(v)) {
				index = i;
				break;
			}
		}
		if (index == -1)
			throw new RuntimeException("Can't find body for " + funcname);
		return index;
	}

	/**
	 * Builds a ModuleMap that maps all the globals/aliases/headers in 
	 * main to the sames ones in other.
	 * This uses the name maps of the modules to associate the values.
	 */
	private static ModuleMap buildModuleMap(Module main, Module other) {
		// assumes name map intact for global/alias/header 
		ModuleMap map = new ModuleMap();
		mapGlobals(main, other, map);
		mapAliases(main, other, map);
		mapHeaders(main, other, map);
		return map;
	}
	
	private static void mapGlobals(Module main, Module other, ModuleMap map) {
		// do globals
		for (int i = 0; i < other.getNumGlobalVariables(); i++) {
			GlobalVariable otherGlobal = other.getGlobalVariable(i);
			Set<String> names = getValueNames(otherGlobal, other);
			if (names.size() == 0)
				throw new RuntimeException("Global has no name");
			
			Value mainValue = null;
			int found = 0;
			Set<String> missingNames = new HashSet<String>();
			for (String name : names) {
				Value next = main.getValueByName(name);
				if (next == null) {
					missingNames.add(name);
					continue;
				}
				if (!next.isGlobalVariable())
					throw new RuntimeException("Value is not a global!");
				found++;
				if (mainValue == null) {
					mainValue = next;
				} else if (!mainValue.equalsValue(next)) {
					throw new RuntimeException("Different names for main global map to different value in other");
				}
			}
			
			if (found == 0) {
				// found none of them, copy it over
				map.globalMap.put(otherGlobal, otherGlobal);
				main.addGlobalVariable(otherGlobal);
				for (String n : missingNames)
					main.addValueName(n, otherGlobal);
			} else if (found == names.size()) {
				// found them all
				map.globalMap.put(otherGlobal, mainValue.getGlobalVariableSelf());
			} else {
				throw new RuntimeException("Found only subset of all names");
			}
		}
	}
	
	
	private static void mapAliases(Module main, Module other, ModuleMap map) { 
		// do aliases
		for (int i = 0; i < other.getNumAliases(); i++) {
			AliasValue otherAlias = other.getAlias(i);
			Set<String> names = getValueNames(otherAlias, other);
			if (names.size() == 0)
				throw new RuntimeException("Alias has no name");
			
			Value mainValue = null;
			int found = 0;
			Set<String> missingNames = new HashSet<String>();
			for (String name : names) {
				Value next = main.getValueByName(name);
				if (next == null) {
					missingNames.add(name);
					continue;
				}
				if (!next.isAlias())
					throw new RuntimeException("Value is not an alias!");
				found++;
				if (mainValue == null) {
					mainValue = next;
				} else if (!mainValue.equalsValue(next)) {
					throw new RuntimeException("Different names for main alias map to different value in other");
				}
			}

			if (found == 0) {
				map.aliasMap.put(otherAlias, otherAlias);
				main.addAlias(otherAlias);
				for (String n : missingNames)
					main.addValueName(n, otherAlias);
			} else if (found == names.size()) {
				map.aliasMap.put(otherAlias, mainValue.getAliasSelf());
			} else {
				throw new RuntimeException("Found only subset of all names");
			}
		}
	}
	
	private static void mapHeaders(Module main, Module other, ModuleMap map) {
		// do headers
		for (int i = 0; i < other.getNumFunctionHeaders(); i++) {
			FunctionValue otherHeader = other.getFunctionHeader(i);
			Set<String> names = getValueNames(otherHeader, other);
			if (names.size() == 0)
				throw new RuntimeException("Header has no name");
			
			Value mainValue = null;
			int found = 0;
			Set<String> missingNames = new HashSet<String>(); 
			for (String name : names) {
				Value next = main.getValueByName(name);
				if (next == null) {
					missingNames.add(name);
					continue;
				}
				if (!next.isFunction())
					throw new RuntimeException("Value is not a function!");
				found++;
				if (mainValue == null) {
					mainValue = next;
				} else if (!mainValue.equalsValue(next)) {
					throw new RuntimeException("Different names for main header map to different value in other");
				}
			}
			
			if (found == 0) {
				map.headerMap.put(otherHeader, otherHeader);
				main.addFunctionHeader(otherHeader);
				for (String n : missingNames)
					main.addValueName(n, otherHeader);
			} else if (found == names.size()) {
				map.headerMap.put(otherHeader, mainValue.getFunctionSelf());
			} else {
				throw new RuntimeException("Found subset of values");
			}
		}
	}
	
	private static Set<String> getValueNames(Value v, Module m) {
		Set<String> result = new HashSet<String>();
		for (String name : m.getValueNames()) {
			if (m.getValueByName(name).equalsValue(v)) {
				result.add(name);
			}
		}
		return result;
	}
	
	private static FunctionBody updateFunctionBody(FunctionBody body, ModuleMap moduleMap) {
		Map<Value,Value> map = new HashMap<Value,Value>();
		map.putAll(moduleMap.globalMap);
		map.putAll(moduleMap.aliasMap);
		map.putAll(moduleMap.headerMap);
		
		FunctionValue newheader = moduleMap.headerMap.get(body.getHeader());
		if (newheader == null)
			throw new NullPointerException("Header maps to nothing");

		for (int i = 0; i < newheader.getNumArguments(); i++) {
			map.put(body.getHeader().getArgument(i), newheader.getArgument(i));
		}
		
		FunctionBody newbody = new FunctionBody(newheader);
		
		RegisterAssignment reg = body.getRegisterAssignment();
		RegisterAssignment newreg = newbody.getRegisterAssignment();
		for (Handle handle : reg.getHandles()) {
			newreg.set(reg.getRegister(handle), handle);
		}
		
		for (int i = 0; i < body.getNumBlocks(); i++) {
			BasicBlock block = body.getBlock(i);
			for (int j = 0; j < block.getNumInstructions(); j++) {
				Handle handle = block.getHandle(j);
				Instruction newinst = handle.getInstruction().rewrite(map);
				if (newinst != handle.getInstruction()) {
					block.removeInstruction(j);
					Handle newhandle = block.insertInstruction(j, newinst);
					if (newreg.isAssigned(handle)) {
						VirtualRegister vr = newreg.remove(handle);
						newreg.set(vr, newhandle);
					}
				}
			}
			newbody.addBlock(block);
			newbody.setStart(body.getStart());
		}
		
		return newbody;
	}
	
	
	public static void main(final String args[]) throws IOException {
		if (args.length < 3) {
			System.err.println("USAGE: ModuleMerger <dest.bc> <func1:input1.bc> <func2:input2.bc> [...]");
			System.exit(1);
		}

		Iterator<Pair<String,Module>> iter = 
			new Iterator<Pair<String,Module>>() {
				private Pair<String,Module> next;
				private int index = 1;
			
				public boolean hasNext() {
					if (next == null) {
						if (index >= args.length)
							return false;
						String[] parts = args[index].split(":");
						if (parts.length != 2)
							throw new RuntimeException("Bad format: " + args[index]);
						try {
							BitcodeReader reader = new BitcodeReader(new FileInputStream(parts[1]));
							ModuleBlock moduleBlock = reader.readBitcode();
							Module module = ModuleDecoder.decode(moduleBlock);
							next = new Pair<String,Module>(parts[0], module);
						} catch (Throwable t) {
							throw new RuntimeException("Error reading module file: " + parts[1]);
						}
						index++;
						return true;
					} else
						return true;
				}
				public Pair<String,Module> next() {
					if (!hasNext())
						throw new NoSuchElementException();
					Pair<String,Module> result = next;
					next = null;
					return result;
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		

		Module result = merge(iter);
		FileOutputStream fout = new FileOutputStream(args[0]);
		BitcodeWriter writer = new BitcodeWriter();
		new ModuleEncoder(writer, result).writeModule();
		writer.dump(fout);
		fout.close();
	}
}
	