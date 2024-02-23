package llvm.values;

import java.util.*;

import llvm.instructions.FunctionBody;
import llvm.types.Type;

/**
 * Represents an LLVM (2.3 or 2.8) Module.
 * Modules contain function bodies, function headers (for stubs as well as bodies),
 * global variables, aliases, the list of section names, GC collector names,
 * library names, the global value->name mapping, a target triple,
 * a data layout, and optionally some module ASM.
 * This class is what you get when you "parse" an LLVM bitcode file, and can
 * be written back to a bitcode file (see package llvm.bitcode). 
 */
public class Module implements NamedValueMap {
	protected final boolean is2_8;
	protected final List<FunctionBody> bodies;
	protected final List<FunctionValue> functionHeaders;
	protected final List<GlobalVariable> globalVars;
	protected final List<AliasValue> aliases;
	protected final List<String> sectionNames;
	protected final List<String> collectorNames;
	protected final List<String> libraries;
	protected final Map<String,Type> typeNameMap;
	protected final Map<String,Value> valueNameMap;
	protected final Map<String,MetadataNodeList> name2namedmetadata;
	protected final Map<String,Integer> name2metadataKind;
	protected String targetTriple;
	protected String dataLayout;
	protected String moduleInlineASM;

	public Module() {
		this(false);
	}
	public Module(boolean _is2_8) {
		this.is2_8 = _is2_8;
		this.bodies = new ArrayList<FunctionBody>();
		this.functionHeaders = new ArrayList<FunctionValue>();
		this.globalVars = new ArrayList<GlobalVariable>();
		this.aliases = new ArrayList<AliasValue>();
		this.sectionNames = new ArrayList<String>();
		this.collectorNames = new ArrayList<String>();
		this.libraries = new ArrayList<String>();
		this.typeNameMap = new HashMap<String,Type>();
		this.valueNameMap = new HashMap<String,Value>();
		this.name2namedmetadata = new HashMap<String,MetadataNodeList>();
		this.name2metadataKind = new HashMap<String,Integer>();
	}

	public boolean is2_8() {return this.is2_8;}
	
	public int getMetadataKind(String name) {
		if (this.name2metadataKind.containsKey(name))
			return this.name2metadataKind.get(name);
		else {
			int next = this.name2metadataKind.size()+1;
			this.name2metadataKind.put(name, next);
			return next;
		}
	}
	public Set<String> getMetadataKindNames() {
		return this.name2metadataKind.keySet();
	}

	public MetadataNodeList getNamedMetadata(String name) {
		if (this.name2namedmetadata.containsKey(name)) {
			return this.name2namedmetadata.get(name);
		} else {
			MetadataNodeList list = new MetadataNodeList();
			this.name2namedmetadata.put(name, list);
			return list;
		}
	}
	public Set<String> getNamedMetadataNames() {
		return this.name2namedmetadata.keySet();
	}
	
	/**
	 * Any given value may have multiple names, this will return the first.
	 * If you wish to find all names of a given value, use getValueNames and
	 * getValueByName.
	 */
	public String lookupValueName(Value header) {
		for (String name : this.valueNameMap.keySet()) {
			if (this.valueNameMap.get(name).equals(header))
				return name;
		}
		return null;
	}
	
	public void setTargetTriple(String triple) {
		this.targetTriple = triple;
	}
	public String getTargetTriple() {return this.targetTriple;}
	
	public void setDataLayout(String layout) {
		this.dataLayout = layout;
	}
	public String getDataLayout() {return this.dataLayout;}
	
	public void setModuleInlineASM(String asm) {
		this.moduleInlineASM = asm;
	}
	public String getModuleInlineASM() {return this.moduleInlineASM;}

	public String removeSectionName(int i) {
		return this.sectionNames.remove(i);
	}
	public void addSectionName(String name) {
		this.sectionNames.add(name);
	}
	public int getNumSections() {return this.sectionNames.size();}
	public String getSectionName(int i) {return this.sectionNames.get(i);}
	
	public String removeCollectorName(int i) {
		return this.collectorNames.remove(i);
	}
	public void addCollectorName(String name) {
		this.collectorNames.add(name);
	}
	public int getNumCollectors() {return this.collectorNames.size();}
	public String getCollectorName(int i) {return this.collectorNames.get(i);}

	public String removeLibraryName(int i) {
		return this.libraries.remove(i);
	}
	public void addLibraryName(String name) {
		this.libraries.add(name);
	}
	public int getNumLibraries() {return this.libraries.size();}
	public String getLibraryName(int i) {return this.libraries.get(i);}
	
	public FunctionValue removeFunctionHeader(int i) {
		return this.functionHeaders.remove(i);
	}
	public void addFunctionHeader(FunctionValue value) {
		this.functionHeaders.add(value);
	}
	public int getNumFunctionHeaders() {return this.functionHeaders.size();}
	public FunctionValue getFunctionHeader(int i) {return this.functionHeaders.get(i);}
	
	public FunctionBody removeFunctionBody(int i) {
		return this.bodies.remove(i);
	}
	public void addFunctionBody(FunctionBody value) {
		this.bodies.add(value);
	}
	public int getNumFunctionBodies() {return this.bodies.size();}
	public FunctionBody getFunctionBody(int i) {return this.bodies.get(i);}
	public FunctionBody getBodyByHeader(FunctionValue header) {
		for (FunctionBody b : this.bodies) {
			if (b.getHeader().equals(header))
				return b;
		}
		return null;
	}
	
	public GlobalVariable removeGlobalVariable(int i) {
		return this.globalVars.remove(i);
	}
	public void addGlobalVariable(GlobalVariable value) {
		this.globalVars.add(value);
	}
	public int getNumGlobalVariables() {return this.globalVars.size();}
	public GlobalVariable getGlobalVariable(int i) {return this.globalVars.get(i);}
	
	public AliasValue removeAlias(int i) {
		return this.aliases.remove(i);
	}
	public void addAlias(AliasValue value) {
		this.aliases.add(value);
	}
	public int getNumAliases() {return this.aliases.size();}
	public AliasValue getAlias(int i) {return this.aliases.get(i);}
	
	public Set<String> getTypeNames() {return Collections.unmodifiableSet(this.typeNameMap.keySet());}
	public Type getTypeByName(String name) {return this.typeNameMap.get(name);}
	public void addTypeName(String name, Type type) {this.typeNameMap.put(name, type);}
	public Type removeTypeName(String name) {return this.typeNameMap.remove(name);}

	public Set<String> getValueNames() {return Collections.unmodifiableSet(this.valueNameMap.keySet());}
	public Value getValueByName(String name) {return this.valueNameMap.get(name);}
	public void addValueName(String name, Value value) {this.valueNameMap.put(name, value);}
	public Value removeValueName(String name) {return this.valueNameMap.remove(name);}
}
