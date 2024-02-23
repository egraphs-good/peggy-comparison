package llvm.bitcode;

import java.util.HashMap;
import java.util.Map;

import llvm.types.FunctionType;
import llvm.types.Type;
import llvm.values.AliasValue;
import llvm.values.FunctionValue;
import llvm.values.GlobalVariable;
import llvm.values.Module;
import llvm.values.Value;
import peggy.represent.llvm.AliasLLVMLabel;
import peggy.represent.llvm.FunctionLLVMLabel;
import peggy.represent.llvm.GlobalLLVMLabel;
import peggy.represent.llvm.ModuleProvider;

/**
 * This resolver does NOT fabricate module-relative functions/aliases/globals,
 * so it should only be used in certain situations.
 * @author mstepp
 */
public class MultiModuleReferenceResolver {
	private final Map<FunctionLLVMLabel,FunctionValue> functionCache;
	private final Map<GlobalLLVMLabel,GlobalVariable> globalCache;
	private final Map<AliasLLVMLabel,AliasValue> aliasCache;
	private final ModuleProvider provider;
	
	public MultiModuleReferenceResolver(
			ModuleProvider _provider) {
		this.provider = _provider;
		this.functionCache = new HashMap<FunctionLLVMLabel,FunctionValue>();
		this.globalCache = new HashMap<GlobalLLVMLabel,GlobalVariable>();
		this.aliasCache = new HashMap<AliasLLVMLabel,AliasValue>();
	}
	
	public String getFunctionName(FunctionValue value) {
		for (Module m : provider.getAllModulesLazily()) {
			String name = m.lookupValueName(value);
			if (name != null)
				return name;
		}
		return null;
	}
	public String getAliasName(AliasValue value) {
		for (Module m : provider.getAllModulesLazily()) {
			String name = m.lookupValueName(value);
			if (name != null)
				return name;
		}
		return null;
	}
	public String getGlobalName(GlobalVariable value) {
		for (Module m : provider.getAllModulesLazily()) {
			String name = m.lookupValueName(value);
			if (name != null)
				return name;
		}
		return null;
	}
	public FunctionValue resolveFunction(String name, FunctionType type) {
		FunctionLLVMLabel label = new FunctionLLVMLabel(type, name);
		if (functionCache.containsKey(label))
			return functionCache.get(label);
		for (Module module : provider.getAllModulesLazily()) {
			Value v = module.getValueByName(name);
			if (v != null && 
				v.isFunction() &&
				v.getFunctionSelf().getType().getPointeeType().equalsType(type)) {
				FunctionValue result = v.getFunctionSelf();
				functionCache.put(label, result);
				return result;
			}
		}

		throw new IllegalArgumentException("Label name does not match any function: " + name);
	}
	
	public GlobalVariable resolveGlobal(String name, Type type) {
		GlobalLLVMLabel label = new GlobalLLVMLabel(type, name);
		if (globalCache.containsKey(label))
			return globalCache.get(label);

		for (Module module : provider.getAllModulesLazily()) {
			Value value = module.getValueByName(name);
			if (value != null && 
				value.isGlobalVariable() &&
				value.getGlobalVariableSelf().getType().equalsType(type)) {
				GlobalVariable result = value.getGlobalVariableSelf();
				globalCache.put(label, result);
				return result;
			}
		}
		throw new IllegalArgumentException("Label name does not match any global: " + name);
	}

	public AliasValue resolveAlias(String name, Type type) {
		AliasLLVMLabel label = new AliasLLVMLabel(type, name);
		if (aliasCache.containsKey(label))
			return aliasCache.get(label);
			
		for (Module module : provider.getAllModulesLazily()) {
			Value value = module.getValueByName(name);
			if (value != null && 
				value.isAlias() &&
				value.getAliasSelf().getType().equalsType(type)) {
				AliasValue result = value.getAliasSelf();
				aliasCache.put(label, result);
				return result;
			}
		}
		throw new IllegalArgumentException("Label name does not match any alias: " + name);
	}
}
