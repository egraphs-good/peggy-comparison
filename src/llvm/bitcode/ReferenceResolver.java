package llvm.bitcode;

import llvm.types.FunctionType;
import llvm.types.Type;
import llvm.values.AliasValue;
import llvm.values.FunctionValue;
import llvm.values.GlobalVariable;

/**
 * Instances of this interface are used to lookup functions/aliases/globals
 * based on their names and types.
 */
public interface ReferenceResolver {
	public AliasValue resolveAlias(String name, Type type);
	public GlobalVariable resolveGlobal(String name, Type type);
	public FunctionValue resolveFunction(String name, FunctionType type);
	public String getFunctionName(FunctionValue header);
	public String getGlobalName(GlobalVariable global);
	public String getAliasName(AliasValue alias);
}
