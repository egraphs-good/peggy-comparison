package peggy.tv.llvm;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import util.Action;
import util.pair.Pair;

/**
 * This is an options subclass that also allows string pair parameters.
 */
public class Options extends peggy.Options {
	protected final Map<String,Info> stringPairKeys = new HashMap<String,Info>();
	
	// override in subclasses
	public boolean hasKey(String key) {
		return stringPairKeys.containsKey(key) || super.hasKey(key);
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
		data.put(key, new Pair<String,String>(init1,init2));
	}
	
	public void setStringPair(String key, String s1, String s2) {
		if (stringPairKeys.containsKey(key)) {
			final Pair<String,String> value = new Pair<String,String>(s1,s2);
			Info info = stringPairKeys.get(key);
			if (info.getAction() != null)
				((Action<Pair<String,String>>)info.getAction()).execute(value);
			data.put(key, value);
		} else
			throw new IllegalArgumentException();
	}

	// override in subclasses
	public void setValue(String key, String... items) {
		if (stringPairKeys.containsKey(key)) {
			if (items==null || items.length!=2)
				throw new IllegalArgumentException();
			setStringPair(key, items[0], items[1]);
		} 
		else super.setValue(key, items);
	}
	
	// override in subclasses
	public int getNumTokens(String key) {
		if (stringPairKeys.containsKey(key))
			return stringPairKeys.get(key).getNumTokens();
		else 
			return super.getNumTokens(key);
	}
	
	// override in subclasses
	public String getDescription(String key) {
		if (stringPairKeys.containsKey(key))
			return stringPairKeys.get(key).getDescription();
		else 
			return super.getDescription(key);
	}
	
	
	public Set<String> getStringPairKeys() {
		return Collections.unmodifiableSet(stringPairKeys.keySet());
	}
	
	public Pair<String,String> getStringPair(String key) {
		if (!stringPairKeys.containsKey(key))
			throw new NoSuchElementException();
		return (Pair<String,String>)this.data.get(key);
	}
}
