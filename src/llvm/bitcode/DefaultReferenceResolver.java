package llvm.bitcode;

import llvm.bitcode.ReferenceResolver;
import llvm.types.FunctionType;
import llvm.types.Type;
import llvm.values.AliasValue;
import llvm.values.FunctionValue;
import llvm.values.GlobalVariable;
import llvm.values.Module;
import llvm.values.Value;

/**
 * This class provides lookup functions for function/globals/aliases 
 * based on their names and types.
 */
public class DefaultReferenceResolver implements ReferenceResolver {
	private final Module module;
	public DefaultReferenceResolver(Module _module) {
		this.module = _module; 
	}
	public String getFunctionName(FunctionValue value) {
		for (String name : this.module.getValueNames()) {
			if (this.module.getValueByName(name).equalsValue(value))
				return name;
		}
		return null;
	}
	public String getAliasName(AliasValue value) {
		for (String name : this.module.getValueNames()) {
			if (this.module.getValueByName(name).equalsValue(value))
				return name;
		}
		return null;
	}
	public String getGlobalName(GlobalVariable value) {
		for (String name : this.module.getValueNames()) {
			if (this.module.getValueByName(name).equalsValue(value))
				return name;
		}
		return null;
	}
	public FunctionValue resolveFunction(String name, FunctionType type) {
		Value v = this.module.getValueByName(name);
		if (v == null || !v.isFunction())
			throw new IllegalArgumentException("Label name does not match any function: " + name);
		FunctionValue fv = v.getFunctionSelf();
		if (!fv.getType().getPointeeType().equalsType(type))
			throw new IllegalArgumentException("Label type does not match function type: " + type + " != " + fv.getType().getPointeeType());
		return fv;
	}
	public GlobalVariable resolveGlobal(String name, Type type) {
		Value value = this.module.getValueByName(name);
		if (value == null || !value.isGlobalVariable())
			throw new IllegalArgumentException("Label name does not match any global: " + name);
		GlobalVariable gv = value.getGlobalVariableSelf();
		if (!gv.getType().getPointeeType().equalsType(type))
			throw new IllegalArgumentException("Label type does not match global type: " + type + " != " + gv.getType().getPointeeType());
		return gv;
	}
	public AliasValue resolveAlias(String name, Type type) {
		Value value = this.module.getValueByName(name);
		if (value == null || !value.isAlias())
			throw new IllegalArgumentException("Label name does not match any alias: " + name);
		AliasValue av = value.getAliasSelf();
		if (!av.getType().getPointeeType().equalsType(type))
			throw new IllegalArgumentException("Label type does not match alias type: " + type + " != " + av.getType().getPointeeType());
		return av;
	}
}
