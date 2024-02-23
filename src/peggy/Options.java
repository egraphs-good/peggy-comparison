package peggy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import util.Action;

/**
 * This is a class to help with the parsing of command-line arguments.
 * Every argument is associated with a description, a pattern to check for validity,
 * and an action to be taken once the parameter is set. All of these three are optional,
 * and may be null.
 * 
 * This base class supports Strings, booleans, and longs. A subclass can
 * add more types by simply adding a ___Keys map, and adding the appropriate 
 * functions for accessing/manipulating it.
 */
public class Options {
	protected static class Info {
		protected final Action<?> action;
		protected final String description;
		protected final int numTokens;
		public Info(
				String d, 
				int tokens, 
				Action<?> _action) {
			this.description = d;
			this.numTokens = tokens;
			this.action = _action;
		}
		public String getDescription() {return description;}
		public Action<?> getAction() {return action;}
		public int getNumTokens() {return numTokens;}
	}
	
	protected final Map<String,Object> data = new HashMap<String,Object>();
	protected final Map<String,Info> booleanKeys = new HashMap<String,Info>();
	protected final Map<String,Info> stringKeys = new HashMap<String,Info>();
	protected final Map<String,Info> longKeys = new HashMap<String,Info>();
	// add map in subclasses

	// override in subclasses
	public boolean hasKey(String key) {
		return booleanKeys.containsKey(key) ||
			stringKeys.containsKey(key) ||
			longKeys.containsKey(key);
	}
	
	protected boolean parseBoolean(String str) {
		str = str.trim().toLowerCase();
		if (str.equals("true"))
			return true;
		else if (str.equals("false"))
			return false;
		else
			throw new OptionParsingException("Cannot parse boolean string: " + str);
	}
	protected long parseLong(String str) {
		try {
			return Long.parseLong(str);
		} catch (Throwable t) {
			throw new OptionParsingException("Cannot parse long value", t); 
		}
	}
	
	public static Action<Long> getLongBoundsVerifier(final Long min, final Long max) {
		return new Action<Long>() {
			public void execute(Long value) {
				if (min!=null && value<min)
					throw new OptionParsingException("Value too low: " + value);
				else if (max!=null && value>max)
					throw new OptionParsingException("Value too high: " + value);
			}
		};
	}

	public void registerBoolean(
			String key, 
			boolean init, 
			String description) {
		registerBoolean(key, init, description, null);
	}
	public void registerBoolean(
			String key, 
			boolean init, 
			String description,
			Action<Boolean> action) {
		if (hasKey(key))
			throw new IllegalArgumentException();
		booleanKeys.put(key, new Info(description, 1, action));
		data.put(key, init);
	}
	

	public void registerString(
			String key, 
			String init,
			String description) {
		registerString(key, init, description, null);
	}
	public void registerString(
			String key, 
			String init,
			String description,
			Action<String> action) {
		if (hasKey(key))
			throw new IllegalArgumentException();
		stringKeys.put(key, new Info(description, 1, action));
		data.put(key, init);
	}

	
	public void registerLong(
			String key, 
			long init,
			String description,
			Long min,
			Long max) {
		registerLong(key, init, description, getLongBoundsVerifier(min, max));
	}
	public void registerLong(
			String key, 
			long init,
			String description,
			Action<Long> action) {
		if (hasKey(key))
			throw new IllegalArgumentException();
		longKeys.put(key, new Info(description, 1, action));
		data.put(key, init);
	}

	
	public void setBoolean(String key, boolean value) {
		if (booleanKeys.containsKey(key)) {
			Info info = booleanKeys.get(key);
			if (info.getAction() != null)
				((Action<Boolean>)info.getAction()).execute(value);
			data.put(key, value);
		} else
			throw new IllegalArgumentException();
	}
	public void setString(String key, String value) {
		if (stringKeys.containsKey(key)) {
			Info info = stringKeys.get(key);
			if (info.getAction() != null)
				((Action<String>)info.getAction()).execute(value);
			data.put(key, value);
		} else
			throw new IllegalArgumentException();
	}
	public void setLong(String key, long value) {
		if (longKeys.containsKey(key)) {
			Info info = longKeys.get(key);
			if (info.getAction() != null)
				((Action<Long>)info.getAction()).execute(value);
			data.put(key, value);
		} else
			throw new IllegalArgumentException();
	}
	
	
	// override in subclasses
	public void setValue(String key, String... items) {
		if (booleanKeys.containsKey(key)) {
			if (items==null || items.length!=1)
				throw new IllegalArgumentException();
			setBoolean(key, parseBoolean(items[0]));
		} 
		else if (stringKeys.containsKey(key)) {
			if (items==null || items.length!=1)
				throw new IllegalArgumentException();
			setString(key, items[0]);
		}
		else if (longKeys.containsKey(key)) {
			if (items==null || items.length!=1)
				throw new IllegalArgumentException();
			setLong(key, parseLong(items[0]));
		}
		else
			throw new IllegalArgumentException();
	}
	
	// override in subclasses
	public int getNumTokens(String key) {
		if (booleanKeys.containsKey(key))
			return booleanKeys.get(key).getNumTokens();
		else if (stringKeys.containsKey(key))
			return stringKeys.get(key).getNumTokens();
		else if (longKeys.containsKey(key))
			return longKeys.get(key).getNumTokens();
		else
			throw new IllegalArgumentException();
	}
	

	// override in subclasses
	public String getDescription(String key) {
		if (booleanKeys.containsKey(key))
			return booleanKeys.get(key).getDescription();
		else if (stringKeys.containsKey(key))
			return stringKeys.get(key).getDescription();
		else if (longKeys.containsKey(key))
			return longKeys.get(key).getDescription();
		else
			throw new IllegalArgumentException(key);
	}
	
	public Set<String> getBooleanKeys() {
		return Collections.unmodifiableSet(booleanKeys.keySet());
	}
	public Set<String> getStringKeys() {
		return Collections.unmodifiableSet(stringKeys.keySet());
	}
	public Set<String> getLongKeys() {
		return Collections.unmodifiableSet(longKeys.keySet());
	}
	
	public boolean getBoolean(String key) {
		if (!booleanKeys.containsKey(key))
			throw new NoSuchElementException();
		return (Boolean)this.data.get(key);
	}
	public String getString(String key) {
		if (!stringKeys.containsKey(key))
			throw new NoSuchElementException();
		return (String)this.data.get(key);
	}
	public long getLong(String key) {
		if (!longKeys.containsKey(key))
			throw new NoSuchElementException();
		return (Long)this.data.get(key);
	}
}
