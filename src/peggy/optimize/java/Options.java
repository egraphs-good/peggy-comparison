package peggy.optimize.java;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import peggy.OptionParsingException;
import util.Action;
import util.pair.Pair;

/**
 * This is an Options instance that also allows file parameters and string pair
 * parameters.
 */
public class Options extends peggy.Options {
	protected final Map<String,Info> fileKeys = new HashMap<String,Info>();
	protected final Map<String,Info> stringPairKeys = new HashMap<String,Info>();
	
	// override in subclasses
	public boolean hasKey(String key) {
		return 
			fileKeys.containsKey(key) || 
			stringPairKeys.containsKey(key) ||
			super.hasKey(key);
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
	
	public void registerStringPair(
			String key, 
			String init1,
			String init2,
			String description,
			Action<Pair<String,String>> action) {
		if (hasKey(key))
			throw new IllegalArgumentException();
		stringPairKeys.put(key, new Info(description, 2, action));
		data.put(key, new Pair<String,String>(init1, init2));
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
	
	public void setStringPair(String key, String s1, String s2) {
		if (stringPairKeys.containsKey(key)) {
			Info info = stringPairKeys.get(key);
			Pair<String,String> value = new Pair<String,String>(s1,s2);
			if (info.getAction() != null)
				((Action<Pair<String,String>>)info.getAction()).execute(value);
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
		else if (stringPairKeys.containsKey(key)) {
			if (items==null || items.length!=2)
				throw new IllegalArgumentException();
			setStringPair(key, items[0], items[1]);
		}
		else super.setValue(key, items);
	}
	
	// override in subclasses
	public int getNumTokens(String key) {
		if (fileKeys.containsKey(key))
			return fileKeys.get(key).getNumTokens();
		else if (stringPairKeys.containsKey(key))
			return stringPairKeys.get(key).getNumTokens();
		else 
			return super.getNumTokens(key);
	}
	
	// override in subclasses
	public String getDescription(String key) {
		if (fileKeys.containsKey(key))
			return fileKeys.get(key).getDescription();
		else if (stringPairKeys.containsKey(key))
			return stringPairKeys.get(key).getDescription();
		else 
			return super.getDescription(key);
	}
	
	
	public Set<String> getFileKeys() {
		return Collections.unmodifiableSet(fileKeys.keySet());
	}
	
	public Set<String> getStringPairKeys() {
		return Collections.unmodifiableSet(stringPairKeys.keySet());
	}
	
	public File getFile(String key) {
		if (!fileKeys.containsKey(key))
			throw new NoSuchElementException();
		return (File)this.data.get(key);
	}
	
	public Pair<String,String> getStringPair(String key) {
		if (!stringPairKeys.containsKey(key))
			throw new NoSuchElementException();
		return (Pair<String,String>)this.data.get(key);
	}
	
}

