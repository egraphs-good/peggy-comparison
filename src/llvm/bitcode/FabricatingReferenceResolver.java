package llvm.bitcode;

import java.util.HashMap;
import java.util.Map;

import llvm.types.FunctionType;
import llvm.types.Type;
import llvm.values.AliasValue;
import llvm.values.FunctionValue;
import llvm.values.GlobalVariable;
import llvm.values.Linkage;
import llvm.values.Module;
import llvm.values.Value;
import llvm.values.Visibility;
import peggy.represent.llvm.AliasLLVMLabel;
import peggy.represent.llvm.FunctionLLVMLabel;
import peggy.represent.llvm.GlobalLLVMLabel;
import peggy.represent.llvm.ModuleProvider;

/**
 * This is a reference resolver that will load additional modules if necessary,
 * and hence "fabricate" the results. If the given function/global/alias cannot
 * be located in the current module, then the other modules in the module path
 * will be examined one by one until it is found. 
 * This class should be used with care because it can return values from different
 * modules.
 */
public class FabricatingReferenceResolver implements ReferenceResolver {
	private Module mainModule;
	private final Map<FunctionLLVMLabel,FunctionValue> functionCache;
	private final Map<GlobalLLVMLabel,GlobalVariable> globalCache;
	private final Map<AliasLLVMLabel,AliasValue> aliasCache;
	private ModuleProvider provider;
	
	public FabricatingReferenceResolver(
			Module _main,
			ModuleProvider _provider) {
		this.mainModule = _main;
		this.provider = _provider;
		this.functionCache = new HashMap<FunctionLLVMLabel,FunctionValue>();
		this.globalCache = new HashMap<GlobalLLVMLabel,GlobalVariable>();
		this.aliasCache = new HashMap<AliasLLVMLabel,AliasValue>();
	}

	public String getFunctionName(FunctionValue value) {
		for (String name : this.mainModule.getValueNames()) {
			if (this.mainModule.getValueByName(name).equalsValue(value))
				return name;
		}
		return null;
	}
	public String getAliasName(AliasValue value) {
		for (String name : this.mainModule.getValueNames()) {
			if (this.mainModule.getValueByName(name).equalsValue(value))
				return name;
		}
		return null;
	}
	public String getGlobalName(GlobalVariable value) {
		for (String name : this.mainModule.getValueNames()) {
			if (this.mainModule.getValueByName(name).equalsValue(value))
				return name;
		}
		return null;
	}
	public FunctionValue resolveFunction(String name, FunctionType type) {
		{// test the first one 
			Value v = mainModule.getValueByName(name);
			if (v != null && v.isFunction() &&
				v.getFunctionSelf().getType().getPointeeType().equalsType(type)) {
				return v.getFunctionSelf();
			}
		}
		
		FunctionLLVMLabel label = new FunctionLLVMLabel(type, name);
		if (functionCache.containsKey(label)) {
			return functionCache.get(label);
		}
		for (Module module : provider.getAllModulesLazily()) {
			Value v = module.getValueByName(name);
			if (v != null && v.isFunction() &&
				v.getFunctionSelf().getType().getPointeeType().equalsType(type)) {
				FunctionValue result = fabricateFunction(v.getFunctionSelf());
				functionCache.put(label, result);
				mainModule.addFunctionHeader(result);
				mainModule.addValueName(name, result);
				return result;
			}
		}

		throw new IllegalArgumentException("Label name does not match any function: " + name);
	}
	private FunctionValue fabricateFunction(FunctionValue f) {
		return new FunctionValue(
				f.getType(),
				f.getCallingConvention(),
				true,
				Linkage.ExternalLinkage,
				f.getParameterAttributeMap(),
				f.getAlignment(),
				f.getSectionIndex(),
				Visibility.DefaultVisibility,
				f.getCollectorIndex());
	}
	
	public GlobalVariable resolveGlobal(String name, Type type) {
		{// test the first one
			Value value = mainModule.getValueByName(name);
			if (value != null && value.isGlobalVariable() &&
				value.getGlobalVariableSelf().getType().getPointeeType().equalsType(type)) {
				return value.getGlobalVariableSelf();
			}
		}
		
		GlobalLLVMLabel label = new GlobalLLVMLabel(type, name);
		if (globalCache.containsKey(label))
			return globalCache.get(label);

		for (Module module : provider.getAllModulesLazily()) {
			Value value = module.getValueByName(name);
			if (value != null && value.isGlobalVariable() &&
				value.getGlobalVariableSelf().getType().equalsType(type)) {
				GlobalVariable result = fabricateGlobal(value.getGlobalVariableSelf());
				globalCache.put(label, result);
				mainModule.addGlobalVariable(result);
				mainModule.addValueName(name, result);
				return result;
			}
		}
		throw new IllegalArgumentException("Label name does not match any global: " + name);
	}
	private GlobalVariable fabricateGlobal(GlobalVariable gv) {
		return new GlobalVariable(
				gv.getType(),
				gv.isConstant(),
				gv.getInitialValue(),
				Linkage.ExternalLinkage,
				gv.getAlignment(),
				gv.getSectionIndex(),
				Visibility.DefaultVisibility,
				gv.isThreadLocal());
	}
	
	
	public AliasValue resolveAlias(String name, Type type) {
		{// test the first one
			Value value = mainModule.getValueByName(name);
			if (value != null && value.isAlias() &&
				value.getAliasSelf().getType().getPointeeType().equalsType(type)) {
				return value.getAliasSelf();
			}
		}
		
		AliasLLVMLabel label = new AliasLLVMLabel(type, name);
		if (aliasCache.containsKey(label))
			return aliasCache.get(label);
			
		for (Module module : provider.getAllModulesLazily()) {
			Value value = module.getValueByName(name);
			if (value != null && value.isAlias() &&
				value.getAliasSelf().getType().equalsType(type)) {
				AliasValue result = fabricateAlias(value.getAliasSelf());
				aliasCache.put(label, result);
				mainModule.addAlias(result);
				mainModule.addValueName(name, result);
				return result;
			}
		}
		throw new IllegalArgumentException("Label name does not match any alias: " + name);
	}
	private AliasValue fabricateAlias(AliasValue alias) {
		Value aliasee = alias.getAliaseeValue();
		Value newaliasee;
		if (aliasee.isGlobalVariable())
			newaliasee = fabricateGlobal(aliasee.getGlobalVariableSelf());
		else if (aliasee.isAlias())
			newaliasee = fabricateAlias(aliasee.getAliasSelf());
		else if (aliasee.isFunction())
			newaliasee = fabricateFunction(aliasee.getFunctionSelf());
		else
			throw new IllegalArgumentException("Invalid aliasee value: " + aliasee);
		
		return new AliasValue(
				alias.getType(),
				newaliasee,
				Linkage.ExternalLinkage,
				Visibility.DefaultVisibility);
	}
}
