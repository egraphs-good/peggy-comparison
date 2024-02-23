package llvm.values;

import java.util.Set;

/**
 * Interface for any object that assigns names to values.
 * This includes Modules and FunctionBody's
 */
public interface NamedValueMap {
	public Set<String> getValueNames();
	public Value getValueByName(String name);
	public void addValueName(String name, Value value);
	public Value removeValueName(String name);
}
