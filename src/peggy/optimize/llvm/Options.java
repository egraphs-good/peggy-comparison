package peggy.optimize.llvm;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import peggy.OptionParsingException;

import util.Action;

/**
 * This is an Options instance that supports file parameters.
 */
public class Options extends peggy.Options {
	protected final Map<String,Info> fileKeys = new HashMap<String,Info>();
	
	// override in subclasses
	public boolean hasKey(String key) {
		return fileKeys.containsKey(key) || super.hasKey(key);
	}
	
	public void registerFile(
			String key, 
			File init, 
			String description,
			Action<File> action) {
		if (hasKey(key))
			throw new IllegalArgumentException();
		fileKeys.put(key, new Info(description, 1, action));
		data.put(key, init);
	}
	
	public void setFile(String key, File value) {
		if (fileKeys.containsKey(key)) {
			Info info = fileKeys.get(key);
			if (info.getAction() != null)
				((Action<File>)info.getAction()).execute(value);
			data.put(key, value);
		} else
			throw new IllegalArgumentException();
	}

	protected File parseFile(String str) {
		try {
			return new File(str);
		} catch (Throwable t) {
			throw new OptionParsingException("Cannot parse as file", t);
		}		
	}
	
	// override in subclasses
	public void setValue(String key, String... items) {
		if (fileKeys.containsKey(key)) {
			if (items==null || items.length!=1)
				throw new IllegalArgumentException();
			setFile(key, parseFile(items[0]));
		} 
		else super.setValue(key, items);
	}
	
	// override in subclasses
	public int getNumTokens(String key) {
		if (fileKeys.containsKey(key))
			return fileKeys.get(key).getNumTokens();
		else 
			return super.getNumTokens(key);
	}
	
	// override in subclasses
	public String getDescription(String key) {
		if (fileKeys.containsKey(key))
			return fileKeys.get(key).getDescription();
		else 
			return super.getDescription(key);
	}
	
	
	public Set<String> getFileKeys() {
		return Collections.unmodifiableSet(fileKeys.keySet());
	}
	
	public File getFile(String key) {
		if (!fileKeys.containsKey(key))
			throw new NoSuchElementException();
		return (File)this.data.get(key);
	}
}
